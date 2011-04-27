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

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.edition.commands.AbstractCommand;
import com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame;

public class ModifyFFrameCommand extends AbstractCommand{
//	private AddFFrameCommand addFFrameCommand;
//	private RemoveFFrameCommand removeFFrameCommand;
	private int index;
	private int newIndex;
	private DefaultEditableFeatureSource defs;
	private IFFrame frame;
	public ModifyFFrameCommand(EditableFeatureSource efs,IFFrame frame,int index,int newIndex){
		this.index=index;
		this.newIndex=newIndex;
		defs=(DefaultEditableFeatureSource)efs;
		this.frame=frame;
//		removeFFrameCommand=new RemoveFFrameCommand(efs,index);
//		addFFrameCommand=new AddFFrameCommand(efs,frame,newIndex);

	}
	public void undo() {
//		removeFFrameCommand.undo();
//		addFFrameCommand.undo();
		defs.undoModifyFFrame(index,newIndex);

	}

	public void redo() {
//		removeFFrameCommand.redo();
//		addFFrameCommand.redo();
		defs.redoModifyFFrame(index,newIndex,frame);

	}
	public String getType() {
		return PluginServices.getText(this,"modificar");
	}

}
