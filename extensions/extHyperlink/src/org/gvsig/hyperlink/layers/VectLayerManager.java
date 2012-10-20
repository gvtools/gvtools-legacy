package org.gvsig.hyperlink.layers;

import java.awt.geom.Point2D;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;

public class VectLayerManager implements ILinkLayerManager {
	private FLyrVect _layer = null;

	public URI[] getLink(Point2D point, double tolerance, String fieldName,
			String fileExtension) {
		FLyrVect lyrVect = (FLyrVect) _layer;
		FBitSet newBitSet;
		BitSet bitset;
		ArrayList uriList;

		// Construimos el BitSet (Véctor con componentes BOOLEAN) con la
		// consulta que
		// hacemos a la capa.

		try {
			newBitSet = lyrVect.queryByPoint(point, tolerance);
			bitset = newBitSet;
		} catch (ReadDriverException e1) {
			PluginServices.getLogger().error(e1);
			return null;
		} catch (VisitorException e1) {
			PluginServices.getLogger().error(e1);
			return null;
		}

		// Si el bitset creado no está vacío creamos el vector de URLS
		// correspondientes
		// a la consulta que hemos hecho.

		if (bitset != null) {
			try {
				if (lyrVect instanceof AlphanumericData) {

					DataSource ds = ((AlphanumericData) lyrVect).getRecordset();
					ds.start();
					// boolean exist=false;
					int idField;
					// Creo el vector de URL´s con la misma longitud que el
					// bitset
					uriList = new ArrayList();

					// Consigo el identificador del campo pasandole como
					// parámetro el
					// nombre del campo del énlace
					idField = ds.getFieldIndexByName(fieldName);
					if (idField != -1) {
						// Recorremos el BitSet siguiendo el ejmplo de la clase
						// que se
						// proporciona en la API
						int i = 0;
						for (int j = bitset.nextSetBit(0); j >= 0; j = bitset
								.nextSetBit(j + 1)) {
							// Creamos el fichero con el nombre del campo y la
							// extensión.
							String fieldValue = ds.getFieldValue(j, idField)
									.toString();
							if (!fieldValue.equals("")) {
								try {
									uriList.add(getURI(fieldValue,
											fileExtension));
								} catch (URISyntaxException e) {
									NotificationManager
											.addWarning(
													PluginServices
															.getText(this,
																	"Hyperlink__field_value_is_not_valid_file"),
													e);
								}
							}
						}
						ds.stop();
						return (URI[]) uriList.toArray(new URI[0]);
					}
					ds.stop();
				} else {
					PluginServices
							.getLogger()
							.error("Hyperlink error. FLyrVect class hierarchy changed??");
				}
			} catch (ReadDriverException e) {
				PluginServices.getLogger().error(e);
			}
		}
		return new URI[0];
	}

	protected URI getURI(String baseURI, String extension)
			throws URISyntaxException {
		String stringURI;
		if (extension.equals("")) {
			stringURI = baseURI;
		} else if (extension.startsWith(".")) {
			stringURI = baseURI + extension;
		} else {
			stringURI = baseURI + "." + extension;
		}
		File file = new File(stringURI);
		if (file.exists()) {
			return file.toURI();
		} else {
			return new URI(stringURI);
		}
	}

	public FLayer getLayer() {
		return _layer;
	}

	public void setLayer(FLayer layer) throws IncompatibleLayerException {
		try {
			_layer = (FLyrVect) layer;
		} catch (ClassCastException ex) {
			throw new IncompatibleLayerException(ex);
		}
	}

	public Object create() {
		return this;
	}

	public Object create(Object[] args) {
		return this;
	}

	public Object create(Map args) {
		return this;
	}

	public URI[][] getLink(Point2D point, double tolerance, String[] fieldName,
			String fileExtension) {
		FLyrVect lyrVect = (FLyrVect) _layer;
		FBitSet newBitSet;
		BitSet bitset;
		URI uri[][] = null;

		// Construimos el BitSet (Véctor con componentes BOOLEAN) con la
		// consulta que
		// hacemos a la capa.
		try {
			newBitSet = lyrVect.queryByPoint(point, tolerance);
		} catch (ReadDriverException e1) {
			PluginServices.getLogger().error(e1);
			return null;
		} catch (VisitorException e1) {
			PluginServices.getLogger().error(e1);
			return null;
		}
		bitset = newBitSet;

		// Si el bitset creado no está vacío creamos el vector de URLS
		// correspondientes
		// a la consulta que hemos hecho.

		if (bitset != null) {
			try {
				if (lyrVect instanceof AlphanumericData) {

					DataSource ds = ((AlphanumericData) lyrVect).getRecordset();
					ds.start();
					// boolean exist=false;
					int idField;
					// Creo el vector de URL´s con la misma longitud que el
					// bitset
					uri = new URI[bitset.length()][fieldName.length];

					// Recorremos el BitSet siguiendo el ejmplo de la clase que
					// se
					// proporciona en la API
					for (int geomNumber = bitset.nextSetBit(0); geomNumber >= 0; geomNumber = bitset
							.nextSetBit(geomNumber + 1)) {
						for (int fieldCount = 0; fieldCount < fieldName.length; fieldCount++) {
							// get the field ID using the field name
							idField = ds
									.getFieldIndexByName(fieldName[fieldCount]);
							if (idField != -1) {
								String auxField = ds.getFieldValue(geomNumber,
										idField).toString();
								if (auxField.startsWith("http:/")) {
									try {
										uri[geomNumber][fieldCount] = new URI(
												auxField);
									} catch (URISyntaxException e) {
										PluginServices.getLogger().error(e);
									}
								} else {
									File file = new File(ds.getFieldValue(
											geomNumber, idField).toString());
									uri[geomNumber][fieldCount] = file.toURI();
								}
							} else {
								PluginServices.getLogger().error(
										"Hyperlink error. Field "
												+ fieldName[fieldCount]
												+ "doesn't exist!!");
								uri[geomNumber][fieldCount] = null;
							}
						}

					}
					ds.stop();
					return uri;
				} else {
					PluginServices
							.getLogger()
							.error("Hyperlink error. FLyrVect class hierarchy changed??");
				}
			} catch (ReadDriverException e) {
				PluginServices.getLogger().error(e);
			}
		}
		return new URI[0][0];
	}

	public String[] getFieldCandidates() {
		ReadableVectorial reader = _layer.getSource();
		try {
			SelectableDataSource dataSource = reader.getRecordset();
			ArrayList fields = new ArrayList();
			int fieldType;
			for (int i = 0; i < dataSource.getFieldCount(); i++) {
				fieldType = dataSource.getFieldType(i);
				if (fieldType == java.sql.Types.VARCHAR
						|| fieldType == java.sql.Types.LONGVARCHAR
						|| fieldType == java.sql.Types.CHAR
						|| fieldType == java.sql.Types.BIGINT
						|| fieldType == java.sql.Types.INTEGER
						|| fieldType == java.sql.Types.NUMERIC
						|| fieldType == java.sql.Types.SMALLINT
						|| fieldType == java.sql.Types.TINYINT) {
					fields.add(dataSource.getFieldName(i));
				}
			}
			return (String[]) fields.toArray(new String[0]);
		} catch (ReadDriverException e) {
			NotificationManager.addError(
					PluginServices.getText(this, "Error reading layer fields"),
					e);
		}
		return new String[0];
	}

}
