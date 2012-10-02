package com.iver.cit.gvsig.fmap.layers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import javax.print.attribute.PrintRequestAttributeSet;

import org.cresques.cts.ProjectionUtils;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ITextSymbol;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.drivers.BoundedShapes;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.edition.AnnotationEditableAdapter;
import com.iver.cit.gvsig.fmap.operations.strategies.AnnotationStrategy;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.fmap.operations.strategies.StrategyManager;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.VectorialUniqueValueLegend;
import com.iver.cit.gvsig.fmap.spatialindex.QuadtreeJts;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;

/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class FLyrAnnotation extends FLyrVect {
	private MappingAnnotation mapping = null;

	private ArrayList m_labels;

	private int indexEditing = -1;

	private boolean inPixels;
	private VectorialUniqueValueLegend vuvl=new VectorialUniqueValueLegend();
	private Strategy strategy=null;
	/**
	 * Crea un nuevo FLyrAnnotation.
	 */
	public FLyrAnnotation() {
		super();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param mapping
	 *            DOCUMENT ME!
	 */
	public void setMapping(MappingAnnotation mapping) {
		// TODO: comprobar si ha cambiado
		this.mapping = mapping;
		try {
			setLegend();
			createLabels();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		this.updateDrawVersion();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public MappingAnnotation getMapping() {
		return mapping;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.LayerOperations#draw(java.awt.image.BufferedImage,
	 *      ISymbol)
	 */
	public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
			Cancellable cancel, double scale) throws ReadDriverException {
		if (isWithinScale(scale)) {
			// Las que solo tienen etiquetado sin pintar el shape,
			// no pasamos por ellas
			boolean bDrawShapes = true;

			if (bDrawShapes) {
				if (strategy == null){
					strategy = (AnnotationStrategy) StrategyManager
							.getStrategy(this);
				}
				try {
					g.setColor(Color.black);
					strategy.draw(image, g, viewPort, cancel);
					if (getISpatialIndex()==null && !isEditing()) {
						createSpatialIndex();
					}
				} catch (ReadDriverException e) {
					this.setVisible(false);
					this.setActive(false);
					throw e;
				}
			}

			if (getVirtualLayers() != null) {
				getVirtualLayers().draw(image, g, viewPort, cancel, scale);
			}
		}
	}

	/**
	 * @throws ReadDriverException
	 * @throws ExpansionFileReadException
	 * @see com.iver.cit.gvsig.fmap.layers.LayerOperations#getFullExtent()
	 */
	public Rectangle2D getFullExtent() throws ReadDriverException, ExpansionFileReadException {
		Rectangle2D rAux;
		// logger.debug("source.start()");
		try {
			getSource().start();
		} catch (InitializeDriverException e) {
			throw new ReadDriverException(getName(),e);
		}
		rAux = getSource().getFullExtent();
			// logger.debug("source.stop()");
		getSource().stop();
			// Si existe reproyección, reproyectar el extent
		MathTransform transform = getCrsTransform();
		if (transform != null) {
			Point2D pt1 = new Point2D.Double(rAux.getMinX(), rAux.getMinY());
			Point2D pt2 = new Point2D.Double(rAux.getMaxX(), rAux.getMaxY());
			pt1 = ProjectionUtils.transform(pt1, transform);
			pt2 = ProjectionUtils.transform(pt2, transform);
			rAux = new Rectangle2D.Double();
			rAux.setFrameFromDiagonal(pt1, pt2);
		}
		return rAux;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.FLayer#print(java.awt.Graphics2D,
	 *      com.iver.cit.gvsig.fmap.ViewPort,
	 *      com.iver.utiles.swing.threads.Cancellable)
	 */
	public void print(Graphics2D g, ViewPort viewPort, Cancellable cancel,
			double scale, PrintRequestAttributeSet properties) throws ReadDriverException {
		if (isVisible() && isWithinScale(scale)) {
			Strategy strategy = StrategyManager.getStrategy(this);
			strategy.print(g, viewPort, cancel, properties);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.layers.layerOperations.RandomVectorialData#queryByRect(java.awt.geom.Rectangle2D)
	 */
	public FBitSet queryByRect(Rectangle2D rect) throws ReadDriverException, VisitorException {
		Strategy s = StrategyManager.getStrategy(this);

		return s.queryByRect(rect);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param p
	 *            DOCUMENT ME!
	 * @param tolerance
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 * @throws DriverException
	 *             DOCUMENT ME!
	 */
	public FBitSet queryByPoint(Point2D p, double tolerance)
			throws ReadDriverException, VisitorException {
		Strategy s = StrategyManager.getStrategy(this);

		return s.queryByPoint(p, tolerance);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param g
	 *            DOCUMENT ME!
	 * @param relationship
	 *            DOCUMENT ME!
	 *
	 * @return FBitset
	 */
	public FBitSet queryByShape(IGeometry g, int relationship)
			throws ReadDriverException, VisitorException {
		Strategy s = StrategyManager.getStrategy(this);

		return s.queryByShape(g, relationship);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws XMLException
	 *
	 * @see com.iver.cit.gvsig.fmap.layers.FLayer#getProperties()
	 */
	public XMLEntity getXMLEntity() throws XMLException {
		XMLEntity xml = super.getXMLEntity();
		xml.addChild(mapping.getXMLEntity());
		xml.putProperty("isInPixels", isInPixels());

		return xml;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.FLyrDefault#setXMLEntity(com.iver.utiles.XMLEntity)
	 */
	public void setXMLEntity(XMLEntity xml) throws XMLException {
		Iterator iter=xml.findChildren("className",MappingAnnotation.class.getName());
		if (iter.hasNext())
			mapping = MappingAnnotation.createFromXML((XMLEntity)iter.next());
		else{
			//Este else para versiones anteriores a la 1.0.2.(908)
			if (xml.getChildrenCount()==3)
				mapping = MappingAnnotation.createFromXML(xml.getChild(2));
			else
				mapping = MappingAnnotation.createFromXML(xml.getChild(3));
		}
		setInPixels(xml.getBooleanProperty("isInPixels"));

		CoordinateReferenceSystem crs = null;

		if (xml.contains("proj")) {
			crs = ProjectionUtils.getCRS(xml.getStringProperty("proj"));
		}

//		VectorialAdapter adapter=null;
//		if (xml.contains("file")){
//			adapter = new VectorialFileAdapter(new File(xml
//					.getStringProperty("file")));
//		}else if (xml.contains("db")){
//			adapter = new VectorialDBAdapter();
//		}
//		Driver d;
//
//		try {
//			d = LayerFactory.getDM().getDriver(
//					xml.getStringProperty("driverName"));
//		} catch (DriverLoadException e1) {
//			throw new XMLException(e1);
//		}
//
//		adapter.setDriver((VectorialDriver) d);
//		// TODO Meter esto dentro de la comprobación de si hay memoria
//		if (false) {
//		} else {
//			setSource(adapter);
//			setProjection(proj);
//		}

		// Le asignamos también una legenda por defecto acorde con
		// el tipo de shape que tenga. Tampoco sé si es aquí el
		// sitio adecuado, pero en fin....
		/*
		 * if (d instanceof WithDefaultLegend) { WithDefaultLegend aux =
		 * (WithDefaultLegend) d; adapter.start(); setLegend((VectorialLegend)
		 * aux.getDefaultLegend()); adapter.stop(); } else {
		 * setLegend(LegendFactory.createSingleSymbolLegend(getShapeType())); }
		 */

		super.setXMLEntity(xml);
		try {
			createLabels();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Esto tiene el fallo de que obligas a una etiqueta por entidad, para poder
	 * evitar esto, una posible solución sería que un FLabel pudiera ser una
	 * colección de FLabel (Patrón Composite)
	 *
	 * @param lyrVect
	 * @throws ReadDriverException
	 * @throws DriverException
	 */
	private void createLabels() throws ReadDriverException{
		SelectableDataSource ds = getRecordset();
		try {
			ReadableVectorial adapter = getSource();
			adapter.start();
			ds.start();
			int sc;
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

			sc = (int) ds.getRowCount();
			m_labels = new ArrayList(sc);
			DriverAttributes attr = adapter.getDriverAttributes();
			boolean bMustClone = false;
			if (attr != null) {
				if (attr.isLoadedInMemory()) {
					bMustClone = attr.isLoadedInMemory();
				}
			}
			MathTransform transform = getCrsTransform();
			FSymbol defaultSym = (FSymbol) getLegend().getDefaultSymbol();
			for (int i = 0; i < sc; i++) {
				IGeometry geom = adapter.getShape(i);

				if (geom == null) {
					m_labels.add(null);
					continue;
				}
				if (transform != null) {
					if (bMustClone)
						geom = geom.cloneGeometry();
					geom.reProject(transform);
				}

				// TODO: El método contenedor (createLabelLayer) debe recoger
				// los parámetros de posicionamiento y de allowDuplicates
				// if (i >= 328)
				// System.out.println("i= " + i + " " + val.toString());
				//ArrayList values=new ArrayList(4);
				String t=new String();
				Value val = ds.getFieldValue(i, fieldId);
				t=val.toString();
				//values.add(val);
				if (idFieldColorText!=-1){
					Value valColor=ds.getFieldValue(i,idFieldColorText);
					t=t.concat(valColor.toString());
					//values.add(valColor);
				}
				if (idFieldTypeFontText!=-1){
					Value valTypeFont=ds.getFieldValue(i,idFieldTypeFontText);
					t=t.concat(valTypeFont.toString());
					//values.add(valTypeFont);
				}

				if (idFieldStyleFontText!=-1){
					Value valStyleFont=ds.getFieldValue(i,idFieldStyleFontText);
					t=t.concat(valStyleFont.toString());
					//values.add(valStyleFont);
				}
				//Value total=ValueFactory.createValue((Value[])values.toArray(new Value[0]));
				Value total=ValueFactory.createValue(t);
				if ((val instanceof NullValue) || (val == null)) {
					m_labels.add(null);
					continue;
				}
				FLabel[] lbls = geom.createLabels(0, true);
				for (int j = 0; j < lbls.length; j++) {
					if (lbls[j] != null) {
						lbls[j].setString(val.toString());
						if (idFieldRotationText != -1) {
							NumericValue rotation = (NumericValue) ds
									.getFieldValue(i, idFieldRotationText);
							lbls[j].setRotation(rotation.doubleValue());
						} else {
							lbls[j].setRotation(defaultSym.getRotation());
						}

						float height;
						if (idFieldHeightText != -1) {
							NumericValue h = (NumericValue) ds
									.getFieldValue(i, idFieldHeightText);
							height=h.floatValue();
							lbls[j].setHeight(height);
						} else {
							height=defaultSym.getFontSize();
							lbls[j].setHeight(height);
						}



						if (vuvl.getSymbolByValue(total)==null){
							Color color;
							if (idFieldColorText != -1) {
								NumericValue c = (NumericValue) ds.getFieldValue(
										i, idFieldColorText);
								color=new Color(c.intValue());
							} else {
								color=defaultSym.getFontColor();
							}
							String typeFont;
							if (idFieldTypeFontText != -1) {
								StringValue tf = (StringValue) ds
										.getFieldValue(i, idFieldTypeFontText);
								typeFont=tf.getValue();
							} else {
								typeFont=defaultSym.getFont().getFontName();
							}
							int style;
							if (idFieldStyleFontText != -1) {
								IntValue sf = (IntValue) ds
										.getFieldValue(i, idFieldStyleFontText);
								style = sf.getValue();
							} else {
								style = defaultSym.getFont().getStyle();
							}
							//FSymbol symbol=new FSymbol(FConstant.SYMBOL_TYPE_TEXT);

							ITextSymbol symbol;

							symbol = SymbologyFactory.createDefaultTextSymbol();

							// casca perque ara és un ITextSymbol
//							symbol.setFontSizeInPixels(isInPixels());
							symbol.setFont(new Font(typeFont, style, (int)height));
							symbol.setDescription(lbls[j].getString());
							//symbol.setFontColor(color);
							symbol.setTextColor(color);
							vuvl.addSymbol(total,symbol);
						}

					}
				m_labels.add(lbls[j]);

				}
			}

			ds.stop();
			adapter.stop();
		} catch (ExpansionFileReadException e) {
			throw new ReadDriverException(getName(),e);
		} catch (InitializeDriverException e) {
			throw new ReadDriverException(getName(),e);
		}

	}

	public FLabel getLabel(int numReg) {
		if (m_labels == null || numReg == -1)
			return null;
		if (getSource() instanceof AnnotationEditableAdapter){
			AnnotationEditableAdapter aea=((AnnotationEditableAdapter)getSource());
			return aea.getLabel(numReg,false);
		}
		return (FLabel)m_labels.get(numReg);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.layers.layerOperations.RandomVectorialData#createIndex()
	 */
	public void createSpatialIndex() {
		// FJP: ESTO HABRÁ QUE CAMBIARLO. PARA LAS CAPAS SECUENCIALES, TENDREMOS
		// QUE ACCEDER CON UN WHILE NEXT. (O mejorar lo de los FeatureVisitor
		// para que acepten recorrer sin geometria, solo con rectangulos.

		//AZABALA: Como no tengo claro de donde se crean las capas de textos
		//el índice espacial creado seguirá siendo el Quadtree en memoria
		//de JTS (QuadtreeJts es un adapter a nuestra api de indices)
		spatialIndex = new QuadtreeJts();
		ReadableVectorial va = getSource();
		MathTransform transform = getCrsTransform();
		BoundedShapes shapeBounds = (BoundedShapes) va.getDriver();
		try {
			va.start();

			for (int i = 0; i < va.getShapeCount(); i++) {
				Rectangle2D r = null;
				FLabel label=getLabel(i);
				if (label != null) {
					r = label.getBoundBox();
				} else {
					r = shapeBounds.getShapeBounds(i);
				}
				// TODO: MIRAR COMO SE TRAGARÍA ESTO LO DE LAS REPROYECCIONES
				if (transform != null) {
					r = ProjectionUtils.transform(r, transform);
				}
				if (r != null) {
//					Coordinate c1 = new Coordinate(r.getMinX(), r.getMinY());
//					Coordinate c2 = new Coordinate(r.getMaxX(), r.getMaxY());
//					Envelope env = new Envelope(c1, c2);
//					spatialIndex.insert(env, new Integer(i));
					spatialIndex.insert(r, i);
				}
			} // for
			va.stop();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		} 
	}

	public void setSelectedEditing() throws ReadDriverException {
		FBitSet bitSet = getRecordset().getSelection();
		if (bitSet.cardinality() == 0)
			return;
		indexEditing = bitSet.nextSetBit(0);
	}

	public void setInPixels(boolean b) {
		inPixels = b;
	}

	public boolean isInPixels() {
		return inPixels;
	}

	public void setInEdition(int i) {
		indexEditing = i;

	}

	public int getInEdition() {
		return indexEditing;
	}

	public ArrayList getLabels() {
		return m_labels;
	}


	public void setLegend() {
		try {
			getSource().getRecordset().start();
			vuvl.setClassifyingFieldNames(
					new String[] {
							getSource().getRecordset().getFieldName(mapping.getColumnText())
					}
			);

//			vuvl.setDefaultSymbol(new FSymbol(FConstant.SYMBOL_TYPE_TEXT));
			vuvl.setDefaultSymbol(SymbologyFactory.createDefaultTextSymbol());
			setLegend((IVectorLegend) vuvl);
			getSource().getRecordset().stop();
		} catch (LegendLayerException e) {
			e.printStackTrace();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}

	}

	public Strategy getStrategy() {
		return strategy;
	}
	public void setEditing(boolean b) throws StartEditionLayerException {
		super.setEditing(b);
		deleteSpatialIndex();
	}

	public static FLayer createLayerFromVect(FLyrVect layer) throws ReadDriverException, LegendLayerException{
		FLyrAnnotation la=new FLyrAnnotation();
		la.setSource(layer.getSource());
		la.setRecordset(layer.getRecordset());
		la.setCrs(layer.getCrs());
		la.setLegend((IVectorLegend)layer.getLegend());
		return la;
	}
}
