/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.arcims.fmap.layers;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.cresques.cts.ProjectionUtils;
import org.cresques.geo.ViewPortData;
import org.cresques.px.Extent;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.fmap.raster.layers.IRasterLayerActions;
import org.gvsig.fmap.raster.layers.IStatusRaster;
import org.gvsig.fmap.raster.layers.StatusLayerRaster;
import org.gvsig.raster.dataset.CompositeDataset;
import org.gvsig.raster.dataset.IBuffer;
import org.gvsig.raster.dataset.MosaicNotValidException;
import org.gvsig.raster.dataset.MultiRasterDataset;
import org.gvsig.raster.grid.Grid;
import org.gvsig.raster.grid.GridPalette;
import org.gvsig.raster.grid.GridTransparency;
import org.gvsig.raster.grid.filter.FilterTypeException;
import org.gvsig.raster.grid.filter.RasterFilterList;
import org.gvsig.raster.grid.filter.RasterFilterListManager;
import org.gvsig.raster.grid.filter.bands.ColorTableFilter;
import org.gvsig.raster.grid.filter.bands.ColorTableListManager;
import org.gvsig.remoteClient.arcims.ArcImsClientP;
import org.gvsig.remoteClient.arcims.ArcImsProtImageHandler;
import org.gvsig.remoteClient.arcims.ArcImsProtocolHandler;
import org.gvsig.remoteClient.arcims.ArcImsStatus;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.MyCancellable;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayer;
import org.gvsig.remoteClient.utils.Utilities;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.Tiling;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.StringXMLItem;
import com.iver.cit.gvsig.fmap.layers.layerOperations.XMLItem;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.connections.ConnectionException;
import com.iver.utiles.swing.threads.Cancellable;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;

import es.prodevelop.cit.gvsig.arcims.fmap.drivers.ArcImsDriver;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapRasterArcImsDriver;
import es.prodevelop.cit.gvsig.arcims.fmap.listeners.FRasterLyrArcIMSListener;

/**
 * This class implements an ArcIMS raster layer.
 * 
 * @author jldominguez
 */
public class FRasterLyrArcIMS extends FLyrRasterSE {

	private static Logger logger = Logger.getLogger(FRasterLyrArcIMS.class
			.getName());
	public static final String SCALE_CHANGED_COMMAND = "SCALE_HAS_CHANGED";
	private MyCancellable myCanc;
	private URL host;
	private String service;
	private String serviceType;
	private Rectangle2D fullExtent;
	private boolean firstLoad = false;
	private ArcImsStatus arcimsStatus = new ArcImsStatus();
	private String SRS;
	private String layerQuery;
	private ArcImsDriver driver;
	private VisualStatusArcIms visualStatus = new VisualStatusArcIms();
	private int transparency = -1;
	private boolean arcImsTransparency = true;
	private String format;

	private RasterFilterList filterList = null;
	private GridTransparency gridTransparency = null;
	private int[] renderBands = null;
	private FLyrRasterSE layerRaster = null;
	private ArrayList filterArguments = null;

	private int posX = 0;
	private int posY = 0;
	private double posXWC = 0;
	private double posYWC = 0;
	private ArrayList actionListeners = new ArrayList();
	private int maxTileWidth = 1023;
	private int maxTileHeight = 1023;

	private Grid grid = null;

	private boolean nameQueryChange = false;
	private ArrayList listeners = new ArrayList();
	private boolean tiled = false;
	private int[] tileSize = new int[2];
	private ActionEvent scaleChangedEvent = new ActionEvent(this,
			ActionEvent.ACTION_PERFORMED, SCALE_CHANGED_COMMAND);

	public FRasterLyrArcIMS() {
		myCanc = new MyCancellable(new DefaultCancellableMonitorable());
	}

