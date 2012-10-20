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
 *   Generalitat Valenciana
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
package es.prodevelop.cit.gvsig.arcims.fmap.drivers;

import java.awt.geom.Rectangle2D;

import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.VectorialAdapter;

public class ArcImsVectorialAdapter extends VectorialAdapter {
	private IFeature[] testArray;

	public ArcImsVectorialAdapter(FMapFeatureArcImsDriver drv) {
		setDriver(drv);
	}

	public IGeometry getShape(int index) throws ReadDriverException {
		return getDriver().getShape(index);
	}

	public int getShapeCount() throws ReadDriverException {
		return getDriver().getShapeCount();
	}

	public Rectangle2D getFullExtent() throws ReadDriverException {
		return getDriver().getFullExtent();
	}

	public int getShapeType() throws ReadDriverException {
		return getDriver().getShapeType();
	}

	/**
	 * This method is used when the data source is a proper database that needs
	 * initialization.
	 */
	public void start() throws ReadDriverException {
		((FMapFeatureArcImsDriver) getDriver()).getRecordSet().start();
	}

	/**
	 * This method is used when the data source is a proper database that needs
	 * to be closed after use.
	 */
	public void stop() throws ReadDriverException {
		((FMapFeatureArcImsDriver) driver).getRecordSet().stop();
	}

	public SelectableDataSource getRecordset() throws ReadDriverException {
		return ((FMapFeatureArcImsDriver) driver).getRecordSet();
	}

	public void requestFeatureAttributes(FBitSet fbs) throws ArcImsException {
		((FMapFeatureArcImsDriver) driver).requestFeatureAttributes(fbs);
	}
}
