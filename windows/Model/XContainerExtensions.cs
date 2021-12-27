using System.Collections.Generic;
using System.Linq;
using System.Xml.Linq;

namespace Duck.Cameras.Windows.Model
{
    public static class XContainerExtensions
    {
        public static XElement Get(this XContainer container, string localName)
        {
            return container.Elements().First(e => e.Name.LocalName == localName);
        }

        public static IEnumerable<XElement> GetAll(this XContainer container, string localName)
        {
            return container.Elements().Where(e => e.Name.LocalName == localName);
        }
    }
}
