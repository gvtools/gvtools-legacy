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
package com.iver.cit.gvsig.fmap.layers;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.gvsig.exceptions.BaseException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.driverManager.Driver;
import com.hardcode.driverManager.DriverManager;
import com.hardcode.driverManager.IDelayedDriver;
import com.hardcode.driverManager.WriterManager;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.drivers.VectorialFileDriver;
import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.AttrInTableLabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;

/**
 * Crea un adaptador del driver que se le pasa como parámetro en los métodos
 * createLayer. Si hay memoria suficiente se crea un FLyrMemory que pasa todas
 * las features del driver a memoria
 * 
 * @deprecated use {@link GTLayerFactory} instead
 */
public class LayerFactory {
	// private static ArrayList<ISolveErrorListener> solveListeners=new
	// ArrayList<ISolveErrorListener>();
	private static Hashtable<Class, ISolveErrorListener> solveListeners = new Hashtable<Class, ISolveErrorListener>();

	private static Logger logger = Logger.getLogger(LayerFactory.class
			.getName());

	private static String driversPath = "../FMap 03/drivers";
	private static String writersPath = "../FMap 03/drivers";
	private static WriterManager writerManager;
	private static DataSourceFactory dataSourceFactory;

	/**
	 * Map en el que se guarda para cada fuente de datos añadida al sistema, el
	 * adaptador que la maneja. Ha de ser un TreeMap ya que esta clase define la
	 * igualdad entre las claves a traves del método equals de las mismas. Los
	 * objetos FileSource, DBSource y WFSSource tienen definido el método equals
	 * de forma que devuelven true cuando dos objetos apuntan a la misma fuente
	 * de datos
	 */
	private static TreeMap sourceAdapter;

	/**
	 * This Hashtable allows to register an alternative LayerClass for an
	 * specific LayerClass than is attempting to create this factory
	 */
	private static Hashtable layerClassMapping = new Hashtable();

	static {
		layerClassMapping.put("com.iver.cit.gvsig.fmap.layers.FLyrVect",
				FLyrVect.class);
	}

	/*
	 * Crea un RandomVectorialFile con el driver que se le pasa como parámetro y
	 * guardándose el nombre del fichero para realizar los accesos, la capa
	 * tendrá asociada la proyección que se pasa como parametro también
	 * 
	 * @param layerName Nombre de la capa. @param driverName Nombre del driver.
	 * 
	 * @param f fichero. @param proj Proyección.
	 * 
	 * @return FLayer. @throws DriverException
	 * 
	 * @throws DriverException @throws DriverIOException
	 */
	public static FLayer createLayer(String layerName, String driverName,
			File f, CoordinateReferenceSystem crs) throws LoadLayerException {
		return createLayer(layerName, driverName, f, crs, null);
	}

	public static FLayer createLayer(String layerName, String driverName,
			File f, CoordinateReferenceSystem crs, Color background)
			throws LoadLayerException {
		return createLayer(layerName, (VectorialFileDriver) null, f, crs,
				background);
	}

	/**
	 * It creates a FLayer (FLyrVect) which reads its data from a file driver,
	 * projected in the specified projection.
	 * 
	 * @param layerName
	 *            name of the layer
	 * @param d
	 *            vectorial file driver to read layer's data
	 * @param f
	 *            file associated to the driver
	 * @param crs
	 *            layer projection
	 * 
	 * @return FLayer new vectorial layer
	 * @throws IOException
	 */
	public static FLayer createLayer(String layerName, VectorialFileDriver d,
			File f, CoordinateReferenceSystem crs) {
		return createLayer(layerName, d, f, crs, null);
	}

	public static FLayer createLayer(String layerName, VectorialFileDriver d,
			File file, CoordinateReferenceSystem crs, Color background) {
		try {
			/*
			 * Ignore driver
			 */
			return GTLayerFactory.createVectorLayer(layerName, file, crs,
					background);
		} catch (IOException e) {
			throw new RuntimeException("Bug: Use GTLayerFactory please");
		}
	}

	private static FLyrVect tryToSolveError(BaseException e, FLayer layer,
			Driver d) {
		ISolveErrorListener sel = solveListeners.get(e.getClass());
		if (sel != null) {
			FLyrVect solvedLayer = null;
			solvedLayer = (FLyrVect) sel.solve(layer, d);
			if (solvedLayer != null && sel != null) {
				return solvedLayer;
			}
		}
		layer.setAvailable(false);
		layer.addError(e);
		return (FLyrVect) layer;
	}

	public static void addSolveErrorForLayer(Class exception,
			ISolveErrorListener sel) {
		solveListeners.put(exception, sel);
	}

	public static void removeSolveErrorListener(Class exception) {
		solveListeners.remove(exception);
	}

