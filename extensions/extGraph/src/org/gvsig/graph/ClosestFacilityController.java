package org.gvsig.graph;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.gvsig.graph.core.GraphException;
import org.gvsig.graph.core.GvFlag;
import org.gvsig.graph.core.IFlagListener;
import org.gvsig.graph.core.Network;
import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.gui.RouteControlPanel;
import org.gvsig.graph.gui.RouteReportPanel;
import org.gvsig.graph.solvers.OneToManySolver;
import org.gvsig.graph.solvers.Route;
import org.gvsig.graph.solvers.ShortestPathSolverAStar;
import org.gvsig.graph.tools.FlagListener;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.NoSuchTableException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.values.StringValue;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.ExternalData;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.LayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.shp.IndexedShpDriver;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.writers.shp.ShpWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.util.GvSession;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

/**
 * @author falario
 *
 */

public class ClosestFacilityController implements IFlagListener{
	
	//private ClosestFacilityDialog cfd;
	private Network network;
	private MapControl mpCtrl;
	
	//private Hashtable facilitiesLayers;
	//private Hashtable events;
	private GvFlag sourceEvent;
	private Hashtable facilities;
	private int toolMaxFacilitiesNumber;
	private int maxFacilitiesNumber;
	private double facilitiesMaxLimit;
	private boolean toEvent;
	private ArrayList solvedFacilities;
	private Hashtable solvedRoutes;
	
	private static final int EVENT_ADDED = 0;
	private static final int EVENT_REMOVED = 1;
	private static final int EVENTS_REMOVED = 2;
	private static final int EVENT_MODIFIED = 3;
	
	private static final int FACILITY_ADDED = 4;
	private static final int FACILITY_REMOVED = 5;
	private static final int FACILITIES_REMOVED = 6;
	private static final int FACILITY_MODIFIED = 7;
	
	private static final int SOLVEDFACILITY_ADDED = 8;
	private static final int SOLVEDFACILITIES_REMOVED = 9;
	
	private ArrayList closestFacilitiesListeners;
	private FLyrVect layerFacilities;
	private boolean onlySelectedFacilities;
	private GvFlag[] events;
	
	private boolean solutionFromEventSelected;
	private GvFlag solutionEventSelected;
	
	private PluginServices ps=PluginServices.getPluginServices(this);
	
	private static final Logger logger = Logger.getLogger(ClosestFacilityController.class.getName());
	
	public ClosestFacilityController(Network network, MapControl mpCtrl){
		this.network=network;
		this.network.addFlagListener(this);
		this.mpCtrl=mpCtrl;
		
		//this.facilitiesLayers=new Hashtable();
		this.facilities=new Hashtable();
		this.maxFacilitiesNumber=0;
		this.toolMaxFacilitiesNumber=15;
		this.toEvent=true;
		this.solvedFacilities=new ArrayList(this.toolMaxFacilitiesNumber);
		this.solvedRoutes=new Hashtable();		
		this.closestFacilitiesListeners=new ArrayList();
		this.layerFacilities=null;
	}
	
	/**
	 * Adds a ClosestFacilityListener to the ClosestFacilityController
	 * @param listener the ClosestFacilityListener to be added
	 */
	public void addClosestFacilityListener(IClosestFacilityListener listener){
		this.closestFacilitiesListeners.add(listener);
		logger.info("Listener: "+listener+" añadido");
	}
	
	/**
	 * Removes an ClosestFacilityListener from the ClosestFacilityController
	 * @param listener the ClosestFacilityListener to be removed
	 */
	public void removeClosestFacilityListener(IClosestFacilityListener listener){
		this.closestFacilitiesListeners.remove(listener);
		logger.info("Listener: "+listener+" borrado");
	}
	
	private void informListeners(int reason, GvFlag flag1, GvFlag flag2) {
		Iterator it=this.closestFacilitiesListeners.iterator();
			switch(reason){
				case EVENT_ADDED:
					logger.info("Evento "+flag1+" añadido");
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).eventAdded(flag1);
					}
					break;
				
