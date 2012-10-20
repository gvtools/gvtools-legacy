package com.hardcode.gdbms.engine.data.edition;

import com.hardcode.gdbms.engine.values.ValueCollection;

/**
 * Information about a deletion action. It has information about the deleted pk
 * and the index of the matching row in the source table
 * 
 * @author Fernando González Cortés
 */
public class DeletionInfo {
	private ValueCollection pk;
	private int originalIndex;

	/**
	 * Crea un nuevo DeletionInfo.
	 * 
	 * @param pk
	 *            Deleted primary key
	 * @param originalIndex
	 *            Index in the source table
	 */
	public DeletionInfo(ValueCollection pk, int originalIndex) {
		this.pk = pk;
		this.originalIndex = originalIndex;
	}

	/**
	 * Gets the index in the source table. -1 if the primary key is from a new
	 * created record.
	 * 
	 * @return int
	 */
	public int getOriginalIndex() {
		return originalIndex;
	}

	/**
	 * Sets the index in the source table
	 * 
	 * @param originalIndex
	 *            index in the source table
	 */
	public void setOriginalIndex(int originalIndex) {
		this.originalIndex = originalIndex;
	}

	/**
	 * Gets the deleted pk.
	 * 
	 * @return The PK
	 */
	public ValueCollection getPk() {
		return pk;
	}

	/**
	 * Sets the deleted PK
	 * 
	 * @param pk
	 *            deleted PK
	 */
	public void setPk(ValueCollection pk) {
		this.pk = pk;
	}
}
