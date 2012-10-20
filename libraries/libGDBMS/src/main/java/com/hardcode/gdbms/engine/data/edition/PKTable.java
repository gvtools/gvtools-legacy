package com.hardcode.gdbms.engine.data.edition;

import java.util.ArrayList;

import com.hardcode.gdbms.engine.values.ValueCollection;

/**
 * Data structure to relate the primary keys of a table with the location of the
 * corresponding record during edition. This primary keys are accessed by index.
 * 
 * @author Fernando González Cortés
 */
public class PKTable {
	public final static int ORIGINAL = 0;
	public final static int MODIFIED = 1;
	public final static int ADDED = 2;
	private ArrayList pks = new ArrayList();
	private ArrayList editionInfo = new ArrayList();
	private ArrayList deleted = new ArrayList();

	/**
	 * Adds the 'pk' primary key to the 'pkIndex' index pointing to the
	 * 'actualIndex' location in the original table. Usually actualIndex and
	 * pkIndex will be the same number
	 * 
	 * @param pkIndex
	 *            index of the pk
	 * @param pk
	 *            primary key
	 * @param actualIndex
	 *            location of the PK at the original source
	 */
	public void addPK(int pkIndex, ValueCollection pk, int actualIndex) {
		pks.add(pkIndex, pk);
		editionInfo.add(pkIndex,
				new EditionInfo(ORIGINAL, actualIndex, pkIndex));
	}

	/**
	 * Deletes a pk
	 * 
	 * @param index
	 *            index of the PK to delete
	 */
	public void deletePK(int index) {
		DeletionInfo di = new DeletionInfo((ValueCollection) pks.remove(index),
				((EditionInfo) editionInfo.get(index)).getOriginalIndex());
		deleted.add(di);
		editionInfo.remove(index);
	}

	/**
	 * Returns the deteted pk count
	 * 
	 * @return int
	 */
	public int getDeletedPKCount() {
		return deleted.size();
	}

	/**
	 * returns the indexth deleted pk
	 * 
	 * @param index
	 *            index of the pk
	 * 
	 * @return Value
	 */
	public DeletionInfo getDeletedPK(int index) {
		return (DeletionInfo) deleted.get(index);
	}

	/**
	 * updates the position of the PK with the 'actualIndex'
	 * 
	 * @param index
	 *            index of the PK to be updated
	 * @param actualIndex
	 *            new position
	 */
	public void updatePK(int index, int actualIndex) {
		EditionInfo pv = (EditionInfo) editionInfo.get(index);
		pv.setFlag(MODIFIED);
		pv.setIndex(actualIndex);
	}

	/**
	 * Adds a pk to the end of the data structure
	 * 
	 * @param pk
	 *            pk to add
	 * @param actualIndex
	 *            location of the pk
	 */
	public void addPK(ValueCollection pk, int actualIndex) {
		pks.add(pk);
		editionInfo.add(new EditionInfo(ADDED, actualIndex, -1));
	}

	/**
	 * gets the indexth pk
	 * 
	 * @param index
	 *            index of the pk
	 * 
	 * @return Value
	 */
	public ValueCollection getPK(int index) {
		return (ValueCollection) pks.get(index);
	}

	/**
	 * Get's the pk count
	 * 
	 * @return int
	 */
	public int getPKCount() {
		return pks.size();
	}

	/**
	 * Returns the location of the indexth PK
	 * 
	 * @param index
	 *            index of the pk
	 * 
	 * @return FlagIndexPair
	 */
	public EditionInfo getIndexLocation(int index) {
		return (EditionInfo) editionInfo.get(index);
	}
}
