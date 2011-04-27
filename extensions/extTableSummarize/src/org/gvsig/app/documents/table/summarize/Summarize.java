/* gvSIG. Sistema de Informacion Geografica de la Generalitat Valenciana
 *
 * Copyright (C) 2009 IVER T.I.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  IVER T.I.
 *   C/ Lerida, 20
 *   46009 Valencia
 *   SPAIN
 *   http://www.iver.es
 *   dac@iver.es
 *   +34 963163400
 *   
 *  or
 *  
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibañez, 50
 *   46010 VALENCIA
 *   SPAIN
 */
package org.gvsig.app.documents.table.summarize;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.gvsig.app.documents.table.summarize.exceptions.DBFExportException;
import org.gvsig.app.documents.table.summarize.exceptions.GroupByFieldNotExistsException;
import org.gvsig.app.documents.table.summarize.exceptions.GroupingErrorException;
import org.gvsig.app.documents.table.summarize.exceptions.InitializationException;
import org.gvsig.app.documents.table.summarize.exceptions.SummarizeException;
import org.gvsig.app.documents.table.summarize.utils.IntList;
import org.gvsig.app.documents.table.summarize.utils.SelectedStatistics;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.FileDriver;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.hardcode.gdbms.parser.ParseException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IWriteable;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.selection.SelectionFieldIterator;
import com.iver.cit.gvsig.project.documents.table.Statistics.NonNumericFieldException;

/**
 * Summarizes a table. 
 * 
 * @author IVER T.I. <http://www.iver.es> 01/02/2009
 */
public class Summarize {
	private SelectableDataSource sds;
	private static Logger logger = Logger.getLogger(Summarize.class.getName());
	private ArrayList<SelectedStatistics> operations;
	private String groupByField;
	
	public Summarize() {
		
	}
	
	public Summarize(SelectableDataSource ds) {
		this.sds = ds;
	}

	public void summarizeToWriter(IWriter writer)
	throws InitializationException, GroupByFieldNotExistsException, SummarizeException, DBFExportException, GroupingErrorException {
		if (operations==null) {
			throw new InitializationException("Operation not initialized");
		}
		if (sds==null) {
			throw new InitializationException("Data source not initialized");
		}
		if (groupByField==null) {
			throw new InitializationException("GroupByField not initialized");
		}
		ArrayList<String> outputFieldNames = new ArrayList<String>();
		IntList outputTypeList = new IntList();
		try {
			createFieldsAndTypes(operations, outputFieldNames, outputTypeList);
			summarizeToWriter(writer, outputFieldNames.toArray(new String[0]), outputTypeList.toArray());
		} catch (ReadDriverException e) {
			throw new SummarizeException(e);
		}
	}


	public void summarizeToDbf(File dbfFile) 
		throws InitializationException, GroupByFieldNotExistsException, SummarizeException, DBFExportException, GroupingErrorException {
		if (operations==null) {
			throw new InitializationException("Operation not initialized");
		}
		if (sds==null) {
			throw new InitializationException("Data source not initialized");
		}
		if (groupByField==null) {
			throw new InitializationException("GroupByField not initialized");
		}
		
		ArrayList<String> outputFieldNames = new ArrayList<String>();
		IntList outputTypeList = new IntList();
		try {
			createFieldsAndTypes(operations, outputFieldNames, outputTypeList);
			IWriter writer = createDbfFile(dbfFile, outputFieldNames.toArray(new String[0]), outputTypeList.toArray());
			summarizeToWriter(writer, outputFieldNames.toArray(new String[0]), outputTypeList.toArray());
		} catch (ReadDriverException e) {
			throw new SummarizeException(e);
		}
	}
	
	protected void summarizeToWriter(IWriter writer, String[] outputFieldNames, int[] fieldTypes)
	throws SummarizeException, DBFExportException, 
	GroupingErrorException{
		try {
			initWriter(writer);
			calculateStats(getOperations(), groupByField, outputFieldNames, writer);
			finishWriter(writer);
		} catch (NonNumericFieldException e) {
			SummarizeException ex = new SummarizeException(e);
			throw ex;
		}
	}


