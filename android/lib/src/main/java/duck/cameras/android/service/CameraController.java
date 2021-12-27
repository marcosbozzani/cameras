package duck.cameras.android.service;

import android.util.Log;

import androidx.annotation.RawRes;

import java.util.Timer;
import java.util.TimerTask;

import duck.cameras.android.R;
import duck.cameras.android.model.Callback;
import duck.cameras.android.model.Camera;
import duck.cameras.android.model.PtzData;
import duck.cameras.android.model.PtzStatus;
import duck.cameras.android.model.Result;
import duck.cameras.android.model.XmlNode;

public class CameraController {

    private Timer moveTimer;
    private final ResourceLoader resourceLoader;

    public CameraController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public void loadAsync(Camera camera, Camera.Profile profile, Callback<PtzData> callback) {
        new Thread(() -> {
            try {
                final PtzData ptzData = new PtzData();
                ptzData.camera = camera;
                ptzData.profile = profile;

                getConfigurations(camera, ptzData);
                for (PtzData.Configuration configuration : ptzData.configurations) {
                    getConfigurationOptions(camera, configuration);
                }

                callback.execute(Result.ok(ptzData));
            } catch (RuntimeException e) {
                Log.e(CameraController.class.getSimpleName(), "loadAsync error", e);
                callback.execute(Result.error(null, e));
            }
        }).start();
    }

    private void getConfigurations(Camera camera, PtzData ptz) {
        String url = "http://" + camera.endPoint + "/onvif/PTZ";
        String response = NetworkService.httpPost(url, resourceLoader.loadString(R.raw.ws_get_configurations));
        XmlNode envelope = XmlParser.parse(response);
        Iterable<XmlNode> configurations = envelope.get("Body").get("GetConfigurationsResponse").getAll("PTZConfiguration");
        for (XmlNode node : configurations) {
            PtzData.Configuration configuration = new PtzData.Configuration();
            configuration.name = node.get("Name").value();
            configuration.token = node.attr("token");
            ptz.configurations.add(configuration);
        }
    }

    private void getConfigurationOptions(Camera camera, PtzData.Configuration configuration) {
        String url = "http://" + camera.endPoint + "/onvif/PTZ";
        String response = NetworkService.httpPost(url, resourceLoader.loadString(R.raw.ws_get_configurations_options, configuration.token));
        XmlNode envelope = XmlParser.parse(response);
        XmlNode options = envelope.get("Body").get("GetConfigurationOptionsResponse").get("PTZConfigurationOptions").get("Spaces");

        XmlNode option = null;
        XmlNode range = null;

        option = options.get("RelativePanTiltTranslationSpace");
        if (option != null) {
            range = option.get("XRange");
            configuration.relativeSpace.xmin = Float.parseFloat(range.get("Min").value());
            configuration.relativeSpace.xmax = Float.parseFloat(range.get("Max").value());
            range = option.get("YRange");
            configuration.relativeSpace.ymin = Float.parseFloat(range.get("Min").value());
            configuration.relativeSpace.ymax = Float.parseFloat(range.get("Max").value());
        }

        option = options.get("RelativeZoomTranslationSpace");
        if (option != null) {
            range = option.get("XRange");
            configuration.relativeSpace.zmin = Float.parseFloat(range.get("Min").value());
            configuration.relativeSpace.zmax = Float.parseFloat(range.get("Max").value());
        }

        option = options.get("ContinuousPanTiltVelocitySpace");
        if (option != null) {
            range = option.get("XRange");
            configuration.continuousSpace.xmin = Float.parseFloat(range.get("Min").value());
            configuration.continuousSpace.xmax = Float.parseFloat(range.get("Max").value());
            range = option.get("YRange");
            configuration.continuousSpace.ymin = Float.parseFloat(range.get("Min").value());
            configuration.continuousSpace.ymax = Float.parseFloat(range.get("Max").value());
        }

        option = options.get("ContinuousZoomVelocitySpace");
        if (option != null) {
            range = option.get("XRange");
            configuration.continuousSpace.zmin = Float.parseFloat(range.get("Min").value());
            configuration.continuousSpace.zmax = Float.parseFloat(range.get("Max").value());
        }

        option = options.get("PanTiltSpeedSpace");
        if (option != null) {
            range = option.get("XRange");
            configuration.speedSpace.xmin = Float.parseFloat(range.get("Min").value());
            configuration.speedSpace.xmax = Float.parseFloat(range.get("Max").value());
            configuration.speedSpace.ymin = Float.parseFloat(range.get("Min").value());
            configuration.speedSpace.ymax = Float.parseFloat(range.get("Max").value());
        }

        option = options.get("ZoomSpeedSpace");
        if (option != null) {
            range = option.get("XRange");
            configuration.speedSpace.zmin = Float.parseFloat(range.get("Min").value());
            configuration.speedSpace.zmax = Float.parseFloat(range.get("Max").value());
        }
    }

    public void getPtzStatusAsync(PtzData ptzData, Callback<PtzStatus> callback) {
        run(ptzData, result -> {
            if (result.ok()) {
                XmlNode statusNode = result.value().get("GetStatusResponse").get("PTZStatus");

                final PtzStatus ptzStatus = new PtzStatus();

                XmlNode positionNode = statusNode.get("Position");
                ptzStatus.position.x = Float.parseFloat(positionNode.get("PanTilt").attr("x"));
                ptzStatus.position.y = Float.parseFloat(positionNode.get("PanTilt").attr("y"));
                ptzStatus.position.z = Float.parseFloat(positionNode.get("Zoom").attr("x"));

                XmlNode moveStatus = statusNode.get("MoveStatus");
                ptzStatus.moveStatus.panTilt = moveStatus.get("PanTilt").value();
                ptzStatus.moveStatus.zoom = moveStatus.get("Zoom").value();

                callback.execute(Result.ok(ptzStatus));
            } else {
                callback.execute(Result.error(null, result.error()));
            }
        }, R.raw.ws_get_status, ptzData.profile.token);
    }

    public void moveLeft(PtzData ptzData) {
        move(ptzData, -1, 0);
    }

    public void moveRight(PtzData ptzData) {
        move(ptzData, +1, 0);
    }

    public void moveUp(PtzData ptzData) {
        move(ptzData, 0, +1);
    }

    public void moveDown(PtzData ptzData) {
        move(ptzData, 0, -1);
    }

    private synchronized void move(PtzData ptzData, double x, double y) {
        if (moveTimer == null) {
            final double delta = 0.2;
            run(ptzData, Callback.empty(), R.raw.ws_continuous_move, ptzData.profile.token, x * delta, y * delta);
            moveTimer = new Timer();
            moveTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stop(ptzData);
                    moveTimer = null;
                }
            }, 500);
        }
    }

    public void stop(PtzData ptzData) {
        run(ptzData, Callback.empty(), R.raw.ws_stop, ptzData.profile.token);
    }

    private void run(PtzData ptzData, Callback<XmlNode> callback, @RawRes int resId, Object... params) {
        new Thread(() -> {
            try {
                String url = "http://" + ptzData.camera.endPoint + "/onvif/PTZ";
                String response = NetworkService.httpPost(url, resourceLoader.loadString(resId, params));
                XmlNode envelopeBody = XmlParser.parse(response).get("Body");
                callback.execute(Result.ok(envelopeBody));
            } catch (RuntimeException e) {
                Log.e(CameraController.class.getSimpleName(), "CameraController error", e);
                callback.execute(Result.error(null, e));
            }
        }).start();
    }
}
