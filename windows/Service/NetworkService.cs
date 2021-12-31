using Duck.Cameras.Windows.Model;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Duck.Cameras.Windows.Service
{
    public class NetworkService
    {
        public enum Mode { Local, Remote }

        private class Authority
        {
            public string Host { get; private set; }
            public int Port { get; private set; }

            public Authority(string host, int port)
            {
                Host = host;
                Port = port;
            }
        }

        private static readonly HttpClient httpClient = new HttpClient();
        private static Mode mode = Mode.Local;
        private static readonly IDictionary<string, Authority> forwardingMap = new Dictionary<string, Authority>();

        public static async Task<string> HttpGetAsync(string url)
        {
            url = ProcessUrl(url);
            var response = await httpClient.GetAsync(url);
            return await response.Content.ReadAsStringAsync();
        }

        public static async Task<Stream> HttpGetStreamAsync(string url)
        {
            url = ProcessUrl(url);
            var response = await httpClient.GetStreamAsync(url);
            return response;
        }

        public static async Task<string> HttpPostAsync(string url, string data)
        {
            url = ProcessUrl(url);
            List<Exception> exceptions = new List<Exception>();
            for (int count = 0; count < 1; count++)
            {
                string soap = "application/soap+xml";
                HttpContent content = new StringContent(data, Encoding.UTF8, soap);
                try
                {
                    var response = await httpClient.PostAsync(url, content);
                    return await response.Content.ReadAsStringAsync();
                }
                catch (EndOfStreamException ex)
                {
                    exceptions.Add(ex);
                }
                catch (TaskCanceledException ex)
                {
                    exceptions.Add(ex);
                }
                await Task.Delay(1000 * count);
            }
            throw new RetryException(exceptions);
        }

        public static string ProcessUrl(string urlString)
        {
            if (mode == Mode.Local)
            {
                return urlString;
            }
            string protocol = GetProtocol(urlString);
            string tempUrlString = urlString;
            if (!protocol.StartsWith("http"))
            {
                tempUrlString = tempUrlString.Replace(protocol, "http://");
            }
            Uri url = new Uri(tempUrlString);
            string key = url.Host + ":" + url.Port;
            if (forwardingMap.ContainsKey(key))
            {
                Authority value = forwardingMap[key];
                Uri newUrl = new UriBuilder(url)
                {
                    Host = value.Host,
                    Port = value.Port
                }
                .Uri;
                tempUrlString = newUrl.ToString();
                if (!protocol.StartsWith("http"))
                {
                    tempUrlString = tempUrlString.Replace("http://", protocol);
                }
                return tempUrlString;
            }
            return urlString;
        }

        private static string GetProtocol(string url)
        {
            Regex regex = new Regex("^[a-z]+://");
            var matches = regex.Matches(url);
            if (matches.Count > 0)
            {
                return matches[0].Groups[0].Value;
            }
            throw new Exception("protocol not found in: " + url);
        }

        public static async Task SetMode(RemoteSettings settings)
        {
            forwardingMap.Clear();
            foreach (var endPoint in settings.EndPoints)
            {
                forwardingMap.Add(endPoint.Host + ":" + endPoint.LocalPort.Command,
                        new Authority(settings.Router, endPoint.RemotePort.Command));
                forwardingMap.Add(endPoint.Host + ":" + endPoint.LocalPort.Stream,
                        new Authority(settings.Router, endPoint.RemotePort.Stream));
                forwardingMap.Add(endPoint.Host + ":" + endPoint.LocalPort.Snapshot,
                        new Authority(settings.Router, endPoint.RemotePort.Snapshot));
            }
            string devicePublicIP = await GetDevicePublicIP(settings);
            if (string.IsNullOrEmpty(devicePublicIP))
            {
                mode = Mode.Local;
                return;
            }
            string routerPublicIP = GetRouterPublicIP(settings);
            if (string.IsNullOrEmpty(routerPublicIP))
            {
                mode = Mode.Local;
                return;
            }
            if (devicePublicIP == routerPublicIP)
            {
                mode = Mode.Local;
                return;
            }
            mode = Mode.Remote;
        }

        public static async Task<string> GetDevicePublicIP(RemoteSettings settings)
        {
            foreach (var resolver in settings.Resolvers)
            {
                try
                {
                    var response = await HttpGetAsync(resolver);
                    return response.Trim();
                }
                catch { }
            }
            return "";
        }

        public static string GetRouterPublicIP(RemoteSettings settings)
        {
            try
            {
                return Dns.GetHostAddresses(settings.Router)
                    .FirstOrDefault(ip => ip.AddressFamily == AddressFamily.InterNetwork)
                    .ToString();
            }
            catch { return ""; }
        }

        public static IEnumerable<string> UdpRequest(IPEndPoint local, IPEndPoint remote, string data, int timeout)
        {
            List<string> result = new List<string>();
            using (UdpClient udpClient = new UdpClient(local))
            {
                udpClient.Client.ReceiveTimeout = timeout;
                var request = Encoding.UTF8.GetBytes(data);
                udpClient.Send(request, request.Length, remote);
                int tries = 0;
                while (true)
                {
                    try
                    {
                        var response = udpClient.Receive(ref remote);
                        result.Add(Encoding.UTF8.GetString(response));
                    }
                    catch (SocketException ex)
                    {
                        Debug.Write(ex, nameof(NetworkService));
                        if (tries++ == 3 || result.Count > 0)
                        {
                            break;
                        }
                    }
                }
            }
            return result;
        }

        public static IEnumerable<IPAddress> GetSiteLocalAddresses()
        {
            foreach (var netTnterface in NetworkInterface.GetAllNetworkInterfaces())
            {
                if (netTnterface.OperationalStatus == OperationalStatus.Up &&
                    netTnterface.NetworkInterfaceType != NetworkInterfaceType.Loopback)
                {
                    foreach (var addressInfo in netTnterface.GetIPProperties().UnicastAddresses)
                    {
                        if (IsSiteLocalAddress(addressInfo.Address))
                        {
                            yield return addressInfo.Address;
                        }
                    }
                }
            }
        }

        public static bool IsSiteLocalAddress(IPAddress address)
        {
            if (address.AddressFamily == AddressFamily.InterNetworkV6)
            {
                return address.IsIPv6SiteLocal;
            }

            var raw = address.GetAddressBytes();

            // refer to RFC 1918
            // 10/8 prefix
            // 172.16/12 prefix
            // 192.168/16 prefix
            return (raw[0] & 0xFF) == 10
                   || ((raw[0] & 0xFF) == 172 && (raw[1] & 0xF0) == 16)
                   || ((raw[0] & 0xFF) == 192 && (raw[1] & 0xFF) == 168);
        }
    }
}
