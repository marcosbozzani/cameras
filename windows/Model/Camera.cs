using System.Collections.Generic;

namespace Duck.Cameras.Windows.Model
{
    public class Camera
    {
        public string EndPoint;
        public List<CameraService> Services;
        public CameraInformation Information;
        public List<CameraProfile> Profiles;
        public string Name;

        public Camera()
        {
            Services = new List<CameraService>();
            Information = new CameraInformation();
            Profiles = new List<CameraProfile>();
        }
    }

    public class CameraService
    {
        public string Namespace;
        public string Address;
        public string Version;
    }

    public class CameraInformation
    {
        public string Manufacturer;
        public string Model;
        public string FirmwareVersion;
        public string SerialNumber;
        public string HardwareId;
    }

    public class CameraProfile
    {
        public string Name;
        public string Token;
        public string StreamUri;
        public string SnapshotUri;
    }
}