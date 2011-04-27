package com.iver.cit.gvsig.geoprocess.impl.build.fmap;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.visitors.StopVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.CartographicSupportToolkit;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.rendering.FInterval;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;
import com.iver.cit.gvsig.fmap.rendering.VectorialIntervalLegend;
import com.iver.cit.gvsig.geoprocess.core.fmap.AbstractGeoprocess;
import com.iver.cit.gvsig.geoprocess.core.fmap.FeaturePersisterProcessor2;
import com.iver.cit.gvsig.geoprocess.core.fmap.GeoprocessException;
import com.iver.cit.gvsig.geoprocess.core.fmap.XTypes;
import com.iver.cit.gvsig.project.documents.view.gui.View;
import com.iver.cit.gvsig.topology.NodeError;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;
import com.iver.utiles.swing.threads.IMonitorableTask;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateArrays;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.util.LinearComponentExtracter;
import com.vividsolutions.jts.operation.overlay.SnappingOverlayOperation;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jts.planargraph.Node;
import com.vividsolutions.jts.planargraph.NodeMap;

public class BuildGeoprocess extends AbstractGeoprocess {

	private List<IWriter> tempWriters = new ArrayList<IWriter>();

	private HashMap<IWriter,ISymbol> writer2sym = new HashMap<IWriter, ISymbol>();

	private HashMap<IWriter,IVectorLegend> writer2legend = new HashMap<IWriter, IVectorLegend>();

	private boolean onlyFirstLayerSelection = false;

	private ILayerDefinition resultLayerDefinition;

	private boolean createLyrsWithErrorGeometries = false;

	//Used to calculate pseudonodes
	private SnappingOverlayOperation overlayOp = null;	

	private static final SimpleMarkerSymbol symNodeError =(SimpleMarkerSymbol) SymbologyFactory.createDefaultSymbolByShapeType(FShape.POINT, Color.RED);
	private static final ILineSymbol symDangle = (ILineSymbol) SymbologyFactory.createDefaultSymbolByShapeType(FShape.LINE, Color.RED);
	private static final ILineSymbol symCutEdge = (ILineSymbol)SymbologyFactory.createDefaultSymbolByShapeType(FShape.LINE, Color.BLUE);
	private static final ILineSymbol symInvalidRing = (ILineSymbol)SymbologyFactory.createDefaultSymbolByShapeType(FShape.LINE, Color.YELLOW);
	static{
		symNodeError.setSize(10);
		symNodeError.setStyle(SimpleMarkerSymbol.SQUARE_STYLE);
		symNodeError.setOutlined(true);
		symNodeError.setOutlineColor(Color.RED);
	}

	FeaturePersisterProcessor2 processor;

	private static int numOcurrences = 0;

	private boolean applyDangleTolerance = false;
	private double dangleTolerance = 0.1;

	public BuildGeoprocess(FLayer layer) {
		this.firstLayer = (FLyrVect)layer;
	}

	@Override
	public void checkPreconditions() throws GeoprocessException {
	}

	@Override
	public ILayerDefinition createLayerDefinition() {
		if (resultLayerDefinition == null) {
			resultLayerDefinition = getLayerDefinition();
		}
		return resultLayerDefinition;
	}

	@Override
	public void process() throws GeoprocessException {
		try {
			createTask().run();
		} catch (Exception e) {
			throw new GeoprocessException("Error al ejecutar el geoproceso");
		}
	}

	@Override
	public void setParameters(Map params) throws GeoprocessException {
		Boolean firstLayerSelection = (Boolean) params.get("firstlayerselection");
		if (firstLayerSelection != null)
			this.onlyFirstLayerSelection = firstLayerSelection.booleanValue();

		Boolean groupOfLyrs = (Boolean) params.get("createlayerswitherrors");
		if (groupOfLyrs != null)
			this.createLyrsWithErrorGeometries = groupOfLyrs.booleanValue();
	}

	public IMonitorableTask createTask() {
		return new BuildTask();
	}

