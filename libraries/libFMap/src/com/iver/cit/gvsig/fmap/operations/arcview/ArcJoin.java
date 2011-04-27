package com.iver.cit.gvsig.fmap.operations.arcview;

import java.util.HashMap;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.customQuery.CustomQuery;
import com.hardcode.gdbms.engine.customQuery.QueryException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.instruction.Adapter;
import com.hardcode.gdbms.engine.instruction.Expression;
import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;
import com.hardcode.gdbms.engine.instruction.Utilities;
import com.hardcode.gdbms.engine.strategies.OperationDataSource;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.Value;

/**
 * @author Fernando González Cortés
 */
public class ArcJoin implements CustomQuery {

	/**
	 * @see com.hardcode.gdbms.engine.customQuery.CustomQuery#evaluate(com.hardcode.gdbms.engine.data.DataSource[], com.hardcode.gdbms.engine.instruction.Expression[])
	 */
	public OperationDataSource evaluate(DataSource[] tables, Expression[] values) throws QueryException {
		if (tables.length != 2) throw new QueryException("ArcJoin sólo opera con 2 tablas");
		if (values.length != 2) throw new QueryException("Se esperan dos expresiones de campo");

		//Se establece el origen de datos para las expresiones
		((Adapter) values[0]).getInstructionContext().setDs(tables[0]);
		((Adapter) values[0]).getInstructionContext().setFromTable(tables[0]);

		String fieldName0 = values[0].getFieldName();
		if  (fieldName0 == null) throw new QueryException("El valor debe ser una referencia a columna:" + Utilities.getText(((Adapter)values[0]).getEntity()));
		String fieldName1 = values[1].getFieldName();
		if  (fieldName1 == null) throw new QueryException("El valor debe ser una referencia a columna:" + Utilities.getText(((Adapter)values[1]).getEntity()));

		try {
			tables[0].start();
			tables[1].start();

			int[] result = new int[(int) tables[0].getRowCount()];

			int index0 = tables[0].getFieldIndexByName(fieldName0);
			if (index0 == -1)  throw new QueryException("No existe el campo: " + fieldName0);
			int index1 = tables[1].getFieldIndexByName(fieldName1);
			if (index1 == -1)  throw new QueryException("No existe el campo: " + fieldName1);

			//Construimos el índice
			HashMap idx = new HashMap(((int) tables[1].getRowCount())*2);
			for (int i = 0; i < tables[1].getRowCount(); i++) {
				Value v = tables[1].getFieldValue(i, index1);
				if (idx.get(v) == null)
				idx.put(v, new Integer(i));
			}

/*			Index idx = new DiskIndex(((int) tables[1].getRowCount())*2);
			idx.start();
			for (int i = 0; i < tables[1].getRowCount(); i++) {
				idx.add(tables[1].getFieldValue(i, index1), i);
			}
*/
			//Hacemos la query
			for (int i = 0; i < tables[0].getRowCount(); i++) {
				Value v = tables[0].getFieldValue(i, index0);
				Integer pi = (Integer) idx.get(v);
				if (pi == null){
					result[i] = -1;
				} else {
					try {
						if (((BooleanValue)v.equals(tables[1].getFieldValue(pi.intValue(), index1))).getValue()){
							result[i] = pi.intValue();
						}
					} catch (IncompatibleTypesException e1) {
						throw new QueryException("Los tipos de datos son incompatibles: " + tables[0].getFieldType(index0) + " - " + tables[1].getFieldType(index1), e1);
					}
				}
			}
/*
			for (int i = 0; i < tables[0].getRowCount(); i++) {
				Value v = tables[0].getFieldValue(i, index0);
				PositionIterator pi = idx.getPositions(v);
				boolean any = false;
				while (pi.hasNext()){
					int pos = pi.next();
					try {
						if (((BooleanValue)v.equals(tables[1].getFieldValue(pos, index1))).getValue()){
							result[i] = pos;
							any = true;
						}
					} catch (IncompatibleTypesException e1) {
						throw new QueryException("Los tipos de datos son incompatibles: " + tables[0].getFieldType(index0) + " - " + tables[1].getFieldType(index1), e1);
					}
				}
				if (!any) result[i] = -1;
			}
*/
			tables[0].stop();
			tables[1].stop();
//			idx.stop();

			return new ArcJoinDataSource(result, tables[0], tables[1], index1);
		} catch (ReadDriverException e) {
			throw new QueryException("Error accediendo a los datos", e);
		}
	}

	/**
	 * @see com.hardcode.gdbms.engine.customQuery.CustomQuery#getName()
	 */
	public String getName() {
		return "com_iver_cit_gvsig_arcjoin";
	}

}
