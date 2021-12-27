package duck.cameras.android.model;

import java.io.Serializable;

public class PtzStatus implements Serializable {
    public Position position;
    public MoveStatus moveStatus;

    public PtzStatus() {
        position = new Position();
        moveStatus = new MoveStatus();
    }

    public static class Position implements Serializable {
        public Float x;
        public Float y;
        public Float z;
    }

    public static class MoveStatus implements Serializable {
        public String panTilt;
        public String zoom;
    }
}
