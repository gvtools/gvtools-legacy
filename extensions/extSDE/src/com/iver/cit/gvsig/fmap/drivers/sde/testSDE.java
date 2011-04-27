/*
 * Created on 13-may-2005
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.fmap.drivers.sde;

import java.util.Vector;

import com.esri.sde.sdk.client.SeColumnDefinition;
import com.esri.sde.sdk.client.SeConnection;
import com.esri.sde.sdk.client.SeException;
import com.esri.sde.sdk.client.SeLayer;
import com.esri.sde.sdk.client.SeObjectId;
import com.esri.sde.sdk.client.SeQuery;
import com.esri.sde.sdk.client.SeRow;
import com.esri.sde.sdk.client.SeShape;
import com.esri.sde.sdk.client.SeSqlConstruct;
import com.iver.andami.messages.NotificationManager;
import com.iver.cit.gvsig.fmap.core.GeneralPathX;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class testSDE {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        // Conexión:
        SeConnection conn = null;
        String server = "192.168.0.114";
        int instance = 5151;
        String database = "ProvinciasPruebas";
        String user = "sde";
        String password = "iver";

        try {
            conn = new SeConnection(server, instance, database, user, password);
        } catch (SeException e) {
        	NotificationManager.addError(e);

            return;
        }

        // Fetching data
        String layerName = "EJES";
        SeObjectId layerID = null;
        String strSpatialColumn = "";

        try {
            Vector theLayers = conn.getLayers();

            for (int i = 0; i < theLayers.size(); i++) {
                SeLayer layer = (SeLayer) theLayers.elementAt(i);

                if (layer.getName().equals(layerName)) {
                    layerID = layer.getID();
                    strSpatialColumn = layer.getSpatialColumn();
                    System.err.println("Nombre de la capa= " + layer.getName());

                    for (int k = 0; k < layerID.longValue(); k++) {
                        layer.getAccess();
                        layer.getQualifiedName();
                        layer.getArraySize();
                        layer.getDescription();
                        layer.getInfo();
                        layer.getShapeTypes();

                        //                    	try{
                        System.err.println("Nombre campo= " +
                            layerID.longValue());

                        //                    	} catch( SeException e ) {
                        //                    		//System.out.println(e.getSeError().getErrDesc());
                        //                    	}
                    }
                }
            }

            if (layerID == null) {
                System.err.println("Capa no encontrada");

                return;
            }

            SeLayer layer = new SeLayer(conn, layerName, strSpatialColumn);
            SeSqlConstruct sqlConstruct = new SeSqlConstruct(layerName);
            long t1 = System.currentTimeMillis();

            //      Create a query stream between the client and server
            String[] cols = new String[2];
            cols[0] = new String("FID");
            cols[1] = layer.getSpatialColumn();

            SeQuery query = new SeQuery(conn, cols, sqlConstruct);
            query.prepareQuery();
            query.execute();

            long t2 = System.currentTimeMillis();

            System.out.println("Tiempo de consulta:" + (t2 - t1) +
                " milisegundos");
            t1 = System.currentTimeMillis();

            int cont = 0;
            SeRow row = query.fetch();

            if (row == null) {
                System.out.println(" No rows fetched");

                return;
            }

            // String rowID = "2";
            // Get the definitions of all the columns retrieved
            SeColumnDefinition[] colDefs = row.getColumns();

            while (row != null) {
                evaluateRow(row, colDefs);
                row = query.fetch();
                cont++;
            }

            // Close the query.
            query.close();
            t2 = System.currentTimeMillis();

            System.out.println("Tiempo de recorrido:" + (t2 - t1) +
                " milisegundos. " + cont + " registros.");

            /* SeQuery extentQuery = new SeQuery( conn, cols, sqlConstruct );
            SeQueryInfo queryInfo = new SeQueryInfo();
            queryInfo.setConstruct(sqlConstruct);
            // queryInfo.setQueryType(SeQueryInfo.SE_QUERYTYPE_JFA);
            // query.prepareQueryInfo(queryInfo);

            SeExtent seExtent = extentQuery.calculateLayerExtent(queryInfo);
            extentQuery.close();
            System.out.println(seExtent.toString());


            SeQuery queryAux;
            t1 = System.currentTimeMillis();
            // queryAux = new SeQuery( conn, cols, sqlConstruct );
            for (int i=0; i < 250; i++)
            {
                queryAux = new SeQuery( conn, cols, sqlConstruct );
                SeObjectId rowID = new SeObjectId(i+1);
                row = queryAux.fetchRow("provin", rowID, cols);

                evaluateRow(row, colDefs);
                queryAux.close();
            }
            // queryAux.close();
            t2 = System.currentTimeMillis();
            System.out.println("Tiempo de recorrido:"  + (t2 - t1) + " milisegundos. "); */
            /* queryAux = new SeQuery( conn, cols, sqlConstruct );
            SeObjectId rowID = new SeObjectId(1);
            row = queryAux.fetchRow("provin", rowID, cols);
            evaluateRow(row, colDefs);
            row = queryAux.fetch();
            evaluateRow(row, colDefs);

            queryAux.close(); */
        } catch (SeException e) {
            System.out.println(e.getSeError().getErrDesc());
        }
    }

    static GeneralPathX convertSeShapeToGeneralPathX(SeShape spVal)
        throws SeException {
        double[][][] points = spVal.getAllCoords();
        GeneralPathX gpx = new GeneralPathX();

        // Display the X and Y values
        boolean bStartPart;

        for (int partNo = 0; partNo < points.length; partNo++) {
            bStartPart = true;

            for (int subPartNo = 0; subPartNo < points[partNo].length;
                    subPartNo++)
                for (int pointNo = 0;
                        pointNo < points[partNo][subPartNo].length;
                        pointNo += 2) {
                    if (bStartPart) {
                        bStartPart = false;
                        gpx.moveTo(points[partNo][subPartNo][pointNo],
                            points[partNo][subPartNo][(pointNo + 1)]);
                    } else {
                        gpx.lineTo(points[partNo][subPartNo][pointNo],
                            points[partNo][subPartNo][(pointNo + 1)]);
                    }
                }
        }

        return gpx;
    }

    static void evaluateRow(SeRow row, SeColumnDefinition[] colDefs) {
        try {
            for (int colNum = 0; colNum < colDefs.length; colNum++) {
                SeColumnDefinition colDef = colDefs[colNum];
                int dataType = colDef.getType();

                if (row.getIndicator((short) colNum) != SeRow.SE_IS_NULL_VALUE) {
                    switch (dataType) {
                    case SeColumnDefinition.TYPE_SMALLINT:
                        break;

                    case SeColumnDefinition.TYPE_DATE:
                        break;

                    case SeColumnDefinition.TYPE_INTEGER:
                        break;

                    case SeColumnDefinition.TYPE_FLOAT:
                        break;

                    case SeColumnDefinition.TYPE_DOUBLE:
                        break;

                    case SeColumnDefinition.TYPE_STRING:
                    case SeColumnDefinition.TYPE_NSTRING:

                        // System.out.println(row.getString(colNum));
                        break;

                    case SeColumnDefinition.TYPE_SHAPE:

                        SeShape spVal = row.getShape(colNum);
                        convertSeShapeToGeneralPathX(spVal);

                        // GeneralPath gp = spVal.toGeneralPath();
                        // GeneralPathX gpx = new GeneralPathX(gp);
                        // System.out.println("spVal.FID = " + spVal.getFeatureId().longValue());
                        // getShapeDetails(spVal);
                        break;
                    } // End switch
                } // End if
            } // for
        } catch (SeException e) {
        	NotificationManager.addError(e);
        }
    }
}
