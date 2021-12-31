using Duck.Cameras.Windows.Layout;
using System;
using System.Windows.Forms;

namespace Duck.Cameras.Windows
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            LibVLCSharp.Shared.Core.Initialize();
            Application.Run(new MainForm());
        }
    }

    static partial class BuildConfig
    {
        public static readonly string BuildDate;
        public static readonly string BuildType;
    }
}
