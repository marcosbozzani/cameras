using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Duck.Cameras.Windows.Service
{
    public class MessageLoader
    {
        public static string Load(string resource, params object[] args)
        {
            return string.Format(CultureInfo.InvariantCulture, resource, args);
        }
    }
}
