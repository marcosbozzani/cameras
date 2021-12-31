using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Duck.Cameras.Windows.Model
{
    public class RemoteSettings
    {
        public string Router = "";
        public IList<string> Resolvers = new List<string>();
        public IList<RemoteSettingsEndPoint> EndPoints = new List<RemoteSettingsEndPoint>();

        public RemoteSettingsEndPoint GetEndPoint(string endPointString)
        {
            foreach (var endPoint in EndPoints)
            {
                string key = endPoint.Host + ":" + endPoint.LocalPort.Command;
                if (key == endPointString)
                {
                    return endPoint;
                }
            }
            return null;
        }
    }

    public class RemoteSettingsEndPoint
    {
        public string Name { get; set; } = "";
        public string Host { get; set; } = "";
        public RemoteSettingsPorts LocalPort { get; set; } = new RemoteSettingsPorts();
        public RemoteSettingsPorts RemotePort { get; set; } = new RemoteSettingsPorts();
        public string Profile { get; set; } = "";
        public double Speed { get; set; } = 1;
        public IDictionary<string, string> Presets { get; set; } = new Dictionary<string, string>();
        public string StreamPath { get; set; } = "";
        public string SnapshotPath { get; set; } = "";
    }

    public class RemoteSettingsPorts
    {
        public int Command { get; set; } = 0;
        public int Stream { get; set; } = 0;
        public int Snapshot { get; set; } = 0;
    }

}
