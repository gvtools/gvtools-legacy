/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
package com.iver.cit.gvsig.gui.selectionByTheme;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.layerOperations.Selectable;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.QueryByThemeVisitor;

/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class MySelectionByThemeListener implements SelectionByThemeListener {
	/**
	 * @see com.iver.cit.gvsig.gui.selectionByTheme.SelectionByThemeListener#newSet(int,
	 *      int, int)
	 */
	public void newSet(FLayer[] toSelect, FLayer selectionLayer, int action) {
		long t1 = System.currentTimeMillis();
		if (selectionLayer instanceof FLyrVect) {
			try {
				Selectable selected = ((FLyrVect) selectionLayer)
						.getRecordset();
				for (int i = 0; i < toSelect.length; i++) {
					if (toSelect[i] instanceof FLyrVect) {
						Selectable capa = ((FLyrVect) toSelect[i])
								.getRecordset();
						QueryByThemeVisitor visitor = new QueryByThemeVisitor(
								toSelect[i], selectionLayer, action);
						capa.clearSelection();

						((VectorialData) selectionLayer).process(visitor,
								selected.getSelection());
						capa.setSelection(visitor.getBitset());
					}
				}
			} catch (ProcessVisitorException e) {
				NotificationManager.addError("Error leyendo las capas", e);
			} catch (ReadDriverException e) {
				NotificationManager.addError("Error leyendo las capas", e);
			} catch (VisitorException e) {
				NotificationManager.addError("Error leyendo las capas", e);
			}

		}
		long t2 = System.currentTimeMillis();
		System.out
				.println("Tiempo de consulta: " + (t2 - t1) + " milisegundos");
		// doSelection(toSelect, selectionLayer, action, false);
	}

	/**
	 * @see com.iver.cit.gvsig.gui.selectionByTheme.SelectionByThemeListener#addToSet(int,
	 *      int, int)
	 */
	public void addToSet(FLayer[] toSelect, FLayer selectionLayer, int action) {
		if (selectionLayer instanceof FLyrVect) {
			try {
				Selectable selected = ((FLyrVect) selectionLayer)
						.getRecordset();
				for (int i = 0; i < toSelect.length; i++) {
					if (toSelect[i] instanceof FLyrVect) {
						Selectable capa = ((FLyrVect) toSelect[i])
								.getRecordset();
						QueryByThemeVisitor visitor = new QueryByThemeVisitor(
								toSelect[i], selectionLayer, action);
						FBitSet selection = capa.getSelection();
						((VectorialData) selectionLayer).process(visitor,
								selected.getSelection());
						selection.or(visitor.getBitset());
						capa.setSelection(selection);
					}
				}
			} catch (ReadDriverException e) {
				NotificationManager.addError("Error leyendo las capas", e);
			} catch (ProcessVisitorException e) {
				NotificationManager.addError("Error leyendo las capas", e);
			} catch (VisitorException e) {
				NotificationManager.addError("Error leyendo las capas", e);
			}
		}
	}

	/**
	 * @see com.iver.cit.gvsig.gui.selectionByTheme.SelectionByThemeListener#fromSet(int,
	 *      int, int)
	 */
	public void fromSet(FLayer[] toSelect, FLayer selectionLayer, int action) {
		if (selectionLayer instanceof FLyrVect) {
			try {
				Selectable selected = ((FLyrVect) selectionLayer)
						.getRecordset();
				for (int i = 0; i < toSelect.length; i++) {
					if (toSelect[i] instanceof FLyrVect) {
						Selectable capa = ((FLyrVect) toSelect[i])
								.getRecordset();

						QueryByThemeVisitor visitor = new QueryByThemeVisitor(
								toSelect[i], selectionLayer, action);
						FBitSet selection = capa.getSelection();
						((VectorialData) selectionLayer).process(visitor,
								selected.getSelection());
						selection.and(visitor.getBitset());
						capa.setSelection(selection);
					}
				}
			} catch (ReadDriverException e) {
				NotificationManager.addError("Error leyendo las capas", e);
			} catch (ProcessVisitorException e) {
				NotificationManager.addError("Error leyendo las capas", e);
			} catch (VisitorException e) {
				NotificationManager.addError("Error leyendo las capas", e);
			}

		}
	}
}
