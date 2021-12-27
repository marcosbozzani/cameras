using System;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    public class Window
    {
        public static void Minimize()
        {
            Form.ActiveForm.WindowState = FormWindowState.Minimized;
        }

        public static void Restore()
        {
            if (Form.ActiveForm.WindowState == FormWindowState.Normal)
            {
                Rectangle wa = Screen.FromControl(Form.ActiveForm).WorkingArea;
                Form.ActiveForm.MaximumSize = new Size(wa.Width, wa.Height);
                Form.ActiveForm.WindowState = FormWindowState.Maximized;
            }
            else if (Form.ActiveForm.WindowState == FormWindowState.Maximized)
            {
                Form.ActiveForm.WindowState = FormWindowState.Normal;
            }
        }

        public static void Close()
        {
            Form.ActiveForm.Close();
        }

        public static void Drag()
        {
            ReleaseCapture();
            SendMessage(Form.ActiveForm.Handle, WM_NCLBUTTONDOWN, HT_CAPTION, 0);
        }

        private const int WM_NCLBUTTONDOWN = 0xA1;
        private const int HT_CAPTION = 0x2;

        [DllImport("user32.dll")]
        private static extern int SendMessage(IntPtr hWnd, int Msg, int wParam, int lParam);

        [DllImport("user32.dll")]
        private static extern bool ReleaseCapture();
    }
}
