package com.iver.cit.gvsig;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.gvsig.tools.file.PathGenerator;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.data.SourceInfo;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.contextMenu.AbstractDocumentContextMenuAction;
import com.iver.cit.gvsig.project.documents.exceptions.OpenException;
import com.iver.cit.gvsig.project.documents.exceptions.SaveException;
import com.iver.cit.gvsig.project.documents.layout.ProjectMap;
import com.iver.cit.gvsig.project.documents.layout.ProjectMapFactory;
import com.iver.cit.gvsig.project.documents.layout.fframes.FFrameView;
import com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.view.ProjectView;
import com.iver.cit.gvsig.project.documents.view.ProjectViewFactory;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.cit.gvsig.project.documents.view.toc.AbstractTocContextMenuAction;
import com.iver.cit.gvsig.project.documents.view.toc.ITocItem;
import com.iver.utiles.XMLEntity;
import com.iver.utiles.extensionPoints.IExtensionBuilder;
import com.iver.utiles.xmlEntity.generate.XmlTag;

public class CopyPasteExtension extends Extension {

	public void initialize() {
		/*
		ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();



		// TOC
		MyTocMenuEntry copy = new CopyTocMenuEntry();
		MyTocMenuEntry cut = new CutTocMenuEntry();
		MyTocMenuEntry paste = new PasteTocMenuEntry();
		Utiles utiles = new Utiles();
		copy.setUtiles(utiles);
		cut.setUtiles(utiles);
		paste.setUtiles(utiles);

		extensionPoints.add("View_TocActions","Copy","Copy selectes layers to system clipboard",copy);
		extensionPoints.add("View_TocActions","Cut","Cut selectes layers to system clipboard", cut);
		extensionPoints.add("View_TocActions","Paste","Paste layers from system clipboard",paste);


    	//FPopupMenu.addEntry(copy);
    	//FPopupMenu.addEntry(cut);
    	//FPopupMenu.addEntry(paste);


    	// ProjectWindow
		CopyProjectElement copyProjectElement = new CopyProjectElement();
		CutProjectElement cutProjectElement = new CutProjectElement();
		PasteProjectElement pasteProjectElementView = new PasteProjectElement();
		PasteProjectElement pasteProjectElementTable = new PasteProjectElement();
		PasteProjectElement pasteProjectElementMap = new PasteProjectElement();

		copyProjectElement.setUtiles(utiles);
		cutProjectElement.setUtiles(utiles);
		pasteProjectElementView.setUtiles(utiles);
		pasteProjectElementTable.setUtiles(utiles);
		pasteProjectElementMap.setUtiles(utiles);

		pasteProjectElementView.setType("views");
		pasteProjectElementTable.setType("tables");
		pasteProjectElementMap.setType("maps");


		extensionPoints.add("DocumentActions_View","Copy","Copy selectes documento to system clipboard",copyProjectElement);
		extensionPoints.add("DocumentActions_View","Cut","Cut selectes documento to system clipboard", cutProjectElement);
		extensionPoints.add("DocumentActions_View","Paste","Paste views from system clipboard",pasteProjectElementView);


		extensionPoints.add("DocumentActions_Table","Copy","Copy selectes documento to system clipboard",copyProjectElement);
		extensionPoints.add("DocumentActions_Table","Cut","Cut selectes documento to system clipboard", cutProjectElement);
		extensionPoints.add("DocumentActions_Table","Paste","Paste tables from system clipboard",pasteProjectElementTable);

		extensionPoints.add("DocumentActions_Map","Copy","Copy selectes documento to system clipboard",copyProjectElement);
		extensionPoints.add("DocumentActions_Map","Cut","Cut selectes documento to system clipboard", cutProjectElement);
		extensionPoints.add("DocumentActions_Map","Paste","Paste maps from system clipboard",pasteProjectElementMap);

		*/
	}

	public void execute(String actionCommand) {
		// TODO Auto-generated method stub

	}

	public boolean isEnabled() {
		return false;
	}

	public boolean isVisible() {
		return false;
	}


}

abstract class MyDocumentAction extends AbstractDocumentContextMenuAction implements IExtensionBuilder {
	protected Utiles utiles;

	public void setUtiles(Utiles utiles) {
		this.utiles = utiles;
	}

	public String getGroup() {
		return "ClipboardActions";
	}


	public Object create() {
		return this;
	}

	public Object create(Object[] args) {
		// TODO Auto-generated method stub
		return this;
	}

	public Object create(Map args) {
		// TODO Auto-generated method stub
		return this;
	}
}


class CopyProjectElement extends MyDocumentAction{
	public String getDescription() {
		//FIXME: Falta claves
		//return PluginServices.getText(this,"tooltip_copiar_al_portapapeles");
		return null;
	}

	public int getOrder() {

		return 0;
	}

	public boolean isVisible(ProjectDocument item, ProjectDocument[] selectedItems) {
		return true;
	}

	public boolean isEnabled(ProjectDocument item, ProjectDocument[] selectedItems) {
		return selectedItems.length > 0;
	}


	public void execute(ProjectDocument item, ProjectDocument[] selectedItems) {
		XMLEntity xml = this.utiles.generateXMLCopyDocuments(selectedItems);
		if (xml == null) {
			JOptionPane.showMessageDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+PluginServices.getText(this,"No_ha_sido_posible_realizar_la_operacion")+"</html>",//Mensaje
					PluginServices.getText(this,"pegar"),//titulo
					JOptionPane.ERROR_MESSAGE
					);
			return;
		}

