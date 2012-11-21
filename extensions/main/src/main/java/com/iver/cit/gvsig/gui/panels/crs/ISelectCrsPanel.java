package com.iver.cit.gvsig.gui.panels.crs;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.andami.ui.mdiManager.IWindow;

public interface ISelectCrsPanel extends IWindow {
	public CoordinateReferenceSystem getCrs() throws NoSuchAuthorityCodeException, FactoryException;

	public void setCrs(CoordinateReferenceSystem crs) throws NoSuchAuthorityCodeException, FactoryException;

	public boolean isOkPressed();
}
