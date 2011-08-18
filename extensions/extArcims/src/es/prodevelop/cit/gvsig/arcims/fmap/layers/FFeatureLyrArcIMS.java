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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.cresques.cts.ICoordTrans;
import org.cresques.cts.IProjection;
import org.gvsig.remoteClient.arcims.ArcImsClientP;
import org.gvsig.remoteClient.arcims.ArcImsFeatureClient;
import org.gvsig.remoteClient.arcims.ArcImsVectStatus;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.FieldInformation;
import org.gvsig.remoteClient.arcims.utils.MyCancellable;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerFeatures;
import org.gvsig.remoteClient.utils.Utilities;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.CartographicSupport;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.IMultiLayerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ISpatialDB;
import com.iver.cit.gvsig.fmap.layers.LegendChangedEvent;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.SpatialCache;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.InfoByPoint;
import com.iver.cit.gvsig.fmap.rendering.IClassifiedVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendListener;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;
import com.iver.cit.gvsig.fmap.rendering.ZSort;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.connections.ConnectionException;
import com.iver.utiles.swing.threads.Cancellable;
import com.iver.utiles.swing.threads.DefaultCancellableMonitorable;

import es.prodevelop.cit.gvsig.arcims.fmap.drivers.ArcImsInMemoryAttsTableDriver;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.ArcImsVectorialAdapter;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.ArcImsVectorialEditableAdapter;
import es.prodevelop.cit.gvsig.arcims.fmap.drivers.FMapFeatureArcImsDriver;


