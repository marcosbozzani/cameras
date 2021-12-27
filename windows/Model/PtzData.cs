using System.Collections.Generic;

namespace Duck.Cameras.Windows.Model
{
    public class PtzData
    {
        public Camera Camera;
        public CameraProfile Profile;
        public List<PtzConfiguration> Configurations;

        public PtzData()
        {
            Configurations = new List<PtzConfiguration>();
        }
    }

    public class PtzConfiguration
    {
        public string Name;
        public string Token;
        public PtzRelativeSpace RelativeSpace;
        public PtzContinuousSpace ContinuousSpace;
        public PtzSpeedSpace SpeedSpace;

        public PtzConfiguration()
        {
            RelativeSpace = new PtzRelativeSpace();
            ContinuousSpace = new PtzContinuousSpace();
            SpeedSpace = new PtzSpeedSpace();
        }
    }

    public class PtzRelativeSpace
    {
        public float? XMin;
        public float? XMax;
        public float? YMin;
        public float? YMax;
        public float? ZMin;
        public float? ZMax;
    }

    public class PtzContinuousSpace
    {
        public float? XMin;
        public float? XMax;
        public float? YMin;
        public float? YMax;
        public float? ZMin;
        public float? ZMax;
    }

    public class PtzSpeedSpace
    {
        public float? XMin;
        public float? XMax;
        public float? YMin;
        public float? YMax;
        public float? ZMin;
        public float? ZMax;
    }

}
