package com.iver.cit.gvsig.fmap.edition;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.DoubleValue;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.expansionfile.OpenExpansionFileException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.drivers.BoundedShapes;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.MappingAnnotation;
import com.iver.cit.gvsig.fmap.operations.strategies.AnnotationStrategy;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.VectorialUniqueValueLegend;
import com.iver.cit.gvsig.fmap.spatialindex.QuadtreeJts;

public class AnnotationEditableAdapter extends VectorialEditableAdapter{
	private ArrayList labels;
	private MappingAnnotation mapping;
	private ILegend legend;
	private boolean isInPixels=true;
	private FLyrAnnotation lyrAnnotation;
	private LabelExpansion labelExpansion=new LabelExpansion();
	private boolean isToSave=false;

	public FLabel createLabel(IRow row){
		FSymbol symbol;
		// El mapping[0] es el text
		int fieldId = mapping.getColumnText();
		// El mapping[1] es el ángulo
		int idFieldRotationText = mapping.getColumnRotate();
		// El mapping[2] es el color
		int idFieldColorText = mapping.getColumnColor();
		// El mapping[3] es el alto
		int idFieldHeightText = mapping.getColumnHeight();
		// El mapping[4] es el tipo de fuente
		int idFieldTypeFontText = mapping.getColumnTypeFont();
		// El mapping[5] es el estilo de fuente
		int idFieldStyleFontText = mapping.getColumnStyleFont();

		IGeometry geom = ((IFeature) row).getGeometry();
		String t = new String();
		Value val = row.getAttribute(fieldId);
		t = val.toString();
		if (idFieldColorText != -1) {
			DoubleValue valColor = (DoubleValue) row
					.getAttribute(idFieldColorText);
			t = t.concat(valColor.toString());
		}
		if (idFieldTypeFontText != -1) {
			StringValue valTypeFont = (StringValue) row
					.getAttribute(idFieldTypeFontText);
			t = t.concat(valTypeFont.toString());
		}
		if (idFieldStyleFontText != -1) {
			IntValue valStyleFont = (IntValue) row
					.getAttribute(idFieldStyleFontText);
			t = t.concat(valStyleFont.toString());
		}

		Value total = ValueFactory.createValue(t);

		FLabel[] lbls = geom.createLabels(0, true);
		double rotat = 0;
		FSymbol sym = (FSymbol) legend.getDefaultSymbol();
		for (int j = 0; j < lbls.length; j++) {
			if (lbls[j] != null) {
				lbls[j].setString(val.toString());
				if (idFieldRotationText != -1) {
					Value value=row.getAttribute(idFieldRotationText);
					if (value instanceof NullValue){
						rotat = sym.getRotation();
					}else{
						NumericValue rotation = (NumericValue) value;
						rotat = rotation.doubleValue();
					}
				} else {
					rotat = sym.getRotation();
				}
				lbls[j].setRotation(rotat);

				float height;
				if (idFieldHeightText != -1) {
					Value value=row.getAttribute(idFieldHeightText);
					if (value instanceof NullValue){
						height = sym.getFontSize();
						lbls[j].setHeight(height);
					}else{
						NumericValue h = (NumericValue) value;
						height = h.floatValue();
						lbls[j].setHeight(height);
					}
				} else {
					height = sym.getFontSize();
					lbls[j].setHeight(height);
				}
				VectorialUniqueValueLegend vuvl = (VectorialUniqueValueLegend) legend;
				if (vuvl.getSymbolByValue(total) == null) {
					Color color;
					if (idFieldColorText != -1) {
						DoubleValue c = (DoubleValue) row
								.getAttribute(idFieldColorText);
						color = new Color((int) c.getValue());
					} else {
						color = sym.getFontColor();
					}
					String typeFont;
					if (idFieldTypeFontText != -1) {
						StringValue tf = (StringValue) row
								.getAttribute(idFieldTypeFontText);
						typeFont = tf.getValue();
					} else {
						typeFont = sym.getFont()
								.getFontName();
					}
					int style;
					if (idFieldStyleFontText != -1) {
						IntValue sf = (IntValue) row
								.getAttribute(idFieldStyleFontText);
						style = sf.getValue();
					} else {
						style = sym.getFont().getStyle();
					}
					symbol = new FSymbol(FConstant.SYMBOL_TYPE_TEXT);
					symbol.setFontSizeInPixels(isInPixels);
					symbol.setFont(new Font(typeFont, style, (int) height));
					symbol.setDescription(lbls[j].getString());
					symbol.setFontColor(color);
					vuvl.addSymbol(total, symbol);
				}

			}
			return lbls[0];
		}
		return null;
	}




