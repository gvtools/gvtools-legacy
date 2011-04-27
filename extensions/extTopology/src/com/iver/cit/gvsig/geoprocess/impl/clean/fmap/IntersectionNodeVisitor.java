package com.iver.cit.gvsig.geoprocess.impl.clean.fmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.cit.gvsig.exceptions.visitors.ProcessVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialData;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.fmap.operations.strategies.StrategyManager;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureFactory;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.util.SnappingCoordinateMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geomgraph.Node;
import com.vividsolutions.jts.geomgraph.NodeFactory;
import com.vividsolutions.jts.geomgraph.SnappingNodeMap;
import com.vividsolutions.jts.linearref.LinearLocation;

public class IntersectionNodeVisitor implements FeatureVisitor {

	/**
	 * Recordset of the layer we are working with
	 */
	protected SelectableDataSource recordset;

	/**
	 * Layer which we are cleaning
	 */
	protected FLyrVect layerToClean;

	/**
	 * marks if we are going to clean only layer selection
	 */
	protected boolean cleanOnlySelection;

	/*
	 * TODO Meter esto en preferencias
	 */
	public final static double DEFAULT_SNAP = 0.1;

	protected double snapTolerance = DEFAULT_SNAP;

	/**
	 * Strategy of the layer we are working with.
	 */
	protected Strategy strategy;

	/**
	 * Saves features resulting of cleaning process
	 */
	protected FeatureProcessor featureProcessor;

	protected ILayerDefinition layerDefinition;

	/**
	 * Saves pseudonodes found
	 */
	protected FeatureProcessor intersectProcessor;
	
	/**
	 * Saves dangle lines found
	 */
	protected FeatureProcessor dangleProcessor;

	/**
	 * Counter of new features
	 */
	private int fid = 0;
	
	private double dangleTolerance = 0;

	/**
	 * It caches all written pseudonodes, to avoid writing the same pseudonode
	 * twice.
	 * 
	 */
	SnappingCoordinateMap snapCoordMap;
	
	/**
	 * Indice de la nueva feature en la nueva capa
	 */
	private int indexFeat = 0;

	/**
	 * Constructor.
	 * 
	 * @param processor
	 * @param intersectsProcessor
	 * @param cleanOnlySelection
	 * @param layerDefinition
	 * @param intersectDefinition
	 * @param firstLayer
	 * @param source
	 * @param snapCoordMap
	 * @param dangleTol 
	 */
	public IntersectionNodeVisitor(FeatureProcessor processor,FeaturePersisterProcessor2 intersectsProcessor,boolean cleanOnlySelection, ILayerDefinition layerDefinition, FLyrVect firstLayer,SelectableDataSource source, SnappingCoordinateMap snapCoordMap, double dangleTol,double fuzzyTol,FeatureProcessor danglesProcessor) {
		
		this.featureProcessor = processor;
		this.cleanOnlySelection = cleanOnlySelection;
//		this.layerDefinition = layerDefinition;
		this.intersectProcessor = intersectsProcessor;
		this.dangleProcessor = danglesProcessor;
		this.layerToClean = firstLayer;
		this.recordset = source;
		this.strategy = StrategyManager.getStrategy(layerToClean);
		this.snapCoordMap = snapCoordMap;
		this.dangleTolerance = dangleTol;
		this.snapTolerance = fuzzyTol;
		this.layerDefinition = layerDefinition;
	}

	public void setLayerDefinition(ILayerDefinition layerDefinition) {
		this.layerDefinition = layerDefinition;
	}

	private boolean checkForLineGeometry(Geometry geometry) {
		if (geometry instanceof LineString)
			return true;
		if (geometry instanceof MultiLineString)
			return true;
		if (geometry instanceof GeometryCollection) {
			GeometryCollection col = (GeometryCollection) geometry;
			for (int i = 0; i < col.getNumGeometries(); i++) {
				if (!checkForLineGeometry(col.getGeometryN(i)))
					return false;
			}
			return true;
		}
		return false;
	}

