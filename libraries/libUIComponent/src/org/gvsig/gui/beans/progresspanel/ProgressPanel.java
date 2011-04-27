package org.gvsig.gui.beans.progresspanel;

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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.gvsig.gui.beans.Messages;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelEvent;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelListener;
import org.gvsig.gui.beans.defaultbuttonspanel.DefaultButtonsPanel;

/**
 * <code>ProgressPanel</code>. Muestra una ventana de diálogo para representar
 * una barra de progreso con su ventana de registro.
 *
 * @version 15/07/2008
 *
 * @author BorSanZa - Borja Sanchez Zamorano (borja.sanchez@iver.es)
 */
public class ProgressPanel extends JDialog {
	private static final long serialVersionUID = 4321011219898234352L;

	private JPanel              jPanel              = null;
	private JLabel              jLabel              = null;
	private JLabel              jLabel1             = null;
	private JPanel              jPanel1             = null;
	private JPanel              njp                 = null;
	private DefaultButtonsPanel defaultButtonsPanel = null;
	private JProgressBar        jProgressBar        = null;
	private JScrollPane         jScrollPane         = null;
	private JTextPane           jTextPane           = null;
	private LogControl          text                = new LogControl();
	private long                startTime           = System.currentTimeMillis();
	private boolean             showPause           = true;

	/**
	 * Constructor
	 */
	public ProgressPanel() {
		super();
		initialize();
	}

	public ProgressPanel(boolean showPause) {
		super();
		this.showPause = showPause;
		initialize();
	}
	
	/**
	 * @see JDialog#JDialog(Dialog, boolean)
	 */
	public ProgressPanel(Dialog owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		initialize();
	}

	/**
	 * @see JDialog#JDialog(Dialog, String, boolean, GraphicsConfiguration)
	 */
	public ProgressPanel(Dialog owner, String title, boolean modal,	GraphicsConfiguration gc) throws HeadlessException {
		super(owner, title, modal, gc);
		initialize();
	}

	/**
	 * @see JDialog#JDialog(Dialog, String, boolean)
	 */
	public ProgressPanel(Dialog owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		initialize();
	}

	/**
	 * @see JDialog#JDialog(Dialog, String)
	 */
	public ProgressPanel(Dialog owner, String title) throws HeadlessException {
		super(owner, title);
		initialize();
	}

	/**
	 * @see JDialog#JDialog(Dialog)
	 */
	public ProgressPanel(Dialog owner) throws HeadlessException {
		super(owner);
		initialize();
	}

	/**
	 * @see JDialog#JDialog(Frame, boolean)
	 */
	public ProgressPanel(Frame owner, boolean modal) throws HeadlessException {
		super(owner, modal);
		initialize();
	}

	/**
	 * @see JDialog#JDialog(Frame, String, boolean, GraphicsConfiguration)
	 */
	public ProgressPanel(Frame owner, String title, boolean modal, GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
		initialize();
	}

	/**
	 * @see JDialog#JDialog(Frame, String, boolean)
	 */
	public ProgressPanel(Frame owner, String title, boolean modal)
			throws HeadlessException {
		super(owner, title, modal);
		initialize();
	}

	/**
	 * @see JDialog#JDialog(Frame, String)
	 */
	public ProgressPanel(Frame owner, String title) throws HeadlessException {
		super(owner, title);
		initialize();
	}

	/**
	 * @see JDialog#JDialog(Frame)
	 */
	public ProgressPanel(Frame owner) throws HeadlessException {
		super(owner);
		initialize();
	}	
	
