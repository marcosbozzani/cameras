using Duck.Cameras.Windows.Controls;
using Duck.Cameras.Windows.Model;
using Duck.Cameras.Windows.Service;
using System;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Layout
{
    [DesignerCategory("Designer")]
    public partial class MainPage : Page, IPageLifecycle
    {
        private Timer timer = new Timer();

        public MainPage()
        {
            InitializeComponent();

            actionBar.AddButton(Theme.Icons.Settings()).Click += (o, e) =>
            {
                Navigator.Open(new SettingsPage());
            };

            actionBar.AddButton(Theme.Icons.Reload()).Click += async (o, e) =>
            {
                await FindCameras(true);
            };

            timer.Enabled = false;
            timer.Interval = 5000;
            timer.Tick += Timer_Tick;
        }

        public void Resume()
        {
            timer.Enabled = true;
            if (LocalSettingsManager.IsComplete())
            {
                FindCameras(false).FireAndForget();
            }
            else
            {
                Navigator.Open(new SettingsPage());
            }
        }

        public void Pause()
        {
            timer.Enabled = false;
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
                LoadImage(card, camera.Profiles[0].SnapshotUri);
            }
        }

        private async Task FindCameras(bool forceUpdate)
        {
            spinner.Show();
            grid.Controls.Clear();
            var cameras = await new CameraFinder().FindFromSettingsAsync(forceUpdate);
            foreach (var camera in cameras)
            {
                int size = 22;
                var card = new Card();
                card.Tag = camera;
                card.Title = camera.Name ?? camera.EndPoint;
                card.Margin = new Padding(5);
                card.ImageSize = new Size(size * 16, size * 9);
                LoadImage(card, camera.Profiles[0].SnapshotUri);
                card.Click += Card_Click;
                grid.Controls.Add(card);
            }
            spinner.Hide();
        }

        private void LoadImage(Card card, string url)
        {
            NetworkService.HttpGetStreamAsync(url).ContinueWith(task =>
            {
                if (task.IsCompleted)
                {
                    if (card.Image != null)
                    {
                        card.Image.Dispose();
                    }
                    try
                    {
                        card.Image = Image.FromStream(task.Result);
                    }
                    catch { }
                }
            })
            .FireAndForget();

        }
    }
}
