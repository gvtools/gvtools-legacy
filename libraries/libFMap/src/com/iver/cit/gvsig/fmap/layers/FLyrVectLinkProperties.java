package com.iver.cit.gvsig.fmap.layers;

import java.awt.geom.Point2D;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.BitSet;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;

/**
 * This class extends AbstractLinkProperties and implements the method to get an
 * array of URI using a point2D and a tolerance. This class extends
 * AstractLinkProperties to add HyperLink to Vectorial Layer(FLyrVect)
 */

public class FLyrVectLinkProperties extends AbstractLinkProperties {

	/**
	 * Default Constructor. Costructs a LinkProperties with the necessary
	 * information
	 * 
	 */
	public FLyrVectLinkProperties() {
		setType(0);
		setField(null);
		setExt(null);
	}

	/**
	 * Constructor. Constructs a LinkProperties with the information that
	 * receives
	 * 
	 * @param tipo
	 * @param fieldName
	 * @param extension
	 */
	public FLyrVectLinkProperties(int tipo, String fieldName, String extension) {
		setType(tipo);
		setField(fieldName);
		setExt(extension);
	}

	/**
	 * Creates an array of URI. With the point and the tolerance makes a query
	 * to the layer and gets the geometries that contains the point with a
	 * certain error (tolerance). For each one of this geometries creates one
	 * URI and adds it to the array
	 * 
	 * @param layer
	 * @param point
	 * @param tolerance
	 * @return Array of URI
	 */
	public URI[] getLink(FLayer layer, Point2D point, double tolerance) {
		// Sacado de la clase LinkListener
		FLyrVect lyrVect = (FLyrVect) layer;
		FBitSet newBitSet;
		BitSet bitset;
		URI uri[] = null;

		// Construimos el BitSet (Véctor con componentes BOOLEAN) con la
		// consulta que
		// hacemos a la capa.
		try {
			newBitSet = lyrVect.queryByPoint(point, tolerance);
			bitset = newBitSet;
		} catch (ReadDriverException e) {
			return null;
		} catch (VisitorException e) {
			return null;
		}

		// Si el bitset creado no está vacío creamos el vector de URLS
		// correspondientes
		// a la consulta que hemos hecho.

		if (bitset != null) {
			try {
				if (layer instanceof AlphanumericData) {

					DataSource ds = ((AlphanumericData) layer).getRecordset();
					ds.start();
					// boolean exist=false;
					int idField;
					// Creo el vector de URL´s con la misma longitud que el
					// bitset
					uri = new URI[bitset.length()];

					// Consigo el identificador del campo pasandole como
					// parámetro el
					// nombre del campo del énlace
					idField = ds.getFieldIndexByName(this.getField());
					if (idField != -1) {
						// Recorremos el BitSet siguiendo el ejmplo de la clase
						// que se
						// proporciona en la API
						for (int j = bitset.nextSetBit(0); j >= 0; j = bitset
								.nextSetBit(j + 1)) {
							// TODO
							// Sacado de la clase LinkPanel, decidir como pintar
							// la URL le
							// corresponde a LinkPanel, aquí solo creamos el
							// vector de URL´s
							String auxext = "";
							if (!super.getExt().equals("")) {
								auxext = "." + this.getExt();
							}
							// ds.start();
							// Creamos el fichero con el nombre del campo y la
							// extensión.
							String auxField = ds.getFieldValue(j, idField)
									.toString();
							if (auxField.startsWith("http:/")) {
								try {
									uri[j] = new URI(auxField);
								} catch (URISyntaxException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {

								File file = new File(ds.getFieldValue(j,
										idField).toString()
										+ auxext);
								uri[j] = file.toURI();
							}
							System.out.println(uri[j].toString());
							// System.out.println(uri[j]);

						}
						return uri;
					}

					ds.stop();
				} else {
					// Posible error
					System.out.println("Error");
				}

			} catch (ReadDriverException e) {
				// Posible error
				System.out.println("Error");
			}

		}
		return null;
	}

	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

}
