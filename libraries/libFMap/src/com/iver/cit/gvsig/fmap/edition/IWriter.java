package com.iver.cit.gvsig.fmap.edition;

import java.util.Properties;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;

public interface IWriter extends Driver {
	void preProcess() throws StartWriterVisitorException;

	/**
	 * Aquí dentro se puede hacer el chequeo de las reglas asignadas para que
	 * antes de guardar algo, se compruebe que es correcto. Aunque lo ideal
	 * sería haberlo hecho antes, para que lo que se guarde esté corregido al
	 * máximo.
	 * 
	 * @throws ProcessWriterVisitorException
	 *             TODO
	 * @throws VisitorException
	 */
	void process(IRowEdited row) throws VisitorException;

	void postProcess() throws StopWriterVisitorException;

	/**
	 * A developer can use this Properties for his own purposes. For example, to
	 * let his extension know something about one writer. <br>
	 * We can found for example: <br>
	 * <b>FieldNameMaxLength<b>: Maximum length of field name (Null or 0 -->
	 * unlimited).
	 * 
	 * @param capability
	 * @return A message describing the capability. Null if not supported.
	 */
	public String getCapability(String capability);

	/**
	 * @param capabilities
	 *            The capabilities to set.
	 */
	public void setCapabilities(Properties capabilities);

	public abstract boolean canWriteAttribute(int sqlType);

	public abstract boolean canAlterTable();

	public abstract boolean canSaveEdits() throws VisitorException;

	public void initialize(ITableDefinition tableDefinition)
			throws InitializeWriterException;

	public ITableDefinition getTableDefinition() throws ReadDriverException;

	/**
	 * @return true if the driver needs to iterate trhu all records. for
	 *         example, shp files need return true. jdbc writers will return
	 *         false
	 */
	public boolean isWriteAll();

}