	protected void createFieldsAndTypes(ArrayList<SelectedStatistics> operations,
			ArrayList<String> outputFieldNames,
			IntList fieldTypes) throws ReadDriverException, GroupByFieldNotExistsException, SummarizeException {
		
		//fixed columns
		int groupByFieldIdx;
		
		groupByFieldIdx = sds.getFieldIndexByName(groupByField);
		
		if (groupByFieldIdx==-1) {
			throw new GroupByFieldNotExistsException();
		}
		
		int maxlength = 10;
		
		String groupByName = createUniqueFieldName(outputFieldNames, "", PluginServices.getText(this, "GROUP"), maxlength);
		outputFieldNames.add(groupByName);
		fieldTypes.add(sds.getFieldType(groupByFieldIdx));
		
		String countName = createUniqueFieldName(outputFieldNames, "", PluginServices.getText(this, "COUNT"), maxlength);
		outputFieldNames.add(countName);
		fieldTypes.add(Types.INTEGER);
	
		for(int i=0; i<operations.size(); i++){				
			String currentFieldName = operations.get(i).getColumnName();
			int currFieldType = operations.get(i).getColumnType();
			
			if(operations.get(i).isMin()){
				String newField = createUniqueFieldName(outputFieldNames, currentFieldName, "_"+PluginServices.getText(this, "MIN"), maxlength);
				outputFieldNames.add(newField);
				fieldTypes.add(currFieldType);
			}
			if(operations.get(i).isMax()){
				String newField = createUniqueFieldName(outputFieldNames, currentFieldName, "_"+PluginServices.getText(this, "MAX"), maxlength);
				outputFieldNames.add(newField);
				fieldTypes.add(currFieldType);
			}
			if(operations.get(i).isMean()){
				String newField = createUniqueFieldName(outputFieldNames, currentFieldName, "_"+PluginServices.getText(this, "MEAN"), maxlength);
				outputFieldNames.add(newField);
				fieldTypes.add(Types.DOUBLE);
			}
			if(operations.get(i).isSum()){
				String newField = createUniqueFieldName(outputFieldNames, currentFieldName, "_"+PluginServices.getText(this, "SUM"), maxlength);
				outputFieldNames.add(newField);
				fieldTypes.add(currFieldType);
			}
			if(operations.get(i).isSd()){
				String newField = createUniqueFieldName(outputFieldNames,
						currentFieldName,
						"_"+PluginServices.getText(this, "STDEV"),
						maxlength);
				outputFieldNames.add(newField);
				fieldTypes.add(Types.DOUBLE);
			}
			if(operations.get(i).isVar()){
				String newField = createUniqueFieldName(outputFieldNames,
						currentFieldName,
						"_"+PluginServices.getText(this, "VAR"),
						maxlength);
				outputFieldNames.add(newField);
				fieldTypes.add(Types.DOUBLE);
			}
		}
	}
	
	
	/**
	 * <p>Creates a new field name (not contained in the existingFields list),
	 * based in the provided baseName and suffix and with the provided maximum
	 * length.
	 * The following pattern is used to create the new names:<p>
	 * <pre>(baseName)(index)*(suffix)</pre>
	 * <p>Examples of created names:</p>
	 * <pre>baseName: "TERRAIN", suffix: "_MAX", maxLength: 11
	 * existingFields: {"TERRAIN_MAX"}
	 * created fields: "TERRAI0_MAX", "TERRAI1_MAX", "TERRAI2_MAX", ..., "TERRA00_MAX", "TERRA01_MAX", etc.
	 * </pre>
	 * 
	 * @param existingFields
	 * @param baseName
	 * @param sufix
	 * @return
	 * @throws SummarizeException 
	 */
	public String createUniqueFieldName(ArrayList<String> existingFields,
			String baseName,
			String suffix,
			int maxLength) throws SummarizeException{
		
		// ensure we don't try to take a substring longer than baseName.length
		int auxLength = Math.min(maxLength-suffix.length(),
						baseName.length());
		
		String result = baseName.substring(0, auxLength) + suffix;
		if (!existingFields.contains(result)) {
			return result;
		}
		
		//ensure the index length is not longer than the maximum allowed field length
		long maxIndex = (long) Math.pow(10, maxLength-suffix.length());

		int index = 0;
		
		do {
			if (index>=maxIndex)  {
				throw new SummarizeException(
						PluginServices.getText(this, "Not_possible_generate_unique_field_name"));
			}
			// ensure we don't try to take a substring longer than baseName.length
			auxLength = 
				Math.min(maxLength-Integer.toString(index).length()-suffix.length(),
						baseName.length());
			result = baseName.substring(0, auxLength) + index + suffix;
			index++;
			
		} while (existingFields.contains(result));

		return result;
	}

