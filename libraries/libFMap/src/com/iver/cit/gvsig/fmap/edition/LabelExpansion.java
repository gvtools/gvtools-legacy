package com.iver.cit.gvsig.fmap.edition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;

/**
 * Implementación en memoria de LabelExpansion.
 *
 * @author Vicente Caballero Navarro
 */
public class LabelExpansion {
	private ArrayList labels = new ArrayList();
	//BitSet invalidRows = new BitSet();
	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#addRow(IRow, int)
	 */
	public int addLabel(FLabel label){
		int newIndex = labels.size();
		labels.add(label);

		return newIndex;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#modifyRow(int,
	 * 		IRow)
	 */
	public int modifyLabel(int index, FLabel label){
		/*if (invalidRows.get(index)) {
			throw new RuntimeException(
				"Se ha intentado modificar una geometría que ha sido borrada anteriormente");
		}
*/
		//invalidateRow(index);
		labels.add(label);

		return labels.size() - 1;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#getRow(int)
	 */
	public FLabel getLabel(int index) {
		/*if (invalidRows.get(index)) {
			return null;
		}
*/
		return (FLabel) labels.get(index);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#invalidateRow(int)
	 */
	/*public void invalidateRow(int index) {
		invalidRows.set(index, true);
	}
*/
	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#compact()
	 */
	public void compact(HashMap relations) {
	/*	ArrayList geoAux = new ArrayList();
		Iterator iter = relations.keySet().iterator();
		HashMap aux = new HashMap();
		int n = 0;

		while (iter.hasNext()) {
			Integer virtualIndex = (Integer) iter.next();
			Integer expansionIndex = (Integer) relations.get(virtualIndex);

			if (!invalidRows.get(expansionIndex.intValue())){
				geoAux.add(rows.get(expansionIndex.intValue()));
				aux.put(new Integer(n), new Integer(geoAux.size()-1));
				n++;
			}
		}

		invalidRows.clear();
		rows = geoAux;
		relations.clear();
		relations.putAll(aux);
*/

	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#getRowCount()
	 */
	/*public int getRowCount() {
		return rows.size() - invalidRows.cardinality();
	}
*/
	public void deleteLastLabel() {
		//invalidRows.set(rows.size()-1,false);
		labels.remove(labels.size()-1);

	}

	public void open() throws IOException {
		// TODO Auto-generated method stub

	}

	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	public int getSize() {
		return labels.size();
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.ExpansionFile#validateRow(int)
	 */
	/*public void validateRow(int previousExpansionFileIndex) {
		invalidRows.set(previousExpansionFileIndex, false);
	}

	public BitSet getInvalidRows() {
		return invalidRows;
	}*/
}
