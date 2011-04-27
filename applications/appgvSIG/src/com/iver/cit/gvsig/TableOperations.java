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

import java.awt.Dimension;
import java.io.IOException;

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
import com.iver.cit.gvsig.fmap.drivers.FieldDescription;
import com.iver.cit.gvsig.fmap.edition.EditableAdapter;
import com.iver.cit.gvsig.fmap.layers.FBitSet;
import com.iver.cit.gvsig.fmap.layers.LayerFactory;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.operations.arcview.ArcJoinDataSource;
import com.iver.cit.gvsig.gui.filter.ExpressionListener;
import com.iver.cit.gvsig.project.Project;
import com.iver.cit.gvsig.project.documents.table.FieldSelectionModel;
import com.iver.cit.gvsig.project.documents.table.ObjectSelectionStep;
import com.iver.cit.gvsig.project.documents.table.ProjectTable;
import com.iver.cit.gvsig.project.documents.table.ProjectTableFactory;
import com.iver.cit.gvsig.project.documents.table.TableSelectionModel;
import com.iver.cit.gvsig.project.documents.table.gui.AndamiWizard;
import com.iver.cit.gvsig.project.documents.table.gui.JoinWizardController;
import com.iver.cit.gvsig.project.documents.table.gui.Table;
import com.iver.utiles.swing.objectSelection.SelectionException;
import com.iver.utiles.swing.wizard.WizardControl;
import com.iver.utiles.swing.wizard.WizardEvent;
import com.iver.utiles.swing.wizard.WizardListener;


/**
 * Extensión que controla las operaciones realizadas sobre las tablas.
 *
 * @author Fernando González Cortés
 */
public class TableOperations extends Extension implements ExpressionListener {
	private SelectableDataSource dataSource = null;
	//private Table vista;
	private String defaultSourceTable = null;

	
	/* execute with a default source table name; this is useful for calling
	   JOIN from the layer context menu in the TOC */	
	public void execute(String actionCommand, String defaultTable ) {
		defaultSourceTable = defaultTable;		
		this.execute(actionCommand);
	}
	
	/**
	 * @see com.iver.mdiApp.plugins.IExtension#updateUI(java.lang.String)
	 */
	public void execute(String actionCommand) {
		ProjectExtension pe = (ProjectExtension) PluginServices.getExtension(ProjectExtension.class);
		Project project=pe.getProject();
		ProjectTable[] pts = (ProjectTable[]) project.getDocumentsByType(ProjectTableFactory.registerName)
			.toArray(new ProjectTable[0]);
		if ("JOIN".equals(actionCommand)) {
			if ( defaultSourceTable == null ) {
				JoinWizardController wizardController = new JoinWizardController(this);
				wizardController.runWizard(pts);
			} else {
				JoinWizardController wizardController = new JoinWizardController(this, defaultSourceTable);
				wizardController.runWizard(pts);
			}			
		}else if ("LINK".equals(actionCommand)) {
			try {
				final ObjectSelectionStep sourceTable = new ObjectSelectionStep();
				sourceTable.setModel(new TableSelectionModel(pts,
						PluginServices.getText(this, "seleccione_tabla_origen")));

				final ObjectSelectionStep targetTable = new ObjectSelectionStep();
				targetTable.setModel(new TableSelectionModel(pts,
				        PluginServices.getText(this, "seleccione_tabla_a_enlazar")));

				final ObjectSelectionStep firstTableField = new ObjectSelectionStep();
				final ObjectSelectionStep secondTableField = new ObjectSelectionStep();
				final AndamiWizard wiz = new AndamiWizard(PluginServices.getText(this, "back"), PluginServices.getText(this, "next"), PluginServices.getText(this, "finish"), PluginServices.getText(this, "cancel"));
				//wiz.setSize(new Dimension(450,200));
				wiz.addStep(sourceTable);
				wiz.addStep(firstTableField);
				wiz.addStep(targetTable);
				wiz.addStep(secondTableField);

				wiz.addWizardListener(new WizardListener() {
						public void cancel(WizardEvent w) {
							PluginServices.getMDIManager().closeWindow(wiz);
						}

						public void finished(WizardEvent w) {
							PluginServices.getMDIManager().closeWindow(wiz);

							ProjectTable sourceProjectTable = (ProjectTable) sourceTable.getSelected();
							SelectableDataSource sds1=null;;
							try {
								sds1 = sourceProjectTable.getModelo().getRecordset();
							} catch (ReadDriverException e) {
								e.printStackTrace();
							}
							//String tableName1 = sds1.getName();

							ProjectTable targetProjectTable = (ProjectTable) targetTable.getSelected();
							SelectableDataSource sds2=null;
							try {
								sds2 = targetProjectTable.getModelo().getRecordset();
							} catch (ReadDriverException e) {
								e.printStackTrace();
							}

							//String tableName2 = sds2.getName();

							String field1 = (String) firstTableField.getSelected();
							String field2 = (String) secondTableField.getSelected();
							sourceProjectTable.setLinkTable(sds2.getName(),field1,field2);
							((ProjectExtension)PluginServices.getExtension(ProjectExtension.class)).getProject().setLinkTable();
						}

						public void next(WizardEvent w) {
							WizardControl wiz = w.wizard;
							wiz.enableBack(true);
							wiz.enableNext(((ObjectSelectionStep) wiz.getCurrentStep()).getSelectedItem() != null);

							if (w.currentStep == 1) {
								ProjectTable pt = (ProjectTable) sourceTable.getSelected();

								try {
									firstTableField.setModel(new FieldSelectionModel(
											pt.getModelo().getRecordset(),
											PluginServices.getText(this, "seleccione_campo_enlace"),
											-1));
								} catch (SelectionException e) {
									NotificationManager.addError("Error obteniendo los campos de la tabla",
										e);
								} catch (ReadDriverException e) {
									e.printStackTrace();
								}
							} else if (w.currentStep == 3) {
								try {
									//tabla
									ProjectTable pt = (ProjectTable) sourceTable.getSelected();

									//índice del campo
									SelectableDataSource sds = pt.getModelo().getRecordset();
									String fieldName = (String) firstTableField.getSelected();
									int fieldIndex = sds.getFieldIndexByName(fieldName);
									int type = sds.getFieldType(fieldIndex);

									secondTableField.setModel(new FieldSelectionModel(
											((ProjectTable) targetTable.getSelected()).getModelo().getRecordset(),
											PluginServices.getText(this, "seleccione_campo_enlace"),
											type));
								} catch (SelectionException e) {
									NotificationManager.addError("Error obteniendo los campos de la tabla",
										e);
								} catch (ReadDriverException e) {
									NotificationManager.addError("Error obteniendo los campos de la tabla",
										e);
								}
							}
						}

						public void back(WizardEvent w) {
							WizardControl wiz = w.wizard;
							wiz.enableBack(true);
							wiz.enableNext(((ObjectSelectionStep) wiz.getCurrentStep()).getSelectedItem() != null);
						}
					});
				project.setModified(true);
				PluginServices.getMDIManager().addWindow(wiz);
			} catch (SelectionException e) {
				NotificationManager.addError("Error abriendo el asistente", e);
			}
      	}
	}

