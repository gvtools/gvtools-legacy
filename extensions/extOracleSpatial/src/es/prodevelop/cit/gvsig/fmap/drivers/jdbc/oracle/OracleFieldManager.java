package es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.values.ValueWriter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.fieldmanagers.AbstractFieldManager;
import com.iver.cit.gvsig.fmap.edition.fieldmanagers.AddFieldCommand;
import com.iver.cit.gvsig.fmap.edition.fieldmanagers.FieldCommand;
import com.iver.cit.gvsig.fmap.edition.fieldmanagers.RemoveFieldCommand;
import com.iver.cit.gvsig.fmap.edition.fieldmanagers.RenameFieldCommand;

public class OracleFieldManager extends AbstractFieldManager {

	private static Logger logger = Logger.getLogger(OracleFieldManager.class
			.getName());
	private Connection conn;
	private String tableName;

	private String[] forbiddenNames;

	public OracleFieldManager(Connection c, String tn, String[] forbidden) {
		conn = c;
		tableName = tn;
		forbiddenNames = forbidden;
	}

	private boolean isOneOf(String str, String[] arr) {

		for (int i = 0; i < arr.length; i++) {
			if (str.compareToIgnoreCase(arr[i]) == 0)
				return true;
		}
		return false;

	}

	public void setOriginalFields(FieldDescription[] flds) {

		ArrayList aux = new ArrayList();
		for (int i = 0; i < flds.length; i++) {
			if (!isOneOf(flds[i].getFieldName(), forbiddenNames)) {
				aux.add(flds[i]);
			}
		}
		originalFields = (FieldDescription[]) aux
				.toArray(new FieldDescription[0]);
	}

	public boolean alterTable() throws WriteDriverException {

		String sql = "";
		Statement st;
		try {
			st = conn.createStatement();

			for (int i = 0; i < fieldCommands.size(); i++) {
				FieldCommand fc = (FieldCommand) fieldCommands.get(i);
				if (fc instanceof AddFieldCommand) {
					AddFieldCommand addFC = (AddFieldCommand) fc;

					// ALTER TABLE STAFF_OPTIONS ADD SO_INSURANCE_PROVIDER
					// Varchar2(35);
					sql = "ALTER TABLE "
							+ tableName
							+ " ADD "
							+ addFC.getFieldDesc().getFieldName()
							+ " "
							+ OracleSpatialDriver
									.fieldTypeToSqlStringType(addFC
											.getFieldDesc())
							+ " "
							+ "DEFAULT "
							+ addFC.getFieldDesc()
									.getDefaultValue()
									.getStringValue(
											ValueWriter.internalValueWriter)
							+ "";
					st.execute(sql);
				}
				if (fc instanceof RemoveFieldCommand) {
					RemoveFieldCommand deleteFC = (RemoveFieldCommand) fc;
					sql = "ALTER TABLE " + tableName + " DROP "
							+ deleteFC.getFieldName();
					st.execute(sql);
				}
				if (fc instanceof RenameFieldCommand) {
					RenameFieldCommand renFC = (RenameFieldCommand) fc;
					sql = "ALTER TABLE " + tableName + " RENAME COLUMN "
							+ renFC.getAntName() + " TO " + renFC.getNewName();
					st.execute(sql);
				}
				logger.debug("Alter Table: " + sql);
			}
			conn.commit();
		} catch (SQLException e) {
			logger.error("Alter Table: " + sql);
			e.printStackTrace();
			try {
				conn.rollback();
				conn.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			throw new WriteDriverException("JDBC", e);
		}

		return false;
	}

}
