using Duck.Cameras.Windows.Model;
using System.Xml.Linq;

namespace Duck.Cameras.Windows.Service
{
    public class SoapParser
    {

        public static XContainer Parse(string message)
        {
            return XDocument.Parse(message).Get("Envelope").Get("Body");
        }
    }
}