	public void calculateStats(ArrayList<SelectedStatistics> operations,
			String groupByField, String[] outputFieldNames, IWriter writer)
		throws NonNumericFieldException, DBFExportException,
			GroupingErrorException, SummarizeException {
		ArrayList<Object> claves = new ArrayList<Object>();

		
		FBitSet dss = (FBitSet) getDataSource().getSelection().clone();
		int indiceColumnaMaestra;
		try {
			indiceColumnaMaestra = getDataSource().getFieldIndexByName(groupByField);
		

		//ONLY IF THE USER HAS SELECTED SOME ROWS
		if(dss.cardinality() > 0){

			for(int i = dss.nextSetBit(0); i >= 0; i = dss.nextSetBit(i+1)){
				// get all the distinct groups
				if(!claves.contains(getDataSource().getFieldValue(i, indiceColumnaMaestra)))
					claves.add(getDataSource().getFieldValue(i, indiceColumnaMaestra));
			}
		}
		else {
			//ONLY IF THE USER HAS NOT SELECTED ANY ROW
			for(int i = 0; i < getDataSource().getRowCount(); i++){
				// get all the distinct groups (this may be done with a SELECT DISTINCT)
				if(!claves.contains(getDataSource().getFieldValue(i, indiceColumnaMaestra)))
					claves.add(getDataSource().getFieldValue(i, indiceColumnaMaestra));
			}
		};
		
		for(int z=0; z<claves.size(); z++){
			int indice = 0;
			Value[] value = new Value[outputFieldNames.length];

			//GROUP BY column
			value[indice] =  (Value)claves.get(z); indice++;

			for(int i=0; i<operations.size(); i++){				

				DataSource ds = getSelectedAndFilteredRows(claves.get(z), operations.get(i).getColumnName(), groupByField, getDataSource());
				FBitSet selection = new FBitSet();
				//row number after using the filter (count field)
				long numFilteredRows = ds.getRowCount();
				if (numFilteredRows>0) {
					selection.set(0, (int) ds.getRowCount());
				}
				SelectionFieldIterator iterator = new SelectionFieldIterator(ds, selection, operations.get(i).getColumnName());
				
				com.iver.cit.gvsig.project.documents.table.Statistics statsCalculator = new com.iver.cit.gvsig.project.documents.table.Statistics(iterator);

				//COUNT column (only on first iteraction)
				if(i==0){
					value[indice] =  ValueFactory.createValue(numFilteredRows);
					indice++;
				}

				if(operations.get(i).isMin()){
					BigDecimal min = statsCalculator.min();
					value[indice] =  ValueFactory.createValueByType(min.toString(),
							operations.get(i).getColumnType());
					indice++;
				}
				if(operations.get(i).isMax()){
					BigDecimal max = statsCalculator.max();
					value[indice] =  ValueFactory.createValueByType(max.toString(),
							operations.get(i).getColumnType());
					indice++;
				}
				if(operations.get(i).isMean()){
					BigDecimal mean = statsCalculator.mean();
					value[indice] =  ValueFactory.createValueByType(mean.toString(),
							Types.DOUBLE);
					indice++;
				}
				if(operations.get(i).isSum()){
					BigDecimal sum = statsCalculator.sum();
					value[indice] =  ValueFactory.createValueByType(sum.toString(),
							operations.get(i).getColumnType());
					indice++;
				}
				if(operations.get(i).isSd()){
					BigDecimal stdDeviation = statsCalculator.stdDeviation();
					value[indice] =  ValueFactory.createValueByType(stdDeviation.toString(),
							Types.DOUBLE);
					indice++;
				}
				if(operations.get(i).isVar()){
					BigDecimal variance = statsCalculator.variance();
					value[indice] =  ValueFactory.createValueByType(variance.toString(),
							Types.DOUBLE);
					indice++;
				}
			}

			try {
				writeRow(writer, value, z);
			} catch (VisitorException e) {
				DBFExportException ex = new DBFExportException(e);
				throw ex;
			}
		}
		} catch (ReadDriverException e1) {
			SummarizeException ex = new SummarizeException(e1);
			throw ex;
		} catch (java.text.ParseException e1) {
			SummarizeException ex = new SummarizeException(e1);
			throw ex;
		}
		//************************************************************************************************************

	}

	
	public IWriter createDbfFile(File targetFile, String[] fieldNames, int[] fieldTypes) throws DBFExportException {
		FileDriver driver = null;
		try {
			driver = (FileDriver) LayerFactory.getDM().getDriver("gdbms dbf driver");

			driver.createSource(targetFile.getAbsolutePath(), fieldNames, fieldTypes);
			targetFile.createNewFile();

			driver.open(targetFile);
			return ((IWriteable)driver).getWriter();
		} catch (DriverLoadException e2) {
			throw new DBFExportException(e2);
		} catch (ReadDriverException e2) {
			throw new DBFExportException(e2);
		} catch (IOException e2) {
			throw new DBFExportException(e2);
		}

	}