		String data = this.utiles.marshallXMLEntity(xml);
		if (data == null) {
			JOptionPane.showMessageDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+PluginServices.getText(this,"No_ha_sido_posible_realizar_la_operacion")+"</html>",//Mensaje
					PluginServices.getText(this,"pegar"),//titulo
					JOptionPane.ERROR_MESSAGE
					);
			return;
		}
		this.utiles.putInClipboard(data);
	}

	public String getText() {
		return PluginServices.getText(this, "copiar");
	}

}

class CutProjectElement extends MyDocumentAction {
	public String getDescription() {
		//FIXME: Falta claves
		//return PluginServices.getText(this,"tooltip_cortar_al_portapapeles");
		return null;
	}

	public int getOrder() {
		return 1;
	}

	public boolean isVisible(ProjectDocument item, ProjectDocument[] selectedItems) {
		return true;
	}

	public boolean isEnabled(ProjectDocument item, ProjectDocument[] selectedItems) {
		return selectedItems.length > 0;
	}


	public void execute(ProjectDocument item, ProjectDocument[] selectedItems) {
		XMLEntity xml = this.utiles.generateXMLCopyDocuments(selectedItems);
		if (xml == null) {
			JOptionPane.showMessageDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+PluginServices.getText(this,"No_ha_sido_posible_realizar_la_operacion")+"</html>",//Mensaje
					PluginServices.getText(this,"cortar"),//titulo
					JOptionPane.ERROR_MESSAGE
					);
			return;
		}

		String data = this.utiles.marshallXMLEntity(xml);
		if (data == null) {
			JOptionPane.showMessageDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+PluginServices.getText(this,"No_ha_sido_posible_realizar_la_operacion")+"</html>",//Mensaje
					PluginServices.getText(this,"cortar"),//titulo
					JOptionPane.ERROR_MESSAGE
					);
			return;
		}
		this.utiles.putInClipboard(data);


    	int option=JOptionPane.showConfirmDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"desea_borrar_el_documento"));
    	if (option!=JOptionPane.OK_OPTION) {
    		return;
    	}


		this.utiles.removeDocuments(selectedItems);

	}

	public String getText() {
		return PluginServices.getText(this, "cortar");
	}

}

class PasteProjectElement extends MyDocumentAction {
	private String type;

	public String getDescription() {
		//FIXME: Falta claves
		//return PluginServices.getText(this,"tooltip_pegar_desde_el_portapapeles");
		return null;
	}

	public int getOrder() {
		return 2;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType(String type) {
		return this.type;
	}

	public boolean isVisible(ProjectDocument item, ProjectDocument[] selectedItems) {
		return true;
	}

	public boolean isEnabled(ProjectDocument item, ProjectDocument[] selectedItems) {
		String sourceString = this.utiles.getFromClipboard();
		if (sourceString == null) return false;

		XMLEntity xml = this.utiles.unMarshallXMLEntity(sourceString);
		if (xml == null) return false;

		if (!this.utiles.checkXMLRootNode(xml)) return false;

		if (this.utiles.getXMLEntityChildOfType(xml,this.type) == null) return false;
		return true;
	}


	public void execute(ProjectDocument item, ProjectDocument[] selectedItems) {
		String sourceString = this.utiles.getFromClipboard();
		if (sourceString == null) return;

		XMLEntity xml = this.utiles.unMarshallXMLEntity(sourceString);
		if (xml == null) return;

		if (!this.utiles.checkXMLRootNode(xml)) return;

		if (this.type.equals("views")) {
			this.utiles.loadViewsFromXML(xml);
		} else if (this.type.equals("tables")) {
			this.utiles.loadTablesFromXML(xml);
		} else if (this.type.equals("maps")) {
			this.utiles.loadMapsFromXML(xml);
		} else {
			//TODO que hacer aqui??
			return;
		}

	}

	public String getText() {
		return PluginServices.getText(this, "pegar");
	}

}



abstract class  MyTocMenuEntry extends AbstractTocContextMenuAction {
	protected Utiles utiles;

	public void setUtiles(Utiles utiles) {
		this.utiles = utiles;
	}

	public String getGroup() {
		return "copyPasteLayer";
	}

	public int getGroupOrder() {
		return 60;
	}

}

class CopyTocMenuEntry extends MyTocMenuEntry{
	public int getOrder() {
		return 0;
	}

	public String getText() {
		return PluginServices.getText(this, "copiar");
	}

	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		return selectedItems.length >= 1 && isTocItemBranch(item);
	}


	public void execute(ITocItem item, FLayer[] selectedItems) {
		XMLEntity xml = this.utiles.generateXMLCopyLayers(selectedItems);
		if (xml == null) {
			JOptionPane.showMessageDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+PluginServices.getText(this,"No_ha_sido_posible_realizar_la_operacion")+"</html>",//Mensaje
					PluginServices.getText(this,"copiar"),//titulo
					JOptionPane.ERROR_MESSAGE
					);
			return;
		}

		String data = this.utiles.marshallXMLEntity(xml);
		if (data == null) {
			JOptionPane.showMessageDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+PluginServices.getText(this,"No_ha_sido_posible_realizar_la_operacion")+"</html>",//Mensaje
					PluginServices.getText(this,"copiar"),//titulo
					JOptionPane.ERROR_MESSAGE
					);
			return;
		}

		this.utiles.putInClipboard(data);

	}


}

class CutTocMenuEntry extends MyTocMenuEntry{
	public int getOrder() {
		return 1;
	}

