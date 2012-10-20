/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 * $Id: 
 * $Log: 
 */
package com.iver.cit.gvsig.cad;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SpatialCache;
import com.iver.cit.gvsig.layers.VectorialLayerEdited;

/**
 * Utility methods to work with CAD extension
 * 
 * @author Alvaro Zabala
 * 
 */
public class CADUtil {

	/**
	 * Adds a new feature to a vectorial layer edited
	 * 
	 * @param vle
	 *            vectorial layer to add the new feature
	 * @param geometry
	 *            new feature's geometry
	 * @param transactionDesc
	 *            description of the transaction
	 * @param values
	 *            new feature's attributes
	 * 
	 * @return index of the new feature in the layer
	 */
	public static int addFeature(VectorialLayerEdited vle, IFeature newFeature,
			String transactionDesc) {
		int index = 0;
		VectorialEditableAdapter vea = vle.getVEA();
		try {

			index = vea.addRow(newFeature, transactionDesc,
					EditionEvent.GRAPHIC);
			SpatialCache spatialCache = ((FLyrVect) vle.getLayer())
					.getSpatialCache();
			IGeometry geometry = newFeature.getGeometry();
			Rectangle2D r = geometry.getBounds2D();
			if (geometry.getGeometryType() == FShape.POINT) {
				r = new Rectangle2D.Double(r.getX(), r.getY(), 1, 1);
			}
			spatialCache.insert(r, geometry);
		} catch (ValidateRowException e) {
			NotificationManager.addError(e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e);
		}
		return vea.getInversedIndex(index);
	}

	public static void modifyFeature(VectorialLayerEdited vle, int index,
			String descTransaction, IFeature row) {
		try {
			vle.getVEA().modifyRow(index, row, descTransaction,
					EditionEvent.GRAPHIC);
		} catch (ValidateRowException e) {
			NotificationManager.addError(e.getMessage(), e);
		} catch (ExpansionFileWriteException e) {
			NotificationManager.addError(e.getMessage(), e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(e.getMessage(), e);
		}
		// draw(row.getGeometry().cloneGeometry());
	}

	/**
	 * Returns the selected features in a VectorialLayerEdited. If the selection
	 * is previous to the edition beginning, it fills selected row's list
	 * 
	 * @param vle
	 * @return
	 */
	public static List getSelectedFeatures(VectorialLayerEdited vle) {
		ArrayList selection = vle.getSelectedRow();
		if (selection.size() == 0) {
			VectorialEditableAdapter vea = vle.getVEA();
			try {
				FBitSet bitset = vea.getSelection();
				for (int j = bitset.nextSetBit(0); j >= 0; j = bitset
						.nextSetBit(j + 1)) {
					IRowEdited rowEd = vea.getRow(j);
					selection.add(rowEd);
				}
			} catch (ExpansionFileReadException e) {
				e.printStackTrace();
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
		}// selection size
		return selection;
	}
}
