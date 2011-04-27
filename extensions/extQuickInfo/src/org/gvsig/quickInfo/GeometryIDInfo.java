package org.gvsig.quickInfo;

/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/**
 *
 *
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public class GeometryIDInfo { // implements Comparable, Comparator {
	private String id;
	private boolean hasInfo;

	public GeometryIDInfo(String id) {
		this.id = id;
		hasInfo = true; // by default has info
	}
	
	public GeometryIDInfo(String id, boolean hasInfo) {
		this.id = id;
		this.hasInfo = hasInfo;
	}
	
	public String getID() {
		return id;
	}
	
	public boolean hasInfo() {
		return hasInfo;
	}
	
	public void setHasInfo(boolean b) {
		hasInfo = b;
	}
//
//	public int compareTo(Object arg0) {
//		return id.compareTo((String)arg0);
//	}
//
//	public int compare(Object arg0, Object arg1) {
//		return ((String)arg0.toString()).compareTo((String)arg1.toString());
//	}

	public String toString() {
		return id;
	}
	
	public boolean equals(GeometryIDInfo obj) {
		return id.compareTo(obj.toString()) == 0;
	}
}
