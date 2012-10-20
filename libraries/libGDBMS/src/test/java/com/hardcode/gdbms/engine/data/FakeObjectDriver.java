package com.hardcode.gdbms.engine.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;

/**
 *
 */
public class FakeObjectDriver implements ObjectDriver {

	private Value[][] values = new Value[3][7];
	private String[] names = new String[] { "id", "nombre", "apellido",
			"fecha", "tiempo", "marcatiempo", "PK" };

	public FakeObjectDriver() throws ParseException {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

		values[0][0] = ValueFactory.createValue(0);
		values[0][1] = ValueFactory.createValue("fernando");
		values[0][2] = ValueFactory.createValue("gonzalez");
		values[0][3] = ValueFactory.createValue(df.parse("1980-9-5"));
		values[0][4] = ValueFactory.createValue(Time.valueOf("10:30:00"));
		values[0][5] = ValueFactory.createValue(Timestamp
				.valueOf("1980-9-5 10:30:00.666666666"));
		values[0][6] = ValueFactory.createValue(0);

		values[1][0] = ValueFactory.createValue(1);
		values[1][1] = ValueFactory.createValue("huracán");
		values[1][2] = ValueFactory.createValue("gonsales");
		values[1][3] = ValueFactory.createValue(df.parse("1980-9-5"));
		values[1][4] = ValueFactory.createValue(Time.valueOf("10:30:00"));
		values[1][5] = ValueFactory.createValue(Timestamp
				.valueOf("1980-9-5 10:30:00.666666666"));
		values[1][6] = ValueFactory.createValue(1);

		values[2][0] = ValueFactory.createValue(2);
		values[2][1] = ValueFactory.createValue("fernan");
		values[2][2] = null;
		values[2][3] = ValueFactory.createValue(df.parse("1980-9-5"));
		values[2][4] = ValueFactory.createValue(Time.valueOf("10:30:00"));
		values[2][5] = ValueFactory.createValue(Timestamp
				.valueOf("1980-9-5 10:30:00.666666666"));
		values[2][6] = ValueFactory.createValue(2);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getPrimaryKeys()
	 */
	public int[] getPrimaryKeys() throws ReadDriverException {
		return new int[] { 6 };
	}

	/**
	 * @throws ReadDriverException
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#write(com.hardcode.gdbms.engine.data.edition.DataWare)
	 */
	public void write(DataWare dataWare) throws WriteDriverException,
			ReadDriverException {
		Value[][] newValues = new Value[(int) dataWare.getRowCount()][dataWare
				.getFieldCount()];
		for (int i = 0; i < dataWare.getRowCount(); i++) {
			for (int j = 0; j < dataWare.getFieldCount(); j++) {
				newValues[i][j] = dataWare.getFieldValue(i, j);
			}
		}

		values = newValues;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldValue(long,
	 *      int)
	 */
	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		return values[(int) rowIndex][fieldId];
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldCount()
	 */
	public int getFieldCount() throws ReadDriverException {
		return names.length;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldName(int)
	 */
	public String getFieldName(int fieldId) throws ReadDriverException {
		return names[fieldId];
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws ReadDriverException {
		return values.length;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ReadAccess#getFieldType(int)
	 */
	public int getFieldType(int i) throws ReadDriverException {
		switch (i) {
		case 0:
			return Types.INTEGER;
		case 1:
		case 2:
			return Types.VARCHAR;
		case 3:
			return Types.DATE;
		case 4:
			return Types.TIME;
		case 5:
			return Types.TIMESTAMP;
		case 6:
			return Types.BIGINT;
		}

		throw new RuntimeException();
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getName() {
		return null;
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		// TODO Auto-generated method stub
		return values[0][i].getWidth();
	}

	public void reload() throws ReloadDriverException {
		// TODO Auto-generated method stub

	}

}
