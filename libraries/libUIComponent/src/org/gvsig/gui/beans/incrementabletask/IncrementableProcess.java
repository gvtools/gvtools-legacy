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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.gvsig.gui.beans.Messages;
import org.gvsig.gui.beans.buttonspanel.ButtonsPanel;
import org.gvsig.gui.beans.progresspanel.LogControl;

/**
 * Process to be executed by an {@link IncrementableTask IncrementableTask}.
 * 
 * @version 20/08/2008
 * 
 * @author Pablo Piqueras Bartolomé (pablo.piqueras@iver.es)
 */
public abstract class IncrementableProcess implements IIncrementable, IncrementableListener, Runnable {
	protected IncrementableTask 	iTask	 		= null;
	protected LogControl 			log		 		= new LogControl();
	protected int 					percentage		= 0;
	protected boolean 				ended			= false;
	protected volatile boolean		threadSuspended = false;
	protected volatile Thread 		blinker			= null;
	protected long 					t0 				= 0;
	protected static Cancellable 	cancelProcess 	= null;
	protected boolean 				isCancellable 	= true;
	protected boolean 				isPausable	 	= false;
	

	protected String 				title 			= "";
	protected String 				label 			= "";

	/**
	 * Creates a new process.
	 * 
	 * @param title title for the dialog that displays the evolution of the process
	 */
	public IncrementableProcess(String title) {
		super();
		this.title = title;
		this.cancelProcess = new Cancel();
	}

	/**
	 * Creates a new process.
	 * 
	 * @param title title for the dialog that displays the evolution of the process
	 * @param label brief of this process, that will be displayed in the dialog
	 */
	public IncrementableProcess(String title, String label) {
		super();
		this.title = title;
		this.label = label;
		this.cancelProcess = new Cancel();
	}

