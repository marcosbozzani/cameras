namespace Duck.Cameras.Windows.Model
{
    public class PtzStatus
    {
        public PtzPosition Position;
        public PtzMoveStatus MoveStatus;

        public PtzStatus()
        {
            Position = new PtzPosition();
            MoveStatus = new PtzMoveStatus();
        }
    }

    public class PtzPosition
    {
        public float? X;
        public float? Y;
        public float? Z;
    }

    public class PtzMoveStatus
    {
        public string PanTilt;
        public string Zoom;
    }

}
