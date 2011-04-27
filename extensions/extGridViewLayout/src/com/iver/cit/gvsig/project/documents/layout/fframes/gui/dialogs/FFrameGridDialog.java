package com.iver.cit.gvsig.project.documents.layout.fframes.gui.dialogs;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.gvsig.gui.beans.AcceptCancelPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.gui.panels.ColorChooserPanel;
import com.iver.cit.gvsig.project.documents.layout.fframes.FFrameGrid;
import com.iver.cit.gvsig.project.documents.layout.fframes.FFrameView;
import com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame;
import com.iver.cit.gvsig.project.documents.layout.fframes.ListViewModel;
import com.iver.cit.gvsig.project.documents.layout.gui.Layout;

public class FFrameGridDialog extends JPanel implements IFFrameDialog{

	private JPanel pMarcoVista=null;
	private JScrollPane jScrollPane;
	private JList liVistas;
	private Layout layout;
	private FFrameGrid fframegrid;
	private FFrameView fframeview;
	private JLabel lblInterval = null;
	private JTextField txtInterval = null;
	private JRadioButton rbPoints = null;
	private JRadioButton rbLines = null;
	private JLabel lblWight = null;
	private JTextField txtWidth = null;
	private JLabel lblColor = null;
	private ColorChooserPanel m_colorFont=null;
	private JLabel lblColorLine = null;
	private ColorChooserPanel m_colorLine=null;  //  @jve:decl-index=0:visual-constraint="441,177"
	private AcceptCancelPanel accept;
	private FFrameGrid newFFrameGrid;
	private Rectangle2D rect;
	private Color textcolor;
	private Color linecolor;
	private boolean isAcepted;
	private JLabel lblFontSize = null;
	private JTextField txtFontSize = null;





