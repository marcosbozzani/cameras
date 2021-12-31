using Duck.Cameras.Windows.Controls;
using Duck.Cameras.Windows.Model;
using Duck.Cameras.Windows.Service;
using System;
using System.ComponentModel;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Layout
{
    [DesignerCategory("Designer")]
    public partial class SettingsPage : Page, IKeyHandler
    {
        public SettingsPage()
        {
            InitializeComponent();
            actionBar.ShowBackButton = LocalSettingsManager.IsComplete();
        }

        private void SettingsPage_Load(object sender, EventArgs e)
        {
            txtVersion.Text = string.Format("{0} ({1})", BuildConfig.BuildDate, BuildConfig.BuildType);
            edtLoginToken.Text = LocalSettingsManager.LoadLoginToken().Value;
            edtSettingsUrl.Text = LocalSettingsManager.LoadSettingsUrl().Value;            
        }

        private void SettingsPage_Resize(object sender, EventArgs e)
        {
            controlGroup.Left = (Width / 2) - (controlGroup.Width / 2);
            controlGroup.Top = (Height / 2) - (controlGroup.Height / 2);
            controlGroup.Invalidate(true);
        }

        public bool OnKeyDown(Keys key, bool repeat)
        {
            if (key == Keys.Enter)
            {
                btnSave.PerformClick();
                return true;
            }
            return false;
        }

        public bool OnKeyUp(Keys key)
        {
            return false;
        }

        private async void btnSave_Click(object sender, EventArgs e)
        {
            try
            {
                LocalSettingsManager.SaveLoginToken(new LoginToken(edtLoginToken.Text));
                LocalSettingsManager.SaveSettingsUrl(new SettingsUrl(edtSettingsUrl.Text));
                await RemoteSettingsLoader.LoadAsync(true);
                Navigator.Back();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message);
            }
        }
    }
}