	public void visit(IGeometry g, final int index) throws VisitorException,StopWriterVisitorException, ProcessVisitorException {
		// first, we check it isnt a null geometry and the geometry type
		// is correct
		if (g == null)
			return;
		int geometryType = g.getGeometryType();
		if (geometryType != XTypes.ARC && geometryType != XTypes.LINE
				&& geometryType != XTypes.MULTI)
			return;

		// after that, if we are going to clean only selected features, we
		// check if this feature is selected
		if (cleanOnlySelection) {
			try {
				if (!layerToClean.getRecordset().getSelection().get(index))
					return;
			} catch (ReadDriverException e) {
				throw new ProcessVisitorException(recordset.getName(), e,
						"Error verificando seleccion en CLEAN");
			}
		}// if cleanOnly

		final Geometry jtsGeo = g.toJTSGeometry();

		// we check if jts geometry is a line (or a line collection)
		if (!checkForLineGeometry(jtsGeo))
			return;

		SnappingNodeMap nodes = new SnappingNodeMap(new NodeFactory(),snapTolerance);
		/*
		 * Se nos plantea una problematica. Tenemos dos features: A y B, y he
		 * calculado la interseccion de A y B. Ahora, hay dos alternativas: -a)
		 * nunca mas calcular esta intersección, pero almacenar su resultado en
		 * SnappingNodeMap. Así, en una segunda pasada, con todos los "Nodos"
		 * calculados, podríamos fragmentar las lineas de forma individual.
		 * PROBLEMA: SnappingNodeMap iría creciendo, almacenando todos los nodos
		 * de una capa....(si se apoya sobre un IndexedShpDriver, no tendría por
		 * qué)
		 * 
		 * 
		 * -b) Calcular la intersección en los dos sentidos: A int B, y luego al
		 * procesar B, B int A. En este caso, pasamos olímpicamente del bitset
		 * 
		 * De momento, por simplicidad, seguimos la alternativa -b)
		 */

		final boolean onlySelection = cleanOnlySelection;
		try {
			CleanFeatureVisitor cleanFeatureVisitor = new CleanFeatureVisitor(index,onlySelection,layerToClean,recordset,jtsGeo,nodes,snapCoordMap,intersectProcessor);
			strategy.process(cleanFeatureVisitor, g.getBounds2D());
			
			/*
			 * Primero hay que 'ordenar' los nodos, dentro de la geometria jtsGeo, que es con la que
			 * estan intersectando
			 */
			nodes = sortNodes(nodes, jtsGeo);
			
			
			boolean rightGeometry = true;
			if(nodes.values().size() > 0) {
				Coordinate[] coords = jtsGeo.getCoordinates();
				rightGeometry = false;
				List<Coordinate> auxCoords = new ArrayList<Coordinate>();
				GeometryFactory geomFact = new GeometryFactory();
				//Recorremos todas las coordenadas de la linea
				for(int i=0;i<coords.length-1;i++) {
					Coordinate c1 = coords[i];
					Coordinate c2 = coords[i+1];
					Coordinate[] lineCoords = new Coordinate[] {c1,c2};
					LineString line = geomFact.createLineString(lineCoords);
					insertCoordinate(auxCoords,c1);
					Iterator<Node> it = nodes.iterator();
					//Para cada uno de los nodos de la intersección
					while(it.hasNext()) {
						Node node = (Node)it.next();
						Point point = geomFact.createPoint(node.getCoordinate());
						/*
						 * Hay que comprobar si para la linea obtenida, las coordenadas
						 * del nodo intersectan. La siguiente condicion viene a comprobar 
						 * lo mismo
						 */
						if(line.distance(point) < 0.0000001) {
							/*
							 * Si intersecta, se crea una feature, desde la ultima coordenada
							 * insertada, hasta el nodo
							 */
							insertCoordinate(auxCoords,node.getCoordinate());
							if(auxCoords.size() > 1) {
								LineString lineToInsert = geomFact.createLineString(getCoordsAsArray(auxCoords));
								if(lineToInsert.getLength()>=dangleTolerance) {
									IFeature feature = createFeature(lineToInsert, index);
									featureProcessor.processFeature(feature);
									auxCoords.clear();
									insertCoordinate(auxCoords,node.getCoordinate());
								}
								else {
									if(!isAPoint(lineToInsert)) {
										IFeature feature = createFeature(lineToInsert, index);
										dangleProcessor.processFeature(feature);
									}
								}
							}//if
						}//if
					}//while
					insertCoordinate(auxCoords,c2);
					
				}//for
				if(auxCoords.size() > 1) {
					LineString lineToInsert = geomFact.createLineString(getCoordsAsArray(auxCoords));
					if(lineToInsert.getLength()>=dangleTolerance) {
						IFeature feature = createFeature(lineToInsert, index);
						featureProcessor.processFeature(feature);
						auxCoords.clear();
					}
					else {
						if(!isAPoint(lineToInsert)) {
							IFeature feature = createFeature(lineToInsert, index);
							dangleProcessor.processFeature(feature);
						}
					}
				}//if
			}//if
			
			if(rightGeometry) {
				IFeature feature = createFeature(g, index);
				if(jtsGeo.getLength()>=dangleTolerance) {
					featureProcessor.processFeature(feature);
				}
				else {
					dangleProcessor.processFeature(feature);
				}
			}
			
//			// At this point, nodes variable (SnappingNodeMap)
//			// has all intersections of the visited feature with the rest of
//			// features of the layer
//
//			// It computes linear distance of a point on the given jtsGeo linear
//			// geometry
//			boolean rightGeometry = true;
//			if (nodes.values().size() > 0) {
//				
//				LengthIndexedLine lengthLine = new LengthIndexedLine(jtsGeo);
//				Iterator nodesIt = nodes.iterator();
//				ArrayList<LineIntersection> nodeIntersections = new ArrayList<LineIntersection>();
//				while (nodesIt.hasNext()) {
//					Node node = (Node) nodesIt.next();
//					Coordinate coord = node.getCoordinate();
//					double lengthOfNode = lengthLine.indexOf(coord);
//					LineIntersection inters = new LineIntersection();
//					inters.coordinate = coord;
//					inters.lenght = lengthOfNode;
//					nodeIntersections.add(inters);
//				}
//				
//				if (nodeIntersections.size() > 0) {
//					// We sort the intersections by distance along the line
//					// (dynamic segmentation)
//					rightGeometry = false;
//					LineIntersectionComparator comp = new LineIntersectionComparator();
//					Collections.sort(nodeIntersections,comp);
//
//					LinearLocation lastLocation = null;
//					LineIntersection lastIntersection = null;
//					LocationIndexedLine indexedLine = new LocationIndexedLine(jtsGeo);
//					for (int i = 0; i < nodeIntersections.size(); i++) {
//						Geometry solution = null;
//						LineIntersection li = (LineIntersection) nodeIntersections.get(i);
//
//						LinearLocation location = indexedLine.indexOf(li.coordinate);// es posible que esto
//														// esté mal por no
//														// pasarle una longitud.
//														// REVISAR
//						if (lastLocation == null) {
//							LinearLocation from = new LinearLocation(0, 0d);
//							
////							solution = splitLineString(jtsGeo, from, location, null, li);
//							
//							solution = indexedLine.extractLine(from, location);
//							
//							
//							lastLocation = location;
//							lastIntersection = li;
//							/*
//							 * Construimos una linea desde el primer punto hasta
//							 * el punto contenido en LineIntersection, con todos
//							 * los puntos intermedios de la linea.
//							 * 
//							 * 
//							 * 
//							 */
//						} else {
//							// Construimos una linea entre lastIntersection y la
//							// intersection
//							// actual
//							LinearLocation locationFrom = lastLocation;
//							
////							solution = splitLineString(jtsGeo, locationFrom,
////									location, lastIntersection, li);
//							
//							solution = indexedLine.extractLine(locationFrom, location);
//							lastLocation = location;
//							lastIntersection = li;
//
//						}
//						if(solution.getLength()>=dangleTolerance) {
//							IFeature feature = createFeature(solution, index);
//							featureProcessor.processFeature(feature);
//						}
//						// TODO Podriamos guardar los puntos de interseccion
//						// para
//						// mostrar al usuario que puntos eran pseudonodos
//					}// for
//
//					// añadimos el ultimo segmento
////					Coordinate[] geomCoords = jtsGeo.getCoordinates();
////					ArrayList coordinates = new ArrayList();
////					coordinates.add(lastIntersection.coordinate);
////					int startIndex = lastLocation.getSegmentIndex() + 1;
////					for (int i = startIndex; i < geomCoords.length; i++) {
////						coordinates.add(geomCoords[i]);
////					}
////					Coordinate[] solutionCoords = new Coordinate[coordinates
////							.size()];
////					coordinates.toArray(solutionCoords);
////					IFeature lastFeature = createFeature(new GeometryFactory()
////							.createLineString(solutionCoords), index);
//					LinearLocation endLocation = new LinearLocation();
//					endLocation.setToEnd(jtsGeo);
//					Geometry geo = indexedLine.extractLine(lastLocation, endLocation);
//					if(geo.getLength()>=dangleTolerance) {
//						IFeature lastFeature = createFeature(geo, index);
//						featureProcessor.processFeature(lastFeature);
//					}
//
//				}
//			} 
//			if(rightGeometry){
//				if(jtsGeo.getLength()>=dangleTolerance) {
//					IFeature feature = createFeature(g, index);
//					featureProcessor.processFeature(feature);
//				}
//			}
			

		} catch (ReadDriverException e) {
			throw new ProcessVisitorException(recordset.getName(), e,
					"Error buscando los overlays que intersectan con un feature");
		} 
	}
	