				case EVENT_REMOVED:
					logger.info("Evento "+flag1+" borrado");
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).eventRemoved(flag1);
					}
					break;
				case EVENTS_REMOVED:
					logger.info("Borrados todos los eventos");
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).allEventsRemoved();
					}
					break;
				case EVENT_MODIFIED:
					logger.info("Modificación del evento "+flag1+" pasa a ser: "+flag2);
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).eventModified(flag1, flag2);
					}
					break;
				case FACILITY_ADDED:
					logger.info("Proveedor añadido: "+flag1);
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).facilityAdded(flag1);
					}
					break;
				case FACILITY_REMOVED:
					logger.info("Proveedor borrado: "+flag1);
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).facilityRemoved(flag1);
					}
					break;
				case FACILITIES_REMOVED:
					logger.info("Todos los proveedores han sido borrados");
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).allFacilitiesRemoved();
					}
					break;
				case FACILITY_MODIFIED:
					logger.info("Modificación del proveedor "+flag1+" pasa a ser: "+flag2);
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).facilityModified(flag1, flag2);
					}
					break;
				case SOLVEDFACILITY_ADDED:
					logger.info("Añadido un proveedor a la solución: "+flag1);
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).addedSolvedFacility(flag1);
					}
					break;
				case SOLVEDFACILITIES_REMOVED:
					logger.info("Los proveedores de la solución han sido eliminados");
					while(it.hasNext()){
						((IClosestFacilityListener)it.next()).removedSolvedFacilities();
					}
					break;
			}
	}
	
	/**
	 * Returns the network used in the ClosestFacilityController
	 * @return the network used in the ClosestFacilityController
	 */
	public Network getNetwork(){
		return this.network;
	}
	
	/**
	 * Sets the network to be used in the ClosestFacilityController
	 * @param network The networ wich will be used in the ClosestFacilityController
	 */
	public void setNetwork(Network network){
		this.network=network;
	}
	
	/**
	 * The maximum number of facilities to be considered in the solution
	 * @param maxFacilitiesNumber sets the maximum number of facilities that will be considered in the solution
	 */
	public void setMaxFacilitiesNumber(int maxFacilitiesNumber){
		this.maxFacilitiesNumber = (maxFacilitiesNumber<=this.toolMaxFacilitiesNumber)? maxFacilitiesNumber : this.toolMaxFacilitiesNumber;
		//this.cfd.setMaxFacilitiesNumber(maxFacilitiesNumber);
	}
	
	/**
	 * Gets the maximum number of facilities to be considered in the solution
	 * @return the maximum number of facilities to be considered in the solution
	 */
	public int getMaxFacilitiesNumber(){
		return this.maxFacilitiesNumber;
	}
	
	/**
	 * Set the default maximum number of facilities to be considered in the solution
	 * @param toolMaxFacilitiesNumber the default maximum number of facilities to be considered in the solution
	 */
	public void setToolMaxFacilitiesNumber(int toolMaxFacilitiesNumber){
		this.toolMaxFacilitiesNumber=toolMaxFacilitiesNumber;
	}
	
	/**
	 * Gets the default maximum number of facilities to be considered in the solution
	 * @return the default maximum number of facilities to be considered in the solution
	 */
	public int getToolMaxFacilitiesNumber(){
		return this.toolMaxFacilitiesNumber;
	}
	
	/**
	 *  It won't be considered in the solution facilities that have a much higher cost than this property
	 * @return the maximum cost
	 */
	public double getFacilitiesMaxLimit(){
		return this.facilitiesMaxLimit;
	}
	
	/**
	 * It won't be considered in the solution facilities that have a much higher cost than this property
	 * @param facilitiesMaxLimit the maximum cost 
	 */
	public void setFacilitiesMaxLimit(double facilitiesMaxLimit){
		this.facilitiesMaxLimit=facilitiesMaxLimit;
	}
	
	/**
	 * Set the direction of the route from the facility to the event
	 */
	public void setToEvent(){
		this.toEvent=true;
	}
	
	/**
	 * Set the direction of the route from the event to the facility
	 */
	public void setFromEvent(){
		this.toEvent=false;
	}
	
	/**
	 * Determines if the direction of the route is from the facility to the event
	 * @return true if the direction of the route is from the facility to the event, false otherwise
	 */
	public boolean isToEvent(){
		return this.toEvent;
	}
	
	/**
	 * Determines if the direction of the route is from the event to the facility
	 * @return true if the direction of the route is from the event to the facility, false otherwise
	 */
	public boolean isFromEvent(){
		return !this.toEvent;
	}
	
	/**
	 * Center the graphics used in the ClosestFacilityController on the flag
	 * @param flag the graphics will be centered on this flag
	 */
	public void centerGraphicsOnFlag(GvFlag flag) {
		NetworkUtils.centerGraphicsOnFlag(this.mpCtrl, flag);	
	}
	
	/**
	 * The graphics used in the ClosestFacilityController will be centered on the minimum rectangle that contains all the flags
	 * @param flags the array of flags
	 */
	public void centerGraphicsOnFlags(GvFlag[] flags){
		NetworkUtils.centerGraphicsOnFlags(this.mpCtrl, flags);
	}
	
	/**
	 * Adds an event to the network used in the ClosestFacilityController
	 * @param event the event to be added
	 */
	private void addEvent(GvFlag event){
		this.network.addFlag(event);
		//NetworkUtils.addGraphicFlag(this.mpCtrl, event);
		//this.mpCtrl.drawGraphics();
		this.informListeners(EVENT_ADDED, event, null);
	}
	
	/**
	 * Shows the position of the flag on the graphics
	 * @param event the event to be shown
	 */
	public void addEventToGraphics(GvFlag event) {
		NetworkUtils.addGraphicFlag(this.mpCtrl, event);
		this.mpCtrl.drawGraphics();
	}
	
	/**
	 * Sets the event used to find the closest facility
	 * @param event the event used to find the closest facility
	 */
	public void setSourceEvent(GvFlag event){
		this.sourceEvent=event;
	}
	
	/**
	 * Gets the event used to find the closest facility
	 * @return the event used to find the closest facility
	 */
	public GvFlag getSourceEvent(){
		return this.sourceEvent;
	}
	
	private GvFlag createFlagFromGeometry(Geometry geom, double tol) throws GraphException{
		GvFlag flag=null;
		if (geom instanceof Point || geom instanceof MultiPoint){
			Coordinate[] coords = geom.getCoordinates();
			for (int j = 0; j < coords.length; j++) {
				flag = this.network.createFlag(coords[j].x, coords[j].y, tol);
				if (flag == null)
				{
					// segundo intento:
					flag = this.network.createFlag(coords[j].x, coords[j].y, 4*tol);
				}

			}
		}
		
		return flag;
	}

	public boolean getOnlySelectedFacilities(){
		return this.onlySelectedFacilities;
	}

	public void setOnlySelectedFacilities(boolean onlySelectedFacilities) {
		this.onlySelectedFacilities=onlySelectedFacilities;
	}
	
	public void setLayerFacilities(FLyrVect layerFacilities) {
		this.layerFacilities=layerFacilities;
	}
	
	public int loadFacilitiesFromLayer(FLyrVect layerFacilities, boolean onlySelectedFacilities) throws ReadDriverException, GraphException{
		long init=System.currentTimeMillis();
		GvFlag flag=null;
		int rejectedFacilities=0;
		int incrementIdFlag=0;
		int fieldDescriptionIndex=-1;
		this.removeAllFacilities();
		if(this.getSourceEvent()!=null){
			incrementIdFlag=this.getSourceEvent().getIdFlag()+1;
		}
		double realTol = this.mpCtrl.getViewPort().toMapDistance(FlagListener.pixelTolerance);
		SelectableDataSource ds=layerFacilities.getRecordset();
		FieldDescription[] fieldDesc=ds.getFieldsDescription();
		if(fieldDesc!=null){
			for (int i = 0; i < fieldDesc.length && fieldDescriptionIndex == -1; i++) {
				if(fieldDesc[i].getFieldName().equalsIgnoreCase("descript") && fieldDesc[i].getFieldType()==Types.VARCHAR){
					fieldDescriptionIndex=i;
				}
			}
		}
		if(onlySelectedFacilities){
			BitSet selection=layerFacilities.getRecordset().getSelection();
			for(int i=selection.nextSetBit(0), j=0; i>=0; i=selection.nextSetBit(i+1), j++) {
				flag=this.createFlagFromGeometry(layerFacilities.getSource().getShape(i).toJTSGeometry(), realTol);
				if(flag==null) rejectedFacilities++;
				else{
					if(fieldDescriptionIndex>=0 && fieldDescriptionIndex<fieldDesc.length){
						Value[] values=ds.getRow(i);
						if(values[fieldDescriptionIndex] instanceof StringValue){
							flag.setDescription(((StringValue)values[fieldDescriptionIndex]).toString());
						}
					}
					else{
						flag.setDescription(ps.getText("facility")+" "+j);
					}
					flag.setIdFlag(i+incrementIdFlag);
					this.addFacility(flag);
				}
			}
		}
		else{
			int shapesCount=layerFacilities.getSource().getShapeCount();
			for (int i = 0; i < shapesCount; i++) {
				flag=this.createFlagFromGeometry(layerFacilities.getSource().getShape(i).toJTSGeometry(), realTol);
				if(flag==null) rejectedFacilities++;
				else{
					if(fieldDescriptionIndex>=0 && fieldDescriptionIndex<fieldDesc.length){
						Value[] values=ds.getRow(i);
						if(values[fieldDescriptionIndex] instanceof StringValue){
							flag.setDescription(((StringValue)values[fieldDescriptionIndex]).toString());
						}
					}
					else{
						flag.setDescription(ps.getText("facility")+" "+i);
					}
					flag.setIdFlag(i+incrementIdFlag);
					this.addFacility(flag);
				}
			}
		}
		
		logger.info(ps.getText("facilities_loaded_in")+" "+(System.currentTimeMillis()-init)+" millis");
		return rejectedFacilities;
	}
	
	private SelectableDataSource getRecordset(IndexedShpDriver shpDriver) throws DriverLoadException, ReadDriverException, NoSuchTableException{
		SelectableDataSource ds;
		//VectorialFileDriver driver = (VectorialFileDriver) getDriver();

		if (shpDriver instanceof ExternalData) {
			ExternalData ed = (ExternalData) shpDriver;
			File dataFile = ed.getDataFile(shpDriver.getFile());
			String driverName = ed.getDataDriverName();

			String name = LayerFactory.getDataSourceFactory().addFileDataSource(driverName,
					dataFile.getAbsolutePath());
			//					 CHEMA: AUTOMATIC DATA SOURCE
			//ds = new SelectableDataSource(LayerFactory.getDataSourceFactory().createRandomDataSource(name, DataSourceFactory.MANUAL_OPENING));
			ds = new SelectableDataSource(LayerFactory.getDataSourceFactory().createRandomDataSource(name, DataSourceFactory.AUTOMATIC_OPENING));
		} else if (shpDriver instanceof ObjectDriver) {
			String name = LayerFactory.getDataSourceFactory().addDataSource((ObjectDriver)shpDriver);
			//					 CHEMA: AUTOMATIC DATA SOURCE
			//ds = new SelectableDataSource(LayerFactory.getDataSourceFactory().createRandomDataSource(name, DataSourceFactory.MANUAL_OPENING));
			ds = new SelectableDataSource(LayerFactory.getDataSourceFactory().createRandomDataSource(name, DataSourceFactory.AUTOMATIC_OPENING));
		} else {
			return null;
		}
		return ds;
	}

	public void loadEventsFromFile(File file) throws FileNotFoundException, NullPointerException, GraphException, ReadDriverException, DriverLoadException, NoSuchTableException, Exception{
//		Cargar el fichero seleccionado, comprobando que sea de puntos
		IndexedShpDriver shpDriver=null;
		SelectableDataSource ds=null;
		FieldDescription[] fieldDesc=null;
		int fieldDescriptionIndex=-1;

		if(!file.exists()){
			throw new FileNotFoundException(ps.getText("the_file")+" "+file.getName()+" "+ps.getText("doesnt_exist"));
		}
		shpDriver=new IndexedShpDriver();
		shpDriver.open(file);
		shpDriver.initialize();
		if(shpDriver.getShapeType()==FShape.POINT){
			ds=this.getRecordset(shpDriver);
			fieldDesc=ds.getFieldsDescription();
			if(fieldDesc!=null){
				for (int i = 0; i < fieldDesc.length && fieldDescriptionIndex == -1; i++) {
					if(fieldDesc[i].getFieldName().equalsIgnoreCase("descript") && fieldDesc[i].getFieldType()==Types.VARCHAR){
						fieldDescriptionIndex=i;
					}
				}
			}

			int shapeCount=shpDriver.getShapeCount();
			double realTol = this.mpCtrl.getViewPort().toMapDistance(FlagListener.pixelTolerance);
			GvFlag flag;
			for (int i = 0; i < shapeCount; i++) {
				/*FPoint2D point=(FPoint2D)shpDriver.getShape(i).getInternalShape();
					flag=this.network.createFlag(point.getX(), point.getY(), 10);
					this.network.addFlag(flag);
					//this.addEvent(flag);*/

				Geometry geo = shpDriver.getShape(i).toJTSGeometry();
				if (!((geo instanceof Point) || (geo instanceof MultiPoint)))
					continue;

				Coordinate[] coords = geo.getCoordinates();
				for (int j = 0; j < coords.length; j++) {
					flag = this.network.createFlag(coords[j].x, coords[j].y, realTol);
					if (flag == null)
					{
						// segundo intento:
						flag = this.network.createFlag(coords[j].x, coords[j].y, 4*realTol);
						if (flag == null)
						{								
							throw new NullPointerException(ps.getText("event")+" " + i + " "+ps.getText("out_of_the_network")+"."+" "+ps.getText("tolerance")+"=" + realTol);
							//NotificationManager.addError("No se puedo situar el registro " + i +"Por favor, compruebe que está encima de la red o aumente la toleracina.", e);
						}
					}
					if (flag != null)
					{
						if(fieldDescriptionIndex>=0 && fieldDescriptionIndex<fieldDesc.length){
							Value[] values=ds.getRow(i);
							if(values[fieldDescriptionIndex] instanceof StringValue){
								flag.setDescription(((StringValue)values[fieldDescriptionIndex]).toString());
							}
						}
						this.addEvent(flag);
					}

				} // for j
			} // for i
			shpDriver.close();
		}
		else{
			shpDriver.close();
			throw new Exception(ps.getText("only_points_in_file"));
		}
	}
	
	public void createSHPEvents(File file) throws InitializeWriterException, StartWriterVisitorException, StopWriterVisitorException, ProcessWriterVisitorException {
		LayerDefinition tDef = new LayerDefinition();
		tDef.setShapeType(FShape.POINT);
		FieldDescription[] fieldDescriptions=new FieldDescription[2];
		fieldDescriptions[0]=new FieldDescription();
		fieldDescriptions[0].setFieldName("IdFlag");
		fieldDescriptions[0].setFieldType(Types.INTEGER);
		fieldDescriptions[1]=new FieldDescription();
		fieldDescriptions[1].setFieldName("Descript");
		fieldDescriptions[1].setFieldType(Types.VARCHAR);
		fieldDescriptions[1].setFieldLength(50);
		
		tDef.setFieldsDesc(fieldDescriptions);
		 
		ShpWriter shpDrv = new ShpWriter();
		shpDrv.setFile(file);
		shpDrv.initialize(tDef);
		 
		shpDrv.preProcess();
		int rowIndex = 0;
		GvFlag[] events=this.network.getFlags();
		for (int i = 0; i < events.length; i++) {
			Value[] rowValues = new Value[]{ValueFactory.createValue(events[i].getIdFlag()), ValueFactory.createValue(events[i].getDescription())};
			IFeature feat = new DefaultFeature(ShapeFactory.createPoint2D(new FPoint2D(events[i].getOriginalPoint())), rowValues, "" + rowIndex);
			DefaultRowEdited row = new DefaultRowEdited(feat, IRowEdited.STATUS_ADDED, rowIndex);
			row.setAttributes(rowValues);
			shpDrv.process(row);
		}
		  
		shpDrv.postProcess();
	}
	
	public GvFlag getEvent(int idEvent){
		GvFlag[] flags=this.network.getFlags();
		GvFlag flag=null;
		
		for (int i = 0; i < flags.length && flag==null; i++) {
			if(flags[i].getIdFlag()==idEvent) flag=flags[i];
		}
		
		return flag;
	}
	

	public GvFlag getEventByIndex(int index) {
		try{
			return this.network.getFlags()[index];
		}
		catch(IndexOutOfBoundsException except){
			return null;
		}
	}
	
	public int getEventCount(){
		return this.network.getFlags().length;
	}
	
	public void removeEvent(GvFlag event){
		this.network.removeFlag(event);
		NetworkUtils.clearFlagFromGraphics(this.mpCtrl, event);
		//this.mpCtrl.drawGraphics();
		this.informListeners(EVENT_REMOVED, event, null);
	}
	
	public void removeEventFromGraphics(GvFlag event){
		NetworkUtils.clearFlagFromGraphics(this.mpCtrl, event);
		this.mpCtrl.drawGraphics();
	}
	
	public void removeAllEvents(){
		this.network.removeFlags();
		this.informListeners(EVENTS_REMOVED, null, null);
	}
	
	public void flashFacilitiesOnGraphics() throws ReadDriverException {
		Iterator it=this.facilities.values().iterator();
		
		this.mpCtrl.repaint();
		while(it.hasNext()){
			this.flashFlag((GvFlag)it.next(), Color.BLUE, 5);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void addFacility(GvFlag facility){
		if(this.facilities.get(facility.getIdFlag())==null){
			this.facilities.put(String.valueOf(facility.getIdFlag()), facility);
			this.informListeners(FACILITY_ADDED, facility, null);
		}
	}
	
	/**
	 * @param idFacility the id of the facility to be returned
	 * @return the facility that correspond with the specified id
	 */
	public GvFlag getFacility(int idFacility){
		return (GvFlag)this.facilities.get(String.valueOf(idFacility));
	}
	
	/**
	 * The ammount number of facilities
	 * @return the number of facilities
	 */
	public int getFacilitiesCount(){
		return this.facilities.size();
	}
	
	/**
	 * Removes the facilities with the specified Id
	 * @param idFacility the id of the facility to be removed
	 */
	public void removeFacility(int idFacility){
		GvFlag flag=null;
		if((flag=this.getFacility(idFacility))!=null){
			this.facilities.remove(idFacility);
			this.informListeners(FACILITY_REMOVED, flag, null);
		}
	}
	
	/**
	 * Removes all the facilities
	 */
	public void removeAllFacilities(){
		this.facilities=new Hashtable();
		this.informListeners(FACILITIES_REMOVED, null, null);
	}
	
	/*public GvFlag getSelectedEvent(){
		 return this.cfd.getSelectedEvent();
	}*/
	
	private void addSortedSolvedFacility(GvFlag flag){
		boolean added=false;
		int index=0;
		double flagCost=flag.getCost();
		if(flagCost<=this.facilitiesMaxLimit && flagCost>=0 && this.solvedFacilities.size()<this.maxFacilitiesNumber){
			for (index = 0; !added  && index < this.solvedFacilities.size(); index++) {
				if(flagCost<=((GvFlag)this.solvedFacilities.get(index)).getCost()){
					this.addSolvedFacility(index, flag);
					added=true;
				}
			}
		
			if(!added){
				index=this.solvedFacilities.size();
				this.addSolvedFacility(index, flag);
			}
		}
	}
	
	private void addSortedSolvedRoute(GvFlag flag, Route route) throws IndexOutOfBoundsException{
		boolean added=false;
		double routeCost=route.getCost();
		int index=0;
		if(routeCost<=this.facilitiesMaxLimit && this.solvedFacilities.size()<this.maxFacilitiesNumber){
			for (index = 0; !added && index < this.solvedFacilities.size(); index++) {
				if(routeCost<=((GvFlag)this.solvedFacilities.get(index)).getCost()){
					this.addSolvedFacility(index, flag);
					this.solvedRoutes.put(String.valueOf(index), route);
					added=true;
				}
			}

			if(!added){
				index=this.solvedFacilities.size();
				this.addSolvedFacility(index, flag);
				this.solvedRoutes.put(String.valueOf(index), route);
			}
		}
	}
	
	private void addSolvedFacility(int index, GvFlag flag) throws IndexOutOfBoundsException{
		this.solvedFacilities.add(index, flag);
		this.informListeners(SOLVEDFACILITY_ADDED, flag, null);
	}
	
	/**
	 * Gets the facility stored in the specified index. Remember that the solved facilities are stored in sorted mode, from the closest to the farthest
	 * @param index the position where is the facility
	 * @return the facility
	 * @throws IndexOutOfBoundsException when the index is less than 0 and bigger than the facilities number 
	 */
	public GvFlag getSolvedFacility(int index) throws IndexOutOfBoundsException{
		return (GvFlag)this.solvedFacilities.get(index);
	}
	
	/**
	 * Gets the event used to generate the solution
	 * @return the event used to generate the solution
	 */
	public GvFlag getSolvedEvent(){
		return this.solutionEventSelected;
	}
	
	private Route calculateRoute(Network network, GvFlag source, GvFlag destination) throws GraphException{
		logger.info("Calculando ruta desde "+source+" hasta "+destination);
		long iniTime=System.currentTimeMillis();
		ShortestPathSolverAStar solverToEvent=new ShortestPathSolverAStar();
		//this.network.setLayer(lyr);
		//this.network.setGraph(g);
		solverToEvent.setNetwork(network);
		String fieldStreetName = (String) network.getLayer().getProperty("network_fieldStreetName");
		solverToEvent.setFielStreetName(fieldStreetName);


		Route route=null;
		network.removeFlags();
		network.addFlag(source);
		network.addFlag(destination);
		route=solverToEvent.calculateRoute();
		network.removeFlags();
		source.setCost(route.getCost());
		logger.info("Ruta desde "+source+" hasta "+destination+" calculada en "+(System.currentTimeMillis()-iniTime)+" millis");
		return route;
	}
	
	/**
	 * Gets the route stored in the specified index. If the route doesn't exist it will be created with the source event specified when the route was solved and the facility that correspond to the route. Then, when the route is created, is stored for future accesses.
	 * @param index the position where is the route
	 * @return the route
	 * @throws GraphException
	 * @throws IndexOutOfBoundsException when the specified index is less than 0 and bigger than the number of solved facilities
	 */
	public Route getSolvedRoute(int index) throws GraphException, IndexOutOfBoundsException{
		Route route=null;
		try{
			route=(Route)this.solvedRoutes.get(String.valueOf(index));
			logger.info("Ruta \""+route+"\" obtenida de la coleccion");
		}
		catch(IndexOutOfBoundsException except){
			route=null;
		}
		if(route==null){
			GvFlag facility=this.getSolvedFacility(index);
			GvFlag source, destination;
			if(this.solutionFromEventSelected){
				source=this.solutionEventSelected;
				destination=facility;
			}
			else{
				source=facility;
				destination=this.solutionEventSelected;
			}
			
			GvFlag[] flags=this.getNetworkEvents(this.network);
			route=this.calculateRoute(this.network, source, destination);
			this.restoreNetworkEvents(this.network, flags);
			this.solvedRoutes.put(String.valueOf(index), route);
		}
		
		return route;
	}
	
	/**
	 * Draws the route over the network
	 * @param route the route that will be draw
	 */
	public void drawRouteOnGraphics(Route route){ 
		NetworkUtils.clearRouteFromGraphics(this.mpCtrl);
		
		NetworkUtils.drawRouteOnGraphics(this.mpCtrl, route);
		this.mpCtrl.drawMap(false);
	}
	
	/**
	 * Shows a frame with the instructions of the route
	 * @param route the route with the instructions to be shown
	 */
	public void showRouteReport(Route route) {
		RouteReportPanel routeReport=new RouteReportPanel(route, this.mpCtrl);
		List reportsPanels = (List) GvSession.getInstance().get(this.mpCtrl, "RouteReport");
		if(reportsPanels == null){
			reportsPanels = new ArrayList();
			GvSession.getInstance().put(this.mpCtrl, "RouteReport", reportsPanels);
		} 
		reportsPanels.add(routeReport);
		RouteControlPanel controlPanel = (RouteControlPanel) GvSession.getInstance().get(this.mpCtrl, "RouteControlPanel");
		if (controlPanel != null)
			controlPanel.refresh();

		PluginServices.getMDIManager().addWindow(routeReport);
	}
	
	/**
	 * @return the number of solved facilities
	 */
	public int getSolvedFacilitiesCount(){
		return this.solvedFacilities.size();
	}
	
	private void removeSolvedFacilities(){
		this.solvedFacilities=new ArrayList(this.maxFacilitiesNumber);
		this.solvedRoutes=new Hashtable();
		this.informListeners(SOLVEDFACILITIES_REMOVED, null, null);
	}
	
	/**
	 * Gives all the facilities sorted by the cost field from closest to farthest
	 * @return all the facilities
	 */
	public Iterator getSortedSolvedFacilities(){
		return this.solvedFacilities.iterator();
	}
	
	/**
	 * Gives all the routes sorted by the cost field from closest to farthest
	 * @return all the routes
	 */
	public Iterator getSortedSolvedRoutes(){
		return this.solvedRoutes.values().iterator();
	}
	

	/**
	 * Shows an animation wich points with a circle to the position where is the flag
	 * @param flag the pointed flag
	 * @param color the color of the circle wich will point the flag
	 * @param maxCount level of transparency of the circle
	 */
	public void flashFlag(GvFlag flag, Color color, int maxCount) {
		NetworkUtils.flashPoint(this.mpCtrl, color, maxCount, flag.getOriginalPoint().getX(), flag.getOriginalPoint().getY());	
	}
	
	/**
	 * Clear the flashes in the graphics
	 */
	public void clearFlashes(){
		this.mpCtrl.repaint();
	}

	/* (non-Javadoc)
	 * @see org.gvsig.graph.core.IFlagListener#flagsChanged(int)
	 */
	public void flagsChanged(int reason) {
		switch(reason){
			case IFlagListener.FLAG_ADDED:
				this.informListeners(EVENT_ADDED, null, null);
				break;
			case IFlagListener.FLAG_REMOVED:
				this.informListeners(EVENT_REMOVED, null, null);
				break;
		}
		this.mpCtrl.drawMap(false);
	}
	
	private GvFlag[] getNetworkEvents(Network network) {
		return network.getFlags();
	}
	
	private void restoreNetworkEvents(Network network, GvFlag[] events){
		for (int i = 0; i < events.length; i++) {
			network.addFlag(events[i]);
		}
	}
	
	/**
	 * Finds and stores in sorted mode the closest facilities. Cost field is used to sort the facilities from the closest to the farthest. 
	 * @throws GraphException
	 * @throws ReadDriverException
	 */
	public void solve() throws GraphException, ReadDriverException{
		this.solutionEventSelected=this.sourceEvent;
		this.solutionFromEventSelected=!this.toEvent;
		
		long initialTime=System.currentTimeMillis();
		this.network.removeFlagListener(this);
		this.events=this.getNetworkEvents(this.network);
		this.network.removeFlags();
		this.removeSolvedFacilities();
		if(this.facilities.size()>0){
			this.maxFacilitiesNumber=(this.maxFacilitiesNumber<=0 || this.maxFacilitiesNumber>this.toolMaxFacilitiesNumber)? this.toolMaxFacilitiesNumber : this.maxFacilitiesNumber;
			this.facilitiesMaxLimit=(this.facilitiesMaxLimit<=0)? Double.MAX_VALUE : this.facilitiesMaxLimit;
			if(this.getSourceEvent()!=null){
				if(this.toEvent){
					ShortestPathSolverAStar solverToEvent=new ShortestPathSolverAStar();
					//this.network.setLayer(lyr);
					//this.network.setGraph(g);
					solverToEvent.setNetwork(this.network);

					Iterator it=this.facilities.values().iterator();
					GvFlag flag=null;
					Route route=null;
					while(it.hasNext() && this.solvedFacilities.size()<this.maxFacilitiesNumber) {
						flag=(GvFlag)it.next();
						route=this.calculateRoute(this.network, flag, this.solutionEventSelected);
						if(route.getCost()<=this.facilitiesMaxLimit)
							flag.setCost(route.getCost());
							this.addSortedSolvedRoute(flag, route);
					}
				}
				else{
					OneToManySolver solverFromEvent = new OneToManySolver();
					//this.network.setLayer(lyr);
					//this.network.setGraph(g);
					solverFromEvent.setNetwork(this.network);

					this.network.addFlag(this.getSourceEvent());
					solverFromEvent.setSourceFlag(this.getSourceEvent());

					Iterator it=this.facilities.values().iterator();
					while(it.hasNext()) {
						this.network.addFlag((GvFlag)it.next());
					}
					solverFromEvent.putDestinationsOnNetwork(this.network.getFlags());

					solverFromEvent.setExploreAllNetwork(false);
					solverFromEvent.setMaxCost(this.facilitiesMaxLimit);
					solverFromEvent.calculate();
					solverFromEvent.removeDestinationsFromNetwork(this.network.getFlags());

					GvFlag[] solvedFlags=this.network.getFlags();
					for (int i = 1; i < solvedFlags.length; i++) {
						this.addSortedSolvedFacility(solvedFlags[i]);
					}
				}
			}
		}
		this.network.removeFlags();
		this.restoreNetworkEvents(this.network, this.events);
		this.network.addFlagListener(this);
		logger.info("Solución obtenida en: "+(System.currentTimeMillis()-initialTime)+" millis");
	}
}