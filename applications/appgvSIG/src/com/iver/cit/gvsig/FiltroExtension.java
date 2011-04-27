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
package com.iver.cit.gvsig;

import java.awt.Component;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.hardcode.driverManager.DriverLoadException;
import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.data.DataSourceFactory;
import com.hardcode.gdbms.engine.instruction.EvaluationException;
import com.hardcode.gdbms.engine.instruction.SemanticException;
import com.hardcode.gdbms.parser.ParseException;
import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.plugins.Extension;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.FLayer;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.layers.layerOperations.AlphanumericData;
import com.iver.cit.gvsig.gui.filter.DefaultExpressionDataSource;
import com.iver.cit.gvsig.gui.filter.ExpressionListener;
import com.iver.cit.gvsig.gui.filter.FilterDialog;
import com.iver.cit.gvsig.project.documents.ProjectDocument;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.cit.gvsig.project.documents.view.IProjectView;
import com.iver.utiles.exceptionHandling.ExceptionListener;


/**
 * Extensión que abre un diálogo para poder hacer un filtro de una capa o tabla.
 *
 * @author Vicente Caballero Navarro
 */
public class FiltroExtension extends Extension implements ExpressionListener {
	protected SelectableDataSource dataSource = null;
	protected Table vista;
	private String filterTitle;

	/**
	 * DOCUMENT ME!
	 */
	public void initialize() {
		registerIcons();
	}

	private void registerIcons(){
		PluginServices.getIconTheme().registerDefault(
				"table-filter",
				this.getClass().getClassLoader().getResource("images/Filtro.png")
			);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param actionCommand DOCUMENT ME!
	 */
	public void execute(String actionCommand) {
		if ("FILTRO".equals(actionCommand)) {
			try {
				IWindow v = PluginServices.getMDIManager().getActiveWindow();

				if (v instanceof Table) {
					vista = (Table) v;

					dataSource = vista.getModel().getModelo().getRecordset();
					filterTitle = vista.getModel().getName();
					vista.getModel().setModified(true);
				} else if (v instanceof com.iver.cit.gvsig.project.documents.view.gui.View) {
					IProjectView pv = ((com.iver.cit.gvsig.project.documents.view.gui.View) v).getModel();
					filterTitle = ((com.iver.cit.gvsig.project.documents.view.gui.View) v).getModel().getName();
					FLayer layer = pv.getMapContext()
					.getLayers().getActives()[0];
					dataSource = pv.getProject().getDataSourceByLayer(layer);
					((ProjectDocument)pv).setModified(true);
				}
			}  catch (ReadDriverException e) {
				NotificationManager.addError("Error filtrando", e);
			}

			doExecute();
		}
	}

	/**
	 * "execute" method action.
	 *
	 */
	protected void doExecute(){
		DefaultExpressionDataSource ds = new DefaultExpressionDataSource();
		ds.setTable(dataSource);
		FilterDialog dlg = new FilterDialog(filterTitle);
		dlg.addExpressionListener(this);
		dlg.addExceptionListener(new ExceptionListener() {
			public void exceptionThrown(Throwable t) {
				NotificationManager.addError(t.getMessage(), t);
			}
		});
		dlg.setModel(ds);
		PluginServices.getMDIManager().addWindow(dlg);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean isEnabled() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		}

		if (v instanceof Table) {
			return true;
		} else {
			if (v instanceof com.iver.cit.gvsig.project.documents.view.gui.View) {
				com.iver.cit.gvsig.project.documents.view.gui.View view = (com.iver.cit.gvsig.project.documents.view.gui.View) v;
				IProjectView pv = view.getModel();
				FLayer[] seleccionadas = pv.getMapContext().getLayers()
				.getActives();

				if (seleccionadas.length == 1) {
					if (seleccionadas[0].isAvailable() && seleccionadas[0] instanceof AlphanumericData) {
						return true;
					}
				}
			}

			return false;
		}

	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean isVisible() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		}

