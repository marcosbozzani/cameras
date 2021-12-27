using System;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    [DesignerCategory("Code")]
    public partial class Card : Control
    {
        private Label titleLabel;
        private PictureBox pictureBox;
        private Image pictureBoxOriginalImage;

        public Card()
        {
            Size = new Size(250, 150);
            BackColor = Theme.Colors.CardBgColor;
            Layout += Card_Layout;

            pictureBox = new PictureBox();
            pictureBox.Dock = DockStyle.Fill;
            pictureBox.SizeMode = PictureBoxSizeMode.StretchImage;
            pictureBox.BackColor = Theme.Colors.CardImageBgColor;
            pictureBox.Click += Child_Click;
            pictureBox.MouseEnter += PictureBox_MouseEnter;
            pictureBox.MouseLeave += PictureBox_MouseLeave;
            Controls.Add(pictureBox);

            titleLabel = new Label();
            titleLabel.Dock = DockStyle.Bottom;
            titleLabel.Text = "Card";
            titleLabel.BackColor = Theme.Colors.CardTitleBgColor;
            titleLabel.ForeColor = Theme.Colors.CardTitleTextColor;
            titleLabel.Font = Theme.Fonts.Default(14);
            titleLabel.Padding = new Padding(10);
            titleLabel.Click += Child_Click;
            titleLabel.Layout += TitleLabel_Layout;
            Controls.Add(titleLabel);
        }

        private void PictureBox_MouseLeave(object sender, EventArgs e)
        {
            if (pictureBox.Image != null && pictureBoxOriginalImage != null)
            {
                pictureBox.Image = pictureBoxOriginalImage;
                pictureBoxOriginalImage = null;
            }
        }

        private void PictureBox_MouseEnter(object sender, EventArgs e)
        {
            if (pictureBox.Image != null)
            {
                pictureBoxOriginalImage = pictureBox.Image;
                pictureBox.Image = pictureBox.Image.WithAlpha(30);
            }
        }

        private void Child_Click(object sender, EventArgs e)
        {
            OnClick(e);
        }

        private void Card_Layout(object sender, LayoutEventArgs e)
        {
            if (pictureBox != null && titleLabel != null)
            {
                Width = pictureBox.Width;
                Height = pictureBox.Height + titleLabel.Height;
            }
        }

        private void TitleLabel_Layout(object sender, LayoutEventArgs e)
        {
            var textSize = TextRenderer.MeasureText(titleLabel.Text, titleLabel.Font);
            var verticalPadding = titleLabel.Padding.Top + titleLabel.Padding.Bottom;
            titleLabel.MinimumSize = new Size(0, textSize.Height + verticalPadding);
        }

        [Browsable(true)]
        [Category("Appearance")]
        public string Title
        {
            get { return titleLabel.Text; }
            set { titleLabel.Text = value; }
        }

        [Browsable(true)]
        [Category("Appearance")]
        public Image Image
        {
            get { return pictureBox.Image; }
            set { pictureBox.Image = value; }
        }

        [Browsable(true)]
        [Category("Appearance")]
        public string ImageLocation
        {
            get { return pictureBox.ImageLocation; }
            set { pictureBox.ImageLocation = value; }
        }

        [Browsable(true)]
        [Category("Appearance")]
        public Size ImageSize
        {
            get { return pictureBox.Size; }
            set { pictureBox.Size = value; }
        }

        [Browsable(true)]
        [Category("Appearance")]
        public PictureBoxSizeMode ImageSizeMode
        {
            get { return pictureBox.SizeMode; }
            set { pictureBox.SizeMode = value; }
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
