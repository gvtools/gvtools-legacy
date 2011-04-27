package org.gvsig.fmap.geometries.operation.println;
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
 * $Id: PrintlnControler.java,v 1.1 2008/03/12 08:46:21 cvs Exp $
 * $Log: PrintlnControler.java,v $
 * Revision 1.1  2008/03/12 08:46:21  cvs
 * *** empty log message ***
 *
 *
 */
/**
 * @author Jorge Piera Llodrá (jorge.piera@iver.es)
 */
public class PrintlnControler {
	public boolean hasChildren = false;
	public int childrenNumber = 0;
	public int childrenVisited = 0;
	public String tab = "";

	/**
	 * Initialize the geometries level to 0
	 */
	public void setLevel0(){
		hasChildren = false;
		tab = "";
		childrenNumber = 0;
		childrenVisited = 0;
	}

	/**
	 * @return the hasChildren
	 */
	public boolean isHasChildren() {
		return hasChildren;
	}
	/**
	 * @param hasChildren the hasChildren to set
	 */
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	/**
	 * @return the childrenNumber
	 */
	public int getChildrenNumber() {
		return childrenNumber;
	}
	/**
	 * @param childrenNumber the childrenNumber to set
	 */
	public void setChildrenNumber(int childrenNumber) {
		this.childrenNumber = childrenNumber;
	}
	/**
	 * @return the childrenVisited
	 */
	public int getChildrenVisited() {
		return childrenVisited;
	}
	/**
	 * @param childrenVisited the childrenVisited to set
	 */
	public void setChildrenVisited(int childrenVisited) {
		this.childrenVisited = childrenVisited;
	}
	/**
	 * @return the tab
	 */
	public String getTab() {
		return tab;
	}
	/**
	 * @param tab the tab to set
	 */
	public void setTab(String tab) {
		this.tab = tab;
	}	
}
