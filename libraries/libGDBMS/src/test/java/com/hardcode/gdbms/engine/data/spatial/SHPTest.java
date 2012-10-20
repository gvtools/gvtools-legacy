package com.hardcode.gdbms.engine.data.spatial;

import java.awt.geom.PathIterator;
import java.sql.Types;

import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.SpatialDataSource;
import com.hardcode.gdbms.engine.spatial.GeneralPath;
import com.hardcode.gdbms.engine.spatial.GeometryImpl;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

/**
 *
 */
public class SHPTest extends DataSourceTestCase {
	private void testOpen(String dsName) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		d.stop();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testOpen() throws Exception {
		testOpen("shppuntos");
		testOpen("shplineas");
		testOpen("shppoligonos");
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	private void testGetFieldValue(String dsName) throws Exception {
		SpatialDataSource d = (SpatialDataSource) ds.createRandomDataSource(
				dsName, DataSourceFactory.MANUAL_OPENING);

		d.start();

		GeometryImpl myGeometry = (GeometryImpl) d.getGeometry(0);
		GeneralPath myGp = myGeometry.getGp();
		PathIterator myPathI = myGp.getPathIterator(null);
		double[] mySegment = new double[6];
		int resSegment = myPathI.currentSegment(mySegment);
		assertTrue(resSegment == PathIterator.SEG_MOVETO);
		assertTrue(mySegment[0] == 2433.98567845422);
		assertTrue(mySegment[1] == 177.104836185899);
		myPathI.next();
		resSegment = myPathI.currentSegment(mySegment);
		assertTrue(resSegment == PathIterator.SEG_LINETO);
		assertTrue(mySegment[0] == 2448.71148490583);
		assertTrue(mySegment[1] == 176.999651854102);
		myPathI.next();
		resSegment = myPathI.currentSegment(mySegment);
		assertTrue(resSegment == PathIterator.SEG_LINETO);
		assertTrue(mySegment[0] == 2449.44777522841);
		assertTrue(mySegment[1] == 152.70207120894);
		myPathI.next();
		resSegment = myPathI.currentSegment(mySegment);
		assertTrue(resSegment == PathIterator.SEG_LINETO);
		assertTrue(mySegment[0] == 2434.9323374404);
		assertTrue(mySegment[1] == 152.807255540738);
		myPathI.next();
		resSegment = myPathI.currentSegment(mySegment);
		assertTrue(resSegment == PathIterator.SEG_LINETO);
		assertTrue(mySegment[0] == 2433.98567845422);
		assertTrue(mySegment[1] == 177.104836185899);

		d.stop();
	}

	public void testGetFieldValue() throws Exception {
		testGetFieldValue("shppuntos");
		testGetFieldValue("shplineas");
		testGetFieldValue("shppoligonos");
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	private void testGetRowCount(String dsName) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		d.start();

		long myCount = d.getRowCount();
		assertTrue(myCount == 22);

		d.stop();
	}

	public void testGetRowCount() throws Exception {
		testGetRowCount("shppuntos");
		testGetRowCount("shplineas");
		testGetRowCount("shppoligonos");
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	private void testgetFieldType(String dsName) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		d.start();

		int myFieldType = d.getFieldType(0);
		assertTrue(myFieldType == Types.INTEGER);
		myFieldType = d.getFieldType(1);
		assertTrue(myFieldType == Types.INTEGER);

		d.stop();
	}

	public void testgetFieldType() throws Exception {
		testgetFieldType("shppuntos");
		testgetFieldType("shplineas");
		testgetFieldType("shppoligonos");
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	private void testNotGeomAsAField(String dsName) throws Exception {
		DataSource d = ds.createRandomDataSource(dsName,
				DataSourceFactory.MANUAL_OPENING);

		d.start();

		int fieldCount = d.getFieldCount();
		assertTrue(fieldCount == 2);

		d.stop();
	}

	public void testNotGeomAsAField() throws Exception {
		testNotGeomAsAField("shppuntos");
		testNotGeomAsAField("shplineas");
		testNotGeomAsAField("shppoligonos");
	}

	public void testJTSGeometry() throws Exception {
		SpatialDataSource d = (SpatialDataSource) ds.createRandomDataSource(
				"shppuntos", DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 3);
		Geometry g = d.getJTSGeometry(0);
		Coordinate[] coords = g.getCoordinates();
		assertTrue(coords.length == 1);
		assertTrue(coords[0].x - 44.11 < 0.01);
		assertTrue(coords[0].y - 68.63 < 0.01);
		g = d.getJTSGeometry(1);
		coords = g.getCoordinates();
		assertTrue(coords.length == 1);
		assertTrue(coords[0].x - 28.59 < 0.01);
		assertTrue(coords[0].y - 73.61 < 0.01);
		g = d.getJTSGeometry(2);
		coords = g.getCoordinates();
		assertTrue(coords.length == 1);
		assertTrue(coords[0].x - 33.07 < 0.01);
		assertTrue(coords[0].y - 61.27 < 0.01);

		d.stop();

		d = (SpatialDataSource) ds.createRandomDataSource("shplineas",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 2);
		g = d.getJTSGeometry(0);
		coords = g.getCoordinates();
		assertTrue(coords.length == 3);
		assertTrue(coords[0].x - 29.75 < 0.01);
		assertTrue(coords[0].y - 68.24 < 0.01);
		assertTrue(coords[1].x - 33.9 < 0.01);
		assertTrue(coords[1].y - 67.91 < 0.01);
		assertTrue(coords[2].x - 34.37 < 0.01);
		assertTrue(coords[2].y - 68.42 < 0.01);
		g = d.getJTSGeometry(1);
		coords = g.getCoordinates();
		assertTrue(coords.length == 3);
		assertTrue(coords[0].x - 30.72 < 0.01);
		assertTrue(coords[0].y - 72.39 < 0.01);
		assertTrue(coords[1].x - 35.31 < 0.01);
		assertTrue(coords[1].y - 72.17 < 0.01);
		assertTrue(coords[2].x - 36.79 < 0.01);
		assertTrue(coords[2].y - 67.91 < 0.01);

		d.stop();

		d = (SpatialDataSource) ds.createRandomDataSource("shppoligonos",
				DataSourceFactory.MANUAL_OPENING);

		d.start();
		assertTrue(d.getRowCount() == 2);
		g = d.getJTSGeometry(0);
		assertTrue(g instanceof Polygon);
		coords = g.getCoordinates();
		assertTrue(coords.length == 3);
		assertTrue(coords[0].x - 29.75 < 0.01);
		assertTrue(coords[0].y - 68.24 < 0.01);
		assertTrue(coords[1].x - 33.9 < 0.01);
		assertTrue(coords[1].y - 67.91 < 0.01);
		assertTrue(coords[2].x - 34.37 < 0.01);
		assertTrue(coords[2].y - 68.42 < 0.01);
		g = d.getJTSGeometry(1);
		coords = g.getCoordinates();
		assertTrue(coords.length == 3);
		assertTrue(coords[0].x - 30.72 < 0.01);
		assertTrue(coords[0].y - 72.39 < 0.01);
		assertTrue(coords[1].x - 35.31 < 0.01);
		assertTrue(coords[1].y - 72.17 < 0.01);
		assertTrue(coords[2].x - 36.79 < 0.01);
		assertTrue(coords[2].y - 67.91 < 0.01);

		d.stop();

	}
	/*
	 * public void testJTSGeometry() throws Exception {
	 * testJTSGeometry("shppuntos"); // testJTSGeometry("shplineas"); //
	 * testJTSGeometry("shppoligonos"); }
	 */
}
