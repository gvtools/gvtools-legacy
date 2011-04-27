package org.gvsig.graph.gui.wizard.servicearea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;

import org.gvsig.graph.core.NetworkUtils;
import org.gvsig.graph.gui.wizard.servicearea.AbstractPointsModel.InvalidCostException;

import jwizardcomponent.JWizardPanel;

import com.iver.andami.PluginServices;

public class ServiceAreaPage1 extends JWizardPanel implements KeyListener, MouseListener, ComponentListener, ActionListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7051194740312102283L;

	private ServiceAreaWizard owner;
	
	private int selectedRowIndex;
		
	private JPanel contentpane;
	private JPanel panelNorth;
	private JLabel labelTitle;
	private JLabel labelAreaMainCost;
	private JLabel labelErrorMainCosts;
	private ImageIcon iconErrorCost;
	private JScrollPane scrollAreaMainCosts;
	private JTextArea textAreaMainCosts;
	private JLabel labelAreaSecondaryCosts;
	private JLabel labelErrorSecondaryCosts;
	private JScrollPane scrollAreaSecondaryCosts;
	private JTextArea textAreaSecondaryCosts;
	private JCheckBox checkSameCosts;
	private TitledBorder borderPanelPoints;
	private JPanel panelTable;
	private JScrollPane scrollPoints;
	//private JTable tablePoints;
	private ColoredTable tablePoints;
	
	private static final int BORDER_HGAP = 7;
	private static final int BORDER_VGAP = 7;
	private static final int COMPONENT_HGAP = 4;
	private static final int COMPONENT_VGAP = 4;
	private static final int PREFERRED_HEIGHT = 23;
	private static final int PREFERRED_WIDTH  = 120;

	public ServiceAreaPage1(ServiceAreaWizard wizard){
		super(wizard.getWizardComponents());
		this.owner=wizard;
		this.selectedRowIndex=-1;
		this.initialize();
	}
	
	private void initialize(){
		this.addComponentListener(this);
		
		this.contentpane=new JPanel();
		this.panelNorth=new JPanel();
		this.labelTitle=new JLabel("<html><b>Edición de costes de cada área</b></html>");
		this.labelAreaMainCost=new JLabel("<html>Costes <b>primarios</b> de área:</html>");
		this.labelErrorMainCosts=new JLabel();
		this.labelErrorMainCosts.setOpaque(true);
		this.iconErrorCost=PluginServices.getIconTheme().get("service_area_wrong_costs");
		this.scrollAreaMainCosts=new JScrollPane();
		this.scrollAreaMainCosts.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollAreaMainCosts.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.textAreaMainCosts=new JTextArea();
		this.textAreaMainCosts.setRows(4);
		this.textAreaMainCosts.setLineWrap(true);
		this.textAreaMainCosts.addKeyListener(this);
		this.scrollAreaMainCosts.getViewport().add(this.textAreaMainCosts);
		this.labelAreaSecondaryCosts=new JLabel("<html>Costes <b>secundarios</b> de área:</html>");
		this.labelErrorSecondaryCosts=new JLabel();
		this.labelErrorSecondaryCosts.setOpaque(true);
		this.scrollAreaSecondaryCosts=new JScrollPane();
		this.scrollAreaSecondaryCosts.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollAreaSecondaryCosts.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.textAreaSecondaryCosts=new JTextArea();
		this.textAreaSecondaryCosts.setRows(4);
		this.textAreaSecondaryCosts.setLineWrap(true);
		this.textAreaSecondaryCosts.addKeyListener(this);
		this.scrollAreaSecondaryCosts.getViewport().add(this.textAreaSecondaryCosts);
		this.checkSameCosts=new JCheckBox("<html><p align='left'>Utilizar los costes del</p><p align='left'>punto seleccionado</p><p align='left'>para el resto</p><p align='left'>de puntos</p></html>");
		this.checkSameCosts.addActionListener(this);
		this.panelTable=new JPanel();
		this.borderPanelPoints=new TitledBorder("Gestor de paradas");
		this.panelTable.setBorder(this.borderPanelPoints);
		this.scrollPoints=new JScrollPane();
		//this.tablePoints=new JTable();
		this.tablePoints=new ColoredTable();
		this.tablePoints.enablePijamaEffect(Color.white, new Color(229, 251, 252));
		//this.tablePoints.setModel(this.owner.getModel());
		this.tablePoints.addMouseListener(this);
		this.scrollPoints.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.scrollPoints.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPoints.getViewport().add(this.tablePoints);
		
		this.panelNorth.setLayout(null);
		this.labelTitle.setBounds(BORDER_HGAP, BORDER_VGAP, this.labelTitle.getFontMetrics(this.labelTitle.getFont()).stringWidth(this.labelTitle.getText()), PREFERRED_HEIGHT);
		this.labelAreaMainCost.setBounds(BORDER_HGAP, this.labelTitle.getY()+this.labelTitle.getHeight()+COMPONENT_VGAP, this.labelAreaMainCost.getFontMetrics(this.labelAreaMainCost.getFont()).stringWidth(this.labelAreaMainCost.getText()), 23);
		this.labelErrorMainCosts.setBounds(this.labelAreaMainCost.getWidth()+this.labelAreaMainCost.getX()+COMPONENT_HGAP, this.labelAreaMainCost.getY(), 23, 23);
		this.scrollAreaMainCosts.setBounds(BORDER_HGAP, this.labelAreaMainCost.getY()+this.labelAreaMainCost.getHeight()+COMPONENT_VGAP, 200, 70);
		this.checkSameCosts.setBounds(this.scrollAreaMainCosts.getX()+this.scrollAreaMainCosts.getWidth()+BORDER_HGAP, this.scrollAreaMainCosts.getY(), this.checkSameCosts.getFontMetrics(this.checkSameCosts.getFont()).stringWidth(this.checkSameCosts.getText()), this.scrollAreaMainCosts.getHeight());
		this.labelAreaSecondaryCosts.setBounds(BORDER_HGAP, this.scrollAreaMainCosts.getY()+this.scrollAreaMainCosts.getHeight()+COMPONENT_VGAP, this.labelAreaSecondaryCosts.getFontMetrics(this.labelAreaSecondaryCosts.getFont()).stringWidth(this.labelAreaSecondaryCosts.getText()), PREFERRED_HEIGHT);
		this.labelErrorSecondaryCosts.setBounds(this.labelAreaSecondaryCosts.getWidth()+this.labelAreaSecondaryCosts.getX()+COMPONENT_HGAP, this.labelAreaSecondaryCosts.getY(), 23, 23);
		this.scrollAreaSecondaryCosts.setBounds(BORDER_HGAP, this.labelAreaSecondaryCosts.getY()+this.labelAreaSecondaryCosts.getHeight()+BORDER_VGAP, 200, 70);
				
		this.panelNorth.setPreferredSize(new Dimension(0, 
				this.labelTitle.getHeight() + 
				this.labelAreaMainCost.getHeight() + 
				this.scrollAreaMainCosts.getHeight() +
				this.labelAreaSecondaryCosts.getHeight() +
				this.scrollAreaSecondaryCosts.getHeight() + 40));
		
		this.panelNorth.add(this.labelTitle);
		this.panelNorth.add(this.labelAreaMainCost);
		this.panelNorth.add(this.labelErrorMainCosts);
		this.panelNorth.add(this.scrollAreaMainCosts);
		this.panelNorth.add(this.checkSameCosts);
		this.panelNorth.add(this.labelAreaSecondaryCosts);
		this.panelNorth.add(this.labelErrorSecondaryCosts);
		this.panelNorth.add(this.scrollAreaSecondaryCosts);
		
		this.panelTable.setLayout(new BorderLayout(7, 7));
		this.panelTable.add(this.scrollPoints, BorderLayout.CENTER);
		
		this.contentpane.setLayout(new BorderLayout(7, 7));
		this.contentpane.add(this.panelNorth, BorderLayout.NORTH);
		this.contentpane.add(this.panelTable, BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout(7, 7));
		this.add(this.contentpane, BorderLayout.CENTER);
	}
	
	public void next(){
		AbstractPointsModel model=this.owner.getController().getModel();
		
		if(model.getEnabledRowsCount()>0){
			if(!model.isUniqueMainCostEnabled() || !model.isUniqueSecondaryCostEnabled()){
				boolean flag=true;
				int rowCount=model.getRowCount();
				int i=0;

				for (i = 0; i < rowCount && flag; i++) {
					if(model.isRowEnabled(i)){
						if(String.valueOf(model.getValueAt(i, 3)).trim().equals("") || String.valueOf(model.getValueAt(i, 4)).trim().equals("")){
							flag=false;
						}
					}
				}

				if(flag){
					super.next();
				}
				else{
					JOptionPane.showMessageDialog(this, "La fila con ID "+Integer.valueOf(String.valueOf(model.getValueAt(i, 1)))+" no tiene todos los costes asignados", "Costes no asignados", JOptionPane.WARNING_MESSAGE);
				}
			}
			else{
				super.next();
			}
		}
		else{
			JOptionPane.showMessageDialog(this, "No hay ninguna fila habilitada", "Advertencia", JOptionPane.WARNING_MESSAGE);
		}
	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
		if(e.getSource()==this.textAreaMainCosts){
			try{
				AbstractPointsModel model=this.owner.getController().getModel();
				double[] costs=NetworkUtils.string2doubleArray(this.textAreaMainCosts.getText()+e.getKeyChar(), model.COSTS_SEPARATOR);
				this.labelErrorMainCosts.setIcon(null);
				this.labelErrorMainCosts.setToolTipText("");
				this.textAreaMainCosts.setBackground(Color.white);
				if(this.selectedRowIndex>=0 && this.selectedRowIndex<this.owner.getController().getModel().getRowCount()){
					String s="";
					for (int i = 0; i < costs.length; i++) {
						s+=costs[i]+model.COSTS_SEPARATOR;
					}
					s=s.trim();
					if(s.endsWith(model.COSTS_SEPARATOR)) s=s.substring(0, s.length()-1);
					this.owner.getController().getModel().setValueAt(s, this.selectedRowIndex, 3);
					this.tablePoints.updateUI();
				}
			}
			catch(NumberFormatException except){
				this.labelErrorMainCosts.setIcon(this.iconErrorCost);
				this.labelErrorMainCosts.setToolTipText("Hay un error en los costes primarios");
				this.textAreaMainCosts.setBackground(new Color(255, 210, 210));
			}
		}
		else if(e.getSource()==this.textAreaSecondaryCosts){
			try{
				AbstractPointsModel model=this.owner.getController().getModel();
				double[] costs=NetworkUtils.string2doubleArray(this.textAreaSecondaryCosts.getText()+e.getKeyChar(), model.COSTS_SEPARATOR);
				this.labelErrorSecondaryCosts.setIcon(null);
				this.labelErrorSecondaryCosts.setToolTipText("");
				this.textAreaSecondaryCosts.setBackground(Color.white);
				if(this.selectedRowIndex>=0 && this.selectedRowIndex<this.owner.getController().getModel().getRowCount()){
					String s="";
					for (int i = 0; i < costs.length; i++) {
						s+=costs[i]+model.COSTS_SEPARATOR;
					}
					s=s.trim();
					if(s.endsWith(model.COSTS_SEPARATOR)) s=s.substring(0, s.length()-1);
					this.owner.getController().getModel().setValueAt(s, this.selectedRowIndex, 4);
					this.tablePoints.updateUI();
				}
			}
			catch(NumberFormatException except){
				this.labelErrorSecondaryCosts.setIcon(this.iconErrorCost);
				this.labelErrorSecondaryCosts.setToolTipText("Hay un error en los costes secundarios");
				this.textAreaSecondaryCosts.setBackground(new Color(255, 210, 210));
			}
		}
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getSource()==this.tablePoints){
			this.selectedRowIndex=this.tablePoints.getSelectedRow();
			if(this.selectedRowIndex>=0 && this.selectedRowIndex<this.owner.getController().getModel().getRowCount()){
				String s=String.valueOf(this.owner.getController().getModel().getValueAt(this.selectedRowIndex, 3));
				this.textAreaMainCosts.setText(s.trim());
				s=String.valueOf(this.owner.getController().getModel().getValueAt(this.selectedRowIndex, 4));
				this.textAreaSecondaryCosts.setText(s.trim());
			}
			
			this.textAreaMainCosts.setBackground(Color.white);
			this.textAreaSecondaryCosts.setBackground(Color.white);
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

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
		this.tablePoints.setModel(this.owner.getController().getModel());
		//this.textAreaMainCosts.setText("");
		//this.textAreaSecondaryCosts.setText("");
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.checkSameCosts){
			AbstractPointsModel model=this.owner.getController().getModel();

			if(this.checkSameCosts.isSelected()){
				if(this.tablePoints.getSelectedRowCount()>0){
					String mainCosts=String.valueOf(model.getValueAt(this.tablePoints.getSelectedRow(), 3)),
					secondaryCosts=String.valueOf(model.getValueAt(this.tablePoints.getSelectedRow(),4));
					if(!mainCosts.trim().equals("") && !secondaryCosts.trim().equals("")){
						try {
							model.enableUniqueMainCost(String.valueOf(model.getValueAt(this.tablePoints.getSelectedRow(), 3)));
							model.enableUniqueSecondaryCost(String.valueOf(model.getValueAt(this.tablePoints.getSelectedRow(), 4)));
							this.tablePoints.setRowBackground(this.tablePoints.getSelectedRow(), new Color(237, 240, 184));
							this.tablePoints.updateUI();
						} catch (InvalidCostException except) {
							JOptionPane.showMessageDialog(this, except.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
					else if(mainCosts.trim().equals("")){
						JOptionPane.showMessageDialog(this, "El coste primario de la fila seleccionada no es correcto", "Coste incorrecto", JOptionPane.ERROR_MESSAGE);
						this.checkSameCosts.setSelected(false);
					}
					else if(secondaryCosts.trim().equals("")){
						JOptionPane.showMessageDialog(this, "El coste secundario de la fila seleccionada no es correcto", "Coste incorrecto", JOptionPane.ERROR_MESSAGE);
						this.checkSameCosts.setSelected(false);
					}
				}
				else{
					JOptionPane.showMessageDialog(this, "No se puede asginar un coste único porque no hay ninguna fila seleccionada");
					this.checkSameCosts.setSelected(false);
				}
			}
			else{
				model.disableUniqueMainCost();
				model.disableUniqueSecondaryCost();
				this.tablePoints.clearColoredRow();
				this.tablePoints.updateUI();
			}
		}
	}
	
	class ColoredTable extends JTable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -8283898126306207443L;
		private Color rowColor;
		private int coloredRowIndex = -1;
		
		private boolean pijamaEffectEnabled=false;
		private Color pijamaColor1;
		private Color pijamaColor2;
		
		public void setRowBackground(int rowIndex, Color color) throws IndexOutOfBoundsException{
			if(this.getModel().getRowCount()>rowIndex){
				this.rowColor=color;
				this.coloredRowIndex=rowIndex;
			}
			else{
				throw new IndexOutOfBoundsException("La fila "+rowIndex+" no existe");
			}
		}
		
		public void clearColoredRow(){
			this.coloredRowIndex=-1;
		}
		
		public void enablePijamaEffect(Color color1, Color color2){
			this.pijamaColor1=color1;
			this.pijamaColor2=color2;
			this.pijamaEffectEnabled=true;
		}
		
		public void disablePijamaEffect(){
			this.pijamaEffectEnabled=false;
		}
		
		public boolean isPijamaEffectEnabled(){
			return this.pijamaEffectEnabled;
		}

		public Component prepareRenderer(TableCellRenderer renderer, int row, int column){
			Component returnComp = super.prepareRenderer(renderer, row, column);
			
			if (!returnComp.getBackground().equals(getSelectionBackground()) && this.coloredRowIndex==row){
				returnComp .setBackground(rowColor);
			}
			
			if(this.pijamaEffectEnabled && !returnComp.getBackground().equals(getSelectionBackground()) && row!=coloredRowIndex){
				if(row%2==0){
					returnComp.setBackground(this.pijamaColor1);
				}
				else{
					returnComp.setBackground(this.pijamaColor2);
				}
			}

			return returnComp;
		}
	}
}
