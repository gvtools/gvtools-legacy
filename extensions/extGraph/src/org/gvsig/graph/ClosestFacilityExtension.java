package org.gvsig.graph;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.gvsig.graph.core.Network;
import org.gvsig.graph.gui.solvers.ClosestFacilityDialog;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.iver.andami.PluginServices;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.MapContext;
import com.iver.cit.gvsig.fmap.MapControl;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.layers.CancelationException;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.FLayers;
import com.iver.cit.gvsig.fmap.layers.FLyrVect;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionEvent;
import com.iver.cit.gvsig.fmap.layers.LayerCollectionListener;
import com.iver.cit.gvsig.fmap.layers.LayerPositionEvent;
import com.iver.cit.gvsig.fmap.layers.SingleLayerIterator;
import com.iver.cit.gvsig.project.documents.view.gui.View;

public class ClosestFacilityExtension extends Extension implements LayerCollectionListener{
	
	private ClosestFacilityController cfc=null;
	private ClosestFacilityDialog cfd=null;
	private Network network;
	private MapControl mpCtrl;
	private PluginServices ps;
	
	public void execute(String actionCommand) {
		ps.getPluginServices(this);
		IWindow win = PluginServices.getMDIManager().getActiveWindow();
		boolean flagNetworkLayer=false;
		if (win instanceof View) {
			View view = (View) win;
			ArrayList layersTemp=new ArrayList();
			this.mpCtrl=view.getMapControl();
			FLayers fLayers=this.mpCtrl.getMapContext().getLayers();
			fLayers.addLayerCollectionListener(this);
			FLayer[] layers = fLayers.getVisibles();
			for (int i = 0; i < layers.length; i++) {
				try {
					if(layers[i] instanceof FLyrVect && ((FLyrVect)layers[i]).getShapeType()==FShape.POINT){
						layersTemp.add((FLyrVect)layers[i]);
					}
					
					if(!flagNetworkLayer && layers[i].isActive() && (this.network=(Network)layers[i].getProperty("network"))!=null){
						flagNetworkLayer=true;
					}
				} catch (ReadDriverException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(!flagNetworkLayer){
				JOptionPane.showMessageDialog(null, ps.getText("no_network_layer"), "Error", JOptionPane.ERROR_MESSAGE);;
			}
			else{
				this.cfc=new ClosestFacilityController(this.network, this.mpCtrl);
				this.cfd=new ClosestFacilityDialog(this.cfc);
				
				Iterator it=layersTemp.iterator();
				while(it.hasNext()){
					this.cfd.addFacilitiesLayer((FLyrVect)it.next());
				}
								
				PluginServices.getMDIManager().addWindow(cfd);
			}
		}
	}

	
	public void initialize() {
		PluginServices.getIconTheme().registerDefault(
				"closest_facility",
				this.getClass().getClassLoader().getResource("images/closest_facility.png")
			);		

	}

	public boolean isEnabled() {
		IWindow window = PluginServices.getMDIManager().getActiveWindow();
		if (window instanceof View)
		{
			View v = (View) window;
	        MapControl mapCtrl = v.getMapControl();
			MapContext map = mapCtrl.getMapContext();
			
			SingleLayerIterator it = new SingleLayerIterator(map.getLayers());
			while (it.hasNext())
			{
				FLayer aux = it.next();
				if (!aux.isActive())
					continue;
				Network net = (Network) aux.getProperty("network");
				
				if ( net != null)
				{
					return true;
				}
			}
		}
		return false;
	}

	public boolean isVisible() {
		// TODO Auto-generated method stub
		IWindow f = PluginServices.getMDIManager()
		 .getActiveWindow();
		if (f == null) {
		    return false;
		}
		if (f instanceof View) {
			return true;
		}
		return false;
	}

	public void layerAdded(LayerCollectionEvent e) {
		FLayer layer=e.getAffectedLayer();
		try {
			if(layer instanceof FLyrVect && ((FLyrVect)layer).getShapeType()==FShape.POINT){
				this.cfd.addFacilitiesLayer((FLyrVect)layer);
				this.mpCtrl.drawGraphics();
			}
		} catch (ReadDriverException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void layerAdding(LayerCollectionEvent e) throws CancelationException {
	}

	public void layerMoved(LayerPositionEvent e) {
	}

	public void layerMoving(LayerPositionEvent e) throws CancelationException {
	}

	public void layerRemoved(LayerCollectionEvent e) {
		FLayer layer=e.getAffectedLayer();
		try {
			if(layer instanceof FLyrVect && ((FLyrVect)layer).getShapeType()==FShape.POINT){
				this.cfd.removeFacilitiesLayer((FLyrVect)layer);
			}
		} catch (ReadDriverException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void layerRemoving(LayerCollectionEvent e) throws CancelationException {
	}

	public void visibilityChanged(LayerCollectionEvent e) throws CancelationException {
		FLayer layer=e.getAffectedLayer();
		try{
			if(layer instanceof FLyrVect && ((FLyrVect)layer).getShapeType()==FShape.POINT){
				FLyrVect layerVect=(FLyrVect)layer;
				if(layerVect.isVisible()){
					this.cfd.addFacilitiesLayer(layerVect);
				}
				else{
					this.cfd.removeFacilitiesLayer(layerVect);
				}
			}
		}
		catch(ReadDriverException except){
			except.printStackTrace();
		}
	}
}
