using Duck.Cameras.Windows.Properties;
using Svg;
using System.Collections.Generic;
using System.Drawing;
using System.IO;

namespace Duck.Cameras.Windows.Controls
{
    public class Theme
    {
        public class IconData
        {
            public int Width { get; set; } = 24;
            public int Height { get; set; } = 24;
            public Color Color { get; set; } = Colors.White;
            public static IconData Default = new IconData();
        }

        public static class Icons
        {
            public static Bitmap Logo(IconData iconData = default) =>
                LoadIco(Resources.icon_logo, iconData);

            public static Bitmap Back(IconData iconData = default) =>
                LoadSvg(Resources.icon_back, iconData);

            public static Bitmap Close(IconData iconData = default) =>
                LoadSvg(Resources.icon_close, iconData);

            public static Bitmap Maximize(IconData iconData = default) =>
                LoadSvg(Resources.icon_maximize, iconData);

            public static Bitmap Minimize(IconData iconData = default) =>
                LoadSvg(Resources.icon_minimize, iconData);

            public static Bitmap Reload(IconData iconData = default) =>
                LoadSvg(Resources.icon_reload, iconData);

            public static Bitmap Restore(IconData iconData = default) =>
                LoadSvg(Resources.icon_restore, iconData);

            public static Bitmap Settings(IconData iconData = default) =>
                LoadSvg(Resources.icon_settings, iconData);

            private static Bitmap LoadIco(Icon icon, IconData iconData)
            {
                if (iconData == null)
                {
                    iconData = IconData.Default;
                }

                return Bitmap.FromHicon(new Icon(icon, iconData.Width, iconData.Height).Handle);
            }

            private static Bitmap LoadSvg(byte[] bytes, IconData iconData)
            {
                if (iconData == null)
                {
                    iconData = IconData.Default;
                }

                using (MemoryStream memory = new MemoryStream(bytes))
                {
                    var document = SvgDocument.Open<SvgDocument>(memory);
                    ProcessNodes(document.Descendants(), new SvgColourServer(iconData.Color));
                    return document.Draw(iconData.Width, iconData.Height);
                }
            }

            private static void ProcessNodes(IEnumerable<SvgElement> nodes, SvgPaintServer colorServer)
            {
                foreach (var node in nodes)
                {
                    if (IsColorSet(node.Fill)) node.Fill = colorServer;
                    if (IsColorSet(node.Color)) node.Color = colorServer;
                    if (IsColorSet(node.Stroke)) node.Stroke = colorServer;
                    ProcessNodes(node.Descendants(), colorServer);
                }
            }

            private static bool IsColorSet (SvgPaintServer color)
            {
                return color != SvgPaintServer.None && color != SvgPaintServer.NotSet && color != null;
            }
        }

        public class Fonts
        {
            public static Font Default(float size = 14f, FontStyle style = FontStyle.Regular)
            {
                return new Font(Resources.default_font, size, style);
            }
        }

        public class Colors
        {
            public static Color Primary200 => ColorTranslator.FromHtml(Resources.primary_200);
            public static Color Primary500 => ColorTranslator.FromHtml(Resources.primary_500);
            public static Color Primary700 => ColorTranslator.FromHtml(Resources.primary_700);

            public static Color Secondary200 => ColorTranslator.FromHtml(Resources.secondary_200);
            public static Color Secondary700 => ColorTranslator.FromHtml(Resources.secondary_700);

            public static Color Black => ColorTranslator.FromHtml(Resources.black);
            public static Color White => ColorTranslator.FromHtml(Resources.white);

            public static Color PageBgColor => ColorTranslator.FromHtml(Resources.page_bg_color);

            public static Color CardBgColor => ColorTranslator.FromHtml(Resources.card_bg_color);
            public static Color CardImageBgColor => ColorTranslator.FromHtml(Resources.card_image_bg_color);
            public static Color CardTitleBgColor => ColorTranslator.FromHtml(Resources.card_title_bg_color);
            public static Color CardTitleTextColor => ColorTranslator.FromHtml(Resources.card_title_text_color);

            public static Color SpinnerBgColor => ColorTranslator.FromHtml(Resources.spinner_bg_color);
            public static Color SpinnerNodeFillColor => ColorTranslator.FromHtml(Resources.spinner_node_fill_color);
            public static Color SpinnerNodeBorderColor => ColorTranslator.FromHtml(Resources.spinner_node_border_color);
        }
    }
}