	/**
	 * This method initializes this
	 */
	private void initialize() {
		njp = new JPanel();
		njp.setLayout(new java.awt.BorderLayout(5, 5));
		njp.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 5, 15));
		this.setContentPane(njp);
		this.setResizable(false);
		njp.add(getJPanel(), java.awt.BorderLayout.NORTH);
		njp.add(getJPanel1(), java.awt.BorderLayout.CENTER);
		njp.add(getButtonsPanel(), java.awt.BorderLayout.SOUTH);
		showLog(false);
		setPercent(0);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(
				(int) (d.getWidth() - this.getWidth()) >> 1,
				(int) (d.getHeight() - this.getHeight()) >> 1);
		this.setVisible(true);
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jLabel1 = new JLabel();
			jLabel = new JLabel();
			jLabel.setText(Messages.getText("espere") + "...");
			jPanel = new JPanel();
			jPanel.setLayout(new java.awt.BorderLayout(5, 5));
			jPanel.add(jLabel, java.awt.BorderLayout.WEST);
			jPanel.add(jLabel1, java.awt.BorderLayout.EAST);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new java.awt.BorderLayout(5, 5));
			jPanel1.add(getJProgressBar(), java.awt.BorderLayout.NORTH);
			jPanel1.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		}
		return jPanel1;
	}

	public void addButtonPressedListener(ButtonsPanelListener listener) {
		getDefaultButtonsPanel().addButtonPressedListener(listener);
	}

	public void removeButtonPressedListener(ButtonsPanelListener listener) {
		getDefaultButtonsPanel().removeButtonPressedListener(listener);
	}

	public void showLog(boolean visible) {
		getButtonsPanel().getButton(ButtonsPanel.BUTTON_SEEDETAILS).setVisible(!visible);
		getButtonsPanel().getButton(ButtonsPanel.BUTTON_HIDEDETAILS).setVisible(visible);
		jScrollPane.setVisible(visible);

		this.setIgnoreRepaint(true);

		if (visible)
			this.setSize(this.getWidth(), 300);
		else
			this.pack();

		this.setIgnoreRepaint(false);
		if (this.isVisible())
			this.setVisible(true);
	}

	public void showPause(boolean visible) {
		if (showPause) {
			getButtonsPanel().getButton(ButtonsPanel.BUTTON_RESTART).setVisible(!visible);
			getButtonsPanel().getButton(ButtonsPanel.BUTTON_PAUSE).setVisible(visible);
		} else {
			getButtonsPanel().getButton(ButtonsPanel.BUTTON_RESTART).setVisible(false);
			getButtonsPanel().getButton(ButtonsPanel.BUTTON_PAUSE).setVisible(false);
		}
	}

	/**
	 * @see DefaultButtonsPanel#getButtonsPanel()
	 */
	public ButtonsPanel getButtonsPanel() {
		return getDefaultButtonsPanel().getButtonsPanel();
	}
	
	/**
	 * This method initializes DefaultButtonsPanel
	 *
	 * @return DefaultButtonsPanel
	 */
	public DefaultButtonsPanel getDefaultButtonsPanel() {
		if (defaultButtonsPanel == null) {
			defaultButtonsPanel = new DefaultButtonsPanel(ButtonsPanel.BUTTONS_NONE);
			getButtonsPanel().addSeeDetails();
			getButtonsPanel().addHideDetails();
			getButtonsPanel().addPause();
			getButtonsPanel().addRestart();
			showPause(true);
			getButtonsPanel().addCancel();
			getButtonsPanel().setLayout(new java.awt.FlowLayout(FlowLayout.CENTER));
			getButtonsPanel().addButtonPressedListener(new ButtonsPanelListener() {
				public void actionButtonPressed(ButtonsPanelEvent e) {
					switch (e.getButton()) {
						case ButtonsPanel.BUTTON_SEEDETAILS:
							showLog(true);
							break;
						case ButtonsPanel.BUTTON_HIDEDETAILS:
							showLog(false);
							break;
						case ButtonsPanel.BUTTON_PAUSE:
							showPause(false);
							break;
						case ButtonsPanel.BUTTON_RESTART:
							showPause(true);
							break;
					}
				}
			});
		}
		return defaultButtonsPanel;
	}

	/**
	 * This method initializes jProgressBar
	 *
	 * @return javax.swing.JProgressBar
	 */
	private JProgressBar getJProgressBar() {
		if (jProgressBar == null) {
			jProgressBar = new JProgressBar();
			jProgressBar.setValue(50);
			jProgressBar.setPreferredSize(new Dimension(450, 18));
		}
		return jProgressBar;
	}

	/**
	 * This method initializes jScrollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTextPane());
			jScrollPane.setVisible(false);
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextPane
	 *
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.setEditable(false);
		}
		return jTextPane;
	}

	private void updateLog() {
		jTextPane.setText(text.getText());
		jTextPane.setCaretPosition(jTextPane.getText().length());
	}

	public void addLineLog(String line) {
		text.addLine(line);
		updateLog();
	}

	public void replaceLastLineLog(String line) {
		text.replaceLastLine(line);
		updateLog();
	}

	public void setLog(String value) {
		long time = (System.currentTimeMillis() - startTime) / 1000;
		text.setText(value + "\n" + Messages.getText("tiempo_transcurrido") + ": " + time + "s");
		updateLog();
	}

	public int getPercent() {
		return jProgressBar.getValue();
	}

	public void setPercent(int value) {
		if ((value == 0) && (!jProgressBar.isIndeterminate())) {
			jProgressBar.setIndeterminate(true);
			jLabel1.setVisible(false);
		}
		if ((value != 0) && (jProgressBar.isIndeterminate())) {
			jProgressBar.setIndeterminate(false);
			jLabel1.setVisible(true);
		}
		jProgressBar.setValue(value);
		jLabel1.setText(value + "%");
	}

	public void setLabel(String value) {
		jLabel.setText(value);
	}

	/**
	 * <p>Sets whether this panel will show a normal progress bar
	 * or an indeterminate one.</p>
	 *
	 * @see JProgressBar.setIndeterminate(boolean)
	 * @param indeterminate
	 */
	public void setIndeterminate(boolean indeterminate) {
		getJProgressBar().setIndeterminate(indeterminate);
	}

	/**
	 * <p>Gets whether this panel will show a normal progress bar
	 * or an indeterminate one.</p>
	 *
	 * @see JProgressBar.setIndeterminate(boolean)
	 * @param indeterminate
	 */
	public boolean getIndeterminate() {
		return getJProgressBar().isIndeterminate();
	}
}