	public int doAddRow(IRow feat, int sourceType) throws ReadDriverException, ExpansionFileWriteException {
		boolean cancel = fireBeforeRowAdded(sourceType,feat.getID());
		if (cancel)
			return -1;
		Value[] values=feat.getAttributes();
		values[mapping.getColumnText()]=ValueFactory.createValue("New");
		FLabel label=createLabel(feat);
		//Rectangle2D r=((DefaultFeature)feat).getGeometry().getBounds2D();
		//r.setRect(r.getX()-r.getWidth()*4,r.getY()-r.getHeight()*4,r.getWidth()*10,r.getHeight()*4);
		//label.setBoundBox(r);
		AnnotationStrategy strategy = (AnnotationStrategy)lyrAnnotation.getStrategy();
		Rectangle2D r=strategy.getBoundBox(label.getOrig(),(float)label.getHeight(),label.getJustification(),label.getString());
        label.setBoundBox(r);
		IGeometry geom=getGeometry(label.getBoundBox());
		feat=new DefaultFeature(geom,values,feat.getID());

		///lyrAnnotation.deleteSpatialIndex();

		int calculatedIndex = -1;
		calculatedIndex = (int) getOriginalRecordset().getRowCount() + numAdd;

		int pos = expansionFile.addRow(feat, IRowEdited.STATUS_ADDED, actualIndexFields);
		labelExpansion.addLabel(label);
		relations.put(new Integer(calculatedIndex), new Integer(pos));
		numAdd++;

		fmapSpatialIndex.insert(r, new Integer(calculatedIndex));
        //lyrAnnotation.getFMap().invalidate();
        fireAfterRowAdded(feat,calculatedIndex, sourceType);
        return calculatedIndex;
	}

	public int doModifyRow(int calculatedIndex, IRow feat,int sourceType) throws ExpansionFileWriteException {
		boolean cancel;
		try {
			cancel = fireBeforeModifyRow(feat,calculatedIndex, sourceType);
		} catch (ReadDriverException e) {
			throw new ExpansionFileWriteException(writer.getName(),e);
		}
		if (cancel)
			return -1;
		int posAnteriorInExpansionFile = -1;
		Integer integer = new Integer(calculatedIndex);

//		System.err.println("Modifica una Row en la posición: "
//				+ calculatedIndex);
		// Si la geometría no ha sido modificada
		if (!relations.containsKey(integer)) {
			FLabel label=(FLabel)getLabel(calculatedIndex,true).clone();
			Value value=feat.getAttribute(mapping.getColumnText());
			Rectangle2D rLabelAnt=(Rectangle2D)label.getBoundBox().clone();
			label.setString(value.toString());

			if (mapping.getColumnRotate()==mapping.getColumnText()){
				label.setRotation(((NumericValue)value).doubleValue());
			} //Aquí hay que comprobar, si se quiere, el resto de columnas, si son iguales a la de Texto.


			int newPosition = expansionFile.addRow(feat, IRowEdited.STATUS_MODIFIED, actualIndexFields);
			relations.put(integer, new Integer(newPosition));

			// Se actualiza el índice espacial
			IGeometry g=((IFeature) feat).getGeometry();
			double[] d=new double[4];
			//Rectangle2D r = g.getBounds2D();
			g.getPathIterator(null).currentSegment(d);
			Point2D p=new Point2D.Double(d[0],d[1]);
			label.setBoundBox(new Rectangle2D.Double(p.getX(),p.getY(),rLabelAnt.getWidth(),rLabelAnt.getHeight()));
			Rectangle2D rLabel=label.getBoundBox();
			label.setOrig(p);
			fmapSpatialIndex.delete(rLabelAnt, new Integer(calculatedIndex));
			fmapSpatialIndex.insert(rLabel, new Integer(
					calculatedIndex));
			labelExpansion.addLabel(label);
		} else {
			FLabel label=(FLabel)labelExpansion.getLabel(((Integer)relations.get(new Integer(calculatedIndex))).intValue()).clone();
			Value value=feat.getAttribute(mapping.getColumnText());
			Rectangle2D rLabelAnt=(Rectangle2D)label.getBoundBox().clone();
			label.setString(value.toString());

			if (mapping.getColumnRotate()==mapping.getColumnText()){
				label.setRotation(((NumericValue)value).doubleValue());
			} //Aquí hay que comprobar, si se quiere, el resto de columnas, si son iguales a la de Texto.


			// Obtenemos el índice en el fichero de expansión
			int num = ((Integer) relations.get(integer)).intValue();
			posAnteriorInExpansionFile = num;

			/*
			 * Se modifica la geometría y nos guardamos el índice dentro del
			 * fichero de expansión en el que se encuentra la geometría
			 * modificada
			 */
			num = expansionFile.modifyRow(num, feat, actualIndexFields);

			/*
			 * Actualiza la relación del índice de la geometría al índice en el
			 * fichero de expansión.
			 */
			relations.put(integer, new Integer(num));

			Rectangle2D r = ((IFeature) feat).getGeometry().getBounds2D();

			Point2D p=new Point2D.Double(r.getX(),r.getY());

			label.setBoundBox(new Rectangle2D.Double(p.getX(),p.getY()+rLabelAnt.getHeight(),rLabelAnt.getWidth(),rLabelAnt.getHeight()));
			Rectangle2D rLabel=label.getBoundBox();
			label.setOrig(p);
			fmapSpatialIndex.delete(rLabelAnt, new Integer(calculatedIndex));
			fmapSpatialIndex.insert(rLabel, new Integer(
					calculatedIndex));
			labelExpansion.modifyLabel(num,label);
		}
		fireAfterModifyRow(calculatedIndex,sourceType);
		return posAnteriorInExpansionFile;
	}

