package org.gvsig.graph.gui.wizard.servicearea;

import java.util.logging.Logger;

import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.Network;


public class NetworkPointsModel extends AbstractPointsModel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1694526796604795043L;
	
	private Network network;
		
	@SuppressWarnings("unused")
	private Logger logger;
	
	public NetworkPointsModel(Network network){
		super();
		
		this.logger=Logger.getLogger(this.getClass().getName());
		
		this.network=network;
		this.mainCosts=new String[this.getRowCount()];
		this.secondaryCosts=new String[this.getRowCount()];
		this.bitsetEnabled.set(0, this.getRowCount());
	}

	public int getColumnCount() {
		return 5;
	}

	public int getRowCount() {
		return this.network.getFlagsCount();
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
				if(this.getRowCount()>rowIndex)
					return Integer.valueOf(((GvFlag)this.network.getOriginaFlags().get(rowIndex)).getIdFlag());
				else
					return null;
			case 2:
				if(rowIndex<this.getRowCount())
					return ((GvFlag)this.network.getOriginaFlags().get(rowIndex)).getDescription();
				else
					return "<Error en la obtención>";
			case 3:
				if(this.isUniqueMainCostEnabled()){
					return this.uniqueMainCosts;
				}
				else{
					if(rowIndex<this.mainCosts.length){
						if(this.mainCosts[rowIndex]!=null){
							try{
								return this.getTotalCosts(this.mainCosts[rowIndex]);
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
				if(rowIndex<this.getRowCount())
					((GvFlag)this.network.getOriginaFlags().get(rowIndex)).setDescription(String.valueOf(aValue));
					
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
		return "Modelo: Red # Número de flags: "+this.network.getOriginaFlags().size()+" #";
	}
}
