using System;
using System.IO;
using System.Text;

namespace Duck.Cameras.Windows.Service
{
    public class Log
    {
        public static void Write(string label, string message)
        {
            Write(label + ": " + message);
        }

        public static void Write(string message)
        {
            if (File.Exists("log.txt"))
            {
                var builder = new StringBuilder();
                builder.Append(DateTime.UtcNow.ToString("o"))
                    .Append(" ").Append(message).Append(Environment.NewLine);
                File.AppendAllText("log.txt", builder.ToString());
            }
        }
    }
}
