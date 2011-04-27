package com.iver.cit.gvsig.fmap.drivers;

import java.io.File;
import java.sql.Types;

import com.iver.cit.gvsig.fmap.core.FShape;

public class DXFLayerDefinition extends LayerDefinition {

	private File dxfFile;

	public DXFLayerDefinition() {
		super();
		FieldDescription layerField = new FieldDescription();
		layerField.setFieldName("Layer");
		layerField.setFieldType(Types.VARCHAR);
		FieldDescription colorField = new FieldDescription();
		colorField.setFieldName("Color");
		colorField.setFieldType(Types.INTEGER);
		FieldDescription elevationField= new FieldDescription();
		elevationField.setFieldName("Elevation");
		elevationField.setFieldType(Types.FLOAT);
		FieldDescription thicknessField= new FieldDescription();
		thicknessField.setFieldName("Thickness");
		thicknessField.setFieldType(Types.FLOAT);
		FieldDescription textField= new FieldDescription();
		textField.setFieldName("Text");
		textField.setFieldType(Types.VARCHAR);
		FieldDescription heightTextField= new FieldDescription();
		heightTextField.setFieldName("heightText");
		heightTextField.setFieldType(Types.FLOAT);
		FieldDescription rotationTextField= new FieldDescription();
		rotationTextField.setFieldName("rotationText");
		rotationTextField.setFieldType(Types.FLOAT);

		FieldDescription[] fieldsDXF = new FieldDescription[7];
		fieldsDXF[0] = layerField;
		fieldsDXF[1] = colorField;
		fieldsDXF[2] = elevationField;
		fieldsDXF[3] = thicknessField;
		fieldsDXF[4] = textField;
		fieldsDXF[5] = heightTextField;
		fieldsDXF[6] = rotationTextField;
		this.setFieldsDesc(fieldsDXF);
	}

	public FieldDescription[] getFieldsDesc() {
		return super.getFieldsDesc();
	}

	public int getShapeType() {
		return FShape.MULTI;
	}

	public void setFile(File newFile) {
		dxfFile = newFile;
	}

}
