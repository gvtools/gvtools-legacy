/* gvSIG. Sistema de Informacion Geografica de la Generalitat Valenciana
 *
 * Copyright (C) 2009 IVER T.I.
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
 *  IVER T.I.
 *   C/ Lerida, 20
 *   46009 Valencia
 *   SPAIN
 *   http://www.iver.es
 *   dac@iver.es
 *   +34 963163400
 *   
 *  or
 *  
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibañez, 50
 *   46010 VALENCIA
 *   SPAIN
 */
package org.gvsig.app.documents.table.summarize;

import java.util.BitSet;

import org.gvsig.app.documents.table.summarize.gui.SummarizeForm;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.plugins.PluginClassLoader;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.project.documents.table.gui.Table;

/**
 * Adds a Summarize tool for Tables.
 * 
 * @author IVER T.I. <http://www.iver.es> 01/02/2009
 */
public class TableSummarizeExtension extends Extension  {

	public void execute(String actionCommand) {
		try{
			
			IWindow v = PluginServices.getMDIManager().getActiveWindow();
		    if (v instanceof Table){
		    	
				Table table = (Table) PluginServices.getMDIManager().getActiveWindow();
				 try {
					DataSource sds = table.getModel().getModelo().getRecordset();
					BitSet indices = table.getSelectedFieldIndices();
					SummarizeForm st = new SummarizeForm();
					if (indices.cardinality()>0) {
						st.show(table, sds.getFieldName(indices.nextSetBit(0)));
					}
					else {
						st.show(table);
					}
				} catch (ReadDriverException e) {
					NotificationManager.showMessageError(PluginServices.getText(this, "Summarize_Error_accessing_the_table"), e);
				}
		    }
		} 
		catch (Exception e){
			NotificationManager.showMessageError(PluginServices.getText(this, "Unknown_summarize_error"), e);
		}
	}

	public void initialize() {
		initilializeIcons();
	}
	
	private void initilializeIcons(){
		PluginClassLoader loader = PluginServices.getPluginServices(this).getClassLoader();
		PluginServices.getIconTheme().registerDefault(
				"tableSummarize-statistics",
				loader.getResource("images/tableSummarize16.png")
			);
		PluginServices.getIconTheme().registerDefault(
				"tableSummarize-statistics-22x22",
				loader.getResource("images/tableSummarize22.png")
			);
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
//		IWindow v = PluginServices.getMDIManager().getActiveWindow();
//
//		if (v == null) {
//			return false;
//		}
//
//		if (v instanceof Table) {
//			Table table = (Table) v;
//			return doIsEnabled(table);
//		}
	}

//	protected boolean doIsEnabled(Table table){
//		BitSet indices = table.getSelectedFieldIndices();
//		//one column must be selected to activate the extension button
//		if (indices.cardinality() == 1) return true;
//		else return false;
//	}
	


	/**
	 * @see com.iver.andami.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		}

		if (v instanceof Table) {
			return true;
		}
		return false;
	}

}
