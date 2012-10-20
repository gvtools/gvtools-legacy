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

import java.io.IOException;

import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;

/**
 * Registro para apilar los comandos que se van ejecutando.
 * 
 * @author Vicente Caballero Navarro
 */
public interface CommandRecord {
	/**
	 * Añade un nuevo comando a la pila de deshacer(undos) y borra la de
	 * rehacer(redos).
	 * 
	 * @param command
	 *            Comando a añadir.
	 * @throws DriverIOException
	 * @throws IOException
	 */
	void pushCommand(Command command);

	/**
	 * Deshace, ejecutando el último comando de la pila de undos y cambiandolo
	 * de esta pila a la de redos.
	 * 
	 * @throws IOException
	 * @throws DriverIOException
	 * 
	 * @throws DriverIOException
	 * @throws IOException
	 * @throws EditionCommandException
	 */
	void undoCommand() throws EditionCommandException;

	/**
	 * Rehacer, ejecuta el último comando apilado en redos y lo cambia a la pila
	 * de undos.
	 * 
	 * @throws IOException
	 * @throws DriverIOException
	 * @throws IOException
	 * @throws DriverIOException
	 */
	void redoCommand() throws EditionCommandException;

	/**
	 * Devuelve true si quedan más comandos aplilados para deshacer.
	 * 
	 * @return True si quedan comandos de deshacer.
	 */
	boolean moreUndoCommands();

	/**
	 * Devuelve true si quedan comandos para rehacer.
	 * 
	 * @return True si quedan comandos para rehacer.
	 */
	boolean moreRedoCommands();

	public Command[] getUndoCommands();

	public Command[] getRedoCommands();

	public int getPos();

	public void setPos(int pos) throws EditionCommandException;

	public void addCommandListener(CommandListener ecl);

	public void removeCommandListener(CommandListener e);

	public void fireCommandsRepaint(CommandEvent e);

	public int getCommandCount();

	/**
	 * Removes all do's and undo's
	 */
	public void clearAll();

}
