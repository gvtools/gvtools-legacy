/* ALL THIS WORK ABOUT DGN IS BASED IN DGNLib, from Frank Warmerdam
 * It is a java port, and is incompleted, just to let gvSIG
 * display DGN's. His DGNLib is more advanced, and must be
 * used as reference.
 * DGNLib:  http://dgnlib.maptools.org/
 *
 * (Thanks, Frank :). For this, and for your very good job with
 * shapefiles. And also for GDAL!!!!
 */

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.fmap.drivers.dgn;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.io.File;
import java.sql.Types;
import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.OpenDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FPoint3D;
import com.iver.cit.gvsig.fmap.core.FPolygon2D;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IFillSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.MemoryDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.VectorialUniqueValueLegend;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.AttrInTableLabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;


/**
 * Driver DGN que trabaja directamente cargando el fichero en memoria.
 *
 * @author Vicente Caballero Navarro
 */
public class DgnMemoryDriver extends MemoryDriver implements VectorialFileDriver, WithDefaultLegend {
	private final int ID_FIELD_ID = 0;
	private final int ID_FIELD_ENTITY = 1;
	private final int ID_FIELD_LAYER = 2;
	private final int ID_FIELD_COLOR = 3;
	private final int ID_FIELD_HEIGHTTEXT = 4;
	private final int ID_FIELD_ROTATIONTEXT = 5;
	private final int ID_FIELD_TEXT = 6;
	DGNReader m_DgnReader;
	VectorialUniqueValueLegend defaultLegend;
	private String path;
	private File m_Fich;
	private DriverAttributes attr = new DriverAttributes();
	private ILabelingStrategy labeler;

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#open(java.io.File)
	 */
	public void open(File f) throws OpenDriverException {
		m_Fich = f;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#initialize()
	 */
	public void initialize() throws ReadDriverException{
		float heightText = 10;
		attr.setLoadedInMemory(true);

		m_DgnReader = new DGNReader(m_Fich.getAbsolutePath());

		Value[] auxRow = new Value[7];
		Value[] cellRow = new Value[7];
		Value[] complexRow = new Value[7];
		ArrayList arrayFields = new ArrayList();
		arrayFields.add("ID");
		arrayFields.add("Entity");
		arrayFields.add("Layer");
		arrayFields.add("Color");
		arrayFields.add("HeightText");
		arrayFields.add("RotationText");
		arrayFields.add("Text");

		getTableModel().setColumnIdentifiers(arrayFields.toArray());

		// jaume
		labeler = new AttrInTableLabelingStrategy();
		((AttrInTableLabelingStrategy) labeler).setTextFieldId(arrayFields.indexOf("Text"));
		((AttrInTableLabelingStrategy) labeler).setRotationFieldId(arrayFields.indexOf("RotationText"));
		((AttrInTableLabelingStrategy) labeler).setHeightFieldId(arrayFields.indexOf("HeightText"));
		((AttrInTableLabelingStrategy) labeler).setUnit(1); //MapContext.NAMES[1] (meters)


		// Ahora las rellenamos.
		FShape aux;
		boolean bElementoCompuesto = false;
		boolean bEsPoligono = false;
		boolean bInsideCell = false;
		boolean bFirstHoleEntity = false;
		boolean bConnect = false; // Se usa para que los polígonos cierren bien cuando son formas compuestas
		int contadorSubElementos = 0;
		int numSubElementos = 0;
		int complex_index_fill_color = -1;
		int nClass; // Para filtrar los elementos de construcción, etc.
		GeneralPathX elementoCompuesto = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD);

		for (int id = 0; id < m_DgnReader.getNumEntities(); id++) {
			// System.out.println("Elemento " + id + " de " + m_DgnReader.getNumEntities());
			m_DgnReader.DGNGotoElement(id);

			DGNElemCore elemento = m_DgnReader.DGNReadElement();
			nClass = 0;
			auxRow[ID_FIELD_HEIGHTTEXT] = ValueFactory.createValue(0);
			auxRow[ID_FIELD_ROTATIONTEXT] = ValueFactory.createValue(0);
			auxRow[ID_FIELD_TEXT] = ValueFactory.createNullValue();

			if (elemento.properties != 0) {
				nClass = elemento.properties & DGNFileHeader.DGNPF_CLASS;
			}

			if ((elemento != null) && (elemento.deleted == 0) && (nClass == 0)) //Leer un elemento
			 {
				aux = null;

				// if ((elemento.element_id > 3800) && (elemento.element_id < 3850))
				// 	m_DgnReader.DGNDumpElement(m_DgnReader.getInfo(),elemento,"");
				if ((elemento.stype == DGNFileHeader.DGNST_MULTIPOINT) ||
						(elemento.stype == DGNFileHeader.DGNST_ARC) ||
						(elemento.stype == DGNFileHeader.DGNST_CELL_HEADER) ||
						(elemento.stype == DGNFileHeader.DGNST_SHARED_CELL_DEFN) ||
						(elemento.stype == DGNFileHeader.DGNST_COMPLEX_HEADER)) {
					if (elemento.complex != 0) {
						bElementoCompuesto = true;
					} else {
						if (bElementoCompuesto) {
							if (bInsideCell) {
								auxRow[ID_FIELD_ENTITY] = cellRow[ID_FIELD_ENTITY];
							} else {
								auxRow = complexRow;
							}

							// System.err.println("Entidad compuesta. bInsideCell = " + bInsideCell + " auxRow = " + auxRow[ID_FIELD_ENTITY]);
							addShape(new FPolyline2D(elementoCompuesto), auxRow);

							if (bEsPoligono) {
								if (complex_index_fill_color != -1) {
									auxRow[ID_FIELD_COLOR] = ValueFactory.createValue(complex_index_fill_color);
								}

								addShape(new FPolygon2D(elementoCompuesto),
									auxRow);
							}

							elementoCompuesto = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD);
						}

						// System.err.println("Entidad simple");
						bElementoCompuesto = false;
						bEsPoligono = false;
						bConnect = false;

						// elementoCompuesto = new GeneralPathX();
						bInsideCell = false;
					}
				}

				switch (elemento.stype) {
					case DGNFileHeader.DGNST_SHARED_CELL_DEFN:
						bInsideCell = true;
						cellRow[ID_FIELD_ID] = ValueFactory.createValue(elemento.element_id);
						cellRow[ID_FIELD_LAYER] = ValueFactory.createValue(elemento.level);
						cellRow[ID_FIELD_COLOR] = ValueFactory.createValue(elemento.color);
						cellRow[ID_FIELD_ENTITY] = ValueFactory.createValue(
								"Shared Cell");

						break;

					case DGNFileHeader.DGNST_CELL_HEADER:
						bInsideCell = true;

						DGNElemCellHeader psCellHeader = (DGNElemCellHeader) elemento;
						cellRow[ID_FIELD_ID] = ValueFactory.createValue(elemento.element_id);
						cellRow[ID_FIELD_LAYER] = ValueFactory.createValue(elemento.level);
						cellRow[ID_FIELD_COLOR] = ValueFactory.createValue(elemento.color);
						cellRow[ID_FIELD_ENTITY] = ValueFactory.createValue(
								"Cell");
						complex_index_fill_color = m_DgnReader.DGNGetShapeFillInfo(elemento);

						// System.err.println("Cell Header " + complex_index_fill_color);
						break;

					case DGNFileHeader.DGNST_COMPLEX_HEADER:

						// bElementoCompuesto = true;
						// System.err.println("Complex Header");
						contadorSubElementos = 0;

						DGNElemComplexHeader psComplexHeader = (DGNElemComplexHeader) elemento;
						numSubElementos = psComplexHeader.numelems;
						complexRow[ID_FIELD_ID] = ValueFactory.createValue(elemento.element_id);
						complexRow[ID_FIELD_LAYER] = ValueFactory.createValue(elemento.level);
						complexRow[ID_FIELD_COLOR] = ValueFactory.createValue(elemento.color);
						complexRow[ID_FIELD_ENTITY] = ValueFactory.createValue(
								"Complex");

						if (psComplexHeader.type == DGNFileHeader.DGNT_COMPLEX_SHAPE_HEADER) {
							bEsPoligono = true;

							// Si es un agujero, no conectamos con el anterior
							if ((psComplexHeader.properties & 0x8000) != 0) {
								bFirstHoleEntity = true;
							} else {
								// Miramos si tiene color de relleno
								// complex_index_fill_color = -1;
								// if (elemento.attr_bytes > 0) {
								complex_index_fill_color = m_DgnReader.DGNGetShapeFillInfo(elemento);

								// System.err.println("complex shape fill color = " + elemento.color);
								// }
							}

							bConnect = true;
						} else {
							bEsPoligono = false;
							bConnect = false;
						}

						break;

					case DGNFileHeader.DGNST_MULTIPOINT:

						// OJO: Si lo que viene en este multipoint es un elemento con type=11 (curve), se trata de una "parametric
						// spline curve". La vamos a tratar como si no fuera curva, pero según la documentación, los 2 primeros puntos
						// y los 2 últimos puntos definen "endpoint derivatives" y NO se muestran.
						// TODAVÍA HAY UN PEQUEÑO FALLO CON EL FICHERO dgn-sample.dgn, pero lo dejo por ahora.
						// Es posible que tenga que ver con lo de los arcos (arco distorsionado), que
						// todavía no está metido.
						DGNElemMultiPoint psLine = (DGNElemMultiPoint) elemento;
						auxRow[ID_FIELD_ID] = ValueFactory.createValue(elemento.element_id);
						auxRow[ID_FIELD_ENTITY] = ValueFactory.createValue(
								"Multipoint");
						auxRow[ID_FIELD_LAYER] = ValueFactory.createValue(elemento.level);
						auxRow[ID_FIELD_COLOR] = ValueFactory.createValue(elemento.color);

						if ((psLine.num_vertices == 2) &&
								(psLine.vertices[0].x == psLine.vertices[1].x) &&
								(psLine.vertices[0].y == psLine.vertices[1].y)) {
							auxRow[ID_FIELD_ENTITY] = ValueFactory.createValue(
									"Point");
							addShape(new FPoint3D(psLine.vertices[0].x,
									psLine.vertices[0].y, psLine.vertices[0].z),
								auxRow);
						} else {
							GeneralPathX elShape = new GeneralPathX(GeneralPathX.WIND_EVEN_ODD);

							if (psLine.type == DGNFileHeader.DGNT_CURVE) {
								psLine.num_vertices = psLine.num_vertices - 4;

								for (int aux_n = 0;
										aux_n < psLine.num_vertices; aux_n++) {
									psLine.vertices[aux_n] = psLine.vertices[aux_n +
										2];
								}
							}

							if ((psLine.type == DGNFileHeader.DGNT_SHAPE) &&
									((psLine.properties & 0x8000) != 0)) {
								// Invertimos el orden porque es un agujero
								elShape.moveTo(psLine.vertices[psLine.num_vertices -
									1].x,
									psLine.vertices[psLine.num_vertices - 1].y);

								for (int i = psLine.num_vertices - 2; i >= 0;
										i--)
									elShape.lineTo(psLine.vertices[i].x,
										psLine.vertices[i].y);
							} else {
								elShape.moveTo(psLine.vertices[0].x,
									psLine.vertices[0].y);

								for (int i = 1; i < psLine.num_vertices; i++)
									elShape.lineTo(psLine.vertices[i].x,
										psLine.vertices[i].y);
							}

							if ((psLine.vertices[0].x == psLine.vertices[psLine.num_vertices -
									1].x) &&
									(psLine.vertices[0].y == psLine.vertices[psLine.num_vertices -
									1].y)) {
								// Lo añadimos también como polígono
								bEsPoligono = true;

								// Miramos si tiene color de relleno
								if (elemento.attr_bytes > 0) {
									elemento.color = m_DgnReader.DGNGetShapeFillInfo(elemento);

									// System.err.println("fill color = " + elemento.color);
									if (elemento.color != -1) {
										auxRow[ID_FIELD_COLOR] = ValueFactory.createValue(elemento.color);
									}
								}

								if (elemento.complex == 0) {
									addShape(new FPolygon2D(elShape), auxRow);
								}
							}

							if (elemento.complex != 0) {
								// Si es un agujero o
								// es la primera entidad del agujero, lo añadimos sin unir al anterior
								if (bFirstHoleEntity ||
										((psLine.type == DGNFileHeader.DGNT_SHAPE) &&
										((psLine.properties & 0x8000) != 0))) {
									elementoCompuesto.append(elShape, false);
									bFirstHoleEntity = false;
								} else {
									elementoCompuesto.append(elShape, bConnect);
								}
							} else {
								addShape(new FPolyline2D(elShape), auxRow);
							}
						}

						break;

					case DGNFileHeader.DGNST_ARC:

						// m_DgnReader.DGNDumpElement(m_DgnReader.getInfo(), elemento,"");
						DGNElemArc psArc = (DGNElemArc) elemento;

						// La definición de arco de MicroStation es distinta a la de Java.
						// En el dgn el origin se entiende que es el centro del arco,
						// y a la hora de crear un Arc2D las 2 primeras coordenadas son
						// la esquina inferior izquierda del rectángulo que rodea al arco.
						// 1.- Creamos la elipse sin rotación.
						// 2.- Creamos el arco
						// 3.- Rotamos el resultado

						/* System.out.println("Arco con primari axis: " + psArc.primary_axis +
						   " start angle: " + psArc.startang + " sweepang = " + psArc.sweepang);
						   System.out.println("secondaria axis: " + psArc.secondary_axis +
						                                    " rotation = " + psArc.rotation); */
						AffineTransform mT = AffineTransform.getRotateInstance(Math.toRadians(
									psArc.rotation), psArc.origin.x,
								psArc.origin.y);

						// mT.preConcatenate(AffineTransform.getScaleInstance(100.0,100.0));
						Arc2D.Double elArco = new Arc2D.Double(psArc.origin.x -
								psArc.primary_axis,
								psArc.origin.y - psArc.secondary_axis,
								2.0 * psArc.primary_axis,
								2.0 * psArc.secondary_axis, -psArc.startang,
								-psArc.sweepang, Arc2D.OPEN);

						// Ellipse2D.Double elArco = new Ellipse2D.Double(psArc.origin.x - psArc.primary_axis,
						// 		psArc.origin.y - psArc.secondary_axis,2.0 * psArc.primary_axis, 2.0 * psArc.secondary_axis);
						GeneralPathX elShapeArc = new GeneralPathX(elArco);

						// Transformamos el GeneralPahtX porque si transformamos elArco nos lo convierte
						// a GeneralPath y nos guarda las coordenadas en float, con la correspondiente pérdida de precisión
						elShapeArc.transform(mT);

						if (m_DgnReader.getInfo().dimension == 3) {
							//Aquí podríamos hacer cosas con la coordenada Z
						}

						auxRow[ID_FIELD_ID] = ValueFactory.createValue(elemento.element_id);
						auxRow[ID_FIELD_ENTITY] = ValueFactory.createValue(
								"Arc");
						auxRow[ID_FIELD_LAYER] = ValueFactory.createValue(elemento.level);
						auxRow[ID_FIELD_COLOR] = ValueFactory.createValue(elemento.color);

						/* Line2D.Double ejeMayor = new Line2D.Double(psArc.origin.x - psArc.primary_axis, psArc.origin.y,
						   psArc.origin.x + psArc.primary_axis, psArc.origin.y);

						   lyrLines.addShape(new FShape(FConstant.SHAPE_TYPE_POLYLINE, new GeneralPathX(ejeMayor)), auxRow); */

						// lyrLines.addShape(new FShape(FConstant.SHAPE_TYPE_POLYLINE, elShapeArc), auxRow);
						if (elemento.complex != 0) {
							// Esto es una posible fuente de fallos si detrás de una
							// elipse vienen más cosas pegadas. Deberíamos volver
							// a conectar una vez pasada la elipse.
							if (elemento.type == DGNFileHeader.DGNT_ELLIPSE) {
								bConnect = false;
							}

							// SI LA ELIPSE ES UN AGUJERO, SE AÑADE SIN PEGAR
							// Y EL ELEMENTO ES UN POLIGONO
							if (bFirstHoleEntity ||
									((elemento.type == DGNFileHeader.DGNT_SHAPE) &&
									((elemento.properties & 0x8000) != 0))) {
								elementoCompuesto.append(elShapeArc, false);
								bFirstHoleEntity = false;
							} else {
								elementoCompuesto.append(elShapeArc, bConnect);
							}
						} else {
							addShape(new FPolyline2D(elShapeArc), auxRow);

							if (psArc.type == DGNFileHeader.DGNT_ELLIPSE) {
								addShape(new FPolygon2D(elShapeArc), auxRow);
							}
						}

						// System.err.println("Entra un Arco");
						break;

					case DGNFileHeader.DGNST_TEXT:

						DGNElemText psText = (DGNElemText) elemento;
						FPoint2D elShapeTxt = new FPoint3D(psText.origin.x,
								psText.origin.y, psText.origin.z);

						auxRow[ID_FIELD_ID] = ValueFactory.createValue(elemento.element_id);
						auxRow[ID_FIELD_ENTITY] = ValueFactory.createValue(
								"Text");
						auxRow[ID_FIELD_LAYER] = ValueFactory.createValue(elemento.level);
						auxRow[ID_FIELD_COLOR] = ValueFactory.createValue(elemento.color);
						heightText = (float) psText.height_mult;
						auxRow[ID_FIELD_HEIGHTTEXT] = ValueFactory.createValue(heightText);
						auxRow[ID_FIELD_ROTATIONTEXT] = ValueFactory.createValue(psText.rotation);
						auxRow[ID_FIELD_TEXT] = ValueFactory.createValue(psText.string); // .trim();
						addShape(elShapeTxt, auxRow);

						// System.out.println("Rotación texto: " + psText.rotation + "Altura Texto = " + heightText);

						/* System.out.println("  origin=(" + psText.origin.x +
						   ", " + psText.origin.y + ") rotation=" +
						   psText.rotation + "\n" + "  font=" +
						   psText.font_id + " just=" +
						   psText.justification + "length_mult=" +
						   psText.length_mult + " height_mult=" +
						   psText.height_mult + "\n" + "  string =" +
						   new String(psText.string).toString().trim() +
						   "\n"); */
						break;

					/* default:
					   m_DgnReader.DGNDumpElement(m_DgnReader.getInfo(), elemento, "");
					 */
				} // switch
			} // if
		} // for

