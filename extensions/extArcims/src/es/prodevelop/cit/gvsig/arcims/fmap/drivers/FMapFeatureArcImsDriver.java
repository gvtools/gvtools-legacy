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
package es.prodevelop.cit.gvsig.arcims.fmap.drivers;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.ArcImsClientP;
import org.gvsig.remoteClient.arcims.ArcImsFeatureClient;
import org.gvsig.remoteClient.arcims.ArcImsStatus;
import org.gvsig.remoteClient.arcims.ArcImsVectStatus;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.utils.FieldInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerFeatures;
import org.gvsig.remoteClient.exceptions.ServerErrorException;
import org.gvsig.remoteClient.utils.BoundaryBox;
import org.gvsig.remoteClient.wms.ICancellable;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.driver.exceptions.ReloadDriverException;
import com.hardcode.gdbms.driver.exceptions.WriteDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.data.driver.ObjectDriver;
import com.hardcode.gdbms.engine.data.edition.DataWare;
import com.hardcode.gdbms.engine.data.object.ObjectSourceInfo;
import com.hardcode.gdbms.engine.values.IntValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.core.DefaultFeature;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.IGeometry;
import com.iver.cit.gvsig.fmap.core.ShapeFactory;
import com.iver.cit.gvsig.fmap.drivers.DriverAttributes;
import com.iver.cit.gvsig.fmap.drivers.VectorialDriver;
import com.iver.cit.gvsig.fmap.edition.VectorialEditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.gui.View;

import es.prodevelop.cit.gvsig.arcims.fmap.layers.FFeatureLyrArcIMS;

