package es.prodevelop.cit.gvsig.jdbc_spatial;

import org.apache.log4j.Logger;

import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.SourceInfo;
import com.hardcode.gdbms.engine.data.file.FileSourceInfo;
import com.iver.cit.gvsig.fmap.drivers.dbf.DBFDriver;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;

import es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle.OracleSpatialDriver;

public class OraEpsgTableLoader {

	private static Logger logger = Logger.getLogger(OraEpsgTableLoader.class
			.getName());

	public boolean createOracleEpsgTable() {

		SourceInfo si = LayerFactory.getDataSourceFactory().getDriverInfo(
				OracleSpatialDriver.ORACLE_EPSG_TABLE_NAME);

		// Create 'oracle codes - epsg codes' table
		DBFDriver dbfdrv = new DBFDriver();

		// dbfdrv.setDataSourceFactory()
		UtilityFileDataSourceAdapter fdsa = new UtilityFileDataSourceAdapter();

		fdsa.setDriver(dbfdrv);

		// ---------------------------------------------
		FileSourceInfo fsi = new FileSourceInfo();
		fsi.file = createFileString("dbf",
				OracleSpatialDriver.ORACLE_EPSG_FILE_NAME);
		fsi.spatial = false;
		fsi.name = OracleSpatialDriver.ORACLE_EPSG_TABLE_NAME;
		fsi.driverName = dbfdrv.getName();

		fdsa.setSourceInfo(fsi);

		SelectableDataSource sds = null;
		EditableAdapter ea = new EditableAdapter();
		ProjectTable pt = null;

		try {
			sds = new SelectableDataSource(fdsa);
			ea.setOriginalDataSource(sds);
			pt = ProjectTableFactory.createTable(
					OracleSpatialDriver.ORACLE_EPSG_TABLE_NAME, ea);
		} catch (Exception ex) {
			logger.error("While creating datasource: " + ex.getMessage());
			return false;
		}

		sds.setSourceInfo(fsi);

		DataSourceFactory dsf = LayerFactory.getDataSourceFactory();
		dsf.addFileDataSource(fsi.driverName, fsi.name, fsi.file);
		sds.setDataSourceFactory(dsf);

		return true;
	}

	private String createFileString(String folder, String _filename) {

		String filename = _filename.toUpperCase();

		java.net.URL dbf_url;

		try {
			dbf_url = createResourceUrl(folder + "/" + filename);
			if (dbf_url == null) {
				dbf_url = createResourceUrl(folder + "/"
						+ filename.toLowerCase());
			}
		} catch (Throwable th) {
			logger.error("Unable to find resource: " + _filename);
			return folder + "/" + _filename;
		}
		return dbf_url.getFile();
	}

	private java.net.URL createResourceUrl(String path) {
		return getClass().getClassLoader().getResource(path);
	}
}
