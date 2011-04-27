/*#########################################################################
 *
 * A component of the Gatherer application, part of the Greenstone digital
 * library suite from the New Zealand Digital Library Project at the
 * University of Waikato, New Zealand.
 *
 * Author: John Thompson, Greenstone Digital Library, University of Waikato
 *
 * Copyright (C) 1999 New Zealand Digital Library Project
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *########################################################################
 */
package org.gvsig.gui;

import java.awt.AWTEvent;
import java.awt.ActiveEvent;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;



/** An extension of the JDialog that overrides the JVM's typical modal behaviour. This typical behaviour is that when a modal dialog is opened, all other windows cease to respond to user events until the modal dialog is disposed. However this prevents us opening the help documents property whenever a modal dialog is open. Thus we override the modal behaviour so that only the owner frame or dialog is blocked. 
 * Note that because we always call the super constructor with modal set to false, this should be made visible with setVisible(true) rather than show() which will return straight away. */
// feedback note: veronika had changed all the super constructor calls to 
// use modal instead of false - not sure if this is needed so I have not 
// put that in. --kjdon
public class ModalDialog
    extends JDialog {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6966218812722673601L;

	/** The current modal dialog being shown on screen, if any. */
    static public ModalDialog current_modal = null;

    /** true if this dialog should be modal, ie block user actions to its owner window, false otherwise. */
    protected boolean modal = false;
    /** true if this dialog is currently waiting some thread. */
    protected boolean waiting = false;

    /** Constructor.
     */
    public ModalDialog() {
	super((Frame)null, "", false);
    }

    /** Constructor.
     * @param parent the Dialog which is the owener of this dialog.
    */
    public ModalDialog(Dialog parent) {
	super(parent, "", false);
    }

    /** Constructor.
     * @param parent the Dialog which is the owener of this dialog.
     * @param modal true if this dialog should be modal, ie block user actions to its owner window, false otherwise.
     */
    public ModalDialog(Dialog parent, boolean modal) {
	super(parent, "", false);
	this.modal = modal;
    }
    
    /** Constructor.
     * @param parent the Dialog which is the owner of this dialog.
     * @param title the String to use as the title for this dialog.
     */
    public ModalDialog(Dialog parent, String title) {
	super (parent, title, false);
	this.modal = false;
    }

    /** Constructor.
     * @param parent the Dialog which is the owener of this dialog.
     * @param title the String to use as the title for this dialog.
     * @param modal true if this dialog should be modal, ie block user actions to its owner window, false otherwise.
     */
    public ModalDialog(Dialog parent, String title, boolean modal) {
	super (parent, title, false);
	this.modal = modal;
    }

   /** Constructor.
     * @param parent the Frame which is the owener of this dialog.
     */
    public ModalDialog(Frame parent) {
	super(parent, "", false);
    }

    /** Constructor.
     * @param parent the Frame which is the owener of this dialog.
     * @param modal whether this dialog is modal or not
     */
    public ModalDialog(Frame parent, boolean modal) {
	super(parent, "", false);
	this.modal = modal;
    }
    
    /** Constructor.
     * @param parent the Frame which is the owner of this dialog.
     * @param title the String to use as the title for this dialog.
     */
    public ModalDialog(Frame parent, String title) {
	super (parent, title, false);
    }

    /** Constructor.
     * @param parent the Frame which is the owener of this dialog.
     * @param title the String to use as the title for this dialog.
     * @param modal true if this dialog should be modal, ie block user actions to its owner window, false otherwise.
     */
    public ModalDialog(Frame parent, String title, boolean modal) {
	super (parent, title, false);
	this.modal = modal;
    }

    public void dispose() {
	super.dispose();
    }

    /** Ensures the current dialog is visible. */
    public void makeVisible() {
	super.setVisible(true);
    }

    /** The set visible method is overriden to provide modal functionality. It essentially hijacks control of the event dispatch thread while the dialog is open, only allowing non-user events to be passed to the parent dialog. Furthermore it only has this effect within the current AWT component tree by utilitizing the TreeLock.
     * @param visible true if this dialog should be painted on-screen, false otherwise.
     */
    public void setVisible(boolean visible)
    {
	if (visible) {
	    current_modal = this;
	}
	else {
	    current_modal = null;
	}

	// If we are in the AWT Dispatch thread then it is all good.
 	if (SwingUtilities.isEventDispatchThread()) {
 	    super.setVisible(visible);
	    if (modal && visible) {
		EventQueue theQueue = getToolkit().getSystemEventQueue();
		while (isVisible()) {
		    try {
			AWTEvent event = theQueue.getNextEvent();
			Object src = event.getSource();

			// Block all keyboard and mouse events to the parent component
			if (src.equals(getParent())) {
			    if (event instanceof KeyEvent || event instanceof MouseEvent) {
				// System.err.println("Event to parent component blocked.");
				continue;
			    }
			}

			// Re-dispatch other events
			if (event instanceof ActiveEvent) {
  			    ((ActiveEvent) event).dispatch();
  			}			    
			else if (src instanceof Component) {
			    ((Component) src).dispatchEvent(event);
			}
		    }
		    catch (Exception exception) {
		    	exception.printStackTrace();
		    }
		}
	    }
 	}
 	else {
 	    try {
 		SwingUtilities.invokeAndWait(new MakeDialogVisibleTask(this, visible));
 	    }
 	    catch (Exception exception) {
 	    	exception.printStackTrace();
 	    }
 	}
    }
    
    private class MakeDialogVisibleTask
	implements Runnable {
	private boolean make_visible;
	private ModalDialog dialog;
	public MakeDialogVisibleTask(ModalDialog dialog, boolean make_visible) {
	    this.dialog = dialog;
	    this.make_visible = make_visible;
	}
	public void run() {
	    // Blocks until the user dismisses the dialog
	    dialog.setVisible(make_visible);
	}
    }

    /** Overridden method so we can control modality and not rely on the Dialog default.
     * @param modal true if this dialog should be modal, ie block user actions to its owner window, false otherwise.
     */
    public void setModal (boolean modal) {
	this.modal = modal;
    }
}
