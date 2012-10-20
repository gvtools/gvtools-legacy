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
package com.iver.cit.gvsig.fmap.edition;

import java.util.ArrayList;
import java.util.HashMap;

import com.iver.cit.gvsig.exceptions.expansionfile.CloseExpansionFileException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.expansionfile.OpenExpansionFileException;
import com.iver.cit.gvsig.fmap.core.IRow;

/**
 * Implementación en memoria de ExpansionFile.
 * 
 * @author Vicente Caballero Navarro
 */
public class MemoryExpansionFile implements ExpansionFile {
	ArrayList rows = new ArrayList();
	EditableAdapter edAdapter;

	private class InternalRow {
		private IRowEdited row;
		private int indexInternalFields;

		public InternalRow(IRowEdited row, int indexInternalFields) {
			this.row = row;
			this.indexInternalFields = indexInternalFields;
		}

		public int getIndexInternalFields() {
			return indexInternalFields;
		}

		public IRowEdited getRow() {
			return row;
		}
	}

	public MemoryExpansionFile(EditableAdapter edAdapter) {
		this.edAdapter = edAdapter;
	}

	// BitSet invalidRows = new BitSet();
	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#addRow(IRow, int)
	 */
	public int addRow(IRow row, int status, int indexInternalFields)
			throws ExpansionFileWriteException {
		int newIndex = rows.size();
		IRowEdited edRow = new DefaultRowEdited(row, status, newIndex);
		InternalRow iRow = new InternalRow(edRow, indexInternalFields);
		rows.add(iRow);

		return newIndex;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#modifyRow(int, IRow)
	 */
	// public int modifyRow(int index, IRow row, int indexInternalFields) throws
	// IOException {
	// /*if (invalidRows.get(index)) {
	// throw new RuntimeException(
	// "Se ha intentado modificar una geometría que ha sido borrada anteriormente");
	// }
	// */
	// //invalidateRow(index);
	// IRowEdited edRow = new DefaultRowEdited(row,
	// IRowEdited.STATUS_MODIFIED, index);
	//
	// InternalRow iRow = new InternalRow(edRow, indexInternalFields);
	// rows.add(iRow);
	//
	//
	// return rows.size() - 1;
	// }

	public int modifyRow(int index, IRow row, int indexInternalFields)
			throws ExpansionFileWriteException {
		/*
		 * if (invalidRows.get(index)) { throw new RuntimeException(
		 * "Se ha intentado modificar una geometría que ha sido borrada anteriormente"
		 * ); }
		 */
		// invalidateRow(index);
		InternalRow iOldRow = (InternalRow) rows.get(index);
		IRowEdited edRow = new DefaultRowEdited(row, iOldRow.getRow()
				.getStatus(), index);

		InternalRow iRow = new InternalRow(edRow, indexInternalFields);
		rows.add(iRow);

		return rows.size() - 1;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#getRow(int)
	 */
	public IRowEdited getRow(int index) throws ExpansionFileReadException {
		/*
		 * if (invalidRows.get(index)) { return null; }
		 */
		InternalRow iRow = (InternalRow) rows.get(index);
		int indexInternalFields = iRow.getIndexInternalFields();
		return edAdapter.createExternalRow(iRow.getRow(), indexInternalFields);
		// return iRow.getRow();
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#invalidateRow(int)
	 */
	/*
	 * public void invalidateRow(int index) { invalidRows.set(index, true); }
	 */
	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#compact()
	 */
	public void compact(HashMap relations) {
		/*
		 * ArrayList geoAux = new ArrayList(); Iterator iter =
		 * relations.keySet().iterator(); HashMap aux = new HashMap(); int n =
		 * 0;
		 * 
		 * while (iter.hasNext()) { Integer virtualIndex = (Integer)
		 * iter.next(); Integer expansionIndex = (Integer)
		 * relations.get(virtualIndex);
		 * 
		 * if (!invalidRows.get(expansionIndex.intValue())){
		 * geoAux.add(rows.get(expansionIndex.intValue())); aux.put(new
		 * Integer(n), new Integer(geoAux.size()-1)); n++; } }
		 * 
		 * invalidRows.clear(); rows = geoAux; relations.clear();
		 * relations.putAll(aux);
		 */

	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#getRowCount()
	 */
	/*
	 * public int getRowCount() { return rows.size() -
	 * invalidRows.cardinality(); }
	 */
	public void deleteLastRow() {
		// invalidRows.set(rows.size()-1,false);
		rows.remove(rows.size() - 1);

	}

	public void open() throws OpenExpansionFileException {
		// TODO Auto-generated method stub

	}

	public void close() throws CloseExpansionFileException {
		rows.clear();
		System.gc();
	}

	public int getSize() {
		return rows.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#validateRow(int)
	 */
	/*
	 * public void validateRow(int previousExpansionFileIndex) {
	 * invalidRows.set(previousExpansionFileIndex, false); }
	 * 
	 * public BitSet getInvalidRows() { return invalidRows; }
	 */
}

// [eiel-gestion-excepciones]