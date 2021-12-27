using System;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    public static class ControlUtil
    {
        public static void RunOnUiThread(this Control control, Action action)
        {
            if (control.InvokeRequired)
            {
                control.Invoke(action);
            }
            else
            {
                action();
            }
        }
    }
}
