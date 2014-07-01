using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;

using Dynamsoft.DotNet.TWAIN;

namespace JavaTwain
{
    public interface IJavaProxy
    {
        bool AcquireImage(int iIndex);
        String[] GetSources();
        bool RegisterListener(INativeProxy proxy);
        void CloseSource();
    }

    public interface INativeProxy
    {
        bool Notify(String message, String value);
    }

    public class DotNetScanner : IJavaProxy
    {
        private readonly Random r = new Random();
        private DynamicDotNetTwain dynamicDotNetTwain;
        private INativeProxy listener;

        public DotNetScanner()
        {
            // initialize TWAIN Component
            try
            {
                dynamicDotNetTwain = new Dynamsoft.DotNet.TWAIN.DynamicDotNetTwain();
                dynamicDotNetTwain.OnPostAllTransfers += new Dynamsoft.DotNet.TWAIN.Delegate.OnPostAllTransfersHandler(this.dynamicDotNetTwain_OnPostAllTransfers);
                dynamicDotNetTwain.MaxImagesInBuffer = 64;
                dynamicDotNetTwain.IfAppendImage = true;
                dynamicDotNetTwain.IfThrowException = true;
                dynamicDotNetTwain.IfShowUI = false;
                dynamicDotNetTwain.IfThrowException = true;
                dynamicDotNetTwain.ScanInNewProcess = true;
            }
            catch 
            {
                MessageBox.Show(dynamicDotNetTwain.ErrorString);
            }

        }

        private static Image resizeImage(Image img, Size size)
        {
            return (Image)(new Bitmap(img, size));
        }

        private void dynamicDotNetTwain_OnPostAllTransfers()
        {
            //MessageBox.Show("dynamicDotNetTwain_OnPostAllTransfers");

            if (dynamicDotNetTwain.MaxImagesInBuffer < 1)
            {
                return;
            }

            Image img = dynamicDotNetTwain.GetImage(0);
            img = resizeImage(img, new Size(480, 640));
            img.Save("twain.png");

            if (listener != null)
            {
                listener.Notify("data ready", "twain.png");
            }
        }

        public void CloseSource()
        {
            dynamicDotNetTwain.CloseSource();
        }

        public bool AcquireImage(int iIndex)
        {
            try
            {
                //dynamicDotNetTwain.CloseSource();
                bool success = dynamicDotNetTwain.SelectSourceByIndex(Convert.ToInt16(iIndex));
                dynamicDotNetTwain.OpenSource();
                dynamicDotNetTwain.AcquireImage();
            }
            catch (Dynamsoft.DotNet.TWAIN.TwainException exp)
            {
                String errorstr = "";
                errorstr += "Error " + exp.Code + "\r\n" + "Description: " + exp.Message + "\r\nPosition: " + exp.TargetSite + "\r\nHelp: " + exp.HelpLink + "\r\n";
                MessageBox.Show(errorstr);
            }
            catch (Exception exp)
            {
                String errorstr = "";
                errorstr += "ErrorMessage: " + exp.Message + "\r\n";
                MessageBox.Show(errorstr);
            }

            return true;
        }

        public bool RegisterListener(INativeProxy obj)
        {
            listener = obj;
            return true;
        }

        public String[] GetSources()
        {
            int iIndex;
            try
            {
                dynamicDotNetTwain.OpenSourceManager();
            }
            catch
            {
                MessageBox.Show(dynamicDotNetTwain.ErrorString);
            }

            if (dynamicDotNetTwain.SourceCount < 1)
            {
                MessageBox.Show("no source");
                return null;
            }

            String[] sources = new String[dynamicDotNetTwain.SourceCount];

            for (iIndex = 0; iIndex < dynamicDotNetTwain.SourceCount; iIndex++)
            {
                sources[iIndex] = dynamicDotNetTwain.SourceNameItems(Convert.ToInt16(iIndex));
            }

            return sources;
        }
    }
}
