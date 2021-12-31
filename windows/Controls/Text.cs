using System.ComponentModel;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    [DesignerCategory("Code")]
    public class Text : Label
    {
        public Text()
        {
            ForeColor = Theme.Colors.White;
            BackColor = Theme.Colors.PageBgColor;
            Font = Theme.Fonts.Default(12);
        }
    }
}
