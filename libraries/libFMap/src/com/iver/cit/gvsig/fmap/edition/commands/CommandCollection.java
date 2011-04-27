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
package com.iver.cit.gvsig.fmap.edition.commands;

import java.util.ArrayList;

import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;


public class CommandCollection extends AbstractCommand{
	ArrayList commands=new ArrayList();
	public boolean isEmpty() {
		return commands.size()==0;
	}
	/**
	 * @throws EditionCommandException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#undo()
	 */
	public void undo() throws EditionCommandException {
	for(int i=commands.size()-1;i>=0;i--){
		((Command)commands.get(i)).undo();
	}
	}

	/**
	 * @throws EditionCommandException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#redo()
	 */
	public void redo() throws EditionCommandException {
		for (int i=0;i<commands.size();i++){
			((Command)commands.get(i)).redo();
		}
	}
	public void add(Command c){
		commands.add(c);
	}

	public String getType() {
		if (commands.size() == 0)
			return null;
		ArrayList types=new ArrayList(3);
		for (int i=0;i<commands.size();i++) {
			String type=((Command)commands.get(i)).getType();
			if (!types.contains(type))
				types.add(type);
		}
		String type="";
		type=(String)types.get(0);
		for (int i=1;i<types.size();i++) {
			type=type+"-"+(String)types.get(i);
		}
		return type;
	}
}
