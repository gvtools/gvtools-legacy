package com.iver.cit.gvsig.gui.command;

import javax.swing.table.AbstractTableModel;

import com.iver.cit.gvsig.fmap.edition.commands.Command;
import com.iver.cit.gvsig.fmap.edition.commands.CommandRecord;

public class MyModel extends AbstractTableModel{
private CommandRecord cr;

public MyModel(CommandRecord cr) {
	this.cr=cr;
	}
	public int getPos() {
		return cr.getPos();
	}
	public int getColumnCount() {
		return 1;
	}
	public int getRowCount() {
		return cr.getCommandCount();
	}
	public Object getValueAt(int i, int j) {
		Command[] undos=cr.getUndoCommands();
		Command[] redos=cr.getRedoCommands();
		if (i<undos.length){
			//System.out.println("undo i=" + i + " index=" + (undos.length-1-i));
			return undos[undos.length-1-i];
		}else{
			//System.out.println("redo i=" + i + " index=" + (i-undos.length));
			return redos[i-undos.length];
		}
	}
}