	/**
	 * The extCatalogYNomenclator needs this creator.
	 * 
	 * @param p
	 *            a Map object with the following keys:
	 * 
	 *            (key, object type returned) ---------------------------
	 *            "host", String (with or without the servlet path, example:
	 *            "http://www.geographynetwork.com") "service_name", String
	 *            (remote service name) "srs", String (coordinate system)
	 *            "layer_name", String (local layer's name)
	 * 
	 * @return a FRasterLyrArcIMS layer
	 */
	public FRasterLyrArcIMS(Map m) throws ConnectionException {
		myCanc = new MyCancellable(new DefaultCancellableMonitorable());

		String _host = "";

		try {
			_host = (String) m.get("host");

			String _service_name = (String) m.get("service_name");
			String _srs = (String) m.get("srs");
			String _image_format = "JPG";
			String _layer_name = (String) m.get("layer_name");

			// in case layer_name is missing or equals the empty string:
			if ((_layer_name == null) || (_layer_name.length() == 0)) {
				_layer_name = _service_name;
			}

			// ----------------------------
			URL _true_host = ArcImsProtocolHandler.getUrlWithServlet(new URL(
					_host));
			CoordinateReferenceSystem _true_srs = ProjectionUtils.getCRS(_srs);

			FMapRasterArcImsDriver _drv = new FMapRasterArcImsDriver(
					_true_host.toString(), _service_name,
					ServiceInfoTags.vIMAGESERVICE);

			if (!_drv.connect(myCanc)) {
				throw new ConnectionException("Unable to connect to host ",
						new Exception(_host));
			}

			setDriver(_drv);
			setHost(_true_host);
			setService(_service_name);
			setServiceType(ServiceInfoTags.vIMAGESERVICE);
			setServiceInformationInStatus(_drv.getClient()
					.getServiceInformation());

			ServiceInformation _si = getArcimsStatus().getServiceInfo();

			if ((_si.getFeaturecoordsys() == null)
					|| (_si.getFeaturecoordsys().equals(""))) {
				_si.setFeaturecoordsys(ProjectionUtils.getAbrev(_true_srs)
						.substring(ServiceInfoTags.vINI_SRS.length()).trim());
				logger.warn("Server provides no SRS. ");
			}

			setFullExtent(((ArcImsProtImageHandler) _drv.getClient()
					.getHandler()).getServiceExtent(_true_srs,
					getArcimsStatus()));

			// JPG
			setFormat(_image_format);
			setTransparency(0);

			// =================================== query
			int layercount = _si.getLayers().size();
			String _layer_query = "";

			for (int i = 0; i < layercount; i++) {
				if (isTrueString(((ServiceInformationLayer) _si.getLayer(i))
						.getVisible())) {
					_layer_query = _layer_query
							+ ","
							+ ((ServiceInformationLayer) _si.getLayer(i))
									.getId();
				}
			}

			if (_layer_query.length() == 0) {
				throw new Exception("No layers are visible by default ");
			} else {
				_layer_query = _layer_query.substring(1);
			}

			// ===================================
			setLayerQuery(_layer_query);
			setCrs(_true_srs);
			setName(_layer_name);
		} catch (Exception ex) {
			throw new ConnectionException(
					"While creating FRasterLyrArcIMS from Map", ex);
		}
	}

	/**
	 * Gets the driver object onto which <tt>getMap()</tt> and
	 * <tt>getFeatureInfo()</tt> requests are passed. If the driver was not
	 * created, this methdo creates it with the server's URL and the service
	 * name.
	 * 
	 * @return the driver that actually performs the requests.
	 */
	public ArcImsDriver getDriver() {
		if (driver == null) {
			driver = new FMapRasterArcImsDriver(host.toString(), service,
					this.serviceType);
		}

		return driver;
	}

	public void setDriver(FMapRasterArcImsDriver d) {
		driver = d;
	}

	/**
	 * Gets the transformation matrix to be written in the georeference file.
	 * The second and the third coefficients will always be zero (it doesn't
	 * implement image rotation)
	 * 
	 * @param bBox
	 *            Image's size and position (user's coordinates)
	 * @param sz
	 *            Images' size in pixels
	 * @return the worldfile, as a String
	 * @throws IOException
	 */
	public String getDataWorldFile(Rectangle2D bBox, Dimension sz)
			throws IOException {
		StringBuffer data = new StringBuffer();
		data.append(((bBox.getMaxX() - bBox.getMinX()) / (sz.getWidth() - 1))
				+ "\n");
		data.append("0.0\n");
		data.append("0.0\n");
		data.append("-"
				+ ((bBox.getMaxY() - bBox.getMinY()) / (sz.getHeight() - 1))
				+ "\n");

		// data.append((bBox.getMaxY() - bBox.getMinY()) / (sz.getHeight() - 1)
		// + "\n");
		data.append("" + bBox.getMinX() + "\n");

		// data.append("" + bBox.getMinY() + "\n");
		data.append("" + bBox.getMaxY() + "\n");

		return data.toString();
	}

	/**
	 * This method must be invoqued with <b>true</b> if the layer must show its
	 * transparency. The level of transparency is set by another method.
	 * 
	 * @param t
	 *            Whether or not the transparency must be shown.
	 */
	public void setArcImsTransparency(boolean t) {
		this.arcImsTransparency = t;
	}

