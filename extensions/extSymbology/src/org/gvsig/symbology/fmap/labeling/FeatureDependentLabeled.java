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
* $Id: FeatureDependentLabeled.java 10671 2007-03-09 08:33:43Z jaume $
* $Log$
* Revision 1.2  2007-03-09 08:33:43  jaume
* *** empty log message ***
*
* Revision 1.1.2.2  2007/02/01 11:42:47  jaume
* *** empty log message ***
*
* Revision 1.1.2.1  2007/01/30 18:10:45  jaume
* start commiting labeling stuff
*
*
*/
package org.gvsig.symbology.fmap.labeling;


import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelingFactory;
import com.iver.utiles.XMLEntity;

public class FeatureDependentLabeled implements ILabelingMethod {
	private boolean flagDefinesPriorities;
	private ArrayList<LabelClass> classes = new ArrayList<LabelClass>();

	public void addLabelClass(LabelClass lbl) throws IllegalArgumentException{
		if (getLabelClassByName(lbl.getName()) !=null) {
			throw new IllegalArgumentException("A class with the same name already exists!");
		}
		classes.add(lbl);
	}

	public void deleteLabelClass(LabelClass lbl) {
		classes.remove(lbl);
	}

	public String getClassName() {
		return getClass().getName();
	}

	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", getClassName());
		xml.putProperty("definesPriorities", definesPriorities());



		LabelClass[] labels = getLabelClasses();
		if (labels!=null) {
			XMLEntity xmlLabels = new XMLEntity();
			xmlLabels.putProperty("id", "LabelClasses");
			for (int i = 0; i < labels.length; i++) {
				xmlLabels.addChild(labels[i].getXMLEntity());
			}
			xml.addChild(xmlLabels);
		}
		return xml;
	}

	public LabelClass[] getLabelClasses() {
		return classes.toArray(new LabelClass[0]);
	}

	public void setXMLEntity(XMLEntity xml) {
		if (xml.contains("definesPriorities"))
			setDefinesPriorities(xml.getBooleanProperty("definesPriorities"));

		XMLEntity aux = xml.firstChild("id", "defaultLabelClass");

//		if (aux!=null)
//			defaultLabel = LabelingFactory.createLabelClassFromXML(aux);

		aux = xml.firstChild("id", "LabelClasses");
		if (aux!=null) {
			for (int i = 0; i < aux.getChildrenCount(); i++) {
				addLabelClass(LabelingFactory.
						createLabelClassFromXML(aux.getChild(i)));
			}
		}
	}

	public boolean allowsMultipleClass() {
		return true;
	}

	public void renameLabelClass(LabelClass lbl, String newName) {
		LabelClass label = (LabelClass) classes.get(classes.indexOf(lbl));
		label.setName(newName);
	}

	public IFeatureIterator getFeatureIteratorByLabelClass(FLyrVect layer, LabelClass lc, ViewPort viewPort, String[] usedFields)
	throws ReadDriverException {
//		if(lc.isUseSqlQuery()){
		String sqlFields = "";
		for (int i = 0; i < usedFields.length; i++) {
			sqlFields += usedFields[i];
			if (i < usedFields.length -1) sqlFields += ", ";
		}
		String fieldNames[] = layer.getSource().getRecordset().getFieldNames();
		StringBuilder sql = new StringBuilder();
		sql.append("select ");
		for (int i=0; i<fieldNames.length-1; i++) {
			sql.append(fieldNames[i]);
			sql.append(",");
		}
		sql.append(fieldNames[fieldNames.length-1]);
		sql.append(" from ");
		sql.append(layer.getRecordset().getName());
		if(lc.isUseSqlQuery()){
			sql.append(" where ");
			sql.append(lc.getSQLQuery());
		}
		sql.append(";");
		return layer.getSource().getFeatureIterator(sql.toString(), layer.getCrs());
//		}
//		else {
//			return layer.getSource().getFeatureIterator(
//					viewPort.getAdjustedExtent(), usedFields,
//					layer.getProjection(), true);
//		}
	}

	public boolean definesPriorities() {
		return flagDefinesPriorities;
	}

	public void setDefinesPriorities(boolean flag) {
		if (flag == false) {
			LabelClass[] lcs = getLabelClasses();
			for (int i = 0; i < lcs.length; i++) {
				lcs[i].setPriority(0);
			}
		}
		flagDefinesPriorities = flag;
	}

	public void clearAllClasses() {
		classes.clear();
	}

	public LabelClass getLabelClassByName(String labelName) {
		LabelClass[] classes = getLabelClasses();
		for (int i = 0; i < classes.length; i++) {
			if (classes[i].getName().equals(labelName)) {
				return classes[i];
			}
		}
		return null;
	}

	public ILabelingMethod cloneMethod() {
		// TODO Auto-generated method stub
		throw new Error("Not yet implemented!");
	}
}
