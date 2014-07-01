import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import net.sf.jni4net.Bridge;

import java.io.IOException;

import javatwain.DotNetScanner;
import javatwain.IJavaProxy;
import javatwain.INativeProxy;
 
public class ScanDocuments extends JPanel
                             implements ActionListener, INativeProxy {
    private JButton mLoad, mScan;
    private JFileChooser mFileChooser;
    private JLabel mImage;
	private String mResult;
	private IJavaProxy mScanner;
	private JComboBox mSourceList;
	private String[] mSources;
 
    public ScanDocuments() {
        super(new BorderLayout());
		initTWAIN();
		
        //Create a file chooser
        mFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                ".png", "png");
        mFileChooser.setFileFilter(filter);
        mLoad = new JButton("Load");
        mLoad.addActionListener(this);

        mScan = new JButton("Scan");
        mScan.addActionListener(this);
        
		// get sources
		mSources = mScanner.GetSources();
		
		if (mSources != null) {
			mSourceList = new JComboBox(mSources);
		}
		else {
			mSourceList = new JComboBox(new String[]{"N/A"});
		}
        mSourceList.setSelectedIndex(0);
		
        // button panel
        JPanel buttonPanel = new JPanel(); 
		buttonPanel.add(mSourceList);
		buttonPanel.add(mScan);
		buttonPanel.add(mLoad);
        add(buttonPanel, BorderLayout.PAGE_START);
        
        // image panel
		JPanel imageViewer = new JPanel();
		mImage = new JLabel();
		mImage.setSize(480, 640);
		imageViewer.add(mImage);
		add(imageViewer, BorderLayout.CENTER);
    }
	
	private void initTWAIN() {
		try {
			Bridge.init();
			Bridge.LoadAndRegisterAssemblyFrom(new java.io.File("JavaTwain.j4n.dll"));
		}
		catch (Exception e) {
            e.printStackTrace();
        }
		
		mScanner = new DotNetScanner();
		mScanner.RegisterListener(this);
	}
 
    @Override
	public void actionPerformed(ActionEvent e) {

        if (e.getSource() == mLoad) {
	        
            int returnVal = mFileChooser.showOpenDialog(ScanDocuments.this);
 
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = mFileChooser.getSelectedFile();                
                mImage.setIcon(new ImageIcon(file.toPath().toString()));
            }
        } 
        else if (e.getSource() == mScan) {
			if (mSources == null)
				return;
				
			String sourceName = (String)mSourceList.getSelectedItem();
			int len = mSources.length;
			int index = 0;
			for (index = 0; index < len; ++index) {
				if (mSources[index].equals(sourceName)) {
					break;
				}
			}
			
			mScanner.AcquireImage(index);
			
			ActionListener taskPerformer = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
					//JOptionPane.showMessageDialog(null, "refresh");
					mScanner.CloseSource();
                }
            };
			int delay = 1; //milliseconds
            Timer timer = new Timer(delay, taskPerformer);
            timer.setRepeats(false);
            timer.start();
        }
    }
 
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Scan Documents");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Add content to the window.
        frame.add(new ScanDocuments());
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(480, 700);
        
        double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();
        frame.setLocation((int)(width - frameWidth) / 2, (int)(height - frameHeight) / 2);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                //Turn off metal's use of bold fonts
                UIManager.put("swing.boldMetal", Boolean.FALSE); 
                createAndShowGUI();
            }
        });
    }

    public boolean Notify(String message, String value) {
		mImage.setIcon(new ImageIcon(value));
        return true;
    }
}
