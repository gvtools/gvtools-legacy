package org.gvsig.hyperlink;

import java.net.URI;

import com.iver.utiles.extensionPoints.IExtensionBuilder;

/**
 * TODO document this interface
 * This interface must be implemented by format managers for the
 * hyperlink tool. A manager is able to load an specific file, either
 * by loading it in an AbstractHyperLinkPanel or by opening the proper
 * program to do the task.
 * 
 * Format managers must be registered in the ExtensionPoint named
 * "HyperLinkAction" in order to be available in the HyperLink tool.
 *  
 * @author cesar
 *
 */
public interface ILinkActionManager extends IExtensionBuilder {
	
	public void showDocument(URI doc) throws UnsupportedOperationException;
	
	public boolean hasPanel();

	public AbstractHyperLinkPanel createPanel(URI doc) throws UnsupportedOperationException;
	
	public String getActionCode();

	public String getName();

	public String getDescription();
}