	public IRow doRemoveRow(int index,int sourceType) throws ReadDriverException, ExpansionFileReadException {
		boolean cancel = fireBeforeRemoveRow(index, sourceType);
		if (cancel)
			return null;
		//Llega el calculatedIndex
    	Integer integer = new Integer(index);

        IFeature feat = null;

        FLabel label=getLabel(index,true);
        delRows.set(index, true);
        //Si la geometría no ha sido modificada
        if (!relations.containsKey(integer)) {
           	feat = (DefaultFeature) (ova.getFeature(index));
       } else {
			int num = ((Integer) relations.get(integer)).intValue();
			feat = (IFeature) expansionFile.getRow(num).getLinkedRow();
		}
        System.err.println("Elimina una Row en la posición: " + index);
        //Se actualiza el índice
        if (feat != null) {
            Rectangle2D r = label.getBoundBox();
            this.fmapSpatialIndex.delete(r, new Integer(index));
        }
        setSelection(new FBitSet());
        fireAfterRemoveRow(index, sourceType);
        return feat;
	}

	public void undoAddRow(int calculatedIndex, int sourceType) throws EditionCommandException {
		boolean cancel=true;
		try {
			cancel = fireBeforeRemoveRow(calculatedIndex, sourceType);
		} catch (ReadDriverException e1) {
			throw new EditionCommandException(writer.getName(),e1);
		}
		if (cancel)
			return;
		Rectangle2D r = getLabel(calculatedIndex,true).getBoundBox();
        this.fmapSpatialIndex.delete(r, new Integer(calculatedIndex));
        expansionFile.deleteLastRow();
		relations.remove(new Integer(calculatedIndex));
		numAdd--;
        try {
			setSelection(new FBitSet());
		} catch (ReadDriverException e) {
			throw new EditionCommandException(writer.getName(),e);
		}
		labelExpansion.deleteLastLabel();
		fireAfterRemoveRow(calculatedIndex, sourceType);
	}

	public int undoModifyRow(int calculatedIndex, int previousExpansionFileIndex, int sourceType) throws EditionCommandException {
		try {
		if (previousExpansionFileIndex == -1) {
	            //Se obtiene la geometría para actualizar el índice
	        	int inverse = getInversedIndex(calculatedIndex);
	        	DefaultFeature df;
				df = (DefaultFeature)getRow(inverse).getLinkedRow();
				IGeometry g = df.getGeometry();
	            Rectangle2D r = g.getBounds2D();
	            //Se elimina de las relaciones y del fichero de expansión
	            relations.remove(new Integer(calculatedIndex));
	            expansionFile.deleteLastRow();
	            labelExpansion.deleteLastLabel();
	            //Se actualizan los índices
	            DefaultFeature dfAnt;
				dfAnt = (DefaultFeature)getRow(inverse).getLinkedRow();
				IGeometry gAnt = dfAnt.getGeometry();
	            boolean cancel = fireBeforeModifyRow(dfAnt,calculatedIndex, sourceType);
	    		if (cancel)
	    			return -1;
	            Rectangle2D rAnt = gAnt.getBounds2D();
	            this.fmapSpatialIndex.delete(r, new Integer(calculatedIndex));
	            this.fmapSpatialIndex.insert(rAnt, new Integer(calculatedIndex));


	        } else {
	            //Se obtiene la geometría para actualizar el índice
	            IGeometry g = null;
	            int inverse = getInversedIndex(calculatedIndex);
	            DefaultFeature df=(DefaultFeature)getRow(inverse).getLinkedRow();
	            g = df.getGeometry();
	            Rectangle2D r = g.getBounds2D();
	            this.fmapSpatialIndex.delete(r, new Integer(calculatedIndex));
	            int numAnt=((Integer)relations.get(new Integer(calculatedIndex))).intValue();
	            //Se actualiza la relación de índices
	            relations.put(new Integer(calculatedIndex),
	                new Integer(previousExpansionFileIndex));

	            //Se actualizan los índices
	            df=(DefaultFeature)getRow(inverse).getLinkedRow();
	            boolean cancel = fireBeforeModifyRow(df,calculatedIndex, sourceType);
	    		if (cancel)
	    			return -1;
	            g = df.getGeometry();
	            r = g.getBounds2D();
	            this.fmapSpatialIndex.insert(r, new Integer(calculatedIndex));
	            Value value=df.getAttribute(mapping.getColumnText());
	    		FLabel label= getLabel(inverse,true);
	    		label.setString(value.toString());
	    		if (mapping.getColumnRotate()==mapping.getColumnText()){
	    			label.setRotation(((NumericValue)value).doubleValue());
	    		}
	    		return numAnt;
	        }
		}  catch (ReadDriverException e) {
			throw new EditionCommandException(writer.getName(),e);
		}
		fireAfterModifyRow(calculatedIndex, sourceType);
		return -1;
	}

