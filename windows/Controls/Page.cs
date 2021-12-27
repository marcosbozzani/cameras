using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    [DesignerCategory("Code")]
    public partial class Page : UserControl
    {
        public Page()
        {
            InitializeComponent();

            bool designMode = (LicenseManager.UsageMode == LicenseUsageMode.Designtime);

            if (designMode)
            {
                Dock = DockStyle.None;
                Size = new Size(800, 600);
            }
            else
            {
                Dock = DockStyle.Fill;
            }

            BackColor = Theme.Colors.PageBgColor;
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