		if (bElementoCompuesto) {
			if (bInsideCell) {
				auxRow = cellRow;
			} else {
				auxRow = complexRow;
			}

			// System.err.println("Entidad compuesta. bInsideCell = " + bInsideCell + " auxRow = " + auxRow[ID_FIELD_ENTITY]);
			addShape(new FPolyline2D(elementoCompuesto), auxRow);

			if (bEsPoligono) {
				if (complex_index_fill_color != -1) {
					auxRow[ID_FIELD_COLOR] = ValueFactory.createValue(complex_index_fill_color);
				}

				addShape(new FPolygon2D(elementoCompuesto), auxRow);
			}
		}

		defaultLegend = LegendFactory.createVectorialUniqueValueLegend(getShapeType());
		defaultLegend.setClassifyingFieldNames(new String[] {"Color"} );
		defaultLegend.setClassifyingFieldTypes(new int[]{Types.INTEGER,Types.VARCHAR,Types.INTEGER,Types.INTEGER,Types.FLOAT,Types.DOUBLE,Types.VARCHAR});


		ISymbol myDefaultSymbol = SymbologyFactory.
			createDefaultSymbolByShapeType(getShapeType());

		defaultLegend.setDefaultSymbol(myDefaultSymbol);


		ObjectDriver rs = this;
		IntValue clave;
		ISymbol theSymbol = null;