	public void undoRemoveRow(int index, int sourceType) throws EditionCommandException {
		delRows.set(index, false);
		String fid;
		try {
			fid = getRow(index).getID();

		boolean cancel = fireBeforeRowAdded(sourceType,fid);
		if (cancel){
			delRows.set(index,true);
			return;
		}
		} catch (ExpansionFileReadException e) {
			throw new EditionCommandException(lyrAnnotation.getName(),e);
		} catch (ReadDriverException e) {
			throw new EditionCommandException(lyrAnnotation.getName(),e);
		}
	    Rectangle2D r = getLabel(index,true).getBoundBox();
	    this.fmapSpatialIndex.insert(r, new Integer(index));
	    fireAfterRowAdded(null,index, sourceType);
	}

	public AnnotationEditableAdapter(FLyrAnnotation lyrAnnotation) {
		super();
		this.labels=lyrAnnotation.getLabels();
		this.mapping=lyrAnnotation.getMapping();
		this.legend=lyrAnnotation.getLegend();
		this.isInPixels=lyrAnnotation.isInPixels();
		this.lyrAnnotation=lyrAnnotation;
	}
	public IRowEdited[] getFeatures(Rectangle2D r, String strEPSG) throws ReadDriverException, ExpansionFileReadException {
		// En esta clase suponemos random access.
		// Luego tendremos otra clase que sea VectorialEditableDBAdapter
		// que reescribirá este método.
//        Envelope e = FConverter.convertRectangle2DtoEnvelope(r);
        List l = fmapSpatialIndex.query(r);
        IRowEdited[] feats = new IRowEdited[l.size()];
        for (int index = 0; index < l.size(); index++) {
	        Integer i = (Integer) l.get(index);
	        int inverse = getInversedIndex(i.intValue());
	        feats[index] = getRow(inverse);
	    }
		return feats;
	}
	 /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.edition.IEditableSource#getRow(int)
     */
    public IRowEdited getRow(int index) throws ReadDriverException, ExpansionFileReadException {
    	int calculatedIndex=getCalculatedIndex(index);
    	Integer integer = new Integer(calculatedIndex);
        //Si no está en el fichero de expansión
    	DefaultRowEdited edRow=null;

    	if (!relations.containsKey(integer)) {
			IFeature f=ova.getFeature(calculatedIndex);
			String s=f.getID();
			FLabel label=getLabel(index,false);
			if (label==null)
				return null;
			IGeometry geom=getGeometry(label.getBoundBox());
			f=new DefaultFeature(geom,f.getAttributes(),s);
			//f.setID(s);
			edRow = new DefaultRowEdited(f,
			        IRowEdited.STATUS_ORIGINAL, index);
			return edRow;
        }
		int num = ((Integer) relations.get(integer)).intValue();
		IRowEdited aux = expansionFile.getRow(num);
		IFeature f=(IFeature)aux.getLinkedRow().cloneRow();
		IGeometry geom=getGeometry(labelExpansion.getLabel(num).getBoundBox());
		String s=f.getID();
		f=new DefaultFeature(geom,f.getAttributes(),s);
		//f.setID(s);
		edRow = new DefaultRowEdited(f, aux.getStatus(), index);
		return edRow;
    }
    public void saveEdits(IWriter writer, int sourceType) throws StopWriterVisitorException {
    	isToSave=true;
    	super.saveEdits(writer,sourceType);
    	isToSave=false;

    }

