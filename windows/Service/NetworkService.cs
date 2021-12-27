using Duck.Cameras.Windows.Model;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Net.NetworkInformation;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Duck.Cameras.Windows.Service
{
    public class NetworkService
    {
        private static readonly HttpClient httpClient = new HttpClient();

        public static async Task<string> HttpPostAsync(string url, string data)
        {
            List<Exception> exceptions = new List<Exception>();
            for (int count = 0; count < 5; count++)
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
