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

public class ServiceAreaPage2 extends JWizardPanel implements ActionListener,
		MouseListener {

	private JLabel labelChooseArea;
	private JPanel panelAreaTypes;
	private ButtonGroup groupAreaTypes;
	private JPanel panelAreaTypesCompact;
	private TitledBorder titledBorderAreaTypesCompact;
	private JRadioButton radioAreaTypesCompact;
	private JPanel panelAreaTypesCompactPicture;
	private JLabel labelAreaTypesCompactPicture;
	private JPanel panelAreaTypesConvex;
	private TitledBorder titledBorderAreaTypesConvex;
	private JRadioButton radioAreaTypesConvex;
	private JPanel panelAreaTypesConvexPicture;
	private JLabel labelAreaTypesConvexPicture;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5139067753651358490L;

	private ServiceAreaWizard owner;

	public ServiceAreaPage2(ServiceAreaWizard wizard) {
		super(wizard.getWizardComponents());
		this.owner = wizard;
		this.initialize();
	}

	private void initialize() {
		this.labelChooseArea = new JLabel(
				"<html><b>Escoge el tipo de área:</b></html>", JLabel.LEFT);
		this.panelAreaTypes = new JPanel();
		this.groupAreaTypes = new ButtonGroup();
		this.panelAreaTypesCompact = new JPanel();
		this.titledBorderAreaTypesCompact = new TitledBorder("Área compacta");
		this.panelAreaTypesCompact.setBorder(this.titledBorderAreaTypesCompact);
		this.radioAreaTypesCompact = new JRadioButton(
				"<html>Área compacta</html>");
		this.radioAreaTypesCompact.addActionListener(this);
		this.radioAreaTypesCompact.setSelected(true);
		this.groupAreaTypes.add(this.radioAreaTypesCompact);
		this.panelAreaTypesCompactPicture = new JPanel();
		this.labelAreaTypesCompactPicture = new JLabel();
		this.labelAreaTypesCompactPicture.setOpaque(true);
		this.labelAreaTypesCompactPicture.setIcon(PluginServices.getIconTheme()
				.get("service_area_compact"));
		this.labelAreaTypesCompactPicture.setEnabled(true);
		this.labelAreaTypesCompactPicture.addMouseListener(this);
		this.panelAreaTypesConvex = new JPanel();
		this.titledBorderAreaTypesConvex = new TitledBorder("Área convexa");
		this.panelAreaTypesConvex.setBorder(this.titledBorderAreaTypesConvex);
		this.radioAreaTypesConvex = new JRadioButton("Área convexa");
		this.radioAreaTypesConvex.addActionListener(this);
		this.groupAreaTypes.add(this.radioAreaTypesConvex);
		this.panelAreaTypesConvexPicture = new JPanel();
		this.labelAreaTypesConvexPicture = new JLabel();
		this.labelAreaTypesConvexPicture.setOpaque(true);
		this.labelAreaTypesConvexPicture.setBackground(Color.BLUE);
		this.labelAreaTypesConvexPicture.setIcon(PluginServices.getIconTheme()
				.get("service_area_convex"));
		this.labelAreaTypesConvexPicture.setEnabled(false);
		this.labelAreaTypesConvexPicture.addMouseListener(this);

		this.labelAreaTypesCompactPicture.setSize(
				this.labelAreaTypesCompactPicture.getIcon().getIconWidth(),
				this.labelAreaTypesCompactPicture.getIcon().getIconHeight());
		this.panelAreaTypesCompactPicture
				.add(this.labelAreaTypesCompactPicture);
		this.panelAreaTypesCompactPicture
				.setSize(this.labelAreaTypesCompactPicture.getSize());
		this.panelAreaTypesCompact.setLayout(new GridLayout(2, 1, 7, 7));
		this.panelAreaTypesCompact.add(this.radioAreaTypesCompact);
		this.panelAreaTypesCompact.add(this.panelAreaTypesCompactPicture);

		this.labelAreaTypesConvexPicture.setSize(
				this.labelAreaTypesConvexPicture.getIcon().getIconWidth(),
				this.labelAreaTypesConvexPicture.getIcon().getIconHeight());
		this.panelAreaTypesConvexPicture.add(this.labelAreaTypesConvexPicture);
		this.panelAreaTypesConvexPicture
				.setSize(this.labelAreaTypesConvexPicture.getSize());
		this.panelAreaTypesConvex.setLayout(new GridLayout(2, 1, 7, 7));
		this.panelAreaTypesConvex.add(this.radioAreaTypesConvex);
		this.panelAreaTypesConvex.add(this.panelAreaTypesConvexPicture);

		this.panelAreaTypes.setLayout(new GridLayout(2, 1, 7, 7));
		this.panelAreaTypes.add(this.panelAreaTypesCompact);
		this.panelAreaTypes.add(this.panelAreaTypesConvex);

		this.setLayout(new BorderLayout(7, 7));
		this.add(this.labelChooseArea, BorderLayout.NORTH);
		this.add(this.panelAreaTypes, BorderLayout.CENTER);

		this.radioAreaTypesCompact.doClick();
	}

	public void next() {
		super.next();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.radioAreaTypesCompact) {
			this.labelAreaTypesCompactPicture.setEnabled(true);
			this.labelAreaTypesConvexPicture.setEnabled(false);
			this.owner.getController().setCompactAreas(true);
		} else if (e.getSource() == this.radioAreaTypesConvex) {
			this.labelAreaTypesCompactPicture.setEnabled(false);
			this.labelAreaTypesConvexPicture.setEnabled(true);
			this.owner.getController().setCompactAreas(false);
		}
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == this.labelAreaTypesCompactPicture) {
			this.radioAreaTypesCompact.setSelected(true);
			this.radioAreaTypesCompact.doClick();
		} else if (e.getSource() == this.labelAreaTypesConvexPicture) {
			this.radioAreaTypesConvex.setSelected(true);
			this.radioAreaTypesConvex.doClick();
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