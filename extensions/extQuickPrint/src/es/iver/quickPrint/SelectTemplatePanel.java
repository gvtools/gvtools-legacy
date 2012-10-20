package es.iver.quickPrint;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

public class SelectTemplatePanel extends JPanel implements IWindow {

	private JPanel jPanel2 = null;
	private JPanel jPanel3 = null;
	private JPanel jPanel4 = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JPanel jPanel5 = null;
	private JPanel jPanel6 = null;
	private JPanel jPanel7 = null;
	private JCheckBox checkGrid = null;
	private JCheckBox checkLegend = null;
	private JRadioButton rbWithOutLogo = null;
	private JRadioButton rbDefault = null;
	private JRadioButton rbImage = null;
	private JPanel jPanel8 = null;
	private JTextField txtScale = null;
	private JPanel jPanel9 = null;
	private JTextField txtGrid = null;
	private JPanel jPanel10 = null;
	private JComboBox cmbLegend = null;
	private JTextField txtImage = null;
	private JButton bImage = null;
	private JPanel jPanel11 = null;
	private JButton bOk = null;
	private JButton bPrevView = null;
	private JButton bCancel = null;
	private JTextArea txtATitle = null;
	private JPanel jPanel12 = null;
	private JLabel lblFormat = null;
	private JComboBox cmbFormat = null;
	private JPanel jPanel13 = null;
	private JLabel lblOrientation = null;
	private JComboBox cmbOrientation = null;
	private JLabel lblCopies = null;
	private JTextField txtCopies = null;
	private ModelTemplatePanel model;
	private ButtonGroup group = new ButtonGroup();
	private JCheckBox chbScale = null;

	/**
	 * This is the default constructor
	 */
	public SelectTemplatePanel(ModelTemplatePanel model) {
		super();
		this.model = model;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		BorderLayout layout = new BorderLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		this.setLayout(layout);

		this.add(getMainPanel(), BorderLayout.CENTER);

		this.add(getAcceptCancelPanel(), BorderLayout.SOUTH);

		// constraints.anchor = GridBagConstraints.SOUTH;
		// constraints.fill = GridBagConstraints.HORIZONTAL;
		// this.add(getAcceptCancelPanel(), constraints);

	}

