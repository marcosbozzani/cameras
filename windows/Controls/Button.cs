using System.ComponentModel;

namespace Duck.Cameras.Windows.Controls
{
    [DesignerCategory("Code")]
    public class Button : System.Windows.Forms.Button
    {
        public Button()
        {
            Font = Theme.Fonts.Default(14);
            AutoSize = true;
            Padding = new System.Windows.Forms.Padding(8, 2, 8, 2);
            ForeColor = Theme.Colors.White;
            BackColor = Theme.Colors.Primary500;
            FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            FlatAppearance.BorderSize = 0;
        }
    }
}
