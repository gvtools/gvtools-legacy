package com.iver.cit.gvsig.fmap.core;

import com.hardcode.gdbms.engine.values.Value;

/**
 * @author fjp
 * 
 *         Simple Row. IFeature extends it. Useful for edition, for example. You
 *         can have a EditionAdapter backed by an ExpansionFile and work always
 *         with IRow.
 */
public interface IRow {

	public String getID();

	public Value getAttribute(int fieldIndex);

	public Value[] getAttributes();

	public void setAttributes(Value[] att);

	public IRow cloneRow();

	public void setID(String ID);

}