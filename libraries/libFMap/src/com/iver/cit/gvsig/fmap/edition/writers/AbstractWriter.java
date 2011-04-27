package com.iver.cit.gvsig.fmap.edition.writers;

import java.util.Properties;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;

public abstract class AbstractWriter implements IWriter {
	protected Properties capabilities = new Properties();
	protected ITableDefinition tableDef;
	protected boolean bWriteAll;

	/**
	 * A developer can use this Properties for his own purposes. For example, to
	 * let his extension know something about one writer.
	 * @param capability
	 * @return A message describing the capability. Null if not supported.
	 */
	public String getCapability(String capability)
	{
		if (capabilities.containsKey(capability))
			return capabilities.getProperty(capability);
		return null;
	}

	/**
	 * @param capabilities The capabilities to set.
	 */
	public void setCapabilities(Properties capabilities) {
		this.capabilities = capabilities;
	}

	// public abstract boolean canWriteGeometry(int gvSIGgeometryType);
	public abstract boolean canWriteAttribute(int sqlType);


	public void initialize(ITableDefinition tableDefinition) throws InitializeWriterException{
		this.tableDef = tableDefinition;

	}

	public ITableDefinition getTableDefinition() {
		return tableDef;
	}

	/**
	 * @return Returns the bWriteAll.
	 */
	public boolean isWriteAll() {
		return bWriteAll;
	}

	/**
	 * @param writeAll
	 *            The bWriteAll to set.
	 */
	public void setWriteAll(boolean writeAll) {
		bWriteAll = writeAll;
	}


}
