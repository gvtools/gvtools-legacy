package org.gvsig.graph.gui.solvers;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.iver.andami.PluginServices;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.utiles.swing.JComboBox;

public class ClosestFacilityProperties extends JPanel implements IWindow, ActionListener {
    
	private JPanel panelMain;
    private JButton buttonAccept;
    private JButton buttonCancel;
    private JComboBox comboCostFieldValues;
    private JComboBox comboRoundValues;
    private JLabel labelCostField;
    private JLabel labelCostUnits;
    private JLabel labelCostUnitsValue;
    private JLabel labelLinesLayer;
    private JLabel labelLinesLayerValue;
    private JLabel labelRoundValues;
    
    private WindowInfo wi;
    private Hashtable properties;
	
    public ClosestFacilityProperties(Hashtable properties) {
    	this.properties=properties;
        initComponents();
    }
    
    private void initComponents() {
        labelLinesLayer = new JLabel("Capa de líneas:", JLabel.RIGHT);
        String linesLayerName=null;
        try{
        	linesLayerName=(String)this.properties.get("LINES_LAYER_NAME");
        }
        catch(NullPointerException except){
        	
        }
        if(linesLayerName==null) linesLayerName="<sin especificar>";
        labelLinesLayerValue = new JLabel(linesLayerName, JLabel.LEFT);
        labelCostField = new JLabel("Campo de coste:", JLabel.RIGHT);
        Iterator it=null;
        try{
        	it=((ArrayList)properties.get("LAYER_FIELDS")).iterator();
        }
        catch(NullPointerException except){
        	
        }
        comboCostFieldValues = new JComboBox();
        comboCostFieldValues.addItem("<Longitud de línea>");
        if(it!=null){
        	while(it.hasNext()){
        		comboCostFieldValues.addItem(String.valueOf(it.next()));
        	}
        }
        try{
        	comboCostFieldValues.setSelectedIndex(0);
        	String fieldName=(String)this.properties.get("COST_FIELD");
        	if(fieldName!=null) comboCostFieldValues.setSelectedItem(fieldName);
        }
        catch(NullPointerException except){
        	
        }
        
        labelCostUnits = new JLabel("Unidades de coste:", JLabel.RIGHT);
        labelCostUnitsValue = new JLabel("<sin especificar>", JLabel.LEFT);
        labelRoundValues = new JLabel("Valores redondeados a:", JLabel.RIGHT);
        comboRoundValues = new JComboBox();
        comboRoundValues.addItem("d");
        comboRoundValues.addItem("d.d");
        comboRoundValues.addItem("d.dd");
        comboRoundValues.addItem("d.ddd");
        comboRoundValues.addItem("d.dddd");
        comboRoundValues.addItem("d.ddddd");
        try{
        	int roundValue=((Integer)this.properties.get("ROUND_VALUE")).intValue();
        	comboRoundValues.setSelectedIndex(roundValue);
        }
        catch(IndexOutOfBoundsException except){

        }
        catch(NullPointerException except){
        	
        }
        buttonAccept = new JButton(PluginServices.getText(this,"Aceptar"));
        this.buttonAccept.addActionListener(this);
        buttonCancel = new JButton(PluginServices.getText(this,"Cancelar"));
        this.buttonCancel.addActionListener(this);
        
        this.panelMain=new JPanel();
        this.panelMain.setLayout(new GridLayout(6, 2, 7, 7));
        this.panelMain.add(labelLinesLayer);
        this.panelMain.add(labelLinesLayerValue);
        this.panelMain.add(labelCostField);
        this.panelMain.add(comboCostFieldValues);
        this.panelMain.add(labelCostUnits);
        this.panelMain.add(labelCostUnitsValue);
        this.panelMain.add(labelRoundValues);
        this.panelMain.add(comboRoundValues);
        this.panelMain.add(new JPanel());
        this.panelMain.add(new JPanel());
        this.panelMain.add(buttonAccept);
        this.panelMain.add(buttonCancel);
        
        this.setLayout(new BorderLayout(50, 50));
        this.add(this.panelMain, BorderLayout.CENTER);
    }
    
    public Hashtable getProperties(){
    	return this.properties;
    }
    
	public WindowInfo getWindowInfo() {
		if(this.wi==null){
			this.wi=new WindowInfo(WindowInfo.MODALDIALOG);
			this.wi.setTitle("Propiedades");
			this.wi.setWidth(300);
			this.wi.setHeight(150);
		}
		
		return wi;
	}
	
	public Object getWindowProfile(){
		return WindowInfo.DIALOG_PROFILE;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.buttonAccept){
			String costFieldName=null;
			try{
				costFieldName=(String)this.comboCostFieldValues.getSelectedItem();
				this.properties.put("COST_FIELD", costFieldName);
			}
			catch(NullPointerException except){
				
			}
			try{
				this.properties.put("ROUND_VALUE", Integer.valueOf(this.comboRoundValues.getSelectedIndex()));
			}
			catch(NullPointerException except){
				
			}
			PluginServices.getMDIManager().closeWindow(this);
		}
		else if(e.getSource()==this.buttonCancel){
			PluginServices.getMDIManager().closeWindow(this);
		}
	}
}
