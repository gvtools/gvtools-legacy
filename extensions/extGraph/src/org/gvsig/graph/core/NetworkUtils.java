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
package org.gvsig.graph.core;

import java.awt.Color;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.gvsig.exceptions.BaseException;
import org.gvsig.graph.solvers.Route;

import com.hardcode.gdbms.driver.exceptions.InitializeDriverException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.layers.LegendLayerException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.ViewPort;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.styles.ArrowDecoratorStyle;
import com.iver.cit.gvsig.fmap.core.styles.ILineStyle;
import com.iver.cit.gvsig.fmap.core.styles.SimpleLineStyle;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleLineSymbol;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.core.v02.FConverter;
import com.iver.cit.gvsig.fmap.core.v02.FSymbol;
import com.iver.cit.gvsig.fmap.drivers.BoundedShapes;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.GraphicLayer;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.rendering.FGraphic;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.spatialindex.ISpatialIndex;
import com.iver.cit.gvsig.fmap.spatialindex.QuadtreeJts;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.xmlEntity.generate.XmlTag;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.CoordinateSequences;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineSegment;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class NetworkUtils {
	static int idSymbolFlag = -1;

	static Logger logger = Logger.getLogger(NetworkUtils.class);

	static private GeometryFactory geomFactory = new GeometryFactory();

	private static IMarkerSymbol simFlag2;
	private static FSymbol simFlag;

	public static void clearBarriersFromGraphics(MapControl mc) {
		GraphicLayer graphics = mc.getMapContext().getGraphicsLayer();
		for (int i = graphics.getNumGraphics() - 1; i >= 0; i--) {
			FGraphic aux = graphics.getGraphic(i);
			if (aux.getTag() != null)
				if (aux.getTag().equalsIgnoreCase("BARRIER"))
					graphics.removeGraphic(aux);
		}
	}

	public static void clearFlagsFromGraphics(MapControl mc) {
		GraphicLayer graphics = mc.getMapContext().getGraphicsLayer();
		for (int i = graphics.getNumGraphics() - 1; i >= 0; i--) {
			FGraphic aux = graphics.getGraphic(i);
			if (aux.getTag() != null)
				if (aux.getTag().equalsIgnoreCase("FLAG"))
					graphics.removeGraphic(aux);
		}
	}

	public static void clearFlagFromGraphics(MapControl mc, GvFlag flag) {
		GraphicLayer graphics = mc.getMapContext().getGraphicsLayer();
		FGraphic graphic = graphics.getGraphicByObjectTag(flag);
		graphics.removeGraphic(graphic);
	}

	public static void clearGraphicByObjectTag(MapControl mc, Object obj) {
		GraphicLayer graphics = mc.getMapContext().getGraphicsLayer();
		FGraphic graphic = graphics.getGraphicByObjectTag(obj);
		graphics.removeGraphic(graphic);
	}

	public static void clearRouteFromGraphics(MapControl mc) {
		GraphicLayer graphics = mc.getMapContext().getGraphicsLayer();
		for (int i = graphics.getNumGraphics() - 1; i >= 0; i--) {
			FGraphic aux = graphics.getGraphic(i);
			if (aux.getTag() != null)
				if (aux.getTag().equalsIgnoreCase("ROUTE"))
					graphics.removeGraphic(aux);
		}
	}

	public static void centerGraphicsOnFlags(MapControl mc, GvFlag[] flags) {
		ViewPort vp = mc.getViewPort();
		Rectangle2D extent = vp.getAdjustedExtent();
		/*
		 * double xNC = flag.getOriginalPoint().getX(); double yNC =
		 * flag.getOriginalPoint().getY();
		 */
		double width = extent.getWidth();
		double height = extent.getHeight();

		Rectangle2D.Double r = null;// new Rectangle2D.Double();

		/*
		 * r.width = width; r.height = height; r.x = xNC - width/2; r.y = yNC -
		 * height/2;
		 */

		for (int i = 0; i < flags.length; i++) {
			if (flags[i] != null) {
				Point2D p = flags[i].getOriginalPoint();
				if (r == null)
					r = new Rectangle2D.Double(p.getX(), p.getY(), 0, 0);
				else
					r.add(flags[i].getOriginalPoint());
			}
		}

		double realTol = mc.getViewPort().toMapDistance(15);

		r.height = r.height + realTol;
		r.width = r.width + realTol;
		r.x = r.x - (realTol / 2.0);// r.getMaxX() - r.width/2;
		r.y = r.y - (realTol / 2.0);// r.getMaxY() - r.height/2;

		vp.setExtent(r);
		mc.getMapContext().invalidate();
	}

	public static void centerGraphicsOnFlag(MapControl mc, GvFlag flag) {
		ViewPort vp = mc.getViewPort();
		Rectangle2D extent = vp.getAdjustedExtent();
		double xNC = flag.getOriginalPoint().getX();
		double yNC = flag.getOriginalPoint().getY();
		double width = extent.getWidth();
		double height = extent.getHeight();

		Rectangle2D.Double r = new Rectangle2D.Double();

		r.width = width;
		r.height = height;
		r.x = xNC - width / 2;
		r.y = yNC - height / 2;

		vp.setExtent(r);
	}

	public static void drawRouteOnGraphics(MapControl mc, Route route) {
		Iterator it = route.getFeatureList().iterator();
		int idSymbolLine = -1;
		GraphicLayer graphicLayer = mc.getMapContext().getGraphicsLayer();
		// if (idSymbolLine == -1)
		{
			SimpleLineSymbol arrowSymbol = new SimpleLineSymbol();
			// FSymbol arrowSymbol = new FSymbol(FConstant.SYMBOL_TYPE_LINE);
			arrowSymbol.setLineColor(Color.RED);
			arrowSymbol.setUnit(-1); // pixels
			arrowSymbol.setLineWidth(3.0f);
			ILineStyle lineStyle = new SimpleLineStyle();

			ArrowDecoratorStyle arrowDecoratorStyle = new ArrowDecoratorStyle();
			arrowDecoratorStyle.setArrowMarkerCount(1);
			lineStyle.setArrowDecorator(arrowDecoratorStyle);
			lineStyle.setLineWidth(3.0f);
			arrowSymbol.setLineStyle(lineStyle);
			idSymbolLine = graphicLayer.addSymbol(arrowSymbol);

		}
		// Para evitar hacer reallocate de los elementos de la
		// graphicList cada vez, creamos primero la lista
		// y la insertamos toda de una vez.
		ArrayList graphicsRoute = new ArrayList();
		while (it.hasNext()) {
			IFeature feat = (IFeature) it.next();
			IGeometry gAux = feat.getGeometry();
			FGraphic graphic = new FGraphic(gAux, idSymbolLine);
			graphic.setTag("ROUTE");
			graphicsRoute.add(graphic);
			// graphicLayer.insertGraphic(0, graphic);
		}
		// Lo insertamos al principio de la lista para que los
		// pushpins se dibujen después.

		graphicLayer.inserGraphics(0, graphicsRoute);
		mc.drawGraphics();
	}

	public static File getNetworkFile(FLayer lyr) {
		String directoryName = System.getProperty("java.io.tmpdir");
		String aux = lyr.getName().replaceAll("\\Q.shp\\E", ".net");
		File newFile = new File(directoryName + File.separator + aux);
		return newFile;

	}

	public static void addGraphicFlag(MapControl mapControl, GvFlag flag) {
		addGraphicFlag(mapControl, flag, getDefaultSymbolFlag());
	}

	/**
	 * @return
	 */
	public static ISymbol getDefaultSymbolFlag() {
		if (simFlag == null) {

			simFlag = new FSymbol(FConstant.SYMBOL_TYPE_ICON);
			ImageIcon icon = new ImageIcon(NetworkUtils.class.getClassLoader()
					.getResource("images/pushpin.png"));
			simFlag.setIcon(icon.getImage());
			simFlag.setSize(24);
			simFlag.setSizeInPixels(true);
			simFlag.setStyle(FConstant.SYMBOL_STYLE_MARKER_IMAGEN);
		}
		return simFlag;
	}

	public static void addGraphicFlag(MapControl mapControl, GvFlag flag,
			ISymbol sym) {
		GraphicLayer graphicLayer = mapControl.getMapContext()
				.getGraphicsLayer();
		int idSymbol = graphicLayer.getSymbol(sym);
		if (idSymbol == -1) { // El símbolo no existe todavía en la lista de
			// gráficos
			idSymbol = graphicLayer.addSymbol(sym);
		}
		IGeometry gAux = ShapeFactory.createPoint2D(flag.getOriginalPoint()
				.getX(), flag.getOriginalPoint().getY());
		FGraphic graphic = new FGraphic(gAux, idSymbol);
		graphic.setTag("FLAG");
		graphic.setObjectTag(flag);
		graphicLayer.addGraphic(graphic);

	}

	public static double getPercentAlong(Geometry jtsGeom, double x, double y) {
		// Primero calculamos la longitud total del shape.
		// Luego calculamos el punto más cercano y su distancia para cada
		// segmento del shape.
		// Nos quedamos con el que esté más cerca y luego recorremos hasta él
		// acumulando distancia.
		// Finalmente, dividimos esa distancia por la longitud total.

		Coordinate[] coords = jtsGeom.getCoordinates();

		Coordinate userCoord = new Coordinate(x, y);

		double longReal = 0;
		// Le pegamos una primera pasada para saber su longitud real.
		// OJO, NO TRABAJAMOS CON SHAPES MULTIPARTE, NO TIENE SENTIDO CON LAS
		// REDES (CREO)
		// POR ESO SUPONEMOS UNA ÚNICA PARTE (LÍNEA CONTÍNUA)
		// A la vez calculamos el punto más cercano y su distancia para cada
		// segmento.
		double minDist = Double.MAX_VALUE;
		double distTo = 0;
		double dist = 0;
		Coordinate cOrig = null;
		Coordinate closestPoint = null;
		for (int j = 0; j < coords.length - 1; j++) {
			Coordinate c1 = coords[j];
			Coordinate c2 = coords[j + 1];
			LineSegment line = new LineSegment(c1, c2);

			Coordinate auxPoint = line.closestPoint(userCoord);
			dist = userCoord.distance(auxPoint);
			if ((dist < minDist)) {
				minDist = dist;
				cOrig = c1;
				closestPoint = auxPoint;
				distTo = longReal;
			}
			longReal += line.getLength();
		}
		dist = cOrig.distance(closestPoint);
		double longBuscada = distTo + dist;

		double pct;
		if (longReal > 0)
			pct = longBuscada / longReal;
		else
			pct = 0.0;

		return pct;
	}

	/**
	 * Receives jtsgeom, percentage of desired linestring and direction.
	 * 
	 * @param geom
	 * @param pct
	 * @param direction
	 *            1=> same as geometry. 0=> Inversed
	 * @return partial linestring
	 */
	public static LineString getPartialLineString(Geometry geom, double pct,
			int direction)
	// Si parte vale cero, los válidos son los primeros. Si no, los segundos.
	{
		int j, numVertices;
		double longAcum, longReal, longBuscada, distSobre, miniPorcentaje;
		double nuevaX, nuevaY; // Por cuestiones de claridad al programar
		double dist = 0;

		longAcum = 0;
		longReal = geom.getLength();
		longBuscada = longReal * pct;
		Coordinate[] coords = geom.getCoordinates();
		Coordinate c1 = null, c2 = null;
		ArrayList savedCoords = new ArrayList();
		Coordinate lastCoord = null;

		if (direction > 0) // Hemos entrado por el 1 hacia el 2 (al 2 no
		// llegamos)
		{
			for (j = 0; j < coords.length - 1; j++) {
				c1 = coords[j];
				c2 = coords[j + 1];
				dist = c1.distance(c2);
				longAcum += dist;
				if ((lastCoord == null) || (!c1.equals2D(lastCoord))) {
					savedCoords.add(c1);
					lastCoord = c1;
				}

				if (longAcum >= longBuscada) {
					// Hasta aquí. Ahora ahi que poner el punto sobre el tramo
					distSobre = dist - (longAcum - longBuscada);
					miniPorcentaje = distSobre / dist;

					nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
					nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

					savedCoords.add(new Coordinate(nuevaX, nuevaY));
					break;
				} // if longAcum >= longBuscada
			} // for j

		} else // Hemos entrado por el 2 hacia el 1
		{
			numVertices = 0;
			for (j = 0; j < coords.length; j++) {
				// //////////////////////////////////////////////////////////////
				// 13_ene_2005: Si el último punto es el último punto no
				// podemos acceder al elemento j+1 porque nos salimos del shape
				// ///////////////////////////////////////////////////////////////
				c1 = coords[j];
				if (j < coords.length - 1) {
					c2 = coords[j + 1];

					dist = c1.distance(c2);
					longAcum += dist;
				}

				if (longAcum >= longBuscada) {
					// Hasta aquí. Empezamos a meter puntos

					if (numVertices == 0) {
						distSobre = dist - (longAcum - longBuscada);
						miniPorcentaje = distSobre / dist;
						nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
						nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

						savedCoords.add(new Coordinate(nuevaX, nuevaY));
						savedCoords.add(c2);
					} else {
						if ((lastCoord == null) || (!c2.equals2D(lastCoord))) {
							savedCoords.add(c2);
							lastCoord = c2;
						}

					}
					numVertices++;
					// break;
				} // if longAcum >= longBuscada
			} // for j

			// savedCoords.add(c2);

		} // if else

		return geomFactory.createLineString((Coordinate[]) savedCoords
				.toArray(new Coordinate[0]));
	}

	/**
	 * If coordList != null, it will be used to return all coordinates. Useful
	 * to avoid two iterations, and only if you linestring is single part. If
	 * you don't need it, simply set coordList = null
	 * 
	 * @param geom
	 * @param coordList
	 * @return
	 */
	public static double getLength(IGeometry geom,
			ArrayList<Coordinate> coordList) {
		double partlength = 0;
		PathIterator pi = geom.getPathIterator(null, FConverter.FLATNESS);
		double[] theData = new double[6];
		double totalLength = 0;
		Coordinate c1 = null;
		Coordinate c2 = null;
		Coordinate first = null;
		while (!pi.isDone()) {
			// while not done
			int type = pi.currentSegment(theData);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				// coordList = new CoordinateList();
				// listOfParts.add(coordList);
				totalLength += partlength;
				partlength = 0;
				c1 = new Coordinate(theData[0], theData[1]);
				first = c1;
				if (coordList != null)
					coordList.add(c1);
				break;
			case PathIterator.SEG_LINETO:
				c2 = new Coordinate(theData[0], theData[1]);
				if (coordList != null)
					coordList.add(c2);
				partlength += c2.distance(c1);
				c1 = c2;
				break;

			case PathIterator.SEG_CLOSE:
				if (coordList != null)
					coordList.add(first);
				partlength += c1.distance(first);
				break;

			}
			pi.next();
		}
		totalLength += partlength;
		return totalLength;
	}

	public static IGeometry flipGeometry(IGeometry geom) {
		GeneralPathX gp = new GeneralPathX();
		PathIterator pi = geom.getPathIterator(null, FConverter.FLATNESS);
		double[] theData = new double[6];
		Coordinate first = null;
		CoordinateList coordList = new CoordinateList();
		Coordinate c1;
		GeneralPathX newGp = new GeneralPathX();
		ArrayList listOfParts = new ArrayList();
		while (!pi.isDone()) {
			// while not done
			int type = pi.currentSegment(theData);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				coordList = new CoordinateList();
				listOfParts.add(coordList);
				c1 = new Coordinate(theData[0], theData[1]);
				coordList.add(c1, true);
				break;
			case PathIterator.SEG_LINETO:
				c1 = new Coordinate(theData[0], theData[1]);
				coordList.add(c1, true);
				break;

			case PathIterator.SEG_CLOSE:
				coordList.add(coordList.getCoordinate(0));
				break;

			}
			pi.next();
		}

		for (int i = listOfParts.size() - 1; i >= 0; i--) {
			coordList = (CoordinateList) listOfParts.get(i);
			Coordinate[] coords = coordList.toCoordinateArray();
			CoordinateArraySequence seq = new CoordinateArraySequence(coords);
			CoordinateSequences.reverse(seq);
			coords = seq.toCoordinateArray();
			newGp.moveTo(coords[0].x, coords[0].y);
			for (int j = 1; j < coords.length; j++) {
				newGp.lineTo(coords[j].x, coords[j].y);
			}
		}

		return ShapeFactory.createPolyline2D(newGp);
	}

	/**
	 * Receives IGeometry, percentage of desired linestring and direction. Used
	 * in populateRoute (at least) in order to speed up populate route
	 * 
	 * @param geom
	 * @param pct
	 * @param direction
	 *            1=> same as geometry. 0=> Inversed
	 * @return partial linestring
	 */
	public static IGeometry getPartialLineString(IGeometry geom, double pct,
			int direction)
	// Si parte vale cero, los válidos son los primeros. Si no, los segundos.
	{
		int j, numVertices;
		double longAcum, longReal, longBuscada, distSobre, miniPorcentaje;
		double nuevaX, nuevaY; // Por cuestiones de claridad al programar
		double dist = 0;

		longAcum = 0;
		ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
		longReal = getLength(geom, coords);
		longBuscada = longReal * pct;
		// Coordinate[] coords = geom.getCoordinates();
		Coordinate c1 = null, c2 = null;
		// ArrayList savedCoords = new ArrayList();
		GeneralPathX gpx = new GeneralPathX();
		Coordinate lastCoord = null;

		if (direction > 0) // Hemos entrado por el 1 hacia el 2 (al 2 no
		// llegamos)
		{
			numVertices = 0;
			for (j = 0; j < coords.size() - 1; j++) {
				c1 = coords.get(j);
				c2 = coords.get(j + 1);
				dist = c1.distance(c2);
				longAcum += dist;
				if ((lastCoord == null) || (!c1.equals2D(lastCoord))) {
					// savedCoords.add(c1);
					if (numVertices == 0)
						gpx.moveTo(c1.x, c1.y);
					else
						gpx.lineTo(c1.x, c1.y);
					numVertices++;
					lastCoord = c1;
				}

				if (longAcum >= longBuscada) {
					// Hasta aquí. Ahora ahi que poner el punto sobre el tramo
					distSobre = dist - (longAcum - longBuscada);
					miniPorcentaje = distSobre / dist;

					nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
					nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

					// savedCoords.add(new Coordinate(nuevaX, nuevaY));
					gpx.lineTo(nuevaX, nuevaY);
					numVertices++;
					break;
				} // if longAcum >= longBuscada
			} // for j

		} else // Hemos entrado por el 2 hacia el 1
		{
			numVertices = 0;
			for (j = 0; j < coords.size(); j++) {
				// //////////////////////////////////////////////////////////////
				// 13_ene_2005: Si el último punto es el último punto no
				// podemos acceder al elemento j+1 porque nos salimos del shape
				// ///////////////////////////////////////////////////////////////
				c1 = coords.get(j);
				if (j < coords.size() - 1) {
					c2 = coords.get(j + 1);

					dist = c1.distance(c2);
					longAcum += dist;
				}

				if (longAcum >= longBuscada) {
					// Hasta aquí. Empezamos a meter puntos

					if (numVertices == 0) {
						distSobre = dist - (longAcum - longBuscada);
						miniPorcentaje = distSobre / dist;
						nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
						nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

						// savedCoords.add(new Coordinate(nuevaX, nuevaY));
						// savedCoords.add(c2);
						gpx.moveTo(nuevaX, nuevaY);
						gpx.lineTo(c2.x, c2.y);
					} else {
						if ((lastCoord == null) || (!c2.equals2D(lastCoord))) {
							// savedCoords.add(c2);
							gpx.lineTo(c2.x, c2.y);
							lastCoord = c2;
						}

					}
					numVertices++;
					// break;
				} // if longAcum >= longBuscada
			} // for j

			// savedCoords.add(c2);

		} // if else

		return ShapeFactory.createPolyline2D(gpx);
	}

	/**
	 * Retrieves a sub-linestring from pct1 and length = pct2
	 * 
	 * @param geom
	 * @param pct1
	 *            => from pct
	 * @param pct2
	 *            => distance (in percentage) of second point
	 * @param direction
	 *            1=> same as geometry. 0=> Inversed
	 * @return partial linestring
	 */
	public static LineString getPartialLineString(Geometry geom, double pct1,
			double pct2, int direction)
	// Si parte vale cero, los válidos son los primeros. Si no, los segundos.
	{
		int j, numVertices;
		double longAcum, longReal, longFrom, longTo, distSobre, miniPorcentaje;
		double nuevaX, nuevaY; // Por cuestiones de claridad al programar
		double dist = 0;

		longAcum = 0;
		longReal = geom.getLength();
		longFrom = longReal * pct1;

		Coordinate[] coords = geom.getCoordinates();
		Coordinate c1 = null, c2 = null;
		ArrayList savedCoords = new ArrayList();
		Coordinate lastCoord = null;

		if (direction > 0) // Hemos entrado por el 1 hacia el 2 (al 2 no
		// llegamos)
		{
			longTo = longReal * (pct1 + pct2);
			numVertices = 0;
			for (j = 0; j < coords.length - 1; j++) {
				c1 = coords[j];
				c2 = coords[j + 1];
				dist = c1.distance(c2);
				longAcum += dist;
				if ((lastCoord == null) || (!c1.equals2D(lastCoord))) {
					if (longAcum >= longFrom) {
						if (numVertices == 0) {
							distSobre = dist - (longAcum - longFrom);
							miniPorcentaje = distSobre / dist;
							nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
							nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

							savedCoords.add(new Coordinate(nuevaX, nuevaY));
						} else {
							savedCoords.add(c1);
						}
						numVertices++;
					}
					lastCoord = c1;
				}

				if (longAcum >= longTo) {
					// Hasta aquí. Ahora ahi que poner el punto sobre el tramo
					distSobre = dist - (longAcum - longTo);
					miniPorcentaje = distSobre / dist;

					nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
					nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

					savedCoords.add(new Coordinate(nuevaX, nuevaY));
					break;
				} // if longAcum >= longBuscada
			} // for j

		} else // Hemos entrado por el 2 hacia el 1
		{
			numVertices = 0;

			longTo = longFrom;
			longFrom = longFrom - (longReal * pct2);

			for (j = 0; j < coords.length; j++) {
				// //////////////////////////////////////////////////////////////
				// 13_ene_2005: Si el último punto es el último punto no
				// podemos acceder al elemento j+1 porque nos salimos del shape
				// ///////////////////////////////////////////////////////////////
				c1 = coords[j];
				if (j < coords.length - 1) {
					c2 = coords[j + 1];

					dist = c1.distance(c2);
					longAcum += dist;
				}

				if (longAcum >= longFrom) {
					// Desde aquí. Empezamos a meter puntos

					if (numVertices == 0) {
						distSobre = dist - (longAcum - longFrom);
						miniPorcentaje = distSobre / dist;
						nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
						nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

						savedCoords.add(new Coordinate(nuevaX, nuevaY));
						savedCoords.add(c2);
					} else {
						if ((lastCoord == null) || (!c2.equals2D(lastCoord))) {
							if (longAcum <= longTo)
								savedCoords.add(c2);
							lastCoord = c2;
						}

					}
					numVertices++;
					if (longAcum >= longTo) {
						// Hasta aquí. Ahora ahi que poner el punto sobre el
						// tramo
						distSobre = dist - (longAcum - longTo);
						miniPorcentaje = distSobre / dist;

						nuevaX = c1.x + (c2.x - c1.x) * miniPorcentaje;
						nuevaY = c1.y + (c2.y - c1.y) * miniPorcentaje;

						savedCoords.add(new Coordinate(nuevaX, nuevaY));
						break;
					} // if longAcum >= longTo

				} // if longAcum >= longFrom
			} // for j

			// savedCoords.add(c2);

		} // if else

		return geomFactory.createLineString((Coordinate[]) savedCoords
				.toArray(new Coordinate[0]));
	}

	public static double[] string2doubleArray(String str, String separator)
			throws NumberFormatException {
		str = str.replace(" ", "");

		if (!str.contains(separator)) {
			try {
				double[] result = new double[1];
				result[0] = Double.parseDouble(str);
				return result;
			} catch (NumberFormatException except) {
				if (str.startsWith("[") && str.endsWith("]")) {
					String[] tokens = str.substring(1, str.length() - 1).split(
							":");
					if (tokens.length == 3) {
						try {
							Double ini = Double.valueOf(tokens[0]);
							Double end = Double.valueOf(tokens[1]);
							Double interval = Double.valueOf(tokens[2]);

							int length = ((int) ((end - ini) / interval)) + 1;
							if (length > 0) {
								double[] result = new double[length];

								for (int i = 0; i < result.length; i++) {
									result[i] = ini;
									ini += interval;
								}

								return result;
							} else {
								return new double[0];
							}
						} catch (NumberFormatException except2) {
							throw except2;
						}
					} else {
						throw except;
					}
				} else {
					throw except;
				}
			}
		} else {
			String[] parts;
			parts = str.split(separator);
			ArrayList ret = new ArrayList();

			for (int i = 0; i < parts.length; i++) {
				try {
					ret.add(Double.valueOf(parts[i]));
				} catch (NumberFormatException except) {
					if (parts[i].startsWith("[") && parts[i].endsWith("]")) {
						String[] tokens = parts[i].substring(1,
								parts[i].length() - 1).split(":");
						if (tokens.length == 3) {
							try {
								Double ini = Double.valueOf(tokens[0]);
								Double end = Double.valueOf(tokens[1]);
								Double interval = Double.valueOf(tokens[2]);

								for (double j = ini; j <= end; j += interval) {
									ret.add(Double.valueOf(j));
								}
							} catch (NumberFormatException except2) {
								throw except2;
							}
						}
					} else {
						throw except;
					}
				}
			}

			double[] result = new double[ret.size()];
			for (int i = 0; i < result.length; i++) {
				result[i] = ((Double) ret.get(i)).doubleValue();
			}

			return result;
		}
	}

	/**
	 * Truco sucio para evitar la inquisición. Cuando funcione, habrá que hacer
	 * una propuesta para ver si es aceptado....
	 * 
	 * @param mapCtrl
	 * @param x
	 * @param y
	 */
	public static void flashPoint(MapControl mapCtrl, double x, double y) {
		flashPoint(mapCtrl, Color.RED, 5, x, y);
	}

	/**
	 * @param mapCtrl
	 * @param color
	 * @param x
	 * @param y
	 */
	public static void flashPoint(MapControl mapCtrl, Color color,
			int maxCount, double x, double y) {
		int delay = 100; // milliseconds
		MyTask task = new MyTask(mapCtrl, color, maxCount, x, y);

		java.util.Timer timer = new java.util.Timer();
		timer.schedule(task, delay, 60);

	}

	public static GvFlag[] putFlagsOnNetwork(FLyrVect layer, Network net,
			double tolerance) throws BaseException {
		ReadableVectorial reader = layer.getSource();
		reader.start();
		IFeatureIterator it = reader.getFeatureIterator();
		int i = 0;
		ArrayList<GvFlag> flags = new ArrayList();
		while (it.hasNext()) {
			IFeature feat = it.next();
			Geometry geo = feat.getGeometry().toJTSGeometry();
			if (!((geo instanceof Point) || (geo instanceof MultiPoint)))
				continue;

			Coordinate[] coords = geo.getCoordinates();
			if (coords.length > 1) {
				logger.warn("The record " + i + " has " + coords.length
						+ "coordinates. Pay attention!!");
				logger.warn("Only one point will be used.");
			}
			for (int j = 0; j < coords.length; j++) {
				GvFlag flag = net.addFlag(coords[j].x, coords[j].y, tolerance);
				if (flag == null) {
					GraphException e = new GraphException("Punto " + i
							+ " fuera de la red. Tolerancia=" + tolerance);
					e.setCode(GraphException.FLAG_OUT_NETWORK);
					throw e;
					// NotificationManager.addError("No se puedo situar el
					// registro " + i +
					// " Por favor, compruebe que está encima de la red o
					// aumente la tolerancia.",
					// e);
				}
				System.out.println("Situando flag " + i + " de la capa "
						+ layer.getName() + " en idArc=" + flag.getIdArc());
				flags.add(flag);
				flag.getProperties().put("rec", new Integer(i));
				break;
			}
			i++;
		}
		it.closeIterator();
		reader.stop();
		return flags.toArray(new GvFlag[0]);
	}

	/**
	 * Parche hasta corregir QuadtreGT2 y createSpatialIndex (lo de menos de
	 * 65.000 niveles)
	 * 
	 * @param lyrVect
	 * @throws ReadDriverException
	 * @throws InitializeDriverException
	 */
	public static ISpatialIndex createJtsQuadtree(FLyrVect lyrVect)
			throws InitializeDriverException, ReadDriverException {
		ReadableVectorial va = lyrVect.getSource();
		if (!(va.getDriver() instanceof BoundedShapes))
			return null;
		va.start();
		QuadtreeJts spatialIndex = new QuadtreeJts();
		BoundedShapes shapeBounds = (BoundedShapes) va.getDriver();
		int to = va.getShapeCount();
		for (int i = 0; i < to; i++) {
			Rectangle2D r = shapeBounds.getShapeBounds(i);
			if (r != null)
				spatialIndex.insert(r, i);
			if ((i % 100000) == 0)
				System.out.println("Inserting  " + i + " bounding of " + to);

		} // for
		va.stop();
		// vectorial adapter needs a reference to the spatial index, to
		// solve
		// request for feature iteration based in spatial queries
		va.setSpatialIndex(spatialIndex);
		return spatialIndex;
	}

	public static void loadLegend(FLyrVect lyr, String gvlPath) {
		File xmlFile = new File(gvlPath);

		FileReader reader = null;

		try {
			reader = new FileReader(xmlFile);

			XmlTag tag = (XmlTag) XmlTag.unmarshal(reader);
			IVectorLegend myLegend = LegendFactory.createFromXML(new XMLEntity(
					tag));

			if (myLegend != null) {
				lyr.setLegend(myLegend);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MarshalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LegendLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
