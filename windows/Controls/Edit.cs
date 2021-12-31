using System.ComponentModel;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    [DesignerCategory("Code")]
    public class Edit : TextBox
    {
        public Edit()
        {
            Font = Theme.Fonts.Default(14);
        }
    }
}