	/**
	 * This method initializes jPanel2
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getAcceptCancelPanel() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.add(getBPrevView(), null);
			jPanel2.add(getBOk(), null);
			jPanel2.add(getBCancel(), null);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jPanel3
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getPrintingPanel() {
		if (jPanel3 == null) {
			GridLayout gridLayout6 = new GridLayout();
			gridLayout6.setRows(2);
			jPanel3 = new JPanel();
			jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this, "impresora"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			jPanel3.setLayout(gridLayout6);
			jPanel3.add(getJPanel12(), null);
			jPanel3.add(getJPanel13(), null);
		}
		return jPanel3;
	}

	/**
	 * This method initializes jPanel4
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (jPanel4 == null) {
			jPanel4 = new JPanel(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.gridx = 0;
			constraints.insets = new Insets(5, 5, 5, 5);
			constraints.anchor = GridBagConstraints.NORTH;
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 1.0;
			jPanel4.add(getPrintingPanel(), constraints);

			constraints.anchor = GridBagConstraints.NORTH;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.weighty = 1.0;
			jPanel4.add(getViewTitlePanel(), constraints);

			constraints.anchor = GridBagConstraints.NORTH;
			constraints.fill = GridBagConstraints.BOTH;
			constraints.weighty = 0.0;
			jPanel4.add(getOptionsPanel(), constraints);

			constraints.anchor = GridBagConstraints.NORTH;
			constraints.fill = GridBagConstraints.BOTH;
			jPanel4.add(getLogoPanel(), constraints);
		}
		return jPanel4;
	}

	/**
	 * This method initializes jPanel5
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getViewTitlePanel() {
		if (jPanel5 == null) {
			GridBagLayout layout = new GridBagLayout();
			jPanel5 = new JPanel(layout);

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.BOTH;
			constraints.weightx = 1.0;
			constraints.weighty = 1.0;
			constraints.insets = new Insets(4, 4, 4, 4);
			jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this, "titulo_vista"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			jPanel5.add(getTxtATitle(), constraints);
		}
		return jPanel5;
	}

	/**
	 * This method initializes jPanel6
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getOptionsPanel() {
		if (jPanel6 == null) {
			GridBagLayout gridLayout = new GridBagLayout();
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.weightx = 1.0;
			constraints.insets = new Insets(5, 5, 5, 5);
			jPanel6 = new JPanel();
			jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this, "Options"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));
			jPanel6.setLayout(gridLayout);
			jPanel6.add(getJPanel10(), constraints);
			jPanel6.add(getJPanel9(), constraints);
			jPanel6.add(getJPanel8(), constraints);
		}
		return jPanel6;
	}

	/**
	 * This method initializes jPanel7
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getLogoPanel() {
		if (jPanel7 == null) {
			GridLayout gridLayout1 = new GridLayout();
			gridLayout1.setRows(3);

			jPanel7 = new JPanel(new GridBagLayout());
			jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this, "Image"),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.DEFAULT_POSITION, null,
					null));

			GridBagConstraints constraints = new GridBagConstraints();
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.weightx = 1.0;
			constraints.insets = new Insets(5, 5, 5, 5);

			jPanel7.add(getRbWithOutLogo(), constraints);
			jPanel7.add(getRbDefault(), constraints);
			jPanel7.add(getJPanel11(), constraints);
		}
		return jPanel7;
	}

	/**
	 * This method initializes checkGrid
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCheckGrid() {
		if (checkGrid == null) {
			checkGrid = new JCheckBox();
			checkGrid.setText(PluginServices
					.getText(this, "mostrar_cuadricula"));
			checkGrid.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (checkGrid.isSelected()) {
						getTxtGrid().setEditable(true);
					} else {
						getTxtGrid().setEditable(false);
					}
				}
			});
		}
		return checkGrid;
	}

	/**
	 * This method initializes checkLegend
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getCheckLegend() {
		if (checkLegend == null) {
			checkLegend = new JCheckBox();
			checkLegend
					.setText(PluginServices.getText(this, "mostrar_leyenda"));
			// checkLegend.addActionListener(new java.awt.event.ActionListener()
			// {
			// public void actionPerformed(java.awt.event.ActionEvent e) {
			// if (checkLegend.isSelected()){
			// getCmbLegend().setEnabled(true);
			// }else{
			// getCmbLegend().setEnabled(false);
			// }
			// }
			// });
		}
		return checkLegend;
	}

	/**
	 * This method initializes rbWithOutLogo
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbWithOutLogo() {
		if (rbWithOutLogo == null) {
			rbWithOutLogo = new JRadioButton();
			group.add(rbWithOutLogo);
			if (model.getLogo() == ModelTemplatePanel.WITHOUTLOGO) {
				rbWithOutLogo.setSelected(true);
			}
			rbWithOutLogo.setText(PluginServices.getText(this, "sin_logo"));
			rbWithOutLogo.setPreferredSize(new java.awt.Dimension(73, 20));
			rbWithOutLogo
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							if (rbWithOutLogo.isSelected()) {
								getTxtImage().setEnabled(false);
								getBImage().setEnabled(false);
							}
						}
					});
		}
		return rbWithOutLogo;
	}

	/**
	 * This method initializes rbDefault
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbDefault() {
		if (rbDefault == null) {
			rbDefault = new JRadioButton();
			group.add(rbDefault);
			if (model.getLogo() == ModelTemplatePanel.DEFAULT) {
				rbDefault.setSelected(true);
			}
			rbDefault.setText(PluginServices.getText(this, "por_defecto"));
			rbDefault.setPreferredSize(new java.awt.Dimension(94, 20));
			rbDefault.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (rbDefault.isSelected()) {
						getTxtImage().setEnabled(false);
						getBImage().setEnabled(false);
					}
				}
			});
		}
		return rbDefault;
	}

	/**
	 * This method initializes rbImage
	 * 
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbImage() {
		if (rbImage == null) {
			rbImage = new JRadioButton();
			group.add(rbImage);
			if (model.getLogo() == ModelTemplatePanel.IMAGE) {
				rbImage.setSelected(true);
			}
			rbImage.setText(PluginServices.getText(this, "imagen"));
			rbImage.setPreferredSize(new java.awt.Dimension(67, 20));
			rbImage.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (rbImage.isSelected()) {
						getTxtImage().setEnabled(true);
						getBImage().setEnabled(true);
					}
				}
			});
		}
		return rbImage;
	}

	/**
	 * This method initializes jPanel8
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel8() {
		if (jPanel8 == null) {
			jPanel8 = new JPanel();
			jPanel8.setLayout(new BorderLayout());

			jPanel8.add(getChbScale(), java.awt.BorderLayout.WEST);
			FlowLayout layout = new FlowLayout(FlowLayout.LEFT, 0, 0);
			JPanel panel = new JPanel(layout);
			panel.add(new JLabel("1:"));
			panel.add(getTxtScale());
			jPanel8.add(panel, java.awt.BorderLayout.EAST);
		}
		return jPanel8;
	}

	/**
	 * This method initializes txtScale
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtScale() {
		if (txtScale == null) {
			txtScale = new JTextField();
			txtScale.setText(String.valueOf(model.getScale()));
			if (!model.isForceScale()) {
				txtScale.setEditable(false);
			}
			txtScale.setPreferredSize(new java.awt.Dimension(80, 20));
		}
		return txtScale;
	}

	/**
	 * This method initializes jPanel9
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel9() {
		if (jPanel9 == null) {
			jPanel9 = new JPanel();
			jPanel9.setLayout(new BorderLayout());
			jPanel9.add(getCheckGrid(), java.awt.BorderLayout.WEST);
			jPanel9.add(getTxtGrid(), java.awt.BorderLayout.EAST);
		}
		return jPanel9;
	}

	/**
	 * This method initializes txtGrid
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtGrid() {
		if (txtGrid == null) {
			txtGrid = new JTextField();
			txtGrid.setText(String.valueOf(model.getGrid()));
			if (!model.isGrid()) {
				txtGrid.setEditable(false);
			}
			txtGrid.setPreferredSize(new java.awt.Dimension(80, 20));
		}
		return txtGrid;
	}

	/**
	 * This method initializes jPanel10
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel10() {
		if (jPanel10 == null) {
			jPanel10 = new JPanel();
			jPanel10.setLayout(new BorderLayout());
			jPanel10.add(getCheckLegend(), java.awt.BorderLayout.WEST);
			// commented out: currently is not possible to customize legend size
			// in maps
			// jPanel10.add(getCmbLegend(), java.awt.BorderLayout.EAST);
			jPanel10.add(new JPanel(), java.awt.BorderLayout.EAST);
		}
		return jPanel10;
	}

	/**
	 * This method initializes cmbLegend
	 * 
	 * @return javax.swing.JComboBox
	 */
	// private JComboBox getCmbLegend() {
	// if (cmbLegend == null) {
	// cmbLegend = new JComboBox(ModelTemplatePanel.sizeFont);
	// if (!model.isLegend()){
	// cmbLegend.setEnabled(false);
	// }
	// cmbLegend.setSelectedItem(new Integer(model.getLegend()));
	// cmbLegend.setPreferredSize(new java.awt.Dimension(80,20));
	// }
	// return cmbLegend;
	// }

