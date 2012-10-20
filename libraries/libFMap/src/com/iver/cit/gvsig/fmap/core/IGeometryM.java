package com.iver.cit.gvsig.fmap.core;

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
/* CVS MESSAGES:
 *
 * $Id: IGeometryM.java,v 1.1 2007/10/19 10:03:45 jorpiell Exp $
 * $Log: IGeometryM.java,v $
 * Revision 1.1  2007/10/19 10:03:45  jorpiell
 * First commit
 *
 *
 */
/**
 * All the geometries that contains a M coordinate in their nodes have to
 * implement this interface. It has method to manage this coordinate.
 * 
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public interface IGeometryM extends IGeometry {

	/**
	 * @return if the M coordinates are decreasing
	 */
	public boolean isDecreasing();

	/**
	 * @return the M value for each node
	 */
	public double[] getMs();

	/**
	 * Sets the M value at a position
	 * 
	 * @param i
	 *            Position of the node
	 * @param value
	 *            The M value to set
	 */
	public void setMAt(int i, double value);

	/**
	 * Revert the Ms
	 */
	public void revertMs();

	/**
	 * Return the geometry like text. It is added because JTS doesn't support
	 * the M coordinate
	 * 
	 * @return
	 */
	public String toText();

}
