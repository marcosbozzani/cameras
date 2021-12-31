using Duck.Cameras.Windows.Model;
using Duck.Cameras.Windows.Properties;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;
using System.Xml.Linq;

namespace Duck.Cameras.Windows.Service
{
    public class CameraFinder
    {
        private const int localPort = 55000;
        private const int upnpPort = 3702;
        private const int upnpTimeout = 1000;
        private readonly IPEndPoint upnpIPv4 = new IPEndPoint(IPAddress.Parse("239.255.255.250"), upnpPort);
        private readonly IPEndPoint upnpIPv6 = new IPEndPoint(IPAddress.Parse("[FF05::C]"), upnpPort);

        public Task<IEnumerable<Camera>> FindAsync()
        {
            return Task.Run<IEnumerable<Camera>>(async () =>
            {
                List<Camera> result = new List<Camera>();

                string uuid = Guid.NewGuid().ToString();
                string data = MessageLoader.Load(Resources.ws_discovery_probe, uuid);

                foreach (var localAddress in NetworkService.GetSiteLocalAddresses())
                {
                    if (localAddress.AddressFamily == AddressFamily.InterNetwork)
                    {
                        var localEndPoint = new IPEndPoint(localAddress, localPort);
                        var remoteEndPoint = upnpIPv4;
                        var responses = NetworkService.UdpRequest(localEndPoint, remoteEndPoint, data, upnpTimeout);
                        result.AddRange(await GetCameras(responses));
                    }
                }

                return result;
            });
        }

        private async Task<IEnumerable<Camera>> GetCameras(IEnumerable<string> responses)
        {
            List<Camera> result = new List<Camera>();

            foreach (var response in responses)
            {
                var probeMatch = SoapParser.Parse(response).Get("ProbeMatches").Get("ProbeMatch");
                if (probeMatch.Get("Types").Value == "dn:NetworkVideoTransmitter")
                {
                    string xAddrs = probeMatch.Get("XAddrs").Value;
                    Camera camera = new Camera();
                    camera.EndPoint = new Uri(xAddrs).Authority;
                    await GetServices(camera);
                    await GetInformation(camera);
                    await GetProfiles(camera);
                    result.Add(camera);
                }
            }

            return result;
        }

        private async Task GetServices(Camera camera)
        {
            var url = "http://" + camera.EndPoint + "/onvif/device_service";
            var response = await NetworkService.HttpPostAsync(url, MessageLoader.Load(Resources.ws_get_services));

            var services = SoapParser.Parse(response).Get("GetServicesResponse").GetAll("Service");
            foreach (XElement serviceNode in services)
            {
                CameraService service = new CameraService();
                service.Namespace = serviceNode.Get("Namespace").Value;
                service.Address = serviceNode.Get("XAddr").Value;
                service.Version = serviceNode.Get("Version").Get("Major").Value
                        + "." + serviceNode.Get("Version").Get("Minor").Value;
                camera.Services.Add(service);
            }
        }

        private async Task GetInformation(Camera camera)
        {
            var url = "http://" + camera.EndPoint + "/onvif/device_service";
            var response = await NetworkService.HttpPostAsync(url, MessageLoader.Load(Resources.ws_get_device_information));

            var deviceInfo = SoapParser.Parse(response).Get("GetDeviceInformationResponse");
            camera.Information.Manufacturer = deviceInfo.Get("Manufacturer").Value;
            camera.Information.Model = deviceInfo.Get("Model").Value;
            camera.Information.FirmwareVersion = deviceInfo.Get("FirmwareVersion").Value;
            camera.Information.SerialNumber = deviceInfo.Get("SerialNumber").Value;
            camera.Information.HardwareId = deviceInfo.Get("HardwareId").Value;
        }

        private async Task GetProfiles(Camera camera)
        {
            var url = "http://" + camera.EndPoint + "/onvif/device_service";
            var response = await NetworkService.HttpPostAsync(url, MessageLoader.Load(Resources.ws_get_profiles));

            var profiles = SoapParser.Parse(response).Get("GetProfilesResponse").GetAll("Profiles");
            foreach (var profileNode in profiles)
            {
                CameraProfile profile = new CameraProfile();
                profile.Name = profileNode.Get("Name").Value;
                profile.Token = profileNode.Attribute("token").Value;
                profile.StreamUri = await GetStreamUri(camera, profile.Token);
                profile.SnapshotUri = await GetSnapshotUri(camera, profile.Token);
                camera.Profiles.Add(profile);
            }
        }

        private async Task<string> GetStreamUri(Camera camera, string profileToken)
        {
            var url = "http://" + camera.EndPoint + "/onvif/Media";
            var response = await NetworkService.HttpPostAsync(url, MessageLoader.Load(Resources.ws_get_stream_uri, profileToken));

            return SoapParser.Parse(response).Get("GetStreamUriResponse").Get("MediaUri").Get("Uri").Value;
        }

        private async Task<string> GetSnapshotUri(Camera camera, string profileToken)
        {
            var url = "http://" + camera.EndPoint + "/onvif/Media";
            var response = await NetworkService.HttpPostAsync(url, MessageLoader.Load(Resources.ws_get_snapshot_uri, profileToken));

            return SoapParser.Parse(response).Get("GetSnapshotUriResponse").Get("MediaUri").Get("Uri").Value;
        }

        public async Task<IEnumerable<Camera>> FindFromSettingsAsync(bool update)
        {
            LoginToken loginToken = LocalSettingsManager.LoadLoginToken();
            RemoteSettings settings = await RemoteSettingsLoader.LoadAsync(update);
            await NetworkService.SetMode(settings);

            List<Camera> result = new List<Camera>();

            foreach (var endPoint in settings.EndPoints)
            {
                try
                {
                    Camera camera = new Camera();
                    camera.EndPoint = endPoint.Host + ":" + endPoint.LocalPort.Command;
                    camera.Name = endPoint.Name;
                    CameraProfile profile = new CameraProfile();
                    profile.Token = endPoint.Profile;
                    profile.StreamUri = CreateStreamUri(endPoint, loginToken);
                    profile.SnapshotUri = CreateSnapshotUri(endPoint, loginToken);
                    camera.Profiles.Add(profile);
                    result.Add(camera);
                }
                catch (Exception e)
                {                    
                    Debug.Write(e, nameof(CameraFinder.FindFromSettingsAsync));
                }
            }

            return result;
        }

        private string CreateStreamUri(RemoteSettingsEndPoint endPoint, LoginToken loginToken)
        {
            string path = endPoint.StreamPath.Replace("{token}", loginToken.Value);
            return string.Format("rtsp://{0}:{1}/{2}", endPoint.Host, endPoint.LocalPort.Stream, path);
        }

        private string CreateSnapshotUri(RemoteSettingsEndPoint endPoint, LoginToken loginToken)
        {
            string path = endPoint.SnapshotPath.Replace("{token}", loginToken.Value);
            return string.Format("http://{0}:{1}/{2}", endPoint.Host, endPoint.LocalPort.Snapshot, path);
        }
    }
}
