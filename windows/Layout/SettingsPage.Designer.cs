
namespace Duck.Cameras.Windows.Layout
{
    partial class SettingsPage
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
            this.controlGroup = new Duck.Cameras.Windows.Controls.ControlGroup();
            this.txtVersion = new Duck.Cameras.Windows.Controls.Text();
            this.btnSave = new Duck.Cameras.Windows.Controls.Button();
            this.edtSettingsUrl = new Duck.Cameras.Windows.Controls.Edit();
            this.edtLoginToken = new Duck.Cameras.Windows.Controls.Edit();
            this.text2 = new Duck.Cameras.Windows.Controls.Text();
            this.text1 = new Duck.Cameras.Windows.Controls.Text();
            this.actionBar = new Duck.Cameras.Windows.Controls.ActionBar();
            this.controlGroup.SuspendLayout();
            this.SuspendLayout();
            // 
            // controlGroup
            // 
            this.controlGroup.Anchor = System.Windows.Forms.AnchorStyles.None;
            this.controlGroup.BackColor = System.Drawing.Color.Transparent;
            this.controlGroup.Controls.Add(this.txtVersion);
            this.controlGroup.Controls.Add(this.btnSave);
            this.controlGroup.Controls.Add(this.edtSettingsUrl);
            this.controlGroup.Controls.Add(this.edtLoginToken);
            this.controlGroup.Controls.Add(this.text2);
            this.controlGroup.Controls.Add(this.text1);
            this.controlGroup.Location = new System.Drawing.Point(200, 150);
            this.controlGroup.Name = "controlGroup";
            this.controlGroup.Size = new System.Drawing.Size(400, 300);
            this.controlGroup.TabIndex = 0;
            // 
            // txtVersion
            // 
            this.txtVersion.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(38)))), ((int)(((byte)(38)))), ((int)(((byte)(38)))));
            this.txtVersion.Font = new System.Drawing.Font("Segoe UI", 12F);
            this.txtVersion.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(255)))));
            this.txtVersion.Location = new System.Drawing.Point(0, 238);
            this.txtVersion.Name = "txtVersion";
            this.txtVersion.Size = new System.Drawing.Size(397, 23);
            this.txtVersion.TabIndex = 5;
            this.txtVersion.Text = "Version";
            this.txtVersion.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            // 
            // btnSave
            // 
            this.btnSave.AutoSize = true;
            this.btnSave.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(188)))), ((int)(((byte)(44)))), ((int)(((byte)(0)))));
            this.btnSave.FlatAppearance.BorderSize = 0;
            this.btnSave.FlatStyle = System.Windows.Forms.FlatStyle.Flat;
            this.btnSave.Font = new System.Drawing.Font("Segoe UI", 14F);
            this.btnSave.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(255)))));
            this.btnSave.Location = new System.Drawing.Point(147, 144);
            this.btnSave.Name = "btnSave";
            this.btnSave.Padding = new System.Windows.Forms.Padding(8, 2, 8, 2);
            this.btnSave.Size = new System.Drawing.Size(100, 39);
            this.btnSave.TabIndex = 4;
            this.btnSave.Text = "Save";
            this.btnSave.UseVisualStyleBackColor = false;
            this.btnSave.Click += new System.EventHandler(this.btnSave_Click);
            // 
            // edtSettingsUrl
            // 
            this.edtSettingsUrl.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.edtSettingsUrl.Font = new System.Drawing.Font("Segoe UI", 14F);
            this.edtSettingsUrl.Location = new System.Drawing.Point(0, 91);
            this.edtSettingsUrl.Name = "edtSettingsUrl";
            this.edtSettingsUrl.Size = new System.Drawing.Size(397, 32);
            this.edtSettingsUrl.TabIndex = 3;
            // 
            // edtLoginToken
            // 
            this.edtLoginToken.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.edtLoginToken.Font = new System.Drawing.Font("Segoe UI", 14F);
            this.edtLoginToken.Location = new System.Drawing.Point(0, 24);
            this.edtLoginToken.Name = "edtLoginToken";
            this.edtLoginToken.Size = new System.Drawing.Size(397, 32);
            this.edtLoginToken.TabIndex = 1;
            // 
            // text2
            // 
            this.text2.AutoSize = true;
            this.text2.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(38)))), ((int)(((byte)(38)))), ((int)(((byte)(38)))));
            this.text2.Font = new System.Drawing.Font("Segoe UI", 12F);
            this.text2.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(255)))));
            this.text2.Location = new System.Drawing.Point(-4, 67);
            this.text2.Name = "text2";
            this.text2.Size = new System.Drawing.Size(66, 21);
            this.text2.TabIndex = 2;
            this.text2.Text = "Settings";
            // 
            // text1
            // 
            this.text1.AutoSize = true;
            this.text1.BackColor = System.Drawing.Color.FromArgb(((int)(((byte)(38)))), ((int)(((byte)(38)))), ((int)(((byte)(38)))));
            this.text1.Font = new System.Drawing.Font("Segoe UI", 12F);
            this.text1.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(255)))), ((int)(((byte)(255)))), ((int)(((byte)(255)))));
            this.text1.Location = new System.Drawing.Point(-4, 0);
            this.text1.Name = "text1";
            this.text1.Size = new System.Drawing.Size(50, 21);
            this.text1.TabIndex = 0;
            this.text1.Text = "Token";
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
            this.actionBar.TabIndex = 1;
            this.actionBar.Title = "Cameras";
            // 
            // SettingsPage
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.actionBar);
            this.Controls.Add(this.controlGroup);
            this.Name = "SettingsPage";
            this.Load += new System.EventHandler(this.SettingsPage_Load);
            this.Resize += new System.EventHandler(this.SettingsPage_Resize);
            this.controlGroup.ResumeLayout(false);
            this.controlGroup.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion
        private Controls.ControlGroup controlGroup;
        private Controls.Text text2;
        private Controls.Text text1;
        private Controls.Edit edtSettingsUrl;
        private Controls.Edit edtLoginToken;
        private Controls.ActionBar actionBar;
        private Controls.Button btnSave;
        private Controls.Text txtVersion;
    }
}
