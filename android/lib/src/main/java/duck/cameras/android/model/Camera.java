package duck.cameras.android.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Camera implements Serializable {
    public String endPoint;
    public ArrayList<Service> services;
    public Information information;
    public ArrayList<Profile> profiles;

    public static class Service implements Serializable {
        public String namespace;
        public String address;
        public String version;
    }

    public static class Information implements Serializable {
        public String manufacturer;
        public String model;
        public String firmwareVersion;
        public String serialNumber;
        public String hardwareId;
    }

    public static class Profile implements Serializable {
        public String name;
        public String token;
        public String streamUri;
        public String snapshotUri;
    }

    public Camera() {
        this.services = new ArrayList<>();
        this.information = new Information();
        this.profiles = new ArrayList<>();
    }
}