	/**
	 * Creates a new process.
	 * 
	 * @param title title for the dialog that displays the evolution of the process
	 * @param label brief of this process, that will be displayed in the dialog
 	 * @param cancellable determines if this process can be canceled
 	 * @param pausable determines if this process can be paused
	 */
	public IncrementableProcess(String title, String label, boolean cancellable, boolean pausable) {
		super();
		this.title = title;
		this.label = label;
		this.cancelProcess = new Cancel();
		this.isCancellable = cancellable;
		this.isPausable = pausable;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Adds a new line to the log.
	 * 
	 * @param line
	 *            the line to add
	 */
	protected void insertLineLog(String line) {
		log.addLine(line);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * <p>Sets a brief of the current subprocess.</p>
	 * 
	 * @param label brief of the current subprocess
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#getLog()
	 */
	public String getLog() {
		return log.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#getPercent()
	 */
	public int getPercent() {
		if (percentage > 100)
			percentage = 100;

		if (percentage == 100)
			ended = true;

		return percentage;
	}

	/**
	 * Creates and starts a new thread to execute this importation process.
	 * 
	 * @see Thread#start()
	 * @see #stop()
	 */
	public void start() {
		blinker = new Thread(this);
		blinker.start();
	}

	/**
	 * @see Thread#stop()
	 * @see #start()
	 */
	public synchronized void stop() {
		ended = true;
		blinker = null;
		notify();
	}

	/**
	 * @see Thread#isAlive()
	 * @see #start()
	 */
	public boolean isAlive() {
		return blinker.isAlive();
	}

	/**
	 * Ends the thread that displays the progress dialog with the evolution of
	 * the loading process.
	 * 
	 * @see IncrementableTask#processFinalize()
	 */
	protected void processFinalize() {
		iTask.processFinalize();
	}

	/**
	 * Sets the object that will display the evolution of this loading process
	 * as a progress dialog.
	 * 
	 * @param iTask
	 *            the object that will display the evolution of this loading
	 *            process
	 */
	public void setIncrementableTask(IncrementableTask iTask) {
		this.iTask = iTask;
		iTask.setAskCancel(true);
		iTask.getButtonsPanel().addAccept();
		iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_ACCEPT, false);

		JButton jButton = iTask.getButtonsPanel().getButton(
				ButtonsPanel.BUTTON_ACCEPT);
		jButton.addMouseListener(new MouseAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
			 */
			public void mouseClicked(MouseEvent e) {
				processFinalize();
			}
		});
	}

	/**
	 * Determines if this thread has been suspended.
	 * 
	 * @return <code>true</code> if this thread has been suspended; otherwise <code>false</code>
	 */
	public boolean isSuspended() {
		return threadSuspended;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		String text = null;

		try {
			process();
			while (! ended) {
				t0 += 500;
                Thread.currentThread().sleep(150);
			}
		} catch (Exception ie) {
			if (! cancelProcess.isCanceled()) {
				Logger.getLogger(IncrementableProcess.class).error(ie);
				label = Messages.getText("Process_failed");
				iTask.getProgressPanel().setLabel(label);
				text = Messages.getText("Failed_the_process");
			}
			else {
				label = Messages.getText("Process_canceled");
				iTask.getProgressPanel().setLabel(label);
				text = Messages.getText("Process_canceled");
			}
		}
		finally {
			iTask.setAskCancel(false);
			iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_ACCEPT, true);
			iTask.getButtonsPanel().setEnabled(ButtonsPanel.BUTTON_CANCEL, false);

			if (text != null) {
				log.addLine(Messages.getText("Percent") + ": " + getPercent());
				log.addLine(text);
				
				if (cancelProcess.isCanceled())
					JOptionPane.showMessageDialog(iTask.getButtonsPanel(), text, Messages.getText("Information"), JOptionPane.INFORMATION_MESSAGE);
				else
					JOptionPane.showMessageDialog(iTask.getButtonsPanel(), text, Messages.getText("Error"), JOptionPane.ERROR_MESSAGE);
			}
			
			if (percentage == 100) {
				label = Messages.getText("Process_finished");
				iTask.getProgressPanel().setLabel(label);
//				iTask.getProgressPanel().setPercent(100); // Forces setting the progress bar at 100 %
			}

			// Ends this process
			ended = true;
			
			// Ends the progress panel
			iTask.stop();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionCanceled(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
	 */
	public void actionCanceled(IncrementableEvent e) {
		if (percentage < 100) {
			// If was cancelled
			if (ended == true)
				processFinalize();

			if (isCancellable)
				cancelProcess.setCanceled(true);
			else
				JOptionPane.showMessageDialog(null, Messages.getText("The_process_cant_be_cancelled"), Messages.getText("Warning"), JOptionPane.WARNING_MESSAGE);
		}
		else {
			Logger.getLogger(IncrementableProcess.class).warn(Messages.getText("Process_finished_wont_be_cancelled"));

			processFinalize();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionResumed(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
	 */
	public void actionResumed(IncrementableEvent e) {
//		if (isPausable)
//			resume();
		if ((isPausable) && (threadSuspended)) {
			threadSuspended = false;
	
			blinker.resume();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IncrementableListener#actionSuspended(org.gvsig.gui.beans.incrementabletask.IncrementableEvent)
	 */
	public void actionSuspended(IncrementableEvent e) {
		try {
			if (isPausable) {
				if ( ! threadSuspended) {
					threadSuspended = true;
			
					blinker.suspend();
				}
			}
			
			
//			if (isPausable)
//				suspend();
			else
				JOptionPane.showMessageDialog(null, Messages.getText("The_process_cant_be_paused"), Messages.getText("Warning"), JOptionPane.WARNING_MESSAGE);
		}
		catch (Exception iex) {
			Logger.getLogger(IncrementableTask.class).error(iex);
			JOptionPane.showMessageDialog(null, Messages.getText("Failed_pausing_the_process"), Messages.getText("Error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#isCancelable()
	 */
	public boolean isCancelable() {
		return isCancellable;
	}

	/**
	 * <p>Sets if this process can be canceled.</p>
	 * 
	 * @param b <code>true</code> if this process can be canceled, otherwise <code>false</code>
	 */
	public void setCancelable(boolean b) {
		isCancellable = b;
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#isPausable()
	 */
	public boolean isPausable() {
		return isPausable;
	}

	/**
	 * <p>Sets if this process can be paused.</p>
	 * 
	 * @param b <code>true</code> if this process can be paused, otherwise <code>false</code>
	 */
	public void setPausable(boolean b) {
		isPausable = b;
	}
	/*
	 * (non-Javadoc)
	 * @see org.gvsig.gui.beans.incrementabletask.IIncrementable#process()
	 */
	public abstract void process() throws InterruptedException, Exception;
}
