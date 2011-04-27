/*
 * Created on 21-jun-2006
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
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
/* CVS MESSAGES:
 *
 * $Id: GeoprocessTree.java 6490 2006-07-21 09:32:01Z azabala $
 * $Log$
 * Revision 1.4  2006-07-21 09:32:01  azabala
 * fixed bug 644: disabling ok button untill user select a geoprocess
 *
 * Revision 1.3  2006/06/27 16:14:29  azabala
 * added geoprocess panel opening with user mouse interaction
 *
 * Revision 1.2  2006/06/23 19:04:23  azabala
 * bug for tree creation by namespacies resolved
 *
 * Revision 1.1  2006/06/22 17:46:30  azabala
 * first version in cvs
 *
 *
 */
package com.iver.cit.gvsig.geoprocess.manager;

import java.awt.event.MouseListener;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.geoprocess.core.IGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.buffer.BufferGeoprocessPlugin;
import com.iver.cit.gvsig.geoprocess.impl.clip.ClipGeoprocessPlugin;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;

/**
 * This component shows all registered geoprocesses in extension point
 * "GeoprocessManager", in a tree style.
 * 
 * Its different subnodes represents an organization
 * of geoprocesses.
 * 
 * Leaf nodes are IGeoprocessPlugin instances.
 * 
 * @author azabala
 * 
 */
public class GeoprocessTree extends JScrollPane implements IGeoprocessTree {
	private static final long serialVersionUID = -6244491453178280294L;
	private JTree tree;
	DefaultMutableTreeNode root;
	final  GeoprocessTreeDirectory ROOT = new GeoprocessTreeDirectory();
	
	public GeoprocessTree() {
		super();
		root = new DefaultMutableTreeNode();
		ROOT.description = "";
		root.setUserObject(ROOT);
		tree = new JTree(root);
		loadGeoprocesses();
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
//		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		setViewportView(tree);
		// tree.setCellRenderer( new JTreeEntidadesRenderer(listeners) );
	}
	
	public static void main(String[] args){
		JFrame f = new JFrame();
		ExtensionPoints extensionPoints = 
			ExtensionPointsSingleton.getInstance();
		extensionPoints.add("GeoprocessManager","BUFFER", BufferGeoprocessPlugin.class);
		extensionPoints.add("GeoprocessManager","CLIP", ClipGeoprocessPlugin.class);
		GeoprocessManager tree = new GeoprocessManager();
		f.getContentPane().add(tree);
		f.setSize(800,600);
		f.setVisible(true);
	}

	private void loadGeoprocesses() {
		ExtensionPoints extensionPoints = 
			ExtensionPointsSingleton.getInstance();
		ExtensionPoint geoprocessManager = 
			(ExtensionPoint)extensionPoints.get("GeoprocessManager"); 
		if(geoprocessManager == null)
			return;
		Iterator i = geoprocessManager.keySet().iterator();
		while( i.hasNext() ) { 
			String nombre = (String)i.next(); 
			Class metadataClass =
				(Class) geoprocessManager.get(nombre);
			try {
				register((IGeoprocessPlugin)metadataClass.newInstance());
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Registers a new geoprocess (linked to the plugin)
	 * in the geoprocess manager.
	 */
	public void register(IGeoprocessPlugin metadata) {
		String namespace = metadata.getNamespace();
		String[] directories = getObjectsInPath(namespace);
		DefaultMutableTreeNode bestMatch = root;
		DefaultMutableTreeNode scanned = null;
		int levelNum = 0;
		boolean doit = true;
		while(doit){
			int numChilds = bestMatch.getChildCount();
			if(numChilds == 0)
				break;
			boolean match = true;
			for(int i = 0; i < numChilds; i++){
				match = true;//is true if we dont verify is false
				scanned =
					(DefaultMutableTreeNode) bestMatch.getChildAt(i);
				if(scanned.isLeaf() || 
						(scanned.getUserObject() instanceof IGeoprocessPlugin))//this is a geoprocess, not a directory
				{
					doit = false;
					if(scanned.getUserObject().getClass() == metadata.getClass()){
						//we are trying to add the same geoprocess twice
						return;
					}	
					break;
				}
				
				GeoprocessTreeDirectory path =
					(GeoprocessTreeDirectory) scanned.getUserObject();
				String[] pathStr = path.getPath();
				//verify the length of the path
				for(int j = 0; j < pathStr.length; j++){
					if(!pathStr[j].equalsIgnoreCase(directories[j])){
						match = false;
						break;
					}
				}//for num paths
				if(match){
					bestMatch = scanned;
					break;
				}
			}//for numChilds
			
			if(! match)//si en el escaneo de nivel no se encontro nada, paramos
				doit = false;
			else
				levelNum++;
		}//while
		
		//Llegados a este punto, tenemos el nodo que mejor casa
		//si el numero de niveles recorridos es directories.length -1
		//lo añadimos directamente. Si no, hay que crear nuevos niveles
		DefaultMutableTreeNode gpNode
			= new DefaultMutableTreeNode();
		gpNode.setUserObject(metadata);
		if(levelNum == directories.length -1){
			bestMatch.add(gpNode);
		}else{
			int numNewNodes = (directories.length - 1) - levelNum;
			DefaultMutableTreeNode prev = bestMatch;
			for(int i = 0; i < numNewNodes; i++){
				DefaultMutableTreeNode newNode = 
					new DefaultMutableTreeNode();
				GeoprocessTreeDirectory path
					= new GeoprocessTreeDirectory();
				String[] newPath = new String[levelNum + i + 1];
				System.arraycopy(directories, 0, newPath, 0, (levelNum + i + 1));
				path.path = newPath;
				newNode.setUserObject(path);
				String packageName = "";
				for(int j = 0; j < newPath.length -1; j++){
					packageName += newPath[j];
					packageName += "/";
					
				}
				packageName += newPath[newPath.length -1];
				String description = GeoprocessManager.
					getDescriptionFor(packageName);
				path.description = description;
				prev.add(newNode);
				prev = newNode;
			}//for
			prev.add(gpNode);
		}//else
		
	}

	

	public IGeoprocessPlugin getGeoprocess() {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
				.getLastSelectedPathComponent();
		Object nodeInfo = node.getUserObject();
		if ((node == null) || !(nodeInfo instanceof IGeoprocessPlugin))
			return null;
		else {
			return (IGeoprocessPlugin)nodeInfo;
		}
	}


	/**
	 * A directory of the geoprocesses path.
	 * 
	 * @author azabala
	 * 
	 */
	class GeoprocessTreeDirectory {
		String[] path;

		String description;
		
		public boolean equals(Object o) {
			if (!(o instanceof GeoprocessTreeDirectory))
				return false;
			GeoprocessTreeDirectory d = (GeoprocessTreeDirectory) o;
			for(int i = 0; i < path.length; i++){
				if(!path[i].equalsIgnoreCase(d.path[i]))
					return false;
			}
			return true;
		}

		public String[] getPath() {
			return path;
		}

		public String getDescription() {
			return description;
		}
		
		public String toString(){
			if(path != null && path.length > 0)
				return path[path.length-1];
			else
				return PluginServices.getText(this, 
							"Geoprocesos");
		}
	}

	private String[] getObjectsInPath(String path) {
		return path.split(PATH_SEPARATOR);
	}

	public void addTreeSelectionListener(TreeSelectionListener l) {
		tree.addTreeSelectionListener(l);
	}
	
	public void addMouseListener(MouseListener l){
		tree.addMouseListener(l);
	}
}
