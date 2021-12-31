using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    public interface IKeyHandler
    {
        bool OnKeyDown(Keys key, bool repeat);
        bool OnKeyUp(Keys key);
    }
}
