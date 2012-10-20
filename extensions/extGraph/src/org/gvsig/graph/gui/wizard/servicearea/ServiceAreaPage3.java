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

public class ServiceAreaPage3 extends JWizardPanel implements ActionListener,
		MouseListener {
	private JLabel labelChooseArea;
	private JPanel panelAreaTypes;
	private ButtonGroup groupAreaTypes;
	private JPanel panelAreaTypesFusioned;
	private TitledBorder titledBorderAreaTypesFusioned;
	private JRadioButton radioAreaTypesFusioned;
	private JPanel panelAreaTypesFusionedPicture;
	private JLabel labelAreaTypesFusionedPicture;
	private JPanel panelAreaTypesNonFusioned;
	private TitledBorder titledBorderAreaTypesNonFusioned;
	private JRadioButton radioAreaTypesNonFusioned;
	private JPanel panelAreaTypesNonFusionedPicture;
	private JLabel labelAreaTypesNonFusionedPicture;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5139067753651358490L;

	private ServiceAreaWizard owner;

	public ServiceAreaPage3(ServiceAreaWizard wizard) {
		super(wizard.getWizardComponents());
		this.owner = wizard;
		this.initialize();
	}

	private void initialize() {
		this.labelChooseArea = new JLabel(
				"<html><b>Fusion de áreas:</b></html>", JLabel.LEFT);
		this.panelAreaTypes = new JPanel();
		this.groupAreaTypes = new ButtonGroup();
		this.panelAreaTypesFusioned = new JPanel();
		this.titledBorderAreaTypesFusioned = new TitledBorder("Fusionar áreas");
		this.panelAreaTypesFusioned
				.setBorder(this.titledBorderAreaTypesFusioned);
		this.radioAreaTypesFusioned = new JRadioButton(
				"<html>Las áreas de servicio que se solapen se <b>fusionarán</b> en una sola teniendo en cuenta los costes</html>");
		this.radioAreaTypesFusioned.addActionListener(this);
		this.radioAreaTypesFusioned.setSelected(true);
		this.groupAreaTypes.add(this.radioAreaTypesFusioned);
		this.panelAreaTypesFusionedPicture = new JPanel();
		this.labelAreaTypesFusionedPicture = new JLabel();
		this.labelAreaTypesFusionedPicture.setOpaque(true);
		this.labelAreaTypesFusionedPicture.setIcon(PluginServices
				.getIconTheme().get("service_area_fusion"));
		this.labelAreaTypesFusionedPicture.setEnabled(true);
		this.labelAreaTypesFusionedPicture.addMouseListener(this);
		this.panelAreaTypesNonFusioned = new JPanel();
		this.titledBorderAreaTypesNonFusioned = new TitledBorder(
				"No fusionar áreas");
		this.panelAreaTypesNonFusioned
				.setBorder(this.titledBorderAreaTypesNonFusioned);
		this.radioAreaTypesNonFusioned = new JRadioButton(
				"<html>Las áreas de servicio que ocupen un mismo lugar se solaparán</html>");
		this.radioAreaTypesNonFusioned.addActionListener(this);
		this.groupAreaTypes.add(this.radioAreaTypesNonFusioned);
		this.panelAreaTypesNonFusionedPicture = new JPanel();
		this.labelAreaTypesNonFusionedPicture = new JLabel();
		this.labelAreaTypesNonFusionedPicture.setOpaque(true);
		this.labelAreaTypesNonFusionedPicture.setBackground(Color.BLUE);
		this.labelAreaTypesNonFusionedPicture.setIcon(PluginServices
				.getIconTheme().get("service_area_non_fusion"));
		this.labelAreaTypesNonFusionedPicture.setEnabled(false);
		this.labelAreaTypesNonFusionedPicture.addMouseListener(this);

		this.labelAreaTypesFusionedPicture.setSize(
				this.labelAreaTypesFusionedPicture.getIcon().getIconWidth(),
				this.labelAreaTypesFusionedPicture.getIcon().getIconHeight());
		this.panelAreaTypesFusionedPicture
				.add(this.labelAreaTypesFusionedPicture);
		this.panelAreaTypesFusionedPicture
				.setSize(this.labelAreaTypesFusionedPicture.getSize());
		this.panelAreaTypesFusioned.setLayout(new GridLayout(2, 1, 7, 7));
		this.panelAreaTypesFusioned.add(this.radioAreaTypesFusioned);
		this.panelAreaTypesFusioned.add(this.panelAreaTypesFusionedPicture);

		this.labelAreaTypesNonFusionedPicture
				.setSize(this.labelAreaTypesNonFusionedPicture.getIcon()
						.getIconWidth(), this.labelAreaTypesNonFusionedPicture
						.getIcon().getIconHeight());
		this.panelAreaTypesNonFusionedPicture
				.add(this.labelAreaTypesNonFusionedPicture);
		this.panelAreaTypesNonFusionedPicture
				.setSize(this.labelAreaTypesNonFusionedPicture.getSize());
		this.panelAreaTypesNonFusioned.setLayout(new GridLayout(2, 1, 7, 7));
		this.panelAreaTypesNonFusioned.add(this.radioAreaTypesNonFusioned);
		this.panelAreaTypesNonFusioned
				.add(this.panelAreaTypesNonFusionedPicture);

		this.panelAreaTypes.setLayout(new GridLayout(2, 1, 7, 7));
		this.panelAreaTypes.add(this.panelAreaTypesFusioned);
		this.panelAreaTypes.add(this.panelAreaTypesNonFusioned);

		this.setLayout(new BorderLayout(7, 7));
		this.add(this.labelChooseArea, BorderLayout.NORTH);
		this.add(this.panelAreaTypes, BorderLayout.CENTER);

		this.radioAreaTypesFusioned.doClick();
	}

	public void next() {
		super.next();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.radioAreaTypesFusioned) {
			this.labelAreaTypesFusionedPicture.setEnabled(true);
			this.labelAreaTypesNonFusionedPicture.setEnabled(false);
			this.owner.getController().setFusionAreas(true);
		} else if (e.getSource() == this.radioAreaTypesNonFusioned) {
			this.labelAreaTypesFusionedPicture.setEnabled(false);
			this.labelAreaTypesNonFusionedPicture.setEnabled(true);
			this.owner.getController().setFusionAreas(false);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == this.labelAreaTypesFusionedPicture) {
			this.radioAreaTypesFusioned.setSelected(true);
			this.radioAreaTypesFusioned.doClick();
		} else if (e.getSource() == this.labelAreaTypesNonFusionedPicture) {
			this.radioAreaTypesNonFusioned.setSelected(true);
			this.radioAreaTypesNonFusioned.doClick();
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