	/**
	 * Creates a new vectorial layer from a generic layer (by generic whe mean
	 * that we dont know a priory its origin: file, memory, jdbc database, etc.
	 * 
	 * @param layerName
	 * @param d
	 * @param crs
	 * @return
	 * @throws DriverException
	 */
	public static FLayer createLayer(String layerName, VectorialDriver d,
			CoordinateReferenceSystem crs) {
		VectorialAdapter adapter = null;
		if (d instanceof VectorialFileDriver) {
			adapter = new VectorialFileAdapter(
					((VectorialFileDriver) d).getFile());
		} else if (d instanceof IVectorialDatabaseDriver) {
			adapter = new VectorialDBAdapter();
		} else {
			adapter = new VectorialDefaultAdapter();
		}
		adapter.setDriver((VectorialDriver) d);
		// TODO azo:adapter needs a reference to the projection
		adapter.setCrs(crs);

		FLyrVect layer = null;
		try {
			Class clase = LayerFactory
					.getLayerClassForLayerClassName("com.iver.cit.gvsig.fmap.layers.FLyrVect");
			layer = (FLyrVect) clase.newInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}

		layer.setName(layerName);

		layer.setSource(adapter);
		layer.setCrs(crs);

		try {

			// Le asignamos también una legenda por defecto acorde con
			// el tipo de shape que tenga. Tampoco sé si es aquí el
			// sitio adecuado, pero en fin....
			if (d instanceof WithDefaultLegend) {
				WithDefaultLegend aux = (WithDefaultLegend) d;
				adapter.start();
				layer.setLegend((IVectorLegend) aux.getDefaultLegend());

				ILabelingStrategy labeler = aux.getDefaultLabelingStrategy();
				if (labeler != null) {
					labeler.setLayer(layer);
					layer.setLabelingStrategy(labeler);
					layer.setIsLabeled(true); // TODO: ací no s'hauria de
												// detectar si té etiquetes?????
				}

				adapter.stop();
			} else {
				layer.setLegend(LegendFactory.createSingleSymbolLegend(layer
						.getShapeType()));
			}
		} catch (LegendLayerException e) {

			layer.setAvailable(false);
			layer.addError(e);

		} catch (ReadDriverException e) {
			layer.setAvailable(false);
			layer.addError(e);
		}

		return layer;
	}

	/**
	 * Crea un RandomVectorialWFS con el driver que se le pasa como parámetro y
	 * guardándose la URL del servidor que se pasa como parámetro
	 * 
	 * @param driver
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @param dbName
	 * @param tableName
	 * @param crs
	 * 
	 * @return Capa creada.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public static FLayer createLayer(IVectorialDatabaseDriver driver,
			String host, int port, String user, String password, String dbName,
			String tableName, CoordinateReferenceSystem crs) {
		throw new UnsupportedOperationException();
	}

	public static FLayer createDBLayer(IVectorialDatabaseDriver driver,
			String layerName, CoordinateReferenceSystem crs) {
		return createDBLayer(driver, layerName, crs, null);
	}

	public static FLayer createDBLayer(IVectorialDatabaseDriver driver,
			String layerName, CoordinateReferenceSystem crs, Color background) {

		FLyrVect layer = null;
		try {
			Class clase = LayerFactory
					.getLayerClassForLayerClassName("com.iver.cit.gvsig.fmap.layers.FLyrVect");
			layer = (FLyrVect) clase.newInstance();
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}

		layer.setName(layerName);
		VectorialDBAdapter dbAdapter = new VectorialDBAdapter();
		dbAdapter.setDriver(driver);
		dbAdapter.setCrs(crs);// adapter needs also a ref to prj. review (azo)

		layer.setSource(dbAdapter);
		layer.setCrs(crs);
		try {
			if (driver instanceof WithDefaultLegend) {
				WithDefaultLegend aux = (WithDefaultLegend) driver;
				dbAdapter.start();
				layer.setLegend((IVectorLegend) aux.getDefaultLegend());

				ILabelingStrategy labeler = aux.getDefaultLabelingStrategy();
				if (labeler instanceof AttrInTableLabelingStrategy) {
					((AttrInTableLabelingStrategy) labeler).setLayer(layer);
				}
				layer.setLabelingStrategy(labeler);

				layer.setIsLabeled(true); // TODO: ací no s'hauria de detectar
											// si té etiquetes?????

				dbAdapter.stop();
			} else {
				layer.setLegend(LegendFactory.createSingleSymbolLegend(
						layer.getShapeType(), background));
			}
			if (driver instanceof IDelayedDriver) {
				// Por defecto, los drivers están listos para entregar
				// features al terminar su initialize. Pero con los drivers
				// que implementan IDelayedDriver, el driver es responsable
				// de avisar cuándo está listo
				layer.getFLayerStatus().setDriverLoaded(false);
				((IDelayedDriver) driver)
						.addDriverEventListener(new DefaultDelayedDriverListener(
								layer));
			}
		} catch (LegendLayerException e) {
			// LegendDriverExceptionType exceptType =
			// new
			// LegendDriverExceptionType("Error al construir la leyenda, campo no encontrado");
			// TODO Para hacer esto extensible tiene que usarse puntos
			// de extension, y comparar las clases de leyendas registradas
			// IVectorialLegend legend = (IVectorialLegend)
			// ((WithDefaultLegend)driver).getDefaultLegend();
			//
			// excepType.setLegendLabelField(legend.getLabelField());
			// excepType.setLegendHeightField(legend.getLabelHeightField());
			// excepType.setLegendRotationField(legend.getLabelRotationField());
			// DriverException exception = new DriverException(e, excepType);
			layer.setAvailable(false);
			layer.addError(e);
			return layer;
			// throw new UnsupportedOperationException(e.getMessage());
		} catch (Exception e) {
			// ExceptionDescription excepType = new
			// GenericDriverExceptionType();
			// DriverException exception = new DriverException(e, excepType);
			// layer.addError(null);
			layer.addError(new LoadLayerException(
					"No se ha podido cargar la capa", e));
			layer.setAvailable(false);
			return layer;
		}

		return layer;

	}

	/**
	 * Devuelve el DriverManager.
	 * 
	 * @return DriverManager.
	 */
	public static DriverManager getDM() {
		throw new RuntimeException("This has no sense anymore");
	}

