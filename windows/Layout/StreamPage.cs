using Duck.Cameras.Windows.Controls;
using Duck.Cameras.Windows.Model;
using Duck.Cameras.Windows.Service;
using LibVLCSharp.Shared;
using System;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
using static Duck.Cameras.Windows.Controls.Theme;

namespace Duck.Cameras.Windows.Layout
{
    [DesignerCategory("Designer")]
    public partial class StreamPage : Page, IKeyHandler
    {
        private LibVLC libVLC;
        private MediaPlayer mediaPlayer;
        private Camera camera;
        private CameraController cameraController;
        private PtzData ptzData;

        public StreamPage(Camera camera)
        {
            this.camera = camera;
            cameraController = new CameraController();

            InitializeComponent();

            actionBar.AddButton(Theme.Icons.Settings()).Click += (o, e) =>
            {
                MessageBox.Show(BuildConfig.BuildDate, "Version");
            };

            var leftIcon = Theme.Icons.Back(new IconData
            {
                Width = btnLeft.Width,
                Height = btnLeft.Height
            });
            btnLeft.Image = leftIcon;

            var rightIcon = Theme.Icons.Back(new IconData
            {
                Width = btnLeft.Width,
                Height = btnLeft.Height
            });
            rightIcon.RotateFlip(RotateFlipType.Rotate180FlipNone);
            btnRight.Image = rightIcon;

            var upIcon = Theme.Icons.Back(new IconData
            {
                Width = btnLeft.Width,
                Height = btnLeft.Height
            });
            upIcon.RotateFlip(RotateFlipType.Rotate90FlipNone);
            btnUp.Image = upIcon;

            var downIcon = Theme.Icons.Back(new IconData
            {
                Width = btnLeft.Width,
                Height = btnLeft.Height
            });
            downIcon.RotateFlip(RotateFlipType.Rotate270FlipNone);
            btnDown.Image = downIcon;

            Load += StreamPage_Load;
            Disposed += StreamPage_Disposed;
            Resize += StreamPage_Resize;

            btnLeft.Click += BtnLeft_Click;
            btnRight.Click += BtnRight_Click;
            btnUp.Click += BtnUp_Click;
            btnDown.Click += BtnDown_Click;
            videoOverlay.Click += VideoOverlay_Click;
        }

        private async void StreamPage_Load(object sender, EventArgs e)
        {
            libVLC = new LibVLC();
            mediaPlayer = new MediaPlayer(libVLC);
            videoView.MediaPlayer = mediaPlayer;
            mediaPlayer.Vout += MediaPlayer_Vout;
            mediaPlayer.Play(new Media(libVLC, new Uri(camera.Profiles[0].StreamUri)));

            var result = await cameraController.LoadAsync(camera, camera.Profiles[0]);
            if (result.Success)
            {
                ptzData = result.Value;
            }
        }

        private void MediaPlayer_Vout(object sender, MediaPlayerVoutEventArgs e)
        {
            this.RunOnUiThread(() =>
            {
                spinner.Hide();
                spinner.SendToBack();
            });
        }

        private void StreamPage_Disposed(object sender, EventArgs e)
        {
            mediaPlayer.Dispose();
            libVLC.Dispose();
        }

        private void StreamPage_Resize(object sender, EventArgs e)
        {
            int margin = 10;

            btnLeft.Left = margin;
            btnLeft.Top = videoOverlay.Top + (videoOverlay.Height / 2) - (btnLeft.Height / 2);

            btnRight.Left = videoOverlay.Width - btnRight.Width - margin;
            btnRight.Top = videoOverlay.Top + (videoOverlay.Height / 2) - (btnRight.Height / 2);

            btnUp.Left = (videoOverlay.Width / 2) - (btnUp.Width / 2);
            btnUp.Top = margin + videoOverlay.Top;

            btnDown.Left = (videoOverlay.Width / 2) - (btnDown.Width / 2);
            btnDown.Top = videoOverlay.Top + videoOverlay.Height - btnDown.Height - margin;
        }

        private void BtnLeft_Click(object sender, EventArgs e)
        {
            if (ptzData != null)
            {
                cameraController.MoveLeft(ptzData);
            }

        }

        private void BtnRight_Click(object sender, EventArgs e)
        {
            if (ptzData != null)
            {
                cameraController.MoveRight(ptzData);
            }
        }

        private void BtnUp_Click(object sender, EventArgs e)
        {
            if (ptzData != null)
            {
                cameraController.MoveUp(ptzData);
            }
        }

        private void BtnDown_Click(object sender, EventArgs e)
        {
            if (ptzData != null)
            {
                cameraController.MoveDown(ptzData);
            }
        }

        private void VideoOverlay_Click(object sender, EventArgs e)
        {
            ToogleButtons();
        }

        private void ToogleButtons()
        {
            if (btnLeft.Visible)
            {
                btnLeft.Visible = false;
                btnRight.Visible = false;
                btnUp.Visible = false;
                btnDown.Visible = false;
            }
            else
            {
                btnLeft.Visible = true;
                btnRight.Visible = true;
                btnUp.Visible = true;
                btnDown.Visible = true;
            }
        }

        public void HandleKey(Keys key)
        {
            if (ptzData != null)
            {
                switch (key)
                {
                    case Keys.Left:
                        cameraController.MoveLeft(ptzData);
                        break;
                    case Keys.Right:
                        cameraController.MoveRight(ptzData);
                        break;
                    case Keys.Up:
                        cameraController.MoveUp(ptzData);
                        break;
                    case Keys.Down:
                        cameraController.MoveDown(ptzData);
                        break;
                }
            }
        }
    }
}
