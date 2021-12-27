using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Controls
{
    public interface IKeyHandler
    {
        void HandleKey(Keys key);
    }
}