	/**
	 * This is the default constructor
	 */
	public FFrameGridDialog(Layout layout, FFrameGrid fframe) {
		super();
		this.layout=layout;
		this.fframegrid=fframe;
		linecolor=fframegrid.getLineColor();
		textcolor=fframegrid.getFontColor();
		initialize();
	}
	/**
	 * This method initializes pMarcoVista
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPMarcoVista() {
		if (pMarcoVista == null) {
			pMarcoVista = new JPanel();
			pMarcoVista.setBorder(javax.swing.BorderFactory.createTitledBorder(
					null, PluginServices.getText(this,PluginServices.getText(this,"marco_vista")),javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12), java.awt.Color.black));
			pMarcoVista.setBounds(new java.awt.Rectangle(12,12,270,91));
			pMarcoVista.add(getJScrollPane(), null);
		}

		return pMarcoVista;
	}
	private javax.swing.JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setPreferredSize(new java.awt.Dimension(250,55));
			jScrollPane.setViewportView(getLiVistas());
		}
		return jScrollPane;
	}
	private javax.swing.JList getLiVistas() {
		if (liVistas == null) {
			ListViewModel listmodel = new ListViewModel();
			listmodel.addViews(layout);

			///ArrayList list = listmodel.getViews();

			liVistas = new javax.swing.JList();

			liVistas.setSize(new java.awt.Dimension(250,52));
			liVistas.setModel(listmodel);

			for (int i = 0; i < liVistas.getModel().getSize(); i++) {
				if (fframegrid.getFFrameDependence() != null) {
					fframeview = (FFrameView) liVistas.getModel().getElementAt(i);

					if (fframeview == fframegrid.getFFrameDependence()[0]) {
						liVistas.setSelectedIndex(i);
					}
				}
			}

			liVistas.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
					private int selectIndex=-1;
					public void valueChanged(
								javax.swing.event.ListSelectionEvent e) {
						IFFrame[] fframes=layout.getLayoutContext().getFFrames();
							int selectInt = ((JList) e.getSource())
									.getSelectedIndex();
							if (selectInt != selectIndex) {
								selectIndex = selectInt;
								if (selectIndex == -1)
									return;
								fframeview = (FFrameView) liVistas.getModel()
										.getElementAt(selectInt);

								for (int i = 0; i < fframes.length; i++) {
									IFFrame f = fframes[i];

									if (f instanceof FFrameView) {
										if (((FFrameView) f).getView() == fframeview
												.getView()) {
											fframegrid
													.setFFrameDependence(fframeview);
										}
									}
								}

//								getTNumIntervalos().setText(
//										String.valueOf(fframescalebar
//												.getNumInterval()));
//								getTDivIzquierda().setText(
//										String.valueOf(fframescalebar
//												.getNumLeft()));
//								getTIntervalo().setText(
//										fframescalebar.obtainInterval());
//								getTfNumberScale().setText(
//										fframescalebar.getDescription());
							}
						}
					});
		}

		return liVistas;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		lblFontSize = new JLabel();
		lblFontSize.setBounds(new java.awt.Rectangle(13,177,98,24));
		lblFontSize.setText("font_size");
		lblColorLine = new JLabel();
		lblColorLine.setBounds(new java.awt.Rectangle(204,207,87,27));
		lblColorLine.setText("color_linea");
		lblColor = new JLabel();
		lblColor.setBounds(new java.awt.Rectangle(15,210,96,21));
		lblColor.setText("color_fuente");
		lblWight = new JLabel();
		lblWight.setBounds(new java.awt.Rectangle(205,111,88,24));
		lblWight.setText("grosor_linea");
		lblInterval = new JLabel();
		lblInterval.setBounds(new java.awt.Rectangle(13,111,89,23));
		lblInterval.setText("intervalo");
		this.setLayout(null);
		this.setSize(426, 295);
		this.add(getPMarcoVista(), null);
		this.add(lblInterval, null);
		this.add(getTxtInterval(), null);

		this.add(getRbPoints(), null);
		this.add(getRbLines(), null);
		this.add(lblWight, null);
		this.add(getTxtWidth(), null);
		this.add(lblColor, null);
		this.add(getColorFont(), null);
		this.add(lblColorLine, null);
		this.add(getColorLine(), null);
		this.add(getAcceptCancelPanel(), null);
		this.add(lblFontSize, null);
		this.add(getTxtFontSize(), null);
		ButtonGroup group = new ButtonGroup();
	    group.add(getRbPoints());
	    group.add(getRbLines());
	}

	public void setRectangle(Rectangle2D r) {
		rect=r;
	}
	private ColorChooserPanel getColorFont() {
		if (m_colorFont == null) {
			m_colorFont = new ColorChooserPanel();
			m_colorFont.setAlpha(255);
			m_colorFont.setBounds(new java.awt.Rectangle(126,208,63,25));
			m_colorFont.setColor(fframegrid.getFontColor());
			m_colorFont.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
					textcolor=m_colorFont.getColor();
				}
			});
		}
		return m_colorFont;
	}
	private ColorChooserPanel getColorLine() {
		if (m_colorLine == null) {
			m_colorLine = new ColorChooserPanel();
			m_colorLine.setAlpha(255);
			m_colorLine.setBounds(new java.awt.Rectangle(304,208,61,25));
			m_colorLine.setSize(new java.awt.Dimension(80,25));
			m_colorLine.setColor(fframegrid.getLineColor());
			m_colorLine.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
					linecolor=m_colorLine.getColor();
				}
			});
		}
		return m_colorLine;
	}
	public WindowInfo getWindowInfo() {
		WindowInfo m_viewinfo = new WindowInfo(WindowInfo.MODALDIALOG|WindowInfo.RESIZABLE);
		m_viewinfo.setTitle(PluginServices.getText(this,
				"cuadricula"));

		return m_viewinfo;
	}
	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

	public boolean getIsAcepted() {
		// TODO Auto-generated method stub
		return false;
	}

	public IFFrame getFFrame() {
		return newFFrameGrid;
	}
	/**
	 * This method initializes txtInterval
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtInterval() {
		if (txtInterval == null) {
			txtInterval = new JTextField();
			txtInterval.setBounds(new java.awt.Rectangle(115,111,85,23));
			txtInterval.setText(String.valueOf(fframegrid.getInterval()));
		}
		return txtInterval;
	}
	/**
	 * This method initializes rbPoints
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbPoints() {
		if (rbPoints == null) {
			rbPoints = new JRadioButton();
			rbPoints.setBounds(new java.awt.Rectangle(17,148,120,21));
			rbPoints.setText("puntos");
			rbPoints.setSelected(!fframegrid.isLine());
		}
		return rbPoints;
	}
	/**
	 * This method initializes rbLines
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRbLines() {
		if (rbLines == null) {
			rbLines = new JRadioButton();
			rbLines.setBounds(new java.awt.Rectangle(149,146,154,25));
			rbLines.setText("lineas");
			rbLines.setSelected(fframegrid.isLine());
		}
		return rbLines;
	}
	/**
	 * This method initializes txtWidth
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtWidth() {
		if (txtWidth == null) {
			txtWidth = new JTextField();
			txtWidth.setBounds(new java.awt.Rectangle(298,112,47,23));
			txtWidth.setText(String.valueOf(fframegrid.getLineWidth()));
		}
		return txtWidth;
	}
	private AcceptCancelPanel getAcceptCancelPanel() {
		if (accept == null) {
			ActionListener okAction, cancelAction;
			okAction = new java.awt.event.ActionListener() {

				public void actionPerformed(java.awt.event.ActionEvent e) {
					newFFrameGrid = (FFrameGrid) fframegrid
							.cloneFFrame(layout);
					newFFrameGrid.setFFrameDependence(fframeview);
					newFFrameGrid.setBoundBox();
					newFFrameGrid.setInterval(Double.parseDouble(getTxtInterval().getText().toString()));
					newFFrameGrid.setLineWidth((Double.parseDouble(getTxtWidth().getText().toString())));

					newFFrameGrid.setTextColor(textcolor);
					newFFrameGrid.setLineColor(linecolor);

					newFFrameGrid.setIsLine(getRbLines().isSelected());
					newFFrameGrid.setSizeFont(Integer.parseInt(getTxtFontSize().getText()));

					newFFrameGrid.setRotation(fframeview.getRotation());
					PluginServices.getMDIManager().closeWindow(
							FFrameGridDialog.this);
					// m_layout.refresh();
					isAcepted = true;
				}
			};
			cancelAction = new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					newFFrameGrid=null;
					PluginServices.getMDIManager().closeWindow(
							FFrameGridDialog.this);
				}
			};
			accept = new AcceptCancelPanel(okAction, cancelAction);
			accept.setPreferredSize(new java.awt.Dimension(300, 300));
			// accept.setBounds(new java.awt.Rectangle(243,387,160,28));
			accept.setEnabled(true);
			accept.setBounds(new java.awt.Rectangle(45, 250, 300, 32));
			accept.setVisible(true);
		}
		return accept;
	}
	/**
	 * This method initializes txtFontSize
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getTxtFontSize() {
		if (txtFontSize == null) {
			txtFontSize = new JTextField();
			txtFontSize.setBounds(new java.awt.Rectangle(127,176,52,26));
			txtFontSize.setText(String.valueOf(fframegrid.getSizeFont()));
		}
		return txtFontSize;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