	/**
	 * @see com.iver.cit.gvsig.gui.filter.ExpressionListener#newSet(java.lang.String)
	 */
	public void newSet(String expression) {
		// By Pablo: if no expression -> no element selected
		if (! this.filterExpressionFromWhereIsEmpty(expression)) {
			long[] sel = doSet(expression);

			if (sel == null) {
				throw new RuntimeException("Not a 'where' clause?");
			}

			FBitSet selection = new FBitSet();

			for (int i = 0; i < sel.length; i++) {
				selection.set((int) sel[i]);
			}

			dataSource.clearSelection();
			dataSource.setSelection(selection);
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
			NotificationManager.addError("Error cargando el driver", e);
		} catch (ReadDriverException e) {
			NotificationManager.addError("Error accediendo al driver", e);
		} catch (ParseException e) {
			NotificationManager.addError("Parse error", e);
		} catch (SemanticException e) {
			NotificationManager.addError(e.getMessage(), e);
		} catch (IOException e) {
			NotificationManager.addError("GDBMS internal error", e);
		} catch (EvaluationException e) {
			NotificationManager.addError("Error con la expresión", e);
        }

		return null;
	}

	/**
	 * @see com.iver.cit.gvsig.gui.filter.ExpressionListener#addToSet(java.lang.String)
	 */
	public void addToSet(String expression) {
		// By Pablo: if no expression -> don't add more elements to set
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
			fbs.or(selection);
			dataSource.setSelection(fbs);
		}
	}

	/**
	 * @see com.iver.cit.gvsig.gui.filter.ExpressionListener#fromSet(java.lang.String)
	 */
	public void fromSet(String expression) {
		// By Pablo: if no expression -> no element selected
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

	/**
	 * @see com.iver.mdiApp.plugins.IExtension#isVisible()
	 */
	public boolean isVisible() {
		IWindow v = PluginServices.getMDIManager().getActiveWindow();

		if (v == null) {
			return false;
		}

		if (v instanceof Table) {
			if (!((Table)v).getModel().getModelo().isEditing())
				return true;
		} /*else {
			if (v instanceof com.iver.cit.gvsig.gui.View) {
				com.iver.cit.gvsig.gui.View view = (com.iver.cit.gvsig.gui.View) v;
				ProjectView pv = view.getModel();
				FLayer[] seleccionadas = pv.getMapContext().getLayers()
										   .getActives();

				if (seleccionadas.length == 1) {
					if (seleccionadas[0] instanceof AlphanumericData) {
						return true;
					}
				}
			}
*/
			return false;
		//}
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#initialize()
	 */
	public void initialize() {
		registerIcons();
	}

	private void registerIcons(){
		PluginServices.getIconTheme().registerDefault(
				"table-join",
				this.getClass().getClassLoader().getResource("images/tablejoin.png")
			);

		PluginServices.getIconTheme().registerDefault(
				"table-link",
				this.getClass().getClassLoader().getResource("images/tablelink.png")
			);
	}

	/**
	 * @see com.iver.andami.plugins.IExtension#isEnabled()
	 */
	public boolean isEnabled() {
		return true;
	}

	public void execJoin(ProjectTable sourceProjectTable, String field1, String prefix1,
			ProjectTable targetProjectTable, String field2, String prefix2) {
		// get source table and source field
		SelectableDataSource sds=null;
		try {
			sds = sourceProjectTable.getModelo().getRecordset();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		String tableName1 = sds.getName();
		// get target table and target field
		SelectableDataSource tds=null;
		try {
			tds = targetProjectTable.getModelo().getRecordset();
		} catch (ReadDriverException e) {
			e.printStackTrace();
		}
		String tableName2 = tds.getName();
		// compute the join
		String sql =
			"custom com_iver_cit_gvsig_arcjoin tables '" +
			tableName1 + "', '" + tableName2 + "' values(" +
			field1 + ", " + field2 + ");";
		PluginServices.getLogger().debug(sql);

		try {
			SelectableDataSource result = new SelectableDataSource(LayerFactory.getDataSourceFactory()
					.executeSQL(sql,
							DataSourceFactory.AUTOMATIC_OPENING));
			EditableAdapter auxea=new EditableAdapter();
			auxea.setOriginalDataSource(result);
			String[] currentJoinedTables = sourceProjectTable.getJoinedTables();
			String[] joinedTables;
			if (currentJoinedTables!=null) {
				joinedTables = new String[currentJoinedTables.length+1];
				System.arraycopy(currentJoinedTables, 0, joinedTables, 0, currentJoinedTables.length);
			}
			else {
				joinedTables = new String[1];
			}
			joinedTables[joinedTables.length-1] = targetProjectTable.getName();
			sourceProjectTable.setJoinTable(joinedTables);
			sourceProjectTable.replaceDataSource(auxea);
			renameFields(sourceProjectTable, sds, sanitizeFieldName(prefix1),
					tds, sanitizeFieldName(prefix2));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		} catch (DriverLoadException e) {
			NotificationManager.addError(PluginServices.getText(this, "Error_loading_driver"),
					e);
		} catch (ReadDriverException e) {
			NotificationManager.addError(PluginServices.getText(this, "Error_reading_from_the_driver"),
					e);
		} catch (SemanticException e) {
			throw new RuntimeException(e);
		} catch (EvaluationException e) {
			NotificationManager.addError(PluginServices.getText(this, "Error_evaluationg_expression"),
					e);
		}
	}
//
//	/**
//	 * Renames fields in a JOIN, so that fields from TABLE1
//	 * become sourcePrefix.field1, sourcePrefix.field2, etc, and fields
//	 * from TABLE2 become targetPrefix.field1, targetPrefix.field2, etc.
//	 * @param eds
//	 * @param pt1
//	 * @param pt1
//	 */

	/**
	 * <p>Renames fields in a JOIN, so that fields from DataSource1
	 * become sourcePrefix_field1, sourcePrefix_field2, etc, and fields
	 * from TABLE2 become targetPrefix_field1, targetPrefix_field2, etc.
	 * ProjectTable aliases are used to rename fields.</p>
	 *
	 * @param targetPt The ProjectTable whose fields are to be renamed
	 * @param ds1 The first datasource in the join
	 * @param prefix1 The prefix to apply to fields in ds1
	 * @param ds2 The second datasource in the join
	 * @param prefix2 The prefix to apply to fields in ds2
	 */
	private void renameFields(ProjectTable targetPt, DataSource ds1, String prefix1,
			DataSource ds2, String prefix2) {
		try {
			SelectableDataSource sds = targetPt.getModelo().getRecordset();
//			FieldDescription[] fields = sds.getFieldsDescription();
			String[] aliases = new String[sds.getFieldCount()];
			int i=0;
			while (i<aliases.length && i<ds1.getFieldCount()){
				if (!prefix1.equals(""))
					aliases[i] = prefix1+"_"+sds.getFieldName(i);
				else
					aliases[i] = sds.getFieldName(i);
				sds.setFieldAlias(i, aliases[i]);
				i++;
			}
			while (i<aliases.length) {
				if (!prefix2.equals(""))
					aliases[i] = prefix2 + "_" + sds.getFieldName(i).substring(ArcJoinDataSource.prefix.length());
				else
					aliases[i] = sds.getFieldName(i).substring(ArcJoinDataSource.prefix.length());
				sds.setFieldAlias(i,aliases[i]);
				i++;
			}
			targetPt.setAliases(aliases);
		} catch (ReadDriverException e) {
			//just log the error, it's not bad if fields couldn't be renamed
			PluginServices.getLogger().error("Error renaming fields in a JOIN", e);
		} catch (Exception e) {
			//just log the error, it's not bad if fields couldn't be renamed
			PluginServices.getLogger().error("Error renaming fields in a JOIN", e);
		}
	}

	/**
	 * Ensure that field name only has 'safe' characters
	 * (no spaces, special characters, etc).
	 */
	public String sanitizeFieldName(String fieldName) {
		return fieldName.replaceAll("\\W", "_"); // replace any non-word character by an underscore
	}

}
