using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    [DesignerCategory("Code")]
    public class ControlGroup : Panel
    {
        public ControlGroup()
        {
            SetStyle(ControlStyles.SupportsTransparentBackColor, true);
            BackColor = Color.Transparent;
        }

        protected override CreateParams CreateParams
        {
            get
            {
                CreateParams createParams = base.CreateParams;
                createParams.ExStyle |= 0x00000020; // WS_EX_TRANSPARENT
                return createParams;
            }
        }

        protected override void OnPaintBackground(PaintEventArgs e)
        {
            // Do not paint background.
        }
    }
}