	private ILayerDefinition getLayerDefinition(){
		SHPLayerDefinition definition = new SHPLayerDefinition();
		definition.setShapeType(XTypes.POLYGON);
		FieldDescription[] fields = new FieldDescription[3];
		fields[0] = new FieldDescription();
		fields[0].setFieldLength(10);
		fields[0].setFieldName("FID");
		fields[0].setFieldType(XTypes.BIGINT);
		fields[1] = new FieldDescription();
		fields[1].setFieldLength(15);
		fields[1].setFieldDecimalCount(5);
		fields[1].setFieldName("AREA");
		fields[1].setFieldType(XTypes.DOUBLE);
		fields[2] = new FieldDescription();
		fields[2].setFieldLength(15);
		fields[2].setFieldName("PERIMETER");
		fields[2].setFieldType(XTypes.DOUBLE);
		fields[2].setFieldDecimalCount(5);
		definition.setFieldsDesc(fields);
		return definition;
	}

	private ILayerDefinition getAuxLayerDefinition(){
		SHPLayerDefinition definition = new SHPLayerDefinition();
		definition.setShapeType(XTypes.POLYGON);
		FieldDescription[] fields = new FieldDescription[1];
		fields[0] = new FieldDescription();
		fields[0].setFieldLength(10);
		fields[0].setFieldName("FID");
		fields[0].setFieldType(XTypes.BIGINT);
		definition.setFieldsDesc(fields);
		return definition;
	}

	private ILayerDefinition getNodeLayerDefinition(){
		SHPLayerDefinition definition = new SHPLayerDefinition();
		definition.setShapeType(XTypes.POINT);
		FieldDescription[] fieldDescriptions = new FieldDescription[2];

		fieldDescriptions[0] = new FieldDescription();
		fieldDescriptions[0].setFieldLength(10);
		fieldDescriptions[0].setFieldName("FID");
		fieldDescriptions[0].setFieldType(XTypes.BIGINT);

		fieldDescriptions[1] = new FieldDescription();
		fieldDescriptions[1].setFieldLength(2);
		fieldDescriptions[1].setFieldName("NODETYPE");
		fieldDescriptions[1].setFieldType(XTypes.INTEGER);	

		definition.setFieldsDesc(fieldDescriptions);			
		return definition;
	}

	private void writeGeometriesInMemory(Collection<Geometry> geometries, int geometryType, ISymbol symbol, String fileName) throws InitializeWriterException, SchemaEditionException, VisitorException {

		ShpWriter writer = new ShpWriter();		
		File newFile = new File(fileName);
		writer.setFile(newFile);
		SHPLayerDefinition schema = (SHPLayerDefinition) getAuxLayerDefinition();
		schema.setShapeType(geometryType);
		writer.initialize(schema);
		schema.setFile(newFile);
		ShpSchemaManager schemaManager =
			new ShpSchemaManager(newFile.getAbsolutePath());
		schemaManager.createSchema(schema);
		FeaturePersisterProcessor2 tempProcessor = new
		FeaturePersisterProcessor2(writer);
		tempProcessor = new FeaturePersisterProcessor2(writer);
		tempProcessor.start();
		Iterator<Geometry> it = geometries.iterator();
		int i = 0;
		while(it.hasNext()){
			Geometry geom = (Geometry) it.next();
			Value[] values = new Value[1];
			values[0] = ValueFactory.createValue(i);
			IGeometry igeom = FConverter.jts_to_igeometry(geom);
			DefaultFeature feature = new
			DefaultFeature(igeom, values, (i+""));
			tempProcessor.processFeature(feature);
			i++;
		}
		tempProcessor.finish();
		//We save the information to recover these layers after
		//(to add them to the toc)
		tempWriters.add(writer);

		//saves the symbol specified for this writer, to use it
		//when we will add the layer derived of the writer to the TOC
		writer2sym.put(writer, symbol);
	}

