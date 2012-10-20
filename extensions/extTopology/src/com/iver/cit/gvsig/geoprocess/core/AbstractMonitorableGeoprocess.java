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
package com.iver.cit.gvsig.geoprocess.core;

import com.iver.cit.gvsig.geoprocess.GenericGeoprocessTask;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.utiles.swing.threads.CancellableProgressTask;
import com.iver.utiles.swing.threads.IMonitorableTask;

/**
 * Abstract geoprocess that is monitorable.
 * 
 * This abstract geoprocess allows to monitor the advance of a geoprocess
 * without creating a task by each geoprocess (instead we are going to use the
 * same task, CancellableProgressTask)
 * 
 * 
 * @author Alvaro Zabala
 * 
 */
public abstract class AbstractMonitorableGeoprocess extends AbstractGeoprocess {

	/**
	 * the geoprocess will operate only with selected elements of layer, or with
	 * all elements.<br>
	 * By default, it work with all elements.
	 */
	protected boolean operateOnlyWithSelection = false;

	/**
	 * Executes the geoprocess passing monitorization or cancelation messages to
	 * the specified progress monitor instance.
	 * 
	 * @param progressMonitor
	 */
	public void process() throws GeoprocessException {
		try {
			createTask().run();
		} catch (Exception e) {
			throw new GeoprocessException("Error al ejecutar la tarea", e);
		}
	}

	/**
	 * Creates a task to execute this geoprocess in background
	 */
	public IMonitorableTask createTask() {
		return new GenericGeoprocessTask(this);
	}

	/**
	 * Executes the geoprocess passing monitorization or cancelation messages to
	 * the specified progress monitor instance.
	 * 
	 * @param progressMonitor
	 */
	public abstract void process(CancellableProgressTask progressMonitor)
			throws GeoprocessException;

	/**
	 * Initialize reporting params of progressMonitor with geoprocess
	 * information
	 * 
	 * @param progressMonitor
	 */
	public abstract void initialize(CancellableProgressTask progressMonitor)
			throws GeoprocessException;

}
