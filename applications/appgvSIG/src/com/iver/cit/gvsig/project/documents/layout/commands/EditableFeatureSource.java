/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibáñez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package com.iver.cit.gvsig.project.documents.layout.commands;

import java.awt.Image;

import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.edition.commands.CommandRecord;
import com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame;

/**
 * Interface of class to control the edition of FFrames.
 * 
 * @author Vicente Caballero Navarro
 */
public interface EditableFeatureSource {
	/**
	 * Returns from an index the FFrame.
	 * 
	 * @param index
	 * 
	 * @return FFrame.
	 */
	IFFrame getFFrame(int index);

	/**
	 * Returns all the fframes that are not removed.
	 * 
	 * @return Vector with fframes.
	 */
	IFFrame[] getFFrames();

	/**
	 * Returns the number of FFrame.
	 * 
	 * @return Number of FFrames
	 */
	int getFFrameCount();

	/**
	 * Add a FFrame in the mechanism of control creating a command.
	 * 
	 * @param f
	 *            FFrame to add
	 */
	void addFFrame(IFFrame f);

	/**
	 * Undo the last command added.
	 * 
	 * @throws EditionCommandException
	 */
	void undo() throws EditionCommandException;

	/**
	 * Redo the last command undid.
	 * 
	 * @throws EditionCommandException
	 */
	void redo() throws EditionCommandException;

	/**
	 * Returns if there are more commands to undo
	 * 
	 * @return True if there are more commands to undo
	 */
	boolean moreUndoCommands();

	/**
	 * Returns if there are more commands to redo
	 * 
	 * @return True if there are more commands to redo
	 */
	boolean moreRedoCommands();

	/**
	 * Remove the FFrame by the index.
	 * 
	 * @param index
	 */
	void removeFFrame(int index);

	/**
	 * Modify a fframe to another fframe new.
	 * 
	 * @param fant
	 *            Previous Fframe.
	 * @param fnew
	 *            New FFrame.
	 */
	boolean modifyFFrame(IFFrame fant, IFFrame fnew);

	void setImage(Image i);

	Image getImage();

	/**
	 * Start a composed command of other simpler commands. Create an only one
	 * command to reproduce if all at once.
	 */
	void startComplexCommand();

	/**
	 * Terminate a composed command.
	 */
	void endComplexCommand(String description);

	/**
	 * Undo add FFrame from index.
	 * 
	 * @param index
	 */
	void undoAddFFrame(int index);

	/**
	 * Add FFrame.
	 * 
	 * @param frame
	 * 
	 * @return index of new fframe.
	 */
	int doAddFFrame(IFFrame frame);

	/**
	 * Add FFrame from index.
	 * 
	 * @param frame
	 *            New FFrame.
	 * @param index
	 *            Index of new FFrame.
	 */
	void doAddFFrame(IFFrame frame, int index);

	/**
	 * Undo modify an FFrame modified.
	 * 
	 * @param fant
	 *            Previous fframe.
	 * @param fnew
	 *            New FFrame.
	 * @param indexAnt
	 *            Actual index.
	 * @param indexLast
	 *            Previous index.
	 */
	void undoModifyFFrame(int indexAnt, int previousIndex);

	/**
	 * Modify FFrame from index and new FFrame.
	 * 
	 * @param indexAnt
	 *            Actual index.
	 * @param frameNext
	 *            New FFrame.
	 * 
	 * @return New index of FFrame.
	 */
	int doModifyFFrame(int indexAnt, IFFrame frameNext);

	/**
	 * Undo Remove FFrame from index.
	 * 
	 * @param index
	 *            Actual index of FFrame.
	 */
	void undoRemoveFFrame(int index);

	/**
	 * Remove FFrame from actual index.
	 * 
	 * @param index
	 *            Actual index.
	 */
	void doRemoveFFrame(int index);

	/**
	 * Returns all the fframes, remove and done not remove.
	 * 
	 * @return All FFrames.
	 */
	IFFrame[] getAllFFrames();

	/**
	 * Returns the command record.
	 * 
	 * @return CommandRecord.
	 */
	CommandRecord getCommandRecord();
}
