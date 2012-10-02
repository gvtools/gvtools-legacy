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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.ImageIcon;

import org.cresques.cts.ProjectionUtils;
import org.exolab.castor.xml.ValidationException;
import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.fmap.raster.layers.IRasterLayerActions;
import org.gvsig.fmap.raster.layers.IStatusRaster;
import org.gvsig.fmap.raster.layers.StatusLayerRaster;
import org.gvsig.raster.dataset.CompositeDataset;
import org.gvsig.raster.dataset.IBuffer;
import org.gvsig.raster.dataset.MosaicNotValidException;
import org.gvsig.raster.dataset.MultiRasterDataset;
import org.gvsig.raster.dataset.NotSupportedExtensionException;
import org.gvsig.raster.dataset.io.RasterDriverException;
import org.gvsig.raster.datastruct.ColorTable;
import org.gvsig.raster.datastruct.Extent;
import org.gvsig.raster.datastruct.ViewPortData;
import org.gvsig.raster.grid.GridTransparency;
import org.gvsig.raster.grid.filter.FilterTypeException;
import org.gvsig.raster.grid.filter.RasterFilterList;
import org.gvsig.raster.grid.filter.RasterFilterListManager;
import org.gvsig.remoteClient.wcs.WCSStatus;
import org.gvsig.remoteClient.wms.ICancellable;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.iver.cit.gvsig.exceptions.layers.ConnectionErrorLayerException;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.exceptions.layers.UnsupportedVersionLayerException;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.wcs.FMapWCSDriver;
import com.iver.cit.gvsig.fmap.drivers.wcs.FMapWCSDriverFactory;
import com.iver.cit.gvsig.fmap.drivers.wcs.WCSDriverException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.XMLItem;
import com.iver.utiles.StringUtilities;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;


/**
 * Class for the WCS layer.
 *
 * Capa para el WCS.
 *
 * Las capas WCS son tileadas para descargarlas del servidor. Esto quiere decir que
 * están formadas por multiples ficheros raster. Por esto la fuente de datos raster (IRasterDatasource)
 * de la capa FLyrWCS es un objeto de tipo CompositeDataset. Este objeto está compuesto por un array
 * bidimensional de MultiRasterDataset. Cada uno de los MultiRasterDataset corresponde con un tile
 * salvado en disco. Estos MultiRasterDataset se crean cada vez que se repinta ya que en WCS a cada
 * zoom varian los ficheros fuente. La secuencia de creación de un CompositeDataset sería la siguiente:
 * <UL>
 * <LI>Se hace una petición de dibujado por parte del usuario llamando al método draw de FLyrWCS</LI>
 * <LI>Se tilea la petición</LI>
 * <LI>Cada tile se dibuja abriendo una FLyrRaster para ese tile</LI>
 * <LI>Si es el primer dibujado se guarda una referencia en la capa WMS a las propiedades de renderizado, orden de bandas,
 * transparencia, filtros aplicados, ...</LI>
 * <LI>Si no es el primer dibujado se asignan las propiedades de renderizado cuya referencia se guarda en la capa WMS</LI>
 * <LI>Se guarda el MultiRasterDataset de cada tile</LI>
 * <LI>Al acabar todos los tiles creamos un CompositeDataset con los MultiRasterDataset de todos los tiles</LI>
 * <LI>Asignamos a la capa la referencia de las propiedades de renderizado que tenemos almacenadas. De esta forma si hay
 * alguna modificación desde el cuadro de propiedades será efectiva sobre los tiles que se dibujan.</LI>
 * </UL>
 *
 * @author jaume - jaume.dominguez@iver.es
 */
public class FLyrWCS extends FLyrRasterSE {
	private FMapWCSDriver wcs = null;

	private URL 						host;
	private String						coverageName;
	private Rectangle2D					fullExtent;
	private String						format;
	private String						srs;
	private String						time;
	private String						parameter;
	private Point2D						maxRes;
	private Hashtable 					onlineResources = new Hashtable();

	private WCSStatus					wcsStatus = new WCSStatus();

	private int 						posX = 0, posY = 0;
	private double 						posXWC = 0, posYWC = 0;
	private int 						r = 0, g = 0, b = 0;
	private boolean 					firstLoad = false;
	private VisualStatus				visualStatus = new VisualStatus();

	private boolean 					mustTileDraw = false;
	private int 						maxTileDrawWidth  = 1023;
	private int							maxTileDrawHeight = 1023;
	//private int 						maxTilePrintWidth  = 250;
	//private int							maxTilePrintHeight = 250;
	/**
	 * Lista de filtros aplicada en la renderización
	 */
	private RasterFilterList            filterList = null;
	private GridTransparency			transparency = null;
	private int[]                       renderBands = null;
	private FLyrRasterSE[]				layerRaster = null;
	private ArrayList                   filterArguments = null;
	private int                         lastNColumns = 0;
	private int                         lastNRows = 0;
	
	private class MyCancellable implements ICancellable
	{

