package com.iver.cit.gvsig.project.documents.table.operators;

import java.util.Date;

import org.apache.bsf.BSFException;
import org.apache.bsf.BSFManager;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.BooleanValue;
import com.hardcode.gdbms.engine.values.DateValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.ExpressionFieldExtension;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.AbstractOperator;
import com.iver.cit.gvsig.project.documents.table.Index;
/**
 * @author Vicente Caballero Navarro
 */
public class FieldCopy extends AbstractOperator{
	private FieldDescription fd;
	public FieldCopy() {
	}
	public void setFieldDescription(FieldDescription fd) {
		this.fd=fd;
	}
	public String addText(String s) {
		return s.concat(toString());
	}

	public void eval(BSFManager interpreter) throws BSFException {
		interpreter.declareBean(fd.getFieldAlias(),this,FieldCopy.class);
		interpreter.eval(ExpressionFieldExtension.JYTHON,null,-1,-1,"java.lang.Object "+ fd.getFieldAlias()+ "(){return "+fd.getFieldAlias()+".getValue(indexRow,sds);};");
	}
	public Object getValue(Index indexRow,SelectableDataSource sds) {
		try {
			int index=sds.getFieldIndexByName(fd.getFieldName());
			Value value=sds.getFieldValue(indexRow.get(),index);
			if (value instanceof NumericValue) {
				double dv=((NumericValue)value).doubleValue();
				return new Double(dv);
			}else if (value instanceof DateValue) {
				Date date=((DateValue)value).getValue();
				return date;
			}else if (value instanceof BooleanValue){
				boolean b=((BooleanValue)value).getValue();
				return new Boolean(b);
			}else {
				return value.toString();
			}
		} catch (ReadDriverException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	public String toString() {
		return "["+fd.getFieldAlias()+"]";
	}
	public boolean isEnable() {
		return true;
	}
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}
}
