using System;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    public static class ControlUtil
    {
        public static void RunOnUiThread(this Control control, Action action)
        {
            if (control.InvokeRequired)
            {
                control.BeginInvoke(action);
            }
            else
            {
                action();
            }
        }

        public static void DelayedRun(this Control control, Action action, int milisDelay = 0)
        {
            Task.Delay(milisDelay).ContinueWith(_ => control.BeginInvoke(action));
        }
    }
}