	public FLayer getResult() throws GeoprocessException {
		if(! createLyrsWithErrorGeometries){
			// user choose in GUI not to load errors in TOC
			return super.getResult();
		}else{
			if(tempWriters.size() == 0)
				return super.getResult();
			IWriter[] writers = new IWriter[tempWriters.size()];
			tempWriters.toArray(writers);

			MapContext map = ((View)PluginServices.getMDIManager().
					getActiveWindow()).getModel().getMapContext();
			FLayers solution = new FLayers();//(map,map.getLayers());
			solution.setMapContext(map);
			solution.setParentLayer(map.getLayers());
			solution.setName("Build");
			solution.addLayer(super.getResult());
			for(int i = 0; i < writers.length; i++){
				FLyrVect layer = (FLyrVect) createLayerFrom(writers[i]);
				ISymbol symbol = (ISymbol) writer2sym.get(writers[i]);
				IVectorLegend legend = null;
				if (writer2legend.containsKey(writers[i])){
					legend = writer2legend.get(writers[i]);					
				}else{
					legend = new SingleSymbolLegend(symbol);
				}
				try {
					layer.setLegend(legend);
				} catch (Exception e) {
					throw new GeoprocessException("Error al crear la leyenda de una de las capas resultado", e);
				}

				solution.addLayer(layer);
			}
			return solution;
		}//else
	}



	private class BuildTask extends AbstractMonitorableTask {
		private BuildIntersection buildIntersection = null;

