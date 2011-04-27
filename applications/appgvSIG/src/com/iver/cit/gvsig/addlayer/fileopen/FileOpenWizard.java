/* gvSIG. Sistema de Informaci�n Geogr�fica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
 */
package com.iver.cit.gvsig.addlayer.fileopen;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.cresques.cts.IProjection;
import org.gvsig.gui.beans.swing.JFileChooser;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.addlayer.AddLayerDialog;
import com.iver.cit.gvsig.exceptions.layers.LoadLayerException;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.crs.CRSFactory;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.gui.WizardPanel;
import com.iver.cit.gvsig.gui.panels.CRSSelectPanel;
import com.iver.cit.gvsig.project.documents.gui.ListManagerSkin;
import com.iver.cit.gvsig.project.documents.view.gui.BaseView;
import com.iver.cit.gvsig.project.documents.view.gui.FPanelLocConfig;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.iver.utiles.listManager.ListManagerListener;

/**
 * Pesta�a donde estara la apertura de ficheros
 *
 * @version 04/09/2007
 * @author BorSanZa - Borja S�nchez Zamorano (borja.sanchez@iver.es)
 */
public class FileOpenWizard extends WizardPanel implements ListManagerListener {
	private static final String OPEN_LAYER_FILE_CHOOSER_ID = "OPEN_LAYER_FILE_CHOOSER_ID";
	private static final long serialVersionUID = 335310147513197564L;
	private static String     lastPath        = null;
	private JPanel            jPanel2         = null;
	private JFileChooser      fileChooser     = null;
	private CRSSelectPanel    jPanelProj      = null;
	private ListManagerSkin   listManagerSkin = null;
	private boolean           projection      = false;
	static private FileFilter lastFileFilter  = null;
	private TitledBorder      titledBorder    = null;

	/**
	 * Lista de manejadores de ficheros (extensiones)
	 */
	private ArrayList<IFileOpen> listFileOpen = new ArrayList<IFileOpen>();

	/**
	 * Construye un FileOpenWizard usando la extension por defecto
	 * 'FileExtendingOpenDialog'
	 */
	public FileOpenWizard() {
		this("FileExtendingOpenDialog");
	}

	/**
	 * Construye un FileOpenWizard usando el punto de extension pasado por
	 * parametro
	 * @param nameExtension
	 */
	public FileOpenWizard(String nameExtension) {
		this(nameExtension, true);
	}
	/**
	 * Construye un FileOpenWizard usando el punto de extension pasado por
	 * parametro
	 * @param nameExtension
	 */
	public FileOpenWizard(String nameExtension, boolean proj) {
		ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
		ExtensionPoint extensionPoint = (ExtensionPoint) extensionPoints.get(nameExtension);
		if (extensionPoint == null)
			return;

		Iterator iterator = extensionPoint.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			Object obj = extensionPoint.get(key);
			if (obj instanceof Class) {
				try {
					obj = ((Class) obj).getConstructor(null).newInstance(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (obj instanceof IFileOpen) {
				listFileOpen.add((IFileOpen) obj);
			}
		}
		init(null, proj);
	}

	/**
	 * Creates a new FileOpenWizard object.
	 * @param driverClasses
	 * @param proj
	 */
	public FileOpenWizard(IFileOpen[] driverClasses, boolean proj) {
		init(driverClasses, proj);
	}

	/**
	 * Creates a new FileOpenWizard object.
	 * @param driverClasses
	 */
	public FileOpenWizard(IFileOpen[] driverClasses) {
		init(driverClasses, true);
	}

	/**
	 * Creates a new FileOpenWizard object.
	 * @param driverClasses
	 * @param proj
	 * @param title
	 */
	public FileOpenWizard(IFileOpen[] driverClasses, boolean proj, String title) {
		setTitle(title);
		init(driverClasses, proj);
	}

	/**
	 * @param driverClasses2
	 * @param b
	 */
	private void init(IFileOpen[] driverClasses, boolean projection) {
		this.projection = projection;

		if (driverClasses != null)
			for (int i = 0; i < driverClasses.length; i++)
				listFileOpen.add(driverClasses[i]);

		if (lastPath == null) {
			Preferences prefs = Preferences.userRoot().node("gvsig.foldering");
			lastPath = prefs.get("DataFolder", null);
		}

		initialize();
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.WizardPanel#initWizard()
	 */
	public void initWizard() {
		setTabName(PluginServices.getText(this, "Fichero"));
		init(null, true);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(514, 280);
		this.setLayout(null);
		this.add(getJPanel2(), null);
	}

	public File[] getFiles() {
		MyFile[] files = (MyFile[]) getListManagerSkin().getListManager().getListModel().getObjects().toArray(new MyFile[0]);
		File[] ret = new File[files.length];
		int pos = files.length - 1;
		for (int i = 0; i < files.length; i++) {
			ret[pos] = files[i].getFile();
			pos--;
		}
		return ret;
	}

	public MyFile[] getMyFiles() {
		return (MyFile[]) getListManagerSkin().getListManager().getListModel().getObjects().toArray(new MyFile[0]);
	}

	/**
	 * This method initializes jPanel2
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jPanel2 = new JPanel();
			jPanel2.setLayout(null);
			jPanel2.setBorder(getTitledBorder());
			jPanel2.setPreferredSize(new Dimension(380, 200));
			jPanel2.setBounds(2, 2, 506, 472);
			if (projection)
				jPanel2.add(getJPanelProj(), null);
			jPanel2.add(getListManagerSkin(), null);
		}

		return jPanel2;
	}

	private TitledBorder getTitledBorder() {
		if (titledBorder == null) {
			titledBorder = BorderFactory.createTitledBorder(null, PluginServices.getText(this, "Seleccionar_fichero"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null);
			titledBorder.setTitle(PluginServices.getText(this, PluginServices.getText(this, "Capas")));
		}
		return titledBorder;
	}

	public String[] getDriverNames() {
		MyFile[] files = (MyFile[]) getListManagerSkin().getListManager().getListModel().getObjects().toArray(new MyFile[0]);
		String[] ret = new String[files.length];
		int pos = files.length - 1;

		for (int i = 0; i < files.length; i++) {
			ret[pos] = files[i].getDriverName();
			pos--;
		}
		return ret;
	}

	/**
	 * This method initializes jPanel
	 * @return javax.swing.JPanel
	 */
	private CRSSelectPanel getJPanelProj() {
		if (jPanelProj == null) {
			IProjection proj = CRSFactory.getCRS("EPSG:23030");
			if (PluginServices.getMainFrame() != null) {
				proj = AddLayerDialog.getLastProjection();
			}

			jPanelProj = CRSSelectPanel.getPanel(proj);
			jPanelProj.setTransPanelActive(true);
			jPanelProj.setBounds(11, 400, 448, 35);
			jPanelProj.setPreferredSize(new Dimension(448, 35));
			jPanelProj.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (jPanelProj.isOkPressed()) {
						AddLayerDialog.setLastProjection(jPanelProj.getCurProj());
					}
				}
			});

		}
		return jPanelProj;
	}


