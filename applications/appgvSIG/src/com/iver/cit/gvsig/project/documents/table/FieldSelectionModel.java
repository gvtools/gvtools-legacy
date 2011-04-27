package com.iver.cit.gvsig.project.documents.table;

import java.util.ArrayList;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.utiles.swing.objectSelection.ObjectSelectionModel;
import com.iver.utiles.swing.objectSelection.SelectionException;


/**
 * DOCUMENT ME!
 *
 * @author Fernando González Cortés
 */
public class FieldSelectionModel implements ObjectSelectionModel {
	private DataSource ds;
	private String msg;
	private int type;

	/**
	 * Crea un nuevo FirstFieldSelectionModel.
	 *
	 * @param ds DOCUMENT ME!
	 * @param msg DOCUMENT ME!
	 * @param allowedTypes DOCUMENT ME!
	 */
	public FieldSelectionModel(DataSource ds, String msg,
		int type) {
		this.ds = ds;
		this.msg = msg;
		this.type = type;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws SelectionException
	 *
	 * @see com.iver.utiles.swing.objectSelection.ObjectSelectionModel#getObjects()
	 */
	public Object[] getObjects() throws SelectionException {
		try {
			ds.start();

			ArrayList fields = new ArrayList();
			int fieldCount=ds.getFieldCount();
			for (int i = 0; i < fieldCount; i++) {
				if (type != -1) {
                    System.out.println(ds.getFieldName(i) + " tipo: " + ds.getFieldType(i));
					if (ds.getFieldType(i) == type) {
						fields.add(ds.getFieldName(i));
					}
				} else {
					fields.add(ds.getFieldName(i));
				}
			}

			ds.stop();

			return (String[]) fields.toArray(new String[0]);
		} catch (ReadDriverException e) {
			throw new SelectionException(e);
		}
	}

	/**
	 * @see com.iver.utiles.swing.objectSelection.ObjectSelectionModel#getMsg()
	 */
	public String getMsg() {
		return msg;
	}
}
