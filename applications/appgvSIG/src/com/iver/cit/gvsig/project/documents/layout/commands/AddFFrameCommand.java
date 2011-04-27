
package com.iver.cit.gvsig.project.documents.layout.commands;

import java.io.IOException;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.drivers.DriverIOException;
import com.iver.cit.gvsig.fmap.edition.commands.AbstractCommand;
import com.iver.cit.gvsig.project.documents.layout.fframes.IFFrame;


/**
 * Añade una geometría nueva al EditableFeatureSource
 */
public class AddFFrameCommand extends AbstractCommand {
	private IFFrame frame;
	private int index;
	private EditableFeatureSource efs;
	//private DefaultEditableFeatureSource efs;
	public AddFFrameCommand(EditableFeatureSource efs,IFFrame f,int i){
		this.efs=efs;
		index=i;
		frame=f;
	}
	/**
	 * @throws IOException
	 * @throws DriverIOException
	 * @throws IOException
	 * @throws DriverIOException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#undo()
	 */
	public void undo(){
		efs.undoAddFFrame(index);
	}
	/**
	 * @throws DriverIOException
	 * @throws IOException
	 * @see com.iver.cit.gvsig.fmap.edition.Command#redo()
	 */
	public void redo(){
		efs.doAddFFrame(frame,index);

	}
	public String getType() {
		return PluginServices.getText(this,"Anadir");
	}

}
