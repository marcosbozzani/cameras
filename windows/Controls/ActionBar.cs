using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    [DesignerCategory("Code")]
    public partial class ActionBar : Control
    {
        private PictureBox logo;
        private Label titleLabel;
        private Button backButton;
        private Button closeButton;
        private Button restoreButton;
        private Button minimizeButton;
        private int buttonWidth;
        private Panel rightDock;

        public ActionBar()
        {
            var fontSize = 24;
            var barHeight = GetHeight(fontSize);
            buttonWidth = barHeight + 4;
            
            Dock = DockStyle.Top;
            Size = new Size(800, barHeight);
            BackColor = Theme.Colors.Primary700;
            MouseDown += ActionBar_MouseDown;
            MouseDoubleClick += ActionBar_MouseDoubleClick;

            var titleLabelHolder = new Panel();
            titleLabelHolder.Dock = DockStyle.Left;
            titleLabelHolder.AutoSize = true;
            titleLabelHolder.AutoSizeMode = AutoSizeMode.GrowAndShrink;
            Controls.Add(titleLabelHolder);

            titleLabel = new Label();
            titleLabel.AutoSize = true;
            titleLabel.Text = "Action Bar";
            titleLabel.ForeColor = Theme.Colors.White;
            titleLabel.Font = Theme.Fonts.Default(fontSize);
            titleLabel.Margin = new Padding(0);
            titleLabel.Padding = new Padding(0);
            titleLabel.Left = -5;
            titleLabelHolder.Controls.Add(titleLabel);

            logo = new PictureBox();
            logo.Size = new Size(buttonWidth, buttonWidth);
            logo.Image = Theme.Icons.Logo(new Theme.IconData{ Width = 512, Height = 512 });
            logo.Dock = DockStyle.Left;
            logo.SizeMode = PictureBoxSizeMode.StretchImage;
            logo.Padding = new Padding(4);
            Controls.Add(logo);

            rightDock = new Panel();
            rightDock.Dock = DockStyle.Right;
            rightDock.AutoSize = true;
            rightDock.AutoSizeMode = AutoSizeMode.GrowAndShrink;
            Controls.Add(rightDock);

            backButton = AddButton(Theme.Icons.Back(), this);
            backButton.Visible = false;
            backButton.Click += (o, e) => Navigator.Back();

            closeButton = AddButton(Theme.Icons.Close());
            closeButton.Click += (o, e) => Window.Close();

            restoreButton = AddButton(Theme.Icons.Restore());
            restoreButton.Click += (o, e) => Window.Restore();

            minimizeButton = AddButton(Theme.Icons.Minimize());
            minimizeButton.Click += (o, e) => Window.Minimize();
        }

        private void ActionBar_MouseDoubleClick(object sender, MouseEventArgs e)
        {
            Window.Restore();
        }

        private void ActionBar_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Left && e.Clicks == 1)
            {
                Window.Drag();
            }
        }

        public Button AddButton(Bitmap icon)
        {
            return AddButton(icon, rightDock);
        }

        private Button AddButton(Bitmap icon, Control parent)
        {
            var button = new Button();
            button.Width = buttonWidth;
            button.AutoSize = false;
            button.ForeColor = Theme.Colors.White;
            button.BackColor = Theme.Colors.Primary700;
            button.Dock = DockStyle.Left;
            button.FlatStyle = FlatStyle.Flat;
            button.FlatAppearance.BorderSize = 0;
            button.Image = icon;
            parent.Controls.Add(button);
            return button;
        }

        private int GetHeight(int fontSize)
        {
            return TextRenderer.MeasureText(" ", Theme.Fonts.Default(fontSize)).Height;
        }

        [Browsable(true)]
        [Category("Appearance")]
        public bool ShowBackButton
        {
            get { return backButton.Visible; }
            set
            {
                logo.Visible = !value;
                backButton.Visible = value;
            }
        }

        [Browsable(true)]
        [Category("Appearance")]
        public bool ShowMinimizeButton
        {
            get { return minimizeButton.Visible; }
            set { minimizeButton.Visible = value; }
        }

        [Browsable(true)]
        [Category("Appearance")]
        public bool ShowRestoreButton
        {
            get { return restoreButton.Visible; }
            set { restoreButton.Visible = value; }
        }

        [Browsable(true)]
        [Category("Appearance")]
        public bool ShowCloseButton
        {
            get { return closeButton.Visible; }
            set { closeButton.Visible = value; }
        }

        [Browsable(true)]
        [Category("Appearance")]
        public string Title
        {
            get { return titleLabel.Text; }
            set { titleLabel.Text = value; }
        }

        public new DockStyle Dock
        {
            get => base.Dock;
            set => base.Dock = value;
        }

        public new Size Size
        {
            get => base.Size;
            set => base.Size = value;
        }

        public new Color BackColor
        {
            get => base.BackColor;
            set => base.BackColor = value;
        }
    }
}
