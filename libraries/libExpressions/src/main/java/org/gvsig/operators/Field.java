package org.gvsig.operators;

import java.sql.Types;

import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
/**
 * @author Vicente Caballero Navarro
 */
public class Field extends AbstractField{
	private FieldDescription fd;
	private String typeField;
	public Field() {
	}
	public void setFieldDescription(FieldDescription fd) {
		this.fd=fd;
		switch (fd.getFieldType()) {
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.DECIMAL:
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.NUMERIC:
			typeField =  "numeric_value";
			break;
		case Types.LONGVARCHAR:
		case Types.VARCHAR:
			typeField="string_value";
			break;
		case Types.BOOLEAN:
			typeField="boolean_value";
			break;
		case Types.DATE:
			typeField="date_value";
			break;
		}

	}
	public String addText(String s) {
		return s.concat(toString());
	}
	public String toString() {
		return "["+fd.getFieldAlias()+"]";
	}
	public boolean isEnable() {
		return true;
	}
	public String getTooltip(){
		return "field"+":  "+fd.getFieldAlias()+"\n"+getDescription();
	}
	public String getDescription() {
        return  "type" + ": " +
        typeField;
    }
}
