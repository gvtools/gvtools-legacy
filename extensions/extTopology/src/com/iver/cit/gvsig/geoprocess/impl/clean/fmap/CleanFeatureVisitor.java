package com.iver.cit.gvsig.geoprocess.impl.clean.fmap;

import java.util.ArrayList;
import java.util.Arrays;

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
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureFactory;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeatureProcessor;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.util.SnappingCoordinateMap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geomgraph.SnappingNodeMap;
import com.vividsolutions.jts.operation.overlay.SnappingOverlayOperation;

public class CleanFeatureVisitor implements FeatureVisitor {

	private SnappingOverlayOperation overlayOp = null;

	public final static double DEFAULT_SNAP = 0.1;

	protected double snapTolerance = DEFAULT_SNAP;

	private SnappingCoordinateMap snapCoordMap = null;

	private FeatureProcessor intersectProcessor;

	private int index = 0;

	private boolean onlySelection = false;

	private FLyrVect layerToClean = null;

	private SelectableDataSource recordSet = null;

	private Geometry jtsGeo = null;

	private SnappingNodeMap nodes = null;

	public CleanFeatureVisitor(int ind, boolean onlySelected, FLyrVect layer,
			SelectableDataSource recordset, Geometry geom,
			SnappingNodeMap snappingNodeMap,
			SnappingCoordinateMap snappingCoordMap,
			FeatureProcessor intersProcessor) {
		this.snapCoordMap = snappingCoordMap;
		this.index = ind;
		this.onlySelection = onlySelected;
		this.layerToClean = layer;
		this.recordSet = recordset;
		this.jtsGeo = geom;
		this.nodes = snappingNodeMap;
		this.intersectProcessor = intersProcessor;
	}

	/**
	 * From a given geometry, it returns its nodes (coordinate for a point,
	 * extreme coordinates for a line, first coordinate for a polygon)
	 */
	private Coordinate[] getNodesFor(Geometry processedGeometry) {
		Coordinate[] geomNodes = null;
		if (processedGeometry instanceof LineString) {
			LineString line = (LineString) processedGeometry;
			geomNodes = new Coordinate[2];
			geomNodes[0] = line.getCoordinateN(0);
			geomNodes[1] = line.getCoordinateN(line.getNumPoints() - 1);
		} else if (processedGeometry instanceof MultiLineString) {
			MultiLineString lines = (MultiLineString) processedGeometry;
			int numLines = lines.getNumGeometries();
			geomNodes = new Coordinate[2 * numLines];
			int index = 0;
			for (int i = 0; i < numLines; i++) {
				LineString line = (LineString) lines.getGeometryN(i);
				geomNodes[index] = line.getCoordinateN(0);
				index++;
				geomNodes[index] = line.getCoordinateN(line.getNumPoints() - 1);
				index++;
			}
		} else if (processedGeometry instanceof GeometryCollection) {
			GeometryCollection col = (GeometryCollection) processedGeometry;
			ArrayList coordinates = new ArrayList();
			for (int i = 0; i < col.getNumGeometries(); i++) {
				Geometry geom = col.getGeometryN(i);
				Coordinate[] newNodes = getNodesFor(geom);
				coordinates.addAll(Arrays.asList(newNodes));
			}
		}
		// else {
		// System.out
		// .println("Este proceso solo debe trabajar con lineas");
		// System.out.println(processedGeometry.getGeometryType());
		// }
		return geomNodes;
	}

	/**
	 * Checks if a coordinate is on a node of a given set of nodes
	 * 
	 * @param coord
	 * @param nodes
	 * @param processedGeometry
	 * @return
	 */
	private boolean checkIsNode(Coordinate coord, Coordinate[] nodes) {
		for (int i = 0; i < nodes.length; i++) {
			if (coord.distance(nodes[i]) <= snapTolerance) {
				return true;
			}
		}
		return false;
	}

	private boolean isSamePoint(Coordinate coord1, Coordinate coord2) {
		if (coord1.distance(coord2) < 0.000001)
			return true;
		else
			return false;
	}

	/**
	 * Comprueba si las dos geometrias realmente corresponden a la misma
	 * independientemente de si son LineString o MultiLineString. Comprueba
	 * coordenada a coordenada si contienen los mismos valores
	 * 
	 * @param geom1
	 *            - Primera geometria
	 * @param geom2
	 *            - Segunda geometria
	 * @return - booleano que es TRUE en caso de que sean la misma geometria.
	 *         FALSE en caso contrario
	 */
	private boolean isSameGeometry(Geometry geom1, Geometry geom2) {
		if (geom1.getBoundary().equals(geom2.getBoundary())) {
			Coordinate[] coords1 = geom1.getCoordinates();
			Coordinate[] coords2 = geom2.getCoordinates();
			if (coords1.length != coords2.length)
				return false;
			for (int i = 0; i < coords1.length; i++) {
				if (!isSamePoint(coords1[i], coords2[i]))
					return false;
			}
		}
		return true;
	}

