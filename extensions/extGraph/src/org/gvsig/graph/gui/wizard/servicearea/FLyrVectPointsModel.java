package org.gvsig.graph.gui.wizard.servicearea;

import java.sql.Types;
import java.util.logging.Logger;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

public class FLyrVectPointsModel extends AbstractPointsModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8096985006383242578L;
	
	private FLyrVect layer;
	private String mainCostField;
	private String secondaryCostField;
	
	private SelectableDataSource recordset;
	private int rowCount;
	
	private Logger logger;
	
	private String[] descriptions;
	
	public FLyrVectPointsModel(FLyrVect layer) throws ReadDriverException, InvalidCostFieldException{
		super();
		
		this.logger=Logger.getLogger(this.getClass().getName());

		this.layer=layer;
		this.mainCostField="";
		this.secondaryCostField="";

		this.recordset=this.layer.getRecordset();
		rowCount=(int)this.getRowCount();
		this.bitsetEnabled.set(0, rowCount);
		this.descriptions=new String[rowCount];
		int descriptionFieldIndex = this.recordset.getFieldIndexByName("descript");
		if(descriptionFieldIndex>=0){
			for (int i = 0; i < rowCount; i++) {
				this.descriptions[i]=this.recordset.getFieldValue(i, descriptionFieldIndex).toString();
			}
		}
	}
	
	public FLyrVect getLayer(){
		return this.layer;
	}
	
	public void setCostFields(String mainCostField, String secondaryCostField) throws ReadDriverException, InvalidCostFieldException{
		if(this.mainCostField!=mainCostField && this.secondaryCostField!=secondaryCostField){
			this.logger.info("Modificando los dos campos de coste en el modelo de datos");
			this.mainCostField=mainCostField;
			this.secondaryCostField=secondaryCostField;
			this.mainCosts=new String[this.rowCount];
			this.secondaryCosts=new String[this.rowCount];

			int mainCostFieldIndex = this.recordset.getFieldIndexByName(mainCostField), 
			secondaryCostFieldIndex = this.recordset.getFieldIndexByName(secondaryCostField);

			if(mainCostFieldIndex<0){
				throw new InvalidCostFieldException("El campo especificado para el coste principal no existe");
			}
			else if(secondaryCostFieldIndex<0){
				throw new InvalidCostFieldException("El campo especificado para el coste secundario no existe");
			}

			//CHAPUZA!!!: SE DEBERIA OBTENER EL TIPO DEL CAMPO CON EL METODO .getFieldType(int fieldIndex) Y DESPUES
			//COMPROBAR EL TIPO DEVUELTO CON TODOS LOS QUE ADMITE GDBMS PARA COMPROBAR SI ES VALIDO O NO, 
			//EL PROBLEMA ES QUE NO HE ENCONTRADO LA LISTA DE VALORES QUE ADMITE GDBMS (ALGO ASI COMO LOS SQLTYPE)
			int mainCostFieldType=this.recordset.getFieldValue(0, mainCostFieldIndex).getSQLType();
			int secondaryCostFieldType=this.recordset.getFieldValue(0, secondaryCostFieldIndex).getSQLType();

			switch(mainCostFieldType){
			case Types.LONGVARCHAR:
			case Types.VARCHAR:	
				break;
			default:
				throw new InvalidCostFieldException("El campo especificado para el coste principal no es de tipo texto");
			}

			switch(secondaryCostFieldType){
			case Types.LONGVARCHAR:
			case Types.VARCHAR:
				break;
			default:
				throw new InvalidCostFieldException("El campo especificado para el coste secundario no es de tipo texto");
			}

			for (int i = 0; i < rowCount; i++) {
				this.mainCosts[i]=this.recordset.getFieldValue(i, mainCostFieldIndex).toString();
				this.secondaryCosts[i]=this.recordset.getFieldValue(i, secondaryCostFieldIndex).toString();
			}
		}
		else if(this.mainCostField!=mainCostField){
			this.logger.info("Modificando el campo de coste principal en el modelo de datos");
			this.mainCostField=mainCostField;
			this.mainCosts=new String[this.rowCount];


			int mainCostFieldIndex = this.recordset.getFieldIndexByName(mainCostField);

			if(mainCostFieldIndex<0){
				throw new InvalidCostFieldException("El campo especificado para el coste principal no existe");
			}

			//CHAPUZA!!!: SE DEBERIA OBTENER EL TIPO DEL CAMPO CON EL METODO .getFieldType(int fieldIndex) Y DESPUES
			//COMPROBAR EL TIPO DEVUELTO CON TODOS LOS QUE ADMITE GDBMS PARA COMPROBAR SI ES VALIDO O NO, 
			//EL PROBLEMA ES QUE NO HE ENCONTRADO LA LISTA DE VALORES QUE ADMITE GDBMS (ALGO ASI COMO LOS SQLTYPE)
			int mainCostFieldType=this.recordset.getFieldValue(0, mainCostFieldIndex).getSQLType();

			switch(mainCostFieldType){
			case Types.LONGVARCHAR:
			case Types.VARCHAR:	
				break;
			default:
				throw new InvalidCostFieldException("El campo especificado para el coste principal no es de tipo texto");
			}

			for (int i = 0; i < rowCount; i++) {
				this.mainCosts[i]=this.recordset.getFieldValue(i, mainCostFieldIndex).toString();
			}
		}
		else if(this.secondaryCostField!=secondaryCostField){
			this.logger.info("Modificando el campo de coste secundario en el modelo de datos");
			this.secondaryCostField=secondaryCostField;
			this.secondaryCosts=new String[this.rowCount];

			int secondaryCostFieldIndex = this.recordset.getFieldIndexByName(secondaryCostField);

			if(secondaryCostFieldIndex<0){
				throw new InvalidCostFieldException("El campo especificado para el coste secundario no existe");
			}

			//CHAPUZA!!!: SE DEBERIA OBTENER EL TIPO DEL CAMPO CON EL METODO .getFieldType(int fieldIndex) Y DESPUES
			//COMPROBAR EL TIPO DEVUELTO CON TODOS LOS QUE ADMITE GDBMS PARA COMPROBAR SI ES VALIDO O NO, 
			//EL PROBLEMA ES QUE NO HE ENCONTRADO LA LISTA DE VALORES QUE ADMITE GDBMS (ALGO ASI COMO LOS SQLTYPE)
			int secondaryCostFieldType=this.recordset.getFieldValue(0, secondaryCostFieldIndex).getSQLType();

			switch(secondaryCostFieldType){
			case Types.LONGVARCHAR:
			case Types.VARCHAR:
				break;
			default:
				throw new InvalidCostFieldException("El campo especificado para el coste secundario no es de tipo texto");
			}

			for (int i = 0; i < rowCount; i++) {
				this.secondaryCosts[i]=this.recordset.getFieldValue(i, secondaryCostFieldIndex).toString();
			}
		}
	}
	
	public int getColumnCount() {
		return 5;
	}

	public int getRowCount() {
		try {
			return (int)this.recordset.getRowCount();
		}
		catch (ReadDriverException except) {
			this.logger.warning(except.getMessage());
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public Class getColumnClass(int columnIndex) {
		switch(columnIndex){
			case 0:
				return Boolean.class;
			case 1:
				return Integer.class;
			case 2:
				return String.class;
			case 3:
				return String.class;
			case 4:
				return String.class;
			default:
				return String.class;
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(columnIndex){
			case 0:
				return this.bitsetEnabled.get(rowIndex);
			case 1:
				return rowIndex;
			case 2:
				if(rowIndex<this.descriptions.length){
					if(this.descriptions[rowIndex]!=null)
						return this.descriptions[rowIndex];
					else
						return "";
				}
				else{
					return "<Error en la obtención>";
				}
			case 3:
				if(this.isUniqueMainCostEnabled()){
					return this.uniqueMainCosts;
				}
				else{
					if(rowIndex<this.mainCosts.length){
						if(this.mainCosts[rowIndex]!=null){
							try{
								return this.getTotalCosts(this.mainCosts[rowIndex]);
								//return this.costs[rowIndex];
							}
							catch(NumberFormatException except){
								return "";
							}
						}
						else
							return "";
					}
					else{
						return "<Error en la obtención>";
					}
				}
			case 4:
				if(this.isUniqueSecondaryCostEnabled()){
					return this.uniqueSecondaryCosts;
				}
				else{
					if(rowIndex<this.secondaryCosts.length){
						if(this.secondaryCosts[rowIndex]!=null){
							try{
								return this.getTotalCosts(this.secondaryCosts[rowIndex]);
							}
							catch(NumberFormatException except){
								return "";
							}
						}
						else
							return "";
					}
					else{
						return "<Error en la obtención>";
					}
				}
			default:
				return null;
		}
	}

	public String getColumnName(int column) {
		switch(column){
			case 0:
				return "Habilitado";
			case 1:
				return "Id";
			case 2:
				return "Descripción";
			case 3:
				return "Coste principal";
			case 4:
				return "Coste secundario";
			default:
				return String.valueOf((char)(65+column)); //ASCII
		}		
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		switch(columnIndex){
			case 0:
				return true;
			case 1:
				return false;
			case 2:
				return true;
			case 3:
				return false;
			case 4:
				return false;
			default:
				return false;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch(columnIndex){
			case 0:{
				this.bitsetEnabled.set(rowIndex, ((Boolean)aValue).booleanValue());
				break;
			}
			case 2:{
				if(rowIndex<this.descriptions.length)
					this.descriptions[rowIndex]=String.valueOf(aValue);
				break;
			}
			case 3:{
				if(rowIndex<this.mainCosts.length)
					this.mainCosts[rowIndex]=String.valueOf(aValue);
				break;
			}
			case 4:{
				if(rowIndex<this.secondaryCosts.length)
					this.secondaryCosts[rowIndex]=String.valueOf(aValue);
				break;
			}			
		}
	}
	
	public String toString(){
		return "Modelo: Capa vectorial # Capa: "+this.layer.getName()+" # Campo Coste Primario: "+this.mainCostField+" # Campo Coste Secundario: "+this.secondaryCostField+" #";
	}
}

