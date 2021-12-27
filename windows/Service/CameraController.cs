using Duck.Cameras.Windows.Model;
using Duck.Cameras.Windows.Properties;
using System;
using System.Diagnostics;
using System.Globalization;
using System.Runtime.CompilerServices;
using System.Threading;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Duck.Cameras.Windows.Service
{
    public class CameraController
    {
        private Timer moveTimer;

        public async Task<Result<PtzData>> LoadAsync(Camera camera, CameraProfile profile)
        {
            try
            {
                PtzData ptzData = new PtzData();
                ptzData.Camera = camera;
                ptzData.Profile = profile;

                await GetConfigurations(camera, ptzData);
                foreach (PtzConfiguration configuration in ptzData.Configurations)
                {
                    await GetConfigurationOptions(camera, configuration);
                }

                return Result<PtzData>.Ok(ptzData);
            }
            catch (Exception e)
            {
                Debug.Write(e, typeof(CameraController).Name + ".LoadAsync");
                return Result<PtzData>.Error(null, e);
            }
        }

        private async Task GetConfigurations(Camera camera, PtzData ptzData)
        {
            var url = "http://" + camera.EndPoint + "/onvif/PTZ";
            var response = await NetworkService.HttpPostAsync(url, MessageLoader.Load(Resources.ws_get_configurations));

            var configurations = SoapParser.Parse(response).Get("GetConfigurationsResponse").GetAll("PTZConfiguration");
            foreach (var node in configurations)
            {
                PtzConfiguration configuration = new PtzConfiguration();
                configuration.Name = node.Get("Name").Value;
                configuration.Token = node.Attribute("token").Value;
                ptzData.Configurations.Add(configuration);
            }
        }

        private async Task GetConfigurationOptions(Camera camera, PtzConfiguration configuration)
        {
            var url = "http://" + camera.EndPoint + "/onvif/PTZ";
            var response = await NetworkService.HttpPostAsync(url, MessageLoader.Load(Resources.ws_get_configurations_options, configuration.Token));
            var options = SoapParser.Parse(response).Get("GetConfigurationOptionsResponse").Get("PTZConfigurationOptions").Get("Spaces");

            XElement option = null;
            XElement range = null;

            option = options.Get("RelativePanTiltTranslationSpace");
            if (option != null)
            {
                range = option.Get("XRange");
                configuration.RelativeSpace.XMin = float.Parse(range.Get("Min").Value);
                configuration.RelativeSpace.XMax = float.Parse(range.Get("Max").Value);
                range = option.Get("YRange");
                configuration.RelativeSpace.YMin = float.Parse(range.Get("Min").Value);
                configuration.RelativeSpace.YMax = float.Parse(range.Get("Max").Value);
            }

            option = options.Get("RelativeZoomTranslationSpace");
            if (option != null)
            {
                range = option.Get("XRange");
                configuration.RelativeSpace.ZMin = float.Parse(range.Get("Min").Value);
                configuration.RelativeSpace.ZMax = float.Parse(range.Get("Max").Value);
            }

            option = options.Get("ContinuousPanTiltVelocitySpace");
            if (option != null)
            {
                range = option.Get("XRange");
                configuration.ContinuousSpace.XMin = float.Parse(range.Get("Min").Value);
                configuration.ContinuousSpace.XMax = float.Parse(range.Get("Max").Value);
                range = option.Get("YRange");
                configuration.ContinuousSpace.YMin = float.Parse(range.Get("Min").Value);
                configuration.ContinuousSpace.YMax = float.Parse(range.Get("Max").Value);
            }

            option = options.Get("ContinuousZoomVelocitySpace");
            if (option != null)
            {
                range = option.Get("XRange");
                configuration.ContinuousSpace.ZMin = float.Parse(range.Get("Min").Value);
                configuration.ContinuousSpace.ZMax = float.Parse(range.Get("Max").Value);
            }

            option = options.Get("PanTiltSpeedSpace");
            if (option != null)
            {
                range = option.Get("XRange");
                configuration.SpeedSpace.XMin = float.Parse(range.Get("Min").Value);
                configuration.SpeedSpace.XMax = float.Parse(range.Get("Max").Value);
                configuration.SpeedSpace.YMin = float.Parse(range.Get("Min").Value);
                configuration.SpeedSpace.YMax = float.Parse(range.Get("Max").Value);
            }

            option = options.Get("ZoomSpeedSpace");
            if (option != null)
            {
                range = option.Get("XRange");
                configuration.SpeedSpace.ZMin = float.Parse(range.Get("Min").Value);
                configuration.SpeedSpace.ZMax = float.Parse(range.Get("Max").Value);
            }
        }

        public async Task<PtzStatus> GetPtzStatusAsync(PtzData ptzData)
        {
            var url = "http://" + ptzData.Camera.EndPoint + "/onvif/PTZ";
            var response = await NetworkService.HttpPostAsync(url, MessageLoader.Load(Resources.ws_get_status, ptzData.Profile.Token));

            var statusNode = SoapParser.Parse(response).Get("GetStatusResponse").Get("PTZStatus");

            var ptzStatus = new PtzStatus();

            var positionNode = statusNode.Get("Position");
            ptzStatus.Position.X = float.Parse(positionNode.Get("PanTilt").Attribute("x").Value);
            ptzStatus.Position.Y = float.Parse(positionNode.Get("PanTilt").Attribute("y").Value);
            ptzStatus.Position.Z = float.Parse(positionNode.Get("Zoom").Attribute("x").Value);

            var moveStatus = statusNode.Get("MoveStatus");
            ptzStatus.MoveStatus.PanTilt = moveStatus.Get("PanTilt").Value;
            ptzStatus.MoveStatus.Zoom = moveStatus.Get("Zoom").Value;

            return ptzStatus;
        }

        public void MoveLeft(PtzData ptzData)
        {
            Move(ptzData, -1, 0);
        }

        public void MoveRight(PtzData ptzData)
        {
            Move(ptzData, +1, 0);
        }

        public void MoveUp(PtzData ptzData)
        {
            Move(ptzData, 0, +1);
        }

        public void MoveDown(PtzData ptzData)
        {
            Move(ptzData, 0, -1);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void Move(PtzData ptzData, double x, double y)
        {
            if (moveTimer == null)
            {
                double delta = 0.2;
                var url = "http://" + ptzData.Camera.EndPoint + "/onvif/PTZ";
                var data = MessageLoader.Load(Resources.ws_continuous_move, ptzData.Profile.Token, x * delta, y * delta);
                NetworkService.HttpPostAsync(url, data).GetAwaiter();

                moveTimer = new Timer((state) =>
                {
                    Stop(ptzData);
                    moveTimer = null;
                }, null, 500, Timeout.Infinite);
            }
        }

        public void Stop(PtzData ptzData)
        {
            var url = "http://" + ptzData.Camera.EndPoint + "/onvif/PTZ";
            NetworkService.HttpPostAsync(url, MessageLoader.Load(Resources.ws_stop, ptzData.Profile.Token)).GetAwaiter();
        }
    }
}
