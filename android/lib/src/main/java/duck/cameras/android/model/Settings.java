package duck.cameras.android.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {
    public String router = "";
    public List<String> resolvers = new ArrayList<>();
    public List<EndPoint> endPoints = new ArrayList<>();

    public static class EndPoint {
        public String name = "";
        public String host = "";
        public Ports localPort = new Ports();
        public Ports remotePort = new Ports();
        public String profile = "";
        public double speed = 1;
        public Map<String, String> presets = new HashMap<>();
        public String streamPath = "";
        public String snapshotPath = "";
    }

    public static class Ports {
        public int command = 0;
        public int stream = 0;
        public int snapshot = 0;
    }

    public EndPoint getEndPoint(String endPointString) {
        for (Settings.EndPoint endPoint : endPoints) {
            String key = endPoint.host + ":" + endPoint.localPort.command;
            if (key.equals(endPointString)) {
                return endPoint;
            }
        }
        return null;
    }
}
