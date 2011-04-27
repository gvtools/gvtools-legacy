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
package es.prodevelop.cit.gvsig.arcims.gui.wizards;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.ArcImsClientP;
import org.gvsig.remoteClient.arcims.ArcImsImageClient;
import org.gvsig.remoteClient.arcims.ArcImsStatus;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerFeatures;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerImage;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.gui.WizardPanel;
import com.iver.cit.gvsig.gui.wizards.WizardListener;
import com.iver.cit.gvsig.gui.wizards.WizardListenerSupport;
import com.iver.cit.gvsig.project.documents.view.gui.View;

import es.prodevelop.cit.gvsig.arcims.fmap.datasource.ArcImsWizardData;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapFeatureArcImsDriver;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapRasterArcImsDriver;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.FFeatureLyrArcIMSCollection;
import es.prodevelop.cit.gvsig.arcims.fmap.layers.FRasterLyrArcIMS;
import es.prodevelop.cit.gvsig.arcims.gui.panels.FeatureServicePanel;
import es.prodevelop.cit.gvsig.arcims.gui.panels.ImageServicePanel;
import es.prodevelop.cit.gvsig.arcims.gui.panels.ServiceNamesPanel;
import es.prodevelop.cit.gvsig.arcims.gui.panels.utils.LayersListElement;


/**
 * This class implements the wizard that enables the user to load an ArcIMS
 * layer. Passes most requests on to its <tt>ArcImsWizardData dataSource</tt>
 * object.
 *
 * @see es.prodevelop.cit.gvsig.arcims.fmap.datasource.ArcImsWizardData
 *
 * @author jldominguez
 */
public class ArcImsWizard extends WizardPanel {
    private static Logger logger = Logger.getLogger(ArcImsWizard.class.getName());
    private static final long serialVersionUID = 0;
    private WizardListenerSupport listenerSupport = new WizardListenerSupport();
    private ArcImsWizardData dataSource;
    private ServiceNamesPanel svsNamesPanel;
    private FeatureServicePanel feaServicePanel;
    private ImageServicePanel imgServicePanel;
    private JPanel mainPanel = null;
    private String nombreTema = "ArcIMS layer";
    private String layerQuery = "Empty query";
    private String host = "";
    private String serviceName = "";
    private String serviceType = "";
    private String imageFormat;
    private FLayer theLayer;
    private View theView;

    public ArcImsWizard() {
        super();
        initialize(true, false);
    }

    public ArcImsWizard(boolean editionallowed, boolean props) {
        super();
        initialize(editionallowed, props);
    }

    public ServiceNamesPanel getSvsNamesPanel() {
        return svsNamesPanel;
    }

    public void setSvsNamesPanel(ServiceNamesPanel svsNamesPanel) {
        this.svsNamesPanel = svsNamesPanel;
    }

    public void setLayerQuery(String lq) {
        layerQuery = lq;
    }

    public String getLayerQuery() {
        return layerQuery;
    }

    public void setHostName(String h) {
        host = h;
    }

    public String getHostName() {
        return host;
    }

