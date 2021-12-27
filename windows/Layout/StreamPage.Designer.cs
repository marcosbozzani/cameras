
namespace Duck.Cameras.Windows.Layout
{
    partial class StreamPage
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
            this.actionBar = new Duck.Cameras.Windows.Controls.ActionBar();
            this.videoView = new LibVLCSharp.WinForms.VideoView();
            this.spinner = new Duck.Cameras.Windows.Controls.Spinner();
            this.btnLeft = new Duck.Cameras.Windows.Controls.FloatButton();
            this.btnRight = new Duck.Cameras.Windows.Controls.FloatButton();
            this.btnDown = new Duck.Cameras.Windows.Controls.FloatButton();
            this.btnUp = new Duck.Cameras.Windows.Controls.FloatButton();
            this.videoOverlay = new Duck.Cameras.Windows.Controls.ControlGroup();
            ((System.ComponentModel.ISupportInitialize)(this.videoView)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.btnLeft)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.btnRight)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.btnDown)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.btnUp)).BeginInit();
            this.SuspendLayout();
            // 
            // actionBar
            // 
            this.actionBar.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(188)))), ((int)(((byte)(0)))), ((int)(((byte)(50)))));
            this.actionBar.Dock = System.Windows.Forms.DockStyle.Top;
            this.actionBar.Location = new System.Drawing.Point(0, 0);
            this.actionBar.Name = "actionBar";
            this.actionBar.ShowBackButton = true;
            this.actionBar.ShowCloseButton = true;
            this.actionBar.ShowMinimizeButton = true;
            this.actionBar.ShowRestoreButton = true;
            this.actionBar.Size = new System.Drawing.Size(800, 45);
            this.actionBar.TabIndex = 0;
            this.actionBar.Title = "Cameras";
            // 
            // videoView
            // 
            this.videoView.BackColor = System.Drawing.Color.Black;
            this.videoView.Dock = System.Windows.Forms.DockStyle.Fill;
            this.videoView.Location = new System.Drawing.Point(0, 45);
            this.videoView.MediaPlayer = null;
            this.videoView.Name = "videoView";
            this.videoView.Size = new System.Drawing.Size(800, 555);
            this.videoView.TabIndex = 1;
            this.videoView.Text = "videoView1";
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
            this.spinner.TabIndex = 2;
            this.spinner.Text = "spinner";
            // 
            // btnLeft
            // 
            this.btnLeft.Anchor = System.Windows.Forms.AnchorStyles.Left;
            this.btnLeft.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(188)))), ((int)(((byte)(44)))), ((int)(((byte)(0)))));
            this.btnLeft.Location = new System.Drawing.Point(10, 240);
            this.btnLeft.Name = "btnLeft";
            this.btnLeft.Size = new System.Drawing.Size(75, 75);
            this.btnLeft.TabIndex = 3;
            this.btnLeft.TabStop = false;
            this.btnLeft.Visible = false;
            // 
            // btnRight
            // 
            this.btnRight.Anchor = System.Windows.Forms.AnchorStyles.Right;
            this.btnRight.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(188)))), ((int)(((byte)(44)))), ((int)(((byte)(0)))));
            this.btnRight.Location = new System.Drawing.Point(715, 240);
            this.btnRight.Name = "btnRight";
            this.btnRight.Size = new System.Drawing.Size(75, 75);
            this.btnRight.TabIndex = 4;
            this.btnRight.TabStop = false;
            this.btnRight.Visible = false;
            // 
            // btnDown
            // 
            this.btnDown.Anchor = System.Windows.Forms.AnchorStyles.Bottom;
            this.btnDown.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(188)))), ((int)(((byte)(44)))), ((int)(((byte)(0)))));
            this.btnDown.Location = new System.Drawing.Point(363, 470);
            this.btnDown.Name = "btnDown";
            this.btnDown.Size = new System.Drawing.Size(75, 75);
            this.btnDown.TabIndex = 5;
            this.btnDown.TabStop = false;
            this.btnDown.Visible = false;
            // 
            // btnUp
            // 
            this.btnUp.Anchor = System.Windows.Forms.AnchorStyles.Top;
            this.btnUp.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(188)))), ((int)(((byte)(44)))), ((int)(((byte)(0)))));
            this.btnUp.Location = new System.Drawing.Point(363, 10);
            this.btnUp.Name = "btnUp";
            this.btnUp.Size = new System.Drawing.Size(75, 75);
            this.btnUp.TabIndex = 6;
            this.btnUp.TabStop = false;
            this.btnUp.Visible = false;
            // 
            // videoOverlay
            // 
            this.videoOverlay.BackColor = System.Drawing.Color.Transparent;
            this.videoOverlay.Dock = System.Windows.Forms.DockStyle.Fill;
            this.videoOverlay.Location = new System.Drawing.Point(0, 45);
            this.videoOverlay.Name = "videoOverlay";
            this.videoOverlay.Size = new System.Drawing.Size(800, 555);
            this.videoOverlay.TabIndex = 7;
            // 
            // StreamPage
            // 
            this.Controls.Add(this.spinner);
            this.Controls.Add(this.btnDown);
            this.Controls.Add(this.btnUp);
            this.Controls.Add(this.btnLeft);
            this.Controls.Add(this.btnRight);
            this.Controls.Add(this.videoOverlay);
            this.Controls.Add(this.videoView);
            this.Controls.Add(this.actionBar);
            this.Name = "StreamPage";
            ((System.ComponentModel.ISupportInitialize)(this.videoView)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.btnLeft)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.btnRight)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.btnDown)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.btnUp)).EndInit();
            this.ResumeLayout(false);

        }

        #endregion

        private Controls.ActionBar actionBar;
        private LibVLCSharp.WinForms.VideoView videoView;
        private Controls.Spinner spinner;
        private Controls.FloatButton btnLeft;
        private Controls.FloatButton btnRight;
        private Controls.FloatButton btnDown;
        private Controls.FloatButton btnUp;
        private Controls.ControlGroup videoOverlay;
    }
}
