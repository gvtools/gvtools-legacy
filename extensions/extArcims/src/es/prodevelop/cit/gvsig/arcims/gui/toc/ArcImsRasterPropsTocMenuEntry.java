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
package es.prodevelop.cit.gvsig.arcims.gui.toc;

/**
 * This class implements the raster properties menu item that is added to the
 * ArcIMS layer's popup menu.
 * 
 * @author jldominguez
 * @author Nacho Brodin (brodin_ign@gva.es)
 */
public class ArcImsRasterPropsTocMenuEntry {

	// private JMenuItem propsMenuItem;
	// FLayer lyr = null;
	// private ArcImsRasterPropsDialog propsDialog = null;
	// private BandSetupPanel bandSetup = null;
	//
	// public void initialize(FPopupMenu m) {
	// super.initialize(m);
	//
	// if (isTocItemBranch()) {
	// lyr = getNodeLayer();
	//
	// // ArcIMS layer
	// if ((lyr instanceof FRasterLyrArcIMS)) {
	// propsMenuItem = new JMenuItem(PluginServices.getText(this,
	// "propiedades_raster"));
	// getMenu().add(propsMenuItem);
	// propsMenuItem.setFont(FPopupMenu.theFont);
	// getMenu().setEnabled(true);
	// propsMenuItem.addActionListener(this);
	// }
	// }
	// }
	//
	// /**
	// * Creates an ArcImsRasterPropsDialog object and adds it to the
	// MDIManager.
	// */
	// public void actionPerformed(ActionEvent e) {
	// lyr = getNodeLayer();
	//
	// if (lyr instanceof FRasterLyrArcIMS &&
	// (((FRasterLyrArcIMS) lyr).getPxRaster() != null)) {
	// RasterFilterStackManager stackManager = null;
	//
	// FRasterLyrArcIMS layer = (FRasterLyrArcIMS) lyr;
	//
	// stackManager = new RasterFilterStackManager(layer.getFilterStack());
	//
	// propsDialog = new ArcImsRasterPropsDialog(layer,
	// stackManager.getTransparencyRGB());
	//
	// FMapRasterArcImsDriver drv = (FMapRasterArcImsDriver) layer.getDriver();
	// //).getClient().getServiceInformation().getMapunits();
	// ArcImsImageClient cli = (ArcImsImageClient) drv.getClient();
	// ServiceInformation si = cli.getServiceInformation();
	// String units = si.getMapunits();
	//
	// int alpha = layer.getPxRaster().getAlpha();
	//
	// bandSetup = (BandSetupPanel) ((FilterRasterDialogPanel)
	// propsDialog.getContentPane()).getPanelByClassName(
	// "BandSetupPanel");
	//
	// GeoRasterFile[] files = layer.getPxRaster().getFiles();
	// bandSetup.addFiles(files);
	//
	// for (int i = 0; i < files.length; i++)
	// propsDialog.addNumBands(files[i].getBandCount());
	//
	// PxRaster px = layer.getPxRaster();
	// int posR = 0;
	// int posG = 0;
	// int posB = 0;
	//
	// for (int i = 0; i < px.getPosFile(GeoRasterFile.RED_BAND); i++)
	// posR += files[i].getBandCount();
	//
	// posR += px.getBand(GeoRasterFile.RED_BAND);
	//
	// for (int i = 0; i < px.getPosFile(GeoRasterFile.GREEN_BAND); i++)
	// posG += files[i].getBandCount();
	//
	// posG += px.getBand(GeoRasterFile.GREEN_BAND);
	//
	// for (int i = 0; i < px.getPosFile(GeoRasterFile.BLUE_BAND); i++)
	// posB += files[i].getBandCount();
	//
	// posB += px.getBand(GeoRasterFile.BLUE_BAND);
	//
	// bandSetup.assignBand(posR, GeoRasterFile.RED_BAND);
	// bandSetup.assignBand(posG, GeoRasterFile.GREEN_BAND);
	// bandSetup.assignBand(posB, GeoRasterFile.BLUE_BAND);
	//
	// InfoPanel pInfo = (InfoPanel) this.propsDialog.getPanelByClassName(
	// "InfoPanel");
	// pInfo.setBands(posR, posG, posB);
	// pInfo.addFiles(files);
	//
	// //Asignación del alpha actual de la imagen al dialogo
	// RasterTransparencyPanel rasterTrans = (RasterTransparencyPanel)
	// ((FilterRasterDialogPanel)
	// propsDialog.getContentPane()).getPanelByClassName(
	// "RasterTransparencyPanel");
	// rasterTrans.setOpacity(alpha);
	//
	// propsDialog.setRasterFilterStackManager(stackManager);
	// bandSelector(stackManager, posR, posG, posB);
	// propsDialog.readStat();
	// actionPerformed(stackManager, propsDialog, null);
	//
	// PluginServices.getMDIManager().addWindow(propsDialog);
	// }
	// }
	//
	// /**
	// * Selects the active bands in the band panel
	// * from the filters retrieved from the FilterStack
	// *
	// * @param stackManager RasterFilterStackManager
	// * @param posR R-band position
	// * @param posG G-band position
	// * @param posB B-band position
	// */
	// private void bandSelector(RasterFilterStackManager stackManager, int
	// posR,
	// int posG, int posB) {
	// ArrayList stackList = stackManager.getStringsFromStack();
	// String hideBands = null;
	//
	// for (int i = 0; i < stackList.size(); i++) {
	// if (((String) stackList.get(i)).startsWith(
	// "filter.removebands.bands")) {
	// hideBands = stackManager.getValue((String) stackList.get(i));
	// }
	// }
	//
	// // 1, 2 or 3 bands to show
	// if (hideBands != null) {
	// int pos = 2;
	//
	// if (hideBands.length() == 1) {
	// pos = 1;
	// }
	// else if (hideBands.length() == 2) {
	// pos = 0;
	// }
	//
	// bandSetup.getFileList().getJComboBox().setSelectedIndex(pos);
	//
	// // Reset table controls
	// for (int i = 0;
	// i < bandSetup.getRGBTable().getModel().getRowCount();
	// i++)
	// for (int j = 0; j < 3; j++)
	// bandSetup.getRGBTable().getModel()
	// .setValueAt(new Boolean(false), i, j);
	//
	// if (hideBands.equals("GB") || hideBands.equals("G") ||
	// hideBands.equals("B")) {
	// bandSetup.getRGBTable().getModel()
	// .setValueAt(new Boolean(true), posR, 0);
	// }
	//
	// if (hideBands.equals("RB") || hideBands.equals("R") ||
	// hideBands.equals("B")) {
	// bandSetup.getRGBTable().getModel()
	// .setValueAt(new Boolean(true), posG, 1);
	// }
	//
	// if (hideBands.equals("RG") || hideBands.equals("R") ||
	// hideBands.equals("G")) {
	// bandSetup.getRGBTable().getModel()
	// .setValueAt(new Boolean(true), posB, 2);
	// }
	// }
	// }
	//
	// /**
	// * Modificación del estado de los controles del panel de brillo y
	// contraste
	// */
	// public void actionPerformed(RasterFilterStackManager stackManager,
	// ArcImsRasterPropsDialog propsDialog, FLyrRaster fLayer) {
	// EnhancedBrightnessContrastPanel bcPanel =
	// (EnhancedBrightnessContrastPanel) ((FilterRasterDialogPanel)
	// propsDialog.getContentPane()).getPanelByClassName(
	// "EnhancedBrightnessContrastPanel");
	//
	// //bcPanel.setPropertiesDialog(propsDialog);
	// if (stackManager.isActive(stackManager.getTypeFilter("brightness"))) {
	// bcPanel.getCBrightC().setSelected(true);
	// bcPanel.setBCControlEnabled(true);
	//
	// RasterFilter bright = stackManager.getFilter("brightness");
	//
	// if (bright.getParam("incrBrillo") != null) {
	// int incr = ((Integer) bright.getParam("incrBrillo")).intValue();
	// bcPanel.getLabelSliderText().setSliderValue(incr);
	// bcPanel.getLabelSliderText().setTextValue(String.valueOf(incr));
	// }
	// }
	//
	// if (stackManager.isActive(stackManager.getTypeFilter("contrast"))) {
	// bcPanel.getCBrightC().setSelected(true);
	// bcPanel.setBCControlEnabled(true);
	//
	// RasterFilter cont = stackManager.getFilter("contrast");
	//
	// if (cont.getParam("incrContraste") != null) {
	// int incr = ((Integer) cont.getParam("incrContraste")).intValue();
	// bcPanel.getLabelSliderText1().setSliderValue(incr);
	// bcPanel.getLabelSliderText1().setTextValue(String.valueOf(incr));
	// }
	// }
	//
	// if (stackManager.isActive(stackManager.getTypeFilter("enhanced"))) {
	// bcPanel.getCEnhanced().setSelected(true);
	// bcPanel.setEControlEnabled(true);
	//
	// RasterFilter enhan = stackManager.getFilter("enhanced");
	// RasterFilter tail = stackManager.getFilter("tail");
	//
	// boolean rem = ((Boolean) enhan.getParam("remove")).booleanValue();
	// bcPanel.getJCheckBox().setSelected(rem);
	//
	// if (stackManager.isActive(stackManager.getTypeFilter("tail"))) {
	// double percent = (stackManager.getStackStats().tailPercent) * 100;
	// bcPanel.getCheckSliderText()
	// .setTextValue(String.valueOf(percent));
	// bcPanel.getCheckSliderText().setSliderValue((int) percent);
	// bcPanel.getCheckSliderText().setSelected(true);
	// bcPanel.getCheckSliderText().setControlEnabled(true);
	//
	// boolean remove = ((Boolean) tail.getParam("remove")).booleanValue();
	//
	// if ((remove == true) || (rem == true)) {
	// bcPanel.getJCheckBox().setSelected(true);
	// }
	// else
	// {
	// bcPanel.getJCheckBox().setSelected(false);
	// }
	// }
	// }
	// }
}
