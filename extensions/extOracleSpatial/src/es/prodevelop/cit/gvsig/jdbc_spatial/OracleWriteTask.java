/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package es.prodevelop.cit.gvsig.jdbc_spatial;

import java.awt.Shape;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.opengis.referencing.operation.MathTransform;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.FShape3D;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.core.v02.FLabel;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.ILayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.SHPLayerDefinition;
import com.iver.cit.gvsig.fmap.edition.DefaultRowEdited;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLyrAnnotation;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.ReadableVectorial;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.utiles.swing.threads.AbstractMonitorableTask;

import es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle.OracleSpatialDriver;
import es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle.OracleSpatialWriter;


/**
 * Utility class to export a vector layer as a oracle table.
 *
 * @author jldominguez
 *
 */
public class OracleWriteTask extends AbstractMonitorableTask {
    private static Logger logger = Logger.getLogger(OracleWriteTask.class.getName());
    FLyrVect lyrVect;
    OracleSpatialWriter writer;
    int rowCount;
    ReadableVectorial va;
    SelectableDataSource sds;
    FBitSet bitSet;
    MapContext mapContext;
    OracleSpatialDriver reader;
    Object[] setDataParams = null;

    public OracleWriteTask(MapContext mapc, FLyrVect lyr,
        OracleSpatialWriter wtr, OracleSpatialDriver rdr,
        Object[] setDataPms) throws DriverIOException {
    	
    	setDataParams = setDataPms;
        mapContext = mapc;
        lyrVect = lyr;
        writer = wtr;
        
        try {
			writer.setBbox(lyr.getFullExtent());
		} catch (Exception e) {
			throw new DriverIOException(e.getMessage());
		}
		
        reader = rdr;
        writer.setDriver(rdr);

        int dims = 2;

        try {
        	
            ReadableVectorial rdv = lyrVect.getSource();
            rdv.start();
            IGeometry sample = rdv.getShape(0);
            rdv.stop();
            Shape shp = sample.getInternalShape();

            if (shp instanceof FShape3D) dims = 3;
            
        } catch (Exception ex) {
        	logger.error("While getting dim of sample geometry: " + ex.getMessage());
        }

        writer.setDimensions(dims);

        setInitialStep(0);
        setDeterminatedProcess(true);
        setStatusMessage(PluginServices.getText(this, "exportando_features"));

        va = lyrVect.getSource();
        try {
			sds = lyrVect.getRecordset();
		} catch (ReadDriverException e) {
			throw new DriverIOException(e.getMessage());
		}

        bitSet = sds.getSelection();

        if (bitSet.cardinality() == 0) {
            try {
				rowCount = va.getShapeCount();
			} catch (ReadDriverException e) {
				throw new DriverIOException(e.getMessage());
			}
        }
        else
        {
            rowCount = bitSet.cardinality();
        }

        setFinalStep(rowCount);
    }

    public void run() throws Exception {
        MathTransform trans = lyrVect.getCrsTransform();
        DriverAttributes attr = va.getDriverAttributes();
        boolean bMustClone = false;

        if (attr != null) {
            if (attr.isLoadedInMemory()) {
                bMustClone = attr.isLoadedInMemory();
            }
        }

        if (lyrVect instanceof FLyrAnnotation &&
                (lyrVect.getShapeType() != FShape.POINT)) {
            SHPLayerDefinition lyrDef = (SHPLayerDefinition) writer.getTableDefinition();
            lyrDef.setShapeType(FShape.POINT);
            writer.initialize(lyrDef);
        }

        va.start();
        // Creamos la tabla.
        writer.preProcess();

        if (bitSet.cardinality() == 0) {
            rowCount = va.getShapeCount();

            for (int i = 0; i < rowCount; i++) {
                IGeometry geom = va.getShape(i);

                if (lyrVect instanceof FLyrAnnotation &&
                        (geom.getGeometryType() != FShape.POINT)) {
                    Point2D p = FLabel.createLabelPoint((FShape) geom.getInternalShape());
                    geom = ShapeFactory.createPoint2D(p.getX(), p.getY());
                }

                if ((trans != null) && (geom != null)) {
                    if (bMustClone) {
                        geom = geom.cloneGeometry();
                    }

                    geom.reProject(trans);
                }

                reportStep();
                setNote(PluginServices.getText(this, "exporting_") + " " +
                    (i + 1) + " " + PluginServices.getText(this, "geometries"));

                if (isCanceled()) {
                    logger.debug("Export canceled.");

                    break;
                }

                if (geom != null) {
                    Value[] values = sds.getRow(i);
                    IFeature feat = new DefaultFeature(geom, values, "" + i);
                    DefaultRowEdited edRow = new DefaultRowEdited(feat,
                            DefaultRowEdited.STATUS_ADDED, i);
                    writer.process(edRow);
                }
            }
        }
        else {
        	int count = 0;
        	
            for (int i = bitSet.nextSetBit(0); i >= 0;
                    i = bitSet.nextSetBit(i + 1)) {
                IGeometry geom = va.getShape(i);

                if (lyrVect instanceof FLyrAnnotation &&
                        (geom.getGeometryType() != FShape.POINT)) {
                    Point2D p = FLabel.createLabelPoint((FShape) geom.getInternalShape());
                    geom = ShapeFactory.createPoint2D(p.getX(), p.getY());
                }

                if (trans != null) {
                    if (bMustClone) {
                        geom = geom.cloneGeometry();
                    }

                    geom.reProject(trans);
                }

                reportStep();
                setNote(PluginServices.getText(this, "exporting_") + " " +
                        (count + 1) + " " + PluginServices.getText(this, "geometries"));

                if (isCanceled()) {
                    logger.debug("Export canceled.");

                    break;
                }

                if (geom != null) {
                    Value[] values = sds.getRow(i);
                    IFeature feat = new DefaultFeature(geom, values, "" + i);
                    DefaultRowEdited edRow = new DefaultRowEdited(feat,
                            DefaultRowEdited.STATUS_ADDED, i);

                    writer.process(edRow);
                }
                count++;
            }
        }

        writer.postProcess();
        va.stop();

        if (reader != null) {
            int res = JOptionPane.showConfirmDialog((JComponent) PluginServices.getMDIManager()
                                                                               .getActiveWindow(),
                    PluginServices.getText(this,
                        "insertar_en_la_vista_la_capa_creada"),
                    PluginServices.getText(this, "insertar_capa"),
                    JOptionPane.YES_NO_OPTION);

            if (res == JOptionPane.YES_OPTION) {

            	reader.setData(setDataParams);
                ILayerDefinition lyrDef = (ILayerDefinition) writer.getTableDefinition();
                FLayer newLayer = LayerFactory.createLayer(lyrDef.getName(),
                        reader, mapContext.getCrs());
                mapContext.getLayers().addLayer(newLayer);
            }
        }
    }
}