	/**
	 * Anyade la coordenada como nodo si esta se encuentra en mas de una ocasion
	 * dentro de la multilinea. Esto significa que hay una interseccion.
	 * Solamente se comprobara la primera coordenada de cada linea de la
	 * multilinea, porque la segunda siempre coincidira con la primera de la
	 * siguiente linea
	 * 
	 * @param coord
	 *            - coordenada a comprobar si es nodo
	 * @param multiLine
	 *            - conjunto de lineas de la geometria
	 * @param i
	 *            - indice de la linea que se esta tratando
	 * @param fid1
	 *            - indice de la feature de la capa que se esta tratando
	 * @param fid2
	 *            - indice de la feature de la capa con la que se esta
	 *            comprobando posibles intersecciones
	 * @throws VisitorException
	 */
	private void addIfNode(Coordinate coord, MultiLineString multiLine, int i,
			int fid1, int fid2) throws VisitorException {
		boolean isNode = false;
		for (int j = 0; j < multiLine.getNumGeometries(); j++) {
			if (j == i)
				continue;
			LineString line2 = (LineString) multiLine.getGeometryN(j);
			Coordinate c1 = line2.getCoordinateN(0);
			if (j == multiLine.getNumGeometries() - 1) {
				Coordinate c2 = line2.getCoordinateN(line2.getNumPoints() - 1);
				if (isSamePoint(coord, c2))
					isNode = true;
			}
			if (isSamePoint(coord, c1)) {
				isNode = true;
				break;
			}
		}
		if (isNode) {
			nodes.addNode(coord);
			if (snapCoordMap.containsKey(coord))
				return;
			else {
				snapCoordMap.put(coord, coord);
				IFeature feature = createIntersectFeature(coord, fid1, fid2);
				intersectProcessor.processFeature(feature);
			}
		}
	}

	private IFeature createIntersectFeature(Coordinate coord, int fid1, int fid2) {
		IFeature solution = null;
		Point point = FConverter.geomFactory.createPoint(coord);
		IGeometry cleanedGeometry = FConverter.jts_to_igeometry(point);
		Value[] values = new Value[2];
		values[0] = ValueFactory.createValue(fid1);
		values[1] = ValueFactory.createValue(fid2);
		solution = FeatureFactory.createFeature(values, cleanedGeometry);
		return solution;
	}

	/**
	 * From a given geometry, and the intersection of this geometry with another
	 * geometry, it creates a new node with these intersections if its points
	 * are not coincident with the nodes of the original goemetry.
	 * 
	 * @throws VisitorException
	 * 
	 */
	private void processIntersections(
			com.vividsolutions.jts.geomgraph.SnappingNodeMap nodes,
			Geometry processedGeometry, Geometry intersections, int fid1,
			int fid2) throws VisitorException {

		Coordinate[] geomNodes = getNodesFor(processedGeometry);
		if (intersections instanceof Point) {
			Point p = (Point) intersections;
			Coordinate coord = p.getCoordinate();
			if (!checkIsNode(coord, geomNodes)) {
				nodes.addNode(coord);

				/*
				 * We are computing intersections twice: A intersection B and B
				 * intersection A. This is simpler than manage caches. With this
				 * logic, we avoid to write the same pseudonode twice
				 */
				if (snapCoordMap.containsKey(coord))
					return;
				else {
					snapCoordMap.put(coord, coord);
					IFeature feature = createIntersectFeature(coord, fid1, fid2);
					intersectProcessor.processFeature(feature);
				}
			}
		} else if (intersections instanceof MultiPoint) {
			MultiPoint points = (MultiPoint) intersections;
			for (int i = 0; i < points.getNumGeometries(); i++) {
				Coordinate coord = ((Point) points.getGeometryN(i))
						.getCoordinate();
				if (!checkIsNode(coord, geomNodes)) {
					nodes.addNode(coord);
					if (!snapCoordMap.containsKey(coord)) {
						snapCoordMap.put(coord, coord);
						IFeature feature = createIntersectFeature(coord, fid1,
								fid2);
						intersectProcessor.processFeature(feature);
					}
					// if (snapCoordMap.containsKey(coord))
					// return;
					// else {
					// snapCoordMap.put(coord, coord);
					// IFeature feature = createIntersectFeature(coord, fid1,
					// fid2);
					// intersectProcessor.processFeature(feature);
					// }
				}
			}
		} else if (intersections instanceof LineString) {
			if (!isSameGeometry(intersections, processedGeometry)) {
				LineString line = (LineString) intersections;
				int numPoints = line.getCoordinates().length;
				Coordinate coord1 = line.getCoordinateN(0);
				Coordinate coord2 = line.getCoordinateN(numPoints - 1);
				if (!checkIsNode(coord1, geomNodes)) {
					nodes.addNode(coord1);
					if (snapCoordMap.containsKey(coord1))
						return;
					else {
						snapCoordMap.put(coord1, coord1);
						IFeature feature = createIntersectFeature(coord1, fid1,
								fid2);
						intersectProcessor.processFeature(feature);
					}
				}
				/*
				 * Hay que conseguir controlar que el nodo que pueda ser nodo
				 * final de la geometria y nodo 'intermedio', sea contemplado
				 * como este último caso. Ahora mismo, al ser nodo final, se
				 * descarta y no se anyade la feature al intersectProcessor
				 */
				if (checkIsNode(coord2, geomNodes)) {
					nodes.addNode(coord2);
					if (snapCoordMap.containsKey(coord2))
						return;
					else {
						snapCoordMap.put(coord2, coord2);
						IFeature feature = createIntersectFeature(coord2, fid1,
								fid2);
						intersectProcessor.processFeature(feature);
					}
				}
			}
		} else if (intersections instanceof MultiLineString) {
			/*
			 * Si la geometria tratada es exactamente la misma que tiene
			 * 'intersections', no se crea ninguna feature como punto de
			 * interseccion
			 */
			if (!isSameGeometry(intersections, processedGeometry)) {
				MultiLineString multiLine = (MultiLineString) intersections;
				for (int i = 0; i < multiLine.getNumGeometries(); i++) {
					LineString line = (LineString) multiLine.getGeometryN(i);
					Coordinate coord1 = line.getCoordinateN(0);
					addIfNode(coord1, multiLine, i, fid1, fid2);
				}// for i
			}// if
		} else if (intersections instanceof GeometryCollection) {
			GeometryCollection col = (GeometryCollection) intersections;
			for (int i = 0; i < col.getNumGeometries(); i++) {
				// Es posible que haya alguna geometria de tipo multipoint o
				// multipolygon
				processIntersections(nodes, processedGeometry,
						col.getGeometryN(i), fid1, fid2);
			}
		}
		// else if (intersections instanceof Polygon) {
		// System.out
		// .println("Un poligono interseccion de 2 lineas???");
		// }// else

	}