	private void insertCoordinate(List<Coordinate> coordList,Coordinate coord) {
		if(coordList != null) {
			if(coordList.size() > 0) {
				if(coordList.get(coordList.size()-1) != null) {
					Coordinate lastCoord = coordList.get(coordList.size()-1);
					if(!lastCoord.equals(coord))
						coordList.add(coord);
				}
			}
			else
				coordList.add(coord);
		}
	}
	
	/**
	 * Ordena los nodos con respecto a la geometria. Esto es, se coloca primero,
	 * el que menor distancia tiene a la primera coordenada de la geometria.
	 * @param nodes - SnappingNodeMap con los nodos encontrados que intersectan con la geometria
	 * @param geom - geometria
	 */
	private SnappingNodeMap sortNodes(SnappingNodeMap nodes,Geometry geom) {
		MultiLineString mls = (MultiLineString)geom;
		Coordinate[] coords = mls.getCoordinates();
		GeometryFactory geomFact = new GeometryFactory();
		SnappingNodeMap auxSNM = new SnappingNodeMap(new NodeFactory(),snapTolerance);
		HashMap<String,Node> auxNodes = new HashMap<String,Node>();
		for(int i=0;i<coords.length-1;i++) {
			Coordinate c1 = coords[i];
			Coordinate c2 = coords[i+1];
			Coordinate[] coord = new Coordinate[] {c1,c2};
			LineString line = geomFact.createLineString(coord);
			double minDist = 0;
			for(int j=0;j<nodes.values().size();j++) {
				double dist = Double.MAX_VALUE;
				Iterator<Node> it = nodes.iterator();
				while(it.hasNext()) {
					Node node = (Node)it.next();
					//Primero se comprueba que el nodo intersecta con la linea
					Point point = geomFact.createPoint(node.getCoordinate());
					if(line.distance(point) < 0.0000001){
						/*
						 * Y luego que sea el que menor distancia tiene al primero de los
						 * vertices de la linea
						 */
						double distance = c1.distance(node.getCoordinate());
						if(minDist < distance && distance < dist) {
							if(auxNodes.get(i+""+j) != null)
								auxNodes.remove(i+""+j);
							auxNodes.put(i+""+j,node);
							dist = distance;
						}
					}
				}//while
				minDist = dist;
			}//for j
		}//for i
		/*
		 * Ahora se itera a la 'inversa' para introducir en el map los nodos, segun
		 * se han ido 'ordenando'
		 */
		for(int i=coords.length-1;i>=0;i--) {
			for(int j=nodes.values().size()-1;j>=0;j--) {
				String key = i+""+j;
				if(auxNodes.get(key) != null)
					auxSNM.addNode(auxNodes.get(key));
			}
		}
		return auxSNM;
	}
	
