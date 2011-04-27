/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.gui.dialogs;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.HashMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.layers.FLayer;

import es.prodevelop.cit.gvsig.arcims.fmap.layers.FFeatureLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.FRasterLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.gui.panels.FeatureServicePanel;
import es.prodevelop.cit.gvsig.arcims.gui.panels.ImageServicePanel;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.LayersListElement;
import es.prodevelop.cit.gvsig.arcims.gui.wizards.ArcImsWizard;


/**
 * The TOC ArcIMS properties container panel. It allows users to change
 * a previous request (not the server or service name).
 *
 * @author jldominguez
 *
 */
public class ArcImsPropsDialog extends JPanel implements IWindow,
    ActionListener, KeyListener, ListDataListener {
    private static Logger logger = Logger.getLogger(ArcImsPropsDialog.class.getName());
    private static final long serialVersionUID = 0;
    private JPanel buttonsPanel = null;
    private FLayer fLayer = null;
    private String layerName;
    private URL hostURL;
    private String svcName;
    private String svcType;
    private String imageFormat;
    private JButton btnApply = null;
    private JButton btnOk = null;
    private JButton btnCancel = null;
    private ArcImsWizard arcImsWizard;
    private WindowInfo theViewInfo;
    private JTextField pointerToLayerNameTextField;
    private DefaultListModel pointerToSelectedLayersListModel;

    public ArcImsPropsDialog(FLayer layer) {
        super();
        initialize(layer);
    }

    private void initialize(FLayer layer) {
        setFLayer(layer);

        JPanel theServicePanel = null;

        if (layer instanceof FRasterLyrArcIMS) {
            arcImsWizard = getArcImsWizard(((FRasterLyrArcIMS) layer).getProperties(),
                    true);
            theServicePanel = arcImsWizard.getImgServicePanel();
        }
        else {
            if (layer instanceof FFeatureLyrArcIMS) {
                arcImsWizard = getArcImsWizard(((FFeatureLyrArcIMS) layer).getProperties(),
                        false);
                theServicePanel = arcImsWizard.getFeaServicePanel();
            }
            else {
                logger.error(
                    "Unknow type of layer. ArcImsPropsDialog was not initialized. ");

                return;
            }
        }

        arcImsWizard.setBounds(0, 0, 510, 510);
        buttonsPanel = getButtonsPanel();
        buttonsPanel.setBounds(0, 510, 510, 45);

        this.setLayout(null);
        this.add(arcImsWizard); //, BorderLayout.CENTER);
        this.add(buttonsPanel); //, BorderLayout.SOUTH);
        this.setSize(new java.awt.Dimension(512, 475));
        this.setPreferredSize(new java.awt.Dimension(512, 475));

        loadControlPointers(theServicePanel);
        loadAndDisableControls(theServicePanel, layer);
    }

    private void loadControlPointers(JPanel svcPanel) {
        if (svcPanel instanceof ImageServicePanel) {
            pointerToLayerNameTextField = ((ImageServicePanel) svcPanel).getNewLayerNameTextField();
            pointerToSelectedLayersListModel = ((ImageServicePanel) svcPanel).getOrderedLayersListModel();
        }

        if ((svcPanel instanceof FeatureServicePanel) &&
                (!(svcPanel instanceof ImageServicePanel))) {
            pointerToLayerNameTextField = ((FeatureServicePanel) svcPanel).getNewLayerNameTextField();
            pointerToSelectedLayersListModel = ((FeatureServicePanel) svcPanel).getOrderedLayersListModel();
        }

        pointerToLayerNameTextField.addKeyListener(this);
        pointerToSelectedLayersListModel.addListDataListener(this);
    }

    private void getNewLayerConfig() {
        if (fLayer instanceof FRasterLyrArcIMS) {
            ((FRasterLyrArcIMS) fLayer).setLayerQuery(arcImsWizard.getLayerQuery());
            ((FRasterLyrArcIMS) fLayer).setName(arcImsWizard.getNewLayerName());
            ((FRasterLyrArcIMS) fLayer).setFormat(arcImsWizard.getImageFormat());

            ((FRasterLyrArcIMS) fLayer).setNameQueryChange(true);
        }

        if (fLayer instanceof FFeatureLyrArcIMS) {
            ((FFeatureLyrArcIMS) fLayer).setLayerQuery(arcImsWizard.getLayerQuery());
            ((FFeatureLyrArcIMS) fLayer).setName(arcImsWizard.getNewLayerName());
        }
    }

    private void loadAndDisableControls(JPanel svcPanel, FLayer lyr) {
        arcImsWizard.getSvsNamesPanel().setServerComboText(hostURL.toString());

        JButton connB = arcImsWizard.getSvsNamesPanel().getConnectButton();
        ActionEvent ae = new ActionEvent(connB, ActionEvent.ACTION_PERFORMED, "");
        arcImsWizard.getSvsNamesPanel().actionPerformed(ae);

        // JRadioButton userRadio = arcImsWizard.getSvsNamesPanel().getServiceNameSelectionModeRadioButton();
        // userRadio.setSelected(true);
        // ae = new ActionEvent(userRadio, ActionEvent.ACTION_PERFORMED, "");
        arcImsWizard.getSvsNamesPanel().setUserDecisionTrue();

        arcImsWizard.getSvsNamesPanel().setServiceType(svcType);
        arcImsWizard.getSvsNamesPanel().setUserServiceName(svcName);

        JButton nextB = arcImsWizard.getSvsNamesPanel().getNextButton();
        ae = new ActionEvent(nextB, ActionEvent.ACTION_PERFORMED, "");

        // arcImsWizard.getSvsNamesPanel().actionPerformed(ae);
        arcImsWizard.getSvsNamesPanel().pseudoNextFired(lyr);

        if (svcPanel instanceof ImageServicePanel) { // ((ImageServicePanel) svcPanel)

            JTree avTree = ((ImageServicePanel) svcPanel).getAvailableLayersTree();
            Object root = avTree.getModel().getRoot();
            int nofchildren = avTree.getModel().getChildCount(root);

            String[] lyrIds = new String[1];
            lyrIds = ((FRasterLyrArcIMS) fLayer).getLayerQuery().split(",");

            int i;
            int j;
            LayersListElement lle;
            DefaultMutableTreeNode dmtn;

            for (i = 0; i < lyrIds.length; i++) {
                String id = lyrIds[i];

                for (j = (nofchildren - 1); j >= 0; j--) {
                    dmtn = (DefaultMutableTreeNode) avTree.getModel()
                                                          .getChild(root, j);
                    lle = (LayersListElement) dmtn.getUserObject();

                    if (lle.getID().compareTo(id) == 0) {
                        // arcImsWizard.getImgServicePanel().addLayerToSelectedList(lle);
                        ((ImageServicePanel) svcPanel).addLayerToSelectedListNoConfirm(lle);
                    }
                }
            }

            ((ImageServicePanel) svcPanel).refreshSelectedLayersList();
            ((ImageServicePanel) svcPanel).getNewLayerNameTextField()
             .setText(layerName);
            ((ImageServicePanel) svcPanel).getChangeServerButton()
             .setEnabled(false);
            ((ImageServicePanel) svcPanel).setInImageFormatCombo(imageFormat);
        }

        if ((svcPanel instanceof FeatureServicePanel) &&
                (!(svcPanel instanceof ImageServicePanel))) {
            // ((FeatureServicePanel) svcPanel).getImgServiceTab_2().setVisible(false);
            // ((FeatureServicePanel) svcPanel).getServiceInfoNextButton().setEnabled(false);
            ((FeatureServicePanel) svcPanel).getChangeServerPanel()
             .setVisible(false);

            // ((FeatureServicePanel) svcPanel)
            //			JTree avTree = ((FeatureServicePanel) svcPanel).getAvailableLayersTree();
            //			Object root = avTree.getModel().getRoot();
            //			int nofchildren = avTree.getModel().getChildCount(root);
            //
            //			String[] lyrIds = new String[1];
            //			lyrIds = ((FFeatureLyrArcIMS) fLayer).getLayerQuery().split(",");
            //
            //			int i, j;
            //			LayersListElement lle;
            //			DefaultMutableTreeNode dmtn;
            //			for (i = 0; i < lyrIds.length; i++) {
            //				String id = lyrIds[i];
            //				for (j = (nofchildren - 1); j >= 0; j--) {
            //					dmtn = (DefaultMutableTreeNode) avTree.getModel().getChild(root, j);
            //					lle = (LayersListElement) dmtn.getUserObject();
            //					if (lle.getID().compareTo(id) == 0) {
            //						// arcImsWizard.getImgServicePanel().addLayerToSelectedList(lle);
            //						((FeatureServicePanel) svcPanel).addLayerToSelectedListNoConfirm(lle);
            //					}
            //				}
            //			}
            //			((FeatureServicePanel) svcPanel).refreshSelectedLayersList();
            ((FeatureServicePanel) svcPanel).getNewLayerNameTextField()
             .setText(layerName);

            // ((FeatureServicePanel) svcPanel).getChangeServerButton().setEnabled(false);
            btnApply.setEnabled(false);
        }
    }

    public void setFLayer(FLayer f) {
        fLayer = f;
    }

    /**
     * Gets a new ArcImsWizard to allow the user to update the ArcIMS
     * layer's seetings. Needs the original layer info.
     *
     * @param info the layer info
     * @return a new ArcImsWizard to allow the user to update the ArcIMS settings
     */
    public ArcImsWizard getArcImsWizard(HashMap info, boolean editionallowed) {
        if (info != null) {
            try {
                layerName = (String) info.get("layerName");
                hostURL = (URL) info.get("serverUrl");
                svcName = (String) info.get("serviceName");
                svcType = (String) info.get("serviceType");
                imageFormat = (String) info.get("format");

                // this.imasvcType = (String) info.get("serviceType");
                ArcImsWizard wiz = new ArcImsWizard(editionallowed, true);
                wiz.initWizard();

                // wiz.getDataSource().setHostService(hostURL, svcName,
                // svcType);
                return wiz;
            }
            catch (Exception e) {
                logger.error("While starting wizard ", e);
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        return null;
    }

    public JPanel getButtonsPanel() {
        if (buttonsPanel == null) {
            buttonsPanel = new JPanel();
            buttonsPanel.setLayout(null);
            buttonsPanel.setName("buttonPanel");

            buttonsPanel.add(getBtnOk(), null);
            buttonsPanel.add(getBtnApply(), null);
            buttonsPanel.add(getBtnCancel(), null);
        }

        return buttonsPanel;
    }

    public JButton getBtnOk() {
        if (btnOk == null) {
            btnOk = new JButton("ok");
            btnOk.setText(PluginServices.getText(this, "ok"));
            btnOk.setActionCommand("OK");
            btnOk.addActionListener(this);
            btnOk.setBounds(338, 10, 90, 25);
        }

        return btnOk;
    }

    public JButton getBtnApply() {
        if (btnApply == null) {
            btnApply = new JButton("apply");
            btnApply.setText(PluginServices.getText(this, "apply"));
            btnApply.setEnabled(true);
            btnApply.setActionCommand("APPLY");
            btnApply.addActionListener(this);
            btnApply.setBounds(238, 10, 90, 25);
        }

        return btnApply;
    }

    public JButton getBtnCancel() {
        if (btnCancel == null) {
            btnCancel = new JButton("cancel");
            btnCancel.setText(PluginServices.getText(this, "cancel"));
            btnCancel.setActionCommand("CANCEL");
            btnCancel.addActionListener(this);
            btnCancel.setBounds(80, 10, 90, 25);
        }

        return btnCancel;
    }

    public void close() {
        PluginServices.getMDIManager().closeWindow(this);
    }

    public WindowInfo getWindowInfo() {
        if (theViewInfo == null) {
            theViewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
            theViewInfo.setTitle(PluginServices.getText(this, "fit_ArcIms_layer"));
            theViewInfo.setWidth(this.getWidth() + 10);
            theViewInfo.setHeight(this.getHeight() + 40);
        }

        return theViewInfo;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.btnApply) {
            getNewLayerConfig();
            fLayer.getMapContext().invalidate();
        }

        if (e.getSource() == this.btnOk) {
            if (fLayer instanceof FFeatureLyrArcIMS) {
            }
            else {
                getNewLayerConfig();
                fLayer.getMapContext().invalidate();
            }

            close();
        }

        if (e.getSource() == this.btnCancel) {
            close();
        }
    }

    /**
     * Utility method to center the given JDialog on the screen.
     *
     * @param jd
     */
    public void centerThis(JDialog jd) {
        int dw = jd.getBounds().width;
        int dh = jd.getBounds().height;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        jd.setBounds((screen.width - dw) / 2, (screen.height - dh) / 2, dw, dh);
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getSource() == pointerToLayerNameTextField) {
            boolean correct = isCorrectlyConfigured();
            btnOk.setEnabled(correct);
            btnApply.setEnabled(correct);
        }
    }

    public void contentsChanged(ListDataEvent e) {
    }

    private void contentChanged() {
        boolean correct = isCorrectlyConfigured();
        btnOk.setEnabled(correct);
        btnApply.setEnabled(correct);
    }

    public void intervalAdded(ListDataEvent e) {
        contentChanged();
    }

    public void intervalRemoved(ListDataEvent e) {
        contentChanged();
    }

    private boolean isCorrectlyConfigured() {
        if (pointerToSelectedLayersListModel.size() == 0) {
            return false;
        }

        if (pointerToLayerNameTextField.getText().length() == 0) {
            return false;
        }

        return true;
    }

	public Object getWindowProfile() {
		return WindowInfo.DIALOG_PROFILE;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