	public void visit(IGeometry g2, int indexOverlay) throws VisitorException,
			StopWriterVisitorException, ProcessVisitorException {

		if (g2 == null)
			return;

		// if (index == indexOverlay) {
		// return;

		if (onlySelection) {
			try {
				if (!layerToClean.getRecordset().getSelection()
						.get(indexOverlay))
					return;
			} catch (ReadDriverException e) {
				throw new ProcessVisitorException(recordSet.getName(), e,
						"Error verificando seleccion en clean");
			}// geometry g is not selected
		}// if onlySelection

		int geometryType = g2.getGeometryType();
		if (geometryType != XTypes.ARC && geometryType != XTypes.LINE
				&& geometryType != XTypes.MULTI)
			return;

		/*
		 * TODO De momento no vamos a tener en cuenta que la interseccion ya ha
		 * sido calculada... (Ver comentario al instanciar SnappingNodeMap) //
		 * ya ha sido tratado if(processedFeatures.get(indexOverlay)) return;
		 */
		Geometry jtsGeo2 = g2.toJTSGeometry();
		if (!checkForLineGeometry(jtsGeo2))
			return;

		if (index == indexOverlay) {
			MultiLineString multilineString = (MultiLineString) jtsGeo2;
			// multilineString.g
			// Geometry nodedLineStrings = (LineString)resultLineList.get(0);
			// for(int i=0;i<resultLineList.size();i++) {
			// nodedLineStrings =
			// nodedLineStrings.union((LineString)resultLineList.get(i));
			// }
			// return nodedLineStrings;
		}

		if (overlayOp == null)
			overlayOp = new SnappingOverlayOperation(jtsGeo, jtsGeo2,
					snapTolerance);
		else {
			overlayOp.setSecondGeometry(jtsGeo2);
		}

		Geometry intersections = overlayOp
				.getResultGeometry(SnappingOverlayOperation.INTERSECTION);

		processIntersections(nodes, jtsGeo, intersections, index, indexOverlay);

		// IFeature cleanedFeature;
		// try {
		// cleanedFeature = createFeature(newGeoJts,
		// index, indexOverlay);
		// } catch (DriverException e) {
		// throw new VisitException(
		// "Error al crear el feature resultante del CLEAN");
		// }
		// featureProcessor.processFeature(cleanedFeature);

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

	public String getProcessDescription() {
		return "Computing intersections of a polygon with its adjacents";
	}

	public void stop(FLayer layer) throws StopWriterVisitorException,
			VisitorException {
	}

	public boolean start(FLayer layer) throws StartVisitorException {
		return true;
	}

}