	private boolean isAPoint(LineString lineString) {
		Coordinate[] coords = lineString.getCoordinates();
		double distance = 0;
		for(int i=0;i<coords.length-1;i++) {
			Coordinate c1 = coords[i];
			Coordinate c2 = coords[i+1];
			distance += c1.distance(c2);
		}
		if(distance > 0.0000001)
			return false;
		return true;
	}
	
	
	/**
	 * Dada una linea, una localizacion de partida (from), una localización de llegada (to), y dos puntos de interseccion (from) devuelve la linea entre from y to
	 */
	private Geometry splitLineString(Geometry linearGeometry,LinearLocation from, LinearLocation to, LineIntersection fromInt,LineIntersection toInt) {
		Coordinate[] geomCoords = linearGeometry.getCoordinates();
		ArrayList coordinates = new ArrayList();
		if (fromInt != null)
			coordinates.add(fromInt.coordinate);
		int startIndex = from.getSegmentIndex();
		/*
		 * segmentIndex siempre referencia al punto inmediatamente anterior del
		 * lineString. Nos interesa sumar 1, a no ser que sea el primer punto
		 * del linestring
		 */
		if (startIndex != 0)
			startIndex++;

		for (int i = startIndex; i <= to.getSegmentIndex(); i++) {
			coordinates.add(geomCoords[i]);
		}
		coordinates.add(toInt.coordinate);
		Coordinate[] solutionCoords = new Coordinate[coordinates.size()];
		coordinates.toArray(solutionCoords);
		return new GeometryFactory().createLineString(solutionCoords);

	}

