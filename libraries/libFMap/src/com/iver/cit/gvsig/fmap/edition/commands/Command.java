package com.iver.cit.gvsig.fmap.edition.commands;

import com.iver.cit.gvsig.exceptions.commands.EditionCommandException;

public interface Command {

	void undo() throws EditionCommandException;

	void redo() throws EditionCommandException;

	String getType();

	String getDescription();

	void setDescription(String descrip);

	String getDate();

	String getTime();
}
