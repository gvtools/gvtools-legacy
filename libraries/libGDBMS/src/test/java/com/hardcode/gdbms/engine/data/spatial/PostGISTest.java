package com.hardcode.gdbms.engine.data.spatial;

import java.awt.geom.PathIterator;
import java.sql.Types;

import com.hardcode.gdbms.DataSourceTestCase;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.SpatialDataSource;
import com.hardcode.gdbms.engine.spatial.GeneralPath;
import com.hardcode.gdbms.engine.spatial.GeometryImpl;

/**
 *
 */
public class PostGISTest extends DataSourceTestCase {
	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testOpen() throws Exception {
		DataSource d = ds.createRandomDataSource("poligonos",
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
	public void testGetFieldValue() throws Exception {
		SpatialDataSource d = (SpatialDataSource) ds.createRandomDataSource(
				"poligonos", DataSourceFactory.MANUAL_OPENING);

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

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testGetRowCount() throws Exception {
		DataSource d = ds.createRandomDataSource("poligonos",
				DataSourceFactory.MANUAL_OPENING);

		d.start();

		long myCount = d.getRowCount();
		assertTrue(myCount == 22);

		d.stop();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testgetFieldType() throws Exception {
		DataSource d = ds.createRandomDataSource("poligonos",
				DataSourceFactory.MANUAL_OPENING);

		d.start();

		int myFieldType = d.getFieldType(0);
		assertTrue(myFieldType == Types.INTEGER);
		myFieldType = d.getFieldType(1);
		assertTrue(myFieldType == Types.INTEGER);

		d.stop();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public void testNotGeomAsAField() throws Exception {
		DataSource d = ds.createRandomDataSource("poligonos",
				DataSourceFactory.MANUAL_OPENING);

		d.start();

		int fieldCount = d.getFieldCount();
		assertTrue(fieldCount == 2);

		d.stop();
	}
}
