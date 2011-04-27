package com.hardcode.gdbms.engine.data.edition;

/**
 * Location info of a PK
 *
 * @author Fernando González Cortés
 */
public class EditionInfo {
    private int flag;
    private int index;
    private int originalIndex;

    /**
     * Creates a new FlagIndexPair.
     *
     * @param flag PKTable.ORIGINAL if the PK is stored at the original data
     *        source. PKTable.MODIFIED if the PK was stored at the original
     *        data source but has been modified and PKTable.ADDED if the PK
     *        has been added since the edition begining
     * @param index Index on the data source where the pk is
     * @param originalIndex PK index in the original table. -1 if the PK is not
     *        in the original table (added)
     */
    public EditionInfo(int flag, int index, int originalIndex) {
        this.flag = flag;
        this.index = index;
        this.originalIndex = originalIndex;
    }

    /**
     * gets the flag
     *
     * @return int
     */
    public int getFlag() {
        return flag;
    }

    /**
     * gets the index
     *
     * @return int
     */
    public int getIndex() {
        return index;
    }

    /**
     * sets the flag
     *
     * @param flag flag
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * sets the index
     *
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * gets the original index
     *
     * @return int
     */
    public int getOriginalIndex() {
        return originalIndex;
    }

    /**
     * sets the original index
     *
     * @param originalIndex index in the source table
     */
    public void setOriginalIndex(int originalIndex) {
        this.originalIndex = originalIndex;
    }
}