		private Cancellable original;
		public MyCancellable(Cancellable cancelOriginal)
		{
			this.original = cancelOriginal;
		}
		public boolean isCanceled() {
			return original.isCanceled();
		}
		public Object getID() {
			return this;
		}

	}

	public FLyrWCS(){
		super();
		this.updateDrawVersion();
	}

	public FLyrWCS(Map args) throws DriverIOException{
		FMapWCSDriver drv = null;
		String host = (String)args.get("HOST");
		String sCoverage = (String) args.get((String) "COVERAGE");

		try {
			this.setHost(new URL(host));
		} catch (MalformedURLException e) {
			//e.printStackTrace();
			throw new DriverIOException("Malformed host URL, '" + host + "' (" + e.toString() + ").");
		}
		try {
			drv = this.getDriver();
		} catch (Exception e) {
			// e.printStackTrace();
			throw new DriverIOException("Can't get driver to host '" + host + "' (" + e.toString() + ").");
		}

		try{
			if (!drv.connect(false, null)){
				throw new DriverIOException("Can't connect to host '" + host + "'.");
			}
		}catch(Exception e){
			throw new DriverIOException("Can't connect to host '" + host + "'.");
		}

		WCSLayer wcsNode = drv.getLayer(sCoverage);

		if (wcsNode == null){
			throw new DriverIOException("The server '" + host + "' doesn't has the coverage '" + sCoverage + "'.");
		}

		try{
			this.setFullExtent(drv.getFullExtent(sCoverage,
					(String) args.get((String) "CRS")));
			this.setFormat((String) args.get((String) "FORMAT"));
			this.setParameter("BANDS=" + (String) args.get((String) "BANDS"));
			this.setSRS((String) args.get((String) "CRS"));
			this.setName(sCoverage);
			this.setCoverageName(sCoverage);
			load();
		}catch (Exception e){
			throw new DriverIOException("The server '" + host + "' is not able to load the coverage '" + sCoverage + "'.");
		}

	}

	/**
	 * Clase que contiene los datos de visualización de WCS. Tiene datos que representan al
	 * raster en la vista. Este raster puede estar compuesto por tiles por lo que valores
	 * como el ancho total o el mínimo o máximo deben ser calculados a partir de todos los
	 * tiles visualizados.
	 * @author Nacho Brodin (brodin_ign@gva.es)
	 */
	private class VisualStatus {
		/**
		 * Ancho y alto de la imagen o del conjunto de tiles si los tiene. Coincide con
		 * el ancho y alto del viewPort
		 */
		private	int							width = 0, height = 0;
		private double						minX = 0D, minY = 0D, maxX = 0D, maxY = 0D;
		private int 						bandCount = 0;
		private int							dataType = DataBuffer.TYPE_UNDEFINED;

		/**
		 * Ancho y alto total del raster que será la suma de todos los tiles.
		 */
		private	int							rasterWidth = 0, rasterHeight = 0;
		private	double						rasterMinX = Double.MAX_VALUE, rasterMinY = Double.MAX_VALUE;
		private	double						rasterMaxX = 0, rasterMaxY = 0;
		/**
		 * Lista de nombre de fichero que componen toda la visualización.
		 */
		private String[]					fileNames = null;
	}

