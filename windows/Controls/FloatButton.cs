using System;
using System.ComponentModel;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Linq;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    [DesignerCategory("Code")]
    public class FloatButton : PictureBox
    {
        public FloatButton()
        {
            SetStyle(ControlStyles.UserPaint, true);
            SetStyle(ControlStyles.ResizeRedraw, true);
            SetStyle(ControlStyles.AllPaintingInWmPaint, true);
            SetStyle(ControlStyles.OptimizedDoubleBuffer, true);
            SetStyle(ControlStyles.SupportsTransparentBackColor, true);

            Width = 100;
            Height = 100;
            BackColor = Theme.Colors.Primary500;
        }

        public new int Width { get => base.Width; set => base.Width = value; }

        public new int Height { get => base.Height; set => base.Height = value; }
    }
}
