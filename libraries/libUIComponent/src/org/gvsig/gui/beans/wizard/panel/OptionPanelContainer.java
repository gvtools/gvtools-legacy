/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 * 2010 {Prodevelop}   {Task}
 */

package org.gvsig.gui.beans.wizard.panel;

import java.awt.BorderLayout;

import javax.swing.JOptionPane;

import jwizardcomponent.JWizardComponents;
import jwizardcomponent.JWizardPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * This class is a wizard panel that displays the panel returned by the
 * {@link OptionPanel#getJPanel()} method int the top of the wizard. It also
 * call the other methods when the user clicks a button.
 * <p>
 * 
 * @author <a href="mailto:jpiera@gvsig.org">Jorge Piera Llodr&aacute;</a>
 */
public class OptionPanelContainer extends JWizardPanel {

	private static final long serialVersionUID = 3947658150325230122L;
	private OptionPanel optionPanel = null;
	private static final Logger log = LoggerFactory
			.getLogger(OptionPanelContainer.class);

	public OptionPanelContainer(JWizardComponents wizardComponents,
			OptionPanel optionPanel) {
		super(wizardComponents);
		this.optionPanel = optionPanel;
		setLayout(new BorderLayout());
		setBorder(javax.swing.BorderFactory.createCompoundBorder(
				javax.swing.BorderFactory.createTitledBorder(optionPanel
						.getPanelTitle()), javax.swing.BorderFactory
						.createEmptyBorder(5, 5, 5, 5)));
		add(optionPanel.getJPanel(), BorderLayout.CENTER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jwizardcomponent.JWizardPanel#back()
	 */
	@Override
	public void back() {
		optionPanel.lastPanel();
		super.back();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jwizardcomponent.JWizardPanel#next()
	 */
	@Override
	public void next() {
		try {
			optionPanel.nextPanel();
			super.next();
		} catch (NotContinueWizardException e) {
			// this is not an error and not need to raise a error or
			// warning in the log.
			log.info("It is not possible to continue with the wizard", e);
			if (e.displayMessage()) {
				JOptionPane.showMessageDialog(e.getComponent(),
						e.getLocalizedMessageStack());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jwizardcomponent.JWizardPanel#update()
	 */
	@Override
	public void update() {
		optionPanel.updatePanel();
		super.update();
	}
}