    public void setServiceName(String n) {
        serviceName = n;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setNewLayerName(String n) {
        nombreTema = n;
    }

    public String getNewLayerName() {
        return nombreTema;
    }

    /**
     * Sets tab name and creates gui panels.
     *
     */
    private void initialize(boolean editionallowed, boolean props) {
        setTabName("ArcIMS");
        svsNamesPanel = new ServiceNamesPanel(this);

        imgServicePanel = new ImageServicePanel(this, props);
        feaServicePanel = new FeatureServicePanel(this, editionallowed, props);

        setSize(646, 359);
        setLayout(new BorderLayout(10, 10));

        setPreferredSize(new java.awt.Dimension(750, 420));
        this.add(getMainPanel(), BorderLayout.CENTER);
        this.setEnabledPanels("main");
        this.validate();
    }

    public ArcImsWizardData getDataSource() {
        return dataSource;
    }

    public void setDataSource(ArcImsWizardData ds) {
        dataSource = ds;
    }

    public void initWizard() {
        dataSource = new ArcImsWizardData();
    }

    public void execute() {
    }

    /**
     * This method will be invoqued by gvSIG when the user clicks on the
     * <i>Accept</i> button to retrieve the layers selected by the user. They
     * will be seen from gvSIG as one layer.
     *
     * @return the new ArcIMS layer to be added to the project
     */
    public FLayer getLayer() {
        //		HashMap m = new HashMap();
        //		m.put("host", "http://noruega:9080");
        //		m.put("service_name", "CarreterasIS");
        //		m.put("srs", "EPSG:23030");
        //		// m.put("layer_name", "Prueba");
        //		
        //		try {
        //			return new FRasterLyrArcIMS(m);
        //		} catch (ConnectionException e) {
        //			// TODO Auto-generated catch block
        //			return null;
        //		}

        // now we have the mapControl
        dataSource.setMapControl(getMapCtrl());

        theLayer = null;

        if (serviceType.compareToIgnoreCase(ServiceInfoTags.vIMAGESERVICE) == 0) {
            theLayer = createArcImsRasterLayer();
        }

        if (theLayer == null) {
            logger.error("Error while creating ArcIms layer.");
        }

        if (serviceType.compareToIgnoreCase(ServiceInfoTags.vFEATURESERVICE) == 0) {
            if (feaServicePanel.isGroupOptionSelected()) { // agrupar
                theLayer = createArcImsFeatureLayer(false);
            }
            else { // no agrupar
                theLayer = createArcImsFeatureLayer(true);
            }
        }

        if (theLayer == null) {
            logger.info("Returned a null layer (layers were added manually).");
        }

        return theLayer;
    }

    public FLayer getLayerAsIs() {
        return theLayer;
    }

    /**
     * Creates an ArcIMS feature layer. This method will use the
     * ArcImsWizardData method to create the layer.
     *
     * @return the ArcIMS raster layer
     */
    private FFeatureLyrArcIMSCollection createArcImsFeatureLayer(boolean sep) {
        FFeatureLyrArcIMSCollection layer = null;

        try {
            layer = dataSource.createArcImsFeatureLayer(host, serviceName,
                    layerQuery, nombreTema,
                    getMapCtrl().getMapContext().getProjection(), sep);
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }

        return layer;
    }

    /**
     * Creates an ArcIMS raster layer. This method will use the
     * ArcImsWizardData method to create the layer.
     *
     * @return the ArcIMS raster layer
     */
    private FRasterLyrArcIMS createArcImsRasterLayer() {
        FRasterLyrArcIMS layer = null;

        try {
            // the value of these parameters was set by other gui objects (panels, etc)
            layer = dataSource.createArcImsRasterLayer(host, serviceName,
                    layerQuery, nombreTema,
                    getMapCtrl().getMapContext().getProjection(), imageFormat);
        }
        catch (Exception e1) {
            logger.error("While creating ArcImsRasterLayer ", e1);
        }

        // a warning is shown if the SRS was not provided by the server:
        //		if (dataSource.isMissingSrs()) {
        //			showMissingSrsMessage();
        //		}

        //		if (layer != null) {
        //			ArcImsStatus status = layer.getArcimsStatus();
        //			String fmt = imageFormat.getFormatString();
        //			boolean imagefmtok = ((FMapRasterArcImsDriver) layer.getDriver()).testFormat(status, fmt);
        //			if (!imagefmtok) {
        //				showImageFmtNotSupportedMessage(fmt);
        //				return null;
        //			}
        //		}
        return layer;
    }

    private void showImageFmtNotSupportedMessage(String fmt) {
        JOptionPane.showMessageDialog(this,
            PluginServices.getText(this,
                "server_doesnt_support_selected_image_format") + ": " +
            fmt.toUpperCase(), PluginServices.getText(this, "error"),
            JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Utility method to display warning message.
     *
     */
    private void showMissingSrsMessage() {
        JOptionPane.showMessageDialog(this,
            PluginServices.getText(this,
                "server_provides_no_srs__scale_data_may_be_wrong"),
            PluginServices.getText(this, "warning"), JOptionPane.WARNING_MESSAGE);
    }

    /**
     * This method initializes mainPanel
     *
     * @return javax.swing.JPanel
     */
    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setBounds(new java.awt.Rectangle(15, 90, 616, 256));
            mainPanel.setLayout(new BorderLayout());
        }

        return mainPanel;
    }

    public void addWizardListener(WizardListener listener) {
        listenerSupport.addWizardListener(listener);
    }

    public void removeWizardListener(WizardListener listener) {
        listenerSupport.removeWizardListener(listener);
    }

    /**
     * Enables or disables the main panel and the layer selection panel.
     *
     * @param selectedPanel a String that identifies which panel must be enabled:
     * "main" (for the service names panel) or "service" (for the layer
     * selection panel).
     */
    public void setEnabledPanels(String selectedPanel) {
        mainPanel.removeAll();

        svsNamesPanel.setVisible(false);
        imgServicePanel.setVisible(false);
        feaServicePanel.setVisible(false);

        if (selectedPanel.compareToIgnoreCase("main") == 0) {
            mainPanel.add(svsNamesPanel, BorderLayout.CENTER);
            svsNamesPanel.setVisible(true);
        }

        if (selectedPanel.compareToIgnoreCase("feature") == 0) {
            mainPanel.add(feaServicePanel, BorderLayout.CENTER);
            feaServicePanel.setVisible(true);
        }

        if (selectedPanel.compareToIgnoreCase("image") == 0) {
            mainPanel.add(imgServicePanel, BorderLayout.CENTER);
            imgServicePanel.setVisible(true);
            imgServicePanel.getImageFormatCombo().revalidate();
        }
    }

    /**
     * Fires a notification to this wizard listeners telling them if the
     * configuration is fair enough to send a GetMap request.
     *
     * @param b
     *            <b>true</b> if the data in the wizard is enough to send a
     *            <tt>getMap</tt> request and therefore create a new layer in
     *            the project; <b>false</b> otherwise.
     */
    public void fireWizardComplete(boolean b) {
        listenerSupport.callStateChanged(b);
        callStateChanged(b);
    }

    /**
     * Decides which Panel must be loaded (ServiceNamesPanel or
     * ...ServicePanel) depending on the type of the service selected by the
     * user.
     */
    public void fillAndMoveTabbedPaneToEnabled() {
        if (this.svsNamesPanel.getSelectedServiceType()
                                  .compareToIgnoreCase(ServiceInfoTags.vIMAGESERVICE) == 0) {
            this.fillImageServiceTab();
            this.setEnabledPanels("image");
        }

        if (this.svsNamesPanel.getSelectedServiceType()
                                  .compareToIgnoreCase(ServiceInfoTags.vFEATURESERVICE) == 0) {
            this.fillFeatureServiceTab();
            this.setEnabledPanels("feature");
        }
    }

    /**
     * Loads FeatureServicePanel's controls.
     */
    private void fillImageServiceTab() {
        FMapRasterArcImsDriver drv = (FMapRasterArcImsDriver) this.dataSource.getDriver();
        ArcImsClientP cli = (ArcImsClientP) drv.getClient();
        ServiceInformation si = cli.getServiceInformation();
        String map_units = si.getMapunits();
        int _dpi = si.getScreen_dpi();

        this.imgServicePanel.setDetailsPanelServiceNameInBorder(this.serviceName);
        this.imgServicePanel.emptyTables();
        this.imgServicePanel.emptyFormatsCombo();
        this.imgServicePanel.loadServiceDetailsTable(si);

        ArcImsStatus tmpStatus = new ArcImsStatus();
        tmpStatus.setServiceInformation(si);
        tmpStatus.setService(serviceName);
        this.imgServicePanel.loadImageFormatCombo((ArcImsImageClient) cli,
            tmpStatus);

        // load available layers list
        for (int i = 0; i < si.getLayers().size(); i++) {
            if (si.getLayers().get(i) instanceof ServiceInformationLayerImage) {
                ServiceInformationLayerImage sii = (ServiceInformationLayerImage) si.getLayers()
                                                                                    .get(i);
                this.imgServicePanel.addLayerToAvailableList(new LayersListElement(
                        sii, map_units, _dpi));
            }

            if (si.getLayers().get(i) instanceof ServiceInformationLayerFeatures) {
                ServiceInformationLayerFeatures sif = (ServiceInformationLayerFeatures) si.getLayers()
                                                                                          .get(i);
                this.imgServicePanel.addLayerToAvailableList(new LayersListElement(
                        sif, map_units, _dpi));
            }
        }

        JScrollPane sp = imgServicePanel.getAvailableLayersScrollPane();
        this.imgServicePanel.setAvailableLayersTree(null);

        JTree t = imgServicePanel.getAvailableLayersTree();
        sp.setViewportView(t);
        imgServicePanel.setServiceInfoTabNumber(1);
        imgServicePanel.setServiceInfoTabNumber(0);
    }

    /**
     * Loads the Feature Service Panel's controls.
     */
    private void fillFeatureServiceTab() {
        FMapFeatureArcImsDriver drv = (FMapFeatureArcImsDriver) this.dataSource.getDriver();
        ArcImsClientP cli = (ArcImsClientP) drv.getClient();

        ServiceInformation si = cli.getServiceInformation();
        String map_units = si.getMapunits();
        int _dpi = si.getScreen_dpi();

        feaServicePanel.setDetailsPanelServiceNameInBorder(serviceName);
        feaServicePanel.emptyTables();

        // ------------------- Test -------------------
        // this.imgServicePanel.addLayerToAvailableList(new LayersListElement(sif, "meters", 96));
        // ------------------- Test -------------------
        this.feaServicePanel.loadServiceDetailsTable(si);

        // load available layers list
        for (int i = 0; i < si.getLayers().size(); i++) {
            ServiceInformationLayerFeatures sif = (ServiceInformationLayerFeatures) si.getLayers()
                                                                                      .get(i);
            this.feaServicePanel.addLayerToAvailableList(new LayersListElement(
                    sif, map_units, _dpi));
        }

        JScrollPane sp = feaServicePanel.getAvailableLayersScrollPane();
        this.feaServicePanel.setAvailableLayersTree(null);

        JTree t = feaServicePanel.getAvailableLayersTree();
        sp.setViewportView(t);

        // feaServicePanel.setServiceInfoTabNumber(1);
        feaServicePanel.setServiceInfoTabNumber(0);
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public FeatureServicePanel getFeaServicePanel() {
        return feaServicePanel;
    }

    public void setFeaServicePanel(FeatureServicePanel feaServicePanel) {
        this.feaServicePanel = feaServicePanel;
    }

    public ImageServicePanel getImgServicePanel() {
        return imgServicePanel;
    }

    public void setImgServicePanel(ImageServicePanel imgServicePanel) {
        this.imgServicePanel = imgServicePanel;
    }

    public void setImageFormat(String fmt) {
        this.imageFormat = fmt;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setServerVersionInPanels(String vers) {
        if (imgServicePanel != null) {
            imgServicePanel.setTextInVersionLabel(vers);
        }

        if (feaServicePanel != null) {
            feaServicePanel.setTextInVersionLabel(vers);
        }
    }
} // @jve:decl-index=0:visual-constraint="11,8"