	/**
	 * This method initializes txtImage
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtImage() {
		if (txtImage == null) {
			txtImage = new JTextField();
			txtImage.setText(model.getImage());
			if (!getRbImage().isSelected()) {
				txtImage.setEnabled(false);
			}
			txtImage.setPreferredSize(new java.awt.Dimension(220, 20));
		}
		return txtImage;
	}

	/**
	 * This method initializes bImage
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBImage() {
		if (bImage == null) {
			bImage = new JButton();
			bImage.setPreferredSize(new java.awt.Dimension(34, 20));
			bImage.setText("...");
			if (!getRbImage().isSelected()) {
				bImage.setEnabled(false);
			}
			bImage.addActionListener(new java.awt.event.ActionListener() {
				private String lastPath;

				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fileChooser = new JFileChooser();
					fileChooser.setFileFilter(new FileFilter() {

						public boolean accept(File f) {
							String path = f.getAbsolutePath();
							return path.toLowerCase().endsWith(".bmp")
									|| path.toLowerCase().endsWith(".png")
									|| path.toLowerCase().endsWith(".jpg")
									|| path.toLowerCase().endsWith(".jpeg");
						}

						public String getDescription() {
							return PluginServices.getText(this, "imagenes");
						}

					});
					if (fileChooser.showOpenDialog((Component) PluginServices
							.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
						BufferedImage tempImage;

						File f = fileChooser.getSelectedFile();

						lastPath = f.getParent();

						getTxtImage().setText(f.getAbsolutePath());
					}

				}
			});
		}
		return bImage;
	}

	/**
	 * This method initializes jPanel11
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel11() {
		if (jPanel11 == null) {
			jPanel11 = new JPanel();
			FlowLayout layout = new FlowLayout(FlowLayout.LEADING, 0, 0);
			jPanel11.setLayout(layout);
			jPanel11.add(getRbImage());
			JPanel panel = new JPanel();
			panel.add(getTxtImage());
			panel.add(getBImage());
			jPanel11.add(panel);
		}
		return jPanel11;
	}

	/**
	 * This method initializes bOk
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBOk() {
		if (bOk == null) {
			bOk = new JButton();
			bOk.setText(PluginServices.getText(this, "Aceptar"));
			bOk.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setCopies(Integer.parseInt(getTxtCopies().getText()));
					model.setFormat((String) getCmbFormat().getSelectedItem());
					model.setOrientation((String) getCmbOrientation()
							.getSelectedItem());
					model.setTitle(getTxtATitle().getText());
					model.setLegend(getCheckLegend().isSelected());
					model.setGrid(getCheckGrid().isSelected());
					model.setScale(Double.parseDouble(getTxtScale().getText()));
					model.setGrid(Double.parseDouble(getTxtGrid().getText()));
					// model.setLegend(((Integer)getCmbLegend().getSelectedItem()).intValue());
					model.forceScale(getChbScale().isSelected());
					if (getRbWithOutLogo().isSelected()) {
						model.setLogo(ModelTemplatePanel.WITHOUTLOGO);
					} else if (getRbDefault().isSelected()) {
						model.setLogo(ModelTemplatePanel.DEFAULT);
					} else if (getRbImage().isSelected()) {
						model.setLogo(ModelTemplatePanel.IMAGE);
						model.setImage(getTxtImage().getText());
					}
					model.printReport();
					PluginServices.getMDIManager().closeWindow(
							SelectTemplatePanel.this);
				}
			});
		}
		return bOk;
	}

	/**
	 * This method initializes bPrevView
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBPrevView() {
		if (bPrevView == null) {
			bPrevView = new JButton();
			bPrevView.setText(PluginServices.getText(this, "vista_previa"));
			bPrevView.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					model.setCopies(Integer.parseInt(getTxtCopies().getText()));
					model.setFormat((String) getCmbFormat().getSelectedItem());
					model.setOrientation((String) getCmbOrientation()
							.getSelectedItem());
					model.setTitle(getTxtATitle().getText());
					model.setLegend(getCheckLegend().isSelected());
					model.setGrid(getCheckGrid().isSelected());
					model.setScale(Double.parseDouble(getTxtScale().getText()));
					model.setGrid(Double.parseDouble(getTxtGrid().getText()));
					model.forceScale(getChbScale().isSelected());
					// model.setLegend(((Integer)getCmbLegend().getSelectedItem()).intValue());
					if (getRbWithOutLogo().isSelected()) {
						model.setLogo(ModelTemplatePanel.WITHOUTLOGO);
					} else if (getRbDefault().isSelected()) {
						model.setLogo(ModelTemplatePanel.DEFAULT);
					} else if (getRbImage().isSelected()) {
						model.setLogo(ModelTemplatePanel.IMAGE);
						model.setImage(getTxtImage().getText());
					}
					model.openReport();
				}
			});
		}
		return bPrevView;
	}

	/**
	 * This method initializes bCancel
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getBCancel() {
		if (bCancel == null) {
			bCancel = new JButton();
			bCancel.setText(PluginServices.getText(this, "Cancelar"));
			bCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					PluginServices.getMDIManager().closeWindow(
							SelectTemplatePanel.this);
				}
			});
		}
		return bCancel;
	}

	/**
	 * This method initializes txtATitle
	 * 
	 * @return javax.swing.JTextArea
	 */
	private JTextArea getTxtATitle() {
		if (txtATitle == null) {
			txtATitle = new JTextArea();
			txtATitle.setText(model.getTitle());
			txtATitle.setRows(2);
			txtATitle.setName("txtATitle");
			txtATitle.setBorder(getTxtScale().getBorder());
		}
		return txtATitle;
	}