	/**
	 * Drawing method invoqued from outside the plugin.
	 * 
	 * @param g
	 *            graphic object on which drawing is made.
	 * @param vp
	 *            the view
	 * @param cancel
	 *            allows drawing abortion
	 * @throws DriverException
	 */
	private void drawTile(Graphics2D g, ViewPort vp, Cancellable cancel,
			double scale) throws DriverException {
		// This is the extent that will be requested
		Rectangle2D bBox = vp.getAdjustedExtent(); // getFullExtent();
		Dimension sz = vp.getImageSize();

		try {
			arcimsStatus.setExtent(bBox);
			arcimsStatus.setFormat(getFormat());
			arcimsStatus.setHeight(vp.getImageHeight());
			arcimsStatus.setWidth(vp.getImageWidth());
			arcimsStatus.setLayerIds(Utilities.createVector(layerQuery, ","));
			arcimsStatus.setServer(host.toString());
			arcimsStatus.setService(service);
			arcimsStatus.setSrs(ProjectionUtils.getAbrev(getCrs()));
			arcimsStatus.setTransparency(this.arcImsTransparency);

			// We need to cast because driver returns an Object
			File f = (File) getDriver().getMap(arcimsStatus);

			int lastp = f.getPath().lastIndexOf(".");
			String noext = f.getPath().substring(0, lastp);

			String nameWorldFile = noext + getExtensionWordFile(getFormat());

			com.iver.andami.Utilities.createTemp(nameWorldFile,
					this.getDataWorldFile(bBox, sz));

			// ------------------------------ START
			ViewPortData vpData = new ViewPortData(vp.getCrs(),
					new Extent(bBox), sz);
			vpData.setMat(vp.getAffineTransform());

			// ------------------------------ END
			rasterProcess(f, g, vp, scale, cancel);
		} catch (Exception e) {
			DriverException de = new DriverException("While drawing tile: "
					+ e.getMessage());
			logger.error("While drawing tile: " + e.getMessage());
			throw de;
		}
	}

