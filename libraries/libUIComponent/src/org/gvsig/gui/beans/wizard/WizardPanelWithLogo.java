package org.gvsig.gui.beans.wizard;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import jwizardcomponent.CancelAction;
import jwizardcomponent.DefaultJWizardComponents;
import jwizardcomponent.FinishAction;
import jwizardcomponent.common.SimpleButtonPanel;

import org.gvsig.gui.beans.wizard.panel.OptionPanel;
import org.gvsig.gui.beans.wizard.panel.OptionPanelContainer;

public class WizardPanelWithLogo extends JPanel {

	private static final long serialVersionUID = 7506729926181935234L;
	public final static int ACTION_PREVIOUS = 0;
	public final static int ACTION_NEXT = 1;
	public final static int ACTION_CANCEL = 2;
	public final static int ACTION_FINISH = 3;

	DefaultJWizardComponents wizardComponents;

	JPanel buttonPanel;
	JLabel statusLabel = new JLabel();

	ImageIcon logo;

	public WizardPanelWithLogo(ImageIcon logo) {
		this.logo = logo;
		wizardComponents = new DefaultJWizardComponents();
		init();
	}

	private void init() {
		JPanel logoPanel = new JPanel();

		logoPanel.add(new JLabel(logo));
		logoPanel.setBackground(Color.WHITE);
		this.setLayout(new BorderLayout());
		this.add(logoPanel, BorderLayout.WEST);
		this.add(wizardComponents.getWizardPanelsContainer(),
				BorderLayout.CENTER);

		JPanel auxPanel = new JPanel(new BorderLayout());
		auxPanel.add(new JSeparator(), BorderLayout.NORTH);

		buttonPanel = new SimpleButtonPanel(wizardComponents);
		auxPanel.add(buttonPanel);
		this.add(auxPanel, BorderLayout.SOUTH);

		wizardComponents.setFinishAction(new FinishAction(wizardComponents) {

			public void performAction() {
				// dispose();
			}
		});
		wizardComponents.setCancelAction(new CancelAction(wizardComponents) {

			public void performAction() {
				// dispose();
			}
		});
	}

	public void doAction(int action) {
		switch (action) {
		case ACTION_NEXT:
			getWizardComponents().getNextButton().getActionListeners()[0]
					.actionPerformed(null);
			break;
		case ACTION_PREVIOUS:
			getWizardComponents().getBackButton().getActionListeners()[0]
					.actionPerformed(null);
			break;
		}
	}

	public DefaultJWizardComponents getWizardComponents() {
		return wizardComponents;
	}

	public void setWizardComponents(DefaultJWizardComponents aWizardComponents) {
		wizardComponents = aWizardComponents;
	}

	@Override
	public void show() {
		wizardComponents.updateComponents();
		super.setVisible(true);
	}

	public void addOptionPanel(OptionPanel optionPanel) {
		getWizardComponents().addWizardPanel(
				new OptionPanelContainer(getWizardComponents(), optionPanel));
	}

	public void setNextButtonEnabled(boolean isEnabled) {
		getWizardComponents().getNextButton().setEnabled(isEnabled);
	}

	public void setFinishButtonEnabled(boolean isVisible) {
		getWizardComponents().getFinishButton().setEnabled(isVisible);
	}

	public void setCancelButtonEnabled(boolean isVisible) {
		getWizardComponents().getCancelButton().setEnabled(isVisible);
	}

	private void setFinishAction(FinishAction finishAction) {
		getWizardComponents().setFinishAction(finishAction);
	}

	private void setCancelAction(CancelAction cancelAction) {
		getWizardComponents().setCancelAction(cancelAction);
	}

	public void setWizardListener(WizardPanel wizardPanel) {
		setFinishAction(new WizardPanelFinishAction(getWizardComponents(),
				wizardPanel));
		setCancelAction(new WizardPanelCancelAction(getWizardComponents(),
				wizardPanel));
	}

	public void setBackButtonEnabled(boolean isEnabled) {
		getWizardComponents().getBackButton().setEnabled(isEnabled);
	}
}