	public String getText() {
		return PluginServices.getText(this, "cortar");
	}

	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		return selectedItems.length >= 1 && isTocItemBranch(item);
	}


	public void execute(ITocItem item, FLayer[] selectedItems) {
		XMLEntity xml = this.utiles.generateXMLCopyLayers(selectedItems);
		if (xml == null) {
			JOptionPane.showMessageDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+PluginServices.getText(this,"No_ha_sido_posible_realizar_la_operacion")+"</html>",//Mensaje
					PluginServices.getText(this,"cortar"),//titulo
					JOptionPane.ERROR_MESSAGE
					);
			return;
		}

		String data = this.utiles.marshallXMLEntity(xml);
		if (data == null) {
			JOptionPane.showMessageDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+PluginServices.getText(this,"No_ha_sido_posible_realizar_la_operacion")+"</html>",//Mensaje
					PluginServices.getText(this,"cortar"),//titulo
					JOptionPane.ERROR_MESSAGE
					);
			return;
		}


		this.utiles.putInClipboard(data);


    	int option=JOptionPane.showConfirmDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"desea_borrar_la_capa"));
    	if (option!=JOptionPane.OK_OPTION) {
    		return;
    	}
		getMapContext().beginAtomicEvent();


		boolean isOK =this.utiles.removeLayers(selectedItems);

		getMapContext().endAtomicEvent();

		if (isOK) {
			getMapContext().invalidate();
			if (getMapContext().getLayers().getLayersCount()==0) {
				PluginServices.getMainFrame().enableControls();
			}
		}

	}
}


class PasteTocMenuEntry extends MyTocMenuEntry{
	private XMLEntity xml=null;

	public int getOrder() {
		return 2;
	}

	public String getText() {
		return PluginServices.getText(this, "pegar");
	}

	public boolean isEnabled(ITocItem item, FLayer[] selectedItems) {
		if (isTocItemBranch(item)) {
			FLayer lyr = getNodeLayer(item);
			if (lyr instanceof FLayers) {
				this.xml = this.getCheckedXMLFromClipboard();
				return true;
			}

		} else if (!isTocItemLeaf(item)) {
			if (getNodeLayer(item) == null) {
				this.xml = this.getCheckedXMLFromClipboard();
				return this.xml != null;
			}
		}
		return false;
	}

	private XMLEntity getCheckedXMLFromClipboard() {
		String sourceString = this.utiles.getFromClipboard();
		if (sourceString == null) return null;

		XMLEntity xml = this.utiles.unMarshallXMLEntity(sourceString);
		if (xml == null) return null;

		if (!this.utiles.checkXMLRootNode(xml)) return null;

		if (this.utiles.getXMLEntityChildOfType(xml,"layers") == null) return null;

		return  xml;
	}

	public void execute(ITocItem item, FLayer[] selectedItems) {
		FLayers root;

		if (this.xml == null) return;

		if (isTocItemBranch(item)) {
			root = (FLayers)getNodeLayer(item);
		} else if (getNodeLayer(item) == null){
			root = getMapContext().getLayers();
		} else {
			return;
		}
		getMapContext().beginAtomicEvent();

		boolean isOK = this.utiles.loadLayersFromXML(this.xml,root);

		getMapContext().endAtomicEvent();

		if (isOK) {
			getMapContext().invalidate();
			IWindow view=PluginServices.getMDIManager().getActiveWindow();
			if (view instanceof BaseView)
				((ProjectDocument)((BaseView)view).getModel()).setModified(true);
		}
	}

}


class Utiles {

	/*
	 *
======================================
 Comportamiento del Pegar documentos:
======================================

 Pegar vista.
	Si ya existe una vista en el proyecto con el mismo nombre.
		1. Abortar
		2. Pedir nuevo nombre.
			Que hacemos con las tablas asociadas.
				No se pegan
	Si alguna de las tablas a pegar de las que van en
	el portapapeles ya existen en el proyecto.
		1. abortamos
		2. Informamos al usuario y no se pegan las tablas.

Pegar tabla.
	Si alguna de las tablas existe.
		Se pega igualmente (apareceran tablas duplicadas)

Pegar mapa.
	Si el mapa ya existe en el proyecto.
		1. Abortar
		2. renombrar el mapa
	Si alguna vista ya existe en el proyecto.
		1. Abortar
		2. Usar la vista que ya existe en el proyecto y no
		   pegar la nueva vista.
	Si alguna de las tablas a pegar de las que van en
		el portapapeles ya existen en el proyecto.
			1. abortamos
			2. Informamos al usuario y no se pegan las tablas.


	 */


	/*
	 *
	 *
	 *
	 * Funciones Publicas para generar XML (copiar)
	 *
	 *
	*/

	/**
	 * Genera un XMLEntity con la informacion necesaria
	 * para copiar los elementos de selectedItems en
	 * otro proyecto
	 */
	public XMLEntity generateXMLCopyDocuments(ProjectDocument[] selectedItems) {
		if (selectedItems.length == 0) return null;

		if (selectedItems[0] instanceof ProjectView) {
			ProjectView[] views = new ProjectView[selectedItems.length];
			System.arraycopy(selectedItems,0,views,0,selectedItems.length);
			return this.generateXMLCopyViews(views);
		} else if (selectedItems[0] instanceof ProjectMap) {
			ProjectMap[] maps = new ProjectMap[selectedItems.length];
			System.arraycopy(selectedItems,0,maps,0,selectedItems.length);
			return this.generateXMLCopyMaps(maps);
		} else if (selectedItems[0] instanceof ProjectTable) {
			ProjectTable[] tables = new ProjectTable[selectedItems.length];
			System.arraycopy(selectedItems,0,tables,0,selectedItems.length);
			return this.generateXMLCopyTables(tables);
		} else {
			//FIXME:????
			return null;
		}
	}

