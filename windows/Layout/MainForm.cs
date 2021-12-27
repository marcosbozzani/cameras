using Duck.Cameras.Windows.Controls;
using System;
using System.Windows.Forms;

namespace Duck.Cameras.Windows.Layout
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
            Navigator.Setup(this);
            BackColor = Theme.Colors.PageBgColor;
        }

        private void MainForm_Load(object sender, EventArgs e)
        {
            Navigator.Open(new MainPage());
        }

        protected override bool ProcessCmdKey(ref Message msg, Keys keyData)
        {
            Navigator.HandleKey(keyData);
            return base.ProcessCmdKey(ref msg, keyData);
        }
    }
}
