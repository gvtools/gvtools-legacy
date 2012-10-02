package org.gvsig.geotools.test;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PrintQuality;

import org.apache.log4j.Logger;
import org.cresques.cts.ProjectionUtils;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.collection.SimpleFeatureIteratorImpl;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.instruction.FieldNotFoundException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.exceptions.layers.ReloadLayerException;
import com.iver.cit.gvsig.exceptions.layers.ReprojectLayerException;
import com.iver.cit.gvsig.exceptions.layers.StartEditionLayerException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.CartographicSupport;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FGeometry;
import com.iver.cit.gvsig.fmap.core.FPoint2D;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.gt2.FLiteShape;
import com.iver.cit.gvsig.fmap.core.symbols.IMultiLayerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.WithDefaultLegend;
import com.iver.cit.gvsig.fmap.drivers.featureiterators.JoinFeatureIterator;
import com.iver.cit.gvsig.fmap.edition.AfterFieldEditEvent;
import com.iver.cit.gvsig.fmap.edition.AfterRowEditEvent;
import com.iver.cit.gvsig.fmap.edition.BeforeFieldEditEvent;
import com.iver.cit.gvsig.fmap.edition.BeforeRowEditEvent;
import com.iver.cit.gvsig.fmap.edition.EditionEvent;
import com.iver.cit.gvsig.fmap.layers.AbstractLinkProperties;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.FLyrVectLinkProperties;
import com.iver.cit.gvsig.fmap.layers.LegendChangedEvent;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.SelectionEvent;
import com.iver.cit.gvsig.fmap.layers.SelectionSupport;
import com.iver.cit.gvsig.fmap.layers.SpatialCache;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.VectorialXMLItem;
import com.iver.cit.gvsig.fmap.layers.layerOperations.XMLItem;
import com.iver.cit.gvsig.fmap.operations.strategies.FeatureVisitor;
import com.iver.cit.gvsig.fmap.operations.strategies.Strategy;
import com.iver.cit.gvsig.fmap.operations.strategies.StrategyManager;
import com.iver.cit.gvsig.fmap.rendering.IClassifiedVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendClearEvent;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;
import com.iver.cit.gvsig.fmap.rendering.SymbolLegendEvent;
import com.iver.cit.gvsig.fmap.rendering.ZSort;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.AttrInTableLabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.ILabelingStrategy;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelingFactory;
import com.iver.cit.gvsig.fmap.spatialindex.ISpatialIndex;
import com.iver.utiles.NotExistInXMLEntity;
import com.iver.utiles.PostProcessSupport;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.swing.threads.Cancellable;
import com.iver.utiles.swing.threads.CancellableMonitorable;
import com.vividsolutions.jts.geom.Geometry;

public class GTFLyrVect extends FLyrVect {
	private static Logger logger = Logger.getLogger(GTFLyrVect.class.getName());

	private IVectorLegend legend;
	private SpatialCache spatialCache = new SpatialCache();
	private boolean spatialCacheEnabled = false;

	protected ISpatialIndex spatialIndex = null;
	private boolean bHasJoin = false;
	private XMLEntity orgXMLEntity = null;
	private XMLEntity loadSelection = null;
	private IVectorLegend loadLegend = null;
	private SimpleFeatureSource source;

	private FLyrVectLinkProperties linkProperties = new FLyrVectLinkProperties();
	private boolean waitTodraw = false;

	public GTFLyrVect(File file) throws IOException {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("url", file.toURI().toURL());
		DataStore store = DataStoreFinder.getDataStore(map);
		String typeName = store.getTypeNames()[0];
		source = store.getFeatureSource(typeName);
	}

	public GTFLyrVect(SimpleFeatureSource source) {
		this.source = source;
	}

	public boolean isWaitTodraw() {
		return waitTodraw;
	}

	@Override
	public ReadableVectorial getSource() {
		return null;
	}

	public void setWaitTodraw(boolean waitTodraw) {
		this.waitTodraw = waitTodraw;
	}

	@Override
	public boolean isExternallySpatiallyIndexed() {
		return false;
	}

	@Override
	public void setSource(ReadableVectorial rv) {
	}

	@Override
	public Rectangle2D getFullExtent() throws ReadDriverException,
			ExpansionFileReadException {
		Rectangle2D rAux;
		rAux = (Rectangle2D) computeFullExtent();

		// Si existe reproyección, reproyectar el extent
		if (!(this.getCrs() != null && this.getMapContext().getCrs() != null && this
				.getCrs().getName()
				.equals(this.getMapContext().getCrs().getName()))) {
			MathTransform transform = getCrsTransform();
			try {
				if (transform != null) {
					Point2D pt1 = new Point2D.Double(rAux.getMinX(),
							rAux.getMinY());
					Point2D pt2 = new Point2D.Double(rAux.getMaxX(),
							rAux.getMaxY());
					pt1 = ProjectionUtils.transform(pt1, transform);
					pt2 = ProjectionUtils.transform(pt2, transform);
					rAux = new Rectangle2D.Double();
					rAux.setFrameFromDiagonal(pt1, pt2);
				}
			} catch (IllegalStateException e) {
				this.setAvailable(false);
				this.addError(new ReprojectLayerException(getName(), e));
			}
		}
		// Esto es para cuando se crea una capa nueva con el fullExtent de ancho
		// y alto 0.
		if (rAux.getWidth() == 0 && rAux.getHeight() == 0) {
			rAux = new Rectangle2D.Double(0, 0, 100, 100);
		}

		return rAux;
	}

	private Rectangle2D computeFullExtent() {
		return new Rectangle2D.Double(-2400000, 3000000, 4000000, 2500000);
	}

