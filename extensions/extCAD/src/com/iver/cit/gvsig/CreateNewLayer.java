package com.iver.cit.gvsig;

import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.driverManager.DriverManager;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.drivers.jdbc.postgis.PostGisDriver;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.gui.cad.CADToolAdapter;
import com.iver.cit.gvsig.gui.cad.MyFinishAction;
import com.iver.cit.gvsig.gui.cad.panels.ChooseGeometryType;
import com.iver.cit.gvsig.gui.cad.panels.FileBasedPanel;
import com.iver.cit.gvsig.gui.cad.panels.JPanelFieldDefinition;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.prodevelop.cit.gvsig.vectorialdb.wizard.NewVectorDBConnectionPanel;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class CreateNewLayer extends Extension {
	static ImageIcon LOGO;

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#execute(java.lang.String)
	 */
public void execute(String actionCommand) {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
				.getActiveWindow();

		if (f instanceof View) {
			try {
				View vista = (View) f;

				LOGO = new javax.swing.ImageIcon(this.getClass()
						.getClassLoader().getResource(
								"images/package_graphics.png"));
				CADToolAdapter cta = CADExtension.getCADToolAdapter();
				MapControl mapControl = vista.getMapControl();
				cta.setMapControl(mapControl);
				/*
				 * SimpleLogoJWizardFrame wizardFrame = new
				 * SimpleLogoJWizardFrame( LOGO);
				 * wizardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				 *
				 * SwingUtilities.updateComponentTreeUI(wizardFrame);
				 *
				 * wizardFrame.setTitle("Creaci�n de un nuevo Tema");
				 */
				WizardAndami wizard = new WizardAndami(LOGO);

				DriverManager writerManager = LayerFactory.getDM();
				ArrayList spatialDrivers = new ArrayList();
				String[] writerNames = writerManager.getDriverNames();
				for (int i = 0; i < writerNames.length; i++) {
					Driver drv = writerManager.getDriver(writerNames[i]);
					if (drv instanceof ISpatialWriter)
						spatialDrivers.add(drv.getName());
				}

				ChooseGeometryType panelChoose = new ChooseGeometryType(wizard
						.getWizardComponents());
				JPanelFieldDefinition panelFields = new JPanelFieldDefinition(
						wizard.getWizardComponents());

				if (actionCommand.equals("SHP")) {
					wizard.getWizardComponents().addWizardPanel(panelChoose);
					wizard.getWizardComponents().addWizardPanel(panelFields);

					Driver driver = writerManager.getDriver("gvSIG shp driver");
					panelFields.setWriter(((IWriteable) driver).getWriter());
					panelChoose.setDriver(driver);
					FileBasedPanel filePanel = new FileBasedPanel(wizard
							.getWizardComponents());
					filePanel.setFileExtension("shp");
					wizard.getWizardComponents().addWizardPanel(filePanel);

					wizard.getWizardComponents().setFinishAction(
							new MyFinishAction(wizard.getWizardComponents(),
									vista, actionCommand));
				}
				if (actionCommand.equals("POSTGIS")) {
					wizard.getWizardComponents().addWizardPanel(panelChoose);
					wizard.getWizardComponents().addWizardPanel(panelFields);
					Driver driver = writerManager
							.getDriver(PostGisDriver.NAME);
					panelChoose.setDriver(driver);
					panelFields.setWriter(((IWriteable) driver).getWriter());
					
					// wizard.getWizardComponents().addWizardPanel(
					// 		new PostGISpanel(wizard.getWizardComponents()));
					
					
	       			 NewVectorDBConnectionPanel connPanel = 
	       				 new NewVectorDBConnectionPanel(
	       						 wizard.getWizardComponents(),
	       						 PostGisDriver.NAME,
	       						 20);
	       			wizard.getWizardComponents().addWizardPanel(connPanel);
	       			 

					wizard.getWizardComponents().setFinishAction(
							new MyFinishAction(wizard.getWizardComponents(),
									vista, actionCommand));
				}

				wizard.getWizardComponents().getFinishButton()
						.setEnabled(false);
				wizard.getWindowInfo().setWidth(640);
				wizard.getWindowInfo().setHeight(350);
				wizard.getWindowInfo().setTitle(
						PluginServices.getText(this, "new_layer"));
				// Utilities.centerComponentOnScreen(wizard);
				// wizardFrame.show();
				PluginServices.getMDIManager().addWindow(wizard);
				// System.out.println("Salgo con " + panelChoose.getLayerName());
			} catch (DriverLoadException e) {
				NotificationManager.addError(e.getMessage(),e);
			}
		}
	}
	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		View f = (View) PluginServices.getMDIManager().getActiveWindow();

		if (f == null)
			return false;
		return true;
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		com.iver.andami.ui.mdiManager.IWindow f = PluginServices.getMDIManager()
				.getActiveWindow();

		if (f == null) {
			return false;
		}

		if (f instanceof View)
			return true;
		return false;
	}
}

// [eiel-gestion-conexiones]