	public XMLEntity generateXMLCopyViews(ProjectView[] selectedItems) {
		XMLEntity xml = this.newRootNode();

		XMLEntity xmlTables = this.newTablesNode();
		XMLEntity xmlDataSources = this.newDataSourcesNode();
		XMLEntity xmlViews = this.newViewsNode();

		for (int i=0;i < selectedItems.length; i++) {
			if (!this.addToXMLView(selectedItems[i],xmlViews,xmlTables,xmlDataSources)) return null;

		}


		if (xmlDataSources.getChildrenCount() > 0) {
			xml.addChild(xmlDataSources);
		}
		if (xmlViews.getChildrenCount() > 0) {
			xml.addChild(xmlViews);
		}
		if (xmlTables.getChildrenCount() > 0) {
			xml.addChild(xmlTables);
		}

		return xml;

	}


	public XMLEntity generateXMLCopyMaps(ProjectMap[] selectedItems) {
		XMLEntity xml = this.newRootNode();

		XMLEntity xmlTables = this.newTablesNode();
		XMLEntity xmlDataSources = this.newDataSourcesNode();
		XMLEntity xmlViews = this.newViewsNode();
		XMLEntity xmlMaps = this.newMapsNode();

		for (int i=0;i < selectedItems.length; i++) {
			if (!this.addToXMLMap(selectedItems[i],xmlMaps,xmlViews,xmlTables,xmlDataSources)) return null;

		}


		if (xmlDataSources.getChildrenCount() > 0) {
			xml.addChild(xmlDataSources);
		}
		if (xmlViews.getChildrenCount() > 0) {
			xml.addChild(xmlViews);
		}
		if (xmlTables.getChildrenCount() > 0) {
			xml.addChild(xmlTables);
		}
		if (xmlMaps.getChildrenCount() > 0) {
			xml.addChild(xmlMaps);
		}


		return xml;
	}

	public XMLEntity generateXMLCopyTables(ProjectTable[] selectedItems) {
		XMLEntity xml = this.newRootNode();

		XMLEntity xmlTables = this.newTablesNode();
		XMLEntity xmlDataSources = this.newDataSourcesNode();

		for (int i=0;i < selectedItems.length; i++) {
			if (!this.addToXMLTable(selectedItems[i],xmlTables,xmlDataSources,null)) return null;
		}


		if (xmlDataSources.getChildrenCount() > 0) {
			xml.addChild(xmlDataSources);
		}
		if (xmlTables.getChildrenCount() > 0) {
			xml.addChild(xmlTables);
		}

		return xml;
	}


	public XMLEntity generateXMLCopyLayers(FLayer[] actives) {

		XMLEntity xml = this.newRootNode();
		XMLEntity xmlLayers = this.newLayersNode();
		XMLEntity xmlTables = this.newTablesNode();
		XMLEntity xmlDataSources = this.newDataSourcesNode();

		for (int i=0;i < actives.length; i++) {
			if (!this.addToXMLLayer(actives[i],xmlLayers ,xmlTables,xmlDataSources)) return null;

		}

		if (xmlDataSources.getChildrenCount() > 0) {
			xml.addChild(xmlDataSources);
		}
		if (xmlLayers.getChildrenCount() > 0) {
			xml.addChild(xmlLayers);
		}
		if (xmlTables.getChildrenCount() > 0) {
			xml.addChild(xmlTables);
		}

		return xml;

	}



	/*
	 *
	 *
	 *
	 * Funciones Publicas de carga de un XML (pegar)
	 *
	 *
	 *
	*/

	public boolean loadLayersFromXML(XMLEntity xml, FLayers root) {
		XMLEntity xmlLayers = this.getXMLEntityChildOfType(xml,"layers");
		XMLEntity xmlTables = this.getXMLEntityChildOfType(xml,"tables");
		XMLEntity xmlDataSources = this.getXMLEntityChildOfType(xml,"dataSources");

		if (xmlLayers == null ) return false;

		// Se pegan las tablas igualmente
		/*
		Project project = this.getProject();

		Hashtable tablesConfits = this.getConflicts(xmlTables,project.getTables());
		*/


		if (xmlDataSources != null)  {
			if (!this.registerDataSources(xmlDataSources)) return false;
		}

		if (!this.addLayers(xmlLayers,root)) return false;

		if (xmlTables != null)  {
			if (!this.addTables(xmlTables)) return false;
		}

		return true;

	}


	public boolean loadViewsFromXML(XMLEntity xml) {
		XMLEntity xmlViews = this.getXMLEntityChildOfType(xml,"views");
		XMLEntity xmlTables = this.getXMLEntityChildOfType(xml,"tables");
		XMLEntity xmlDataSources = this.getXMLEntityChildOfType(xml,"dataSources");

		if (xmlViews == null ) return false;

		Project project = this.getProject();

		Hashtable viewsConflits = this.getConflicts(xmlViews,project.getDocumentsByType(ProjectViewFactory.registerName));

		Hashtable tablesConflits = this.getConflicts(xmlTables,project.getDocumentsByType(ProjectTableFactory.registerName));

		if (viewsConflits != null && viewsConflits.size() > 0) {
			int option = JOptionPane.showConfirmDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+
						PluginServices.getText(this,"conflicto_de_nombres_de_vistas_al_pegar") + "<br>" +
						PluginServices.getText(this,"debera_introducir_nombres_para_las_vistas_a_pegar") + "<br>" +
						PluginServices.getText(this,"no_se_pegaran_las_tablas") + "<br>" +
						PluginServices.getText(this,"desea_continuar") +
					"</html>",
					PluginServices.getText(this,"pegar_vistas"),
					JOptionPane.YES_NO_OPTION
					);
			if (option != JOptionPane.YES_OPTION) {
				return false;
			}
			Enumeration en = viewsConflits.elements();
			while (en.hasMoreElements()) {
				XMLEntity view = (XMLEntity)en.nextElement();
				String newName = JOptionPane.showInputDialog(
						(Component)PluginServices.getMainFrame(),
						"<html>"+
							PluginServices.getText(this,"introduzca_nuevo_nombre_para_la_vista") +" "+  view.getStringProperty("name") + ":" +
						"</html>", //Mensaje
						view.getStringProperty("name") //Valor por defecto
						);
				if (newName == null) {
					JOptionPane.showMessageDialog(
							(Component)PluginServices.getMainFrame(),
							"<html>"+PluginServices.getText(this,"operacion_cancelada")+"</html>",//Mensaje
							PluginServices.getText(this,"pegar_vistas"),//titulo
							JOptionPane.ERROR_MESSAGE
							);
				} else if (newName.equalsIgnoreCase(view.getStringProperty("name")) ) {
					JOptionPane.showMessageDialog(
							(Component)PluginServices.getMainFrame(),
							"<html>"+
								PluginServices.getText(this,"operacion_cancelada") +":<br>" +
								PluginServices.getText(this,"nombre_no_valido")+
							"</html>",//Mensaje
							PluginServices.getText(this,"pegar_vistas"),//FIXME: getText
							JOptionPane.ERROR_MESSAGE
							);
					return false;
				}
				view.setName(newName);
			}
			if (xmlTables != null) xmlTables.removeAllChildren();
			tablesConflits = null;
		}