	/**
	 * @deprecated
	 * @see com.iver.cit.gvsig.fmap.layers.layerOperations.InfoByPoint#getInfo
	 */
	public String queryByPoint(Point p) {
		String data = "<file:"+getName().replaceAll("[^a-zA-Z0-9]","")+">\n";
		ArrayList attr = this.getAttributes();
		data += "  <raster\n";
		data += "    File=\""+getName()+"\"\n";
		for (int i=0; i<attr.size(); i++) {
			Object [] a = (Object []) attr.get(i);

			data += "    "+a[0].toString()+"=";
			if (a[1].toString() instanceof String)
				data += "\""+a[1].toString()+"\"\n";
			else
				data += a[1].toString()+"\n";
		}
		data += "    Point=\""+posX+" , "+posY+"\"\n";
		data += "    Point_WC=\""+posXWC+" , "+posYWC+"\"\n";
		data += "    RGB=\""+r+", "+g+", "+b+"\"\n";
		data += "  />\n";

		data += "</file:"+getName().replaceAll("[^a-zA-Z0-9]","")+">\n";
		System.out.println(data);
		return data;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.layerOperations.InfoByPoint#getInfo
	 */
	public XMLItem[] getInfo(Point point, double tolerance, Cancellable cancel ) throws ReadDriverException {
		return super.getInfo(point, tolerance, cancel);
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.FLayer#getFullExtent()
	 */
	public Rectangle2D getFullExtent() {
		return fullExtent;
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.FLayer#draw(java.awt.image.BufferedImage, java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.cit.gvsig.fmap.operations.Cancellable, double)
	 */
	public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort, Cancellable cancel, double scale) throws ReadDriverException {
 		enableStopped();
		// callLegendChanged(null);
 		lastNColumns = lastNRows = 0;
 		
 		closeAndFree();
		
		if (isWithinScale(scale)) {
			Point2D p = viewPort.getOffset();
			// p will be (0, 0) when drawing a view or other when painting onto
			// the Layout.
			visualStatus.width = viewPort.getImageWidth();
			visualStatus.height = viewPort.getImageHeight();
			visualStatus.minX = viewPort.getAdjustedExtent().getMinX();
			visualStatus.minY = viewPort.getAdjustedExtent().getMinY();
			visualStatus.maxX = viewPort.getAdjustedExtent().getMaxX();
			visualStatus.maxY = viewPort.getAdjustedExtent().getMaxY();
			visualStatus.rasterWidth = 0;
			visualStatus.rasterHeight = 0;
			visualStatus.rasterMinX = Double.MAX_VALUE;
			visualStatus.rasterMinY = Double.MAX_VALUE;
			visualStatus.rasterMaxX = 0;
			visualStatus.rasterMaxY = 0;
			visualStatus.fileNames = new String[1];

			try {
				if (true) {
					if (viewPort.getImageWidth() <= maxTileDrawWidth && viewPort.getImageHeight() <= maxTileDrawHeight) {
						layerRaster = new FLyrRasterSE[1];
						drawTile(g, viewPort, cancel, 0, scale, 0);
						if(layerRaster != null && layerRaster[0] != null) {
							dataset = layerRaster[0].getDataSource();
							getRender().setLastRenderBuffer(layerRaster[0].getRender().getLastRenderBuffer());
							initializeRasterLayer(null, new IBuffer[][] { { layerRaster[0].getRender().getLastRenderBuffer() } });
						}
					} else {
						Rectangle r = new Rectangle((int) p.getX(), (int) p.getY(), viewPort.getImageWidth(), viewPort.getImageHeight());
						Tiling tiles = new Tiling(maxTileDrawWidth, maxTileDrawHeight, r);
						tiles.setAffineTransform((AffineTransform) viewPort.getAffineTransform().clone());
						MultiRasterDataset[][] datasets = new MultiRasterDataset[tiles.getNumRows()][tiles.getNumCols()];
						IBuffer[][] buf = new IBuffer[tiles.getNumRows()][tiles.getNumCols()];
						visualStatus.fileNames = new String[tiles.getNumTiles()];
						layerRaster = new FLyrRasterSE[tiles.getNumTiles()];
						lastNColumns = tiles.getNumCols();
						lastNRows = tiles.getNumRows();
						for (int tileNr = 0; tileNr < tiles.getNumTiles(); tileNr++) {
							// drawing part
							try {
								ViewPort vp = tiles.getTileViewPort(viewPort, tileNr);
								boolean painted = drawTile(g, vp, cancel, tileNr, scale, tileNr);
								if(	layerRaster != null && 
									layerRaster[tileNr] != null && 
									painted) {
									datasets[(int) (tileNr / tiles.getNumCols())][tileNr % tiles.getNumCols()] = (MultiRasterDataset) layerRaster[tileNr].getDataSource().newDataset();
									buf[(int) (tileNr / tiles.getNumCols())][tileNr % tiles.getNumCols()] = layerRaster[tileNr].getRender().getLastRenderBuffer();
								}
							} catch (NoninvertibleTransformException e) {
								e.printStackTrace();
							}
						}
						try {
							if (datasets != null && datasets[0][0] != null) {
								dataset = new CompositeDataset(datasets);
								initializeRasterLayer(datasets, buf);
								buf = null;
							}
						} catch (MosaicNotValidException e) {
							throw new ReadDriverException("No hay continuidad en el mosaico.", e);
						} catch (LoadLayerException e) {
							throw new ReadDriverException("Error inicializando la capa.", e);
						}
					}
				} else {
					layerRaster = new FLyrRasterSE[1];
					drawTile(g, viewPort, cancel, 0, scale, 0);
					if(layerRaster != null && layerRaster[0] != null) {
						dataset = layerRaster[0].getDataSource();
						getRender().setLastRenderBuffer(layerRaster[0].getRender().getLastRenderBuffer());
						initializeRasterLayer(null, new IBuffer[][] { { layerRaster[0].getRender().getLastRenderBuffer() } });
					}
				}
			} catch (ConnectionErrorLayerException e) {
				e.printStackTrace();
			} catch (UnsupportedVersionLayerException e) {
				e.printStackTrace();
			} catch (LoadLayerException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
			}
		}
				
		disableStopped();
		// callLegendChanged(null);
		
		/*Runtime r = Runtime.getRuntime();
		System.err.println("********************WCS**********************");
		System.err.println("Memoria Total: " + (r.totalMemory() / 1024) +"KB");
		System.err.println("Memoria Usada: " + ((r.totalMemory() - r.freeMemory()) / 1024) +"KB");
		System.err.println("Memoria Libre: " + (r.freeMemory() / 1024) +"KB");
		System.err.println("Memoria MaxMemory: " + (r.maxMemory() / 1024) +"KB");
		System.err.println("*********************************************");*/
	}
	
	/**
	 * Closes files and releases memory (pointers to null)
	 */
	private void closeAndFree() {
		while(readingData != null && readingData.compareTo(Thread.currentThread().toString()) != 0)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
			
		if(dataset != null) {
			dataset.close();
			dataset = null;
		}
		
		//Cerramos el dataset asociado a la capa si está abierto.
		if(layerRaster != null) {
			for (int i = 0; i < layerRaster.length; i++) {
				if(layerRaster[i] != null) {
					layerRaster[i].setRemoveRasterFlag(true);
					layerRaster[i].getDataSource().close();
					layerRaster[i].getRender().free();
					layerRaster[i].getBufferFactory().free();
					layerRaster[i] = null;
				}
			}
		}
		getRender().free();
		System.gc();
	}

	/**
	 * Acciones que se realizan después de asignar la fuente de datos a
	 * la capa raster.
	 *
	 * @throws LoadLayerException
	 * @throws InterruptedException
	 */
	private void initializeRasterLayer(MultiRasterDataset[][] datasets, IBuffer[][] buf) throws LoadLayerException, InterruptedException {
		if(this.filterList != null)
			getRender().setFilterList(filterList);

		if(this.renderBands != null)
			getRender().setRenderBands(renderBands);
		if(datasets != null) {
			String[][] names = new String[datasets.length][datasets[0].length];
			for (int i = 0; i < datasets.length; i++) {
				for (int j = 0; j < datasets[i].length; j++) {
					if(datasets[i][j] != null)
						names[i][j] = datasets[i][j].getDataset(0)[0].getFName();
				}
			}
			super.setLoadParams(names);
		}
		super.init();
		if(buf != null) {
			int drawablesBandCount = layerRaster[0].getDataSource().getBands().getDrawableBandsCount();
			IBuffer buff = null;
			if(dataset instanceof CompositeDataset)
				buff = ((CompositeDataset)dataset).generateBuffer(buf, drawablesBandCount);
			else
				buff = buf[0][0];
							
			if(getRender().getLastRenderBuffer() != null)
				getRender().getLastRenderBuffer().free();
			getRender().setLastRenderBuffer(buff);
		}

		if (transparency == null)
			transparency = new GridTransparency(getDataSource().getTransparencyFilesStatus());

		if(getRender().getLastTransparency() != null)
			getRender().getLastTransparency().free();
		getRender().setLastTransparency(transparency);
	}

	/**
	 * This is the method used to draw a tile in a WCS mosaic layer.
	 * @param tile Tile number to draw
	 * @throws ReadDriverException
	 * @return true when a tile has been painted
	 */
	private boolean drawTile(Graphics2D g, ViewPort vp, Cancellable cancel, int tile, double scale, int nLyr) throws LoadLayerException, ReadDriverException {
		// Compute the query geometry
		// 1. Check if it is within borders
		Rectangle2D extent = getFullExtent();
		if ((vp.getAdjustedExtent().getMinX() > extent.getMaxX()) ||
				(vp.getAdjustedExtent().getMinY() > extent.getMaxY()) ||
				(vp.getAdjustedExtent().getMaxX() < extent.getMinX()) ||
				(vp.getAdjustedExtent().getMaxY() < extent.getMinY()))
			return false;

		// 2. Compute extent to be requested.
		Rectangle2D bBox = new Rectangle2D.Double();
		Rectangle2D.intersect(vp.getAdjustedExtent(), extent, bBox);

		// 3. Compute size in pixels
		double scalex = vp.getAffineTransform().getScaleX();
		double scaley = vp.getAffineTransform().getScaleY();
		int wImg = (int) Math.ceil(Math.abs(bBox.getWidth() * scalex) + 1);
		int hImg = (int) Math.ceil(Math.abs(bBox.getHeight() * scaley) + 1);
		Dimension sz = new Dimension(wImg, hImg);

		if ((wImg <= 0) || (hImg <= 0))
			return false;

		try {
			sz = new Dimension(wImg, hImg);

			wcsStatus.setCoveraName( coverageName );
			wcsStatus.setExtent( bBox );
			wcsStatus.setFormat( format );
			wcsStatus.setHeight( hImg );
			wcsStatus.setWidth( wImg );
			wcsStatus.setSrs(srs);
			wcsStatus.setParameters( parameter );
			wcsStatus.setTime( time );
			wcsStatus.setOnlineResource((String) onlineResources.get("GetCoverage"));

			File f = getDriver().getCoverage(wcsStatus, new MyCancellable(cancel));
			if (f == null)
				return false;
			String nameWordFile = f.getPath() + getExtensionWorldFile();
			com.iver.andami.Utilities.createTemp(nameWordFile, this.getDataWorldFile(bBox, sz));

			IStatusRaster status = super.getStatus();
			if(status!=null && firstLoad){
				try {
					status.applyStatus(this);
				} catch (NotSupportedExtensionException e) {
					throw new ReadDriverException("", e);
				} catch (RasterDriverException e) {
					throw new ReadDriverException("", e);
				} catch (FilterTypeException e) {
					throw new ReadDriverException("", e);
				}
				firstLoad = false;
			}
			ViewPortData vpData = new ViewPortData(
				vp.getCrs(), new Extent(bBox), sz );
			vpData.setMat(vp.getAffineTransform());

			String filePath = f.getAbsolutePath();
			visualStatus.fileNames[tile] = filePath;

			try {
				rasterProcess(filePath, g, vp, scale, cancel, nLyr);
			} catch (FilterTypeException e) {
			}

		} catch (IOException e) {
			throw new ConnectionErrorLayerException(getName(),e);
		}
		catch (WCSDriverException e) {
			throw new LoadLayerException(getName(),e);
		} catch (IllegalStateException e) {
			throw new LoadLayerException(getName(),e);
		}
		return true;
	}

	/**
	 * Devuelve el FMapWMSDriver.
	 *
	 * @return FMapWMSDriver
	 *
	 * @throws IllegalStateException
	 * @throws ValidationException
	 * @throws UnsupportedVersionLayerException
	 * @throws IOException
	 */
	private FMapWCSDriver getDriver() throws IllegalStateException, IOException {
		if (wcs == null) {
			wcs = FMapWCSDriverFactory.getFMapDriverForURL(host);
		}
		return wcs;
	}

	/**
	 * Calcula el contenido del fichero de georreferenciación de una imagen.
	 * @param bBox Tamaño y posición de la imagen (en coordenadas de usuario)
	 * @param sz Tamaño de la imagen en pixeles.
	 * @return el 'WorldFile', como String.
	 * @throws IOException
	 */
	public String getDataWorldFile(Rectangle2D bBox, Dimension sz) throws IOException {
		StringBuffer data = new StringBuffer();
		data.append((bBox.getMaxX() - bBox.getMinX())/(sz.getWidth() - 1)+"\n");
		data.append("0.0\n");
		data.append("0.0\n");
		data.append("-"+(bBox.getMaxY() - bBox.getMinY())/(sz.getHeight() - 1)+"\n");
		data.append(""+bBox.getMinX()+"\n");
		data.append(""+bBox.getMaxY()+"\n");
		return data.toString();
	}

	/**
	 * Carga y dibuja el raster usando la librería
	 * @param filePath Ruta al fichero en disco
	 * @param g Graphics2D
	 * @param vp ViewPort
	 * @param scale Escala para el draw
	 * @param cancel Cancelación para el draw
	 * @throws ReadDriverException
	 * @throws LoadLayerException
	 */
	private void rasterProcess(String filePath, Graphics2D g, ViewPort vp, double scale, Cancellable cancel, int nLyr) throws ReadDriverException, LoadLayerException, FilterTypeException {
		//Cargamos el dataset con el raster de disco.
		layerRaster[nLyr] = FLyrRasterSE.createLayer("", filePath, vp.getCrs());
		//layerRaster[nLyr].getRender().setBufferFactory(layerRaster[nLyr].getBufferFactory());
		
		if(visualStatus.dataType == IBuffer.TYPE_UNDEFINED && layerRaster[nLyr].getDataType() != null)
			visualStatus.dataType = layerRaster[nLyr].getDataType()[0];
		if(visualStatus.bandCount == 0 && layerRaster[nLyr].getBandCount() != 0)
			visualStatus.bandCount = layerRaster[nLyr].getBandCount();

		if (getLegend() == null)
			lastLegend = layerRaster[nLyr].getLegend();

		//En caso de cargar un proyecto con XMLEntity se crean los filtros
		if(filterArguments != null) {
			RasterFilterList fl = new RasterFilterList();
			fl.addEnvParam("IStatistics", layerRaster[nLyr].getDataSource().getStatistics());
			fl.addEnvParam("MultiRasterDataset", layerRaster[nLyr].getDataSource());
			fl.setInitDataType(layerRaster[nLyr].getDataType()[0]);
			RasterFilterListManager filterListManager = new RasterFilterListManager(fl);
			filterListManager.createFilterListFromStrings(filterArguments);
			StatusLayerRaster.enhancedCompV10(filterArguments, layerRaster[nLyr], filterListManager);
			filterArguments = null;
			filterList = fl;
		}

		//Como el raster se carga a cada zoom el render se crea nuevamente y la lista de
		//filtros siempre estará vacia a cada visualización. Para evitarlo tenemos que
		//guardar la lista de filtro aplicada en la visualización anterior.
		if (filterList != null)
			layerRaster[nLyr].getRender().setFilterList(filterList);
		if (transparency == null)
			transparency = layerRaster[nLyr].getRender().getLastTransparency();
		if (transparency != null)
			layerRaster[nLyr].getRender().setLastTransparency(transparency);
		if (renderBands != null)
			layerRaster[nLyr].getRender().setRenderBands(renderBands);

		//Dibujamos
		layerRaster[nLyr].draw(null, g, vp, cancel, scale);

		//La primera vez asignamos la lista de filtros asociada al renderizador. Guardamos una referencia
		//en esta clase para que a cada zoom no se pierda.
		if (filterList == null)
			filterList = layerRaster[nLyr].getRender().getFilterList();
		if (renderBands == null)
			renderBands = layerRaster[nLyr].getRender().getRenderBands();
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.FLyrDefault#cloneLayer()
	 */
	public FLayer cloneLayer() throws Exception {
		FLyrWCS layer = new FLyrWCS();
		layer.setHost(this.getHost().toString());
		layer.setCoverageName(this.coverageName);
		layer.setSRS(this.srs);
		layer.setFormat(this.format);
		layer.setFullExtent(this.fullExtent);
		layer.setDriver(this.wcs);
		layer.setTime(this.time);
		layer.setParameter(this.parameter);
		layer.setName(this.getName());
		layer.setMaxResolution(this.maxRes);

		ArrayList filters = getRender().getFilterList().getStatusCloned();
		if(layer.getRender().getFilterList() == null)
			layer.getRender().setFilterList(new RasterFilterList());
		layer.getRender().getFilterList().setStatus(filters);
		
		return layer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.raster.IRasterRendering#getRenderFilterList()
	 */
	public RasterFilterList getRenderFilterList(){
		return (filterList != null) ? filterList : getRender().getFilterList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.raster.hierarchy.IRasterRendering#setRenderFilterList(org.gvsig.raster.grid.filter.RasterFilterList)
	 */
	public void setRenderFilterList(RasterFilterList filterList) {
		this.filterList = filterList;
		super.getRender().setFilterList(filterList);
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.raster.IRasterRendering#getRenderTransparency()
	 */
	public GridTransparency getRenderTransparency() {
		return getRender().getLastTransparency();
//		return (transparency != null) ? transparency : getRender().getLastTransparency();
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.raster.hierarchy.IRasterRendering#getRenderBands()
	 */
	public int[] getRenderBands() {
		return (renderBands != null) ? renderBands : getRender().getRenderBands();
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.raster.hierarchy.IRasterRendering#setRenderBands(int[])
	 */
	public void setRenderBands(int[] renderBands) {
		this.renderBands = renderBands;
		getRender().setRenderBands(renderBands);
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.raster.layers.FLyrRasterSE#print(java.awt.Graphics2D, com.iver.cit.gvsig.fmap.ViewPort, com.iver.utiles.swing.threads.Cancellable, double, javax.print.attribute.PrintRequestAttributeSet)
	 */
	public void print(Graphics2D g, ViewPort viewPort, Cancellable cancel, double scale, PrintRequestAttributeSet properties) throws ReadDriverException {
		if (isVisible() && isWithinScale(scale)){
			draw(null, g, viewPort, cancel, scale);
		}
	}

	/**
	 * Returns the XMLEntity containing the necessary info for reproduce
	 * the layer.
	 *
	 * Devuelve el XMLEntity con la información necesaria para reproducir
	 * la capa.
	 *
	 * @return XMLEntity.
	 * @throws XMLException
	 */
	public XMLEntity getXMLEntity() throws XMLException {
		XMLEntity xml = super.getXMLEntity();

		xml.putProperty("wcs.host", getHost());
		xml.putProperty("wcs.fullExtent", StringUtilities.rect2String( fullExtent ));
		xml.putProperty("wcs.layerQuery", coverageName );
		xml.putProperty("wcs.format", format );
		xml.putProperty("wcs.srs", srs );
		xml.putProperty("wcs.time", time );
		xml.putProperty("wcs.parameter", parameter );
		xml.putProperty("wcs.coverageName", coverageName );
		xml.putProperty("wcs.maxResX", maxRes.getX());
		xml.putProperty("wcs.maxResY", maxRes.getY());

		Iterator it = onlineResources.keySet().iterator();
		String strOnlines = "";
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = (String) onlineResources.get(key);
			strOnlines += key+"~##SEP2##~"+value;
			if (it.hasNext())
				strOnlines += "~##SEP1##~";
		}
		xml.putProperty("onlineResources", strOnlines);

		IStatusRaster status = super.getStatus();
		if (status!=null)
			status.getXMLEntity(xml, true, this);
		else{
			status = new StatusLayerRaster();
			status.getXMLEntity(xml, true, this);
		}
		return xml;
	}

	/**
	 * Reproduces the layer from an XMLEntity.
	 *
	 * A partir del XMLEntity reproduce la capa.
	 *
		* @param xml XMLEntity
	 *
	 * @throws XMLException
	 * @throws DriverException
	 * @throws DriverIOException
	 */
	public void setXMLEntity(XMLEntity xml) throws XMLException {
		for (int i = 0; i < xml.getPropertyCount(); i++) {
			String key = xml.getPropertyName(i);
			if(key.startsWith("raster.file")) {
				xml.putProperty(key, "");
			}
		}


		super.setXMLEntity(xml);

		// host
		try {
			host = new URL(xml.getStringProperty("wcs.host"));
		} catch (MalformedURLException e) {
			throw new XMLException(e);
		}

		// full extent
		fullExtent = StringUtilities.string2Rect(xml.getStringProperty("wcs.fullExtent"));

		// coverageQuery
		coverageName = xml.getStringProperty("wcs.layerQuery");

		// format
		format = xml.getStringProperty("wcs.format");

		// srs
		srs = xml.getStringProperty("wcs.srs");

		// time
		time = xml.getStringProperty("wcs.time");

		// parameter
		parameter = xml.getStringProperty("wcs.parameter");

		// coverage name
		coverageName = xml.getStringProperty("wcs.coverageName");

		// max resolution
		if (xml.contains("wcs.maxRes"))
			maxRes = new Point2D.Double(xml.getDoubleProperty("wcs.maxRes"), xml.getDoubleProperty("wcs.maxRes"));
		else if (xml.contains("wcs.maxResX") && xml.contains("wcs.maxResY"))
			maxRes = new Point2D.Double(xml.getDoubleProperty("wcs.maxResX"), xml.getDoubleProperty("wcs.maxResY"));

		// OnlineResources
				if (xml.contains("onlineResources")) {
					String[] operations = xml.getStringProperty("onlineResources").split("~##SEP1##~");
					for (int i = 0; i < operations.length; i++) {
				String[] resources = operations[i].split("~##SEP2##~");
				if (resources.length==2 && resources[1]!="")
					onlineResources.put(resources[0], resources[1]);
			}
				}
		String claseStr = null;
		if (xml.contains("raster.class")) {
			claseStr = xml.getStringProperty("raster.class");
		}

		//IStatusRaster status = super.getStatus();
		if (status != null)
			status.setXMLEntity(xml, this);
		else {
			//Cuando cargamos un proyecto

			if(claseStr!=null && !claseStr.equals("")){
				try{
					Class clase = LayerFactory.getLayerClassForLayerClassName(claseStr);
					Constructor constr = clase.getConstructor(null);
					status = (IStatusRaster)constr.newInstance(null);
					if(status != null) {
						((StatusLayerRaster)status).setNameClass(claseStr);
						status.setXMLEntity(xml, this);
						filterArguments = status.getFilterArguments();
						transparency = status.getTransparency();
						renderBands = status.getRenderBands();
						ColorTable ct = status.getColorTable();
						if(ct != null)
							setLastLegend(ct);
					}
				} catch(ClassNotFoundException exc) {
					exc.printStackTrace();
				} catch(InstantiationException exc) {
					exc.printStackTrace();
				} catch(IllegalAccessException exc) {
					exc.printStackTrace();
				} catch(NoSuchMethodException exc) {
					exc.printStackTrace();
				} catch(InvocationTargetException exc) {
					exc.printStackTrace();
				} catch (FilterTypeException exc) {
					exc.printStackTrace();
				}
			}
		}
		firstLoad = true;
	}

	public void setCoverageName(String coverageName) {
		this.coverageName = coverageName;
	}

	public void setParameter(String parametersString) {
		if (this.parameter == parametersString){
			return;
		}
		if (this.parameter != null && this.parameter.equals(parametersString)){
			return;
		}
		this.parameter = parametersString;
		this.updateDrawVersion();
	}

	public void setTime(String time) {
		if (this.time == time){
			return;
		}
		if (this.time != null && this.time.equals(time)){
			return;
		}
		this.time = time;
		this.updateDrawVersion();
	}

	public void setSRS(String srs) {
		if (this.srs == srs){
			return;
		}
		if (this.srs != null && this.srs.equals(srs)){
			return;
		}
		this.srs = srs;
		this.updateDrawVersion();
		setCrs(ProjectionUtils.getCRS(srs));
	}

	public void setFormat(String format) {
		if (this.format == format){
			return;
		}
		if (this.format != null && this.format.equals(format)){
			return;
		}
		this.format = format;
		this.updateDrawVersion();
	}


	/**
	 * Inserta el URL.
	 *
	 * @param host String.
	 * @throws MalformedURLException
	 */
	public void setHost(String host) {
		try {
			setHost(new URL(host));
		} catch (MalformedURLException e) {

		}
	}

	/**
	 * Inserta el URL.
	 *
	 * @param host URL.
	 */
	public void setHost(URL host) {
		if (this.host == host){
			return;
		}
		if (this.host != null && this.host.equals(host)){
			return;
		}
		this.host = host;
		this.updateDrawVersion();
	}

	/**
	 * Sets the layer's full extent.
	 *
	 * Establece la extensión máxima de la capa.
	 *
	 * @param rect
	 */
	public void setFullExtent(Rectangle2D rect) {
		if (this.fullExtent == rect){
			return;
		}
		if (this.fullExtent != null && this.fullExtent.equals(rect)){
			return;
		}

		this.fullExtent = rect;
		this.updateDrawVersion();
	}

	/**
	 * Devuelve el URL.
	 *
	 * @return URL.
	 */
	public URL getHost() {
		return host;
	}

	/**
	 * Remote source layers have a bunch of properties that are required for get them from
	 * the servers. This method supplies a hash table containing any needed field. This hash
	 * table may be used to let the client to connect to a server and restore a previously saved
	 * layer. So, the layer itself may not be saved to the disk since the actual saved
	 * info is just its properties.
	 *
	 * @return Returns a hash table containing all the required information for
	 * set up a wms layer
	 */
	public Hashtable getProperties(){
		Hashtable info = new Hashtable();
		info.put(   "name", coverageName);
		info.put(   "host", getHost());
		info.put(    "crs", srs);
		info.put( "format", format);
		String str = time;
		if (str==null)
			str = "";
		info.put(   "time", str);
		str = parameter;
		if (str==null)
			str = "";
		info.put("parameter", str);

		return info;
	}

	/**
	 * Obtiene la extensión del fichero de georreferenciación
	 * @return String con la extensiï¿½n del fichero de georreferenciaciï¿½n dependiendo
	 * del valor del formato obtenido del servidor. Por defecto asignaremos un .wld
	 */
	private String getExtensionWorldFile(){
		String extWorldFile = ".wld";
			if (format.equals("image/tif") || format.equals("image/tiff"))
				extWorldFile = ".tfw";
			//En la versión 1.6 de gdal no soporta jpgw si el fichero de imagen no 
			//tiene extensión. Solo lo lee si es wld
			/*if (format.equals("image/jpeg"))
				extWorldFile = ".jpgw";*/
			return extWorldFile;
	}

	public void setMaxResolution(Point2D maxResolution) {
		if (this.maxRes == maxResolution){
			return;
		}
		if (this.maxRes != null && this.maxRes.equals(maxResolution)){
			return;
		}
		this.maxRes = maxResolution;
		this.updateDrawVersion();
	}

	/**
	 * <p>
	 * Gets the max resolution allowed by the coverage. Requesting a higher resolution
	 * than this value does not cause any error, but the info responsed is just an
	 * interpolation. <br>
	 * </p>
	 *
	 * <p>
	 * In exchange for obtaining a greater file and without additional information,
	 * we can easily fit it into the View. <br>
	 * </p>
	 *
	 * <p>
	 * Obtiene la resolución máxima soportada por la cobertura. La petición
	 * de una resolución superior a la soportada no provoca ningún error, aunque
	 * la información obtenida sólo es una mera interpolación de información. <br>
	 * </p>
	 *
	 * <p>
	 * A cambio de obtener un archivo mayor y sin información adicional, podemos
	 * fácilmente acoplarlo a la vista. <br>
	 * </p>
	 *
	 * @return double
	 */
	public Point2D getMaxResolution() {
		if (maxRes==null)
			maxRes = wcs.getMaxResolution(coverageName);
		return maxRes;
	}


	public void setDriver(FMapWCSDriver driver) {
		if (driver == this.wcs){
			return;
		}
		this.wcs = driver;
		this.updateDrawVersion();
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.RasterOperations#getTileSize()
	 */
	public int[] getTileSize() {
		int[] size = {maxTileDrawWidth, maxTileDrawHeight};
		return size;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.FLyrDefault#getTocImageIcon()
	 */
	public ImageIcon getTocImageIcon() {
		return new ImageIcon(getClass().getResource("image/icoLayer.png"));
	}

	/*
	 *  (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.layers.RasterOperations#isTiled()
	 */
	public boolean isTiled() {
		return mustTileDraw;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.raster.layers.FLyrRasterSE#isActionEnabled(int)
	 */
	public boolean isActionEnabled(int action) {
		switch (action) {
			case IRasterLayerActions.ZOOM_PIXEL_RESOLUTION:
			case IRasterLayerActions.FLYRASTER_BAR_TOOLS:
			case IRasterLayerActions.BANDS_FILE_LIST:
			case IRasterLayerActions.GEOLOCATION:
			case IRasterLayerActions.PANSHARPENING:
			case IRasterLayerActions.SAVE_COLORINTERP:
				return false;
			case IRasterLayerActions.BANDS_RGB:
			case IRasterLayerActions.REMOTE_ACTIONS:
				return true;
		}

		return super.isActionEnabled(action);
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.raster.layers.FLyrRasterSE#overviewsSupport()
	 */
	public boolean overviewsSupport() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.fmap.raster.layers.FLyrRasterSE#getFileLayer()
	 */
	public FLayer getFileLayer() {
		if(layerRaster != null && layerRaster[0] != null) {
			FLyrRasterSE ly = null;
			if(lastNColumns == 0 && lastNRows == 0) {  //Una capa sin tilear => MultirasterDataset
				try {
					ly = createLayer(layerRaster[0].getName(), layerRaster[0].getLoadParams(), layerRaster[0].getCrs());
				} catch (LoadLayerException e) {
					return null;
				}
			} else {  //Capa tileada ==> CompositeDataset
				String[][] s = new String[lastNRows][lastNColumns];

				for (int i = 0; i < s.length; i++) {
					for (int j = 0; j < s[0].length; j++) {
						s[i][j] = ((FLyrRasterSE)layerRaster[i]).getDataSource().getNameDatasetStringList(i, j)[0];
					}
				}
				try {
					ly = createLayer("preview", s, getCrs());
				} catch (LoadLayerException e) {
					return null;
				}
			}
			return ly;
		}
		return null;
	}
}