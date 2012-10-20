package com.iver.cit.gvsig.geoprocess.impl.clean.fmap;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import org.gvsig.exceptions.BaseException;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.FileNotFoundDriverException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.validate.ValidateRowException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FGeometry;
import com.iver.cit.gvsig.fmap.core.FPolyline2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.dbf.DbaseFile;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.MultiShpWriter;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.operations.strategies.DefaultStrategy;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.DefinitionUtils;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.geoprocess.core.gui.AddResultLayerTask;
import com.iver.cit.gvsig.geoprocess.impl.build.fmap.BuildGeoprocess;
import com.iver.cit.gvsig.geoprocess.util.StartEditingClean;
import com.iver.cit.gvsig.geoprocess.util.StopEditingClean;
import com.iver.cit.gvsig.project.documents.view.gui.IView;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.util.SnappingCoordinateMap;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.iver.utiles.swing.threads.IPipedTask;
import com.iver.utiles.swing.threads.MonitorableDecoratorMainFirst;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.MultiLineString;

public class CleanGeoprocess extends AbstractGeoprocess {

	private boolean onlyFirstLayerSelection = false;

	private boolean createLyrsWithErrorGeometries = false;

	private double fuzzyTol = 0;

	private double dangleTol = 0;

	private boolean fromBuild;

	private IWriter intersectionsWriter;

	private IWriter dangleWriter;

	private FeaturePersisterProcessor2 processor;

	private FeaturePersisterProcessor2 intersectsProcessor;

	private FeaturePersisterProcessor2 dangleProcessor;

	private ILayerDefinition resultLayerDefinition;

	private FLayer resultLayer = null;

	private boolean getOnlyFirstResultLayer = false;

	private String resultLayerName;

	private static Preferences prefs = Preferences.userRoot().node(
			"gvSIG.encoding.dbf");

	public CleanGeoprocess(FLyrVect inputLayer) {
		this.firstLayer = inputLayer;
	}

	@Override
	public void process() throws GeoprocessException {
		try {
			createTask().run();
		} catch (Exception e) {
			throw new GeoprocessException("Error al ejecutar el geoproceso", e);
		}
	}

	@Override
	public void checkPreconditions() throws GeoprocessException {
		if (firstLayer == null)
			throw new GeoprocessException("CLEAN: capa de entrada a null");
		if (this.writer == null || this.schemaManager == null) {
			throw new GeoprocessException(
					"Operacion de CLEAN sin especificar capa de resultados");
		}
		try {
			if (firstLayer.getSource().getShapeCount() == 0) {
				throw new GeoprocessException("Capa de entrada vacia");
			}
		} catch (ReadDriverException e) {
			throw new GeoprocessException(
					"Error al verificar si la capa est� vac�a");
		}
	}

