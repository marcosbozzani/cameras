package duck.cameras.android.service;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ProtocolException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import duck.cameras.android.model.RetryException;
import duck.cameras.android.model.Settings;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkService {
    private static final OkHttpClient CLIENT = createHttpClient();

    @NonNull
    private static OkHttpClient createHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    private static final MediaType SOAP = MediaType.get("application/soap+xml; charset=utf-8");

    public enum Mode {LOCAL, REMOTE}

    private static Mode mode = Mode.LOCAL;
    private static final Map<String, Authority> forwardingMap = new HashMap<>();

    private static class Authority {
        public String host;
        public int port;

        public Authority(String host, int port) {
            this.host = host;
            this.port = port;
        }
    }

    public static String httpPost(String url, String data) {
        url = processUrl(url);
        ArrayList<Exception> exceptions = new ArrayList<>();
        for (int count = 0; count < 1; count++) {
            RequestBody body = RequestBody.create(data, SOAP);
            Request request = new Request.Builder()
                    .url(url)
                    .header("Connection", "close")
                    .post(body)
                    .build();

            try (Response response = CLIENT.newCall(request).execute()) {
                return response.body().string();
            } catch (ProtocolException e) {
                collectException(exceptions, count, e, "unexpected end of stream");
            } catch (SocketTimeoutException e) {
                collectException(exceptions, count, e, "timeout");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RetryException(exceptions);
    }

    public static String httpGet(String url) {
        url = processUrl(url);
        Request request = new Request.Builder()
                .url(url)
                .header("Connection", "close")
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String processUrl(String urlString) {
        if (mode == Mode.LOCAL) {
            return urlString;
        }
        String protocol = getProtocol(urlString);
        String tempUrlString = urlString;
        if (!protocol.startsWith("http")) {
            tempUrlString = tempUrlString.replace(protocol, "http://");
        }
        HttpUrl url = HttpUrl.parse(tempUrlString);
        String key = url.host() + ":" + url.port();
        if (forwardingMap.containsKey(key)) {
            Authority value = forwardingMap.get(key);
            HttpUrl newUrl = url.newBuilder()
                    .host(value.host)
                    .port(value.port)
                    .build();
            tempUrlString = newUrl.toString();
            if (!protocol.startsWith("http")) {
                tempUrlString = tempUrlString.replace("http://", protocol);
            }
            return tempUrlString;
        }
        return urlString;
    }

    private static String getProtocol(String url) {
        Pattern pattern = Pattern.compile("^[a-z]+://");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(0);
        }
        throw new RuntimeException("protocol not found in: " + url);
    }

    public static void setMode(Settings settings) {
        forwardingMap.clear();
        for (Settings.EndPoint endPoint : settings.endPoints) {
            forwardingMap.put(endPoint.host + ":" + endPoint.localPort.command,
                    new Authority(settings.router, endPoint.remotePort.command));
            forwardingMap.put(endPoint.host + ":" + endPoint.localPort.stream,
                    new Authority(settings.router, endPoint.remotePort.stream));
            forwardingMap.put(endPoint.host + ":" + endPoint.localPort.snapshot,
                    new Authority(settings.router, endPoint.remotePort.snapshot));
        }
        String devicePublicIP = getDevicePublicIP2(settings);
        if (devicePublicIP.isEmpty()) {
            mode = Mode.LOCAL;
            return;
        }
        String routerPublicIP = getRouterPublicIP(settings);
        if (routerPublicIP.isEmpty()) {
            mode = Mode.LOCAL;
            return;
        }
        if (devicePublicIP.equals(routerPublicIP)) {
            mode = Mode.LOCAL;
            return;
        }
        mode = Mode.REMOTE;
    }

    public static Mode getMode() {
        return mode;
    }

    public static String getDevicePublicIP(Settings settings) {
        Map<String, Integer> rank = new HashMap<>();
        for (String resolver : settings.resolvers) {
            try {
                String ip = NetworkService.httpGet(resolver).trim();
                Integer val = rank.get(ip);
                rank.put(ip, val == null ? 1 : val + 1);
            } catch (Exception ignored) {
            }
        }
        Map.Entry<String, Integer> mode = null;
        for (Map.Entry<String, Integer> entry : rank.entrySet()) {
            if (mode == null || entry.getValue() > mode.getValue())
                mode = entry;
        }
        return mode != null && mode.getValue() >= 2 ? mode.getKey() : "";
    }

    public static String getDevicePublicIP2(Settings settings) {
        for (String resolver : settings.resolvers) {
            try {
                return NetworkService.httpGet(resolver).trim();
            } catch (Exception ignored) {
            }
        }
        return "";
    }

    public static String getRouterPublicIP(Settings settings) {
        try {
            return InetAddress.getByName(settings.router).getHostAddress();
        } catch (UnknownHostException ignored) {
            return "";
        }
    }

    private static void collectException(ArrayList<Exception> exceptions, int count, Exception exception, String message) {
        if (exception.getMessage().contains(message)) {
            exceptions.add(exception);
            try {
                Thread.sleep(1000 * count);
            } catch (InterruptedException ignored) {
            }
            return;
        }
        throw new RuntimeException(exception);
    }

    public static List<String> udpResquest(SocketAddress localEndPoint, SocketAddress remoteEndPoint, String data, int timeout) {
        final ArrayList<String> result = new ArrayList<>();

        try (DatagramSocket client = new DatagramSocket(localEndPoint)) {

            client.setSoTimeout(timeout);
            client.send(new DatagramPacket(data.getBytes(StandardCharsets.UTF_8), data.length(), remoteEndPoint));

            final byte[] buffer = new byte[8192];
            DatagramPacket receiverPacket = new DatagramPacket(buffer, buffer.length);
            int tries = 0;
            while (true) {
                try {
                    client.receive(receiverPacket);
                    result.add(new String(receiverPacket.getData(), StandardCharsets.UTF_8));
                } catch (SocketTimeoutException e) {
                    if (tries++ == 3 || result.size() > 0) {
                        break;
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static List<InetAddress> getSiteLocalAddresses() {
        final ArrayList<InetAddress> result = new ArrayList<>();

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface _interface : Collections.list(interfaces)) {
                Enumeration<InetAddress> addresses = _interface.getInetAddresses();
                if (!_interface.isLoopback() && _interface.isUp()) {
                    for (InetAddress address : Collections.list(addresses)) {
                        if (address.isSiteLocalAddress()) {
                            result.add(address);
                        }
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static String getEndpoint(String url) {
        try {
            return new URL(url).getAuthority();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static long ipToLong(String ip) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
            buffer.put(new byte[]{0, 0, 0, 0});
            buffer.put(inetAddress.getAddress());
            buffer.position(0);
            return buffer.getLong();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
