package com.iver.cit.gvsig.project.documents.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.gvsig.exceptions.BaseException;
import org.gvsig.map.ErrorListener;
import org.gvsig.map.MapContext;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.ProjectExtension;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.view.info.gui.HTMLInfoToolPanel;
import com.iver.cit.gvsig.project.documents.view.toolListeners.LinkListener;

public abstract class ProjectViewBase extends ProjectDocument implements
		ErrorListener, IProjectView {

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

	/**
	 * Reports to the user a bundle of driver exceptions produced in the same
	 * atomic MapContext transaction
	 */
	public void reportDriverExceptions(String introductoryText,
			List driverExceptions) {
		HtmlWindow htmlPanel = new HtmlWindow(570, 600, PluginServices.getText(
				this, "driver_error"));
		String htmlText = "";
		if (introductoryText == null) {
			htmlText += "<h2 text=\"#000080\">"
					+ PluginServices
							.getText(this,
									"se_han_producido_los_siguientes_errores_durante_la_carga_de_las_capas")
					+ "</h2>";
		} else {
			htmlText += introductoryText;
		}
		int numErrors = driverExceptions.size();
		for (int i = 0; i < numErrors; i++) {
			// htmlText += "<br>\n";
			BaseException exception = (BaseException) driverExceptions.get(i);
			htmlText += "<p text=\"#550080\">_________________________________________________________________________________________</p>";
			htmlText += "<h3>"
					+ PluginServices.getText(this, exception.getMessageKey())
					+ "</h3>";
			htmlText += "<p>" + exception.getMessage() + "</p>";
			htmlText += "<p text=\"#550080\">_________________________________________________________________________________________</p>";
		}

		System.out.println(htmlText);
		htmlPanel.show(htmlText);

		PluginServices.getMDIManager().addCentredWindow(htmlPanel);

	}

	/**
	 * HtmlInfoToolPanel that implements IWindow
	 * 
	 * @author azabala
	 * 
	 */
	class HtmlWindow extends JPanel implements IWindow {
		private HTMLInfoToolPanel htmlPanel = new HTMLInfoToolPanel();
		WindowInfo viewInfo = null;

		public HtmlWindow(int width, int height, String windowTitle) {
			htmlPanel.setBackground(Color.white);
			JScrollPane scrollPane = new JScrollPane(htmlPanel);
			scrollPane.setPreferredSize(new Dimension(width - 30, height - 30));
			this.add(scrollPane);
			viewInfo = new WindowInfo(WindowInfo.MODELESSDIALOG);
			viewInfo.setTitle(windowTitle);
			viewInfo.setWidth(width);
			viewInfo.setHeight(height);
		}

		public void show(String htmlText) {
			htmlPanel.show(htmlText);
		}

		public WindowInfo getWindowInfo() {
			return viewInfo;
		}

		public Object getWindowProfile() {
			// TODO Auto-generated method stub
			return WindowInfo.PROPERTIES_PROFILE;
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
		FLayers layers = this.getMapContext().getLayers();

		for (int i = layers.getLayersCount() - 1; i >= 0; i--) {
			try {
				if (layers.getLayer(i) instanceof AlphanumericData) {
					Project project = ((ProjectExtension) PluginServices
							.getExtension(ProjectExtension.class)).getProject();
					ProjectTable pt = project
							.getTable((AlphanumericData) layers.getLayer(i));

					ArrayList tables = project
							.getDocumentsByType(ProjectTableFactory.registerName);
					for (int j = 0; j < tables.size(); j++) {
						if (tables.get(j) == pt) {
							project.delDocument((ProjectDocument) tables.get(j));
							break;
						}
					}

					PluginServices.getMDIManager().closeSingletonWindow(pt);
				}
				layers.getLayer(i).getParentLayer()
						.removeLayer(layers.getLayer(i));
				PluginServices.getMainFrame().enableControls();
			} catch (CancelationException e1) {
				e1.printStackTrace();
			}
		}

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

	public void errorThrown(ErrorEvent e) {
		JOptionPane.showMessageDialog(
				(Component) PluginServices.getMainFrame(),
				PluginServices.getText(this, "fallo_capas") + " : \n"
						+ e.getMessage());

	}

	public boolean isLocked() {
		if (super.isLocked())
			return true;
		FLayers layers = getMapContext().getLayers();
		for (int i = 0; i < layers.getLayersCount(); i++) {
			FLayer layer = layers.getLayer(i);
			if (layer.isEditing()) {
				return true;
			}
		}
		return false;
	}

}
