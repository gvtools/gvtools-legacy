package org.gvsig.remoteClient.gml.exceptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

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
 * $Id: GMLExceptionList.java 18271 2008-01-24 09:06:43Z jpiera $
 * $Log$
 * Revision 1.1  2007-01-15 13:11:00  csanchez
 * Sistema de Warnings y Excepciones adaptado a BasicException
 *
 *
 */
/**
 * @author Carlos Sánchez Periñán (sanchez_carper@gva.es)
 */
//***********************************
// PARA WARNINGS YA VEREMOS SI SE USA
//***********************************
public class GMLExceptionList extends GMLException{
	private static final long serialVersionUID = -2229510058574520019L;

private List exceptions = new ArrayList();
	
	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	public int size() {
		return this.exceptions.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		return this.exceptions.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	public boolean contains(Object arg0) {
		return this.exceptions.contains(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		return this.exceptions.toArray();
	}

	/**
	 * @param arg0
	 * @return
	 */
	public Object[] toArray(Object[] arg0) {
		return this.exceptions.toArray(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean add(Object arg0) {
		return this.exceptions.add(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	public boolean remove(Object arg0) {
		return this.exceptions.remove(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean containsAll(Collection arg0) {
		return this.exceptions.contains(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean addAll(Collection arg0) {
		return this.exceptions.addAll(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public boolean addAll(int arg0, Collection arg1) {
		return this.exceptions.addAll(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean removeAll(Collection arg0) {
		return this.exceptions.removeAll(arg0);
	}

	/**
	 * @param arg0
	 * @return
	 */
	public boolean retainAll(Collection arg0) {
		return this.exceptions.retainAll(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		this.exceptions.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.List#get(int)
	 */
	public Object get(int arg0) {
		return this.exceptions.get(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @return
	 */
	public Object set(int arg0, Object arg1) {
		return this.exceptions.set(arg0, arg1);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public void add(int arg0, Object arg1) {
		this.exceptions.add(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int arg0) {
		return this.exceptions.remove(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.List#indexOf(java.lang.Object)
	 */
	public int indexOf(Object arg0) {
		return this.exceptions.indexOf(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.List#lastIndexOf(java.lang.Object)
	 */
	public int lastIndexOf(Object arg0) {
		return this.exceptions.lastIndexOf(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator()
	 */
	public ListIterator listIterator() {
		return this.exceptions.listIterator();
	}

	/* (non-Javadoc)
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator listIterator(int arg0) {
		return this.exceptions.listIterator(arg0);
	}

	/* (non-Javadoc)
	 * @see java.util.List#subList(int, int)
	 */
	public List subList(int arg0, int arg1) {
		return this.exceptions.subList(arg0, arg1);
	}

}
