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

import java.awt.Component;
import java.sql.Types;

import javax.swing.JOptionPane;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.drivers.DBLayerDefinition;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.IConnection;
import com.iver.cit.gvsig.fmap.drivers.db.utils.ConnectionWithParams;
import com.iver.cit.gvsig.fmap.edition.IWriter;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;

import es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle.OracleSpatialDriver;
import es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle.OracleSpatialUtils;
import es.prodevelop.cit.gvsig.fmap.drivers.jdbc.oracle.OracleSpatialWriter;
import es.prodevelop.cit.gvsig.jdbc_spatial.gui.jdbcwizard.OracleConnectionChooserPanel;


/**
 * Writes a layer as an Oracle table.
 *
 *
 * @author jldominguez
 *
 */
public class ExportToOracle {
    public void toOracle(MapContext mapContext, FLyrVect layer)
        throws EditionCommandException, DriverIOException {
        try {
            String tableName = "";
            boolean valid_name = false;

            while (!valid_name) {
                tableName = JOptionPane.showInputDialog(((Component)PluginServices.getMainFrame()),PluginServices.getText(
                            this, "intro_tablename"));
                valid_name = ((tableName == null) ||
                    ((tableName.length() <= (OracleSpatialDriver.MAX_ID_LENGTH -
                    3)) && (tableName.indexOf(" ") == -1) &&
                    (tableName.length() > 0)));

                if (!valid_name) {
                    if (tableName.length() > (OracleSpatialDriver.MAX_ID_LENGTH -
                            3)) {
                        JOptionPane.showMessageDialog(null,
                            PluginServices.getText(this,
                                "nombre_demasiado_largo"),
                            PluginServices.getText(this, "error"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                    else {
                        JOptionPane.showMessageDialog(null,
                            PluginServices.getText(this, "nombre_no_valido"),
                            PluginServices.getText(this, "error"),
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }

            if (tableName == null) {
                return;
            }

            tableName = tableName.toUpperCase();

            OracleConnectionChooserPanel dlg = new OracleConnectionChooserPanel();
            PluginServices.getMDIManager().addWindow(dlg);

            if (!dlg.isOkPressed()) {
                return;
            }

            ConnectionWithParams cwp = dlg.getSelectedCWP();

            if (cwp == null) {
                return;
            }

            IConnection conex = cwp.getConnection();

            DBLayerDefinition dbLayerDef = new DBLayerDefinition();
            dbLayerDef.setCatalogName(cwp.getDb());
            dbLayerDef.setSchema(cwp.getSchema());
            dbLayerDef.setTableName(tableName);
            dbLayerDef.setName(tableName);
            dbLayerDef.setShapeType(layer.getShapeType());

            SelectableDataSource sds = layer.getRecordset();
            FieldDescription[] fieldsDescrip = sds.getFieldsDescription();
            dbLayerDef.setFieldsDesc(fieldsDescrip);

            // Creamos el driver. OJO: Hay que añadir el campo ID a la
            // definición de campos.
            boolean bFound = false;

            for (int i = 0; i < fieldsDescrip.length; i++) {
                FieldDescription f = fieldsDescrip[i];

                if (f.getFieldName().compareTo(OracleSpatialDriver.DEFAULT_ID_FIELD_CASE_SENSITIVE) == 0) {
                    bFound = true;
                    break;
                }
            }

            // Si no está, lo añadimos
            if (!bFound) {
                int numFieldsAnt = fieldsDescrip.length;
                FieldDescription[] newFields = new FieldDescription[dbLayerDef.getFieldsDesc().length +
                    1];

                for (int i = 0; i < numFieldsAnt; i++) {
                    newFields[i] = fieldsDescrip[i];
                }

                newFields[numFieldsAnt] = new FieldDescription();
                newFields[numFieldsAnt].setFieldDecimalCount(0);
                newFields[numFieldsAnt].setFieldType(Types.INTEGER);
                newFields[numFieldsAnt].setFieldLength(7);
                newFields[numFieldsAnt].setFieldName(OracleSpatialDriver.DEFAULT_ID_FIELD_CASE_SENSITIVE);
                dbLayerDef.setFieldsDesc(newFields);
            }

            // addStartIfNotPresent(dbLayerDef, OracleSpatialDriver.ORACLE_ID_FIELD);
            // addEndIfNotPresent(dbLayerDef, OracleSpatialDriver.DEFAULT_GEO_FIELD);
            dbLayerDef.setFieldGeometry(OracleSpatialDriver.DEFAULT_GEO_FIELD);
            dbLayerDef.setFieldID(OracleSpatialDriver.ORACLE_ID_FIELD);

            dbLayerDef.setWhereClause("");

            String strSRID = layer.getProjection().getAbrev().substring(5);
            strSRID = mapContext.getProjection().getAbrev().substring(5);
            dbLayerDef.setSRID_EPSG(strSRID);
            dbLayerDef.setConnection(conex);

            OracleSpatialWriter writer = (OracleSpatialWriter) LayerFactory.getWM()
                                                                           .getWriter("Oracle Spatial Writer");
            writer.setLyrShapeType(layer.getShapeType());

            // writer.setWriteAll(true);
            // writer.setCreateTable(true);
            writer.initialize(dbLayerDef);

            int opt = JOptionPane.showConfirmDialog(null,
                    PluginServices.getText(this, "almacenar_sc_de_vista"),
                    PluginServices.getText(this, "exportando_features"),
                    JOptionPane.YES_NO_OPTION);

            boolean savesrs = (opt == JOptionPane.YES_OPTION);
            writer.setStoreWithSrid(savesrs);

            String orasrid = OracleSpatialDriver.epsgSridToOracleSrid(strSRID);
            boolean geo_cs = OracleSpatialUtils.getIsGCS(orasrid, savesrs);
            writer.setGeoCS(geo_cs);

            OracleSpatialDriver oDriver = new OracleSpatialDriver();

            oDriver.setDestProjection(strSRID);

            DBLayerDefinition driver_ldef = cloneDBLyrDef(dbLayerDef);

            addStartIfNotPresent(driver_ldef,
                OracleSpatialDriver.ORACLE_ID_FIELD);
            oDriver.setLyrDef(driver_ldef);
            oDriver.setUserName(cwp.getUser().toUpperCase());

            writer.setDriver(oDriver);

            Object[] params = new Object[2];
            params[0] = (IConnection) conex;
            params[1] = driver_ldef;

            /*
            PostProcessSupport.clearList();
            Object[] p = new Object[1];
            p[0] = params;
            PostProcessSupport.addToPostProcess(oDriver, "setData", p, 1);
            */
            oDriver.setShapeType(layer.getShapeType());

            writeFeatures(mapContext, layer, writer, oDriver, params);
        } catch (Exception e) {
            throw new EditionCommandException(layer.getName(), e);
        }
    }

    private void addStartIfNotPresent(DBLayerDefinition ldef,
        String default_id_field) {
        FieldDescription[] fdec = ldef.getFieldsDesc();
        int size = fdec.length;

        for (int i = 0; i < size; i++) {
            FieldDescription f = fdec[i];

            if (f.getFieldName().equalsIgnoreCase(default_id_field)) {
                return;
            }
        }

        FieldDescription[] newFields = new FieldDescription[size + 1];

        for (int i = 0; i < size; i++) {
            newFields[i + 1] = fdec[i];
        }

        newFields[0] = new FieldDescription();
        newFields[0].setFieldDecimalCount(0);
        newFields[0].setFieldType(Types.VARCHAR);
        newFields[0].setFieldLength(20);
        newFields[0].setFieldName(default_id_field);
        ldef.setFieldsDesc(newFields);
    }

    private void writeFeatures(MapContext mapContext, FLyrVect layer,
        IWriter writer, OracleSpatialDriver reader, Object[] setDataParams)
        throws DriverIOException {
        PluginServices.cancelableBackgroundExecution(new OracleWriteTask(
                mapContext, layer, (OracleSpatialWriter) writer, reader, setDataParams));
    }

    private DBLayerDefinition cloneDBLyrDef(DBLayerDefinition ldef) {
        DBLayerDefinition resp = new DBLayerDefinition();
        resp.setCatalogName(ldef.getCatalogName());
        resp.setSchema(ldef.getSchema());
        resp.setTableName(ldef.getTableName());
        resp.setName(ldef.getName());
        resp.setShapeType(ldef.getShapeType());
        resp.setFieldsDesc(ldef.getFieldsDesc());
        resp.setFieldGeometry(ldef.getFieldGeometry());
        resp.setFieldID(ldef.getFieldID());
        resp.setWhereClause(ldef.getWhereClause());
        resp.setSRID_EPSG(ldef.getSRID_EPSG());
        resp.setConnection(ldef.getConnection());
        // NO USAR ESTA FUNCIÓN!!
        // TODO: DEPRECARLA DE ALGUNA FORMA, O AVISAR DE QUE NO
        // SE USE CON LOS WRITERS.
        // HAY QUE USAR SETFIELDSDESC
        // resp.setFieldNames(ldef.getFieldNames());

        return resp;
    }



}
