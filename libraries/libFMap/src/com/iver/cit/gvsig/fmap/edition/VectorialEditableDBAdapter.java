/**
 *
 */
package com.iver.cit.gvsig.fmap.edition;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.cresques.cts.IProjection;

import com.hardcode.driverManager.Driver;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileReadException;
import com.iver.cit.gvsig.exceptions.expansionfile.ExpansionFileWriteException;
import com.iver.cit.gvsig.exceptions.layers.CancelEditingLayerException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IRow;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.IFeatureIterator;
import com.iver.cit.gvsig.fmap.drivers.IVectorialDatabaseDriver;
import com.iver.cit.gvsig.fmap.drivers.featureiterators.AttrQueryFeatureIterator;
import com.iver.cit.gvsig.fmap.layers.ISpatialDB;
import com.iver.cit.gvsig.fmap.layers.VectorialDBAdapter;
import com.iver.cit.gvsig.fmap.spatialindex.QuadtreeJts;

/**
 * @author fjp
 *
 */
public class VectorialEditableDBAdapter extends VectorialEditableAdapter
		implements ISpatialDB {
	private class MyIterator implements IFeatureIterator {
		private Rectangle2D extent = null;

		private VectorialDBAdapter orig;

		private IFeature feat;

		private IFeatureIterator featIt;

		private String epsg;

		private IVectorialDatabaseDriver dbDriver;

		Hashtable alreadyDone = new Hashtable();

		private int idFromExpansion = 0;

		private List listFromExpansion;

		private boolean bOriginalCursorOpened = true;

		public MyIterator(Rectangle2D r, String strEPSG) throws ReadDriverException {
			extent = r;
			epsg = strEPSG;
			orig = (VectorialDBAdapter) ova;
			featIt = orig.getFeatureIterator(extent, epsg);
			dbDriver = (IVectorialDatabaseDriver) getOriginalDriver();
			getFeaturesFromExpansionFile();
		}


		/*
		 * azo: these new constructors must be tested
		 * */

		public MyIterator(String[] fields, IProjection newProjection) throws ReadDriverException{
			epsg = newProjection.getAbrev();
			orig = (VectorialDBAdapter) ova;
			featIt = orig.getFeatureIterator(fields, newProjection);
			dbDriver = (IVectorialDatabaseDriver) getOriginalDriver();
			getFeaturesFromExpansionFile();
		}

		public MyIterator(String sql, IProjection newProjection) throws ReadDriverException{
			epsg = newProjection.getAbrev();
			orig = (VectorialDBAdapter) ova;
			featIt = orig.getFeatureIterator(sql, newProjection);
			dbDriver = (IVectorialDatabaseDriver) getOriginalDriver();
			getFeaturesFromExpansionFile();
		}

		public MyIterator(Rectangle2D rect, String[] fields, IProjection newProjection) throws ReadDriverException{
			extent = rect;
			epsg = newProjection.getAbrev();
			orig = (VectorialDBAdapter) ova;
			featIt = orig.getFeatureIterator(extent, fields, newProjection, true);
			dbDriver = (IVectorialDatabaseDriver) getOriginalDriver();
			getFeaturesFromExpansionFile();
		}



		public boolean hasNext() throws ReadDriverException {
			feat = null;
			int calculatedIndex = -1;
			if (bOriginalCursorOpened) // Si hay originales (Es porque si se ha
										// llegado al final, se cierra el
										// iterador y salta un fallo
			{
				bOriginalCursorOpened = featIt.hasNext();
				if (bOriginalCursorOpened) {
					feat = featIt.next();
					int originalIndex = dbDriver.getRowIndexByFID(feat);

					// Iteramos hasta que encontremos alguno no borrado.
					// Aquí suponemos que el orden es el original. Si no, no funcionará.
					if (delRows.get(originalIndex)) // Si está borrado
					{
						feat = null;
						boolean bFound = false;
						while (featIt.hasNext())
						{
							feat = featIt.next();
							originalIndex = dbDriver.getRowIndexByFID(feat);
							// calculatedIndex = getCalculatedIndex(originalIndex);
							if (delRows.get(originalIndex) == false) // Si NO está borrado
							{
								bFound = true;
								break;
							}
						}
						if (bFound == false) // Todos los últimos están borrados.
						{
							bOriginalCursorOpened = false; // para que busque en el fichero de expansión
							feat = null;
						}
					} // if delRows
					if (bOriginalCursorOpened) // Si todavía quedan features por leer, y no están borradas
					{
						calculatedIndex = originalIndex; //getCalculatedIndex(originalIndex);
						Integer integer = new Integer(calculatedIndex);
						if (!relations.containsKey(integer)) { // Si no está en el
																// fichero de
																// expansión
							alreadyDone.put(integer, feat);
						} else { // Si está en el fichero de expansión
							int num = ((Integer) relations.get(integer)).intValue();
							IRowEdited auxR = null;
							try {
								auxR = expansionFile.getRow(num);
							} catch (ExpansionFileReadException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							feat = (IFeature) auxR.getLinkedRow().cloneRow();
							// feat = (IFeature) auxR.getLinkedRow();
							alreadyDone.put(integer, feat);
						} // else
					} // if tercer bOriginalCursorOpened
				} // if segundo bOriginalCursorOpened
			} // if primer bOriginalCursorOpened
			if (!bOriginalCursorOpened) {
				// Si ya no hay más de las originales, todavía tenemos
				// que revisar las añadidas que hay en el fichero
				// de expansión
					while ((idFromExpansion < expansionFile.getSize()) && (feat == null))
					{
						IRowEdited rowEd = expansionFile.getRow(idFromExpansion);
						IFeature aux = (IFeature) rowEd.getLinkedRow();
						Integer calculated = (Integer) mapFID2index.get(aux.getID());
						calculatedIndex = calculated.intValue();
						System.out.println("El elemento idFromExpansion = " + idFromExpansion + " es " + aux.getID());

						// Revisamos los borrados
						if (delRows.get(calculatedIndex) == true)
						{
							boolean bFound = false;
							while ((!bFound) && (idFromExpansion < expansionFile.getSize()-1))
							{
								// calculatedIndex++;
								idFromExpansion++;
								rowEd = expansionFile.getRow(idFromExpansion);
								aux = (IFeature) rowEd.getLinkedRow();



								Integer auxCalculated = (Integer) mapFID2index.get(aux.getID());
								calculatedIndex = auxCalculated.intValue();
								// Si no está borrado y es una entidad válida, que está siendo usada (no es algo que está en el expansionFile sin usarse)
								if ((delRows.get(calculatedIndex) == false) && (relations.containsKey(auxCalculated)))
								{
									bFound = true;
									calculated = auxCalculated;
									break;
								}
								else
								{
									System.out.println("El elemento idFromExpansion = " + idFromExpansion + " está borrado");
								}
							}
							if (bFound)
							{
								calculated = new Integer(calculatedIndex);
								rowEd = expansionFile.getRow(idFromExpansion);
								aux = (IFeature) rowEd.getLinkedRow();
							}
							else
							{
								return false; // El resto están borrados
							}
						} // if primer borrado
						if (relations.containsKey(calculated))
						{
							Integer realExpansionIndex = (Integer) relations.get(calculated);
							if (realExpansionIndex.intValue() == idFromExpansion)
							{
								feat = (IFeature) aux.cloneRow();
							}
						}
						idFromExpansion++;
					}
			}

			if (calculatedIndex == -1)
				return false;
			else {
				if (feat == null)
				{
					if (idFromExpansion == expansionFile.getSize())
						return false;
					else
						System.err.println("ERROR DE ENTREGA DE FEATURE EN hasNext del Iterador");
				}
				/* if (delRows.get(calculatedIndex))
					feat = null; */
				return true;
			}

		}

		public IFeature next() {
			return feat;
		}

		public void closeIterator() throws ReadDriverException {
			// TODO Auto-generated method stub

		}

		private void getFeaturesFromExpansionFile() {
//			Envelope e = FConverter.convertRectangle2DtoEnvelope(extent);
			listFromExpansion = fmapSpatialIndex.query(extent);
		}
	}


	private Hashtable mapFID2index = new Hashtable();
//	private Hashtable mapIndex2FID = new Hashtable();
	/**
	 *
	 */
	public VectorialEditableDBAdapter() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter#getFeatures(java.awt.geom.Rectangle2D,
	 *      java.lang.String)
	 */
	public IRowEdited[] getFeatures(Rectangle2D r, String strEPSG) throws ReadDriverException, ExpansionFileReadException {
		ArrayList aux = new ArrayList();
		IFeatureIterator featIt = getFeatureIterator(r, strEPSG, getLyrDef().getFieldNames());
		int numEntities = 0;
		while (featIt.hasNext()) {
			IFeature feat = featIt.next();
			// TODO:
			assert(feat !=null);
			int index = getRowIndexByFID(feat);
			IRowEdited edRow = new DefaultRowEdited(feat, IRowEdited.STATUS_ORIGINAL, index);
			aux.add(edRow);
			numEntities++;
		}

		return (IRowEdited[]) aux.toArray(new IRowEdited[0]);
		// return (IFeature[]) aux.toArray(new IFeature[0]);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter#startEdition()
	 */
	public void startEdition(int sourceType) throws StartWriterVisitorException {
		isEditing = true;
		Driver drv = ova.getDriver();
		if (drv instanceof IWriteable)
		{
			setWriter(((IWriteable) drv).getWriter());
		}

		try {
			expansionFile.open();

			// TODO: Si la capa dispone de un índice espacial, hacer
			// algo aquí para que se use ese índice espacial.
//			index = new Quadtree();
			fmapSpatialIndex = new QuadtreeJts();
			// No metemos ninguna entidad de las originales dentro
			// de la base de datos porque esa consulta ya la
			// hace getFeatures sin tener en cuenta el índice local.
			 for (int i = 0; i < ova.getShapeCount(); i++)
			 {
				 IFeature feat = ova.getFeature(i);
				 Integer calculatedIndex = new Integer(i);
				 mapFID2index.put(feat.getID(), calculatedIndex);
//				 mapIndex2FID.put(calculatedIndex, feat.getID());
			 }

			/*
			 * for (int i = 0; i < ova.getShapeCount(); i++) { IGeometry g=null;
			 * try { g = ((DefaultFeature) ova.getFeature(i)).getGeometry(); }
			 * catch (DriverException e1) { // TODO Auto-generated catch block
			 * e1.printStackTrace(); }
			 *
			 * if (g == null) { continue; }
			 *
			 * Rectangle2D r = g.getBounds2D(); Envelope e = new
			 * Envelope(r.getX(), r.getX() + r.getWidth(), r.getY(), r.getY() +
			 * r.getHeight()); index.insert(e, new Integer(i)); } } catch
			 * (DriverIOException e) { throw new EditionException(e);
			 */
		} catch (ReadDriverException e) {
			throw new StartWriterVisitorException(writer.getName(),e);
		}

//		System.err.println("Se han metido en el índice "
//				+ index.queryAll().size() + " geometrías");

	}

	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG)
			throws ReadDriverException {
		return new MyIterator(r, strEPSG);
	}

	public IFeatureIterator getFeatureIterator(Rectangle2D r, String strEPSG,
			String[] alphaNumericFieldsNeeded) throws ReadDriverException {
		return new MyIterator(r, alphaNumericFieldsNeeded, CRSFactory.getCRS(strEPSG));
	}

	public IFeatureIterator getFeatureIterator(String[] fields, IProjection newProjection)
	throws ReadDriverException{
		//TODO make tests with these (unit test of vectorialeditableadapter)
		return new MyIterator(fields, newProjection);
//		return new DefaultFeatureIterator(this, projection, newProjection, fields);
	}

	//TODO test this (azo)
	public IFeatureIterator getFeatureIterator(Rectangle2D rect, String[] fields,
			IProjection newProjection,
			boolean fastIteration) throws ReadDriverException{
		return getFeatureIterator(rect, newProjection.getAbrev(), fields);
	}

	/**
	* Return a feature iterator from a given sql statement.
	* <br>
	* In this case, the statement will have the "projection" operator
	* (select campo1, campo2, ...etc) and the "selection" operator (where ....)
	* @param sql statement which define a filter
	* @return feature iterator
	* */
	public IFeatureIterator getFeatureIterator(String sql,
								IProjection newProjection) throws ReadDriverException{

		return new AttrQueryFeatureIterator(this, projection, newProjection, sql);
	}

	public DBLayerDefinition getLyrDef() {
		VectorialDBAdapter orig = (VectorialDBAdapter) ova;
		return orig.getLyrDef();
	}

	public int getRowIndexByFID(IFeature feat) {
		Integer calculatedIndex = (Integer) mapFID2index.get(feat.getID());
		return getInversedIndex(calculatedIndex.intValue());
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter#doAddRow(com.iver.cit.gvsig.fmap.core.IRow)
	 */
	public int doAddRow(IRow feat, int sourceType) throws ReadDriverException, ExpansionFileWriteException {
		int calculatedIndex = super.doAddRow(feat, sourceType);
		// Integer posInExpansionFile = (Integer) relations.get(new Integer(calculatedIndex));
		Integer virtual = new Integer(calculatedIndex); // calculatedIndex es igual al numero de shapes originales + el numero de entidades añadidas.
					// es decir, virtual es el calculatedIndex (no tiene en cuenta los borrados)
					// calculatedIndex = indiceExterno + borrados hasta ese punto.
		mapFID2index.put(feat.getID(), virtual);
//		mapIndex2FID.put(virtual, feat.getID());
		return calculatedIndex;

	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter#doModifyRow(int, com.iver.cit.gvsig.fmap.core.IRow)
	 */
	public int doModifyRow(int calculatedIndex, IRow feat,int sourceType) throws ReadDriverException, ExpansionFileWriteException, ExpansionFileReadException{
		int posAnteriorInExpansionFile = super.doModifyRow(calculatedIndex, feat, sourceType); // devolverá -1 si es original
		// No hacemos nada con las modificaciones sobre los índices.
		// Suponiendo que feat tenga la misma ID que la que había antes.
		Integer virtual = new Integer(calculatedIndex);
//		String theIDoriginal = (String) mapIndex2FID.get(virtual);
//		if (!theIDoriginal.equals(feat.getID()))
//		{
//			AssertionError err = new AssertionError("Fallo al modificar la fila. ID viejo=" + theIDoriginal + " ID nuevo = " + feat.getID());
//			err.printStackTrace();
//		}
		// hashFIDtoExpansionFile.put(feat.getID(), new Integer(posInExpansionFile));
		mapFID2index.put(feat.getID(), virtual);
//		mapIndex2FID.put(virtual, feat.getID());
		return posAnteriorInExpansionFile;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter#doRemoveRow(int)
	 */
	public IRow doRemoveRow(int index,int sourceType) throws ReadDriverException, ExpansionFileReadException {
		// Le entra un calculatedIndex, así que delRows tiene guardados
		// los índices internos, no los externos.
		IFeature deletedFeat = (IFeature) super.doRemoveRow(index, sourceType);
		// Lo borramos de hashFIDtoExpansionFile
		// hashFIDtoExpansionFile.remove(deletedFeat.getID());
		return deletedFeat;
	}

	/* (non-Javadoc)
	 * @see com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter#undoAddRow(int)
	 */
	public void undoAddRow(int calculatedIndex, int sourceType) throws EditionCommandException {
		// TODO Auto-generated method stub
		super.undoAddRow(calculatedIndex,sourceType);
		Integer calculated = new Integer(calculatedIndex);
//		String theID = (String) mapIndex2FID.get(calculated);
		mapFID2index.remove(calculated);
//		mapIndex2FID.remove(theID);

	}
	public void cancelEdition(int sourceType) throws CancelEditingLayerException {
		super.cancelEdition(sourceType);
		mapFID2index.clear();
//		mapIndex2FID.clear();
	}

	public void stopEdition(IWriter writer, int sourceType) throws StopWriterVisitorException{
		super.stopEdition(writer, sourceType);
		mapFID2index.clear();
//		mapIndex2FID.clear();
	}
}
