package com.iver.cit.gvsig.fmap.layers;

import java.awt.geom.Rectangle2D;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;

public interface ISpatialDB extends ReadableVectorial {

	/**
	 * @return devuelve la Conexión a la base de datos, para que
	 * el usuario pueda hacer la consulta que quiera, si lo desea.
	 * Por ejemplo, esto puede ser útil para abrir un cuadro de dialogo
	 * avanazado y lanzar peticiones del tipo "Devuelveme un buffer
	 * a las autopistas", y con el resultset que te venga, escribir
	 * un shape, o cosas así.
	 * @throws ReadDriverException TODO
	 */
	/* public Connection getConnection()
	 {
	 return ((VectorialDatabaseDriver)driver).getConnection();
	 }*/
	/* public IFeatureIterator getFeatureIterator(String sql) throws DriverException
	 {
	 return ((VectorialDatabaseDriver)driver).getFeatureIterator(sql);
	 }*/
	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG)
			throws ReadDriverException;

	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG,
			String[] alphaNumericFieldsNeeded) throws ReadDriverException;

	public DBLayerDefinition getLyrDef();

	public int getRowIndexByFID(IFeature feat);

}