	/**
	 * Devuelve el WriterManager.
	 * 
	 * @return WriterManager.
	 */
	public static WriterManager getWM() {
		initializeWriterManager();

		return writerManager;
	}

	/**
	 * Inicializa el DriverManager.
	 */
	private static void initializeWriterManager() {
		if (writerManager == null) {
			writerManager = new WriterManager();
			writerManager.loadWriters(new File(LayerFactory.writersPath));

			Throwable[] failures = writerManager.getLoadFailures();

			for (int i = 0; i < failures.length; i++) {
				logger.error("", failures[i]);
			}

			getDataSourceFactory().setWriterManager(writerManager);
			getDataSourceFactory().initialize();
			// QueryManager.registerQuery(new ArcJoin());
		}
	}

	/**
	 * sets drivers Directory
	 * 
	 * @param path
	 */
	public static void setDriversPath(String path) {
		throw new RuntimeException("This has no sense anymore");
	}

	/**
	 * sets writers Directory
	 * 
	 * @param path
	 */
	public static void setWritersPath(String path) {
		LayerFactory.writersPath = path;
		initializeWriterManager();
	}

	/**
	 * @return Returns the dataSourceFactory.
	 */
	public static DataSourceFactory getDataSourceFactory() {
		if (dataSourceFactory == null) {
			dataSourceFactory = new DataSourceFactory();
		}
		return dataSourceFactory;
	}

	public static void initialize() {
		initializeWriterManager();
	}

	/**
	 * Set a class to use instead of the originalLayerClassName.
	 * 
	 * @param originalLayerClassName
	 *            name of class to relpace
	 * @param layerClassToUse
	 *            Class than implements FLayer interface to use
	 * 
	 * @see getLayerClassForLayerClassName(String,Class)
	 * @see unregisterLayerClassForName(String)
	 */
	public static void registerLayerClassForName(String originalLayerClassName,
			Class layerClassToUse) {
		Class[] interfaces = layerClassToUse.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
			if (interfaces[i] == FLayer.class)
				break;
		}

		layerClassMapping.put(originalLayerClassName, layerClassToUse);
	}

	/**
	 * Unregister the originalLayerClassName class replacement.
	 * 
	 * @param originalLayerClassName
	 *            name of class to relpace
	 * @param layerClassToUse
	 *            Class than implements FLayer interface to use
	 * @return true if the class had been registered
	 * 
	 * @see getLayerClassForLayerClassName(String,Class)
	 * @see unregisterLayerClassForName(String)
	 */
	public static boolean unregisterLayerClassForName(
			String originalLayerClassName) {
		return layerClassMapping.remove(originalLayerClassName) != null;
	}

	/**
	 * Gets the class to use for the layerClassName. If isn't registered an
	 * alternative class for this layerClass the this returns
	 * 'Class.forName(layerClassName)'
	 * 
	 * @param layerClassName
	 * @return Class implements FLayer to use
	 * @throws ClassNotFoundException
	 * 
	 * @see registerLayerClassForName(String,Class)
	 * @see unregisterLayerClassForName(String)
	 */
	public static Class getLayerClassForLayerClassName(String layerClassName)
			throws ClassNotFoundException {
		Class layerClass = (Class) layerClassMapping.get(layerClassName);
		if (layerClass == null)
			layerClass = Class.forName(layerClassName);
		return layerClass;
	}
}
