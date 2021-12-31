
namespace Duck.Cameras.Windows.Layout
{
    partial class MainPage
    {
        /// <summary> 
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.grid = new System.Windows.Forms.FlowLayoutPanel();
            this.actionBar = new Duck.Cameras.Windows.Controls.ActionBar();
            this.spinner = new Duck.Cameras.Windows.Controls.Spinner();
            this.SuspendLayout();
            // 
            // grid
            // 
            this.grid.Dock = System.Windows.Forms.DockStyle.Fill;
            this.grid.Location = new System.Drawing.Point(0, 45);
            this.grid.Name = "grid";
            this.grid.Padding = new System.Windows.Forms.Padding(5);
            this.grid.Size = new System.Drawing.Size(800, 555);
            this.grid.TabIndex = 3;
            // 
            // actionBar
            // 
            this.actionBar.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(188)))), ((int)(((byte)(0)))), ((int)(((byte)(50)))));
            this.actionBar.Dock = System.Windows.Forms.DockStyle.Top;
            this.actionBar.Location = new System.Drawing.Point(0, 0);
            this.actionBar.Name = "actionBar";
            this.actionBar.ShowBackButton = false;
            this.actionBar.ShowCloseButton = true;
            this.actionBar.ShowMinimizeButton = true;
            this.actionBar.ShowRestoreButton = true;
            this.actionBar.Size = new System.Drawing.Size(800, 45);
            this.actionBar.TabIndex = 2;
            this.actionBar.Title = "Cameras";
            // 
            // spinner
            // 
            this.spinner.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(80)))), ((int)(((byte)(128)))), ((int)(((byte)(128)))), ((int)(((byte)(128)))));
            this.spinner.Dock = System.Windows.Forms.DockStyle.Fill;
            this.spinner.Location = new System.Drawing.Point(0, 45);
            this.spinner.Name = "spinner";
            this.spinner.NodeBorderColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(255)))));
            this.spinner.NodeBorderSize = 2;
            this.spinner.NodeCount = 8;
            this.spinner.NodeFillColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(255)))));
            this.spinner.NodeRadius = 4;
            this.spinner.NodeResizeRatio = 1F;
            this.spinner.Size = new System.Drawing.Size(800, 555);
            this.spinner.SpinnerRadius = 100;
            this.spinner.TabIndex = 4;
            this.spinner.Text = "spinner";
            // 
            // MainPage
            // 
            this.Controls.Add(this.spinner);
            this.Controls.Add(this.grid);
            this.Controls.Add(this.actionBar);
            this.Name = "MainPage";
            this.ResumeLayout(false);

        }

        #endregion

        private Controls.ActionBar actionBar;
        private System.Windows.Forms.FlowLayoutPanel grid;
        private Controls.Spinner spinner;
    }
}
