package duck.cameras.android.service;

import android.content.res.Resources;
import android.util.Log;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import duck.cameras.android.R;
import duck.cameras.android.model.Callback;
import duck.cameras.android.model.Camera;
import duck.cameras.android.model.Result;
import duck.cameras.android.model.XmlNode;
import duck.cameras.android.util.DeviceUtils;

public class CameraFinder {
    private static final int LOCAL_PORT = 55000;
    private static final int UPNP_PORT = 3702;
    private static final int UPNP_TIMEOUT = 1000;
    private static final InetSocketAddress UPNP_IPv4 = new InetSocketAddress("239.255.255.250", UPNP_PORT);
    private static final InetSocketAddress UPNP_IPv6 = new InetSocketAddress("[FF05::C]", UPNP_PORT);

    private final ResourceLoader resourceLoader;

    public CameraFinder(Resources resources) {
        this.resourceLoader = new ResourceLoader(resources);
    }

    public void findAsync(Callback<List<Camera>> callback) {
        new Thread(() -> {
            try {
                final ArrayList<Camera> result = new ArrayList<>();

                if (DeviceUtils.isRunningOnEmulator()) {
                    String response = resourceLoader.loadString(R.raw.ws_emulator_response);
                    result.addAll(getCameras(Collections.singletonList(response)));
                } else {
                    final String uuid = UUID.randomUUID().toString();
                    final String data = resourceLoader.loadString(R.raw.ws_discovery_probe, uuid);

                    for (InetAddress localAddress : NetworkService.getSiteLocalAddresses()) {
                        if (localAddress instanceof Inet6Address) {
                            continue;
                        }
                        InetSocketAddress localEndPoint = new InetSocketAddress(localAddress, LOCAL_PORT);
                        InetSocketAddress remoteEndPoint = localAddress instanceof Inet4Address ? UPNP_IPv4 : UPNP_IPv6;
                        List<String> responses = NetworkService.udpResquest(localEndPoint, remoteEndPoint, data, UPNP_TIMEOUT);
                        result.addAll(getCameras(responses));
                    }
                }

                callback.execute(Result.ok(result));
            } catch (RuntimeException e) {
                Log.e(CameraFinder.class.getSimpleName(), "findAsync error", e);
                callback.execute(Result.error(Collections.emptyList(), e));
            }
        }).start();
    }

    private List<Camera> getCameras(List<String> responses) {
        final ArrayList<Camera> result = new ArrayList<>();
        for (String response : responses) {
            XmlNode envelope = XmlParser.parse(response);
            XmlNode probeMatch = envelope.get("Body").get("ProbeMatches").get("ProbeMatch");
            if (probeMatch.get("Types").value().equals("dn:NetworkVideoTransmitter")) {
                String xAddrs = probeMatch.get("XAddrs").value();
                Camera camera = new Camera();
                camera.endPoint = NetworkService.getEndpoint(xAddrs);
                getServices(camera);
                getInformation(camera);
                getProfiles(camera);
                result.add(camera);
            }
        }
        return result;
    }

    private void getServices(Camera camera) {
        String url = "http://" + camera.endPoint + "/onvif/device_service";
        String response = NetworkService.httpPost(url, resourceLoader.loadString(R.raw.ws_get_services));
        XmlNode envelope = XmlParser.parse(response);
        Iterable<XmlNode> services = envelope.get("Body").get("GetServicesResponse").getAll("Service");
        for (XmlNode serviceNode : services) {
            Camera.Service service = new Camera.Service();
            service.namespace = serviceNode.get("Namespace").value();
            service.address = serviceNode.get("XAddr").value();
            service.version = serviceNode.get("Version").get("Major").value()
                    + "." + serviceNode.get("Version").get("Minor").value();
            camera.services.add(service);
        }
    }

    private void getInformation(Camera camera) {
        String url = "http://" + camera.endPoint + "/onvif/device_service";
        String response = NetworkService.httpPost(url, resourceLoader.loadString(R.raw.ws_get_device_information));
        XmlNode envelope = XmlParser.parse(response);
        XmlNode deviceInfo = envelope.get("Body").get("GetDeviceInformationResponse");
        camera.information.manufacturer = deviceInfo.get("Manufacturer").value();
        camera.information.model = deviceInfo.get("Model").value();
        camera.information.firmwareVersion = deviceInfo.get("FirmwareVersion").value();
        camera.information.serialNumber = deviceInfo.get("SerialNumber").value();
        camera.information.hardwareId = deviceInfo.get("HardwareId").value();
    }

    private void getProfiles(Camera camera) {
        String url = "http://" + camera.endPoint + "/onvif/device_service";
        String response = NetworkService.httpPost(url, resourceLoader.loadString(R.raw.ws_get_profiles));
        XmlNode envelope = XmlParser.parse(response);
        Iterable<XmlNode> profiles = envelope.get("Body").get("GetProfilesResponse").getAll("Profiles");
        for (XmlNode profileNode : profiles) {
            Camera.Profile profile = new Camera.Profile();
            profile.name = profileNode.get("Name").value();
            profile.token = profileNode.attr("token");
            profile.streamUri = getStreamUri(camera, profile.token);
            profile.snapshotUri = getSnapshotUri(camera, profile.token);
            camera.profiles.add(profile);
        }
    }

    private String getStreamUri(Camera camera, String profileToken) {
        String url = "http://" + camera.endPoint + "/onvif/Media";
        String response = NetworkService.httpPost(url, resourceLoader.loadString(R.raw.ws_get_stream_uri, profileToken));
        XmlNode envelope = XmlParser.parse(response);
        return envelope.get("Body").get("GetStreamUriResponse").get("MediaUri").get("Uri").value();
    }

    private String getSnapshotUri(Camera camera, String profileToken) {
        String url = "http://" + camera.endPoint + "/onvif/Media";
        String response = NetworkService.httpPost(url, resourceLoader.loadString(R.raw.ws_get_snapshot_uri, profileToken));
        XmlNode envelope = XmlParser.parse(response);
        return envelope.get("Body").get("GetSnapshotUriResponse").get("MediaUri").get("Uri").value();
    }
}