public class FFeatureLyrArcIMS extends FLyrVect implements InfoByPoint,
    LegendListener {
    // ClassifiableVectorial, Selectable, AlphanumericData, VectorialData,
    // Labelable, SingleLayer, RandomVectorialData {
    private static Logger logger = Logger.getLogger(FFeatureLyrArcIMS.class.getName());
    private URL host;
    private String service;
    private String serviceType;
    private Rectangle2D fullExtent;
    private boolean firstLoad = true;
    private ArcImsVectStatus arcimsStatus = new ArcImsVectStatus();
    private String SRS;
    private String srsAbrev;
    private String layerQuery;
    private VisualStatusArcIms visualStatus = new VisualStatusArcIms();
    private int transparency = -1;
    private boolean arcImsTransparency = true;
    private int shapeType = 0;

    //	private ArrayList initialColors = new ArrayList();
    private ArrayList geometries;
    private MyCancellable myCanc;

    // from ancestor FLyrVect:
    // private VectorialAdapter source;
    // private SelectableDataSource sds; --> is created like this --> new SelectableDataSource(source.getRecordSet()) 
    public FFeatureLyrArcIMS(ReadableVectorial vectAdapter) {
        myCanc = new MyCancellable(new DefaultCancellableMonitorable());

        setSource(vectAdapter);
        addLegendListener(this);
    }

    public FFeatureLyrArcIMS() {
    }

    public void setShapeType(int shpType) {
        shapeType = shpType;
    }

    public void setAdapter(VectorialEditableAdapter edapter) {
        setSource(edapter);

        try {
            shapeType = edapter.getShapeType();

            // ((VectorialLegend) getLegend()).setShapeType(shapeType);
        }
        catch (ReadDriverException e) {
            logger.error("Unexpected error while getting shape type ", e);
        }

        addLegendListener(this);
        myCanc = new MyCancellable(new DefaultCancellableMonitorable());

        //		loadInitialColors();
    }

    /**
     * Draws using IFeatureIterator. This method will replace the old draw(...) one.
     * @autor jaume dominguez faus - jaume.dominguez@iver.es
     * @param image
     * @param g
     * @param viewPort
     * @param cancel
     * @param scale
     * @throws ReadDriverException
     */
    private void _draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
    		Cancellable cancel, double scale) throws ReadDriverException {
    	
        if (!isVisible()) {
            return;
        }

        if (!isWithinScale(scale)) {
            return;
        }

        // In case gvSIG calls the getVisualStatus method... (?)
        visualStatus.width = viewPort.getImageWidth();
        visualStatus.height = viewPort.getImageHeight();
        visualStatus.minX = viewPort.getAdjustedExtent().getMinX();
        visualStatus.minY = viewPort.getAdjustedExtent().getMinY();
        visualStatus.maxX = viewPort.getAdjustedExtent().getMaxX();
        visualStatus.maxY = viewPort.getAdjustedExtent().getMaxY();

        IGeometry geom, clongeom;
        
        ICoordTrans layerTransf = getCoordTrans();
        Rectangle2D bBox = viewPort.getAdjustedExtent();
        
        arcimsStatus.setExtent(bBox);
        arcimsStatus.setHeight(viewPort.getImageHeight());
        arcimsStatus.setWidth(viewPort.getImageWidth());

        // one-item vector:
        arcimsStatus.setLayerIds(Utilities.createVector(layerQuery, ","));
        arcimsStatus.setServer(host.toString());
        arcimsStatus.setService(service);
        arcimsStatus.setSrs(this.getProjection().getAbrev());
        arcimsStatus.setTransparency(this.arcImsTransparency);

        FMapFeatureArcImsDriver drv =
        	(FMapFeatureArcImsDriver) getSource().getDriver();
        IVectorLegend lgnd = (IVectorLegend) getLegend();

        // get needed fields
		String[] _legflds = null;
		String[] legflds = null;
		String[] savedFieldNames = arcimsStatus.getSubfields();

		if (lgnd instanceof IClassifiedVectorLegend) {
			
			_legflds = ((IClassifiedVectorLegend) lgnd).getClassifyingFieldNames();
	       legflds = gvSigNamesToServerNames(_legflds);
			
			if (legflds!=null) {
				String[] appended = appendAtringArrays(savedFieldNames, legflds);
				arcimsStatus.setSubfields(appended);
			}
		}

        try {
            // ********************************************
            // first item in query must contain ID or #ALL#
            // ********************************************
            geometries = (ArrayList) drv.getMap(arcimsStatus);
        } catch (Exception e) {
        	// restore dubfields
        	arcimsStatus.setSubfields(savedFieldNames);
        	ReadDriverException de =
        		new ReadDriverException("While getting map (ArrayList of geometries) ", e);
            throw de;
        }
    	// restore dubfields
        arcimsStatus.setSubfields(savedFieldNames);

    	boolean bDrawShapes = true;
    	
    	if (lgnd instanceof SingleSymbolLegend) {
    		bDrawShapes = lgnd.getDefaultSymbol().isShapeVisible();
    	}
    	Point2D offset = viewPort.getOffset();
    	double dpi = MapContext.getScreenDPI();

    	if (bDrawShapes) {

    		try {

    			// Get the iterator over the visible features
    			IFeatureIterator it = new AvoidGeometryAndIdFeatureIterator(geometries, savedFieldNames.length);
    			ZSort zSort = ((IVectorLegend) getLegend()).getZSort();

    			boolean bSymbolLevelError = false;

    			// if layer has map levels it will use a ZSort
    			boolean useZSort = zSort != null && zSort.isUsingZSort();

    			// -- visual FX stuff
    			long time = System.currentTimeMillis();
    			BufferedImage virtualBim;
    			Graphics2D virtualGraphics;

    			// render temporary map each screenRefreshRate milliseconds;
    			int screenRefreshDelay = (int) ((1D/MapControl.getDrawFrameRate())*3*1000);
    			BufferedImage[] imageLevels = null;
    			Graphics2D[] graphics = null;
    			if (useZSort) {
    				imageLevels = new BufferedImage[zSort.getLevelCount()];
    				graphics = new Graphics2D[imageLevels.length];
    				for (int i = 0; !cancel.isCanceled() && i < imageLevels.length; i++) {
    					imageLevels[i] = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
    					graphics[i] = imageLevels[i].createGraphics();
    					graphics[i].setTransform(g.getTransform());
    					graphics[i].setRenderingHints(g.getRenderingHints());
    				}
    			}
    			// -- end visual FX stuff


    			int featindex = -1;
    			// Iteration over each feature
    			while ( !cancel.isCanceled() && it.hasNext()) {
    				
    				featindex++;
    				IFeature feat = it.next();
    				geom = feat.getGeometry();
    				
    				clongeom = geom.cloneGeometry();
    				geom = clongeom; 
    		        
    		        if ((layerTransf != null) && (isNotSame(layerTransf))) {
    		        	geom.reProject(layerTransf);
    		        }

    				// retrieve the symbol associated to such feature
    				ISymbol sym = lgnd.getSymbolByFeature(feat);

    				if (sym == null) continue;

    				//Código para poder acceder a los índices para ver si está seleccionado un Feature
    				ReadableVectorial rv=getSource();
    				int selectionIndex=-1;
    				
    				
    				FMapFeatureArcImsDriver imsdrv = (FMapFeatureArcImsDriver) getSource().getDriver();
    				selectionIndex = imsdrv.getOverallIndex(featindex);

    				if (selectionIndex!=-1) {
    					if (getSelectionSupport().isSelected(selectionIndex)) {
    						sym = sym.getSymbolForSelection();
    					}
    				}

    				// Check if this symbol is sized with CartographicSupport
    				CartographicSupport csSym = null;
    				int symbolType = sym.getSymbolType();
    				boolean bDrawCartographicSupport = false;

    				if (   symbolType == FShape.POINT
    						|| symbolType == FShape.LINE
    						|| sym instanceof CartographicSupport) {

    					// patch
    					if (!sym.getClass().equals(FSymbol.class)) {
    						csSym = (CartographicSupport) sym;
    						bDrawCartographicSupport = (csSym.getUnit() != -1);
    					}
    				}

    				int x = -1;
    				int y = -1;
    				int[] xyCoords = new int[2];

    				// Check if size is a pixel
    				boolean onePoint = bDrawCartographicSupport ?
    						isOnePoint(g.getTransform(), viewPort, MapContext.getScreenDPI(), csSym, geom, xyCoords) :
    							isOnePoint(g.getTransform(), viewPort, geom, xyCoords);

    						// Avoid out of bounds exceptions
    						if (onePoint) {
    							x = xyCoords[0];
    							y = xyCoords[1];
    							if (x<0 || y<0 || x>= viewPort.getImageWidth() || y>=viewPort.getImageHeight()) continue;
    						}

    						if (useZSort) {
    							// Check if this symbol is a multilayer
								int[] symLevels = zSort.getLevels(sym);
    							if (sym instanceof IMultiLayerSymbol) {
    								// if so, treat each of its layers as a single symbol
    								// in its corresponding map level
    								IMultiLayerSymbol mlSym = (IMultiLayerSymbol) sym;
    								for (int i = 0; !cancel.isCanceled() && i < mlSym.getLayerCount(); i++) {
    									ISymbol mySym = mlSym.getLayer(i);
        								int symbolLevel = 0;
        								if (symLevels != null) {
        									symbolLevel = symLevels[i];
        								} else {
    										/* an error occured when managing symbol levels.
    										 * some of the legend changed events regarding the
    										 * symbols did not finish satisfactory and the legend
    										 * is now inconsistent. For this drawing, it will finish
    										 * as it was at the bottom (level 0) but, when done, the
    										 * ZSort will be reset to avoid app crashes. This is
    										 * a bug that has to be fixed.
    										 */
    										bSymbolLevelError = true;
    									}

    									if (onePoint) {
    										if (x<0 || y<0 || x>= imageLevels[symbolLevel].getWidth() || y>=imageLevels[symbolLevel].getHeight()) continue;
    										imageLevels[symbolLevel].setRGB(x, y, mySym.getOnePointRgb());
    									} else {
    										if (!bDrawCartographicSupport) {
    											geom.drawInts(graphics[symbolLevel], viewPort, mySym, cancel);
    										} else {
    											geom.drawInts(graphics[symbolLevel], viewPort, dpi, (CartographicSupport) mySym, cancel);
    										}
    									}
    								}
    							} else {
    								// else, just draw the symbol in its level
    								int symbolLevel = 0;
    								if (symLevels != null) {

    									symbolLevel=symLevels[0];
    								} else {
    									/* If symLevels == null
    									 * an error occured when managing symbol levels.
    									 * some of the legend changed events regarding the
    									 * symbols did not finish satisfactory and the legend
    									 * is now inconsistent. For this drawing, it will finish
    									 * as it was at the bottom (level 0). This is
    									 * a bug that has to be fixed.
    									 */
//    									bSymbolLevelError = true;
    								}

    								if (!bDrawCartographicSupport) {
    									geom.drawInts(graphics[symbolLevel], viewPort, sym, cancel);
    								} else {
    									geom.drawInts(graphics[symbolLevel], viewPort, dpi, (CartographicSupport) csSym, cancel);
    								}
    							}

    							// -- visual FX stuff
    							// Cuando el offset!=0 se está dibujando sobre el Layout y por tanto no tiene que ejecutar el siguiente código.
    							if (offset.getX()==0 && offset.getY()==0)
    								if ((System.currentTimeMillis() - time) > screenRefreshDelay) {
    									virtualBim = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
    									virtualGraphics = virtualBim.createGraphics();
    									virtualGraphics.drawImage(image,0,0, null);
    									for (int i = 0; !cancel.isCanceled() && i < imageLevels.length; i++) {
    										virtualGraphics.drawImage(imageLevels[i],0,0, null);
    									}
    									g.clearRect(0, 0, image.getWidth(), image.getHeight());
    									g.drawImage(virtualBim, 0, 0, null);
    									time = System.currentTimeMillis();
    								}
    							// -- end visual FX stuff

    						} else {
    							// no ZSort, so there is only a map level, symbols are
    							// just drawn.
    							if (onePoint) {
    								if (x<0 || y<0 || x>= image.getWidth() || y>=image.getHeight()) continue;
    								image.setRGB(x, y, sym.getOnePointRgb());
    							} else {
    								if (!bDrawCartographicSupport) {
    									geom.drawInts(g, viewPort, sym, cancel);
    								} else {
    									geom.drawInts(g, viewPort, dpi, csSym, cancel);
    								}
    							}
    						}
    			}

    			if (useZSort) {
    				g.drawImage(image, 0, 0, null);
    				g.translate(offset.getX(), offset.getY());
    				for (int i = 0; !cancel.isCanceled() && i < imageLevels.length; i++) {
    					g.drawImage(imageLevels[i],0,0, null);
    					imageLevels[i] = null;
    					graphics[i] = null;
    				}
    				g.translate(-offset.getX(), -offset.getY());
    				imageLevels = null;
    				graphics = null;
    			}
    			it.closeIterator();

    			if (bSymbolLevelError) {
    				((IVectorLegend) getLegend()).setZSort(null);
    			}

    		} catch (ReadDriverException e) {
    			this.setVisible(false);
    			this.setActive(false);
    			throw e;
    		}
    	}
    }

    private String[] gvSigNamesToServerNames(String[] flds)
    {
      FMapFeatureArcImsDriver imsdrv = (FMapFeatureArcImsDriver)getSource().getDriver();
      return imsdrv.gvSigNamesToServerNames(flds);
    }



	private String[] replaceSpecialChars(String[] _legflds) {
		
		int len = _legflds.length;
		String[] resp = new String[len];
		for (int i=0; i<len; i++) {
			resp[i] = replaceSpecialChars(_legflds[i]);
		}
		return resp;
	}

	private String replaceSpecialChars(String str) {
		if (str.compareToIgnoreCase("zIDz") == 0) return "#ID#";
		if (str.compareToIgnoreCase("zSHAPEz") == 0) return "#SHAPE#";
		return str;
	}

	private boolean isNotSame(ICoordTrans trans) {
    	
    	String from_abb = trans.getPOrig().getAbrev(); 
    	String to_abb = trans.getPDest().getAbrev();
    	return (from_abb.compareTo(to_abb) != 0);
	}

	private String[] appendAtringArrays(String[] arr1, String[] arr2) {
    	
    	int len = arr1.length + arr2.length;
    	String[] resp = new String[len];
    	int i = 0;
    	for (i=0; i<arr1.length; i++) resp[i] = arr1[i];
    	for (i=0; i<arr2.length; i++) resp[arr1.length+i] = arr2[i];
		return resp;
	}

	/**
     *
     */
    public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
        Cancellable cancel, double scale) throws ReadDriverException {
    	
    	_draw(image, g, viewPort, cancel, scale);
    }

    private ISymbol getSymbolOrSelected(int i, ISymbol symbol) {
        boolean sel = false;

        try {
            sel = getRecordset().getSelectionSupport().isSelected(i);
        }
        catch (ReadDriverException e) {
            logger.error("While getting selection", e);
        }

        if (sel) {
            return SymbologyFactory.createDefaultSymbolByShapeType(shapeType, Color.YELLOW); 
        }
        else {
            return symbol;
        }
    }

    /**
     *
     */
    public void print(Graphics2D g, ViewPort viewPort, Cancellable cancel,
        double scale, PrintRequestAttributeSet properties)
        throws ReadDriverException {
        draw(null, g, viewPort, cancel, scale);
    }

    /**
     *
     */
    public String queryByPoint(Point p) throws ReadDriverException {
        Point2D screenCoords;
        Point2D geoCoords;
        double screenTol;
        double geoTol;

        screenCoords = new Point2D.Double(p.getX(), p.getY());
        geoCoords = new Point2D.Double(p.getX(), p.getY());

        AffineTransform af = getMapContext().getViewPort().getAffineTransform();

        try {
            af.inverseTransform(screenCoords, geoCoords);
        }
        catch (NoninvertibleTransformException e) {
            logger.error("Non invertible AffineTransform ", e);
        }

        screenTol = 2.0;
        geoTol = 1.0;
        geoTol = screenTol / af.getScaleX();

        FBitSet fbs = null;
		try {
			fbs = queryByPoint(geoCoords, geoTol);
		} catch (Exception e) {
			logger.error("While doing query by point: " + e.getMessage());
		}

        // this invoques the client to get attrs.
        try {
            ((ArcImsVectorialAdapter) getSource()).requestFeatureAttributes(fbs);
        }
        catch (ArcImsException e) {
        	ReadDriverException de = new ReadDriverException("While querying by point ", e);
            throw de;
        }

        String resp = getFormattedFieldsFromSetFeatures(fbs);
        System.err.println(resp); //TODO BORRAR ESTO

        return resp;
    }

    // TODO: move this to another class:
    private String getFormattedFieldsFromSetFeatures(FBitSet fbs) {
        String r = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
        r = r + "<FeatureInfoResponse>";
        r = r + "<LAYER ID=\"" + layerQuery + "\" NAME=\"" + getName() + "\">";

        for (int i = fbs.nextSetBit(0); i >= 0; i = fbs.nextSetBit(i + 1)) {
            r = r + getFormattedFieldsFromFeature(i);
        }

        r = r + "</LAYER>";
        r = r + "</FeatureInfoResponse>";

        return r;
    }

    // TODO: move this to another class:
    private String getFormattedFieldsFromFeature(int j) {
        String r = "<FIELDS ";
        String fieldName;
        String fieldValue;
        DataSource ds = null;

        try {
            ds = getSource().getRecordset();
        }
        catch (ReadDriverException e1) {
            logger.error("DriverException while getting field values. ", e1);
        }

        int count;

        try {
            count = ds.getFieldCount();

            for (int i = 0; i < count; i++) {
                fieldName = ds.getFieldName(i);
                fieldName = ArcImsInMemoryAttsTableDriver.replaceUnwantedCharacters(fieldName);

                fieldValue = restoreSpecialChars(ds.getFieldValue((long) j, i)
                                                   .toString());
                logger.debug("fieldvalue = " + fieldValue);
                r = r + " " + fieldName + "=" + "\"" + fieldValue + "\"";
            }

            // <FIELDS NOMBRE="LR-542" OBSERVACIO="" TIPO="CAUT3" _SHAPE_="[Geometry]" _ID_="1975" />
            r = r + "/>";
        }
        catch (ReadDriverException e) {
            logger.error("DriverException while getting field values. ", e);
        }

        return r;
    }

    private String restoreSpecialChars(String str) {
        /*
         * < 60 3c, > 62 3e, & 38 26, " 34 22, ' 39 27
         */
        String resp = str.replaceAll("\\x26", "&amp;");
        resp = resp.replaceAll("\\x3c", "&lt;");
        resp = resp.replaceAll("\\x3e", "&gt;");

        resp = resp.replaceAll("\\x22", "&quot;");
        resp = resp.replaceAll("\\x27", "&#39;");

        return resp;
    }

    public void setInitialLegend() {
        FMapFeatureArcImsDriver drv = (FMapFeatureArcImsDriver) getSource()
                                                                    .getDriver();

        //Try to get the legend from the service information
        try {
        	ArcImsClientP cli = drv.getClient();
        	String lyr_id_0 = (String) arcimsStatus.getLayerIds().get(0);
        	SelectableDataSource ds = getRecordset();
            ILegend initialleg = cli.getLegend(lyr_id_0, ds); 

            if (initialleg != null) {
                setLegend((IVectorLegend) initialleg);
            }

            return;
        }
        catch (Exception e) {
            logger.error("While setting initial legend ", e);
        }

    }

    // Classifiable
    public int getShapeType() throws ReadDriverException {
        FMapFeatureArcImsDriver drv = (FMapFeatureArcImsDriver) getSource()
                                                                    .getDriver();

        return drv.getShapeType();
    }

    public void setServiceInformationInStatus(ServiceInformation si) {
        this.arcimsStatus.setServiceInformation(si);
    }

    public ArcImsVectStatus getArcimsStatus() {
        return arcimsStatus;
    }

    public void setArcimsStatus(ArcImsVectStatus arcimsStatus) {
        this.arcimsStatus = arcimsStatus;
    }

    public boolean isFirstLoad() {
        return firstLoad;
    }

    public void setFirstLoad(boolean firstLoad) {
        this.firstLoad = firstLoad;
    }

    public URL getHost() {
        return host;
    }

    public void setHost(URL host) {
        this.host = host;
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

    public void setService(String service) {
        this.service = service;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getSRS() {
        return SRS;
    }

    public void setSRS(String srs) {
        SRS = srs;
    }

    public boolean getArcImsTransparency() {
        return arcImsTransparency;
    }

    public void setArcImsTransparency(boolean arcImsTransparency) {
        this.arcImsTransparency = arcImsTransparency;
    }

    public int getTransparency() {
        return transparency;
    }

    public void setTransparency(int transparency) {
        this.transparency = transparency;
    }

    public HashMap getProperties() {
        HashMap info = new HashMap();
        String[] layerNames = getLayerQuery().split(",");
        Vector layers = new Vector(layerNames.length);
        FMapFeatureArcImsDriver drv = (FMapFeatureArcImsDriver) getSource()
                                                                    .getDriver();

        try {
            if (drv.connect(myCanc)) {
                info.put("layerName", this.getName());
                info.put("serverUrl", this.getHost());
                info.put("serviceName", this.getService());
                info.put("serviceType", this.getServiceType());

                for (int i = 0; i < layerNames.length; i++)
                    layers.add(layerNames[i]);

                info.put("layerIds", layers);

                return info;
            }
        }
        catch (Exception e) {
            logger.error("Error in FRasterLyrArcIMS.getProperties() ", e);
        }

        return null;
    }

    public void setLayerIdsInStatus(Vector idsv) {
        arcimsStatus.setLayerIds(idsv);
    }

    public void setProjectionInStatus(String abbrev_srs) {
        arcimsStatus.setSrs(abbrev_srs);
    }

    public void setHostInStatus(URL url) {
        arcimsStatus.setServer(url.toString());
    }

    public void setServiceInStatus(String srv) {
        arcimsStatus.setService(srv);
    }

    public void setSubfieldsInStatus() {
        String[] subfields = new String[2];
        String id = (String) arcimsStatus.getLayerIds().get(0);
        ServiceInformationLayerFeatures silf = (ServiceInformationLayerFeatures) arcimsStatus.getServiceInfo()
                                                                                             .getLayerById(id);
        FieldInformation fi = (FieldInformation) silf.getFieldInformationByType(FieldInformation.ID)
                                                     .get(0);
        subfields[1] = fi.getName();
        fi = (FieldInformation) silf.getFieldInformationByType(FieldInformation.SHAPE)
                                    .get(0);
        subfields[0] = fi.getName();
        arcimsStatus.setSubfields(subfields);
    }

    public void legendChanged(LegendChangedEvent e) {
        MapContext fmap = getMapContext();

        if (fmap != null) {
            fmap.invalidate();
        }
    }

    public void setXMLEntity(XMLEntity xml) throws XMLException {
        try {
            setHost(new URL(xml.getStringProperty("_host")));
        }
        catch (MalformedURLException e) {
            logger.error("Bad url ", e);
        }

        setService(xml.getStringProperty("_service"));
        setLayerQuery(xml.getStringProperty("layerQuery"));
        setSrsAbrev(xml.getStringProperty("srs_abrev"));

        FMapFeatureArcImsDriver drv = new FMapFeatureArcImsDriver(host.toString(),
                service, layerQuery);

        if (!(drv.connect(myCanc))) {
            throw new XMLException(new DriverException(
                    "Unable to connect to server"));
        }

        drv.setLayer(this);

        Vector ids = new Vector();
        ids.add(getLayerQuery());
        setLayerIdsInStatus(ids);

        ArcImsClientP cli = drv.getClient();
        ServiceInformation si = cli.getServiceInformation();
        arcimsStatus.setServiceInformation(si);

        String srs = getSrsAbrev();
        arcimsStatus.setSrs(srs);

        if ((si.getFeaturecoordsys() == null) ||
                (si.getFeaturecoordsys().equals(""))) {
            si.setFeaturecoordsys(srs.substring(
                    ServiceInfoTags.vINI_SRS.length()).trim());
            logger.warn("Server provides no SRS. ");
        }

        Rectangle2D fext = null;

        try {
            fext = ((ArcImsFeatureClient) drv.getClient()).getLayerExtent(arcimsStatus);
        }
        catch (Exception e) {
            logger.error("While loading ArcIMS layer ", e);
        }

        arcimsStatus.setExtent(fext);
        drv.setFullExtent(fext);

        setSubfieldsInStatus();

        // drv.loadRecordSet();
        ArcImsVectorialAdapter oldadapter = new ArcImsVectorialAdapter(drv);
        ArcImsVectorialEditableAdapter adapter = new ArcImsVectorialEditableAdapter();

        String recordsetName = xml.getStringProperty("recordset-name");

        try {
            adapter.setOriginalDataSource(drv.getRecordSet(recordsetName));
            adapter.setOriginalVectorialAdapter(oldadapter);
        }
        catch (ReadDriverException e) {
            logger.error("While setting original data source ", e);
        }


        setAdapter(adapter);

        drv.setAdapter(adapter);

        setInitialLegend();

        try {
            setRecordset(((FMapFeatureArcImsDriver) getSource().getDriver()).getRecordSet());
        }
        catch (Exception e) {
            logger.error("While setting data source ", e);
        }

        super.setXMLEntity(xml);
    }

    public XMLEntity getXMLEntity() throws XMLException {
        XMLEntity resp = super.getXMLEntity();

        // PluginServices.getMDIManager().addView(null);
        resp.putProperty("srs_abrev", getMapContext().getProjection().getAbrev());
        resp.putProperty("_host", host.toString());
        resp.putProperty("_service", service);
        resp.putProperty("layerQuery", layerQuery);

        return resp;
    }

    public String getSrsAbrev() {
        return srsAbrev;
    }

    public void setSrsAbrev(String srsAbrev) {
        this.srsAbrev = srsAbrev;
    }

    /**
     * The extCatalogYNomenclator needs this creator.
     *
     * @param p a Map object with the following keys:
     *
     * (key, object type returned)
     * ---------------------------
     * "host", String (with or without the servlet path)
     * "service_name", String (remote service name)
     * "srs", String (coordinate system)
     * "layer_id", String (*single* remote layer's ID)
     * "layer_name", String (local layer name)
     *
     * @return a FRasterLyrArcIMS layer
     * @throws ConnectionException
     */

    /*
    
    public FFeatureLyrArcIMS(Map m) throws ConnectionException {
    
            String _host = (String) m.get("host");
    
            try {
    
                    String _layer_id = (String) m.get("layer_id");
                    String _service_name = (String) m.get("service_name");
                    String _layer_name = (String) m.get("layer_name");
                    String _srs = (String) m.get("srs");
    
                    // ----------------------------------
    
                    URL _true_host = ArcImsProtocolHandler.getUrlWithServlet(new URL(_host));
                    IProjection _true_srs = CRSFactory.getCRS(_srs);
    
    
                    FMapFeatureArcImsDriver _drv = new FMapFeatureArcImsDriver(_true_host.toString(),
                                    _service_name, _layer_id);
                    if (!(_drv.connect(myCanc))) throw new Exception();
                    ArcImsVectorialAdapter _oldadapter = new ArcImsVectorialAdapter(_drv);
                    VectorialEditableAdapter _adapter = new VectorialEditableAdapter();
                    _drv.setLayer(this);
    
                    ServiceInformation _si = _drv.getClient().getServiceInformation();
                    ServiceInformationLayerFeatures _silf = (ServiceInformationLayerFeatures)
                    _si.getLayerById(_layer_id);
    
                    setProjectionInStatus(_true_srs.getAbrev());
                    setHostInStatus(_true_host);
                    setServiceInStatus(_service_name);
    
                    String _units = _si.getMapunits();
                    int _theDpi = _si.getScreen_dpi();
                    long _scale;
                    if (_silf.getMaxscale()!=-1) {
                            _scale = LayerScaleData.getTrueScaleFromRelativeScaleAndMapUnits(
                                            _silf.getMaxscale(), _units, _theDpi);
                            setMaxScale((double) _scale);
                    }
                    if (_silf.getMinscale()!=-1) {
                            _scale = LayerScaleData.getTrueScaleFromRelativeScaleAndMapUnits(
                                            _silf.getMinscale(), _units, _theDpi);
                            setMinScale((double) _scale);
                    }
    
                    setServiceInformationInStatus(_si);
                    Vector _ids = new Vector(); _ids.add(_layer_id);
                    setLayerIdsInStatus((Vector) _ids.clone());
                    setSubfieldsInStatus();
    
                    setHost(_true_host);
                    setService(_service_name);
                    setServiceType(ServiceInfoTags.vFEATURESERVICE);
                    setTransparency(0);
                    setLayerQuery(_layer_id);
                    setProjection(_true_srs);
                    setName(_layer_name);
    
                    Rectangle2D _fext = ((ArcImsFeatureClient)
                                    _drv.getClient()).getLayerExtent(getArcimsStatus());
                    _drv.setFullExtent(_fext);
                    // individualLayers[i].setF. setFullExtent(((ArcImsProtImageHandler) drv.getClient().getHandler()).getServiceExtent(srs, individualLayers[i].getArcimsStatus()));
    
                    // ------ -------------
                    _drv.setAdapter(_adapter);
                    // adapter.setRecordSet(drv.getRecordSet());
                    _adapter.setOriginalDataSource(_drv.getRecordSet());
                    _adapter.setOriginalVectorialAdapter(_oldadapter);
                    _drv.declareTable();
    
                    setSource(_adapter);
                    _adapter.setDriver(_drv);
    
                    getSource().setDriver(_drv);
                    setInitialLegend();
                    setShapeType(_adapter.getShapeType());
                    setRecordset(_drv.getRecordSet());
                    // ------ -------------
    
                    if ((_si.getFeaturecoordsys() == null) ||
                                    (_si.getFeaturecoordsys().equals(""))) {
                            _si.setFeaturecoordsys(_true_srs.getAbrev().substring(ServiceInfoTags.vINI_SRS.length()).trim());
                            logger.warn("Server provides no SRS. ");
                    }
            } catch (Exception e) {
                    throw new ConnectionException("Unable to connect to host " + _host, e);
            }
    }
    
    */
    public ImageIcon getTocImageIcon() {
        ImageIcon resp = null;

        try {
            resp = createImageIcon("images/esrilogo.png");
        }
        catch (Exception ex) {
        }

        if (resp == null) {
            return super.getTocImageIcon();
        }
        else {
            return resp;
        }
    }

    protected ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = createExtensionUrl(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        }
        else {
            logger.error("File not found: " + path);

            return null;
        }
    }

    protected java.net.URL createExtensionUrl(String path) {
        return PluginServices.getPluginServices(
            "es.prodevelop.cit.gvsig.arcims").getClassLoader().getResource(path);

        // return getClass().getClassLoader().getResource(path);
    }
    
    
    
    public Rectangle2D getFullExtent() throws ReadDriverException, ExpansionFileReadException {
    	
    	ReadableVectorial rv = getSource();
    	
        Rectangle2D rAux;
        rv.start();
        rAux = (Rectangle2D) rv.getFullExtent().clone();
        rv.stop();

        // Si existe reproyección, reproyectar el extent
        ICoordTrans ct = getCoordTrans();
		try{
        	if ((ct != null) && (!sourceEqualsTarget(ct))) {
            	Point2D pt1 = new Point2D.Double(rAux.getMinX(), rAux.getMinY());
            	Point2D pt2 = new Point2D.Double(rAux.getMaxX(), rAux.getMaxY());
            	pt1 = ct.convert(pt1, null);
            	pt2 = ct.convert(pt2, null);
            	rAux = new Rectangle2D.Double();
            	rAux.setFrameFromDiagonal(pt1, pt2);
        	}
		} catch (IllegalStateException e) {
			// this.setAvailable(false);
			// this.addError(new ReprojectLayerException(getName(), e));
			logger.error("Unable to reproject extent: " + rAux.toString());
			logger.error("Transf from CRS: " + ct.getPOrig().getFullCode());
			logger.error("Transf to CRS  : " + ct.getPDest().getFullCode());
			logger.warn("No reprojection made.");
		}
        //Esto es para cuando se crea una capa nueva con el fullExtent de ancho y alto 0.
        if (rAux.getWidth()==0 && rAux.getHeight()==0) {
            rAux=new Rectangle2D.Double(0,0,100,100);
        }

        return rAux;

    }
    
    private boolean sourceEqualsTarget(ICoordTrans ct) {
    	
    	IProjection p1 = ct.getPOrig();
    	IProjection p2 = ct.getPDest();
    	String str1 = p1.getFullCode();
    	String str2 = p2.getFullCode();
    	return (str1.compareTo(str2) == 0);
}
    
    
    

    /**
     * Keeps the image's height and width or the image's tiles' height and
     * width, if any. These values are the same as the viewPort's height and
     * width,
     */
    private class VisualStatusArcIms {
        protected int width = 0;
        protected int height = 0;
        protected double minX = 0D;
        protected double minY = 0D;
        protected double maxX = 0D;
        protected double maxY = 0D;
    }
    
    private class AvoidGeometryAndIdFeatureIterator implements IFeatureIterator {
    	
    	private ArrayList featArray;
    	private int index = 0;
    	private int size = 0;
    	private int noToAvoid = 0;
    	
    	public AvoidGeometryAndIdFeatureIterator(ArrayList arr, int navoided) {
    		featArray = arr;
    		size = arr.size();
    		index = 0;
    		noToAvoid = navoided;
    	}

		public void closeIterator() throws ReadDriverException {
    		index = 0;
		}

		public boolean hasNext() throws ReadDriverException {
			return (index < size);
		}

		public IFeature next() throws ReadDriverException {
			index++;
			if (index > size) {
				logger.error("Out of index in AuxFeatureIterator: " + (index-1));
				return null;
			} else {
				IFeature resp = (IFeature) featArray.get(index-1);
				Value[] oldatts = resp.getAttributes();
				Value[] newatts = new Value[oldatts.length - noToAvoid];
				for (int i=0; i<newatts.length; i++) newatts[i] = oldatts[i + noToAvoid];
				resp.setAttributes(newatts);
				return resp;
			}
		}
    	
    }
}
