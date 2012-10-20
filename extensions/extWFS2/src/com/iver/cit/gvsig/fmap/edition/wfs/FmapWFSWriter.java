package com.iver.cit.gvsig.fmap.edition.wfs;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.gvsig.gpe.exceptions.WriterHandlerCreationException;
import org.gvsig.remoteClient.wfs.edition.WFSTTransaction;
import org.gvsig.remoteClient.wfs.exceptions.WFSException;

import com.hardcode.gdbms.engine.data.driver.DriverException;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.drivers.wfs.FMapWFSDriver;
import com.iver.cit.gvsig.fmap.edition.IRowEdited;
import com.iver.cit.gvsig.fmap.edition.ISpatialWriter;
import com.iver.cit.gvsig.fmap.edition.writers.AbstractWriter;

/**
 * @author Jorge Piera LLodrá (jorge.piera@iver.es)
 */
public class FmapWFSWriter extends AbstractWriter implements ISpatialWriter {
	private WFSTTransaction transaction = null;
	private FieldDescription[] fields;
	private boolean isWfstEditing;
	private FMapWFSDriver driver = null;
	// Used to manage the feature identifier
	private int idPosition = -1;
	private boolean isGid = true;

	public FmapWFSWriter(FMapWFSDriver driver, boolean isWfstEditing) {
		super();
		this.driver = driver;
		setWfstEditing(isWfstEditing);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.edition.writers.AbstractWriter#canWriteAttribute
	 * (int)
	 */
	public boolean canWriteAttribute(int sqlType) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.edition.ISpatialWriter#canWriteGeometry(int)
	 */
	public boolean canWriteGeometry(int gvSIGgeometryType) {
		switch (gvSIGgeometryType) {
		case FShape.POINT:
			return true;
		case FShape.LINE:
			return true;
		case FShape.POLYGON:
			return true;
		case FShape.MULTIPOINT:
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.edition.IWriter#canAlterTable()
	 */
	public boolean canAlterTable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.edition.IWriter#canSaveEdits()
	 */
	public boolean canSaveEdits() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.edition.IWriter#postProcess()
	 */
	public void postProcess() {
		if (isWfstEditing) {
			try {
				driver.getRemoteServicesClient().transaction(
						driver.getStatus(), false, null);
			} catch (WFSException e) {
				// TODO The edition message has to be showed
				// in the GUI part. This message has to be
				// deleted
				String message = "";
				if (e.getWfsCode() != null) {
					message = "CODE: " + e.getWfsCode() + "\n";
				}
				if (e.getMessage() != null) {
					message = e.getMessage();
				}
				JOptionPane.showMessageDialog((Component) PluginServices
						.getMDIManager().getActiveWindow(), message,
						PluginServices.getText(this, "wfst_transaction_error"),
						JOptionPane.ERROR_MESSAGE);
				// Unlock the layer
				preProcess();
				try {
					driver.getRemoteServicesClient().transaction(
							driver.getStatus(), false, null);
				} catch (WFSException e1) {
					e.printStackTrace();
					// Impossible to unlock the layer...
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.edition.IWriter#preProcess()
	 */
	public void preProcess() {
		if (isWfstEditing) {
			transaction = driver.getStatus().createTransaction(
					driver.getRemoteServicesClient().getVersion());
			// GPEDefaults.setProperty(GPEDefaults.SRS_BASED_ON_XML,
			// new Boolean(driver.getStatus().isSRSBasedOnXML()));
		}
		fields = getTableDefinition().getFieldsDesc();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.edition.IWriter#process(com.iver.cit.gvsig.fmap
	 * .edition.IRowEdited)
	 */
	public void process(IRowEdited row) {
		if (isWfstEditing) {
			Value idFeature = row.getAttribute(driver.getFieldCount() - 1);
			switch (row.getStatus()) {
			case IRowEdited.STATUS_ADDED:
				try {
					transaction.addInsertOperation(GMLEditionUtils
							.getInsertQuery(row, fields, null,
									driver.getStatus(),
									driver.getRemoteServicesClient()));
				} catch (WriterHandlerCreationException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case IRowEdited.STATUS_MODIFIED:
				transaction.addUpdateOperation(idFeature.toString(),
						GMLEditionUtils.getUpdateQuery(row, fields, null,
								driver.getStatus(),
								driver.getRemoteServicesClient()));
				break;
			case IRowEdited.STATUS_ORIGINAL:

				break;
			case IRowEdited.STATUS_DELETED:
				transaction.addDeleteOperation(idFeature.toString());
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hardcode.driverManager.Driver#getName()
	 */
	public String getName() {
		return "WFST Driver";
	}

	/**
	 * It locks all the features
	 * 
	 * @param expiryTime
	 *            The maximum time to edit
	 */
	public void lockCurrentFeatures(int expiryTime)
			throws WFSTLockFeaturesException {
		// Remove the previously locked features
		driver.getStatus().removeFeaturesToLock();
		driver.getStatus().setExpiry(expiryTime);
		try {
			// Lock features by area doens't work
			// XMLElement element =
			// GMLEditionUtils.getGeometry(driver.getStatus(),
			// driver.getRemoteServicesClient());
			// if (element != null){
			// driver.getStatus().setLockedArea(driver.getFullExtent(),
			// element.getName());
			// }
			String idAttribute = driver.getFieldName(getRowID());
			idAttribute = driver.getStatus().getNamespacePrefix() + ":"
					+ idAttribute;
			for (int i = 0; i < driver.getShapeCount(); i++) {
				Value idFeature = driver.getFieldValue(i, getRowID());
				if (isGid) {
					driver.getStatus().addFeatureToLock(idFeature.toString());
				} else {
					driver.getStatus().addFeatureToLock(idAttribute,
							idFeature.toString());
				}
			}
			driver.getRemoteServicesClient().lockFeature(driver.getStatus(),
					false, null);
		} catch (Exception e) {
			throw new WFSTLockFeaturesException(e);
		}
	}

	/**
	 * @param isWfstEditing
	 *            the isWfstEditing to set
	 */
	public void setWfstEditing(boolean isWfstEditing) {
		this.isWfstEditing = isWfstEditing;
	}

	/**
	 * @return the row that contains the feature ID
	 * @throws DriverException
	 */
	private int getRowID() throws DriverException {
		if (idPosition == -1) {
			for (int i = 0; i < driver.getFieldCount(); i++) {
				String fieldName = driver.getFieldName(i);
				if (fieldName != null) {
					if (fieldName.compareTo("objectidglds") == 0) {
						idPosition = i;
						isGid = false;
					}
				}
			}
			if (idPosition == -1) {
				idPosition = driver.getFieldCount() - 1;
				isGid = true;
			}
		}
		return idPosition;
	}
}
