using System;
using System.Collections.Generic;
using System.Linq;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    public class NavigatorException : Exception { }

    public static class Navigator
    {
        private static Control parent;
        private static List<Page> pages = new List<Page>();

        public static int HistoryCount => pages.Count;

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
                throw new NullReferenceException("Call Navigator.Setup(container)");
            }

            if (page is null)
            {
                throw new NullReferenceException("page");
            }

            Pause(pages.LastOrDefault());
            pages.Add(page);
            parent.Controls.Add(page);
            page.BringToFront();
            Resume(page);
        }

        public static void Back()
        {
            if (parent is null)
            {
                throw new NullReferenceException("Call Navigator.Setup(container)");
            }

            if (pages.Any())
            {
                var page = pages.Last();
                Pause(pages.LastOrDefault());
                parent.Controls.Remove(page);
                pages.RemoveAt(pages.Count - 1);
                Resume(pages.LastOrDefault());
            }
        }

        public static void Clear()
        {
            int count = pages.Count;
            for (int i = 0; i < count; i++)
            {
                Back();
            }
            pages.Clear();
        }

        public static bool OnKeyDown(Keys key, bool repeat)
        {
            if (pages.Any())
            {
                var page = pages.Last();
                if (page is IKeyHandler keyHandler)
                {
                    return keyHandler.OnKeyDown(key, repeat);
                }
            }
            return false;
        }

        public static bool OnKeyUp(Keys key)
        {
            if (pages.Any())
            {
                var page = pages.Last();
                if (page is IKeyHandler keyHandler)
                {
                    return keyHandler.OnKeyUp(key);
                }
            }
            return false;
        }

        private static void Resume(Page page)
        {
            if (page != null && page is IPageLifecycle lifecycle)
            {
                lifecycle.Resume();
            }
        }

        private static void Pause(Page page)
        {
            if (page != null && page is IPageLifecycle lifecycle)
            {
                lifecycle.Pause();
            }
        }
    }
}
