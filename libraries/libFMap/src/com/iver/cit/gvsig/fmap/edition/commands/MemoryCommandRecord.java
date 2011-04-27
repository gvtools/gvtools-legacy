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
import java.util.ArrayList;
import java.util.Stack;

import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;


/**
 * Clase en memoria para registrar y gestionar los comandos que vamos
 * realizando. La forma en que ha sido implementada esta clase, en vez de una
 * única lista para albergar los comandos de deshacer(undos) y los de
 * rehacer(redos), se ha optado por dos pilas una para deshacer(undos) y otra
 * para rehacer(redos), de esta forma :  Cuando se añade un nuevo comando, se
 * inserta este a la pila de deshacer(undos) y se borra de la de
 * rehacer(redos). Si se realiza un deshacer se desapila este comando de la
 * pila deshacer(undos) y se apila en la de rehacer(redos). Y de la misma
 * forma cuando se realiza un rehacer se desapila este comando de la pila de
 * rehacer(redos) y pasa a la de deshacer(undos).
 *
 * @author Vicente Caballero Navarro
 */
public class MemoryCommandRecord implements CommandRecord {
	private Stack undos = new Stack();
	private Stack redos = new Stack();
	private ArrayList commandsListener=new ArrayList();
	private boolean refresh=true;
	private int undosCount=0;

	/**
	 * Añade el comando a la pila de deshacer y borra todo el contenido de la
	 * pila rehacer.
	 *
	 * @param command Comando a añadir.
	 *
	 * @throws IOException
	 * @throws DriverIOException
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.CommandRecord#addCommand(com.iver.cit.gvsig.fmap.edition.Command)
	 */
	public void pushCommand(Command command){
		undos.add(command);
		redos.clear();
		refresh=true;
		fireCommandRefresh();
	}

	/**
	 * Realiza la operación del último comando de deshacer y lo apila en los de
	 * rehacer.
	 * @throws EditionCommandException
	 *
	 * @throws DriverIOException
	 * @throws IOException
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.CommandRecord#delCommand(com.iver.cit.gvsig.fmap.edition.Command)
	 */
	public void undoCommand() throws EditionCommandException {
		Command command = (Command) undos.pop();
		command.undo();
		redos.add(command);
		fireCommandsRepaint(new CommandEvent(command));
	}

	/**
	 * Realiza la operación de rehacer el último comando apilado y lo añade a
	 * la pila deshacer.
	 *
	 * @throws DriverIOException
	 * @throws IOException
	 *
	 * @see com.iver.cit.gvsig.fmap.edition.CommandRecord#redoCommand()
	 */
	public void redoCommand() throws EditionCommandException {
		Command command = (Command) redos.pop();
		command.redo();
		undos.add(command);
		fireCommandsRepaint(new CommandEvent(command));
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.CommandRecord#moreUndoCommands()
	 */
	public boolean moreUndoCommands() {
		return (!undos.isEmpty());
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.edition.CommandRecord#moreRedoCommands()
	 */
	public boolean moreRedoCommands() {
		return (!redos.isEmpty());
	}
	public Command[] getUndoCommands(){
		Stack clonedUndos=(Stack)undos.clone();

		ArrayList commands=new ArrayList();
		while (!clonedUndos.isEmpty()){
			commands.add(clonedUndos.pop());
		}

		return (Command[])commands.toArray(new Command[0]);
	}
	public Command[] getRedoCommands(){
		Stack clonedRedos=(Stack)redos.clone();

		ArrayList commands=new ArrayList();
		while (!clonedRedos.isEmpty()){
			commands.add(clonedRedos.pop());
		}
		return (Command[])commands.toArray(new Command[0]);
	}
	public int getPos(){
		if (refresh){
			undosCount=undos.size()-1;
		}
		return undosCount;
	}
	public void setPos(int newpos) throws EditionCommandException{
		int pos=getPos();
		if (newpos>pos){
			for (int i=pos;i<newpos;i++){
				redoCommand();
				System.out.println("redos = "+i);
			}
		}else if (pos>newpos){
			for (int i=pos-1;i>=newpos;i--){
				undoCommand();
				System.out.println("undos = "+i);
			}
		}
	}

	public void addCommandListener(CommandListener cl) {
		if (!commandsListener.contains(cl))
			commandsListener.add(cl);
	}

	public void fireCommandsRepaint(CommandEvent e) {
		for (int i=0;i<commandsListener.size();i++){
			((CommandListener)commandsListener.get(i)).commandRepaint();
		}
	}
	public void fireCommandRefresh(){
		for (int i=0;i<commandsListener.size();i++){
			((CommandListener)commandsListener.get(i)).commandRefresh();
		}
	}

	public int getCommandCount() {
		return undos.size()+redos.size();
	}

	public void removeCommandListener(CommandListener e) {
		commandsListener.remove(e);
	}

	public void clearAll() {
		redos.clear();
		undos.clear();

	}
}
