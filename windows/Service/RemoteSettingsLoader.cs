using Duck.Cameras.Windows.Model;
using System;
using System.Threading.Tasks;
using YamlDotNet.Serialization;
using YamlDotNet.Serialization.NamingConventions;

namespace Duck.Cameras.Windows.Service
{
    public class RemoteSettingsLoader
    {
        private static IDeserializer deserializer = new DeserializerBuilder()
            .WithNamingConvention(CamelCaseNamingConvention.Instance)
            .Build();

        public static RemoteSettings LoadFromCache()
        {
            string data = Properties.Settings.Default.SettingsCache;
            if (string.IsNullOrEmpty(data))
            {
                throw new NullReferenceException("SettingsCache is null");
            }
            return deserializer.Deserialize<RemoteSettings>(data);
        }

        public static async Task<RemoteSettings> LoadAsync(bool update)
        {
            string data = Properties.Settings.Default.SettingsCache;
            if (string.IsNullOrEmpty(data) || update)
            {
                SettingsUrl settingsUrl = LocalSettingsManager.LoadSettingsUrl();
                if (settingsUrl.IsEmpty())
                {
                    throw new NullReferenceException("SettingsUrl is null");
                }
                data = await NetworkService.HttpGetAsync(settingsUrl.Value);
                Properties.Settings.Default.SettingsCache = data;
                Properties.Settings.Default.Save();
            }
            return deserializer.Deserialize<RemoteSettings>(data);
        }
    }
}