		try {
		    // TODO: Provisional hasta que cambiemos los símbolos.
		    /* BufferedImage bi= new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		   Graphics2D big = bi.createGraphics();
		   Color color=new Color(0,0,0,0);
		   big.setBackground(color);
		   big.clearRect(0, 0, 1, 1);
		   Paint fillProv = null;
		   Rectangle2D rProv = new Rectangle();
		   rProv.setFrame(0, 0,1,1);
		   fillProv = new TexturePaint(bi,rProv); */
			for (long j = 0; j < rs.getRowCount(); j++) {
				clave = (IntValue) rs.getFieldValue(j, ID_FIELD_COLOR);
				if (defaultLegend.getSymbolByValue(clave) == null) {
//
//					theSymbol = new FSymbol(getShapeType());
//					theSymbol.setDescription(clave.toString());
//					Color c = m_DgnReader.DGNLookupColor(
//							clave.getValue());
//					// Le ponemos transparencia para que los polígonos no
//					// tapen del todo. (Está dentro del DGNLookupColor
//					// c.
//					theSymbol.setColor(c);
//					// theSymbol.setFill(fillProv);
//					theSymbol.setStyle(FConstant.SYMBOL_STYLE_DGNSPECIAL);
//					theSymbol.setSize(3);
//					theSymbol.setSizeInPixels(true);

					Color c = m_DgnReader.DGNLookupColor(
							clave.getValue());
					theSymbol =	SymbologyFactory.
						createDefaultSymbolByShapeType(getShapeType(), c);
					theSymbol.setDescription(clave.toString());

					if (theSymbol instanceof IMarkerSymbol) {
						((IMarkerSymbol) theSymbol).setSize(1);
					}

					if (theSymbol instanceof ILineSymbol) {
						((ILineSymbol) theSymbol).setLineWidth(1);
					}

					if (theSymbol instanceof IFillSymbol) {
						((IFillSymbol) theSymbol).getOutline().setLineColor(c);
						((IFillSymbol) theSymbol).getOutline().setLineWidth(1);
						((IFillSymbol) theSymbol).setFillColor(null);
					}

					// theSymbol.setStyle(FConstant.SYMBOL_STYLE_FILL_TRANSPARENT);
					defaultLegend.addSymbol(clave, theSymbol);
				}

				if ("Text".equalsIgnoreCase(((StringValue) rs.getFieldValue(j, ID_FIELD_ENTITY)).toString())) {

				}


			} // for
		} catch (ReadDriverException e) {
			throw new InitializeDriverException(getName(),e);
		}
	}

	/**
	 * Devuelve el tipo de shape que contiene el formato DGN.
	 *
	 * @return Entero que representa el tipo de shape.
	 */
	public int getShapeType() {
		return FShape.MULTI;
	}

	/**
	 * @see com.hardcode.driverManager.Driver#getType()
	 */
	public String getName() {
		return "gvSIG DGN Memory Driver";
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#accept(java.io.File)
	 */
	public boolean accept(File f) {
		return f.getName().toUpperCase().endsWith("DGN");
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend#getDefaultLegend()
	 */
	public ILegend getDefaultLegend() {
		return defaultLegend;
	}

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialDriver#getDriverAttributes()
     */
    public DriverAttributes getDriverAttributes() {
        return attr;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getPrimaryKeys()
     */
    public int[] getPrimaryKeys() throws ReadDriverException {
        return null;
    }

    /**
     * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#write(com.hardcode.gdbms.engine.data.edition.DataWare)
     */
    public void write(DataWare arg0) throws WriteDriverException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver#close()
     */
    public void close() {
        // TODO Auto-generated method stub

    }

	public File getFile() {
		return m_Fich;
	}

	public boolean isWritable() {
		return m_Fich.canWrite();
	}

	public ILabelingStrategy getDefaultLabelingStrategy() {
		return labeler;
	}
	public int getFieldType(int i) {
	    DefaultTableModel dtm=getTableModel();
		String columnName=dtm.getColumnName(i);
	    if (columnName.equals("ID")){
	    	return Types.INTEGER;
	    }else if (columnName.equals("Entity")){
	    	return Types.VARCHAR;
	    }else if (columnName.equals("Layer")){
	    	return Types.INTEGER;
	    }else if (columnName.equals("Color")){
	    	return Types.INTEGER;
	    }else if (columnName.equals("HeightText")){
	    	return Types.FLOAT;
	    }else if (columnName.equals("RotationText")){
	    	return Types.DOUBLE;
	    }else if (columnName.equals("Text")){
	    	return Types.VARCHAR;
	    }else{
	    	return Types.VARCHAR;
	    }
	}
}
