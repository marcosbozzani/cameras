using Duck.Cameras.Windows.Model;
using Duck.Cameras.Windows.Properties;

namespace Duck.Cameras.Windows.Service
{
    public class LocalSettingsManager
    {
        public static LoginToken LoadLoginToken()
        {
            return new LoginToken(Settings.Default.LoginToken);
        }

        public static void SaveLoginToken(LoginToken loginToken)
        {
            Settings.Default.LoginToken = loginToken.Value;
            Settings.Default.Save();
        }

        public static SettingsUrl LoadSettingsUrl()
        {
            return new SettingsUrl(Settings.Default.SettingsUrl);
        }

        public static void SaveSettingsUrl(SettingsUrl settingsUrl)
        {
            Settings.Default.SettingsUrl = settingsUrl.Value;
            Settings.Default.Save();
        }

        public static bool IsComplete()
        {
            return !LoadLoginToken().IsEmpty() && !LoadSettingsUrl().IsEmpty();
        }
    }
}
