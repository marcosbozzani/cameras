using Duck.Cameras.Windows.Controls;
using Duck.Cameras.Windows.Model;
using Duck.Cameras.Windows.Service;
using LibVLCSharp.Shared;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
using static Duck.Cameras.Windows.Controls.Theme;

namespace Duck.Cameras.Windows.Layout
{
    [DesignerCategory("Designer")]
    public partial class StreamPage : Page, IKeyHandler, IPageLifecycle
    {
        private LibVLC libVLC;
        private MediaPlayer mediaPlayer;
        private Camera camera;
        private CameraProfile profile;
        private CameraController cameraController;
        private RemoteSettingsEndPoint endPoint;
        private List<Preset> presets;

        private class Preset
        {
            public string Token;
            public string Name;
        }

        public StreamPage(Camera camera)
        {
            this.camera = camera;
            profile = camera.Profiles[0];
            cameraController = new CameraController();

            InitializeComponent();

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

            var settings = RemoteSettingsLoader.LoadFromCache();
            endPoint = settings.GetEndPoint(camera.EndPoint);
            presets = CreatePresetList(endPoint.Presets);

            Disposed += StreamPage_Disposed;
            Resize += StreamPage_Resize;

            SetupDiectionalButton(btnLeft, Keys.Left);
            SetupDiectionalButton(btnRight, Keys.Right);
            SetupDiectionalButton(btnUp, Keys.Up);
            SetupDiectionalButton(btnDown, Keys.Down);
            videoOverlay.Click += (o, e) => ToggleButtons();

            CreateContextMenu(videoOverlay);
        }

        public void Resume()
        {
            ShowSpinner();
            libVLC = new LibVLC();
            mediaPlayer = new MediaPlayer(libVLC);
            videoView.MediaPlayer = mediaPlayer;
            mediaPlayer.Vout += MediaPlayer_Vout;
            mediaPlayer.Play(new Media(libVLC, new Uri(profile.StreamUri)));
            Mute();
        }

        public void Pause()
        {
            HideSpinner();
            mediaPlayer.Pause();
        }

        public bool OnKeyDown(Keys key, bool repeat)
        {
            if (repeat)
            {
                return false;
            }
            switch (key)
            {
                case Keys.Left:
                    cameraController.MoveLeft(camera.EndPoint, profile.Token, endPoint.Speed);
                    break;
                case Keys.Right:
                    cameraController.MoveRight(camera.EndPoint, profile.Token, endPoint.Speed);
                    break;
                case Keys.Up:
                    cameraController.MoveUp(camera.EndPoint, profile.Token, endPoint.Speed);
                    break;
                case Keys.Down:
                    cameraController.MoveDown(camera.EndPoint, profile.Token, endPoint.Speed);
                    break;
                default:
                    return false;
            }
            return true;
        }

        public bool OnKeyUp(Keys key)
        {
            switch (key)
            {
                case Keys.Left:
                case Keys.Right:
                case Keys.Up:
                case Keys.Down:
                    cameraController.Stop(camera.EndPoint, profile.Token);
                    break;
                default:
                    return false;
            }
            return true;
        }

        private void SetupDiectionalButton(FloatButton button, Keys key)
        {
            button.MouseDown += (o, e) => OnKeyDown(key, false);
            button.MouseUp += (o, e) => OnKeyUp(key);
        }

        private List<Preset> CreatePresetList(IDictionary<string, string> presets)
        {
            var sortedKeys = new List<string>(presets.Keys);
            sortedKeys.Sort((x, y) => x.CompareTo(y));

            var list = new List<Preset>();
            foreach (var key in sortedKeys)
            {
                Preset preset = new Preset();
                preset.Token = key;
                preset.Name = presets[key];
                list.Add(preset);
            }
            return list;
        }

        private void CreateContextMenu(ControlGroup videoOverlay)
        {
            var menu = new ContextMenuStrip();
            menu.Font = Theme.Fonts.Default();
            menu.ShowImageMargin = false;
            menu.Items.Add("Back").Click += (o, e) => Navigator.Back();
            menu.Items.Add(IsMute() ? "Un-Mute" : "Mute").Click += (o, e) =>
            {
                var item = (ToolStripMenuItem)o;
                if (IsMute())
                {
                    UnMute();
                    item.Text = "Mute";
                }
                else
                {
                    Mute();
                    item.Text = "Un-Mute";
                }
            };
            foreach (var preset in presets)
            {
                menu.Items.Add(preset.Name).Click += (o, e) =>
                {
                    cameraController.GotoPreset(camera.EndPoint, profile.Token, preset.Token);
                };
            }
            videoOverlay.ContextMenuStrip = menu;
        }

        private void MediaPlayer_Vout(object sender, MediaPlayerVoutEventArgs e)
        {
            this.RunOnUiThread(HideSpinner);
        }

        private void StreamPage_Disposed(object sender, EventArgs e)
        {
            if (mediaPlayer != null)
            {
                mediaPlayer.Dispose();
            }
            if (libVLC != null)
            {
                libVLC.Dispose();
            }
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

        private void ToggleButtons()
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

        private void Mute()
        {
            if (mediaPlayer != null)
            {
                mediaPlayer.Volume = 0;
            }
        }

        private void UnMute()
        {
            if (mediaPlayer != null)
            {
                mediaPlayer.Volume = 100;
            }
        }

        private bool IsMute()
        {
            if (mediaPlayer == null)
            {
                return true;
            }
            return mediaPlayer.Volume == 0;
        }

        private void HideSpinner()
        {
            spinner.Hide();
            spinner.SendToBack();
        }

        private void ShowSpinner()
        {
            spinner.BringToFront();
            spinner.Show();
        }
    }
}
