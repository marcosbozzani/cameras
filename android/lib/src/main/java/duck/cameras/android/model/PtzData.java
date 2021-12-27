package duck.cameras.android.model;

import java.io.Serializable;
import java.util.ArrayList;

public class PtzData implements Serializable {
    public Camera camera;
    public Camera.Profile profile;
    public ArrayList<Configuration> configurations;

    public PtzData() {
        configurations = new ArrayList<>();
    }

    public static class Configuration implements Serializable {
        public String name;
        public String token;
        public RelativeSpace relativeSpace;
        public ContinuousSpace continuousSpace;
        public SpeedSpace speedSpace;

        public Configuration() {
            relativeSpace = new RelativeSpace();
            continuousSpace = new ContinuousSpace();
            speedSpace = new SpeedSpace();
        }
    }

    public static class RelativeSpace implements Serializable {
        public Float xmin;
        public Float xmax;
        public Float ymin;
        public Float ymax;
        public Float zmin;
        public Float zmax;
    }

    public static class ContinuousSpace implements Serializable {
        public Float xmin;
        public Float xmax;
        public Float ymin;
        public Float ymax;
        public Float zmin;
        public Float zmax;
    }

    public static class SpeedSpace implements Serializable {
        public Float xmin;
        public Float xmax;
        public Float ymin;
        public Float ymax;
        public Float zmin;
        public Float zmax;
    }

}
