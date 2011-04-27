package org.gvsig.gui.beans.incrementabletask;

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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.gvsig.gui.beans.Messages;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelEvent;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanelListener;
import org.gvsig.gui.beans.progresspanel.ProgressPanel;

/**
 * <code>IncrementableTask</code>. Es un dialogo que contiene un ProgressPanel.
 * Se ejecuta bajo un Thread y va consultando a un objeto de tipo IIncrementable
 * para modificar sus valores.
 *
 * @version 20/08/2008
 *
 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
 */
public class IncrementableTask implements Runnable, ButtonsPanelListener {
	IIncrementable                            iIncrementable         = null;
	private volatile ProgressPanel            progressPanel          = null;
	private volatile Thread                   blinker                = null;
	private boolean                           threadSuspended        = false;
	private boolean                           ended                  = false;
	private boolean                           askOnCancel            = true;
	private ArrayList<IncrementableListener>  actionCommandListeners = new ArrayList<IncrementableListener>();
	private boolean                           bDoCallListeners       = true;
	static private int                        eventId                = Integer.MIN_VALUE;
	
	/**
	 * Constructor del IncrementableTask.
	 * @param incrementable
	 */
	public IncrementableTask(IIncrementable incrementable, ProgressPanel dialog) {
		iIncrementable = incrementable;
		progressPanel = dialog;
		configureProgressPanel();
	}
	
	/**
	 * Constructor del IncrementableTask.
	 * @param incrementable
	 */
	public IncrementableTask(IIncrementable incrementable) {
		iIncrementable = incrementable;
		configureProgressPanel();
	}

	/**
	 * Inicio del thread para que la ventana vaya consultando por si sola al
	 * iIncrementable
	 */
	public void start() {
		blinker = new Thread(this);
		blinker.start();
	}

	/**
	 * Detiene el proceso de consulta de la ventana.
	 */
	public void stop() {
		ended = true;
	}
	
	/**
	 * Este thread va leyendo el porcentaje hasta que se completa el histograma.
	 */
	public synchronized void run() {
//		while (!ended && (iIncrementable.getPercent() <= 100)) {
		while (! ended) {
			try {
				getProgressPanel().setLabel(iIncrementable.getLabel());
				getProgressPanel().setPercent(iIncrementable.getPercent());
				getProgressPanel().setTitle(iIncrementable.getTitle());
				getProgressPanel().setLog(iIncrementable.getLog());
				Thread.sleep(100);
				synchronized (this) {
					while (threadSuspended && !ended)
						wait(500);
				}
			} catch (InterruptedException e) {
			}
		}
		
		// Forces to refresh the log with the last changes
		getProgressPanel().setLog(iIncrementable.getLog());
	}

	/**
	 * Termina el proceso de lectura de porcentajes y logs de la ventana y
	 * cierra esta.
	 */
	public void processFinalize() {
		stop();
		while (isAlive());
		hide();
	}

	/**
	 * Ocultar la ventana y parar el proceso
	 */
	private void hide() {
		hideWindow();
		progressPanel = null;
		blinker = null;
	}
	
	/**
	 * Ocultar la ventana
	 */
	public void hideWindow() {
//		getProgressPanel().dispose();
		getProgressPanel().setVisible(false);
	}

	/**
	 * Devuelve un booleano indicando si esta activa la ventana.
	 * @return boolean
	 */
	public boolean isAlive() {
		if (blinker == null)
			return false;
		return blinker.isAlive();
	}

	/**
	 * Muestra la ventana de incremento con el porcentaje de la construcción del
	 * histograma.
	 */
	public void showWindow() {
		getProgressPanel().setTitle(iIncrementable.getTitle());
		getProgressPanel().showLog(false);
		getProgressPanel().setVisible(true);
	}

	/**
	 * Devuelve el componente ProgressPanel de la ventana incrementable.
	 * @return ProgressPanel
	 */
	public ProgressPanel getProgressPanel() {
		if (progressPanel == null) {
			progressPanel = new ProgressPanel(false);
		}
		return progressPanel;
	}
	
