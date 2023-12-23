package duck.cameras.android.service;

import android.content.res.AssetManager;
import android.util.Log;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.BkBasic;
import org.takes.http.BkSafe;
import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.misc.Href;
import org.takes.rq.RqHref;
import org.takes.rs.RsHtml;
import org.takes.rs.RsRedirect;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Arrays;

import javax.net.ServerSocketFactory;

import duck.cameras.android.model.Callback;
import duck.cameras.android.model.LoginToken;
import duck.cameras.android.model.Result;
import duck.cameras.android.model.SettingsUrl;
import duck.cameras.android.util.ThreadUtils;

public class SettingsHttpServer {
    private Control control;

    public void start(Data data, Configuration configuration) {
        if (control != null) {
            return;
        }
        control = new Control();
        if (data.loginToken == null) data.loginToken = "";
        if (data.settingsUrl == null) data.settingsUrl = "";
        new Thread(() -> {
            try {
                ServerSocket socket = null;
                final int[] ports = {8000, 9000, 10000, 11000, 12000};
                for (int port : ports) {
                    try {
                        socket = ServerSocketFactory.getDefault().createServerSocket(port);
                        break;
                    } catch (BindException e) {
                        Log.e(SettingsHttpServer.class.getSimpleName(), e.getMessage() + " port: " + port);
                    }
                }
                if (socket != null) {
                    int port = socket.getLocalPort();
                    ThreadUtils.runOnUiThread(() -> {
                        configuration.ready.execute(Result.ok(port));
                    });
                    Take take = new TkFork(
                        new FkRegex("/", new IndexTake(data, configuration)),
                        new FkRegex("/send", new SendTake(data, configuration))
                    );
                    new FtBasic(new BkSafe(new BkBasic(take)), socket).start(control);
                } else {
                    throw new BindException("bind failed on ports: " + Arrays.toString(ports));
                }
            } catch (IOException e) {
                Log.e(SettingsHttpServer.class.getSimpleName(), e.getMessage(), e);
                ThreadUtils.runOnUiThread(() -> {
                    configuration.ready.execute(Result.error(null, e));
                });
            }
        }).start();
    }

    public void stop() {
        if (control != null) {
            control.stop();
            control = null;
        }
    }

    public static class IndexTake implements Take {

        private final Data data;
        private final Configuration configuration;

        public IndexTake(Data data, Configuration configuration) {
            this.data = data;
            this.configuration = configuration;
        }

        @Override
        public Response act(Request req) throws Exception {
            Href href = new RqHref.Base(req).href();
            if (getParamValue(href, "sync").equals("true")) {
                data.loginToken = getParamValue(href, "token");
                data.settingsUrl = getParamValue(href, "settings");
                configuration.sync.execute(Result.ok(data));
            }

            String html = readAsset("index.html")
                    .replaceAll("\\$Token", data.loginToken)
                    .replaceAll("\\$Settings", data.settingsUrl);

            return new RsHtml(html);
        }

        private String readAsset(String fileName) {
            InputStream inputStream = null;
            BufferedInputStream bufferedInputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = null;
            try {
                inputStream = configuration.assetManager.open(fileName);
                bufferedInputStream = new BufferedInputStream(inputStream);
                byteArrayOutputStream = new ByteArrayOutputStream();
                for (int result = bufferedInputStream.read();
                     result != -1;
                     result = bufferedInputStream.read()) {
                    byteArrayOutputStream.write((byte) result);
                }
                return byteArrayOutputStream.toString("UTF-8");
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (bufferedInputStream != null) {
                        bufferedInputStream.close();
                    }
                    if (byteArrayOutputStream != null) {
                        byteArrayOutputStream.close();
                    }
                } catch (IOException e) {
                    Log.e(SettingsHttpServer.class.getSimpleName(), e.getMessage(), e);
                }
            }
        }

        private String getParamValue(Href href, String key) {
            String value = null;
            Iterable<String> values = href.param(key);
            if (values.iterator().hasNext()) {
                value = values.iterator().next();
            }
            return value == null ? "" : value;
        }
    }

    public static class SendTake implements Take {

        private final Data data;
        private final Configuration configuration;

        public SendTake(Data data, Configuration configuration) {
            this.data = data;
            this.configuration = configuration;
        }

        @Override
        public Response act(Request req) throws Exception {
            Href href = new RqHref.Base(req).href();
            Data data = new Data();
            data.loginToken = getFirstParamValue(href, "token");
            data.settingsUrl = getFirstParamValue(href, "settings");
            configuration.sync.execute(Result.ok(data));
            return new RsRedirect("/");
        }

        private String getFirstParamValue(Href href, String key) {
            String value = null;
            Iterable<String> values = href.param(key);
            if (values.iterator().hasNext()) {
                value = values.iterator().next();
            }
            return value == null ? "" : value;
        }
    }

    public static class Data {
        public String loginToken;
        public String settingsUrl;
    }

    public static class Configuration {
        public AssetManager assetManager;
        public Callback<Integer> ready;
        public Callback<Data> sync;
    }

    public static class Control implements Exit {
        private boolean exit = false;

        public void stop() {
            exit = true;
        }

        @Override
        public boolean ready() {
            return exit;
        }
    }
}
