using System.Drawing;

namespace Duck.Cameras.Windows.Controls
{
    public static class DrawingUtil
    {
        public static Image WithAlpha(this Image image, int alpha)
        {
            var newImage = (Image)image.Clone();
            using (Graphics graphics = Graphics.FromImage(newImage))
            {
                Pen pen = new Pen(Color.White.WithAlpha(alpha), newImage.Width);
                graphics.DrawLine(pen, -1, -1, newImage.Width, newImage.Height);
                graphics.Save();
            }
            return newImage;
        }

        public static Color WithAlpha(this Color color, int alpha)
        {
            return Color.FromArgb(alpha, color.R, color.G, color.B);
        }
    }
}
