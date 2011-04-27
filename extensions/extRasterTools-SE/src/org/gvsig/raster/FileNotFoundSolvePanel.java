package org.gvsig.raster;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.fmap.raster.layers.ISolveErrorListener;
import org.gvsig.gui.beans.AcceptCancelPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

/**
 * Panel de resolución de errores construido como el de appgvSIG.
 * El uso de este panel es temporal hasta que se generalize uno para todo 
 * tipo de capas. Para ello no debería depender de FileNotFoundSolve sino
 * de ISolveErrorListener.
 * 
 * 18/03/2009
 * @author Nacho Brodin nachobrodin@gmail.com
 */
public class FileNotFoundSolvePanel extends JPanel implements IWindow {
	private static final long   serialVersionUID = 1L;
	private JPanel              jPanel           = null;
	private JPanel              jPanel1          = null;
	private JPanel              jPanel2          = null;
	private JPanel              jPanel3          = null;
	private JTextField          jTextField       = null;
	private JTextField          jTextField1      = null;
	private JButton             jButton          = null;
	private AcceptCancelPanel   accept           = null;
	private JFileChooser        fileChooser;
	private static String       lastPath;
	private ISolveErrorListener model;
	File                        myfile           = null;
	private JPanel              jPanel4          = null;
	private JLabel              jLabel1          = null;

	/**
	 * This is the default constructor
	 */
	public FileNotFoundSolvePanel(ISolveErrorListener fnfs) {
		super();
		this.model = fnfs;
		initialize();

	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new FlowLayout());
		this.setSize(426, 174);
		this.add(getJPanel(), null);
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getJPanel1(), java.awt.BorderLayout.NORTH);
			jPanel.add(getJPanel2(), java.awt.BorderLayout.CENTER);
			jPanel.add(getJPanel3(), java.awt.BorderLayout.SOUTH);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, PluginServices.getText(this,"incorrect_path"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanel1.add(getJTextField(), null);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jPanel2
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(new BorderLayout());
			jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, PluginServices.getText(this,"new_file_properties"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
			jPanel2.add(getJPanel4(), java.awt.BorderLayout.CENTER);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jPanel3
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.add(getAcceptCancelPanel());
		}
		return jPanel3;
	}
	
	private AcceptCancelPanel getAcceptCancelPanel() {
		if (accept == null) {
			ActionListener okAction, cancelAction;
			okAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.createLayer(new File(getJTextField1().getText()));
					PluginServices.getMDIManager().closeWindow(
							FileNotFoundSolvePanel.this);
				}
			};
			cancelAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PluginServices.getMDIManager().closeWindow(
							FileNotFoundSolvePanel.this);
				}
			};
			accept = new AcceptCancelPanel(okAction, cancelAction);
			accept.setPreferredSize(new java.awt.Dimension(300, 300));
			accept.setEnabled(true);
			accept.setVisible(true);
		}
		return accept;
	}

	/**
	 * This method initializes jTextField
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setPreferredSize(new java.awt.Dimension(400,23));
			jTextField.setEditable(false);
			jTextField.setText(model.getPath());
		}
		return jTextField;
	}

	/**
	 * This method initializes jTextField1
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setPreferredSize(new java.awt.Dimension(300,23));
		}
		return jTextField1;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setPreferredSize(new java.awt.Dimension(34,20));
			jButton.setText("...");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					myfile = addObjects();
					if (myfile != null)
						getJTextField1().setText(myfile.getAbsolutePath());
				}
			});
		}
		return jButton;
	}
	
	public File addObjects() {
		fileChooser = new JFileChooser(lastPath);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setAcceptAllFileFilterUsed(false);
			
		RasterDriverFileFilter vff = new RasterDriverFileFilter();
		fileChooser.addChoosableFileFilter(vff);
		fileChooser.setFileFilter(vff);

		int result = fileChooser.showOpenDialog(this);

		File newFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			lastPath = fileChooser.getCurrentDirectory().getAbsolutePath();
			newFile = fileChooser.getSelectedFile();
			return newFile;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.iver.andami.ui.mdiManager.IWindow#getWindowInfo()
	 */
	public WindowInfo getWindowInfo() {
		WindowInfo wi=new WindowInfo(WindowInfo.MODALDIALOG | WindowInfo.RESIZABLE);
		wi.setTitle(PluginServices.getText(this,"dont_find_the_file") + ": " + model.getLayerName());
		return wi;
	}

	/**
	 * This method initializes jPanel4
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText(PluginServices.getText(this,"path"));
			FlowLayout flowLayout1 = new FlowLayout();
			flowLayout1.setAlignment(java.awt.FlowLayout.RIGHT);
			jPanel4 = new JPanel();
			jPanel4.setLayout(flowLayout1);
			jPanel4.add(jLabel1, null);
			jPanel4.add(getJTextField1(), null);
			jPanel4.add(getJButton(), null);
		}
		return jPanel4;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}
	
	public class RasterDriverFileFilter extends FileFilter {
		/*
		 * (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;

			if (f.getParentFile().getName().equals("cellhd")) {
				if (f.getName().endsWith(".rmf") || f.getName().endsWith(".rmf~"))
					return false;
				return true;
			}

			// Comprobamos que no sea un rmf propio, osea, que contenga xml
			if (f.getName().toLowerCase().endsWith(".rmf")) {
				FileInputStream reader = null;
				try {
					reader = new FileInputStream(f);
					String xml = "";
					for (int i = 0; i < 6; i++)
						xml += (char) reader.read();
					if (xml.equals("<?xml "))
						return false;
				} catch (Exception e) {
				} finally {
					try {
						reader.close();
					} catch (Exception e) {}
				}
			}

			return FLyrRasterSE.isFileAccepted(f);
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		public String getDescription() {
			return PluginServices.getText(this, "raster");
		}
	}

}  //  @jve:decl-index=0:visual-constraint="71,10"
