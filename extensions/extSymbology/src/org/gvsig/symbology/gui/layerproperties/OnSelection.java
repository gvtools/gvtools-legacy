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
package org.gvsig.symbology.gui.layerproperties;

import org.gvsig.symbology.fmap.labeling.OnSelectionLabeled;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.DefaultLabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;

public class OnSelection extends DefaultLabeling{

	private static final long serialVersionUID = -3619706540747109386L;

	@Override
	public String getName() {
		return PluginServices.getText(this, "label_only_when_selected")+".";
	}

	@Override
	public Class<? extends ILabelingMethod> getLabelingMethodClass() {
		return OnSelectionLabeled.class;
	}

	@Override
	public ILabelingMethod getMethod() {
		OnSelectionLabeled myMethod = (OnSelectionLabeled) super.method;

		if(myMethod == null)
			myMethod = new OnSelectionLabeled();

		LabelClass lc = null;
		if (myMethod.getLabelClasses() != null && myMethod.getLabelClasses().length > 0) {
			lc = myMethod.getLabelClasses()[0];
		} else {
			lc = new LabelClass();
		}
		if (super.getMethod()!=null && super.getMethod().getLabelClasses()!=null)
			myMethod.addLabelClass(lc);
		return myMethod;
	}

	protected ILabelingMethod newMethodForThePreview(LabelClass defaultLabel) {
		OnSelectionLabeled method = new OnSelectionLabeled();
		method.addLabelClass(defaultLabel);
		return method;
	}
}