public class FMapFeatureArcImsDriver implements ArcImsDriver, VectorialDriver,
		ObjectDriver {
	private static Logger logger = Logger
			.getLogger(FMapFeatureArcImsDriver.class.getName());
	private ArcImsFeatureClient client;
	private String server;
	private String service;
	private String serviceType;
	private String remoteLayerName;
	private ArcImsAttributesDataSourceAdapter attsDataSourceAdapter;
	private SelectableDataSource dataSource = null;
	private DriverAttributes drvAtts;
	private ArrayList geometries;
	private HashMap pseudoGeometries;
	private int shapeType = FShape.NULL;
	private FFeatureLyrArcIMS layer;
	private HashMap overallIndexToVisibleIndex;
	private HashMap visibleIndexToOverallIndex;
	private Rectangle2D fullExtent;
	private IGeometry invisiblePoint = ShapeFactory.createPoint2D(0.0, 0.0);
	private VectorialEditableAdapter eadapter;

	private ArcImsInMemoryAttsTableDriver dataTable;
	private DataSourceFactory dsFactory;

	/**
	 * The constructor needs the server's URL and the name of the service to be
	 * used.
	 * 
	 * @param host
	 *            server's URL
	 * @param service
	 *            name of the service, chosen among the ones retrieved after a
	 *            request with the parameter <tt>ServiceName=Catalog</tt>
	 */
	public FMapFeatureArcImsDriver(String host, String service,
			String theRemoteLayerName) {
		remoteLayerName = theRemoteLayerName;
		init(host, service);
		overallIndexToVisibleIndex = new HashMap();
		visibleIndexToOverallIndex = new HashMap();
		pseudoGeometries = new HashMap();

	}

	/**
	 * The constructor needs the server's URL and the name of the service to be
	 * used.
	 * 
	 * @param host
	 *            server's URL
	 * @param service
	 *            name of the service, chosen among the ones retrieved after a
	 *            request with the parameter <tt>ServiceName=Catalog</tt>
	 */
	public FMapFeatureArcImsDriver(String host, String service) {
		remoteLayerName = "None";
		init(host, service);
		overallIndexToVisibleIndex = new HashMap();
		visibleIndexToOverallIndex = new HashMap();
	}

	/**
	 * This method is called by the constructor and creates the <tt>client</tt>
	 * object.
	 * 
	 * @param host
	 * @param service
	 */
	public void init(String host, String svc) {
		server = host;
		service = svc;

		// This class is part of libArcIMS:
		client = new ArcImsFeatureClient(host, service,
				ServiceInfoTags.vFEATURESERVICE);
	}

	private String getIdName() {
		String id = (String) this.layer.getArcimsStatus().getLayerIds().get(0);
		ServiceInformationLayerFeatures silf = (ServiceInformationLayerFeatures) client
				.getServiceInformation().getLayerById(id);

		ArrayList fi = silf.getFieldInformationByType(FieldInformation.ID);

		if (fi.size() != 1) {
			logger.error("ID does not have cardinality one: " + fi.size());
		}

		return ((FieldInformation) fi.get(0)).getName();
	}

	private ArrayList getClientsColumnNames() {
		String id = (String) layer.getArcimsStatus().getLayerIds().get(0);
		ServiceInformationLayerFeatures silf = (ServiceInformationLayerFeatures) client
				.getServiceInformation().getLayerById(id);
		int length = silf.getFieldsInfo().size();

		ArrayList resp = new ArrayList();

		for (int i = 0; i < length; i++) {
			resp.add(silf.getFieldInformation(i).getName());
		}

		return resp;
	}

	private ArrayList getClientsColumnTypes() {
		String id = (String) layer.getArcimsStatus().getLayerIds().get(0);
		ServiceInformationLayerFeatures silf = (ServiceInformationLayerFeatures) client
				.getServiceInformation().getLayerById(id);
		int length = silf.getFieldsInfo().size();

		ArrayList resp = new ArrayList();

		for (int i = 0; i < length; i++) {
			resp.add(new Integer(silf.getFieldInformation(i).getType()));
		}

		return resp;
	}

	public void closeDataSource() {
		try {
			dataSource.stop();
		} catch (ReadDriverException e) {
			logger.error("While closing data source ", e);
		}
	}

	public SelectableDataSource getRecordSet() {
		return getRecordSet(null);
	}

	public SelectableDataSource getRecordSet(String mustHaveThisName) {
		if (dataSource != null) {
			return dataSource;
		}

		String[] subf = new String[1];
		subf[0] = getIdName();

		ArrayList ids = new ArrayList();

		try {
			// ids = getTestIds();
			ids = client.getAttributes(layer.getArcimsStatus(), subf, "", null);
		} catch (ArcImsException e1) {
			logger.error("While getting attributes initially ", e1);
		}

		Value[] idval = getAsValueArray(ids);

		setDataSourceFactory(LayerFactory.getDataSourceFactory());

		// dataSourceFactory.
		// FakeObjectDriver driver = new FakeObjectDriver();
		attsDataSourceAdapter = new ArcImsAttributesDataSourceAdapter(
				getClientsColumnNames(), getClientsColumnTypes(), idval,
				getDataSourceFactory(), this, mustHaveThisName);

		getDataSourceFactory().addDataSource(this,
				attsDataSourceAdapter.getTableName());

		ObjectSourceInfo osi = (ObjectSourceInfo) getDataSourceFactory()
				.getDriverInfo(attsDataSourceAdapter.getTableName());
		osi.name = attsDataSourceAdapter.getTableName();
		osi.driverName = "ArcImsAttributesDataSourceAdapter";

		try {
			dataSource = new SelectableDataSource(attsDataSourceAdapter);
		} catch (ReadDriverException e) {
			logger.error("While creating DataSource ", e);
		}

		return dataSource;
	}

	private DataSourceFactory getDataSourceFactory() {
		if (dsFactory == null) {
			return dataTable.getDataSourceFactory();
		}
		return dsFactory;
	}

	public void declareTable(AlphanumericData layer) {
		try {
			IWindow v = PluginServices.getMDIManager().getActiveWindow();

			if (v == null) {
				ProjectView pv = new ProjectView();
				pv.setName("Nueva");

				View newview = (View) pv.createWindow();
				newview.setModel(pv);

				Project newproj = new Project();
				newproj.setName("nuevo");
				newproj.addDocument(pv);

				// newproj.addView(newprojview);
				v = newview;
			}

			ProjectView pv = (ProjectView) ((View) v).getModel();
			ProjectTable pt = ProjectTableFactory.createTable(
					attsDataSourceAdapter.getName(), eadapter);
			pt.setProjectDocumentFactory(new ProjectTableFactory());
			pt.setAssociatedTable(layer);

			// String r = pt.getProjectDocumentFactory().getRegisterName();
			// ProjectTable pt =
			// ProjectFactory.createTable(attsDataSourceAdapter.getName(),
			// eadapter);
			// ProjectDocumentFactory.
			pv.getProject().addDocument(pt);
		} catch (Exception e) {
			logger.error("While creating DataSource ", e);
			e.printStackTrace();
		}
	}

	public void loadRecordSet() {
		if (dataSource != null) {
			return;
		}

		String[] subf = new String[1];
		subf[0] = getIdName();

		ArrayList ids = new ArrayList();

		try {
			// ids = getTestIds();
			ids = client.getAttributes(layer.getArcimsStatus(), subf, "", null);
		} catch (ArcImsException e1) {
			logger.error("While getting attributes initially ", e1);
		}

		Value[] idval = getAsValueArray(ids);

		setDataSourceFactory(LayerFactory.getDataSourceFactory());

		// dataSourceFactory.
		// FakeObjectDriver driver = new FakeObjectDriver();
		attsDataSourceAdapter = new ArcImsAttributesDataSourceAdapter(
				getClientsColumnNames(), getClientsColumnTypes(), idval,
				getDataSourceFactory(), this, null);
		getDataSourceFactory().addDataSource(this,
				attsDataSourceAdapter.getTableName());

		try {
			dataSource = new SelectableDataSource(attsDataSourceAdapter);
		} catch (ReadDriverException e) {
			logger.error("While loading selectable data source");
		}

	}

	private Value[] getAsValueArray(ArrayList ids) {
		Value[] resp = new Value[ids.size()];

		for (int i = 0; i < ids.size(); i++) {
			resp[i] = ((Value[]) ids.get(i))[0];
		}

		return resp;
	}

	public void getCapabilities(URL server, ICancellable cancel)
			throws ArcImsException, IOException, ProtocolException {
		logger.error("Empty method: nothing done in method getCapabilities(URL) - class: "
				+ getClass().toString());
	}

	public Object getMap(ArcImsStatus status) throws ArcImsException,
			IOException, ProtocolException {
		String[] fieldquery = ((ArcImsVectStatus) status).getSubfields();
		int nooffields = fieldquery.length;

		try {
			geometries = client.getMap((ArcImsVectStatus) status);

			if (geometries.size() > 0) {
				logger.info("Start updating attributes");
				updateIndexHashMaps(geometries, fieldquery, nooffields);
				logger.info("End updating attributes");
			} else {
				updateIndexHashMaps(geometries, fieldquery, nooffields);
				logger.warn("No geometries retrieved from client ");
			}
		} catch (ServerErrorException e) {
			ArcImsException aie = new ArcImsException(e.getMessage());
			logger.error("While getting map ", aie);
			throw aie;
		}

		return geometries;
	}

	// **************************
	//
	// **************************
	private void updateIndexHashMaps(ArrayList iFeatArrayList,
			String[] f_query, int n_of_fields) {
		int added_row_ind;

		// ------------ get id position --------------
		int idfieldind = -1;

		for (int i = 0; i < n_of_fields; i++) {
			if (f_query[i].compareTo(getIdName()) == 0) {
				idfieldind = i;
				break;
			}
		}

		if (idfieldind == -1) {
			logger.error("ID not found in query ");
		}

		// -------------------------------------------
		IntValue id;

		try {
			overallIndexToVisibleIndex.clear();
			visibleIndexToOverallIndex.clear();

			for (int i = 0; i < iFeatArrayList.size(); i++) {
				IFeature ifeat = (IFeature) iFeatArrayList.get(i);
				id = (IntValue) ifeat.getAttribute(idfieldind);
				added_row_ind = attsDataSourceAdapter
						.getRowIndex(id.getValue());

				addPseudoGeometry(ifeat.getGeometry(), added_row_ind);
				overallIndexToVisibleIndex.put(new Integer(added_row_ind),
						new Integer(i));
				visibleIndexToOverallIndex.put(new Integer(i), new Integer(
						added_row_ind));
			}

			// }
		} catch (DriverException e) {
			logger.error("While updating attributes ", e);
		}
	}

	public String getFeatureInfo(ArcImsStatus status, int i, int j,
			int max_value) throws ArcImsException, IOException,
			ProtocolException {
		// TODO comment
		return "";
	}

	public boolean connect(ICancellable cancel) {
		return client.connect(false, cancel);
	}

	/**
	 * Gets available layers from the current server and service
	 * 
	 * @return a TreeMap with available layers
	 */
	public TreeMap getLayers() {
		return null;
		// This method is in libArcIMS
		// return client.getLayers();
	}

	/**
	 * Gets the <tt>ArcImsClientP client</tt> object, onto which requests are
	 * passed.
	 * 
	 * @return the inner object that actually performs requests.
	 */
	public ArcImsClientP getClient() {
		if (client == null) {
			init(server, service);
		}

		return client;
	}

	// ========== VECTORIAL DRIVER INTERFACE == START =======
	public int getShapeType() {
		if (shapeType == FShape.NULL) {
			ServiceInformationLayerFeatures silf = (ServiceInformationLayerFeatures) client
					.getServiceInformation().getLayerById(remoteLayerName);

			shapeType = libArcImsShapeTypeToGvSigType(silf.getIntFclassType());
		}

		return shapeType;
	}

	public int getShapeCount() throws ReadDriverException {
		try {
			// return geometries.size();
			return (int) getRecordSet().getRowCount();
		} catch (ReadDriverException e) {
			ReadDriverException ioe = new ReadDriverException(
					"In getShapeCount: ", e);
			throw ioe;
		}
	}

	public DriverAttributes getDriverAttributes() {
		if (drvAtts != null) {
			return drvAtts;
		}

		drvAtts = new DriverAttributes();
		drvAtts.setLoadedInMemory(true);

		return drvAtts;
	}

	public Rectangle2D getFullExtent() throws ReadDriverException {
		return fullExtent;
	}

	// public IGeometry getShape(int index) throws IOException {
	// return ((IFeature) geometries.get(index)).getGeometry();
	// }
	private void addPseudoGeometry(IGeometry igeom, int row) {
		pseudoGeometries.put(new Integer(row), igeom);
	}

	public IGeometry getShape(int index) throws ReadDriverException {
		Integer overallIndex = new Integer(index);
		Integer visibleIndex = (Integer) overallIndexToVisibleIndex
				.get(overallIndex);

		if (visibleIndex == null) {
			// Somebody asked for a non-visible shape.
			IGeometry igeo = (IGeometry) pseudoGeometries.get(overallIndex);

			if (igeo != null) {
				return igeo;
			}

			// We dont have the geometry and we dont have its pseudo envelope
			// (this happens when loading a project with a selection and the
			// user
			// performs a zoom to selection)
			String id = layer.getLayerQuery();
			BoundaryBox bb = client.getServiceInformation().getLayerById(id)
					.getEnvelope();

			double[] fullx = new double[4];
			double[] fully = new double[4];

			fullx[0] = bb.getXmin();
			fullx[1] = bb.getXmin();
			fullx[2] = bb.getXmax();
			fullx[3] = bb.getXmax();
			fully[0] = bb.getYmin();
			fully[1] = bb.getYmax();
			fully[2] = bb.getYmin();
			fully[3] = bb.getYmax();

			IGeometry full = ShapeFactory.createMultipoint2D(fullx, fully);

			return full;
		} else {
			return ((IFeature) geometries.get(visibleIndex.intValue()))
					.getGeometry();
		}
	}

	public String getName() {
		return "FMapFeatureArcImsDriver";
	}

	// ========== VECTORIAL DRIVER INTERFACE == END =======

	// private void setTestGeometries() {
	// IGeometry g;
	// File wktfile = null;
	// JFileChooser jfc = new JFileChooser();
	// try {
	// jfc.setDialogTitle("Indice archivo WKT con " + getShapeCount() +
	// " geometrias");
	// } catch (IOException e) {
	// logger.error("Error while opening WKT file. ", e);
	// }
	// int returnVal = jfc.showOpenDialog(null);
	// if (returnVal != JFileChooser.APPROVE_OPTION) return;
	// wktfile = jfc.getSelectedFile();
	// if (! jfc.accept(wktfile)) return;
	// WKTFGeometryReader reader = new WKTFGeometryReader(wktfile);
	// g = reader.getNextFGeometry();
	// geometries = new ArrayList();
	// while (g != null) {
	// geometries.add(g);
	// g = reader.getNextFGeometry();;
	// }
	// }
	public void setFullExtent(Rectangle2D fullExtent) {
		this.fullExtent = fullExtent;
	}

	public FFeatureLyrArcIMS getLayer() {
		return layer;
	}

	public void setLayer(FFeatureLyrArcIMS layer) {
		this.layer = layer;
	}

	// private ArrayList getTestIds() throws ArcImsException {
	// Value[] ids = new Value[252];
	//
	// for (int i=0; i<252; i++) {
	// ids[i] = ValueFactory.createValue(i);
	// }
	// ArrayList resp = new ArrayList();
	// resp.add(ids);
	// return resp;
	// }
	public int getOverallIndex(int i) {
		Integer ov = (Integer) visibleIndexToOverallIndex.get(new Integer(i));

		if (ov == null) {
			return -1;
		} else {
			return ov.intValue();
		}

	}

	public void requestFeatureAttributes(FBitSet fbs) throws ArcImsException {
		if (fbs.cardinality() == 0) {
			return;
		}

		FBitSet needRequest = leaveBitsThatNeedRequest(fbs);

		requestFeatureAttributesWithoutChecking(needRequest);
	}

	public void requestBlockWithoutChecking(int[] indlimits)
			throws ArcImsException {
		FBitSet fbs = new FBitSet();
		fbs.set(indlimits[0], indlimits[1] + 1);

		int length = fbs.cardinality();

		if (length == 0) {
			return;
		}

		int[] rowinds = new int[length];

		try {
			int[] req_indices = enumarate(indlimits[0], indlimits[1]);
			int sz = req_indices.length;
			int[] req_ids = new int[sz];
			for (int i = 0; i < sz; i++)
				req_ids[i] = attsDataSourceAdapter.getRowId(req_indices[i]);

			int idcolindex = attsDataSourceAdapter.getIdIndex();
			String idFieldName = attsDataSourceAdapter
					.getOriginalFieldName(idcolindex);
			String inParenthesis = getInIntParenthesis(req_ids);
			String whereClause = idFieldName + " " + inParenthesis;

			String[] subflds = new String[1];
			subflds[0] = "#ALL#";

			logger.debug("Justo antes de llamar a client.getAttributes(...)");

			ArrayList atts = client.getAttributesWithEnvelope(
					layer.getArcimsStatus(), subflds, whereClause, null);

			rowinds = getRowIndicesFromResponse(atts, idcolindex);

			// --------------------------------------------------------
			logger.debug("Justo despues de llamar a client.getAttributes(...)");

			logger.debug("Justo antes de llamar a attsDataSourceAdapter.updateRow(...) "
					+ atts.size() + " veces");

			for (int i = (atts.size() - 1); i >= 0; i--) {
				Value[] newrow = ((DefaultFeature) atts.get(i)).getAttributes();

				attsDataSourceAdapter.updateRow(newrow, subflds, rowinds[i]);
			}

			// -------------- pseudo geometries:
			for (int i = (atts.size() - 1); i >= 0; i--) {
				IGeometry ig = ((DefaultFeature) atts.get(i)).getGeometry();
				addPseudoGeometry(ig, rowinds[i]);
			}

			// --------------
			logger.debug("Justo despues de llamar a attsDataSourceAdapter.updateRow(...)");
			logger.debug("Justo antes de llamar a attsDataSourceAdapter.addAsRequested(...)");

			attsDataSourceAdapter.addAsRequested(fbs);

			logger.debug("Justo despues de llamar a attsDataSourceAdapter.addAsRequested(...)");
		} catch (DriverException e) {
			ArcImsException aie = new ArcImsException("datasource_error");
			logger.error("While requesting features ", aie);
			throw aie;
		}
	}

	private int[] enumarate(int a, int b) {

		int[] resp = null;
		if (a == b) {
			resp = new int[1];
			resp[0] = a;
		} else {

			resp = new int[Math.abs(b - a + 1)];
			if (a < b) {
				for (int i = a; i <= b; i++)
					resp[i - a] = i;
			} else {
				for (int i = b; i <= a; i++)
					resp[i - b] = i;
			}
		}
		return resp;

	}

	private int[] getRowIndicesFromResponse(ArrayList resp_list, int id_col_ind) {

		int sz = resp_list.size();
		int[] resp = new int[sz];
		int row_ind = 0;
		for (int i = 0; i < sz; i++) {
			Value[] item_row = ((DefaultFeature) resp_list.get(i))
					.getAttributes();
			IntValue idv = (IntValue) item_row[id_col_ind];
			row_ind = idToRow(idv.intValue());
			resp[i] = row_ind;
		}
		return resp;
	}

	private int idToRow(int i) {
		try {
			return attsDataSourceAdapter.getRowIndex(i);
		} catch (DriverException e) {
			logger.error("While getting row index of id: " + i + " : "
					+ e.getMessage());
			return 0;
		}
	}

	public void requestFeatureAttributesWithoutChecking(FBitSet fbs)
			throws ArcImsException {
		int length = fbs.cardinality();

		if (length == 0) {
			return;
		}

		int rowcount = 0;
		int firstnonreq = 0;

		try {
			rowcount = (int) attsDataSourceAdapter.getRowCount();
		} catch (ReadDriverException e1) {
			logger.error("Unexpected error while getting row count ");

			return;
		}

		firstnonreq = fbs.nextClearBit(fbs.nextSetBit(0));

		if (firstnonreq >= rowcount) {
			firstnonreq = 0;
		}

		if (length < 10) {
			fbs.or(attsDataSourceAdapter.getNonRequestedFromHere(firstnonreq,
					attsDataSourceAdapter.getRowsPerRequest() - length));
			length = fbs.cardinality();
		}

		int[] ids = new int[length];
		int[] rowinds = new int[length];
		int idind = 0;

		try {
			for (int i = fbs.nextSetBit(0); i >= 0; i = fbs.nextSetBit(i + 1)) {
				ids[idind] = attsDataSourceAdapter.getRowId(i);
				rowinds[idind] = i;
				idind++;
			}

			String inParenthesis = getInIntParenthesis(ids);
			int idcolindex = attsDataSourceAdapter.getIdIndex();
			String idFieldName = attsDataSourceAdapter
					.getOriginalFieldName(idcolindex);
			String whereClause = idFieldName + " " + inParenthesis;
			String[] subflds = new String[1];
			subflds[0] = "#ALL#";

			// -------------- with geometries -------------------------
			ArrayList atts = client.getAttributesWithEnvelope(
					layer.getArcimsStatus(), subflds, whereClause, null);

			// ArrayList atts = client.getAttributes(layer.getArcimsStatus(),
			// subflds, whereClause, null);
			// --------------------------------------------------------
			for (int i = (atts.size() - 1); i >= 0; i--) {
				IGeometry ig = ((DefaultFeature) atts.get(i)).getGeometry();
				addPseudoGeometry(ig, rowinds[i]);
			}

			// --------------
			for (int i = (atts.size() - 1); i >= 0; i--) {
				Value[] newrow = ((DefaultFeature) atts.get(i)).getAttributes();

				// Value[] newrow = (Value[]) atts.get(i);
				attsDataSourceAdapter.updateRow(newrow, subflds, rowinds[i]);
			}

			attsDataSourceAdapter.addAsRequested(fbs);
		} catch (DriverException e) {
			ArcImsException aie = new ArcImsException("datasource_error");
			logger.error("While requesting features ", aie);
			throw aie;
		}
	}

	private FBitSet leaveBitsThatNeedRequest(FBitSet fbs) {
		FBitSet resp = (FBitSet) fbs.clone();

		for (int i = resp.nextSetBit(0); i >= 0; i = resp.nextSetBit(i + 1)) {
			if (!attsDataSourceAdapter.isNonRequestedRow(i)) {
				resp.clear(i);
			}
		}

		return resp;
	}

	private String getInIntParenthesis(int[] ids) {
		String resp = "IN (";

		for (int i = 0; i < ids.length; i++) {
			resp = resp + " " + ids[i] + ",";
		}

		resp = resp.substring(0, resp.length() - 1);
		resp = resp + ")";

		return resp;
	}

	private int libArcImsShapeTypeToGvSigType(int type) {
		int resp = type;

		switch (type) {
		case FShape.MULTIPOINT:
			resp = FShape.POINT;

			break;
		}

		return resp;
	}

	public void setAdapter(VectorialEditableAdapter adapter) {
		eadapter = adapter;
	}

	public VectorialEditableAdapter getAdapter() {
		return eadapter;
	}

	public void reload() throws ReloadDriverException {
	}

	public boolean isWritable() {
		return false;
	}

	public String getRemoteLayerName() {
		return remoteLayerName;
	}

	public void setRemoteLayerName(String remoteLayerName) {
		this.remoteLayerName = remoteLayerName;
	}

	public int[] getPrimaryKeys() throws ReadDriverException {
		// TODO Auto-generated method stub
		return dataTable.getPrimaryKeys();
	}

	public void write(DataWare dataWare) throws WriteDriverException,
			ReadDriverException {
		// TODO Auto-generated method stub

	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
		dsFactory = dsf;
		this.getDataSourceFactory();
		if (dataTable != null) {
			dataTable.setDataSourceFactory(dsf);
		}
	}

	public int getFieldCount() throws ReadDriverException {
		// TODO Auto-generated method stub
		return dataTable.getFieldCount();
	}

	public String getFieldName(int i) throws ReadDriverException {
		// TODO Auto-generated method stub
		return dataTable.getFieldName(i);
	}

	public int getFieldType(int i) throws ReadDriverException {
		// TODO Auto-generated method stub
		return dataTable.getFieldType(i);
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws ReadDriverException {
		// TODO Auto-generated method stub
		return dataTable.getFieldValue(rowIndex, fieldId);
	}

	public int getFieldWidth(int i) throws ReadDriverException {
		return dataTable.getFieldWidth(i);
	}

	public long getRowCount() throws ReadDriverException {
		// TODO Auto-generated method stub
		return dataTable.getRowCount();
	}

	public void setDataTable(ArcImsInMemoryAttsTableDriver dt) {
		dataTable = dt;
	}

	public ArcImsInMemoryAttsTableDriver getDataTable() {
		return dataTable;
	}

	public String[] gvSigNamesToServerNames(String[] _flds) {
		return this.dataTable.gvSigNamesToServerNames(_flds);
	}

}
