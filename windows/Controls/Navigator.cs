using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    public class NavigatorException : Exception { }

    public static class Navigator
    {
        private static Control parent;
        private static List<Page> pages = new List<Page>();
        
        public static void Setup(Control container)
        {
            if (container is null)
            {
                throw new ArgumentNullException(nameof(container));
            }

            parent = container;
        }

        public static void Open(Page page)
        {
            if (parent is null)
            {
                throw new Exception("Must call Navigator Setup first");
            }

            pages.Add(page);
            parent.Controls.Add(page);
            page.BringToFront();
        }

        public static void Back()
        {
            if (parent is null)
            {
                throw new Exception("Must call Navigator Setup first");
            }

            if (pages.Any())
            {
                var page = pages.Last();
                parent.Controls.Remove(page);
                pages.RemoveAt(pages.Count - 1);
                page.Dispose();
            }
        }

        public static void HandleKey(Keys keyData)
        {
            if (pages.Any())
            {
                var page = pages.Last();
                if (page is IKeyHandler keyHandler)
                {
                    keyHandler.HandleKey(keyData);
                }
            }
        }
    }
}
