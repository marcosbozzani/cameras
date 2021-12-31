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
        private bool isMoving = false;
        private static readonly Action emptyAction = () => { };

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
            MoveLeft(ptzData.Camera.EndPoint, ptzData.Profile.Token, 1);
        }

        public void MoveRight(PtzData ptzData)
        {
            MoveRight(ptzData.Camera.EndPoint, ptzData.Profile.Token, 1);
        }

        public void MoveUp(PtzData ptzData)
        {
            MoveUp(ptzData.Camera.EndPoint, ptzData.Profile.Token, 1);
        }

        public void MoveDown(PtzData ptzData)
        {
            MoveDown(ptzData.Camera.EndPoint, ptzData.Profile.Token, 1);
        }

        public void MoveLeft(string endPoint, string profileToken, double speed)
        {
            Move(endPoint, profileToken, speed, -1, 0);
        }

        public void MoveRight(string endPoint, string profileToken, double speed)
        {
            Move(endPoint, profileToken, speed, +1, 0);
        }

        public void MoveUp(string endPoint, string profileToken, double speed)
        {
            Move(endPoint, profileToken, speed, 0, +1);
        }

        public void MoveDown(string endPoint, string profileToken, double speed)
        {
            Move(endPoint, profileToken, speed, 0, -1);
        }

        public void Stop(PtzData ptzData)
        {
            Stop(ptzData.Camera.EndPoint, ptzData.Profile.Token);
        }

        public void Stop(string endPoint, string profileToken)
        {
            Run(endPoint, () => isMoving = false, Resources.ws_stop, profileToken);
        }

        public void GotoPreset(string endPoint, string profileToken, string presetToken)
        {
            Run(endPoint, emptyAction, Resources.ws_goto_preset, profileToken, presetToken);
        }

        [MethodImpl(MethodImplOptions.Synchronized)]
        private void Move(string endPoint, string profileToken, double speed, double x, double y)
        {
            if (!isMoving)
            {
                isMoving = true;
                Run(endPoint, emptyAction, Resources.ws_continuous_move, profileToken, x * speed, y * speed);
            }
        }

        private void Run(string endPoint, Action callback, string resource, params object[] args)
        {
            var url = "http://" + endPoint + "/onvif/ptz_service";
            var data = MessageLoader.Load(resource, args);
            NetworkService.HttpPostAsync(url, data).ContinueWith(task => callback()).FireAndForget();
        }
    }
}