	/**
	 * This method initializes listManagerDemoSkin
	 * @return ListManagerSkin
	 */
	private ListManagerSkin getListManagerSkin() {
		if (listManagerSkin == null) {
			listManagerSkin = new ListManagerSkin(false);
			listManagerSkin.setBounds(11, 21, 491, 363);
			listManagerSkin.getListManager().setListener(this);
		}
		return listManagerSkin;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.utiles.listManager.ListManagerListener#addObjects()
	 */
	public Object[] addObjects() {
		this.callStateChanged(true);
		fileChooser = new JFileChooser(OPEN_LAYER_FILE_CHOOSER_ID, lastPath);
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setAcceptAllFileFilterUsed(false);

		boolean finded = false;
		FileFilter auxFilter=null;
		for (int i = 0; i < listFileOpen.size(); i++) {
			IFileOpen fileOpen = listFileOpen.get(i);
			fileOpen.pre();
			ArrayList<FileFilter> aux = fileOpen.getFileFilter();
			
			for (int j = 0; j < aux.size(); j++) {
				FileFilter fileFilter = aux.get(j);				
				fileChooser.addChoosableFileFilter(fileFilter);
				if (lastFileFilter!=null && lastFileFilter.getDescription().equals(fileFilter.getDescription())){
					auxFilter=fileFilter;
					finded = true;
				}
			}
		}
		if (finded && (lastFileFilter != null))
			fileChooser.setFileFilter(auxFilter);

		int result = fileChooser.showOpenDialog(this);

		File[] newFiles = null;
		ArrayList<MyFile> toAdd = new ArrayList<MyFile>();
		if (result == JFileChooser.APPROVE_OPTION) {
			lastPath = fileChooser.getCurrentDirectory().getAbsolutePath();
			lastFileFilter = (FileFilter) fileChooser.getFileFilter();
			newFiles = fileChooser.getSelectedFiles();

			IFileOpen lastFileOpen = null;
			for (int i = 0; i < listFileOpen.size(); i++) {
				IFileOpen fileOpen = listFileOpen.get(i);
				ArrayList<FileFilter> aux = fileOpen.getFileFilter();
				for (int j = 0; j < aux.size(); j++) {
					if (fileChooser.getFileFilter() == aux.get(j)) {
						for (int iFile = 0; iFile < newFiles.length; iFile++) {
							try {
								newFiles[iFile] = fileOpen.post(newFiles[iFile]);
							} catch (LoadLayerException e) {
								newFiles[iFile] = null;
							}
						}
						lastFileOpen = fileOpen;
						break;
					}
				}
			}

			for (int ind = 0; ind < newFiles.length; ind++) {
				if (newFiles[ind] == null)
					continue;
				
				
				String driverName;

				/* default: */
				driverName = ((FileFilter) fileChooser.getFileFilter()).getDescription();
				
				/* translate known file chooser names to internal driver names */
				if ( fileChooser.getFileFilter().getDescription().equals(PluginServices.getText(this, "Ficheros_SHP")) ) {
					driverName = "gvSIG shp driver";
				}
				if ( fileChooser.getFileFilter().getDescription().equals(PluginServices.getText(this, "Ficheros_DGN")) ) {
					driverName = "gvSIG DGN Memory Driver";
				}
				if ( fileChooser.getFileFilter().getDescription().equals(PluginServices.getText(this, "Ficheros_DWG")) ) {
					driverName = "gvSIG DWG Memory Driver";
				}				
				if ( fileChooser.getFileFilter().getDescription().equals(PluginServices.getText(this, "dxf_files")) ) {
					driverName = "gvSIG DXF Memory Driver";
				}
				if ( fileChooser.getFileFilter().getDescription().equals(PluginServices.getText(this, "gml_files")) ) {
					driverName = "gvSIG GML Memory Driver";
				}
				if ( fileChooser.getFileFilter().getDescription().equals(PluginServices.getText(this, "Ficheros_KML")) ) {
					driverName = "gvSIG KML Memory Driver";
				}

				if ( fileChooser.getFileFilter().getDescription().equals(PluginServices.getText(this, "Ficheros_dbf")) ) {
					driverName = "gdbms dbf driver";
				}

				if ( fileChooser.getFileFilter().getDescription().equals(PluginServices.getText(this, "Ficheros_csv")) ) {
					driverName = "csv string";
				}
				
				/* add file to list of layers to add */
				toAdd.add(new MyFile(newFiles[ind], driverName, lastFileOpen));
			}

			return toAdd.toArray();
		} else
			return new Object[0];
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.WizardPanel#execute()
	 */
	public void execute() {
		if (getFiles() == null)
			return;

		MapControl mapControl = null;
		IWindow[] w = PluginServices.getMDIManager().getAllWindows();

		// Si se est� cargando la capa en el localizador se obtiene el mapcontrol de este
		for (int i = 0; i < w.length; i++) {
			if (w[i] instanceof FPanelLocConfig) {
				mapControl = ((FPanelLocConfig) w[i]).getMapCtrl();
				DefaultListModel lstModel = (DefaultListModel) ((FPanelLocConfig) w[i]).getJList().getModel();
				lstModel.clear();
				for (int k = 0; k < getFiles().length; k++)
					lstModel.addElement(getFiles()[k].getName());
				for (int j = mapControl.getMapContext().getLayers().getLayersCount() - 1; j >= 0; j--) {
					FLayer lyr = mapControl.getMapContext().getLayers().getLayer(j);
					lstModel.addElement(lyr.getName());
				}
			}
		}

		// Obtiene la primera vista activa
		if (mapControl == null) {
			for (int i = 0; i < w.length; i++) {
				IWindow activeWindow = PluginServices.getMDIManager().getActiveWindow();
				if (w[i] instanceof BaseView && w[i].equals(activeWindow))
					mapControl = ((BaseView) w[i]).getMapControl();
			}
		}
		// Si no hay ninguna activa obtiene la primera vista aunque no est� activa
		if (mapControl == null) {
			for (int i = 0; i < w.length; i++) {
				if (w[i] instanceof BaseView)
					mapControl = ((BaseView) w[i]).getMapControl();
			}
		}

		if (mapControl == null)
			return;

		Rectangle2D[] rects = new Rectangle2D[getFiles().length];
		boolean first = false;

		for (int i = getMyFiles().length - 1; i >= 0; i--) {
			if (mapControl.getMapContext().getViewPort().getExtent() == null)
				first = true;
			rects[i] = ((MyFile) getMyFiles()[i]).createLayer(mapControl);
		}

		if (first && (rects.length > 1)) {
			Rectangle2D rect = new Rectangle2D.Double();
			rect.setRect(rects[0]);
			for (int i = 0; i < rects.length; i++)
				if (rects[i] != null)
					rect.add(rects[i]);
			mapControl.getMapContext().getViewPort().setExtent(rect);
		}
		mapControl.getMapContext().endAtomicEvent();
	}

	public void setTitle(String title) {
		getTitledBorder().setTitle(title);
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.utiles.listManager.ListManagerListener#getProperties(java.lang.Object)
	 */
	public Object getProperties(Object selected) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.iver.cit.gvsig.gui.WizardPanel#getLayer()
	 */
	public FLayer getLayer() {
		return null;
	}

	/**
	 * Obtiene la �ltima ruta seleccionada al a�adir ficheros.
	 * @return Ruta del �ltimo fichero seleccionado
	 */
	public static String getLastPath() {
		return lastPath;
	}

	/**
	 * Asigna la �ltima ruta en una selecci�n de ficheros de disco. Es necesario
	 * poder hacer esta asignaci�n desde fuera de FileOpenWizard ya que este path debe
	 * ser com�n a toda la aplicaci�n. Hay otros puntos donde se seleccionan ficheros
	 * de disco.
	 * @param lastPath Ruta del �ltimo fichero de disco seleccionado
	 */
	public static void setLastPath(String path) {
		lastPath = path;
	}
}