		if (tablesConflits != null && tablesConflits.size() > 0) {
			int option = JOptionPane.showConfirmDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>" +
						PluginServices.getText(this,"conflicto_de_nombres_de_tablas_al_pegar") + "<br>" +
						PluginServices.getText(this,"no_se_pegaran_las_tablas") + "<br>" +
						PluginServices.getText(this,"desea_continuar") +
					"</html>", //Mensaje
					PluginServices.getText(this,"pegar_vistas"),//FIXME: getText
					JOptionPane.YES_NO_OPTION
					);
			if (option != JOptionPane.YES_OPTION) {
				return false;
			}
			xmlTables.removeAllChildren();
		}


		if (xmlDataSources != null)  {
			if (!this.registerDataSources(xmlDataSources)) return false;
		}

		if (!this.addViews(xmlViews)) return false;

		if (xmlTables != null)  {
			if (!this.addTables(xmlTables)) return false;
		}

		return true;
	}

	public boolean loadTablesFromXML(XMLEntity xml) {
		XMLEntity xmlTables = this.getXMLEntityChildOfType(xml,"tables");
		XMLEntity xmlDataSources = this.getXMLEntityChildOfType(xml,"dataSources");


		if (xmlTables == null ) return false;

		/*
		Project project = this.getProject();

		Hashtable tablesConfits = this.getConflicts(xmlTables,project.getTables());
		*/

		if (xmlDataSources != null)  {
			if (!this.registerDataSources(xmlDataSources)) return false;
		}



		return this.addTables(xmlTables);
	}

	public boolean loadMapsFromXML(XMLEntity xml) {
		XMLEntity xmlMaps = this.getXMLEntityChildOfType(xml,"Maps");
		XMLEntity xmlViews = this.getXMLEntityChildOfType(xml,"views");
		XMLEntity xmlTables = this.getXMLEntityChildOfType(xml,"tables");
		XMLEntity xmlDataSources = this.getXMLEntityChildOfType(xml,"dataSources");

		if (xmlMaps == null ) return false;

		Project project = this.getProject();

		Hashtable mapsConflits = this.getConflicts(xmlMaps,project.getDocumentsByType(ProjectMapFactory.registerName));

		Hashtable viewsConflits = this.getConflicts(xmlViews,project.getDocumentsByType(ProjectViewFactory.registerName));

		Hashtable tablesConflits = this.getConflicts(xmlTables,project.getDocumentsByType(ProjectTableFactory.registerName));


		if (mapsConflits != null && mapsConflits.size() > 0) {
			int option = JOptionPane.showConfirmDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+
						PluginServices.getText(this,"conflicto_de_nombres_de_mapas_al_pegar") + "<br>" +
						PluginServices.getText(this,"debera_introducir_nombres_para_los_mapas_a_pegar") + "<br>" +
					"</html>", //Mensaje
					PluginServices.getText(this,"pegar_mapas"),//titulo
					JOptionPane.YES_NO_OPTION
					);
			if (option != JOptionPane.YES_OPTION) {
				return false;
			}
			Enumeration en = mapsConflits.elements();
			while (en.hasMoreElements()) {
				XMLEntity map = (XMLEntity)en.nextElement();
				String newName = JOptionPane.showInputDialog(
						(Component)PluginServices.getMainFrame(),
						"<html>"+
							PluginServices.getText(this,"nuevo_nombre_para_el_mapa") +" "+  map.getStringProperty("name") + ":" +
					    "</html>", //Mensaje
						map.getStringProperty("name") //Valor por defecto
						);
				if (newName == null) {
					JOptionPane.showMessageDialog(
							(Component)PluginServices.getMainFrame(),
							"<html>"+PluginServices.getText(this,"operacion_cancelada")+"</html>",//Mensaje
							PluginServices.getText(this,"pegar_mapas"),//titulo
							JOptionPane.ERROR_MESSAGE
							);
				} else if (newName.equalsIgnoreCase(map.getStringProperty("name")) ) {
					JOptionPane.showMessageDialog(
							(Component)PluginServices.getMainFrame(),
							"<html>"+
								PluginServices.getText(this,"operacion_cancelada") +":<br>" +
								PluginServices.getText(this,"nombre_no_valido")+
							"</html>",//Mensaje
							PluginServices.getText(this,"pegar_mapas"),//titulo
							JOptionPane.ERROR_MESSAGE
							);
					return false;
				}
				map.setName(newName);
			}
		}

		if (viewsConflits != null && viewsConflits.size() > 0) {
			int option = JOptionPane.showConfirmDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>"+
						PluginServices.getText(this,"conflicto_de_nombres_de_vistas_al_pegar") + "<br>" +
						PluginServices.getText(this,"no_se_pegaran_las_vistas_del_conflicto") + "<br>" +
						PluginServices.getText(this,"desea_continuar") +
					"</html>",
					PluginServices.getText(this,"pegar_mapas"),//titulo
					JOptionPane.YES_NO_OPTION
					);
			if (option != JOptionPane.YES_OPTION) {
				return false;
			}
			// Eliminamos las vistas del xml que no vamos a importar

			// Esto me devuelve los indices en orden inverso
			int[] indexes = this.getIndexOfConflict(viewsConflits);
			for (int i=0;i < indexes.length;i++) {
				xmlViews.removeChild(indexes[i]);
			}
			viewsConflits = null;

		}


		if (tablesConflits != null && tablesConflits.size() > 0) {
			int option = JOptionPane.showConfirmDialog(
					(Component)PluginServices.getMainFrame(),
					"<html>" +
						PluginServices.getText(this,"conflito_de_nombres_de_tablas_al_pegar") + "<br>" +
						PluginServices.getText(this,"no_se_pegaran_las_tablas") + "<br>" +
						PluginServices.getText(this,"desea_continuar") +
					"</html>", //Mensaje
					PluginServices.getText(this,"pegar_mapas"),
					JOptionPane.YES_NO_OPTION
					);
			if (option != JOptionPane.YES_OPTION) {
				return false;
			}
			xmlTables.removeAllChildren();
		}


		if (xmlDataSources != null)  {
			if (!this.registerDataSources(xmlDataSources)) return false;
		}

		if (xmlViews != null)  {
			if (!this.addViews(xmlViews)) return false;
		}

		if (xmlTables != null)  {
			if (!this.addTables(xmlTables)) return false;
		}

		return this.addMaps(xmlMaps);

	}











	/**
	 * Devuelve las claves de conflits ordenados
	 * en orden inverso. Las claves se esperan que
	 * sean instancias de Integer
	 */
	private int[] getIndexOfConflict(Hashtable conflits) {
		Object[] tmpArray = conflits.keySet().toArray();
		Arrays.sort(tmpArray,new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Integer)o2).intValue() - ((Integer)o1).intValue();
			}
		}
		);
		int[] indexes = new int[] {tmpArray.length};
		for (int i = 0;i< tmpArray.length;i++) {
			indexes[i] = ((Integer)tmpArray[i]).intValue();
		}
		return indexes;


	}


	private boolean addToXMLMapDependencies(ProjectMap map, XMLEntity xmlViews,XMLEntity xmlTables, XMLEntity xmlDataSources) {
		IFFrame[] components = map.getModel().getLayoutContext().getFFrames();
		for (int i=0; i < components.length; i++) {
			if (components[i] instanceof FFrameView) {
				ProjectView view = ((FFrameView)components[i]).getView();
				if (findChildInXML(xmlViews,"name",view.getName())==null) {
					if (!this.addToXMLView(view,xmlViews,xmlTables,xmlDataSources)) return false;
				}
			}
		}

		return true;
	}

	private boolean addToXMLMap(ProjectMap map,XMLEntity xmlMaps,XMLEntity xmlViews,XMLEntity xmlTables,XMLEntity xmlDataSources) {
		try {
			xmlMaps.addChild(map.getXMLEntity());

			return this.addToXMLMapDependencies(map,xmlViews,xmlTables,xmlDataSources);

		} catch (SaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	private boolean addToXMLView(ProjectView view,XMLEntity xmlViews,XMLEntity xmlTables,XMLEntity xmlDataSources) {
		try {
			xmlViews.addChild(view.getXMLEntity());

			if (!this.addToXMLLayerDependencies(view.getMapContext().getLayers(),xmlTables,xmlDataSources)) return false;

			if (view.getMapOverViewContext() != null) {
				return this.addToXMLLayerDependencies(view.getMapOverViewContext().getLayers(),xmlTables,xmlDataSources);
			} else {
				return true;
			}



		} catch (SaveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}



	public boolean checkXMLRootNode(XMLEntity xml) {
		if (!xml.contains("applicationName")) return false;
		if (!xml.getStringProperty("applicationName").equalsIgnoreCase("gvSIG")) return false;

		if (!xml.contains("version")) return false;
		if (!xml.getStringProperty("version").equalsIgnoreCase(Version.format())) return false;

		return true;
	}

	private void fillXMLRootNode(XMLEntity xml) {
		xml.putProperty("applicationName","gvSIG");
		xml.putProperty("version",Version.format());
	}

	public XMLEntity getXMLEntityChildOfType(XMLEntity xml,String type) {
		int childCount = xml.getChildrenCount();
		XMLEntity child;
		for (int i=0; i < childCount; i++  ) {
			child = xml.getChild(i);
			if (child.contains("type")) {
				if (child.getStringProperty("type").equalsIgnoreCase(type)) {
					return child;
				}
			}
		}
		return null;

	}

	private Hashtable getConflicts(XMLEntity xml,ArrayList elements) {
		if (xml == null || xml.getChildrenCount() < 1) return null;
		Hashtable conflits = new Hashtable();
		for (int iXML=0;iXML < xml.getChildrenCount();iXML++) {
			XMLEntity child = xml.getChild(iXML);
			Iterator iter = elements.iterator();
			while (iter.hasNext()) {
				ProjectDocument element = (ProjectDocument)iter.next();
				if (element.getName().equalsIgnoreCase(child.getStringProperty("name"))) {
					conflits.put(new Integer(iXML),child);
					break;
				}

			}
		}
		return conflits;
	}



	private boolean registerDataSources(XMLEntity xmlDataSources) {
		try {
			int numDataSources = xmlDataSources.getChildrenCount();

			if (numDataSources == 0) return true;
			DataSourceFactory dsFactory = LayerFactory.getDataSourceFactory();

			for (int i = 0; i < numDataSources; i++) {
				XMLEntity child = xmlDataSources.getChild(i);
				String name = child.getStringProperty("gdbmsname");

				if (dsFactory.getDriverInfo(name) == null) {
					if (child.getStringProperty("type").equals("otherDriverFile")) {
						LayerFactory.getDataSourceFactory().addFileDataSource(
								child.getStringProperty("driverName"),
								name,
								PathGenerator.getInstance().getAbsolutePath(child.getStringProperty("file"))
						);


					} else if (child.getStringProperty("type").equals("sameDriverFile")) {
						/*                                String layerName = child.getStringProperty("layerName");
						 ProjectView vista = project.getViewByName(child.getStringProperty(
						 "viewName"));
						 FLayer layer = vista.getMapContext().getLayers().getLayer(layerName);

						 modelo = ((AlphanumericData) layer).getRecordset();
						 associatedTable = (AlphanumericData) layer;
						 */
					} else if (child.getStringProperty("type").equals("db")) {
						LayerFactory.getDataSourceFactory().addDBDataSourceByTable(
								name,
								child.getStringProperty("host"),
								child.getIntProperty("port"),
								child.getStringProperty("user"),
								child.getStringProperty("password"),
								child.getStringProperty("dbName"),
								child.getStringProperty("tableName"),
								child.getStringProperty("driverInfo")
						);
					}
				}
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean addTables(XMLEntity xmlTables) {
		try {
			int numTables = xmlTables.getChildrenCount();
			if (numTables == 0) return true;

			Project project = this.getProject();

			for (int i = 0; i < numTables; i++) {
				try{
					ProjectTable ptable = (ProjectTable) ProjectTable.createFromXML(xmlTables.getChild(i), project);
					project.addDocument(ptable);
					/*
					if (ptable.getSeedViewInfo()!=null && ptable.getAndamiView()!=null) { // open the view, if it was open, and restore its dimensions
						PluginServices.getMDIManager().addView(ptable.getAndamiView());
						PluginServices.getMDIManager().changeViewInfo(ptable.getAndamiView(), ptable.getSeedViewInfo());
					}
					*/
				}catch(OpenException e){
					e.printStackTrace();
					return false;
				}
			}

			project.setLinkTable();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public XMLEntity findChildInXML(XMLEntity xml,String propName,String value) {
		int num = xml.getChildrenCount();
		XMLEntity child;
		for (int i=0;i < num; i++) {
			child = xml.getChild(i);
			if (child.getStringProperty(propName).equals(value)) {
				return child;
			}
		}
		return null;
	}

	private boolean addLayers(XMLEntity xmlLayers,FLayers root) {
		try {
			XMLEntity child;
			int numLayers = xmlLayers.getChildrenCount();
			for (int i = 0; i < numLayers; i++) {
				child = xmlLayers.getChild(i);
				if (!root.addLayerFromXMLEntity(child,null)) return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}


	private boolean addViews(XMLEntity xmlViews) {
		try {
			Project project = this.getProject();
			XMLEntity child;
			int numLayers = xmlViews.getChildrenCount();
			for (int i = 0; i < numLayers; i++) {
				child = xmlViews.getChild(i);

				ProjectView pv = (ProjectView) ProjectView.createFromXML(child, project);
				project.addDocument(pv);

			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private boolean addMaps(XMLEntity xmlMaps) {
		try {
			Project project = this.getProject();
			XMLEntity child;
			int numLayers = xmlMaps.getChildrenCount();
			for (int i = 0; i < numLayers; i++) {
				child = xmlMaps.getChild(i);

				ProjectMap pm = (ProjectMap) ProjectMap.createFromXML(child, project);
				project.addDocument(pm);

			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private Project getProject() {
		 return ((ProjectExtension)PluginServices.getExtension(ProjectExtension.class)).getProject();
	}

	private boolean addToXMLDataSource(SourceInfo source,XMLEntity xmlDataSources, Project project) {
		if (project== null) {
			project = this.getProject();
		}
  	  	xmlDataSources.addChild(project.getSourceInfoXMLEntity(source));

  	  	return true;
	}

	private boolean addToXMLTable(ProjectTable pt,XMLEntity xmlTables,XMLEntity xmlDataSources,Project project) {
		if (project== null) {
			project = this.getProject();
		}
		if (findChildInXML(xmlTables,"name",pt.getName()) != null) return true;
		XMLEntity xmlTable = null;
		try {
			xmlTable = pt.getXMLEntity();

			xmlTables.addChild(xmlTable);

			if (pt.getAssociatedTable() != null) {
				this.addToXMLDataSource(pt.getAssociatedTable().getRecordset().getSourceInfo(),xmlDataSources,project);
			}

			if (pt.getLinkTable() != null) {
				if (findChildInXML(xmlTables,"name",pt.getLinkTable()) == null)  {
					ProjectTable ptLink = project.getTable(pt.getLinkTable());
					if (!this.addToXMLTable(ptLink,xmlTables,xmlDataSources,project)) return false;
				}
			}
		} catch (SaveException e) {
			e.printStackTrace();
			return false;
		} catch (ReadDriverException e) {
			e.printStackTrace();
			return false;
		}

  	  	return true;
	}

	private boolean addToXMLLayerDependencies(FLayer lyr,XMLEntity xmlTables,XMLEntity xmlDataSources) {
		try {
			Project project = this.getProject();

			if (lyr instanceof FLayers) {
				FLayers lyrs = (FLayers)lyr;
				int count = lyrs.getLayersCount();
				for (int i=0;i < count;i++) {
					FLayer subLyr = lyrs.getLayer(i);
					this.addToXMLLayerDependencies(subLyr,xmlTables,xmlDataSources);
				}

		    } else if (lyr instanceof AlphanumericData){
            	if (!this.addToXMLDataSource(
        			((AlphanumericData)lyr).getRecordset().getSourceInfo(),
        			xmlDataSources,
        			project

            	)) return false;

                ProjectTable pt = project.getTable((AlphanumericData) lyr);
                if (pt != null) {
                	if (!this.addToXMLTable(pt,xmlTables,xmlDataSources,project)) return false;
                }

            }

		} catch (ReadDriverException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;

		}
		return true;

	}

	private boolean addToXMLLayer(FLayer lyr,XMLEntity xmlLayers,XMLEntity xmlTables,XMLEntity xmlDataSources) {
		try {
			xmlLayers.addChild(lyr.getXMLEntity());

			return this.addToXMLLayerDependencies(lyr,xmlTables,xmlDataSources);

		} catch (XMLException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private XMLEntity newRootNode() {
		XMLEntity xml = new XMLEntity();
		fillXMLRootNode(xml);
		return xml;
	}

	private XMLEntity newLayersNode() {
		XMLEntity xmlLayers = new XMLEntity();
		xmlLayers.putProperty("type","layers");
		return xmlLayers;
	}

	private XMLEntity newDataSourcesNode() {
		XMLEntity xmlDataSources = new XMLEntity();
		xmlDataSources.putProperty("type","dataSources");
		return xmlDataSources;
	}

	private XMLEntity newTablesNode() {
		XMLEntity xmlTables = new XMLEntity();
		xmlTables.putProperty("type","tables");
		return xmlTables;
	}

	private XMLEntity newViewsNode() {
		XMLEntity xmlTables = new XMLEntity();
		xmlTables.putProperty("type","views");
		return xmlTables;
	}

	private XMLEntity newMapsNode() {
		XMLEntity xmlTables = new XMLEntity();
		xmlTables.putProperty("type","maps");
		return xmlTables;
	}


	public void putInClipboard(String data) {
		StringSelection ss = new StringSelection(data);

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss,ss);
	}

	public String marshallXMLEntity(XMLEntity xml) {
		StringWriter buffer = new StringWriter();

		Marshaller m;
		try {
			m = new Marshaller(buffer);
		} catch (IOException e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
			return null;
		}
		m.setEncoding(ProjectExtension.PROJECTENCODING);

		try {
			m.marshal(xml.getXmlTag());
			//if (i < actives.length-1) buffer.write("\n##layer-separator##\n");
		} catch (MarshalException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
			return null;
		} catch (ValidationException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
			return null;
		}

		return buffer.toString();

	}

	public XMLEntity unMarshallXMLEntity(String data) {
		StringReader reader = new StringReader(data);

		XmlTag tag;
		try {
			tag = (XmlTag) XmlTag.unmarshal(reader);
		} catch (MarshalException e) {
			return null;
		} catch (ValidationException e) {
			return null;
		}
		XMLEntity xml=new XMLEntity(tag);

		return xml;
	}

	public String getFromClipboard() {

		 try {
			return (String)Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	public boolean removeLayers(FLayer[] actives) {
    	for (int i = actives.length-1; i>=0; i--){
        	try {
				//actives[i].getParentLayer().removeLayer(actives[i]);
				//FLayers lyrs=getMapContext().getLayers();
				//lyrs.addLayer(actives[i]);
				actives[i].getParentLayer().removeLayer(actives[i]);
				PluginServices.getMainFrame().enableControls();
                if (actives[i] instanceof AlphanumericData){
                    Project project = ((ProjectExtension)PluginServices.getExtension(ProjectExtension.class)).getProject();
                    ProjectTable pt = project.getTable((AlphanumericData) actives[i]);

                    ArrayList tables = project.getDocumentsByType(ProjectTableFactory.registerName);
                    for (int j = 0; j < tables.size(); j++) {
                        if (tables.get(j) == pt){
                            project.delDocument((ProjectDocument)tables.get(j));
                            break;
                        }
                    }

                    PluginServices.getMDIManager().closeSingletonWindow(pt);
                }


    		} catch (CancelationException e1) {
    			e1.printStackTrace();
    			return false;
    		}
    	}
		return true;
	}

	public boolean removeDocuments(ProjectDocument[] selectedItems) {
		Project p = this.getProject();
		ProjectDocument element;
		int index;
		for (int i=selectedItems.length-1;i>=0;i--) {

			element = selectedItems[i];

			if (element instanceof ProjectMap) {

				if (element.isLocked()) {
					JOptionPane.showMessageDialog(
						(Component)PluginServices.getMainFrame(),
						PluginServices.getText(this, "locked_element_it_cannot_be_deleted") + ": " +element.getName()
					);
					//return false;
				} else {
					PluginServices.getMDIManager().closeSingletonWindow(element);
					p.delDocument(element);
				}
			} else if (element instanceof ProjectTable) {
				if (element.isLocked()) {
					JOptionPane.showMessageDialog(
						(Component)PluginServices.getMainFrame(),
						PluginServices.getText(this, "locked_element_it_cannot_be_deleted") + ": " +element.getName()
					);

					//return false;
				} else {
					PluginServices.getMDIManager().closeSingletonWindow(element);
					p.delDocument(element);
				}
			} else {
				if (element.isLocked()) {
					JOptionPane.showMessageDialog(
						(Component)PluginServices.getMainFrame(),
						PluginServices.getText(this, "locked_element_it_cannot_be_deleted") + ": " +element.getName()
					);
					//return false;
				} else {
					PluginServices.getMDIManager().closeSingletonWindow(element);
					p.delDocument(element);
				}
			}
		}
		return true;
	}

}