	/**
	 * Writer to create dbf file
	 * 
	 * @param writer
	 * @param feature - fill with the corresponding rows
	 * @param index  - fill file position 
	 * @throws VisitorException 
	 */
	protected void writeRow(IWriter writer, Value[] values, int index) throws VisitorException {
		IFeature feat = new DefaultFeature(null, values, Integer.toString(index++));
		DefaultRowEdited edRow = new DefaultRowEdited(feat,
				 DefaultRowEdited.STATUS_ADDED, index);
		writer.process(edRow);
	}
	
	protected void initWriter(IWriter writer) throws DBFExportException {
		try {
			writer.initialize(writer.getTableDefinition());
			writer.preProcess();
		} catch (InitializeWriterException e) {
			DBFExportException ex = new DBFExportException(e);
			throw ex;
		} catch (ReadDriverException e) {
			DBFExportException ex = new DBFExportException(e);
			throw ex;
		} catch (StartWriterVisitorException e) {
			DBFExportException ex = new DBFExportException(e);
			throw ex;
		}
		
	}
	
	protected void finishWriter(IWriter writer) throws DBFExportException  {
		try {
			writer.postProcess();
		} catch (StopWriterVisitorException e) {
			DBFExportException ex = new DBFExportException(e);
			throw ex;
		}
	}


	/**
	 * Devuelve un iterador con las filas de la tabla que cumplen con la condicion
	 * @param clave
	 * @param columnNumber The number of the field whose statistics are to be calculated
	 * @param masterColumnName The name of the field which will be used to define the groups
	 * @param originalDS
	 * @return
	 * @throws GroupingErrorException 
	 */
	protected DataSource getSelectedAndFilteredRows(Object clave, String statsColumnName, String masterColumnName, SelectableDataSource originalDS) throws GroupingErrorException{
		
		try {
			int masterIndex = originalDS.getFieldIndexByName(masterColumnName);
			String fieldType = FieldDescription.typeToString(originalDS.getFieldType(masterIndex));
			DataSource ds;
			String query;
			if (fieldType.equals("Double")){
				query = "select * from '"+originalDS.getName()+"' where (("+masterColumnName+" - "+clave+ ") between -1 and 1);";
			} else if (fieldType.equals("Integer")) {
				query = "select * from '"+originalDS.getName()+"' where "+masterColumnName+" = "+clave+";";
			}
			else if (fieldType.equals("Boolean")){
				Boolean bvalue = null;
				if (clave instanceof Boolean){
					// Por si el valor no viene de gdbms
					bvalue = (Boolean) clave;
					
				} else if (clave instanceof BooleanValue){
					bvalue = ((BooleanValue)clave).getValue() ? Boolean.TRUE : Boolean.FALSE;
				} else if (clave == null){
					// nothing to do
				} else{
					throw new GroupingErrorException("Illegal value for boolean: "+clave.getClass().getName()+": " +clave);
				}
				if (bvalue == null){
					query = "select * from '"+originalDS.getName()+"' where "+masterColumnName+" = "+clave+";";
				} else if (bvalue.booleanValue()){
					query = "select * from '"+originalDS.getName()+"' where "+masterColumnName+" = ( 1 = 1 );";
				} else {
					query = "select * from '"+originalDS.getName()+"' where "+masterColumnName+" = ( 1 != 1 );";
				}
			}
			else {
				query = "select * from '"+originalDS.getName()+"' where "+masterColumnName+" = '"+clave.toString().replaceAll("'", "''")+"';";
				
			}
			logger.debug(query);
			return LayerFactory.getDataSourceFactory().executeSQL(query, DataSourceFactory.MANUAL_OPENING);
			
		} catch (DriverLoadException e) {
			throw new GroupingErrorException(e);
		} catch (ReadDriverException e) {
			throw new GroupingErrorException(e);
		} catch (ParseException e) {
			throw new GroupingErrorException(e);
		} catch (SemanticException e) {
			throw new GroupingErrorException(e);
		} catch (EvaluationException e) {
			throw new GroupingErrorException(e);
		} catch (com.hardcode.gdbms.parser.TokenMgrError e) {
			throw new GroupingErrorException(e);
		}
	}



	public void setOperations(ArrayList<SelectedStatistics> operations) {
		this.operations = operations;
	}



	public ArrayList<SelectedStatistics> getOperations() {
		return operations;
	}


	public void setDataSource(SelectableDataSource sds) {
		this.sds = sds;
	}

	public SelectableDataSource getDataSource() {
		return sds;
	}


	public void setGroupByField(String groupByField) {
		this.groupByField = groupByField;
	}

	public String getGroupByField() {
		return groupByField;
	}
}
