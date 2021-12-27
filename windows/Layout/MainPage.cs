using Duck.Cameras.Windows.Controls;
using Duck.Cameras.Windows.Model;
using Duck.Cameras.Windows.Service;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Layout
{
    [DesignerCategory("Designer")]
    public partial class MainPage : Page
    {
        private Timer timer = new Timer();

        public MainPage()
        {
            InitializeComponent();

            actionBar.AddButton(Theme.Icons.Settings()).Click += (o, e) =>
            {
                MessageBox.Show(BuildConfig.BuildDate, "Version");
            };

            actionBar.AddButton(Theme.Icons.Reload()).Click += async (o, e) =>
            {
                await FindCameras();
            };

            timer.Interval = 500;
            timer.Tick += Timer_Tick;
            timer.Enabled = true;
        }

        private async void MainPage_Load(object sender, EventArgs e)
        {
            await FindCameras();
        }

        private void Card_Click(object sender, EventArgs e)
        {
            var camera = (Camera)((Card)sender).Tag;
            Navigator.Open(new StreamPage(camera));
        }

        private void Timer_Tick(object sender, EventArgs e)
        {
            foreach (var card in grid.Controls.Cast<Card>())
            {
                var camera = (Camera)card.Tag;
                card.ImageLocation = camera.Profiles[0].SnapshotUri;
            }
        }

        private async Task FindCameras()
        {
            spinner.Show();
            grid.Controls.Clear();
            var cameras = await new CameraFinder().FindAsync();
            foreach (var camera in cameras)
            {
                int size = 22;
                var card = new Card();
                card.Tag = camera;
                card.Title = camera.EndPoint;
                card.Margin = new Padding(5);
                card.ImageSize = new Size(size * 16, size * 9);
                card.ImageLocation = camera.Profiles[0].SnapshotUri;
                card.Click += Card_Click;                
                grid.Controls.Add(card);
            }
            spinner.Hide();
        }
    }
}
