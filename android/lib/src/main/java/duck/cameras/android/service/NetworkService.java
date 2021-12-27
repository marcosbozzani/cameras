package duck.cameras.android.service;

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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import duck.cameras.android.model.RetryException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkService {
    private static final OkHttpClient CLIENT = new OkHttpClient();
    private static final MediaType SOAP = MediaType.get("application/soap+xml; charset=utf-8");

    public static String httpPost(String url, String data) {
        ArrayList<Exception> exceptions = new ArrayList<>();
        for (int count = 0; count < 5; count++) {
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
}
