using Duck.Cameras.Windows.Controls;
using System;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Layout
{
    public partial class MainForm : Form, IMessageFilter
    {
        public MainForm()
        {
            InitializeComponent();
            Navigator.Setup(this);
            BackColor = Theme.Colors.PageBgColor;
        }

        private void MainForm_Load(object sender, EventArgs e)
        {
            Application.AddMessageFilter(this);
            Navigator.Open(new MainPage());
        }

        private void MainForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            Application.RemoveMessageFilter(this);
        }

        public bool PreFilterMessage(ref Message message)
        {
            const int WM_KEYDOWN = 0x100;
            const int WM_KEYUP = 0x101;
            var control = FromHandle(message.HWnd);
            if (control != null && control.FindForm() == this)
            {
                if (message.Msg == WM_KEYDOWN)
                {
                    var key = (Keys)message.WParam.ToInt32();
                    bool repeat = (message.LParam.ToInt32() & (1 << 30)) != 0;
                    return Navigator.OnKeyDown(key, repeat);
                }
                else if (message.Msg == WM_KEYUP)
                {
                    var key = (Keys)message.WParam.ToInt32();
                    return Navigator.OnKeyUp(key);
                }
            }
            return false;
        }
    }
}