	protected void configureProgressPanel() {
		getProgressPanel().setAlwaysOnTop(true);
		getProgressPanel().addButtonPressedListener(this);
		
		// Must ask if user wants to cancel the process, avoid closing the dialog
		getProgressPanel().setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
		getProgressPanel().addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// Simulates an event like the produced pressing the cancel button of the associated progress panel
				actionButtonPressed(new ButtonsPanelEvent(getProgressPanel(), ButtonsPanel.BUTTON_CANCEL));				
			}
		});
	}

	private void callActionCommandListeners(int actions) {
		if (!bDoCallListeners)
			return;
		Iterator<IncrementableListener> acIterator = actionCommandListeners.iterator();
		while (acIterator.hasNext()) {
			IncrementableListener listener = (IncrementableListener) acIterator.next();
			switch (actions) {
				case IncrementableEvent.RESUMED:
					listener.actionResumed(new IncrementableEvent(this));
					break;
				case IncrementableEvent.SUSPENDED:
					listener.actionSuspended(new IncrementableEvent(this));
					break;
				case IncrementableEvent.CANCELED:
					listener.actionCanceled(new IncrementableEvent(this));
					break;
			}
		}
		eventId++;
	}

	/**
	 * Añadir el manejador de eventos para atender las peticiones de start,
	 * stop...
	 *
	 * @param listener
	 */
	public void addIncrementableListener(IncrementableListener listener) {
		if (!actionCommandListeners.contains(listener))
			actionCommandListeners.add(listener);
	}

	/**
	 * Borrar un manejador de eventos.
	 * @param listener
	 */
	public void removeIncrementableListener(IncrementableListener listener) {
		actionCommandListeners.remove(listener);
	}

	/**
	 * Definir si queremos que confirme al usuario si realmente desea cancelar el
	 * proceso
	 *
	 * @param value
	 */
	public void setAskCancel(boolean value) {
		askOnCancel = value;
	}

	/**
	 * Metodo para gestionar todos los eventos del objeto.
	 */
	public void actionButtonPressed(ButtonsPanelEvent e) {
		switch (e.getButton()) {
			case ButtonsPanel.BUTTON_CANCEL:
				boolean cancelled = true;
				if (askOnCancel) {
					if (! iIncrementable.isCancelable()) {
						JOptionPane.showMessageDialog(null, Messages.getText("The_process_cant_be_cancelled"), Messages.getText("Information"), JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					/* Pauses the process */
					if (iIncrementable.isPausable()) {
						try {
							callActionCommandListeners(IncrementableEvent.SUSPENDED);
						}
						catch (Exception iex) {
							Logger.getLogger(IncrementableTask.class).error(iex);
							JOptionPane.showMessageDialog(null, Messages.getText("Failed_pausing_the_process"), Messages.getText("Error"), JOptionPane.ERROR_MESSAGE);
						}
					}
	
					/* Asks user to cancel or not the process */
					cancelled = false;
					String string1 = Messages.getText("Yes");
					String string2 = Messages.getText("No");
					Object[] options = { string1, string2 };
					int answer = JOptionPane.showOptionDialog(getProgressPanel(), Messages
							.getText("msg_cancel_incrementable"), Messages
							.getText("title_cancel_incrementable"),
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
							options, string1);
					if (answer == JOptionPane.YES_OPTION) {
						cancelled = true;

						/* Continues the process */
						try {
							if (iIncrementable.isPausable())
								callActionCommandListeners(IncrementableEvent.RESUMED);
						} catch (Exception e2) {
							Logger.getLogger(IncrementableTask.class).error(e2);
							Messages.getText("Failed_resuming_the_process");
						}
					}
					else {
						/* Continues the process */
						try {
							if (iIncrementable.isPausable())
								callActionCommandListeners(IncrementableEvent.RESUMED);
						} catch (Exception e2) {
							Logger.getLogger(IncrementableTask.class).error(e2);
							Messages.getText("Failed_resuming_the_process");
						}
					}
				}
				if (cancelled) {
//					ended = true;
					// Will wait the process to finish and notify this to stop
					callActionCommandListeners(IncrementableEvent.CANCELED);
				}
				break;
			case ButtonsPanel.BUTTON_PAUSE:
				threadSuspended = true;

				/* Pauses the associated process */
				try {
					if (! iIncrementable.isPausable()) {
						JOptionPane.showMessageDialog(null, Messages.getText("The_process_cant_be_paused"), Messages.getText("Information"), JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						callActionCommandListeners(IncrementableEvent.SUSPENDED);
					}
				}
				catch (Exception iex) {
					Logger.getLogger(IncrementableTask.class).error(iex);
					JOptionPane.showMessageDialog(null, Messages.getText("Failed_pausing_the_process"), Messages.getText("Error"), JOptionPane.ERROR_MESSAGE);
				}

				break;
			case ButtonsPanel.BUTTON_RESTART:
				threadSuspended = false;

				/* Resumes the associated process */
				callActionCommandListeners(IncrementableEvent.RESUMED);
				break;
		}
	}

	/**
	 * @see ProgressPanel#getButtonsPanel()
	 */
	public ButtonsPanel getButtonsPanel() {
		return getProgressPanel().getButtonsPanel();
	}
}