	@Override
	public ILayerDefinition createLayerDefinition() {
		if (resultLayerDefinition == null) {
			try {
				resultLayerDefinition = DefinitionUtils
						.createLayerDefinition(firstLayer);
				FieldDescription[] fields = resultLayerDefinition
						.getFieldsDesc();
				FieldDescription[] newFields = new FieldDescription[fields.length + 2];
				for (int indexField = 0; indexField < fields.length; indexField++) {
					newFields[indexField] = fields[indexField];
				}// for
				FieldDescription newField1 = new FieldDescription();
				newField1.setFieldType(Types.INTEGER);
				newField1.setFieldName("OldID");
				newFields[newFields.length - 2] = newField1;
				FieldDescription newField2 = new FieldDescription();
				newField2.setFieldType(Types.INTEGER);
				newField2.setFieldName("NewID");
				newFields[newFields.length - 1] = newField2;
				resultLayerDefinition.setFieldsDesc(newFields);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resultLayerDefinition;
	}

	@Override
	public void setParameters(Map params) throws GeoprocessException {
		String resultLayerName = (String) params.get("resultlayername");
		if (resultLayerName != null)
			this.resultLayerName = resultLayerName;

		Boolean firstLayerSelection = (Boolean) params
				.get("firstlayerselection");
		if (firstLayerSelection != null)
			this.onlyFirstLayerSelection = firstLayerSelection.booleanValue();

		Boolean createLyrsWithError = (Boolean) params
				.get("createlayerswitherrors");
		if (createLyrsWithError != null)
			this.createLyrsWithErrorGeometries = createLyrsWithError
					.booleanValue();

		Double fuzzyTolerance = (Double) params.get("fuzzyTolerance");
		if (fuzzyTolerance != null)
			fuzzyTol = fuzzyTolerance.doubleValue();

		Double dangleTolerance = (Double) params.get("dangleTolerance");
		if (dangleTolerance != null)
			dangleTol = dangleTolerance.doubleValue();

		Boolean buildAfter = (Boolean) params.get("cleanbefore");
		if (buildAfter != null)
			fromBuild = buildAfter;
	}

	public IMonitorableTask createTask() {
		return new LineCleanTask();
	}

	public FLayer getResult() throws GeoprocessException {
		if (getOnlyFirstResultLayer) {
			if (resultLayer == null)
				resultLayer = (FLyrVect) createLayerFrom(this.writer);
		} else {
			FLyrVect pseudonodes = null;
			FLyrVect dangleLines = null;
			resultLayer = (FLyrVect) createLayerFrom(this.writer);

			if (this.createLyrsWithErrorGeometries) {
				MapContext map = ((View) PluginServices.getMDIManager()
						.getActiveWindow()).getModel().getMapContext();
				FLayers solution = new FLayers();
				solution.setMapContext(map);
				solution.setName(this.firstLayer.getName() + "_cleaned");

				pseudonodes = (FLyrVect) createLayerFrom(this.intersectionsWriter);
				SimpleMarkerSymbol symIntNode = (SimpleMarkerSymbol) SymbologyFactory
						.createDefaultSymbolByShapeType(FShape.POINT, Color.RED);

				dangleLines = (FLyrVect) createLayerFrom(this.dangleWriter);
				ILineSymbol symDangleEdge = (ILineSymbol) SymbologyFactory
						.createDefaultSymbolByShapeType(FShape.LINE, Color.BLUE);

				try {
					if (pseudonodes.getSource().getShapeCount() != 0) {
						solution.addLayer(pseudonodes);
						symIntNode.setSize(8);
						pseudonodes
								.setLegend(new SingleSymbolLegend(symIntNode));
					}

					if (dangleLines.getSource().getShapeCount() != 0) {
						solution.addLayer(dangleLines);
						dangleLines.setLegend(new SingleSymbolLegend(
								symDangleEdge));
					}
					solution.addLayer(resultLayer);
					return solution;
				} catch (ReadDriverException e) {
					throw new GeoprocessException("Error de lectura de datos");
				} catch (LegendLayerException e) {
					e.printStackTrace();
				}
			}// if
		}

		return resultLayer;
	}

	public MapContext getMapContext() {
		IWindow[] windows = PluginServices.getMDIManager().getAllWindows();
		IView view = null;
		for (int i = 0; i < windows.length; i++) {
			if (windows[i] instanceof IView)
				view = (IView) windows[i];
		}
		return view.getMapControl().getMapContext();
	}

	private class LineCleanTask extends AbstractMonitorableTask implements
			IPipedTask {

		private List<Point2D> intersections = null;

		private LineCleanTask() {
			setInitialStep(0);
			try {
				if (onlyFirstLayerSelection) {
					int numSelected = firstLayer.getRecordset().getSelection()
							.cardinality();
					setFinalStep(numSelected * 2);
				} else {
					int numShapes = firstLayer.getSource().getShapeCount();
					setFinalStep(numShapes * 2);
				}// else
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
			setDeterminatedProcess(true);
			setStatusMessage(PluginServices.getText(this,
					"LineClean._Progress_Message"));

		}

		/**
		 * Verifies cancelation events, and return a boolean flag if processes
		 * must be stopped for this cancelations events.
		 * 
		 * @param cancel
		 * @param va
		 * @param visitor
		 * @return
		 * @throws DriverIOException
		 */
		protected boolean verifyCancelation(ReadableVectorial va) {
			if (isCanceled()) {
				try {
					va.stop();
				} finally {
					return true;
				}
			}
			return false;
		}

		/**
		 * Recorre todas las geometrias de la capa para fusionar sus nodos en
		 * caso de que sea necesario
		 * 
		 * @param layer
		 *            - capa a 'corregir'
		 */
		private void mergeNodes(FLyrVect layer) {
			try {
				ReadableVectorial rv = layer.getSource();
				FBitSet selection = null;
				if (onlyFirstLayerSelection)
					selection = firstLayer.getRecordset().getSelection();
				rv.start();
				for (int i = 0; i < rv.getShapeCount(); i++) {
					if (!isCanceled()) {
						reportStep();
						/*
						 * Se itera para cada registro de la capa (se controla
						 * tb si el geoproceso solo debe actuar sobre los
						 * seleccionados)
						 */
						boolean ahead = false;
						if (selection != null) {
							if (selection.get(i))
								ahead = true;
						} else
							ahead = true;
						if (ahead) {
							// Se tratan todos los vertices de la polilinea
							IGeometry geom = rv.getShape(i);
							FShape shape = (FShape) geom.getInternalShape();
							for (int j = 0; j < shape.getSelectHandlers().length; j++) {
								Point2D point = shape.getSelectHandlers()[j]
										.getPoint();
								mergeNode(layer, point, i, j);
							}
						}
					}// if
				}// for
				rv.stop();
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Fusiona el nodo que se encuentra en las coordenadas del punto 'p' con
		 * las geometrias que se encuentren dentro de un radio menor a la
		 * tolerancia fuzzy
		 * 
		 * @param layer
		 *            - capa clonada de la original sobre la que se realiza el
		 *            geoproceso
		 * @param p
		 *            - Punto del registro que se esta analizando
		 * @param id
		 *            - Identificador en la capa del registro que se esta
		 *            analizando
		 * @param indHandler
		 *            - indice del vertice (punto) en la polilinea
		 */
		private void mergeNode(FLyrVect layer, Point2D p, int id, int indHandler) {
			try {
				ReadableVectorial rv = layer.getSource();
				/*
				 * Primero hay que buscar una posible fusion con las
				 * intersecciones encontradas hasta ahora
				 */
				if (intersections != null) {
					for (int i = 0; i < intersections.size(); i++) {
						if (p.distance(intersections.get(i)) < fuzzyTol) {
							Coordinate coord = new Coordinate(intersections
									.get(i).getX(), intersections.get(i).getY());
							updateFeature(rv, layer, coord, id, indHandler);
							return;
						}
					}
				}
				IGeometry circle = ShapeFactory.createCircle(p, fuzzyTol);
				FBitSet bitSet = layer.queryByShape(circle,
						DefaultStrategy.INTERSECTS);
				List<Integer> found = new ArrayList<Integer>();
				for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet
						.nextSetBit(i + 1)) {
					found.add(i);
				}
				if (found.size() == 1) {
					/*
					 * Si solo encuentra un registro, puede significar dos
					 * cosas: 1 - Que no hay otro registro con el que fusionar 2
					 * - Que hay que fusionar la geometria del registro consigo
					 * misma
					 */
					if (found.get(0) == id) {
						IGeometry geom = rv.getShape(id);
						MultiLineString multiLine = (MultiLineString) geom
								.toJTSGeometry();
						Coordinate pointC = new Coordinate(p.getX(), p.getY());
						Coordinate[] coords = multiLine.getCoordinates();
						Coordinate closest = getClosestPoint(coords, pointC);
						if (closest != null) {
							/*
							 * Si la distancia del punto mas cercano al nodo es
							 * menor que la tolerancia fuzzy, hay que cambiar
							 * las coordenadas del nodo, y situarlo sobre la
							 * coordenada calculada
							 */
							if (p.distance(closest.x, closest.y) < fuzzyTol) {
								updateFeature(rv, layer, closest, id,
										indHandler);
								if (intersections == null) {
									intersections = new ArrayList<Point2D>();
									intersections.add(new Point2D.Double(
											closest.x, closest.y));
								} else
									intersections.add(new Point2D.Double(
											closest.x, closest.y));
							}
						}// if(closest != null)
					}
				}// if(found.size() == 1)
				else {
					/*
					 * Se itera para cada una de las features encontradas
					 */
					Coordinate closest = null;
					for (int i = 0; i < found.size(); i++) {
						IGeometry geomFeat = rv.getShape(found.get(i));
						MultiLineString jtsGeomFeat = (MultiLineString) geomFeat
								.toJTSGeometry();
						Coordinate pointC = new Coordinate(p.getX(), p.getY());
						Coordinate newClosest = getClosestPoint(
								jtsGeomFeat.getCoordinates(), pointC);
						if (newClosest != null)
							if (p.distance(newClosest.x, newClosest.y) < fuzzyTol)
								if (closest != null) {
									if (p.distance(newClosest.x, newClosest.y) < p
											.distance(closest.x, closest.y)) {
										updateFeature(rv, layer, newClosest,
												id, indHandler);
										closest = newClosest;
									}
								} else {
									updateFeature(rv, layer, newClosest, id,
											indHandler);
									closest = newClosest;
								}
					}// for
					if (closest != null) {
						/*
						 * Se anyade la interseccion encontrada
						 */
						if (intersections == null) {
							intersections = new ArrayList<Point2D>();
							intersections.add(new Point2D.Double(closest.x,
									closest.y));
						} else
							intersections.add(new Point2D.Double(closest.x,
									closest.y));
					}
				}
			} catch (ReadDriverException e) {
				e.printStackTrace();
			} catch (VisitorException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Devuelve las coordenadas del punto mas cercano de la polilinea
		 * formada por las coordenadas 'coords', al punto pointC
		 * 
		 * @param coords
		 *            - conjunto de coordenadas de la polilinea
		 * @param pointC
		 *            - coordenadas del punto
		 * @return - coordenadas del punto mas cercano; null en caso de que el
		 *         punto mas cercano sea una de las coordenadas de 'coords'
		 */
		private Coordinate getClosestPoint(Coordinate[] coords,
				Coordinate pointC) {
			Coordinate closest = null;
			for (int i = 0; i < coords.length - 1; i++) {
				Coordinate c1 = coords[i];
				Coordinate c2 = coords[i + 1];
				/*
				 * Primero se comprueba que los vertices esten dentro de la
				 * tolerancia fuzzy. En ese caso, se considerara que el vertice
				 * es el punto mas cercano
				 */
				if (c1.distance(pointC) < fuzzyTol && !isSamePoint(c1, pointC)) {
					if (closest != null) {
						if (c1.distance(pointC) < closest.distance(pointC)) {
							closest = c1;
						}
					} else
						closest = c1;
				} else if (c2.distance(pointC) < fuzzyTol
						&& !isSamePoint(c2, pointC)) {
					if (closest != null) {
						if (c2.distance(pointC) < closest.distance(pointC)) {
							closest = c2;
						}
					} else
						closest = c2;
				} else {
					/*
					 * Hay que comprobar que ninguno de los puntos del segmento
					 * de la polilinea 'coincide' con las coordenadas del nodo
					 */
					if (!isSamePoint(pointC, c1) && !isSamePoint(pointC, c2)) {
						LineSegment line = new LineSegment(c1, c2);
						// Se calcula el punto mas cercano al nodo
						if (closest != null) {
							if (line.closestPoint(pointC).distance(pointC) < closest
									.distance(pointC))
								closest = line.closestPoint(pointC);
						} else
							closest = line.closestPoint(pointC);
					}// if
				}// else
			}// for
			return closest;
		}

		/**
		 * Actualiza la feature 'feat' en la capa 'layer' cambiando su
		 * geometria. Al vertice de indice 'indHandler' le asigna las nuevas
		 * coordenadas 'closest'
		 * 
		 * @param rv
		 *            - Feature de la que se va a modificar la geometria
		 * @param layer
		 *            - capa sobre la que se esta trabajando
		 * @param closest
		 *            - nuevas coordenadas del nodo
		 * @param idFeature
		 *            - id de la feature en la capa
		 * @param indHandler
		 *            - indice del nodo a modificar
		 * @throws ReadDriverException
		 * @throws ExpansionFileReadException
		 */
		private void updateFeature(ReadableVectorial rv, FLyrVect layer,
				Coordinate closest, int idFeature, int indHandler)
				throws ExpansionFileReadException, ReadDriverException {
			IFeature newFeat = (IFeature) rv.getFeature(idFeature).cloneRow();

			FGeometry geometry = (FGeometry) newFeat.getGeometry();
			FPolyline2D polyline = (FPolyline2D) geometry.getInternalShape();
			polyline.getSelectHandlers()[indHandler].set(closest.x, closest.y);

			DefaultFeature df = new DefaultFeature(geometry,
					newFeat.getAttributes(), newFeat.getID());
			DefaultRowEdited dre = new DefaultRowEdited(df,
					DefaultRowEdited.STATUS_MODIFIED, idFeature);
			VectorialEditableAdapter adapter = (VectorialEditableAdapter) layer
					.getSource();

			try {
				adapter.modifyRow(dre.getIndex(), dre.getLinkedRow(), "",
						EditionEvent.GRAPHIC);
			} catch (ExpansionFileWriteException e) {
				e.printStackTrace();
			} catch (ExpansionFileReadException e) {
				e.printStackTrace();
			} catch (ValidateRowException e) {
				e.printStackTrace();
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Comprueba si dos coordenadas corresponden al mismo punto. Si su
		 * distancia es menor que 0.000000001 metros, se asume que es el mismo
		 * punto
		 * 
		 * @param c1
		 *            - coordenada 1
		 * @param c2
		 *            - coordenada 2
		 * @return TRUE si ambas coordenadas corresponden al mismo punto. FALSE
		 *         en caso contrario
		 */
		private boolean isSamePoint(Coordinate c1, Coordinate c2) {
			if (c1.distance(c2) == 0)
				return true;
			return false;
		}

		public void run() throws Exception {
			/*
			 * Se clona la capa inicial y su seleccion, para fusionar nodos con
			 * otros nodos o con segmentos, segun la tolerancia 'fuzzy'. De esta
			 * manera se pueden modificar las geometrias de la capa 'original' y
			 * tenerlas preparadas para aplicarles la separacion de pseudonodos.
			 */
			FLyrVect clonedLayer = (FLyrVect) firstLayer.cloneLayer();

			clonedLayer.setName("auxLayer");

			/*
			 * Se exporta a otra capa (pq aunque se clone, el fichero fisico del
			 * disco es el mismo) De esta manera, ya se puede trabajar con la
			 * capa auxiliar sin que los cambios de geometria afecten a la capa
			 * inicial
			 */
			FLyrVect auxLayer = exportAuxLayer(clonedLayer);

			MapContext mc = getMapContext();
			FLayers root = mc.getLayers();
			auxLayer.setCrs(mc.getCrs());

			root.addLayer(auxLayer);

			mc.getLayers().setAllActives(false);
			auxLayer.setActive(true);
			PluginServices.getExtension(StartEditingClean.class).execute(
					"STARTEDITING");

			/*
			 * Despues de haber fusionado los nodos, hay que guardar la capa
			 * auxiliar, para que los cambios en las geometrias tengan efecto.
			 * De este modo, esta capa sera la que reciba el siguiente codigo
			 * para tratar los pseudonodos
			 */
			mergeNodes(auxLayer);
			PluginServices.getExtension(StopEditingClean.class).execute(
					"STOPEDITING");

			/*
			 * Se elimina de la vista porque ya se ha terminado de usarla
			 */
			root.removeLayer(auxLayer);

			/*
			 * Ya se puede empezar con la separacion de pseudonodos
			 */

			processor = new FeaturePersisterProcessor2(writer);

			intersectionsWriter = new ShpWriter();
			String prefName = resultLayerName.substring(0,
					resultLayerName.indexOf(".shp"));
			String temp = System.getProperty("java.io.tmpdir") + "/" + prefName
					+ "_intersections.shp";
			File newFile = new File(temp);
			((ShpWriter) intersectionsWriter).setFile(newFile);

			ILayerDefinition intersectDefinition = new SHPLayerDefinition();
			intersectDefinition.setShapeType(XTypes.POINT);
			FieldDescription[] intersectFields = new FieldDescription[2];
			intersectFields[0] = new FieldDescription();
			intersectFields[0].setFieldLength(10);
			intersectFields[0].setFieldDecimalCount(0);
			intersectFields[0].setFieldName("FID1");
			intersectFields[0].setFieldType(XTypes.INTEGER);
			intersectFields[1] = new FieldDescription();
			intersectFields[1].setFieldLength(10);
			intersectFields[1].setFieldDecimalCount(0);
			intersectFields[1].setFieldName("FID2");
			intersectFields[1].setFieldType(XTypes.INTEGER);
			intersectDefinition.setFieldsDesc(intersectFields);

			((ShpWriter) intersectionsWriter)
					.initialize((LayerDefinition) intersectDefinition);
			((SHPLayerDefinition) intersectDefinition).setFile(newFile);

			ShpSchemaManager interSchMg = new ShpSchemaManager(
					newFile.getAbsolutePath());
			interSchMg.createSchema(intersectDefinition);

			intersectsProcessor = new FeaturePersisterProcessor2(
					intersectionsWriter);

			dangleWriter = new ShpWriter();
			String temp2 = System.getProperty("java.io.tmpdir")
					+ File.separatorChar + prefName + "_dangles.shp";
			File dangleFile = new File(temp2);
			((ShpWriter) dangleWriter).setFile(dangleFile);

			ILayerDefinition dangleDefinition = new SHPLayerDefinition();
			dangleDefinition.setShapeType(XTypes.LINE);
			FieldDescription[] dangleFields = auxLayer.getRecordset()
					.getFieldsDescription().clone();
			dangleDefinition.setFieldsDesc(dangleFields);

			((ShpWriter) dangleWriter)
					.initialize((LayerDefinition) dangleDefinition);
			((SHPLayerDefinition) dangleDefinition).setFile(dangleFile);

			ShpSchemaManager dangleSchMg = new ShpSchemaManager(
					dangleFile.getAbsolutePath());
			dangleSchMg.createSchema(dangleDefinition);

			dangleProcessor = new FeaturePersisterProcessor2(dangleWriter);

			FBitSet selection = null;
			if (onlyFirstLayerSelection)
				selection = firstLayer.getRecordset().getSelection();
			SnappingCoordinateMap coordMap = new SnappingCoordinateMap(fuzzyTol);
			IntersectionNodeVisitor visitor = new IntersectionNodeVisitor(
					processor, intersectsProcessor, onlyFirstLayerSelection,
					resultLayerDefinition, auxLayer, auxLayer.getRecordset(),
					coordMap, dangleTol, fuzzyTol, dangleProcessor);

			try {
				processor.start();
				intersectsProcessor.start();
				dangleProcessor.start();

				ReadableVectorial va = auxLayer.getSource();
				va.start();
				for (int i = 0; i < va.getShapeCount(); i++) {// for each
																// geometry
					if (verifyCancelation(va)) {
						intersectsProcessor.finish();
						dangleProcessor.finish();
						return;
					}
					if (selection != null) {
						if (selection.get(i)) {
							reportStep();
							visitor.visit(va.getShape(i), i);
						}
					} else {
						reportStep();
						visitor.visit(va.getShape(i), i);
					}
				}// for
				va.stop();
				processor.finish();
				intersectsProcessor.finish();
				dangleProcessor.finish();

			} catch (ReadDriverException e) {
				e.printStackTrace();
			}

			auxLayer = null;

			if (fromBuild) {

				if (resultLayer == null)
					resultLayer = (FLyrVect) createLayerFrom(writer);

				SHPLayerDefinition shpDef = (SHPLayerDefinition) resultLayerDefinition;
				File shpFile = shpDef.getFile();
				String resPath = shpDef.getFile().getAbsolutePath();
				String path = resPath.substring(0,
						resPath.indexOf(shpFile.getName()))
						+ "build.shp";
				File outputFile = new File(path);

				BuildGeoprocess geoprocess = new BuildGeoprocess(resultLayer);

				SHPLayerDefinition definition = (SHPLayerDefinition) geoprocess
						.createLayerDefinition();
				definition.setFile(outputFile);
				ShpSchemaManager schemaManager = new ShpSchemaManager(
						outputFile.getAbsolutePath());
				IWriter buildWriter = null;
				int shapeType = definition.getShapeType();
				if (shapeType != XTypes.MULTI) {
					buildWriter = new ShpWriter();
					((ShpWriter) buildWriter).setFile(definition.getFile());
					buildWriter.initialize(definition);
				} else {
					buildWriter = new MultiShpWriter();
					((MultiShpWriter) writer).setFile(definition.getFile());
					writer.initialize(definition);
				}

				geoprocess.setResultLayerProperties(buildWriter, schemaManager);

				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put("firstlayerselection", new Boolean(false));

				params.put("createlayerswitherrors", new Boolean(
						createLyrsWithErrorGeometries));

				try {
					geoprocess.setParameters(params);
					geoprocess.checkPreconditions();
					IMonitorableTask task1 = geoprocess.createTask();
					AddResultLayerTask task2 = new AddResultLayerTask(
							geoprocess);
					task2.setLayers(root);
					MonitorableDecoratorMainFirst globalTask = new MonitorableDecoratorMainFirst(
							task1, task2);
					if (globalTask.preprocess())
						PluginServices
								.cancelableBackgroundExecution(globalTask);

				} catch (GeoprocessException e) {
					String error = PluginServices.getText(this,
							"Error_ejecucion");
					String errorDescription = PluginServices.getText(this,
							"Error_fallo_geoproceso");
				}

			}
		}

		/**
		 * Exporta la capa a un directorio temporal, en formato shape
		 * 
		 * @param layer
		 *            - capa a exportar
		 * @return la nueva capa que ya apunta fisicamente al shape que se acaba
		 *         de crear
		 * @throws ReadDriverException
		 * @throws BaseException
		 */
		private FLyrVect exportAuxLayer(FLyrVect layer)
				throws ReadDriverException, BaseException {
			FLyrVect auxLayer = null;
			String charSetName = prefs.get("dbf_encoding", DbaseFile
					.getDefaultCharset().toString());
			try {
				ShpWriter writer = (ShpWriter) LayerFactory.getWM().getWriter(
						"Shape Writer");
				Object s = layer.getProperty("DBFFile");
				if (s != null && s instanceof String)
					writer.loadDbfEncoding((String) s,
							Charset.forName(charSetName));
				String temp = System.getProperty("java.io.tmpdir") + "/"
						+ "auxLayer.shp";
				File newFile = new File(temp);
				IndexedShpDriver drv = new IndexedShpDriver();
				if (!newFile.exists()) {
					try {
						newFile.createNewFile();
						File newFileSHX = new File(newFile.getAbsolutePath()
								.replaceAll("[.]shp", ".shx"));
						newFileSHX.createNewFile();
						File newFileDBF = new File(newFile.getAbsolutePath()
								.replaceAll("[.]shp", ".dbf"));
						newFileDBF.createNewFile();
					} catch (IOException e) {
						throw new FileNotFoundDriverException("SHP", e,
								newFile.getAbsolutePath());
					}
				}
				drv.open(newFile);
				SHPLayerDefinition lyrDef = new SHPLayerDefinition();
				lyrDef.setFieldsDesc(layer.getRecordset()
						.getFieldsDescription());
				lyrDef.setFile(newFile);
				lyrDef.setName(newFile.getName());
				lyrDef.setShapeType(layer.getTypeIntVectorLayer());
				writer.setFile(newFile);
				writer.initialize(lyrDef);
				SelectableDataSource sds = layer.getRecordset();
				ReadableVectorial va = layer.getSource();

				va.start();
				writer.preProcess();

				for (int i = 0; i < va.getShapeCount(); i++) {
					if (isCanceled())
						break;
					IGeometry geom = va.getShape(i);
					if (geom == null) {
						continue;
					}

					if (geom != null) {
						Value[] values = sds.getRow(i);
						IFeature feat = new DefaultFeature(geom, values, "" + i);
						DefaultRowEdited edRow = new DefaultRowEdited(feat,
								DefaultRowEdited.STATUS_ADDED, i);
						try {
							writer.process(edRow);
						} catch (ProcessWriterVisitorException e) {
							e.printStackTrace();
						}
					}
				}

				writer.postProcess();
				va.stop();

				ILayerDefinition lyDef = (ILayerDefinition) writer
						.getTableDefinition();
				auxLayer = (FLyrVect) LayerFactory.createLayer(lyDef.getName(),
						drv, layer.getCrs());

			} catch (DriverLoadException e) {
				e.printStackTrace();
			} catch (InitializeWriterException e) {
				e.printStackTrace();
			}
			return auxLayer;
		}

		public String getNote() {
			String cleaningText1 = PluginServices.getText(this,
					"Limpiando_lineas");
			String cleaningText2 = PluginServices.getText(this,
					"Merge_geometry_nodes");
			String of = PluginServices.getText(this, "de");
			String retText1 = cleaningText1 + " "
					+ (getCurrentStep() - (getFinishStep() / 2)) + " " + of
					+ " " + (getFinishStep() / 2);
			String retText2 = cleaningText2 + " " + getCurrentStep() + " " + of
					+ " " + (getFinishStep() / 2);

			if (getCurrentStep() < getFinishStep() / 2)
				return retText2;
			else
				return retText1;
		}

		public void cancel() {
			setCanceled(true);
			CleanGeoprocess.this.cancel();
		}

		public Object getResult() {
			try {
				return CleanGeoprocess.this.getResult();
			} catch (GeoprocessException e) {
				return null;
			}
		}

		public void setEntry(Object object) {
		}

		public void finished() {
		}
	}

}