	/**
	 * This method initializes jPanel12
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel12() {
		if (jPanel12 == null) {
			lblCopies = new JLabel();
			lblCopies.setText(PluginServices.getText(this, "copias"));
			lblFormat = new JLabel();
			lblFormat.setText(PluginServices.getText(this, "formatos"));
			jPanel12 = new JPanel();
			jPanel12.add(lblFormat, null);
			jPanel12.add(getCmbFormat(), null);
			jPanel12.add(lblCopies, null);
			jPanel12.add(getTxtCopies(), null);
		}
		return jPanel12;
	}

	/**
	 * This method initializes cmbFormat
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCmbFormat() {
		if (cmbFormat == null) {
			cmbFormat = new JComboBox();
			for (int i = 0; i < ModelTemplatePanel.formats.length; i++) {
				cmbFormat.addItem(ModelTemplatePanel.formats[i]);
			}
			cmbFormat.setSelectedItem(model.getFormat());
			cmbFormat.setPreferredSize(new java.awt.Dimension(100, 20));
		}
		return cmbFormat;
	}

	/**
	 * This method initializes jPanel13
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel13() {
		if (jPanel13 == null) {
			lblOrientation = new JLabel();
			lblOrientation.setText(PluginServices.getText(this, "orientacion"));
			jPanel13 = new JPanel();
			jPanel13.add(lblOrientation, null);
			jPanel13.add(getCmbOrientation(), null);
		}
		return jPanel13;
	}

	/**
	 * This method initializes cmbOrientation
	 * 
	 * @return javax.swing.JComboBox
	 */
	private JComboBox getCmbOrientation() {
		if (cmbOrientation == null) {
			cmbOrientation = new JComboBox();
			cmbOrientation.addItem(PluginServices.getText(this, "horizontal"));
			cmbOrientation.addItem(PluginServices.getText(this, "vertical"));
			cmbOrientation.setSelectedItem(model.getOrientation());
			cmbOrientation.setPreferredSize(new java.awt.Dimension(100, 20));
		}
		return cmbOrientation;
	}

	/**
	 * This method initializes txtCopies
	 * 
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtCopies() {
		if (txtCopies == null) {
			txtCopies = new JTextField();
			txtCopies.setText(String.valueOf(model.getCopies()));
			txtCopies.setPreferredSize(new java.awt.Dimension(50, 20));
		}
		return txtCopies;
	}

	public WindowInfo getWindowInfo() {
		WindowInfo wi = new WindowInfo(WindowInfo.PALETTE
				| WindowInfo.RESIZABLE);
		wi.setWidth(420);
		wi.setHeight(460);
		wi.setTitle(PluginServices.getText(this, "configuracion_impresion"));
		return wi;
	}

	/**
	 * This method initializes chbScale
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private JCheckBox getChbScale() {
		if (chbScale == null) {
			chbScale = new JCheckBox();
			chbScale.setSelected(model.isForceScale());
			chbScale.setText(PluginServices.getText(this, "force_scale"));
			chbScale.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (chbScale.isSelected()) {
						getTxtScale().setEditable(true);
					} else {
						getTxtScale().setEditable(false);
					}
				}
			});
		}
		return chbScale;
	}

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