		public BuildTask() {
			setInitialStep(0);
			int additionalSteps = 2;
			try {
				if (onlyFirstLayerSelection) {
					int numSelected = firstLayer.getRecordset().getSelection().cardinality();
					setFinalStep(numSelected + additionalSteps);
				} else {
					int numShapes = firstLayer.getSource().getShapeCount();
					setFinalStep(numShapes + additionalSteps);
				}// else
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
			setDeterminatedProcess(true);
			setStatusMessage(PluginServices.getText(this,"PolygonBuild._Progress_Message"));
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

		// TODO Move all this stuff and remove
		boolean verifyCancelation(ReadableVectorial va) {
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
		 * Processes the record "record" of the given vectorial data source
		 * (ReadableVectorial) to extract linear components with "lineFilter".
		 * (Previous step necessary to polygonize a linear layer).
		 *
		 * If user has choosen add to the toc a group of layers (with
		 * topological errors, pseudonodes, dangles, etc) this method computes
		 * pseudonodes also.
		 * @throws StopWriterVisitorException
		 */
		void process(ReadableVectorial va,FeaturePersisterProcessor2 processor,LinearComponentExtracter lineFilter,NodeMap dangleNodeMap, List<Coordinate> pseudonodeList, NodeMap unknownNodeMap, int record) throws ReadDriverException, StopVisitorException {

			if (verifyCancelation(va)) {
				processor.finish();
				return;
			}
			reportStep();
			IGeometry g;
			try {
				g = va.getShape(record);
			} catch (ExpansionFileReadException e) {
				throw new ReadDriverException(va.getDriver().getName(),e);
			}
			if(g == null)
				return;
			Geometry jtsGeom = g.toJTSGeometry();
			jtsGeom.apply(lineFilter);

			// In parallel to the line filtering, if nodeMap != null we
			// look for pseudonodes
			if(dangleNodeMap != null){
				Coordinate[] coords = jtsGeom.getCoordinates();
				if (jtsGeom.isEmpty())
					return;
				Coordinate[] linePts = CoordinateArrays.removeRepeatedPoints(coords);
				Coordinate startPt = linePts[0];
				Coordinate endPt = linePts[linePts.length - 1];

				NodeError nStart = (NodeError) dangleNodeMap.find(startPt);
				NodeError nEnd = (NodeError) dangleNodeMap.find(endPt);
				if (startPt.equals2D(endPt)){
					pseudonodeList.add(startPt);
				}else{
					if (nStart == null){
						nStart = new NodeError(startPt);
						dangleNodeMap.add(nStart);
					}else{
						nStart.setOccurrences(nStart.getOccurrences()+1);
					}
					if (nEnd == null){
						nEnd = new NodeError(endPt);
						dangleNodeMap.add(nEnd);
					}else{
						nEnd.setOccurrences(nEnd.getOccurrences()+1);
					}
				}
			}// if nodeMap
			if (pseudonodeList != null){
				if (buildIntersection == null){
					buildIntersection = new BuildIntersection(jtsGeom);
				}else{
					buildIntersection.intersection(jtsGeom, unknownNodeMap);				
				}
			}
		}		

		@SuppressWarnings({"unchecked"})
		public void run() throws Exception {
			processor = new FeaturePersisterProcessor2(writer);
			Polygonizer polygonizer = new Polygonizer();
			try {
				processor.start();
				ReadableVectorial va = firstLayer.getSource();
				va.start();

				List<LineString> linesList = new ArrayList<LineString>();
				LinearComponentExtracter lineFilter = new LinearComponentExtracter(linesList);

				NodeMap dangleNodeMap = null;
				List<Coordinate> pseudonodeList = new ArrayList<Coordinate>();
				NodeMap unknownNodeMap = null;

				dangleNodeMap = new NodeMap();	
				pseudonodeList = new ArrayList<Coordinate>();
				unknownNodeMap = new NodeMap();

				if (onlyFirstLayerSelection) {
					FBitSet selection = firstLayer.getRecordset().getSelection();
					for (int i = selection.nextSetBit(0); i >= 0; i = selection.nextSetBit(i + 1)) {
						process(va, processor, lineFilter, dangleNodeMap, pseudonodeList, unknownNodeMap, i);						
					}
				} 
				else {
					for (int i = 0; i < va.getShapeCount(); i++) {// for each
						process(va, processor, lineFilter, dangleNodeMap, pseudonodeList, unknownNodeMap, i);						
					}// for
				}// if selection
				va.stop();

				// here lineList has all the linear elements
				//				if (computeCleanBefore) {
				//					linesList = cleanLines(linesList);
				//				}
				Iterator<LineString> it = linesList.iterator();
				for (it = linesList.iterator(); it.hasNext(); ) {
					Geometry g = (Geometry) it.next();
					polygonizer.add(g);
				}

				reportStep();

				// First we save polygons from build
				Collection<Geometry> polygons = polygonizer.getPolygons();
				Iterator<Geometry> iterator = polygons.iterator();
				int ind = 0;
				while(iterator.hasNext()){
					Geometry geom = (Geometry)iterator.next();
					Polygon polygon = (Polygon)geom;
					Value[] values = new Value[3];
					values[0] = ValueFactory.createValue(ind);
					values[1] = ValueFactory.createValue(polygon.getArea());
					values[2] = ValueFactory.createValue(polygon.getLength());
					IGeometry igeom = FConverter.jts_to_igeometry(geom);
					DefaultFeature feature = new DefaultFeature(igeom, values, (ind+""));
					processor.processFeature(feature);
					ind++;
				}
				processor.finish();

				reportStep();
				if(createLyrsWithErrorGeometries){
					Collection<Geometry> cutEdgesLines = polygonizer.getCutEdges();
					Collection<Geometry> danglingLines = polygonizer.getDangles();
					Collection<Geometry> invalidRingLines = polygonizer.getInvalidRingLines();
					List<Geometry> nodes = new ArrayList<Geometry>();	

					// Obtain dangling nodes
					Iterator iter = dangleNodeMap.iterator();
					while (iter.hasNext()){
						NodeError node = (NodeError) iter.next();
						if (node.getOccurrences() == 1){
							FPoint2D p = FConverter.coordinate2FPoint2D(node.getCoordinate());
							IGeometry gAux = ShapeFactory.createPoint2D(p);
							nodes.add(gAux.toJTSGeometry());
						}// if
					}// while
					//Obtain pseudonodes
					List<Geometry> pseudoNodes = new ArrayList<Geometry>();		
					iter = pseudonodeList.iterator();
					while (iter.hasNext()){
						Coordinate coordinate = (Coordinate) iter.next();						
						FPoint2D p = FConverter.coordinate2FPoint2D(coordinate);
						IGeometry gAux = ShapeFactory.createPoint2D(p);
						pseudoNodes.add(gAux.toJTSGeometry());
					}// while
					//Classify unknown nodes
					Iterator coordinatesIterator = unknownNodeMap.iterator();
					while (coordinatesIterator.hasNext()){
						NodeError node = (NodeError) coordinatesIterator.next();
						FPoint2D p = FConverter.coordinate2FPoint2D(node.getCoordinate());
						IGeometry gAux = ShapeFactory.createPoint2D(p);
						if (node.getOccurrences() <= 2){
							pseudoNodes.add(gAux.toJTSGeometry());																
						}else{
							//nodes.add(gAux.toJTSGeometry());			
						}					
					}

					String fileName = ((ShpWriter)writer).getShpPath();
					String layerName = null;
					int fileNameStart = fileName.lastIndexOf(File.separator) + 1;
					if(fileNameStart == -1)
						fileNameStart = 0;
					layerName = fileName.substring(fileNameStart, fileName.length());
					if(layerName.endsWith(".shp"))
						layerName = layerName.substring(0, layerName.length() - 4);

					if(cutEdgesLines != null){
						if(cutEdgesLines.size() > 0)
							writeCutEdgeLines(cutEdgesLines, layerName);
					}

					if(danglingLines != null){
						//check to filter dangling lines by length
						//						if(applyDangleTolerance){
						Collection<Geometry> filteredDangles = new ArrayList<Geometry>();
						Iterator<Geometry> iterat = danglingLines.iterator();
						while(iterat.hasNext()){
							LineString geom = (LineString) iterat.next();
							if(geom.getLength() >= dangleTolerance)
								filteredDangles.add(geom);
						}//while
						danglingLines = filteredDangles;
						//						}
						if(danglingLines.size() > 0)
							writeDanglingLines(danglingLines, layerName);
					}

					if(invalidRingLines != null){
						if(invalidRingLines.size() > 0)
							writeInvalidRingLines(invalidRingLines, layerName);
					}

					if ((nodes != null) && (pseudonodeList != null)){
						if((nodes.size() > 0) || (pseudonodeList.size() > 0))
							writeNodeErrors(nodes, pseudoNodes, layerName);
					}
				}// if addGroupOfLayers
			} catch (ReadDriverException e) {
				e.printStackTrace();
			}
		}

		public String getNote() {
			String buildText = PluginServices.getText(this,
			"Generando_topologia_de_poligonos");
			String of = PluginServices.getText(this, "de");
			return buildText + " " + getCurrentStep() + " " + of + " "
			+ getFinishStep();
		}

		public void cancel() {
			setCanceled(true);
			BuildGeoprocess.this.cancel();
		}

		/* (non-Javadoc)
		 * @see com.iver.utiles.swing.threads.IMonitorableTask#finished()
		 */
		public void finished() {
			// TODO Auto-generated method stub

		}

		void writeCutEdgeLines(Collection<Geometry> cutEdgeLines, String layerName) throws InitializeWriterException, SchemaEditionException, VisitorException{
			String temp = System.getProperty("java.io.tmpdir") 	+ File.separatorChar + layerName +
			"_cutEdgeLines" +
			(numOcurrences++)+
			".shp";
			writeGeometriesInMemory(cutEdgeLines, XTypes.LINE, symCutEdge, temp);
		}

		void writeDanglingLines(Collection<Geometry> danglingLines, String layerName) throws InitializeWriterException, SchemaEditionException, VisitorException{
			String temp = System.getProperty("java.io.tmpdir")  + File.separatorChar +  layerName +
			"_danglingLines" +
			(numOcurrences++)+
			".shp";
			writeGeometriesInMemory(danglingLines, XTypes.LINE, symDangle, temp);
		}

		void writeInvalidRingLines(Collection<Geometry> invalidRingLines, String layerName) throws InitializeWriterException, SchemaEditionException, VisitorException{
			String temp = System.getProperty("java.io.tmpdir")  + File.separatorChar +  layerName +
			"_invalidRing" +
			(numOcurrences++)+
			".shp";
			writeGeometriesInMemory(invalidRingLines, XTypes.LINE,symInvalidRing, temp);
		}

		void writeNodeErrors(Collection<Geometry> pseudoNodes, Collection<Geometry> nodes, String layerName) throws InitializeWriterException, SchemaEditionException, VisitorException{
			String temp = System.getProperty("java.io.tmpdir") + File.separatorChar + layerName +
			"_nodes" +
			(numOcurrences++)+
			".shp";
			//writeGeometriesInMemory(nodeErrors, XTypes.POINT,symNodeError, temp);

			ShpWriter writer = new ShpWriter();		
			File newFile = new File(temp);
			writer.setFile(newFile);

			SHPLayerDefinition schema = (SHPLayerDefinition) getNodeLayerDefinition();
			schema.setShapeType(XTypes.POINT);
			writer.initialize(schema);
			schema.setFile(newFile);
			ShpSchemaManager schemaManager =
				new ShpSchemaManager(newFile.getAbsolutePath());
			schemaManager.createSchema(schema);
			FeaturePersisterProcessor2 tempProcessor = new
			FeaturePersisterProcessor2(writer);
			tempProcessor = new FeaturePersisterProcessor2(writer);
			tempProcessor.start();

			//Writing nodes and pseudonodes			
			writeNodeErrorsByType(tempProcessor, nodes.iterator(), 0);
			writeNodeErrorsByType(tempProcessor, pseudoNodes.iterator(), 1);


			tempProcessor.finish();
			//We save the information to recover these layers after
			//(to add them to the toc)
			tempWriters.add(writer);

			//saves the symbol specified for this writer, to use it
			//when we will add the layer derived of the writer to the TOC
			writer2sym.put(writer, symNodeError);	
			writer2legend.put(writer, getNodeErrorsLegend());
		}

		private void writeNodeErrorsByType(FeaturePersisterProcessor2 tempProcessor, Iterator it, int geometryType) throws VisitorException{
			int i = 0;
			while(it.hasNext()){
				Geometry geom = (Geometry) it.next();
				Value[] values = new Value[2];
				values[0] = ValueFactory.createValue(i);
				values[1] = ValueFactory.createValue(geometryType);
				IGeometry igeom = FConverter.jts_to_igeometry(geom);
				DefaultFeature feature = new
				DefaultFeature(igeom, values, (i+""));
				tempProcessor.processFeature(feature);
				i++;
			}
		}

		private IVectorLegend getNodeErrorsLegend(){
			VectorialIntervalLegend legend = new VectorialIntervalLegend();
			legend.setShapeType(FShape.POINT);

			String[] fields = new String[1];
			fields[0] = "NODETYPE";
			legend.setClassifyingFieldNames(fields);

			//Symbol for pseudonodes
			SimpleMarkerSymbol pseudonodesSymbol = new SimpleMarkerSymbol();
			pseudonodesSymbol.setColor(Color.RED);
			pseudonodesSymbol.setUnit(CartographicSupportToolkit.DefaultMeasureUnit);
			pseudonodesSymbol.setSize(10);
			pseudonodesSymbol.setDescription("Pseudonodes");
			legend.addSymbol(new FInterval(0, 0), pseudonodesSymbol);

			//Symbol for nodes
			SimpleMarkerSymbol nodesSymbol = new SimpleMarkerSymbol();
			nodesSymbol.setColor(new Color(170,0,85,255));
			nodesSymbol.setUnit(CartographicSupportToolkit.DefaultMeasureUnit);
			nodesSymbol.setSize(10);	
			nodesSymbol.setDescription("Dangle Nodes");
			legend.addSymbol (new FInterval(1, 1), nodesSymbol);

			return legend;
		}

	}// PolygonBuildTask

	private class BuildIntersection{
		List<Geometry> geometries = new ArrayList<Geometry>();

		BuildIntersection(Geometry geometry) {
			super();
			geometries.add(geometry);
		}

		void intersection(Geometry geometry,NodeMap unknownNodeMap){
			for (int i=0 ; i<geometries.size() ;i++){
				Geometry intersectionGeom = geometry.intersection(geometries.get(i));
				processIntersections(unknownNodeMap, geometry, intersectionGeom);
			}
			geometries.add(geometry);
		}

		private void processIntersections(NodeMap unknownNodeMap, Geometry processedGeometry, Geometry intersections) {
			if (intersections instanceof Point) {
				Point p = (Point) intersections;				

				NodeError node = (NodeError)unknownNodeMap.find(p.getCoordinate());
				if (node == null){
					unknownNodeMap.add(new NodeError(p.getCoordinate()));
				}else{
					node.setOccurrences(node.getOccurrences()+1);					
				}
			} else if (intersections instanceof GeometryCollection) {
				GeometryCollection col = (GeometryCollection) intersections;
				for (int i = 0; i < col.getNumGeometries(); i++) {					
					processIntersections(unknownNodeMap, processedGeometry, col.getGeometryN(i));
				}
			}
		}
	}

}
