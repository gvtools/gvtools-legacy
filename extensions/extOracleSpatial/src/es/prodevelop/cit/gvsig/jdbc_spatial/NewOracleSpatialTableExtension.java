package es.prodevelop.cit.gvsig.jdbc_spatial;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverManager;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.prodevelop.cit.gvsig.vectorialdb.wizard.NewVectorDBConnectionPanel;

import es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle.OracleSpatialDriver;
import es.prodevelop.cit.gvsig.jdbc_spatial.gui.jdbcwizard.RepeatedChooseGeometryTypePanel;
import es.prodevelop.cit.gvsig.jdbc_spatial.gui.jdbcwizard.RepeatedFieldDefinitionPanel;

public class NewOracleSpatialTableExtension extends Extension {

	private static Logger logger = Logger
			.getLogger(NewOracleSpatialTableExtension.class.getName());
	public static boolean ORACLE_JAR_PRESENT = false;

	public void initialize() {
		ORACLE_JAR_PRESENT = isOracleJarPresent();
	}

	public void execute(String actionCommand) {
		if (actionCommand.compareToIgnoreCase("NEW_ORACLE_SPATIAL") == 0) {

			IWindow w = PluginServices.getMDIManager().getActiveWindow();
			if (w instanceof View) {
				try {
					String _file = createResourceUrl(
							"images/new_geodb_table.png").getFile();
					ImageIcon iicon = new ImageIcon(_file);

					DriverManager writerManager = LayerFactory.getDM();
					WizardAndami wizard = new WizardAndami(iicon);
					RepeatedChooseGeometryTypePanel panelChoose = new RepeatedChooseGeometryTypePanel(
							wizard.getWizardComponents());
					RepeatedFieldDefinitionPanel panelFields = new RepeatedFieldDefinitionPanel(
							wizard.getWizardComponents());
					NewVectorDBConnectionPanel connPanel = new NewVectorDBConnectionPanel(
							wizard.getWizardComponents(),
							OracleSpatialDriver.NAME,
							OracleSpatialDriver.MAX_ID_LENGTH);

					wizard.getWizardComponents().addWizardPanel(panelChoose);
					wizard.getWizardComponents().addWizardPanel(panelFields);
					wizard.getWizardComponents().addWizardPanel(connPanel);

					Driver driver = new OracleSpatialDriver();
					panelFields.setWriter(((IWriteable) driver).getWriter());
					panelChoose.setDriver(driver);

					View theView = (View) w;
					MapContext mc = theView.getMapControl().getMapContext();

					NewOracleSpatialTableFinishAction action = new NewOracleSpatialTableFinishAction(
							wizard.getWizardComponents(), wizard, connPanel, mc);

					wizard.getWizardComponents().setFinishAction(action);
					wizard.getWizardComponents().getFinishButton()
							.setEnabled(false);
					wizard.getWindowInfo().setWidth(640);
					wizard.getWindowInfo().setHeight(350);
					wizard.getWindowInfo().setTitle(
							PluginServices.getText(this, "new_layer"));
					PluginServices.getMDIManager().addWindow(wizard);

				} catch (Exception ex) {
					logger.error("While showing new oracle spatial table wizard: "
							+ ex.getMessage());
				}
			}
		}
	}

	public boolean isEnabled() {

		if (!ORACLE_JAR_PRESENT)
			return false;

		IWindow w = PluginServices.getMDIManager().getActiveWindow();
		return (w instanceof View);
	}

	public boolean isVisible() {
		return isEnabled();
	}

	/**
	 * Check presence of ojdbc14.jar.
	 * 
	 * @return
	 */
	private boolean isOracleJarPresent() {

		try {
			Class rowid_class = Class.forName("oracle.sql.ROWID");
		} catch (Exception ex) {
			logger.error("Unable to instantiate ROWID (oracle jar missing?) : "
					+ ex.getMessage());
			return false;
		}
		return true;
	}

	private java.net.URL createResourceUrl(String path) {
		return getClass().getClassLoader().getResource(path);
	}

}

// [eiel-gestion-conexiones]

