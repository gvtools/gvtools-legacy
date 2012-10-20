/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */

/* CVS MESSAGES:
 *
 * $Id: NetWizard.java 31175 2009-10-06 15:16:20Z fpenarrubia $
 * $Log$
 * Revision 1.9  2006-12-04 17:13:39  fjp
 * *** empty log message ***
 *
 * Revision 1.8  2006/11/09 13:30:40  azabala
 * *** empty log message ***
 *
 * Revision 1.7  2006/11/09 12:51:12  jaume
 * *** empty log message ***
 *
 * Revision 1.6  2006/11/09 09:01:05  azabala
 * *** empty log message ***
 *
 * Revision 1.5  2006/11/08 20:14:42  azabala
 * *** empty log message ***
 *
 * Revision 1.4  2006/10/25 19:13:55  azabala
 * *** empty log message ***
 *
 * Revision 1.3  2006/10/24 08:04:41  jaume
 * *** empty log message ***
 *
 * Revision 1.2  2006/10/20 12:02:50  jaume
 * GUI
 *
 * Revision 1.1  2006/10/19 15:12:10  jaume
 * *** empty log message ***
 *
 *
 */
package org.gvsig.graph.gui.wizard;

import java.io.File;
import java.sql.Types;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.andami.ui.wizard.WizardAndami;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;

public class NetWizard extends WizardAndami implements IWindow {
	// public final static String[] COST_UNITS = {
	// PluginServices.getText(NetWizard.class, "seconds" ),
	// PluginServices.getText(NetWizard.class, "hours" ),
	// PluginServices.getText(NetWizard.class, "minutes" ),
	// "hh:mm:ss" ,
	// PluginServices.getText(NetWizard.class, "millimeters" ),
	// PluginServices.getText(NetWizard.class, "meters" ),
	// PluginServices.getText(NetWizard.class, "inches" ),
	// PluginServices.getText(NetWizard.class, "feet" ),
	// PluginServices.getText(NetWizard.class, "yards" ),
	// PluginServices.getText(NetWizard.class, "miles" ),
	// PluginServices.getText(NetWizard.class, "nautic_miles" ),
	// PluginServices.getText(NetWizard.class, "monetary" ), // unidades
	// monetarias
	// PluginServices.getText(NetWizard.class, "unknown_longitude_units" ),
	// PluginServices.getText(NetWizard.class, "unknown_time_units" ),
	// PluginServices.getText(NetWizard.class, "unknown_units" ),
	// }; // TODO push it to the corresponding geoprocess class
	private FLyrVect lyr;
	private WindowInfo wi;
	private String[] numericLayerFieldNames;
	private String fieldLength;
	private Boolean usingCosts;
	private double unitFactor;
	private String[] layerFieldNames;
	private String fieldType;
	private String fieldSense;
	private String fieldCost;

	/**
	 * flag that tells us if user pressed finish button or cancel button
	 * */
	private boolean wasFinishPressed = false;

	private boolean cleanOriginalLayer = false;
	private boolean applySnapTolerance = false;
	private double snapTolerance = 0d;
	private NetPage1 netPage1;

	public NetWizard(ImageIcon logo, FLyrVect lyr) {
		super(logo);
		this.lyr = lyr;
		netPage1 = new NetPage1(this);
		getWizardComponents().addWizardPanel(new NetPage0(this));
		getWizardComponents().addWizardPanel(netPage1);
		// getWizardComponents().addWizardPanel(new NetPage2(this));
		// getWizardComponents().addWizardPanel(new NetPage3(this));
		// getWizardComponents().addWizardPanel(new NetPage4(this));
		getWizardComponents().setFinishAction(new NetFinishAction(this));
		getWizardComponents().setCancelAction(new NetCancelAction(this));
	}

	public FLyrVect getLayer() {
		return lyr;
	}

	public WindowInfo getWindowInfo() {
		if (wi == null) {
			wi = new WindowInfo(WindowInfo.RESIZABLE | WindowInfo.MODALDIALOG);
			wi.setWidth(800);
			wi.setHeight(370);
			wi.setTitle(PluginServices.getText(this, "create_network") + "...");
		}
		return wi;
	}

	public void setCleanOriginalLayer(boolean value) {
		this.cleanOriginalLayer = value;
	}

	public boolean getCleanOriginalLayer() {
		return cleanOriginalLayer;
	}

	public void setApplySnapTolerance(boolean value) {
		this.applySnapTolerance = value;
	}

	public boolean getApplySnapTolerance() {
		return applySnapTolerance;
	}

	public void setSnapTolerance(double value) {
		this.snapTolerance = value;
	}

	public double getSnapTolerance() {
		return snapTolerance;
	}

	public void setWasFinishPressed(boolean wasFinish) {
		this.wasFinishPressed = wasFinish;
	}

	public boolean wasFinishPressed() {
		return wasFinishPressed;
	}

	public String[] getNumericLayerFieldNames() {
		if (numericLayerFieldNames == null) {
			try {
				String[] aux = lyr.getRecordset().getFieldNames();
				ArrayList temp = new ArrayList();
				for (int i = 0; i < aux.length; i++) {
					switch (lyr.getRecordset().getFieldType(i)) {
					case Types.BIGINT:
					case Types.DECIMAL:
					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.INTEGER:
					case Types.NUMERIC:
					case Types.REAL:
					case Types.SMALLINT:
					case Types.TINYINT:
						temp.add(aux[i]);
					}
				}
				numericLayerFieldNames = (String[]) temp.toArray(new String[0]);
			} catch (Exception e) {
				return new String[] { PluginServices.getText(this,
						"invalid_layer") + "!" };
			}
		}
		return numericLayerFieldNames;
	}

	public String[] getLayerFieldNames() {
		if (layerFieldNames == null) {
			try {
				layerFieldNames = lyr.getRecordset().getFieldNames();
			} catch (Exception e) {
				return new String[] { PluginServices.getText(this,
						"invalid_layer") + "!" };
			}
		}
		return layerFieldNames;
	}

	public void setLengthField(String lengthField) {
		this.fieldLength = lengthField;
	}

	public void setTypeField(String fieldType) {
		this.fieldType = fieldType;
	}

	public void setCostField(String fieldCost) {
		this.fieldCost = fieldCost;
	}

	public void setSenseField(String fieldSense) {
		this.fieldSense = fieldSense;
	}

	public void setUnitFactor(double d) {
		this.unitFactor = d;
	}

	public double getUnitFactor() {
		// NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH); //
		// use always . as decimal separator
		// Number aux;
		// try {
		// aux = nf.parse(unitFactor);
		// return aux.doubleValue();
		// } catch (ParseException e) {
		// JOptionPane.showMessageDialog(this, PluginServices.getText(this,
		// "Bad number format in unitFactor"));
		// e.printStackTrace();
		// }
		return unitFactor;
	}

	public void setUsingCosts(Boolean costs) {
		this.usingCosts = costs;
	}

	public Boolean isUsingCosts() {
		return usingCosts;
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public String getFieldLength() {
		return this.fieldLength;
	}

	public String getFieldSense() {
		return this.fieldSense;
	}

	public String getFieldCost() {
		return this.fieldCost;
	}

	public String getSenseDigitalization() {
		return netPage1.getSenseDigitalization();
	}

	public String getSenseReverseDigitalization() {
		return netPage1.getSenseReverseDigitalization();
	}

	public File getNetworkFile() {
		return new File(netPage1.getTxtFile());
	}

}