    private IGeometry getGeometry(Rectangle2D r){
    	if (isToSave)
    		return ShapeFactory.createPoint2D(r.getX(),r.getMaxY());
    	GeneralPathX resul = new GeneralPathX();
		Point2D[] vs=new Point2D[4];
		vs[0]=new Point2D.Double(r.getX(),r.getY());
    	vs[1]=new Point2D.Double(r.getMaxX(),r.getY());
    	vs[2]=new Point2D.Double(r.getMaxX(),r.getMaxY());
    	vs[3]=new Point2D.Double(r.getX(),r.getMaxY());
    	//vs[4]=new Point2D.Double(r.getX(),r.getY());
		for (int i = 0; i < vs.length; i++) {
			if (i == 0) {
				resul.moveTo(vs[i].getX(),vs[i].getY());
			} else {
				resul.lineTo(vs[i].getX(),vs[i].getY());
			}
		}
		resul.closePath();
		return ShapeFactory.createPolygon2D(resul);
    }
    public void startEdition(int sourceType) throws StartWriterVisitorException {
    	isEditing = true;
		try {
			expansionFile.open();
		} catch (OpenExpansionFileException e1) {
			throw new StartWriterVisitorException(lyrAnnotation.getName(),e1);
		}
		fmapSpatialIndex = new QuadtreeJts();
		for (int i = 0; i < labels.size(); i++) {
			Rectangle2D r=((FLabel)labels.get(i)).getBoundBox();
			fmapSpatialIndex.insert(r, new Integer(i));
			if (fullExtent == null) {
				fullExtent = r;
			} else {
				fullExtent.add(r);
			}
		}

//		System.err.println("Se han metido en el índice "
//				+ index.queryAll().size() + " labels");
	}

    public Rectangle2D getShapeBounds(int index) throws ReadDriverException, ExpansionFileReadException {
		//Solo se utiliza cuando el driver es BoundedShapes
//		 Si no está en el fichero de expansión
		Integer integer = new Integer(index);
		if (!relations.containsKey(integer)) {
			if (ova.getDriver() instanceof BoundedShapes){
				BoundedShapes bs = (BoundedShapes) ova.getDriver();
				return bs.getShapeBounds(index);
			}
			return ova.getDriver().getShape(index).getBounds2D();

		}
		int num = ((Integer) relations.get(integer)).intValue();
		DefaultRowEdited feat;
		feat = (DefaultRowEdited) expansionFile.getRow(num);
		if (feat.getStatus() == IRowEdited.STATUS_DELETED)
			return null;
		IGeometry geom = ((IFeature)feat.getLinkedRow()).getGeometry();
		return geom.getBounds2D();

	}
    public FLabel getLabel(int index,boolean calculated) {
    	FLabel label=null;
    	int calculatedIndex=index;
    	Integer integer=new Integer(index);
    	if (!calculated){
    		calculatedIndex=getCalculatedIndex(index);
    		integer = new Integer(calculatedIndex);
    	}
    	//Si no está en el fichero de expansión
    	if (!relations.containsKey(integer)) {
    		if (calculatedIndex > labels.size()){
    			return null;
    		}
    		label=(FLabel)labels.get(calculatedIndex);
		    return label;
        }
		int num = ((Integer) relations.get(integer)).intValue();
		label=labelExpansion.getLabel(num);
		return label;
    }




//	public IGeometry getShape(int rowIndex) throws DriverIOException {
//		IGeometry geom=null;
//		//		 Si no está en el fichero de expansión
//		int calculatedIndex = getCalculatedIndex(rowIndex);
//		Integer integer = new Integer(calculatedIndex);
//		if (!relations.containsKey(integer)) {
//			geom=ova.getShape(calculatedIndex);
//		} else {
//			int num = ((Integer) relations.get(integer)).intValue();
//			DefaultRowEdited feat;
//			try {
//				feat = (DefaultRowEdited) expansionFile.getRow(num);
//				geom= ((IFeature) feat.getLinkedRow()).getGeometry()
//						.cloneGeometry();
//			} catch (IOException e) {
//				e.printStackTrace();
//				throw new DriverIOException(e);
//			}
//		}
//		if (geom.getGeometryType()!=FShape.POINT) {
//			Point2D p=FLabel.createLabelPoint((FShape)geom.getInternalShape());
//			geom=ShapeFactory.createPoint2D(p.getX(),p.getY());
//		}
//		return geom;
//
//	}
}
