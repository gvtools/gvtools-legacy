package com.iver.cit.gvsig.project.documents.view;

import java.awt.Color;
import java.awt.Component;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.gvsig.layer.Layer;
import org.gvsig.map.ErrorListener;
import org.gvsig.map.MapContext;
import org.gvsig.map.MapContextFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.view.toolListeners.LinkListener;

public abstract class ProjectViewBase extends ProjectDocument implements
		ErrorListener, IProjectView {

	@Inject
	protected MapContextFactory mapContextFactory;

	protected MapContext mapOverViewContext;
	protected MapContext mapContext;
	protected int m_typeLink = LinkListener.TYPELINKIMAGE;
	protected String m_extLink;
	protected String m_selectedField = null;

	// OVERRIDE THESE
	public IWindow getProperties() {
		return null;
	}

	/**
	 * Gets the FMap's contexts of the main map in the view.
	 * 
	 * @return
	 */
	public MapContext getMapContext() {
		return mapContext;
	}

	/**
	 * Gets the FMap's context from the locator, which is the small map in the
	 * left-bottom corner of the View.
	 * 
	 * @return
	 */
	public MapContext getMapOverViewContext() {
		return mapOverViewContext;
	}

	/**
	 * @see com.iver.cit.gvsig.project.documents.view.ProjectView#setMapContext(com.iver.cit.gvsig.fmap.MapContext)
	 */
	public void setMapContext(MapContext fmap) {
		mapContext = fmap;
		fmap.addErrorListener(this);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param fmap
	 *            DOCUMENT ME!
	 */
	public void setMapOverViewContext(MapContext fmap) {
		mapOverViewContext = fmap;
		mapOverViewContext.setCRS(mapContext.getCRS());
	}

	public void showErrors() {
		if (mapContext.getLayersError().size() > 0) {
			String layersError = "";
			for (int i = 0; i < mapContext.getLayersError().size(); i++) {
				layersError = layersError + "\n"
						+ (String) mapContext.getLayersError().get(i);
			}
			JOptionPane.showMessageDialog(
					(Component) PluginServices.getMainFrame(),
					PluginServices.getText(this, "fallo_capas") + " : \n"
							+ layersError);
		}
	}

	public CoordinateReferenceSystem getCrs() {
		return mapContext.getCRS();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public CoordinateReferenceSystem getOverViewCrs() {
		return mapOverViewContext.getCRS();
	}

	public void setCrs(CoordinateReferenceSystem crs) {
		mapContext.setCRS(crs);
		mapOverViewContext.setCRS(crs);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getExtLink() {
		return m_extLink;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public int getTypeLink() {
		return m_typeLink;
	}

	/**
	 * Se selecciona la extensi�n para realizar cuando se quiera el link.
	 * 
	 * @param s
	 *            nombre del campo.
	 */
	public void setExtLink(String s) {
		m_extLink = s;
	}

	/**
	 * Se selecciona el tipo de fichero para realizar cuando se quiera el link.
	 * 
	 * @param i
	 *            tipo de fichero.
	 */
	public void setTypeLink(int i) {
		m_typeLink = i;
	}

	public void afterRemove() {
		assert false : "Create a test that checks that "
				+ "removing a view removes the tables "
				+ "associated with their layers. Let's implement"
				+ "that with an EventBus";
	}

	public void afterAdd() {
		// TODO Auto-generated method stub

	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param c
	 *            DOCUMENT ME!
	 */
	public void setBackColor(Color c) {
		// getMapContext().getViewPort().addViewPortListener(getMapContext()
		// .getEventBuffer());
		getMapContext().setBackColor(c);
		// getMapContext().getViewPort().removeViewPortListener(getMapContext()
		// .getEventBuffer());
	}

	/**
	 * Se selecciona el nombre del campo para realizar cuando se quiera el link.
	 * 
	 * @param s
	 *            nombre del campo.
	 */
	public void setSelectedField(String s) {
		m_selectedField = s;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getSelectedField() {
		return m_selectedField;
	}

	public boolean isLocked() {
		if (super.isLocked())
			return true;
		Layer[] layers = getMapContext().getRootLayer().getAllLayers();
		for (Layer layer : layers) {
			if (layer.isEditing()) {
				return true;
			}
		}
		return false;
	}

}
