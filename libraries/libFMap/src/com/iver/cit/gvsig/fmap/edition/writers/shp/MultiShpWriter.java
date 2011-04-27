package com.iver.cit.gvsig.fmap.edition.writers.shp;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

import com.hardcode.gdbms.driver.exceptions.InitializeWriterException;
import com.hardcode.gdbms.driver.exceptions.SchemaEditionException;
import com.iver.cit.gvsig.exceptions.visitors.ProcessWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StartWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.StopWriterVisitorException;
import com.iver.cit.gvsig.exceptions.visitors.VisitorException;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.ITableDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISchemaManager;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.edition.ShpSchemaManager;
import com.iver.utiles.FileUtils;

/**
 * This writer wraps three ShpWriters, one for points, one for lines and one for
 * polygons geometry types. <br>
 *
 * It allows you to save a FLyrVect with MULTI shape type in SHP file format
 * (that doesnt allow to mix different geometry types). To do that, this IWriter
 * creates a different SHP file for any geometry type, in a transparent manner
 * for the programmer. <br>
 * If your geometries doesnt hava a given geometry type, the Writer wont create
 * a file for this geometry type. <br>
 * <code>
 * MultiShpWriter writer = new MultiShpWriter();
 * writer.setFile(dxfFile);
 * writer.initialize(layerDefinition);
 * writer.preProcess();
 * # obtain features from an iterator
 * writer.process(feature);
 * writer.postProcess();
 * </code>
 *
 */
public class MultiShpWriter implements IWriter {
	/**
	 * original file name selected by user
	 */
	String file = null;

	/**
	 * original layer definition
	 */
	ILayerDefinition layerDefinition;// only works with layers (no only
										// tables)

	IWriter polygons;

	File polygonsFile;

	IWriter lines;

	File linesFile;

	IWriter points;

	File pointsFile;

	public void preProcess() throws StartWriterVisitorException {
	}

	/**
	 * Returns all ShpWriter's created by this wrapper (only those wich writes a
	 * type of the processed geometries)
	 *
	 * @return
	 */
	public IWriter[] getWriters() {
		IWriter[] solution;
		ArrayList list = new ArrayList();
		if (polygons != null)
			list.add(polygons);
		if (lines != null)
			list.add(lines);
		if (points != null)
			list.add(points);
		solution = new IWriter[list.size()];
		list.toArray(solution);
		return solution;
	}

	public void postProcess() throws StopWriterVisitorException {
		if (polygons != null)
			polygons.postProcess();
		if (lines != null)
			lines.postProcess();
		if (points != null)
			points.postProcess();
	}

	/**
	 * Give access to the Writer that processes polygon geometries (and creates
	 * it if it hasnt yet)
	 *
	 * @return
	 * @throws EditionException
	 */
	private IWriter getPolygonsWriter() throws VisitorException {
		if (polygons == null) {
			polygons = new ShpWriter();
			// TODO Hacer que LayerDefinition sea cloneable
			SHPLayerDefinition newDefinition = (SHPLayerDefinition) cloneDef(layerDefinition);
			// we clone layerdefinition to change
			// shape type
			newDefinition.setShapeType(FShape.POLYGON);
			newDefinition.setFile(getPolygonsFile());
			((ShpWriter) polygons).setFile(polygonsFile);
			try {
				polygons.initialize(newDefinition);
			} catch (InitializeWriterException e) {
				throw new ProcessWriterVisitorException(getName(),e);
			}

			try {
				getSchemaManager(polygons, polygonsFile)
						.createSchema(newDefinition);
			} catch (SchemaEditionException e) {
				throw new ProcessWriterVisitorException(getName(),e);
			}

			//AZABALA: no si si es neceario
			polygons.preProcess();
		}
		return polygons;
	}

	/**
	 * Given a Writer, and the file where we want to save persistent features,
	 * it returns an associated ISchemaManager (whose responsability is to
	 * create the new schema-for files create the new files)
	 *
	 * @param writer
	 * @param file
	 * @return
	 */

	private ISchemaManager getSchemaManager(final IWriter writer,
			final File file) {
		return new ShpSchemaManager(file.getAbsolutePath());
	}

	/**
	 * From a given layer definition, it creates a new layer definition 'clon'
	 * of the initial. It is useful to avoid local changes made by individual
	 * Writers to the definition affects the others writers (for example, change
	 * the shape type of the writer)
	 *
	 * @param definition
	 * @return
	 */
	private ILayerDefinition cloneDef(ILayerDefinition definition) {
		ILayerDefinition solution = null;
		if (definition instanceof SHPLayerDefinition) {
			SHPLayerDefinition def = (SHPLayerDefinition) definition;
			solution = new SHPLayerDefinition();
			solution.setName(def.getName());
			FieldDescription[] fields = def.getFieldsDesc();
			solution.setFieldsDesc(fields);
		}
		return solution;
	}

