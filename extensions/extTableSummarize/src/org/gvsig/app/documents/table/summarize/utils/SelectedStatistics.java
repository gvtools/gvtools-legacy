/* gvSIG. Sistema de Informacion Geografica de la Generalitat Valenciana
 *
 * Copyright (C) 2009 IVER T.I.
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
 *  IVER T.I.
 *   C/ Lerida, 20
 *   46009 Valencia
 *   SPAIN
 *   http://www.iver.es
 *   dac@iver.es
 *   +34 963163400
 *   
 *  or
 *  
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibañez, 50
 *   46010 VALENCIA
 *   SPAIN
 */
package org.gvsig.app.documents.table.summarize.utils;

/**
 * @author IVER T.I. <http://www.iver.es> 01/02/2009
 */
public class SelectedStatistics {
	
	private String columnName;
	private int columnNumber;
	/**
	 * As defined in java.sql.Types
	 */
	private int columnType;
	private boolean min;
	private boolean max;
	private boolean mean;
	private boolean sum;
	private boolean sd;
	private boolean var;
	
	

	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public int getColumnNumber() {
		return columnNumber;
	}
	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}
	public boolean isMin() {
		return min;
	}
	public void setMin(boolean min) {
		this.min = min;
	}
	public boolean isMax() {
		return max;
	}
	public void setMax(boolean max) {
		this.max = max;
	}
	public boolean isMean() {
		return mean;
	}
	public void setMean(boolean mean) {
		this.mean = mean;
	}
	public boolean isSum() {
		return sum;
	}
	public void setSum(boolean sum) {
		this.sum = sum;
	}
	public boolean isSd() {
		return sd;
	}
	public void setSd(boolean sd) {
		this.sd = sd;
	}
	public boolean isVar() {
		return var;
	}
	public void setVar(boolean var) {
		this.var = var;
	}
	public void setColumnType(int columnType) {
		this.columnType = columnType;
	}
	public int getColumnType() {
		return columnType;
	}
}