	class LineIntersection {
		Coordinate coordinate;

		double lenght;
	}

	public String getProcessDescription() {
		return "Eliminando pseudonodos de la capa vectorial '"+this.layerToClean.getName()+"'";
	}

	public void stop(FLayer layer) throws StopWriterVisitorException,
			VisitorException {
		this.featureProcessor.finish();
		this.intersectProcessor.finish();
		this.dangleProcessor.finish();
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		if (layer instanceof AlphanumericData && layer instanceof VectorialData) {
			try {
				layerToClean = (FLyrVect) layer;
				recordset = ((AlphanumericData) layer).getRecordset();
				strategy = StrategyManager.getStrategy(layerToClean);
				featureProcessor.start();
				intersectProcessor.start();
				dangleProcessor.start();
			} catch (ReadDriverException e) {
				return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Crea una feature en el 'recordset' con la geometria recibida,
	 * y las propiedades de la feature de indice 'firstLayerIndex',
	 * correspondiente a la capa 'firstLayer'
	 * 
	 * @param jtsGeometry - nueva geometria
	 * @param firstLayerIndex - indice de la feature en la capa original (firstLayer)
	 * @return - la nueva feature creada
	 * @throws ReadDriverException
	 */
	private IFeature createFeature(Geometry jtsGeometry, int firstLayerIndex) throws ReadDriverException {
		IFeature solution = null;
		IGeometry cleanedGeometry = FConverter.jts_to_igeometry(jtsGeometry);
		FieldDescription[] fields = layerDefinition.getFieldsDesc();
		int numFields = fields.length-2;
		Value[] featureAttr = new Value[fields.length];
		for (int indexField = 0; indexField < numFields; indexField++) {
			// for each field of firstRs
			String fieldName = recordset.getFieldName(indexField);
			for (int j = 0; j < fields.length; j++) {
				if (fieldName.equalsIgnoreCase(fields[j].getFieldName())) {
					featureAttr[j] = recordset.getFieldValue(firstLayerIndex,indexField);
					break;
				}// if
			}// for
		}// for
		ValueFactory valFact = new ValueFactory();
		featureAttr[featureAttr.length-2] = valFact.createValue(firstLayerIndex);
		featureAttr[featureAttr.length-1] = valFact.createValue(indexFeat);
		indexFeat++;
		// now we put null values
		for (int i = 0; i < featureAttr.length; i++) {
			if (featureAttr[i] == null)
				featureAttr[i] = ValueFactory.createNullValue();
		}
		solution = FeatureFactory.createFeature(featureAttr, cleanedGeometry);
		return solution;
	}

	

	private IFeature createFeature(IGeometry g, int firstLayerIndex) throws ReadDriverException {
		IFeature solution = null;
		FieldDescription[] fields = layerDefinition.getFieldsDesc();
		int numFields = fields.length-2;
		Value[] featureAttr = new Value[fields.length];
		for (int indexField = 0; indexField < numFields; indexField++) {
			// for each field of firstRs
			String fieldName = recordset.getFieldName(indexField);
			for (int j = 0; j < fields.length; j++) {
				if (fieldName.equalsIgnoreCase(fields[j].getFieldName())) {
					featureAttr[j] = recordset.getFieldValue(firstLayerIndex,indexField);
					break;
				}// if
			}// for
		}// for
		ValueFactory valFact = new ValueFactory();
		featureAttr[featureAttr.length-2] = valFact.createValue(firstLayerIndex);
		featureAttr[featureAttr.length-1] = valFact.createValue(indexFeat);
		indexFeat++;
		// now we put null values
		for (int i = 0; i < featureAttr.length; i++) {
			if (featureAttr[i] == null)
				featureAttr[i] = ValueFactory.createNullValue();
		}
		solution = FeatureFactory.createFeature(featureAttr, g);
		return solution;
	}
	
	private Coordinate[] getCoordsAsArray(List<Coordinate> coords) {
		Coordinate[] coordArray = new Coordinate[coords.size()];
		for(int i=0;i<coords.size();i++) {
			coordArray[i] = coords.get(i);
		}
		return coordArray;
	}

}
