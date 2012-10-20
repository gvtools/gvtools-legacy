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
 *  Generalitat Valenciana
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

package org.gvsig.remoteClient.arcims.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.gvsig.remoteClient.arcims.styling.renderers.Renderer;

import com.iver.cit.gvsig.fmap.core.FShape;

/**
 * Class that stores a layer information of a vector image
 * 
 * @author jsanz
 * 
 */
public class ServiceInformationLayerFeatures extends ServiceInformationLayer {
	// public final static int MULTIPOINT = 1;
	// public final static int POLYLINE = 2;
	// public final static int POLYGON = 3;
	private String fclasstype;
	private int intFclassType;
	private Vector fieldsInfo = null;
	private Renderer layerMainRenderer = null;

	/**
	 * The constructor of this class receives the type of vectorial information
	 * is stored in the layer (polygon,line or point)
	 * 
	 * @param _fclasstype
	 */
	public ServiceInformationLayerFeatures(String _fclasstype) {
		this.type = ServiceInfoTags.vLAYERTYPE_F;
		this.intFclassType = FShape.NULL;
		this.setFclasstype(_fclasstype);
	}

	public String toString() {
		String out;
		out = "Layer: " + this.name + "\t(" + this.type + "-" + this.fclasstype
				+ ")";

		return out;
	}

	/**
	 * Return a FieldInformation object from the vector passing
	 * 
	 * @see FieldInformation an index
	 * @param index
	 * @return FieldInformation object
	 */
	public FieldInformation getFieldInformation(int index) {
		if ((this.fieldsInfo.size() > index)
				&& (this.fieldsInfo.get(index) != null)) {
			return (FieldInformation) this.fieldsInfo.get(index);
		} else {
			return null;
		}
	}

	/**
	 * Return a FieldInformation object from the vector passing a
	 * FieldInformation name
	 * 
	 * @see FieldInformation
	 * @param name
	 * @return FieldInformation object
	 */
	public FieldInformation getFieldInformation(String name) {
		FieldInformation fi = null;

		if (this.fieldsInfo != null) {
			Iterator it = fieldsInfo.iterator();

			while (it.hasNext()) {
				fi = (FieldInformation) it.next();

				if (fi.getName().equalsIgnoreCase(name)) {
					return fi;
				}
			}
		}

		return fi;
	}

	/**
	 * Returns an array of FieldInformation objects passing a type
	 * 
	 * @see FieldInformation
	 * @param type
	 * @return
	 */
	public ArrayList getFieldInformationByType(int type) {
		ArrayList fisA = new ArrayList();
		FieldInformation fiTemp = null;
		Iterator it = fieldsInfo.iterator();

		while (it.hasNext()) {
			fiTemp = (FieldInformation) it.next();

			if (fiTemp.getType() == type) {
				fisA.add(fiTemp);
			}
		}

		return fisA;
	}

	/**
	 * Set a FieldInformation object into the vector @see
	 * ServiceInformationLayerFeatures#fieldsInfo
	 * 
	 * @see FieldInformation
	 */
	public void addFieldInformation(FieldInformation fi) {
		if (this.fieldsInfo == null) {
			this.fieldsInfo = new Vector();
		}

		this.fieldsInfo.add(fi);
	}

	/**
	 * @return Returns the fclasstype.
	 */
	public String getFclasstype() {
		return fclasstype;
	}

	/**
	 * @param fclasstype
	 *            The fclasstype to set.
	 */
	public void setFclasstype(String fclasstype) {
		this.fclasstype = fclasstype;

		if (fclasstype.equals(ServiceInfoTags.aMULTIPOINT)) {
			intFclassType = FShape.MULTIPOINT;
		} else if (fclasstype.equals(ServiceInfoTags.aPOLYLINE)) {
			intFclassType = FShape.LINE;
		} else if (fclasstype.equals(ServiceInfoTags.aPOLYGON)) {
			intFclassType = FShape.POLYGON;
		}
	}

	/**
	 * @return Returns the fieldsInfo.
	 */
	public Vector getFieldsInfo() {
		return fieldsInfo;
	}

	// public FieldInformation getFieldInfoByName(String name){
	// Iterator it = fieldsInfo.iterator()
	// }

	/**
	 * @param fieldsInfo
	 *            The fieldsInfo to set.
	 */
	public void setFieldsInfo(Vector fieldsInfo) {
		this.fieldsInfo = fieldsInfo;
	}

	/**
	 * @return Returns the intFclassType.
	 */
	public int getIntFclassType() {
		return intFclassType;
	}

	/**
	 * @param intFclassType
	 *            The intFclassType to set.
	 */
	public void setIntFclassType(int intFclassType) {
		this.intFclassType = intFclassType;
	}

	/**
	 * @return Returns the layerMainRenderer.
	 */
	public Renderer getLayerMainRenderer() {
		return layerMainRenderer;
	}

	/**
	 * @param layerMainRenderer
	 *            The layerMainRenderer to set.
	 */
	public void setLayerMainRenderer(Renderer layerMainRenderer) {
		this.layerMainRenderer = layerMainRenderer;
	}
}