	public void draw(BufferedImage image, Graphics2D g, ViewPort viewPort,
			Cancellable cancel, double scale) throws ReadDriverException {
		// forTestOnlyVariableUseIterators_REMOVE_THIS_FIELD = true;
		// if (!isUseStrategy()) {
		if (isWaitTodraw()) {
			return;
		}

		boolean bDrawShapes = true;
		if (legend instanceof SingleSymbolLegend) {
			bDrawShapes = legend.getDefaultSymbol().isShapeVisible();
		}
		Point2D offset = viewPort.getOffset();
		double dpi = MapContext.getScreenDPI();

		if (bDrawShapes) {
			boolean cacheFeatures = isSpatialCacheEnabled();
			SpatialCache cache = null;
			if (cacheFeatures) {
				getSpatialCache().clearAll();
				cache = getSpatialCache();
			}

			try {
				ArrayList<String> fieldList = new ArrayList<String>();

				// fields from legend
				String[] aux = null;

				if (legend instanceof IClassifiedVectorLegend) {
					aux = ((IClassifiedVectorLegend) legend)
							.getClassifyingFieldNames();
					if (aux != null) {
						for (int i = 0; i < aux.length; i++) {
							// check fields exists
							if (getFieldIndexByName(aux[i]) == -1) {
								logger.warn("Error en leyenda de " + getName()
										+ ". El campo " + aux[i] + " no está.");
								legend = LegendFactory
										.createSingleSymbolLegend(getShapeType());
								break;
							}
							fieldList.add(aux[i]);
						}
					}
				}
				// Get the iterator over the visible features
				IFeatureIterator it = null;
				if (isJoined()) {
					it = new JoinFeatureIterator(this, viewPort,
							fieldList.toArray(new String[fieldList.size()]));
				} else {
					it = getFeatureIterator(viewPort.getAdjustedExtent(),
							fieldList.toArray(new String[fieldList.size()]),
							viewPort.getCrs());
				}

				ZSort zSort = ((IVectorLegend) getLegend()).getZSort();

				boolean bSymbolLevelError = false;

				// if layer has map levels it will use a ZSort
				boolean useZSort = zSort != null && zSort.isUsingZSort();

				// -- visual FX stuff
				long time = System.currentTimeMillis();
				BufferedImage virtualBim;
				Graphics2D virtualGraphics;

				// render temporary map each screenRefreshRate milliseconds;
				int screenRefreshDelay = (int) ((1D / MapControl
						.getDrawFrameRate()) * 3 * 1000);
				BufferedImage[] imageLevels = null;
				Graphics2D[] graphics = null;
				if (useZSort) {
					imageLevels = new BufferedImage[zSort.getLevelCount()];
					graphics = new Graphics2D[imageLevels.length];
					for (int i = 0; !cancel.isCanceled()
							&& i < imageLevels.length; i++) {
						imageLevels[i] = new BufferedImage(image.getWidth(),
								image.getHeight(), image.getType());
						graphics[i] = imageLevels[i].createGraphics();
						graphics[i].setTransform(g.getTransform());
						graphics[i].setRenderingHints(g.getRenderingHints());
					}
				}
				// -- end visual FX stuff

				boolean isInMemory = false;
				if (getDriverAttributes() != null) {
					isInMemory = getDriverAttributes().isLoadedInMemory();
				}
				// TODO geotools refactoring: currently no selection
				// SelectionSupport selectionSupport = getSelectionSupport();
				// Iteration over each feature
				while (!cancel.isCanceled() && it.hasNext()) {
					IFeature feat = it.next();
					IGeometry geom = null;

					if (isInMemory) {
						geom = feat.getGeometry().cloneGeometry();
					} else {
						geom = feat.getGeometry();
					}

					if (cacheFeatures) {
						if (cache.getMaxFeatures() >= cache.size()) {
							// already reprojected
							cache.insert(geom.getBounds2D(), geom);
						}
					}

					// retrieve the symbol associated to such feature
					ISymbol sym = legend.getSymbolByFeature(feat);

					if (sym == null)
						continue;

					// Código para poder acceder a los índices para ver si está
					// seleccionado un Feature
					// TODO geotools refactoring: currently no selection
					// int selectionIndex = Integer.parseInt(feat.getID());
					// if (selectionIndex != -1) {
					// if (selectionSupport.isSelected(selectionIndex)) {
					// sym = sym.getSymbolForSelection();
					// }
					// }

					// Check if this symbol is sized with CartographicSupport
					CartographicSupport csSym = null;
					int symbolType = sym.getSymbolType();
					boolean bDrawCartographicSupport = false;

					if (symbolType == FShape.POINT || symbolType == FShape.LINE
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
					boolean onePoint = bDrawCartographicSupport ? isOnePoint(
							g.getTransform(), viewPort,
							MapContext.getScreenDPI(), csSym, geom, xyCoords)
							: isOnePoint(g.getTransform(), viewPort, geom,
									xyCoords);

					// Avoid out of bounds exceptions
					if (onePoint) {
						x = xyCoords[0];
						y = xyCoords[1];
						if (x < 0 || y < 0 || x >= viewPort.getImageWidth()
								|| y >= viewPort.getImageHeight())
							continue;
					}

					if (useZSort) {
						// Check if this symbol is a multilayer
						int[] symLevels = zSort.getLevels(sym);
						if (sym instanceof IMultiLayerSymbol) {
							// if so, treat each of its layers as a single
							// symbol
							// in its corresponding map level
							IMultiLayerSymbol mlSym = (IMultiLayerSymbol) sym;
							for (int i = 0; !cancel.isCanceled()
									&& i < mlSym.getLayerCount(); i++) {
								ISymbol mySym = mlSym.getLayer(i);
								int symbolLevel = 0;
								if (symLevels != null) {
									symbolLevel = symLevels[i];
								} else {
									/*
									 * an error occured when managing symbol
									 * levels. some of the legend changed events
									 * regarding the symbols did not finish
									 * satisfactory and the legend is now
									 * inconsistent. For this drawing, it will
									 * finish as it was at the bottom (level 0)
									 * but, when done, the ZSort will be reset
									 * to avoid app crashes. This is a bug that
									 * has to be fixed.
									 */
									bSymbolLevelError = true;
								}

								if (onePoint) {
									if (x < 0
											|| y < 0
											|| x >= imageLevels[symbolLevel]
													.getWidth()
											|| y >= imageLevels[symbolLevel]
													.getHeight())
										continue;
									imageLevels[symbolLevel].setRGB(x, y,
											mySym.getOnePointRgb());
								} else {
									if (!bDrawCartographicSupport) {
										geom.drawInts(graphics[symbolLevel],
												viewPort, mySym, cancel);
									} else {
										geom.drawInts(graphics[symbolLevel],
												viewPort, dpi,
												(CartographicSupport) mySym,
												cancel);
									}
								}
							}
						} else {
							// else, just draw the symbol in its level
							int symbolLevel = 0;
							if (symLevels != null) {

								symbolLevel = symLevels[0];
							} else {
								/*
								 * If symLevels == null an error occured when
								 * managing symbol levels. some of the legend
								 * changed events regarding the symbols did not
								 * finish satisfactory and the legend is now
								 * inconsistent. For this drawing, it will
								 * finish as it was at the bottom (level 0).
								 * This is a bug that has to be fixed.
								 */
								// bSymbolLevelError = true;
							}

							// if (!bDrawCartographicSupport) {
							geom.draw(graphics[symbolLevel], viewPort, sym,
									cancel);
							// } else {
							// geom.draw(graphics[symbolLevel], viewPort,
							// dpi, (CartographicSupport) csSym,
							// cancel);
							// }
						}

						// -- visual FX stuff
						// Cuando el offset!=0 se está dibujando sobre el Layout
						// y por tanto no tiene que ejecutar el siguiente
						// código.
						if (offset.getX() == 0 && offset.getY() == 0)
							if ((System.currentTimeMillis() - time) > screenRefreshDelay) {
								virtualBim = new BufferedImage(
										image.getWidth(), image.getHeight(),
										BufferedImage.TYPE_INT_ARGB);
								virtualGraphics = virtualBim.createGraphics();
								virtualGraphics.drawImage(image, 0, 0, null);
								for (int i = 0; !cancel.isCanceled()
										&& i < imageLevels.length; i++) {
									virtualGraphics.drawImage(imageLevels[i],
											0, 0, null);
								}
								g.clearRect(0, 0, image.getWidth(),
										image.getHeight());
								g.drawImage(virtualBim, 0, 0, null);
								time = System.currentTimeMillis();
							}
						// -- end visual FX stuff

					} else {
						// no ZSort, so there is only a map level, symbols are
						// just drawn.
						if (onePoint) {
							if (x < 0 || y < 0 || x >= image.getWidth()
									|| y >= image.getHeight())
								continue;
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
					g.drawImage(image, (int) offset.getX(),
							(int) offset.getY(), null);
					g.translate(offset.getX(), offset.getY());
					for (int i = 0; !cancel.isCanceled()
							&& i < imageLevels.length; i++) {
						g.drawImage(imageLevels[i], 0, 0, null);
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

	public void print(Graphics2D g, ViewPort viewPort, Cancellable cancel,
			double scale, PrintRequestAttributeSet properties)
			throws ReadDriverException {
		// TEST METHOD
		boolean bDrawShapes = true;
		if (legend instanceof SingleSymbolLegend) {
			bDrawShapes = legend.getDefaultSymbol().isShapeVisible();
		}

		if (bDrawShapes) {

			try {
				double dpi = 72;

				PrintQuality resolution = (PrintQuality) properties
						.get(PrintQuality.class);
				if (resolution.equals(PrintQuality.NORMAL)) {
					dpi = 300;
				} else if (resolution.equals(PrintQuality.HIGH)) {
					dpi = 600;
				} else if (resolution.equals(PrintQuality.DRAFT)) {
					dpi = 72;
				}
				ArrayList<String> fieldList = new ArrayList<String>();
				String[] aux;

				// fields from legend
				if (legend instanceof IClassifiedVectorLegend) {
					aux = ((IClassifiedVectorLegend) legend)
							.getClassifyingFieldNames();
					for (int i = 0; i < aux.length; i++) {
						fieldList.add(aux[i]);
					}
				}

				ZSort zSort = ((IVectorLegend) getLegend()).getZSort();

				// if layer has map levels it will use a ZSort
				boolean useZSort = zSort != null && zSort.isUsingZSort();

				int mapLevelCount = (useZSort) ? zSort.getLevelCount() : 1;
				for (int mapPass = 0; mapPass < mapLevelCount; mapPass++) {
					IFeatureIterator it = null;
					if (isJoined()) {
						it = new JoinFeatureIterator(this, viewPort,
								fieldList.toArray(new String[fieldList.size()]));
					} else {
						it = getFeatureIterator(
								viewPort.getAdjustedExtent(),
								fieldList.toArray(new String[fieldList.size()]),
								viewPort.getCrs());
					}

					// Iteration over each feature
					while (!cancel.isCanceled() && it.hasNext()) {
						IFeature feat = it.next();
						IGeometry geom = feat.getGeometry();

						// retreive the symbol associated to such feature
						ISymbol sym = legend.getSymbolByFeature(feat);
						if (sym == null) {
							continue;
						}
						if (useZSort) {
							int[] symLevels = zSort.getLevels(sym);
							if (symLevels != null) {
								// Check if this symbol is a multilayer
								if (sym instanceof IMultiLayerSymbol) {
									// if so, get the layer corresponding to the
									// current
									// level. If none, continue to next
									// iteration
									IMultiLayerSymbol mlSym = (IMultiLayerSymbol) sym;
									for (int i = 0; i < mlSym.getLayerCount(); i++) {
										ISymbol mySym = mlSym.getLayer(i);
										if (symLevels[i] == mapPass) {
											sym = mySym;
											break;
										}
										System.out.println("avoided layer " + i
												+ "of symbol '"
												+ mlSym.getDescription()
												+ "' (pass " + mapPass + ")");
									}
								} else {
									// else, just draw the symbol in its level
									if (symLevels[0] != mapPass) {
										System.out
												.println("avoided single layer symbol '"
														+ sym.getDescription()
														+ "' (pass "
														+ mapPass
														+ ")");
										continue;
									}
								}
							}
						}

						// Check if this symbol is sized with
						// CartographicSupport
						CartographicSupport csSym = null;
						int symbolType = sym.getSymbolType();

						if (symbolType == FShape.POINT
								|| symbolType == FShape.LINE
								|| sym instanceof CartographicSupport) {

							csSym = (CartographicSupport) sym;
						}

						// System.err.println("passada "+mapPass+" pinte símboll "+sym.getDescription());

						if (csSym == null) {
							geom.drawInts(g, viewPort, sym, null);
						} else {
							geom.drawInts(g, viewPort, dpi,
									(CartographicSupport) csSym, cancel);
						}

					}
					it.closeIterator();
				}
			} catch (ReadDriverException e) {
				this.setVisible(false);
				this.setActive(false);
				throw e;
			}
		}
	}

	public void deleteSpatialIndex() {
		// must we delete possible spatial indexes files?
		spatialIndex = null;
	}

	@Override
	public void createSpatialIndex(CancellableMonitorable cancelMonitor) {
		// TODO geotools refactoring: currently no spatial index
		// // FJP: ESTO HABRÁ QUE CAMBIARLO. PARA LAS CAPAS SECUENCIALES,
		// TENDREMOS
		// // QUE ACCEDER CON UN WHILE NEXT. (O mejorar lo de los FeatureVisitor
		// // para que acepten recorrer sin geometria, solo con rectangulos.
		//
		// // If this vectorial layer is based in a spatial database, the
		// spatial
		// // index is already implicit. We only will index file drivers
		// // We must think in non spatial databases, like HSQLDB
		// ISpatialIndex localCopy = null;
		// try {
		// localCopy = new QuadtreeGt2(fileName, "NM", getFullExtent(),
		// getShapeCount(), true);
		//
		// } catch (SpatialIndexException e1) {
		// // Probably we dont have writing permissions
		// String directoryName = System.getProperty("java.io.tmpdir");
		// File newFile = new File(directoryName + File.separator
		// + file.getName());
		// String newFileName = newFile.getName();
		// try {
		// localCopy = new QuadtreeGt2(newFileName, "NM", getFullExtent(),
		// getShapeCount(), true);
		// } catch (SpatialIndexException e) {
		// // if we cant build a file based spatial index, we'll build
		// // a pure memory spatial index
		// localCopy = new QuadtreeJts();
		// } catch (ReadDriverException e) {
		// localCopy = new QuadtreeJts();
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }// try
		// BoundedShapes shapeBounds = (BoundedShapes) va.getDriver();
		// try {
		// for (int i = 0; i < getShapeCount(); i++) {
		// if (cancelMonitor != null) {
		// if (cancelMonitor.isCanceled())
		// return;
		// cancelMonitor.reportStep();
		// }
		// Rectangle2D r = shapeBounds.getShapeBounds(i);
		// if (r != null)
		// localCopy.insert(r, i);
		// } // for
		// if (localCopy instanceof IPersistentSpatialIndex)
		// ((IPersistentSpatialIndex) localCopy).flush();
		// spatialIndex = localCopy;
		// } catch (ReadDriverException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	public void createSpatialIndex() {
		createSpatialIndex(null);
	}

	public void process(FeatureVisitor visitor, FBitSet subset)
			throws ReadDriverException, ExpansionFileReadException,
			VisitorException {
		Strategy s = StrategyManager.getStrategy(this);
		s.process(visitor, subset);
	}

	public void process(FeatureVisitor visitor) throws ReadDriverException,
			VisitorException {
		Strategy s = StrategyManager.getStrategy(this);
		s.process(visitor);
	}

	public void process(FeatureVisitor visitor, Rectangle2D rect)
			throws ReadDriverException, ExpansionFileReadException,
			VisitorException {
		Strategy s = StrategyManager.getStrategy(this);
		s.process(visitor, rect);
	}

	public FBitSet queryByRect(Rectangle2D rect) throws ReadDriverException,
			VisitorException {
		Strategy s = StrategyManager.getStrategy(this);

		return s.queryByRect(rect);
	}

	public FBitSet queryByPoint(Point2D p, double tolerance)
			throws ReadDriverException, VisitorException {
		Strategy s = StrategyManager.getStrategy(this);
		return s.queryByPoint(p, tolerance);
	}

	public FBitSet queryByShape(IGeometry g, int relationship)
			throws ReadDriverException, VisitorException {
		Strategy s = StrategyManager.getStrategy(this);
		return s.queryByShape(g, relationship);
	}

	public XMLItem[] getInfo(Point p, double tolerance, Cancellable cancel)
			throws ReadDriverException, VisitorException {
		Point2D pReal = this.getMapContext().getViewPort().toMapPoint(p);
		FBitSet bs = queryByPoint(pReal, tolerance);
		VectorialXMLItem[] item = new VectorialXMLItem[1];
		item[0] = new VectorialXMLItem(bs, this);

		return item;
	}

	public void setLegend(IVectorLegend r) throws LegendLayerException {
		if (this.legend == r) {
			return;
		}
		if (this.legend != null && this.legend.equals(r)) {
			return;
		}
		IVectorLegend oldLegend = legend;

		legend = r;
		try {
			legend.setDataSource(getRecordset());
		} catch (FieldNotFoundException e1) {
			throw new LegendLayerException(getName(), e1);
		} catch (ReadDriverException e1) {
			throw new LegendLayerException(getName(), e1);
		} finally {
			this.updateDrawVersion();
		}
		if (oldLegend != null) {
			oldLegend.removeLegendListener(this);
		}
		if (legend != null) {
			legend.addLegendListener(this);
		}
		LegendChangedEvent e = LegendChangedEvent.createLegendChangedEvent(
				oldLegend, legend);
		e.setLayer(this);
		callLegendChanged(e);
	}

	/**
	 * Devuelve la Leyenda de la capa.
	 * 
	 * @return Leyenda.
	 */
	public ILegend getLegend() {
		return legend;
	}

	@Override
	public int getShapeType() throws ReadDriverException {
		// TODO implement correctly
		return FShape.LINE;
	}

	public XMLEntity getXMLEntity() throws XMLException {
		if (!this.isAvailable() && this.orgXMLEntity != null) {
			return this.orgXMLEntity;
		}
		XMLEntity xml = super.getXMLEntity();
		if (getLegend() != null)
			xml.addChild(getLegend().getXMLEntity());
		try {
			if (getRecordset() != null)
				xml.addChild(getRecordset().getSelectionSupport()
						.getXMLEntity());
		} catch (ReadDriverException e1) {
			e1.printStackTrace();
			throw new XMLException(e1);
		}
		// Repongo el mismo ReadableVectorial más abajo para cuando se guarda el
		// proyecto.
		xml.putProperty("type", "vectorial");
		if (bHasJoin)
			xml.putProperty("hasJoin", "true");

		// properties from ILabelable
		xml.putProperty("isLabeled", isLabeled);
		if (strategy != null) {
			XMLEntity strategyXML = strategy.getXMLEntity();
			strategyXML.putProperty("Strategy", strategy.getClassName());
			xml.addChild(strategy.getXMLEntity());
		}
		xml.addChild(getLinkProperties().getXMLEntity());
		return xml;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.layers.FLyrDefault#setXMLEntity(com.iver.utiles.XMLEntity)
	 */
	public void setXMLEntity03(XMLEntity xml) throws XMLException {

		super.setXMLEntity(xml);
		legend = LegendFactory.createFromXML03(xml.getChild(0));

		try {
			setLegend(legend);
		} catch (LegendLayerException e) {
			throw new XMLException(e);
		}

		try {
			getRecordset().getSelectionSupport()
					.setXMLEntity03(xml.getChild(1));
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setXMLEntity(XMLEntity xml) throws XMLException {
		try {
			super.setXMLEntity(xml);
			XMLEntity legendXML = xml.getChild(0);
			IVectorLegend leg = LegendFactory.createFromXML(legendXML);

			/*
			 * Parche para detectar cuando, por algun problema de persistencia
			 * respecto a versiones anteriores, la leyenda que se ha cargado no
			 * se corresponde con el tipo de shape de la capa.
			 */
			int legShapeType = leg.getShapeType();
			if (legShapeType != 0 && legShapeType != this.getShapeType()) {
				leg = LegendFactory.createSingleSymbolLegend(this
						.getShapeType());
				logger.warn("Legend shape type and layer shape type does not match.");
			}
			/* Fin del parche */

			try {
				getRecordset().getSelectionSupport().setXMLEntity(
						xml.getChild(1));
				// JMVIVO: Esto sirve para algo????
				/*
				 * Jaume: si, para restaurar el selectable datasource cuando se
				 * clona la capa, cuando se carga de un proyecto. Si no esta ya
				 * no se puede ni hacer consultas sql, ni hacer selecciones, ni
				 * usar la mayor parte de las herramientas.
				 * 
				 * Lo vuelvo a poner.
				 */

				// TODO geotools refactoring
				// String recordsetName =
				// xml.getStringProperty("recordset-name");
				// LayerFactory.getDataSourceFactory().changeDataSourceName(
				// getSource().getRecordset().getName(), recordsetName);
			} catch (ReadDriverException e1) {
				this.setAvailable(false);
				throw new XMLException(e1);
			}
			// Si tiene una unión, lo marcamos para que no se cree la leyenda
			// hasta
			// el final
			// de la lectura del proyecto
			if (xml.contains("hasJoin")) {
				setIsJoined(true);
				PostProcessSupport.addToPostProcess(this, "setLegend", leg, 1);
			} else {
				try {
					setLegend(leg);
				} catch (LegendLayerException e) {
					throw new XMLException(e);
				}
			}

			// Por compatibilidad con proyectos anteriores a la 1.0
			boolean containsIsLabeled = xml.contains("isLabeled");
			if (containsIsLabeled) {
				isLabeled = xml.getBooleanProperty("isLabeled");
			}
			// set properties for ILabelable
			XMLEntity labelingXML = xml.firstChild("labelingStrategy",
					"labelingStrategy");
			if (labelingXML != null) {
				if (!containsIsLabeled) {
					isLabeled = true;
				}
				try {
					ILabelingStrategy labeling = LabelingFactory
							.createStrategyFromXML(labelingXML, this);
					if (isJoined()) {
						PostProcessSupport.addToPostProcess(this,
								"setLabelingStrategy", labeling, 1);
					} else
						this.setLabelingStrategy(labeling);

				} catch (NotExistInXMLEntity neXMLEX) {
					// no strategy was set, just continue;
					logger.warn("Reached what should be unreachable code");
				}
			} else if (legendXML.contains("labelFieldName")
					|| legendXML.contains("labelfield")) {
				/*
				 * (jaume) begin patch; for backward compatibility purposes.
				 * Since gvSIG v1.1 labeling is no longer managed by the Legend
				 * but by the ILabelingStrategy. The following allows restoring
				 * older projects' labelings.
				 */
				String labelTextField = null;
				if (legendXML.contains("labelFieldName")) {
					labelTextField = legendXML
							.getStringProperty("labelFieldName");
					if (labelTextField != null) {
						AttrInTableLabelingStrategy labeling = new AttrInTableLabelingStrategy();
						labeling.setLayer(this);
						int unit = 1;
						boolean useFixedSize = false;
						String labelFieldHeight = legendXML
								.getStringProperty("labelHeightFieldName");
						labeling.setTextField(labelTextField);
						if (labelFieldHeight != null) {
							labeling.setHeightField(labelFieldHeight);
						} else {
							double size = -1;
							for (int i = 0; i < legendXML.getChildrenCount(); i++) {
								XMLEntity xmlChild = legendXML.getChild(i);
								if (xmlChild.contains("m_FontSize")) {
									double childFontSize = xmlChild
											.getDoubleProperty("m_FontSize");
									if (size < 0) {
										size = childFontSize;
										useFixedSize = true;
									} else {
										useFixedSize = useFixedSize
												&& (size == childFontSize);
									}
									if (xmlChild.contains("m_bUseFontSize")) {
										if (xmlChild
												.getBooleanProperty("m_bUseFontSize")) {
											unit = -1;
										} else {
											unit = 1;
										}
									}
								}
							}
							labeling.setFixedSize(size / 1.4);// Factor de
																// corrección
																// que se
																// aplicaba
																// antes en el
																// etiquetado
						}
						labeling.setUsesFixedSize(useFixedSize);
						labeling.setUnit(unit);
						labeling.setRotationField(legendXML
								.getStringProperty("labelRotationFieldName"));
						if (isJoined()) {
							PostProcessSupport.addToPostProcess(this,
									"setLabelingStrategy", labeling, 1);
						} else
							this.setLabelingStrategy(labeling);
						this.setIsLabeled(true);
					}
				} else {
					labelTextField = legendXML.getStringProperty("labelfield");
					if (labelTextField != null) {
						AttrInTableLabelingStrategy labeling = new AttrInTableLabelingStrategy();
						labeling.setLayer(this);
						int unit = 1;
						boolean useFixedSize = false;
						String labelFieldHeight = legendXML
								.getStringProperty("labelFieldHeight");
						labeling.setTextField(labelTextField);
						if (labelFieldHeight != null) {
							labeling.setHeightField(labelFieldHeight);
						} else {
							double size = -1;
							for (int i = 0; i < legendXML.getChildrenCount(); i++) {
								XMLEntity xmlChild = legendXML.getChild(i);
								if (xmlChild.contains("m_FontSize")) {
									double childFontSize = xmlChild
											.getDoubleProperty("m_FontSize");
									if (size < 0) {
										size = childFontSize;
										useFixedSize = true;
									} else {
										useFixedSize = useFixedSize
												&& (size == childFontSize);
									}
									if (xmlChild.contains("m_bUseFontSize")) {
										if (xmlChild
												.getBooleanProperty("m_bUseFontSize")) {
											unit = -1;
										} else {
											unit = 1;
										}
									}
								}
							}
							labeling.setFixedSize(size / 1.4);// Factor de
																// corrección
																// que se
																// aplicaba
																// antes en el
																// etiquetado
						}
						labeling.setUsesFixedSize(useFixedSize);
						labeling.setUnit(unit);
						labeling.setRotationField(legendXML
								.getStringProperty("labelFieldRotation"));
						if (isJoined()) {
							PostProcessSupport.addToPostProcess(this,
									"setLabelingStrategy", labeling, 1);
						} else
							this.setLabelingStrategy(labeling);
						this.setIsLabeled(true);
					}
				}

			} else if (!containsIsLabeled) {
				isLabeled = false;
			}

			// compatibility with hyperlink from 1.9 alpha version... do we
			// really need to be compatible with alpha versions??
			XMLEntity xmlLinkProperties = xml.firstChild("typeChild",
					"linkProperties");
			if (xmlLinkProperties != null) {
				try {
					String fieldName = xmlLinkProperties
							.getStringProperty("fieldName");
					xmlLinkProperties.remove("fieldName");
					String extName = xmlLinkProperties
							.getStringProperty("extName");
					xmlLinkProperties.remove("extName");
					int typeLink = xmlLinkProperties.getIntProperty("typeLink");
					xmlLinkProperties.remove("typeLink");
					if (fieldName != null) {
						setProperty("legacy.hyperlink.selectedField", fieldName);
						setProperty("legacy.hyperlink.type", new Integer(
								typeLink));
						if (extName != null) {
							setProperty("legacy.hyperlink.extension", extName);
						}
					}
				} catch (NotExistInXMLEntity ex) {
					logger.warn("Error getting old hyperlink configuration", ex);
				}
			}

		} catch (XMLException e) {
			this.setAvailable(false);
			this.orgXMLEntity = xml;
		} catch (Exception e) {
			e.printStackTrace();
			this.setAvailable(false);
			this.orgXMLEntity = xml;

		}

	}

	public void setXMLEntityNew(XMLEntity xml) throws XMLException {
		try {
			super.setXMLEntity(xml);

			XMLEntity legendXML = xml.getChild(0);
			IVectorLegend leg = LegendFactory.createFromXML(legendXML);
			/*
			 * (jaume) begin patch; for backward compatibility purposes. Since
			 * gvSIG v1.1 labeling is no longer managed by the Legend but by the
			 * ILabelingStrategy. The following allows restoring older projects'
			 * labelings.
			 */
			if (legendXML.contains("labelFieldHeight")) {
				AttrInTableLabelingStrategy labeling = new AttrInTableLabelingStrategy();
				labeling.setLayer(this);
				labeling.setTextField(legendXML
						.getStringProperty("labelFieldHeight"));
				labeling.setRotationField(legendXML
						.getStringProperty("labelFieldRotation"));
				this.setLabelingStrategy(labeling);
				this.setIsLabeled(true);
			}
			/* end patch */
			try {
				getRecordset().getSelectionSupport().setXMLEntity(
						xml.getChild(1));

				this.setLoadSelection(xml.getChild(1));
			} catch (ReadDriverException e1) {
				this.setAvailable(false);
				throw new XMLException(e1);
			}
			// Si tiene una unión, lo marcamos para que no se cree la leyenda
			// hasta
			// el final
			// de la lectura del proyecto
			if (xml.contains("hasJoin")) {
				setIsJoined(true);
				PostProcessSupport.addToPostProcess(this, "setLegend", leg, 1);
			} else {
				this.setLoadLegend(leg);
			}

		} catch (XMLException e) {
			this.setAvailable(false);
			this.orgXMLEntity = xml;
		} catch (Exception e) {
			this.setAvailable(false);
			this.orgXMLEntity = xml;
		}

	}

	/**
	 * Sobreimplementación del método toString para que las bases de datos
	 * identifiquen la capa.
	 * 
	 * @return DOCUMENT ME!
	 */
	public String toString() {
		/*
		 * Se usa internamente para que la parte de datos identifique de forma
		 * unívoca las tablas
		 */
		String ret = super.toString();

		return "layer" + ret.substring(ret.indexOf('@') + 1);
	}

	public boolean isJoined() {
		return bHasJoin;
	}

	@Override
	public boolean isSpatiallyIndexed() {
		return getISpatialIndex() != null;
	}

	public void setIsJoined(boolean hasJoin) {
		bHasJoin = hasJoin;
	}

	@Override
	public ISpatialIndex getISpatialIndex() {
		return spatialIndex;
	}

	@Override
	public void setISpatialIndex(ISpatialIndex spatialIndex) {
		this.spatialIndex = spatialIndex;
	}

	public SelectableDataSource getRecordset() throws ReadDriverException {
		// TODO geotools refactoring
		return null;
	}

	public void setEditing(boolean b) throws StartEditionLayerException {
		super.setEditing(b);
		// TODO geotools refactoring: currently not editable
		// try {
		// if (b) {
		// // TODO: Qué pasa si hay más tipos de adapters?
		// // FJP: Se podría pasar como argumento el
		// // VectorialEditableAdapter
		// // que se quiera usar para evitar meter código aquí de este
		// // estilo.
		// vea.addEditionListener(this);
		// vea.setOriginalVectorialAdapter(getSource());
		// // azo: implementations of readablevectorial need
		// // references of projection and spatial index
		// vea.setProjection(getProjection());
		// vea.setSpatialIndex(spatialIndex);
		//
		// // /vea.setSpatialIndex(getSpatialIndex());
		// // /vea.setFullExtent(getFullExtent());
		// vea.setCoordTrans(getCoordTrans());
		// vea.startEdition(EditionEvent.GRAPHIC);
		// setSource(vea);
		// getRecordset().setSelectionSupport(
		// vea.getOriginalAdapter().getRecordset()
		// .getSelectionSupport());
		//
		// } else {
		// VectorialEditableAdapter vea = (VectorialEditableAdapter)
		// getSource();
		// vea.removeEditionListener(this);
		// setSource(vea.getOriginalAdapter());
		// }
		// // Si tenemos una leyenda, hay que pegarle el cambiazo a su
		// // recordset
		// setRecordset(getSource().getRecordset());
		// if (getLegend() instanceof IVectorLegend) {
		// IVectorLegend ley = (IVectorLegend) getLegend();
		// ley.setDataSource(getSource().getRecordset());
		// ley.useDefaultSymbol(true);
		// }
		// } catch (ReadDriverException e) {
		// throw new StartEditionLayerException(getName(), e);
		// } catch (FieldNotFoundException e) {
		// throw new StartEditionLayerException(getName(), e);
		// } catch (StartWriterVisitorException e) {
		// throw new StartEditionLayerException(getName(), e);
		// }
		//
		// setSpatialCacheEnabled(b);
		// callEditionChanged(LayerEvent
		// .createEditionChangedEvent(this, "edition"));
	}

	@Override
	public void setRecordset(SelectableDataSource newSds) {
		getSelectionSupport().addSelectionListener(this);
		this.updateDrawVersion();
	}

	public void clearSpatialCache() {
		spatialCache.clearAll();
	}

	public boolean isSpatialCacheEnabled() {
		return spatialCacheEnabled;
	}

	public void setSpatialCacheEnabled(boolean spatialCacheEnabled) {
		this.spatialCacheEnabled = spatialCacheEnabled;
	}

	public SpatialCache getSpatialCache() {
		return spatialCache;
	}

	/**
	 * Siempre es un numero mayor de 1000
	 * 
	 * @param maxFeatures
	 */
	public void setMaxFeaturesInEditionCache(int maxFeatures) {
		if (maxFeatures > spatialCache.maxFeatures)
			spatialCache.setMaxFeatures(maxFeatures);

	}

	/**
	 * This method returns a boolean that is used by the FPopMenu to make
	 * visible the properties menu or not. It is visible by default, and if a
	 * later don't have to show this menu only has to override this method.
	 * 
	 * @return If the properties menu is visible (or not)
	 */
	public boolean isPropertiesMenuVisible() {
		return true;
	}

	public void reload() throws ReloadLayerException {
		if (this.isEditing()) {
			throw new ReloadLayerException(getName());
		}
		this.setAvailable(true);
		super.reload();
		this.updateDrawVersion();
		try {
			if (this.getLegend() == null) {
				if (this.getRecordset().getDriver() instanceof WithDefaultLegend) {
					WithDefaultLegend aux = (WithDefaultLegend) this
							.getRecordset().getDriver();
					this.setLegend((IVectorLegend) aux.getDefaultLegend());
					this.setLabelingStrategy(aux.getDefaultLabelingStrategy());
				} else {
					this.setLegend(LegendFactory.createSingleSymbolLegend(this
							.getShapeType()));
				}
			}

		} catch (LegendLayerException e) {
			this.setAvailable(false);
			throw new ReloadLayerException(getName(), e);
		} catch (ReadDriverException e) {
			this.setAvailable(false);
			throw new ReloadLayerException(getName(), e);
		}

	}

	protected void setLoadSelection(XMLEntity xml) {
		this.loadSelection = xml;
	}

	protected void setLoadLegend(IVectorLegend legend) {
		this.loadLegend = legend;
	}

	protected void putLoadSelection() throws XMLException {
		if (this.loadSelection == null)
			return;
		try {
			this.getRecordset().getSelectionSupport()
					.setXMLEntity(this.loadSelection);
		} catch (ReadDriverException e) {
			throw new XMLException(e);
		}
		this.loadSelection = null;

	}

	protected void putLoadLegend() throws LegendLayerException {
		if (this.loadLegend == null)
			return;
		this.setLegend(this.loadLegend);
		this.loadLegend = null;
	}

	protected void cleanLoadOptions() {
		this.loadLegend = null;
		this.loadSelection = null;
	}

	public boolean isWritable() {
		// TODO currently not writable
		return false;
	}

	public FLayer cloneLayer() throws Exception {
		GTFLyrVect clonedLayer = new GTFLyrVect(source);
		if (isJoined()) {
			clonedLayer.setIsJoined(true);
			clonedLayer.setRecordset(getRecordset());
		}
		clonedLayer.setVisible(isVisible());
		clonedLayer.setISpatialIndex(getISpatialIndex());
		clonedLayer.setName(getName());
		clonedLayer.setCrsTransform(getCrsTransform());

		clonedLayer.setLegend((IVectorLegend) getLegend().cloneLegend());

		clonedLayer.setIsLabeled(isLabeled());
		ILabelingStrategy labelingStrategy = getLabelingStrategy();
		if (labelingStrategy != null)
			clonedLayer.setLabelingStrategy(labelingStrategy);

		return clonedLayer;
	}

	public SelectionSupport getSelectionSupport() {
		// TODO geotools refactoring: currently no selection
		// try {
		// return getRecordset().getSelectionSupport();
		// } catch (ReadDriverException e) {
		// e.printStackTrace();
		// }
		return null;
	}

	protected boolean isOnePoint(AffineTransform graphicsTransform,
			ViewPort viewPort, double dpi, CartographicSupport csSym,
			IGeometry geom, int[] xyCoords) {
		return isOnePoint(graphicsTransform, viewPort, geom, xyCoords)
				&& csSym.getCartographicSize(viewPort, dpi,
						(FShape) geom.getInternalShape()) <= 1;
	}

	protected boolean isOnePoint(AffineTransform graphicsTransform,
			ViewPort viewPort, IGeometry geom, int[] xyCoords) {
		boolean onePoint = false;
		int type = geom.getGeometryType() % FShape.Z;
		if (type != FShape.POINT && type != FShape.MULTIPOINT
				&& type != FShape.NULL) {

			Rectangle2D geomBounds = geom.getBounds2D();

			double dist1Pixel = viewPort.getDist1pixel();

			onePoint = (geomBounds.getWidth() <= dist1Pixel && geomBounds
					.getHeight() <= dist1Pixel);

			if (onePoint) {
				// avoid out of range exceptions
				FPoint2D p = new FPoint2D(geomBounds.getMinX(),
						geomBounds.getMinY());
				p.transform(viewPort.getAffineTransform());
				p.transform(graphicsTransform);
				xyCoords[0] = (int) p.getX();
				xyCoords[1] = (int) p.getY();

			}

		}
		return onePoint;
	}

	/*
	 * jaume. Stuff from ILabeled.
	 */
	private boolean isLabeled;
	private ILabelingStrategy strategy;

	public boolean isLabeled() {
		return isLabeled;
	}

	public void setIsLabeled(boolean isLabeled) {
		this.isLabeled = isLabeled;
	}

	public ILabelingStrategy getLabelingStrategy() {
		return strategy;
	}

	public void setLabelingStrategy(ILabelingStrategy strategy) {
		this.strategy = strategy;
		try {
			strategy.setLayer(this);
		} catch (ReadDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void drawLabels(BufferedImage image, Graphics2D g,
			ViewPort viewPort, Cancellable cancel, double scale, double dpi)
			throws ReadDriverException {
		if (strategy != null && isWithinScale(scale)) {
			strategy.draw(image, g, viewPort, cancel, dpi);
		}
	}

	public void printLabels(Graphics2D g, ViewPort viewPort,
			Cancellable cancel, double scale,
			PrintRequestAttributeSet properties) throws ReadDriverException {
		if (strategy != null) {
			strategy.print(g, viewPort, cancel, properties);
		}
	}

	// Métodos para el uso de HyperLinks en capas FLyrVect

	@Override
	public boolean allowLinks() {
		return true;
	}

	@Override
	public AbstractLinkProperties getLinkProperties() {
		return linkProperties;
	}

	@Override
	public URI[] getLink(Point2D point, double tolerance) {
		return linkProperties.getLink(this, point, tolerance);
	}

	public void selectionChanged(SelectionEvent e) {
		this.updateDrawVersion();
	}

	public void afterFieldEditEvent(AfterFieldEditEvent e) {
		this.updateDrawVersion();
	}

	public void afterRowEditEvent(IRow feat, AfterRowEditEvent e) {
		this.updateDrawVersion();

	}

	public void beforeFieldEditEvent(BeforeFieldEditEvent e) {

	}

	public void beforeRowEditEvent(IRow feat, BeforeRowEditEvent e) {

	}

	public void processEvent(EditionEvent e) {
		if (e.getChangeType() == EditionEvent.ROW_EDITION) {
			this.updateDrawVersion();
		}

	}

	public void legendCleared(LegendClearEvent event) {
		this.updateDrawVersion();
		LegendChangedEvent e = LegendChangedEvent.createLegendChangedEvent(
				legend, legend);
		this.callLegendChanged(e);
	}

	public boolean symbolChanged(SymbolLegendEvent e) {
		this.updateDrawVersion();
		LegendChangedEvent event = LegendChangedEvent.createLegendChangedEvent(
				legend, legend);
		this.callLegendChanged(event);
		return true;
	}

	public String getTypeStringVectorLayer() throws ReadDriverException {
		try {
			String typeString = "";
			int typeShape = ((FLyrVect) this).getShapeType();
			if (FShape.MULTI == typeShape) {
				int i = 0;
				boolean isCorrect = false;
				while (getShapeCount() > i && !isCorrect) {
					IGeometry geom = getShape(i);
					if (geom == null) {
						i++;
						continue;
					}
					isCorrect = true;
					if ((geom.getGeometryType() & FShape.Z) == FShape.Z) {
						typeString = "Geometries3D";
					} else {
						typeString = "Geometries2D";
					}
				}
			} else {
				int i = 0;
				boolean isCorrect = false;
				while (getShapeCount() > i && !isCorrect) {
					IGeometry geom = getShape(i);
					if (geom == null) {
						i++;
						continue;
					}
					isCorrect = true;
					int type = geom.getGeometryType();
					if (FShape.POINT == type) {
						typeString = "Point2D";
					} else if (FShape.LINE == type) {
						typeString = "Line2D";
					} else if (FShape.POLYGON == type) {
						typeString = "Polygon2D";
					} else if (FShape.MULTIPOINT == type) {
						typeString = "MultiPint2D";
					} else if ((FShape.POINT | FShape.Z) == type) {
						typeString = "Point3D";
					} else if ((FShape.LINE | FShape.Z) == type) {
						typeString = "Line3D";
					} else if ((FShape.POLYGON | FShape.Z) == type) {
						typeString = "Polygon3D";
					} else if ((FShape.MULTIPOINT | FShape.Z) == type) {
						typeString = "MultiPoint3D";
					} else if ((FShape.POINT | FShape.M) == type) {
						typeString = "PointM";
					} else if ((FShape.LINE | FShape.M) == type) {
						typeString = "LineM";
					} else if ((FShape.POLYGON | FShape.M) == type) {
						typeString = "PolygonM";
					} else if ((FShape.MULTIPOINT | FShape.M) == type) {
						typeString = "MultiPointM";
					} else if ((FShape.MULTI | FShape.M) == type) {
						typeString = "M";
					}

				}
				return typeString;
			}
			return "";
		} catch (IOException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	public int getTypeIntVectorLayer() throws ReadDriverException {
		try {
			int typeInt = 0;
			int typeShape = ((FLyrVect) this).getShapeType();
			if (FShape.MULTI == typeShape) {
				int i = 0;
				boolean isCorrect = false;
				while (getShapeCount() > i && !isCorrect) {
					IGeometry geom = getShape(i);
					if (geom == null) {
						i++;
						continue;
					}
					isCorrect = true;
					if ((geom.getGeometryType() & FShape.Z) == FShape.Z) {
						typeInt = FShape.MULTI | FShape.Z;
					} else {
						typeInt = FShape.MULTI;
					}
				}
			} else {
				int i = 0;
				boolean isCorrect = false;
				while (getShapeCount() > i && !isCorrect) {
					IGeometry geom = getShape(i);
					if (geom == null) {
						i++;
						continue;
					}
					isCorrect = true;
					int type = geom.getGeometryType();
					typeInt = type;
				}
				return typeInt;
			}
			return typeInt;
		} catch (IOException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	private int getShapeCount() throws IOException {
		return source.getFeatures().size();
	}

	private IGeometry getShape(int index) throws IOException {
		SimpleFeatureIterator features = source.getFeatures().features();
		int i = -1;
		SimpleFeature feature = null;
		do {
			i++;
			assert features.hasNext();
			feature = features.next();
		} while (i < index);
		Geometry geom = (Geometry) feature.getDefaultGeometry();
		return ShapeFactory.createGeometry(new FLiteShape(geom));
	}

	private DriverAttributes getDriverAttributes() {
		return new DriverAttributes();
	}

	private int getFieldIndexByName(String name) {
		List<AttributeDescriptor> descriptors = source.getSchema()
				.getAttributeDescriptors();
		for (int i = 0; i < descriptors.size(); i++) {
			if (descriptors.get(i).getLocalName().equals(name)) {
				return i;
			}
		}
		return -1;
	}

	private IFeatureIterator getFeatureIterator(Rectangle2D adjustedExtent,
			String[] array, CoordinateReferenceSystem crs) throws ReadDriverException {
		try {
			return new GTFeatureIterator(adjustedExtent, array, crs);
		} catch (IOException e) {
			throw new ReadDriverException(getName(), e);
		}
	}

	private class GTFeatureIterator implements IFeatureIterator {
		private SimpleFeatureIterator delegate;

		private GTFeatureIterator(Rectangle2D extent, String[] array,
				CoordinateReferenceSystem crs) throws IOException {
			// TODO geotools refactoring: no extent/array/projection considered
			delegate = new SimpleFeatureIteratorImpl(source.getFeatures());
		}

		@Override
		public boolean hasNext() throws ReadDriverException {
			return delegate.hasNext();
		}

		@Override
		public IFeature next() throws ReadDriverException {
			SimpleFeature feature = delegate.next();
			Geometry jts = (Geometry) feature.getDefaultGeometry();
			Value[] values = new Value[feature.getAttributeCount()];
			for (int i = 0; i < values.length; i++) {
				// TODO Auto-generated method stub
			}
			FGeometry geom = ShapeFactory.createGeometry(new FLiteShape(jts));
			return new DefaultFeature(geom, values, feature.getID());
		}

		@Override
		public void closeIterator() throws ReadDriverException {
			delegate.close();
		}
	}
}
