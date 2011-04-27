package org.gvsig.graph.gui.wizard.servicearea;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.gvsig.graph.ServiceAreaController;
import org.gvsig.graph.core.Network;

import jwizardcomponent.FinishAction;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.andami.ui.wizard.WizardAndami;

public class ServiceAreaWizard extends WizardAndami implements IWindow{
	
	private WindowInfo wi;
	
	/**
	 * flag that tells us if user pressed finish button
	 * or cancel button
	 * */
	private boolean wasFinishPressed = false;
	
	private ServiceAreaController controller;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2257543774634561419L;
	
	private Network network;

	public ServiceAreaWizard(ImageIcon logo, Network network){
		super(logo);
		
		this.network=network;
		this.controller=new ServiceAreaController();
		
		this.getWizardComponents().addWizardPanel(new ServiceAreaPage0(this));
		this.getWizardComponents().addWizardPanel(new ServiceAreaPage1(this));
		this.getWizardComponents().addWizardPanel(new ServiceAreaPage2(this));
		this.getWizardComponents().addWizardPanel(new ServiceAreaPage3(this));
		this.getWizardComponents().addWizardPanel(new ServiceAreaPage4(this));
		this.getWizardComponents().setFinishAction(new ServiceAreaFinishAction(this));		
	}
	
	public Network getNetwork(){
		return this.network;
	}
	
    public void setWasFinishPressed(boolean wasFinish){
    	this.wasFinishPressed = wasFinish;
    }
    
    public boolean wasFinishPressed(){
    	return wasFinishPressed;
    }
    
    public void setController(ServiceAreaController controller){
    	this.controller=controller;
    }
    
    public ServiceAreaController getController(){
    	return this.controller;
    }

    public WindowInfo getWindowInfo() {
        if (wi==null) {
            wi = new WindowInfo(WindowInfo.RESIZABLE | WindowInfo.MODALDIALOG);
            wi.setWidth(600);
            wi.setHeight(500);
            wi.setMinimumSize(new Dimension(600, 500));
            wi.setTitle(PluginServices.getText(this, "Crear area de servicio") + "...");
        }
        return wi;
    }
	
	private class ServiceAreaFinishAction extends FinishAction
	{
		ServiceAreaWizard wizard;
		public ServiceAreaFinishAction(ServiceAreaWizard wizard)
		{
			super(wizard.getWizardComponents());
			this.wizard=wizard;
		}
		public void performAction() {
			if(this.wizard.getController().getModel()==null){
				JOptionPane.showMessageDialog(this.wizard, "No se han establecido los puntos para crear las áreas de servicio", "Error", JOptionPane.ERROR_MESSAGE);
			}
			else{
				PluginServices.getMDIManager().closeWindow(this.wizard);
				this.wizard.getController().solve();
			}
		}

	}
}