/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
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
 *   Av. Blasco Ib��ez, 50
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

package com.iver.cit.gvsig.project.documents.view.tool;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.BitSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.edition.Annotation_EditableAdapter;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.layers.Annotation_Layer;
import com.iver.cit.gvsig.fmap.layers.Annotation_Mapping;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.operation.strategies.Annotation_Strategy;
import com.iver.cit.gvsig.fmap.tools.BehaviorException;
import com.iver.cit.gvsig.fmap.tools.Events.PointEvent;
import com.iver.cit.gvsig.fmap.tools.Listeners.PointListener;
import com.iver.cit.gvsig.project.documents.view.tool.gui.Annotation_ModifyWindow;

/**
 * Implementation of the interface PointListener as tool of modify annotation.
 * 
 * @author Vicente Caballero Navarro
 */
public class Annotation_ModifyToolListenerImpl implements PointListener {
	private static final int tolerance = 10;
	private static final Image imodifytool = PluginServices.getIconTheme()
			.get("annotation-modify").getImage();
	private Cursor cur = Toolkit.getDefaultToolkit().createCustomCursor(
			imodifytool, new Point(16, 16), "");
	private MapControl mapControl;

	/**
	 * Create a new Annotation_ModifyToolListenerImpl.
	 * 
	 * @param mapControl
	 *            MapControl.
	 */
	public Annotation_ModifyToolListenerImpl(MapControl mapControl) {
		this.mapControl = mapControl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#point(com.iver.
	 * cit.gvsig.fmap.tools.Events.PointEvent)
	 */
	public void point(PointEvent event) throws BehaviorException {
		FLayer[] layers = mapControl.getMapContext().getLayers().getActives();
		if (layers[0] instanceof Annotation_Layer) {
			if (layers[0].isEditing())
				try {
					Annotation_Mapping am = ((Annotation_Layer) layers[0])
							.getAnnotatonMapping();
					Annotation_Strategy as = (Annotation_Strategy) ((Annotation_Layer) layers[0])
							.getStrategy();
					BitSet bs = as.queryByPoint(mapControl.getViewPort()
							.toMapPoint(event.getPoint()), mapControl
							.getViewPort().toMapDistance(tolerance));
					Annotation_EditableAdapter aea = (Annotation_EditableAdapter) ((Annotation_Layer) layers[0])
							.getSource();
					if (bs.cardinality() > 0)
						aea.startComplexRow();
					for (int i = bs.nextSetBit(0); i >= 0; i = bs
							.nextSetBit(i + 1)) {
						IRow row = aea.getRow(i).getLinkedRow().cloneRow();
						Value[] values = row.getAttributes();
						Annotation_ModifyWindow maw = new Annotation_ModifyWindow();
						maw.setValues(values, am);
						PluginServices.getMDIManager().addWindow(maw);
						if (maw.isAccepted()) {
							Value[] newValues = maw.getValues();
							if (!(values.equals(newValues))) {
								row.setAttributes(newValues);
								aea.modifyRow(i, row, PluginServices.getText(
										this, "modify_annotation"),
										EditionEvent.ALPHANUMERIC);
							}
						}
					}
					if (bs.cardinality() > 0) {
						aea.endComplexRow(PluginServices.getText(this,
								"modify_annotation"));
						mapControl.drawMap(false);
					}
				} catch (ExpansionFileReadException e) {
					throw new BehaviorException(e.getMessage());
				} catch (ReadDriverException e) {
					throw new BehaviorException(e.getMessage());
				} catch (ValidateRowException e) {
					throw new BehaviorException(e.getMessage());
				} catch (VisitorException e) {
					throw new BehaviorException(e.getMessage());
				}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#getCursor()
	 */
	public Cursor getCursor() {
		return cur;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.tools.Listeners.ToolListener#cancelDrawing()
	 */
	public boolean cancelDrawing() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.tools.Listeners.PointListener#pointDoubleClick
	 * (com.iver.cit.gvsig.fmap.tools.Events.PointEvent)
	 */
	public void pointDoubleClick(PointEvent event) {
	}
}