	/**
	 * Drawing method invoqued from gvSIG to draw this layer. Uses a
	 * <tt>Tiling</tt> object to prevent downloading images too large to be
	 * retrieved (pixel count limit)
	 * 
	 * @param image
	 *            used to speed up drawing process
	 * @param g
	 *            graphic object on which drawing is make
	 * @param viewPort
	 *            the view's properties
	 * @param cancel
	 *            allows drawing abortion
	 * @param scale
	 *            the current scale
	 * 
	 * @throws DriverException
	 */
	public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
			Cancellable cancel, double scale) throws ReadDriverException {
		if (!isVisible()) {
			return;
		}

		if (!isWithinScale(scale)) {
			return;
		}

		visualStatus.width = viewPort.getImageWidth();
		visualStatus.height = viewPort.getImageHeight();
		visualStatus.minX = viewPort.getAdjustedExtent().getMinX();
		visualStatus.minY = viewPort.getAdjustedExtent().getMinY();
		visualStatus.maxX = viewPort.getAdjustedExtent().getMaxX();
		visualStatus.maxY = viewPort.getAdjustedExtent().getMaxY();

		setTileDimensions(viewPort);

		Point2D p = viewPort.getOffset();
		Rectangle r = new Rectangle((int) p.getX(), (int) p.getY(),
				viewPort.getImageWidth() - 1, viewPort.getImageHeight() - 1);
		Tiling tiles = new Tiling(maxTileWidth, maxTileHeight, r);
		tiles.setAffineTransform((AffineTransform) viewPort
				.getAffineTransform().clone());

		tiled = false;

		try {
			// enableAwake();
			enableStopped();
		} catch (Exception e1) {
			throw new ReadDriverException("Error changing awake state", e1);
		}

		if (tiles.getNumTiles() > 1) {
			tiled = true;
			tileSize[0] = tiles.getMaxTileWidth();
			tileSize[1] = tiles.getMaxTileHeight();

			MultiRasterDataset[][] datasets = new MultiRasterDataset[tiles
					.getNumRows()][tiles.getNumCols()];
			IBuffer[][] buf = new IBuffer[tiles.getNumRows()][tiles
					.getNumCols()];

			// more than one tile ----------------------
			for (int tileNr = 0; tileNr < tiles.getNumTiles(); tileNr++) {
				// drawing part
				try {
					ViewPort vp = tiles.getTileViewPort(viewPort, tileNr);
					drawTile(g, vp, cancel, scale);

					if (layerRaster != null) {
						datasets[(int) (tileNr / tiles.getNumCols())][tileNr
								% tiles.getNumCols()] = (MultiRasterDataset) layerRaster
								.getDataSource().newDataset();
						buf[(int) (tileNr / tiles.getNumCols())][tileNr
								% tiles.getNumCols()] = layerRaster.getRender()
								.getLastRenderBuffer();
					}
				} catch (NoninvertibleTransformException e) {
					logger.error("Non invertible matrix! ", e);
				} catch (DriverException de) {
					logger.error("While drawing tile: " + de.getMessage());
					this.setAvailable(false);
				}
			}

			try {
				if (datasets != null && datasets[0][0] != null) {
					dataset = new CompositeDataset(datasets);
					initializeRasterLayer(datasets, buf);
				}
			} catch (MosaicNotValidException e) {
				throw new ReadDriverException(
						"No hay continuidad en el mosaico.", e);
			} catch (LoadLayerException e) {
				throw new ReadDriverException("Error inicializando la capa.", e);
			} catch (InterruptedException e) {
				throw new ReadDriverException("Error interrupcion.", e);
			}

			// more than one tile ----------------------

		} else {

			// one tile --------------------------------
			try {
				ViewPort vp = tiles.getTileViewPort(viewPort, 0);
				drawTile(g, vp, cancel, scale);

				dataset = layerRaster.getDataSource();
				getRender().setLastRenderBuffer(
						layerRaster.getRender().getLastRenderBuffer());
				initializeRasterLayer(null, new IBuffer[][] { { layerRaster
						.getRender().getLastRenderBuffer() } });

			} catch (NoninvertibleTransformException e) {
				logger.error("Non invertible matrix! ");
			} catch (DriverException de) {
				logger.error("While drawing tile: " + de.getMessage());
				this.setAvailable(false);
			} catch (LoadLayerException e) {
				logger.error("While drawing: " + e.getMessage());
				this.setAvailable(false);
			} catch (InterruptedException e) {
				logger.error("While drawing: " + e.getMessage());
			}

			// one tile --------------------------------

		}

		// the status needs to be reset because the drawTile
		// method had changed its extent and dimension:
		arcimsStatus.setExtent(viewPort.getAdjustedExtent());
		arcimsStatus.setHeight(viewPort.getImageHeight());
		arcimsStatus.setWidth(viewPort.getImageWidth());

		callActionListeners(scaleChangedEvent);

		if (nameQueryChange) {
			callNameOrQueryListeners();
			nameQueryChange = false;
		}

		disableStopped();
	}

	private void setTileDimensions(ViewPort vp) {
		maxTileHeight = vp.getImageHeight();

		try {
			maxTileWidth = Integer.parseInt(arcimsStatus.getServiceInfo()
					.getImagelimit_pixelcount()) / maxTileHeight;
			maxTileWidth--;
		} catch (NumberFormatException nfe) {
			logger.error(
					"getImagelimit_pixelcount() returned a non parseable string. maxTileWidth was set to 100.",
					nfe);
			maxTileWidth = 100;
		}

		logger.info("Tile size = [ " + maxTileWidth + " , " + maxTileHeight
				+ " ]");
	}

	/**
	 * Gets the georeference file extension depending on the image's format.
	 * 
	 * @param f
	 *            raster file format
	 * @return georeference file extension (".wld", ".tfw", etc)
	 */
	private String getExtensionWordFile(String f) {
		String extWorldFile = ".wld";

		if (f.equals("tif") || f.equals("tiff")) {
			extWorldFile = ".tfw";
		}

		if (f.equals("jpeg")) {
			extWorldFile = ".jpgw";
		}

		return extWorldFile;
	}

	/**
	 * Loads and draws the raster using the library
	 * 
	 * @param filePath
	 *            Ruta al fichero en disco
	 * @param g
	 *            Graphics2D
	 * @param vp
	 *            ViewPort
	 * @param scale
	 *            Escala para el draw
	 * @param cancel
	 *            Cancelación para el draw
	 * @throws ReadDriverException
	 * @throws LoadLayerException
	 */
	private void rasterProcess(File file, Graphics2D g, ViewPort vp,
			double scale, Cancellable cancel) throws ReadDriverException,
			LoadLayerException, FilterTypeException {

		// Cerramos el dataset asociado a la capa si está abierto.
		if (layerRaster != null) {
			layerRaster.setRemoveRasterFlag(true);
			layerRaster.getDataSource().close();
		}

		// Cargamos el dataset con el raster de disco.
		layerRaster = FLyrRasterSE.createLayer("", file.getAbsolutePath(),
				vp.getCrs());
		layerRaster.getRender()
				.setBufferFactory(layerRaster.getBufferFactory());

		// Obtenemos la tabla de color del raster abierto ya que se le va a
		// sustituir la lista
		// de filtros y el de tabla de color no queremos sustituirlo.
		RasterFilterList rasterFilterList = layerRaster.getRender()
				.getFilterList();
		ColorTableFilter ct = (ColorTableFilter) rasterFilterList
				.getFilterByBaseClass(ColorTableFilter.class);
		Object param = null;
		if (ct != null)
			param = ct.getParam("colorTable");

		// En caso de cargar un proyecto con XMLEntity se crean los filtros
		if (filterArguments != null) {
			RasterFilterList fl = new RasterFilterList();
			fl.addEnvParam("IStatistics", layerRaster.getDataSource()
					.getStatistics());
			fl.addEnvParam("MultiRasterDataset", layerRaster.getDataSource());
			fl.setInitDataType(layerRaster.getDataType()[0]);
			RasterFilterListManager filterListManager = new RasterFilterListManager(
					fl);
			filterListManager.createFilterListFromStrings(filterArguments);
			filterArguments = null;
			filterList = fl;
		}

		// Como el raster se carga a cada zoom el render se crea nuevamente y la
		// lista de
		// filtros siempre estará vacia a cada visualización. Para evitarlo
		// tenemos que
		// guardar la lista de filtro aplicada en la visualización anterior.
		if (this.filterList != null) {
			// Si tenía tabla de color le asignamos la original
			if (param != null && param instanceof GridPalette) {
				this.filterList.remove(ColorTableFilter.class);
				RasterFilterListManager filterManager = new RasterFilterListManager(
						filterList);
				ColorTableListManager ctm = new ColorTableListManager(
						filterManager);
				ctm.addColorTableFilter((GridPalette) param);
				filterList.move(ColorTableFilter.class, 0);
				filterList.controlTypes();
			}
			layerRaster.getRender().setFilterList(filterList);
		}
		if (gridTransparency != null)
			layerRaster.getRender().setLastTransparency(gridTransparency);
		if (this.renderBands != null)
			layerRaster.getRender().setRenderBands(renderBands);

		// Dibujamos
		layerRaster.draw(null, g, vp, cancel, scale);

		// La primera vez asignamos la lista de filtros asociada al
		// renderizador. Guardamos una referencia
		// en esta clase para que a cada zoom no se pierda.
		if (this.filterList == null)
			filterList = layerRaster.getRender().getFilterList();
		if (this.gridTransparency == null)
			gridTransparency = layerRaster.getRender().getLastTransparency();
		if (this.renderBands == null)
			renderBands = layerRaster.getRender().getRenderBands();

	}

	/*
	 * public void repaintRaster(Graphics2D g, ViewPort vp, File file) {
	 * rasterProcess(file, g, vp, scale, cancel); rasterProcess(g, vpData,
	 * file); }
	 */

	public String getFormat() {
		return format;
	}

	public void setFormat(String f) {
		format = f;
	}

	/**
	 * Gets a series of attributes that describe the layer's raster: Filename,
	 * Filesize, Height, Width and Bands.
	 * 
	 * @return A list of 2-element arrays { "Attribute name" , Value }
	 */
	public ArrayList getAttributes() {

		// if (rasterFile != null) {
		// ArrayList attr = new ArrayList();
		// Object[][] a = {
		// {
		// "Filename",
		// rasterFile.getName()
		// .substring(rasterFile.getName()
		// .lastIndexOf("/") + 1,
		// rasterFile.getName().length())
		// },
		// { "Filesize", new Long(rasterFile.getFileSize()) },
		// {
		// "Width",
		// new Integer((new Double(this.getWidth())).intValue())
		// },
		// {
		// "Height",
		// new Integer((new Double(this.getHeight())).intValue())
		// },
		// { "Bands", new Integer(rasterFile.getBandCount()) }
		// };
		//
		// for (int i = 0; i < a.length; i++) {
		// attr.add(a[i]);
		// }
		//
		// return attr;
		// }

		return null;
	}

	/**
	 * 
	 /** Invoqued from gvSIG to draw this layer on a printable map. Uses a
	 * <tt>Tiling</tt> object to prevent downloading images too large to be
	 * retrieved (pixel count limit).
	 * 
	 * @param g
	 *            graphic object on which drawing is make
	 * @param viewPort
	 *            the view's properties
	 * @param cancel
	 *            allows drawing abortion
	 * @param scale
	 *            the current scale
	 * 
	 * @throws DriverException
	 */
	public void print(Graphics2D g, ViewPort viewPort, Cancellable cancel,
			double scale, PrintRequestAttributeSet properties)
			throws ReadDriverException {
		draw(null, g, viewPort, cancel, scale);
	}

	public HashMap getProperties() {
		HashMap info = new HashMap();
		String[] layerNames = getLayerQuery().split(",");
		Vector layers = new Vector(layerNames.length);

		try {
			if (((FMapRasterArcImsDriver) getDriver()).connect(myCanc)) {
				info.put("layerName", this.getName());
				info.put("serverUrl", this.getHost());
				info.put("serviceName", this.getService());
				info.put("serviceType", this.getServiceType());
				info.put("format", this.format);

				for (int i = 0; i < layerNames.length; i++)
					layers.add(layerNames[i]);

				info.put("layerIds", layers);

				return info;
			}
		} catch (Exception e) {
			logger.error("Error while reading FRasterLyrArcIMS properties. ", e);
		}

		return null;
	}

	public double getHeight() {
		return visualStatus.height;
	}

	public double getMaxX() {
		return visualStatus.maxX;
	}

	public double getMaxY() {
		return visualStatus.maxY;
	}

	public double getMinX() {
		return visualStatus.minX;
	}

	public double getMinY() {
		return visualStatus.minY;
	}

	public int[] getPixel(double wcx, double wcy) {
		return null;
	}

	public double getWidth() {
		return visualStatus.width;
	}

	public void setPos(int x, int y) {
		this.posX = x;
		this.posY = y;
	}

	public void setPosWC(double x, double y) {
		this.posXWC = x;
		this.posYWC = y;
	}

	/**
	 * 
	 * @return XMLEntity.
	 * @throws XMLException
	 */
	public XMLEntity getXMLEntity() throws XMLException {
		XMLEntity xml = super.getXMLEntity();

		xml.putProperty("fullExtent", StringUtilities.rect2String(fullExtent));
		xml.putProperty("host", host.toExternalForm());
		xml.putProperty("serviceName", service);
		xml.putProperty("serviceType", serviceType);
		xml.putProperty("layerQuery", layerQuery);
		xml.putProperty("format", format);

		if (status != null) {
			status.getXMLEntity(xml, true, this);
		} else {
			status = new StatusLayerRaster();
			status.getXMLEntity(xml, true, this);
		}

		xml.putProperty("arcims_transparency", arcImsTransparency);

		int cnt = 0;
		boolean ex = true;
		String kn = "raster.file";
		while (ex) {
			kn = "raster.file" + cnt;
			try {
				xml.getStringArrayProperty(kn);
				xml.remove(kn);
			} catch (Exception exc) {
				ex = false;
			}
			cnt++;
		}

		return xml;
	}

	/**
	 * 
	 * @param xml
	 *            XMLEntity
	 * 
	 * @throws XMLException
	 * @throws DriverException
	 * @throws DriverIOException
	 */
	public void setXMLEntity(XMLEntity xml) throws XMLException {

		super.setXMLEntity(xml);

		fullExtent = StringUtilities.string2Rect(xml
				.getStringProperty("fullExtent"));

		try {
			host = new URL(xml.getStringProperty("host"));
		} catch (MalformedURLException e) {
			logger.error("Whle reading XML file ", e);
			throw new XMLException(e);
		}

		this.layerQuery = xml.getStringProperty("layerQuery");
		this.service = xml.getStringProperty("serviceName");
		this.serviceType = xml.getStringProperty("serviceType");
		this.format = xml.getStringProperty("format");

		FMapRasterArcImsDriver drv = new FMapRasterArcImsDriver(
				host.toString(), service, serviceType);

		if (!drv.connect(myCanc)) {
			logger.error("Error while loading service information. ");
		}

		ArcImsClientP cli = (ArcImsClientP) drv.getClient();
		ServiceInformation si = cli.getServiceInformation();
		this.arcimsStatus.setServiceInformation(si);

		if (xml.contains("arcims_transparency")) {
			this.arcImsTransparency = xml
					.getBooleanProperty("arcims_transparency");
		}

		String claseStr = StatusLayerRaster.defaultClass;

		if (xml.contains("raster.class")) {
			claseStr = xml.getStringProperty("raster.class");
		}

		if (status != null) {
			status.setXMLEntity(xml, this);
		} else {
			// When we load a project
			if ((claseStr != null) && !claseStr.equals("")) {
				try {
					Class clase = Class.forName(claseStr);
					Constructor constr = clase.getConstructor(null);
					status = (IStatusRaster) constr.newInstance(null);

					if (status != null) {
						status.setXMLEntity(xml, this);
						filterArguments = status.getFilterArguments();
						gridTransparency = status.getTransparency();
						renderBands = status.getRenderBands();

					}
				} catch (ClassNotFoundException exc) {
					logger.error("Whle reading XML file ", exc);
				} catch (InstantiationException exc) {
					logger.error("Whle reading XML file ", exc);
				} catch (IllegalAccessException exc) {
					logger.error("Whle reading XML file ", exc);
				} catch (NoSuchMethodException exc) {
					logger.error("Whle reading XML file ", exc);
				} catch (InvocationTargetException exc) {
					logger.error("Whle reading XML file ", exc);
				}
			}
		}

		firstLoad = true;
	}

	public Vector getLayerScaleInfoVector() {
		Vector v = new Vector();
		String id;

		for (int i = 0; i < arcimsStatus.getLayerIds().size(); i++) {
			id = (String) arcimsStatus.getLayerIds().get(i);
			v.add(getScaleInfo(id));
		}

		return v;
	}

	public LayerScaleData getScaleInfo(String theId) {
		ServiceInformation si;
		ServiceInformationLayer sil;
		int noflayers;
		long true_max_scale;
		long true_min_scale;

		LayerScaleData theLayerScaleData = null;

		si = arcimsStatus.getServiceInfo();

		int _dpi = si.getScreen_dpi();
		String mapUnits = si.getMapunits();

		noflayers = si.getLayers().size();

		for (int i = 0; i < noflayers; i++) {
			sil = (ServiceInformationLayer) si.getLayer(i);

			if ((sil.getId().compareTo(theId)) == 0) {
				true_max_scale = LayerScaleData
						.getTrueScaleFromRelativeScaleAndMapUnits(
								sil.getMaxscale(), mapUnits, _dpi);
				true_min_scale = LayerScaleData
						.getTrueScaleFromRelativeScaleAndMapUnits(
								sil.getMinscale(), mapUnits, _dpi);
				theLayerScaleData = new LayerScaleData(sil.getName(),
						sil.getId(), true_min_scale, true_max_scale,
						sil.getType());
			}
		}

		return theLayerScaleData;
	}

	public void addActionlistener(ActionListener al) {
		if (actionListeners.contains(al)) {
			return;
		}

		actionListeners.add(al);
	}

	public void removeActionlistener(ActionListener al) {
		actionListeners.remove(al);
	}

	private void callActionListeners(ActionEvent ae) {
		ActionListener al;

		for (int i = 0; i < actionListeners.size(); i++) {
			al = (ActionListener) actionListeners.get(i);
			al.actionPerformed(ae);
		}
	}

	public long getScale() {
		return getMapContext().getScaleView();
	}

	public int getMaxTileHeight() {
		return maxTileHeight;
	}

	public void setMaxTileHeight(int maxTileHeight) {
		this.maxTileHeight = maxTileHeight;
	}

	public int getMaxTileWidth() {
		return maxTileWidth;
	}

	public void setMaxTileWidth(int maxTileWidth) {
		this.maxTileWidth = maxTileWidth;
	}

	public void setServiceInformationInStatus(ServiceInformation si) {
		this.arcimsStatus.setServiceInformation(si);
	}

	/**
	 * This is the <tt>getDriver</tt> method. The <tt>getDriver</tt> method will
	 * be implemented by sub-classes.
	 * 
	 * @param d
	 *            the new driver.
	 */
	public void setDriver(ArcImsDriver d) {
		driver = d;
	}

	public ArcImsStatus getArcimsStatus() {
		return arcimsStatus;
	}

	public void setArcimsStatus(ArcImsStatus as) {
		arcimsStatus = as;
	}

	public boolean isFirstLoad() {
		return firstLoad;
	}

	public void setFirstLoad(boolean fl) {
		firstLoad = fl;
	}

	public URL getHost() {
		return host;
	}

	public void setHost(URL h) {
		host = h;
	}

	public String getLayerQuery() {
		return layerQuery;
	}

	public void setLayerQuery(String lQuery) {
		this.layerQuery = lQuery;

		if (layerQuery.substring(0, 1).compareTo(",") == 0) {
			layerQuery = layerQuery.substring(1);
		}
	}

	public String getService() {
		return service;
	}

	public void setService(String s) {
		service = s;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String st) {
		serviceType = st;
	}

	public String getSRS() {
		return SRS;
	}

	public void setSRS(String srs) {
		SRS = srs;
	}

	public void setFullExtent(Rectangle2D fe) {
		fullExtent = fe;
	}

	public Rectangle2D getFullExtent() {
		return fullExtent;
	}

	public boolean getArcImsTransparency() {
		return arcImsTransparency;
	}

	public int getTransparency() {
		return transparency;
	}

	public void setTransparency(int transparency) {
		this.transparency = transparency;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public double getPosXWC() {
		return posXWC;
	}

	public void setPosXWC(double posXWC) {
		this.posXWC = posXWC;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public double getPosYWC() {
		return posYWC;
	}

	public void setPosYWC(double posYWC) {
		this.posYWC = posYWC;
	}

	public void setFormatInStatus(String imgFormat) {
		arcimsStatus.setFormat(imgFormat);
	}

	/**
	 * This method invoques the <tt>FMapRasterArcImsDriver</tt>'s
	 * <tt>getFeatureInfo</tt> method with the given coordinates as a parameter
	 * 
	 * @param p
	 *            the corrdinates of the screen pixel to be queried
	 * 
	 * @return the data retrieved for that pixel
	 * @throws DriverException
	 */
	public XMLItem[] getInfo(Point p, double tolerance, Cancellable cancel)
			throws ReadDriverException {
		StringXMLItem[] resp = new StringXMLItem[1];
		String respStr;

		if (((FMapRasterArcImsDriver) driver).isQueryable()) {
			try {
				respStr = ((FMapRasterArcImsDriver) driver).getFeatureInfo(
						arcimsStatus, (int) p.getX(), (int) p.getY(),
						Integer.MAX_VALUE);
			} catch (ArcImsException e) {
				ReadDriverException de = new ReadDriverException(
						"While getting feature info. ", e);
				logger.error("In query by point ", e);
				throw de;
			}
		} else {
			ReadDriverException de = new ReadDriverException(
					"Layer not queriable.", new Exception(
							"Layer not queriable."));
			throw de;
		}

		System.err.println(resp); // TODO BORRAR ESTO
		resp[0] = new StringXMLItem(respStr, this);

		return resp;
	}

	public boolean isTiled() {
		return tiled;
	}

	public int[] getTileSize() {
		return tileSize;
	}

	private boolean isTrueString(String visible) {
		if (visible.compareToIgnoreCase("true") == 0) {
			return true;
		}

		return false;
	}

	public void callNameOrQueryListeners() {
		for (int i = 0; i < listeners.size(); i++) {
			((FRasterLyrArcIMSListener) listeners.get(i)).thingsHaveChanged(
					getLayerQuery(), getName());
		}
	}

	public void addNameOrQueryListener(FRasterLyrArcIMSListener l) {
		if (!listeners.contains(l)) {
			listeners.add(l);
		}
	}

	public void removeNameOrQueryListener(FRasterLyrArcIMSListener l) {
		listeners.remove(l);
	}

	public void setNameQueryChange(boolean c) {
		nameQueryChange = c;
	}

	public ImageIcon getTocImageIcon() {
		ImageIcon resp = null;

		try {
			resp = createImageIcon("images/esrilogo.png");
		} catch (Exception ex) {
		}

		if (resp == null) {
			return super.getTocImageIcon();
		} else {
			return resp;
		}
	}

	protected ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = createExtensionUrl(path);

		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			logger.error("File not found: " + path);

			return null;
		}
	}

	protected java.net.URL createExtensionUrl(String path) {
		return PluginServices
				.getPluginServices("es.prodevelop.cit.gvsig.arcims")
				.getClassLoader().getResource(path);

		// return getClass().getClassLoader().getResource(path);
	}

	public Grid getGrid() {
		return grid;
	}

	private class VisualStatusArcIms {
		/**
		 * Width and height of the image or the group of tiles if it have them.
		 * It's the same dimensions of the viewport
		 */
		private int width = 0;

		/**
		 * Width and height of the image or the group of tiles if it have them.
		 * It's the same dimensions of the viewport
		 */
		private int height = 0;
		private double minX = 0D;
		private double minY = 0D;
		private double maxX = 0D;
		private double maxY = 0D;
		private int bandCount = 0;
		private int dataType = DataBuffer.TYPE_UNDEFINED;
	}

	public RasterFilterList getRenderFilterList() {
		return (filterList != null) ? filterList : getRender().getFilterList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gvsig.raster.hierarchy.IRasterRendering#setRenderFilterList(org.gvsig
	 * .raster.grid.filter.RasterFilterList)
	 */
	public void setRenderFilterList(RasterFilterList filterList) {
		this.filterList = filterList;
		super.getRender().setFilterList(filterList);
	}

	public void setRenderBands(int[] renderBands) {
		this.renderBands = renderBands;
		getRender().setRenderBands(renderBands);
	}

	/**
	 * Actions that are taken after setting the data sources for the raster
	 * layer
	 * 
	 * @throws LoadLayerException
	 * @throws InterruptedException
	 */
	private void initializeRasterLayer(MultiRasterDataset[][] datasets,
			IBuffer[][] buf) throws LoadLayerException, InterruptedException {
		if (this.filterList != null)
			getRender().setFilterList(filterList);
		if (this.gridTransparency != null)
			getRender().setLastTransparency(gridTransparency);
		if (this.renderBands != null)
			getRender().setRenderBands(renderBands);
		if (datasets != null) {
			String[][] names = new String[datasets.length][datasets[0].length];
			for (int i = 0; i < datasets.length; i++)
				for (int j = 0; j < datasets[i].length; j++)
					names[i][j] = datasets[i][j].getDataset(0)[0].getFName();
			super.setLoadParams(names);
		}
		super.init();
		if (buf != null) {
			int drawablesBandCount = layerRaster.getDataSource().getBands()
					.getDrawableBandsCount();
			IBuffer buff = null;
			if (dataset instanceof CompositeDataset)
				buff = ((CompositeDataset) dataset).generateBuffer(buf,
						drawablesBandCount);
			else
				buff = buf[0][0];
			getRender().setLastRenderBuffer(buff);
		}
	}

	public GridTransparency getRenderTransparency() {
		return (gridTransparency != null) ? gridTransparency : getRender()
				.getLastTransparency();
	}

	public boolean isActionEnabled(int action) {
		switch (action) {
		case IRasterLayerActions.ZOOM_PIXEL_RESOLUTION:
		case IRasterLayerActions.FLYRASTER_BAR_TOOLS:
		case IRasterLayerActions.BANDS_FILE_LIST:
		case IRasterLayerActions.COLOR_TABLE:
		case IRasterLayerActions.GEOLOCATION:
		case IRasterLayerActions.PANSHARPENING:
			return false;
		case IRasterLayerActions.REMOTE_ACTIONS:
			return true;
		}

		return super.isActionEnabled(action);
	}

	public int[] getRenderBands() {
		return (renderBands != null) ? renderBands : getRender()
				.getRenderBands();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.fmap.raster.layers.FLyrRasterSE#cloneLayer()
	 */
	public FLayer cloneLayer() throws Exception {
		FRasterLyrArcIMS layer = new FRasterLyrArcIMS();
		layer.setHost(this.getHost());
		layer.setName(this.getName());
		layer.setSRS(this.getSRS());
		layer.setFormat(this.getFormat());
		layer.setFullExtent(this.fullExtent);
		layer.setDriver(this.getDriver());
		layer.setLayerQuery(this.layerQuery);
		layer.setArcimsStatus(this.getArcimsStatus());
		layer.visualStatus = this.visualStatus;
		layer.setCrs(this.getCrs());

		ArrayList filters = getRender().getFilterList().getStatusCloned();
		if (layer.getRender().getFilterList() == null)
			layer.getRender().setFilterList(new RasterFilterList());
		layer.getRender().getFilterList().setStatus(filters);

		return layer;
	}

}