	/**
	 * Give access to the Writer that processes line geometries (and creates it
	 * if it hasnt yet)
	 *
	 * @return
	 * @throws EditionException
	 */
	private IWriter getLinesWriter() throws VisitorException {
		if (lines == null) {
			lines = new ShpWriter();
			SHPLayerDefinition newDefinition = (SHPLayerDefinition) cloneDef(layerDefinition);
			// we clone layerdefinition to change
			// shape type
			newDefinition.setShapeType(FShape.LINE);
			newDefinition.setFile(getLinesFile());
			((ShpWriter) lines).setFile(linesFile);
			try {
				lines.initialize(newDefinition);
				getSchemaManager(lines, linesFile).createSchema(newDefinition);
				lines.preProcess();
			} catch (InitializeWriterException e) {
				throw new ProcessWriterVisitorException(getName(),e);
			} catch (SchemaEditionException e) {
				throw new ProcessWriterVisitorException(getName(),e);
			}
		}
		return lines;
	}

	/**
	 * Give access to the Writer that processes point geometries (and creates it
	 * if it hasnt yet)
	 *
	 * @return
	 * @throws EditionException
	 */

	private IWriter getPointsWriter() throws VisitorException{
		if (points == null) {
			points = new ShpWriter();
			SHPLayerDefinition newDefinition = (SHPLayerDefinition) cloneDef(layerDefinition);
			// we clone layerdefinition to change
			// shape type
			newDefinition.setShapeType(FShape.POINT);
			newDefinition.setFile(getPointsFile());
			((ShpWriter) points).setFile(pointsFile);
			try {
				points.initialize(newDefinition);
				getSchemaManager(points, pointsFile).createSchema(newDefinition);
			} catch (InitializeWriterException e) {
				throw new ProcessWriterVisitorException(getName(),e);
			} catch (SchemaEditionException e) {
				throw new ProcessWriterVisitorException(getName(),e);
			}
				points.preProcess();
		}
		return points;
	}

	/**
	 * Giving an edited row, writes it with the Writer associated to its
	 * geometry type
	 */
	public void process(IRowEdited row) throws VisitorException {
		IFeature feature = (IFeature) row.getLinkedRow();
		int geometryType = feature.getGeometry().getGeometryType();
		switch (geometryType) {
		case FShape.POINT:
			getPointsWriter().process(row);
			break;
		case FShape.LINE:
		case FShape.ELLIPSE:
		case FShape.ARC:
		case FShape.CIRCLE:
			getLinesWriter().process(row);
			break;

		case FShape.POLYGON:
			getPolygonsWriter().process(row);
			break;
		}
	}

	/**
	 * Sets the file where save the results
	 *
	 * @param f
	 */
	public void setFile(File f) {
		file = FileUtils.getFileWithoutExtension(f);
	}

	public String getFileName() {
		return file;
	}

	public String getCapability(String capability) {
		return "";
	}

	public void setCapabilities(Properties capabilities) {
	}

	public boolean canWriteAttribute(int sqlType) {
		return true;
	}

	public void initialize(ITableDefinition layerDefinition)
			throws InitializeWriterException {
		this.layerDefinition = (ILayerDefinition) layerDefinition;
	}

	public String getName() {
		return "MULTI File Writer";
	}

	public ITableDefinition getTableDefinition() {
		return layerDefinition;
	}

	public boolean canAlterTable() {
		return true;
	}
	public boolean canSaveEdits() throws VisitorException {
		if (getPointsWriter().canSaveEdits())
			{
				if (getLinesWriter().canSaveEdits())
				{
					if (getPolygonsWriter().canSaveEdits())
						return true;
				}
			}
		return false;
	}

	public boolean isWriteAll() {
		return true;
	}

	public File getPolygonsFile() {
		if (polygonsFile == null){
			polygonsFile = new File(file + "_POL.shp");
		}
		return polygonsFile;
	}

	public File getLinesFile() {
		if (linesFile == null){
			linesFile = new File(file + "_LIN.shp");
		}
		return linesFile;
	}

	public File getPointsFile() {
		if (pointsFile == null){
			pointsFile = new File(file + "_PT.shp");
		}
		return pointsFile;
	}

}