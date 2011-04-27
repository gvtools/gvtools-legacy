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
package com.iver.cit.gvsig.fmap.core.rendering.styling.labeling;

import junit.framework.TestCase;

import com.iver.cit.gvsig.fmap.core.rendering.TestILegend;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingMethod;

public abstract /* <- MUST BE ABSTRACT ! */ class AbstractLabelingMethodTestCase extends TestCase{
	protected Class<? extends ILabelingMethod> methodClazz;
	
	public AbstractLabelingMethodTestCase(Class<? extends ILabelingMethod> methodClazz) {
		this.methodClazz = methodClazz;
	}
	
	public ILabelingMethod newInstance() {
		try {
			ILabelingMethod method =  (ILabelingMethod) methodClazz.newInstance();
			initMethod(method);
			return method;
		} catch (InstantiationException ex) {
			// TODO Auto-generated catch block
			fail("Instantiating class, cannot test a non-instantiable method '"+ TestILegend.shortClassName(methodClazz)+"'");
		} catch (IllegalAccessException ex) {
			// TODO Auto-generated catch block
			fail("Class not instantiable '"+ TestILegend.shortClassName(methodClazz)+"'");
		} catch (ClassCastException ccEx) {
			fail("Cannot test a non legend class '"+ TestILegend.shortClassName(methodClazz)+"'");
		}			
		return null;
	}
	
	public abstract void initMethod(ILabelingMethod method);
}
