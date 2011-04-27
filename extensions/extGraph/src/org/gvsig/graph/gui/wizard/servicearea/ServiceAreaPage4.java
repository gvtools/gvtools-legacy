package org.gvsig.graph.gui.wizard.servicearea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import jwizardcomponent.JWizardPanel;

import com.iver.andami.PluginServices;

public class ServiceAreaPage4 extends JWizardPanel implements ActionListener, MouseListener{
	private JLabel labelChooseArea;
	private JPanel panelAreaTypes;
	private ButtonGroup groupAreaTypes;
	private JPanel panelAreaTypesDisks;
	private TitledBorder titledBorderAreaTypesDisks;
	private JRadioButton radioAreaTypesDisks;
	private JPanel panelAreaTypesDisksPicture;
	private JLabel labelAreaTypesDisksPicture;
	private JPanel panelAreaTypesNonRings;
	private TitledBorder titledBorderAreaTypesRings;
	private JRadioButton radioAreaTypesRings;
	private JPanel panelAreaTypesRingsPicture;
	private JLabel labelAreaTypesRingsPicture;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5139067753651358490L;
	
	private ServiceAreaWizard owner;
	
	public ServiceAreaPage4(ServiceAreaWizard wizard){
		super(wizard.getWizardComponents());
		this.owner=wizard;
		this.initialize();
	}
	
	private void initialize(){
		this.labelChooseArea=new JLabel("<html><b>Tipo de áreas:</b></html>", JLabel.LEFT);
		this.panelAreaTypes=new JPanel();
		this.groupAreaTypes=new ButtonGroup();
		this.panelAreaTypesDisks=new JPanel();
		this.titledBorderAreaTypesDisks=new TitledBorder("Áreas de disco");
		this.panelAreaTypesDisks.setBorder(this.titledBorderAreaTypesDisks);
		this.radioAreaTypesDisks=new JRadioButton("<html>Las <b>áreas de disco</b> se crean formando polígonos que representan cada una de las áreas de servicio</html>");
		this.radioAreaTypesDisks.addActionListener(this);
		this.radioAreaTypesDisks.setSelected(true);
		this.groupAreaTypes.add(this.radioAreaTypesDisks);
		this.panelAreaTypesDisksPicture=new JPanel();
		this.labelAreaTypesDisksPicture=new JLabel();
		this.labelAreaTypesDisksPicture.setOpaque(true);
		this.labelAreaTypesDisksPicture.setIcon(PluginServices.getIconTheme().get("service_area_disks"));
		this.labelAreaTypesDisksPicture.setEnabled(true);
		this.labelAreaTypesDisksPicture.addMouseListener(this);
		this.panelAreaTypesNonRings=new JPanel();
		this.titledBorderAreaTypesRings=new TitledBorder("Áreas de anillo");
		this.panelAreaTypesNonRings.setBorder(this.titledBorderAreaTypesRings);
		this.radioAreaTypesRings=new JRadioButton("<html>Las <b>áreas de anillo</b> solo muestran una línea que delimita cada una de las áreas de servicio</html>");
		this.radioAreaTypesRings.addActionListener(this);
		this.groupAreaTypes.add(this.radioAreaTypesRings);
		this.panelAreaTypesRingsPicture=new JPanel();
		this.labelAreaTypesRingsPicture=new JLabel();
		this.labelAreaTypesRingsPicture.setOpaque(true);
		this.labelAreaTypesRingsPicture.setBackground(Color.BLUE);
		this.labelAreaTypesRingsPicture.setIcon(PluginServices.getIconTheme().get("service_area_rings"));
		this.labelAreaTypesRingsPicture.setEnabled(false);
		this.labelAreaTypesRingsPicture.addMouseListener(this);
		
		this.labelAreaTypesDisksPicture.setSize(this.labelAreaTypesDisksPicture.getIcon().getIconWidth(), this.labelAreaTypesDisksPicture.getIcon().getIconHeight());
		this.panelAreaTypesDisksPicture.add(this.labelAreaTypesDisksPicture);
		this.panelAreaTypesDisksPicture.setSize(this.labelAreaTypesDisksPicture.getSize());
		this.panelAreaTypesDisks.setLayout(new GridLayout(2, 1, 7, 7));
		this.panelAreaTypesDisks.add(this.radioAreaTypesDisks);
		this.panelAreaTypesDisks.add(this.panelAreaTypesDisksPicture);
		
		this.labelAreaTypesRingsPicture.setSize(this.labelAreaTypesRingsPicture.getIcon().getIconWidth(), this.labelAreaTypesRingsPicture.getIcon().getIconHeight());
		this.panelAreaTypesRingsPicture.add(this.labelAreaTypesRingsPicture);
		this.panelAreaTypesRingsPicture.setSize(this.labelAreaTypesRingsPicture.getSize());
		this.panelAreaTypesNonRings.setLayout(new GridLayout(2, 1, 7, 7));
		this.panelAreaTypesNonRings.add(this.radioAreaTypesRings);
		this.panelAreaTypesNonRings.add(this.panelAreaTypesRingsPicture);
		
		this.panelAreaTypes.setLayout(new GridLayout(2, 1, 7, 7));
		this.panelAreaTypes.add(this.panelAreaTypesDisks);
		this.panelAreaTypes.add(this.panelAreaTypesNonRings);
		
		this.setLayout(new BorderLayout(7, 7));
		this.add(this.labelChooseArea, BorderLayout.NORTH);
		this.add(this.panelAreaTypes, BorderLayout.CENTER);
		
		this.radioAreaTypesDisks.doClick();
	}
	
	public void next(){
		super.next();
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.radioAreaTypesDisks){
			this.labelAreaTypesDisksPicture.setEnabled(true);
			this.labelAreaTypesRingsPicture.setEnabled(false);
			this.owner.getController().setRingAreas(false);
		}
		else if(e.getSource()==this.radioAreaTypesRings){
			this.labelAreaTypesDisksPicture.setEnabled(false);
			this.labelAreaTypesRingsPicture.setEnabled(true);
			this.owner.getController().setRingAreas(true);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getSource()==this.labelAreaTypesDisksPicture){
			this.radioAreaTypesDisks.setSelected(true);
			this.radioAreaTypesDisks.doClick();
		}
		else if(e.getSource()==this.labelAreaTypesRingsPicture){
			this.radioAreaTypesRings.setSelected(true);
			this.radioAreaTypesRings.doClick();
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {	
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}
}