		if (v instanceof Table) {
			return true;
		} else {
			if (v instanceof com.iver.cit.gvsig.project.documents.view.gui.View) {
				com.iver.cit.gvsig.project.documents.view.gui.View view = (com.iver.cit.gvsig.project.documents.view.gui.View) v;
				IProjectView pv = view.getModel();
				FLayer[] seleccionadas = pv.getMapContext().getLayers()
				.getActives();

				if (seleccionadas.length == 1) {
					if (seleccionadas[0] instanceof AlphanumericData) {
						return true;
					}
				}
			}

			return false;
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param expression DOCUMENT ME!
	 */
	public void newSet(String expression) {
		// By Pablo: if no filter expression -> no element selected
		if (! this.filterExpressionFromWhereIsEmpty(expression)) {
			try {
				long[] sel = doSet(expression);

				if (sel == null) {
					//throw new RuntimeException("Not a 'where' clause?");
					return;
				}

				FBitSet selection = new FBitSet();

				for (int i = 0; i < sel.length; i++) {
					selection.set((int) sel[i]);
				}

				dataSource.clearSelection();
				dataSource.setSelection(selection);
			}catch(Exception e){
				JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(), "Asegurate de que la consulta es correcta.");
			}
		}
		else {
			// By Pablo: if no expression -> no element selected
			dataSource.clearSelection();
		}
	}

	/**
	 * @see com.iver.cit.gvsig.gui.filter.ExpressionListener#newSet(java.lang.String)
	 */
	private long[] doSet(String expression) {
		try {
			DataSource ds = LayerFactory.getDataSourceFactory().executeSQL(expression,
					DataSourceFactory.MANUAL_OPENING);

			return ds.getWhereFilter();
		} catch (DriverLoadException e) {
			JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"driver_error")+"\n"+e.getMessage());
		} catch (ReadDriverException e) {
			JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"driver_error")+"\n"+e.getMessage());
		} catch (ParseException e) {
			JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"parse_expresion_error")+"\n"+e.getMessage());
		} catch (SemanticException e) {
			JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"semantic_expresion_error")+"\n"+e.getMessage());
		} catch (IOException e) {
			JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"input_output_error")+"\n"+e.getMessage());
		} catch (EvaluationException e) {
			JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"parse_expresion_error")+"\n"+e.getMessage());
		} catch (com.hardcode.gdbms.parser.TokenMgrError e) {
			JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"expresion_error")+"\n"+e.getMessage());
		} catch (Exception e) {
			JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"expresion_error")+"\n"+e.getMessage());
		}catch (Error e) {
			JOptionPane.showMessageDialog((Component)PluginServices.getMainFrame(),PluginServices.getText(this,"expresion_error")+"\n"+e.getMessage());
		}
		return null;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param expression DOCUMENT ME!
	 */
	public void addToSet(String expression) {
		// By Pablo: if no filter expression -> don't add more elements to set
		if (! this.filterExpressionFromWhereIsEmpty(expression)) {
			long[] sel = doSet(expression);

			if (sel == null) {
				//throw new RuntimeException("Not a 'where' clause?");
				return;
			}

			FBitSet selection = new FBitSet();

			for (int i = 0; i < sel.length; i++) {
				selection.set((int) sel[i]);
			}

			FBitSet fbs = dataSource.getSelection();
			fbs.or(selection);
			dataSource.setSelection(fbs);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param expression DOCUMENT ME!
	 */
	public void fromSet(String expression) {
		// By Pablo: if no filter expression -> no element selected
		if (! this.filterExpressionFromWhereIsEmpty(expression)) {
			long[] sel = doSet(expression);

			if (sel == null) {
				throw new RuntimeException("Not a 'where' clause?");
			}

			FBitSet selection = new FBitSet();

			for (int i = 0; i < sel.length; i++) {
				selection.set((int) sel[i]);
			}

			FBitSet fbs = dataSource.getSelection();
			fbs.and(selection);
			dataSource.setSelection(fbs);
		}
		else {
			// By Pablo: if no expression -> no element selected
			dataSource.clearSelection();
		}
	}

	/**
	 * Returns true if the WHERE subconsultation of the filterExpression is empty ("")
	 *
	 * @author Pablo Piqueras Bartolomé (p_queras@hotmail.com)
	 * @param expression An string
	 * @return A boolean value
	 */
	private boolean filterExpressionFromWhereIsEmpty(String expression) {
		String subExpression = expression.trim();
		int pos;

		// Remove last ';' if exists
		if (subExpression.charAt(subExpression.length() -1) == ';')
			subExpression = subExpression.substring(0, subExpression.length() -1).trim();

		// If there is no 'where' clause
		if ((pos = subExpression.indexOf("where")) == -1)
			return false;

		// If there is no subexpression in the WHERE clause -> true
		subExpression = subExpression.substring(pos + 5, subExpression.length()).trim(); // + 5 is the length of 'where'
		if ( subExpression.length() == 0 )
			return true;
		else
			return false;
	}
}
