/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2005 IVER T.I. and Generalitat Valenciana.
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
package org.gvsig.symbology.gui.styling;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gvsig.gui.beans.AcceptCancelPanel;
import org.gvsig.gui.beans.swing.GridBagLayoutPanel;
import org.gvsig.gui.beans.swing.JButton;

import com.iver.andami.PluginServices;
import com.iver.andami.messages.NotificationManager;
import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;
import com.iver.cit.gvsig.fmap.core.CartographicSupport;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.styles.ILabelStyle;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ITextSymbol;
import com.iver.cit.gvsig.fmap.rendering.styling.labeling.LabelClass;
import com.iver.cit.gvsig.gui.styling.SelectorFilter;
import com.iver.cit.gvsig.gui.styling.StyleSelector;
import com.iver.cit.gvsig.gui.styling.SymbolPreviewer;
import com.iver.cit.gvsig.gui.styling.SymbolSelector;
import com.iver.cit.gvsig.gui.utils.FontChooser;
import com.iver.cit.gvsig.project.documents.view.legend.gui.ISymbolSelector;
import com.iver.utiles.swing.JComboBox;

/**
 * 
 * LabelClassRenderingProperties.java
 * 
 * 
 * @author jaume dominguez faus - jaume.dominguez@iver.es Feb 29, 2008
 * 
 */
public class LabelClassRenderingProperties extends GridBagLayoutPanel implements
		ActionListener {
	private static final long serialVersionUID = 2232555304188584038L;
	private SymbolPreviewer previewer;
	private JComboBox cmbExpressions;
	private JButton btnFont;
	private JButton btnSymbol;
	private JButton btnLabelStyles;
	private JButton btnEditExpression;
	private LabelClass lc;
	private ArrayList<String> expressions;
	private String[] fieldNames;
	private boolean noEvent = false;

	private class ExprEditorPanel extends JPanel implements IWindow {
		private static final long serialVersionUID = -3867224882591879900L;
		private ActionListener action = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if ("OK".equals(e.getActionCommand())) {
					if (validateExpr())
						PluginServices.getMDIManager().closeWindow(
								ExprEditorPanel.this);
					else
						JOptionPane.showMessageDialog(ExprEditorPanel.this,
								PluginServices.getText(this, "syntax_error"),
								PluginServices.getText(this, "error"),
								JOptionPane.ERROR_MESSAGE);
				} else {
					getTxtExpression().setText(lastExtression);
					PluginServices.getMDIManager().closeWindow(
							ExprEditorPanel.this);
				}
			}
		};
		private JTextField txtExpression;
		private String lastExtression;

		public ExprEditorPanel(String currentExpression) {
			super();
			initialize();
			lastExtression = currentExpression;
			getTxtExpression().setText(currentExpression);

		}

		private void initialize() {
			GridBagLayoutPanel content = new GridBagLayoutPanel();
			content.addComponent(new JLabel(" "
					+ PluginServices.getText(this, "label_expression_help")));
			content.addComponent(
					" " + PluginServices.getText(this, "expression") + ":",
					getTxtExpression());
			setLayout(new BorderLayout(5, 5));

			add(content, BorderLayout.CENTER);
			add(new AcceptCancelPanel(action, action), BorderLayout.SOUTH);
			setSize(new Dimension(300, 80));
		}

		private JTextField getTxtExpression() {
			if (txtExpression == null) {
				txtExpression = new JTextField(25);
			}
			return txtExpression;
		}

		public String getExpression() {
			return getTxtExpression().getText();
		}

		public WindowInfo getWindowInfo() {
			WindowInfo viewInfo = new WindowInfo(WindowInfo.MODALDIALOG);
			viewInfo.setWidth(getWidth());
			viewInfo.setHeight(getHeight());
			viewInfo.setTitle(PluginServices.getText(this,
					"label_expression_editor"));
			return viewInfo;
		}

		public Object getWindowProfile() {
			return WindowInfo.DIALOG_PROFILE;
		}

		private boolean validateExpr() {
			// TODO : implement it
			return true;
		}
	}

	public LabelClassRenderingProperties() {
		initialize();
	}

	private void initialize() {
		JPanel aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
		aux.setBorder(BorderFactory.createTitledBorder(null,
				PluginServices.getText(this, "text_string")));
		aux.add(new JLabel(PluginServices.getText(this, "expression") + ":"));
		aux.add(getCmbExpressions());
		aux.add(getBtnEditExpression());
		aux.setPreferredSize(new Dimension(612, 60));
		addComponent(aux);

		aux = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
		aux.setBorder(BorderFactory.createTitledBorder(null,
				PluginServices.getText(this, "text_symbol")));
		aux.add(getSymbolPreviewer());

		JPanel aux2 = new JPanel();
		aux2.add(getBtnFont());
		aux2.add(getBtnSymbol());
		aux2.add(getBtnLabelStyles());

		aux2.setLayout(new GridLayout(aux2.getComponentCount(), 1));
		aux.add(aux2);
		aux.setPreferredSize(new Dimension(612, 100));
		addComponent(aux);
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	private void refreshCmbExpressions() {

		ArrayList<String> exp = getExpressions();

		String expr = lc.getStringLabelExpression();

		noEvent = true;
		if (lc != null && expr != null && !expr.equals("")
				&& !exp.contains(expr)) {
			exp.add(0, expr);
		}

		getCmbExpressions().removeAllItems();
		for (int i = 0; i < exp.size(); i++) {
			getCmbExpressions().addItem(exp.get(i));
		}
		if (lc != null) {
			getCmbExpressions().setSelectedItem(lc.getLabelExpressions());
		} else {
			getCmbExpressions().setSelectedItem(exp.get(0));
		}
		noEvent = false;
	}

	private JButton getBtnEditExpression() {
		if (btnEditExpression == null) {
			btnEditExpression = new JButton(PluginServices.getText(this,
					"edit_expression") + "...");
			btnEditExpression.setName("BTNEDITEXPRESSION");
			btnEditExpression.addActionListener(this);
		}
		return btnEditExpression;
	}

	private JButton getBtnSymbol() {
		if (btnSymbol == null) {
			btnSymbol = new JButton(PluginServices.getText(this, "symbol"));
			btnSymbol.setName("BTNSYMBOL");
			btnSymbol.addActionListener(this);
		}

		return btnSymbol;
	}

	private ArrayList<String> getExpressions() {
		if (expressions == null) {
			expressions = new ArrayList<String>();
			try {
				for (int i = 0; i < fieldNames.length; i++) {
					expressions.add("[" + fieldNames[i] + "];");
				}
			} catch (Exception e) {
				NotificationManager.addError(PluginServices.getText(this,
						"could_not_retreive_layer_field_names"), e);
			}
		}
		return expressions;
	}

	private JComboBox getCmbExpressions() {
		if (cmbExpressions == null) {
			cmbExpressions = new JComboBox();
			cmbExpressions.setPreferredSize(new Dimension(150, 20));
			cmbExpressions.setName("CMBEXPRESSIONS");
			cmbExpressions.addActionListener(this);
		}
		return cmbExpressions;

	}

	private JButton getBtnFont() {
		if (btnFont == null) {
			btnFont = new JButton(PluginServices.getText(this, "fuente")
					+ "...");
			btnFont.setName("BTNFONT");
			btnFont.addActionListener(this);
		}
		return btnFont;
	}

	private SymbolPreviewer getSymbolPreviewer() {
		if (previewer == null) {
			previewer = new SymbolPreviewer();
			previewer.setPreferredSize(new Dimension(420, 65));
		}
		return previewer;
	}

	public void setModel(LabelClass lc) {
		this.lc = lc;
		refreshCmbExpressions();
		refreshTextSymbolPreviewer();
	}

	public void actionPerformed(ActionEvent e) {
		if (noEvent)
			return;
		Component c = (Component) e.getSource();
		if (c.equals(btnEditExpression)) {
			ExprEditorPanel ep = new ExprEditorPanel(
					(String) getCmbExpressions().getSelectedItem());
			PluginServices.getMDIManager().addWindow(ep);
			getCmbExpressions().addItem(ep.getExpression());
			getCmbExpressions().setSelectedItem(ep.getExpression());
			// lc.setLabelExpression(ep.getExpression());
		} else if (c.equals(btnFont)) {
			Font labelFont = lc.getTextSymbol().getFont();
			Font newFont = FontChooser.showDialog(
					PluginServices.getText(this, "font"), labelFont);
			if (newFont != null) {
				lc.getTextSymbol().setFont(newFont);
			}
			refreshTextSymbolPreviewer();
		} else if (c.equals(btnLabelStyles)) {
			// here open symbol selector

			StyleSelector stySel = new StyleSelector(lc.getLabelStyle(),
					FShape.TEXT);
			stySel.setUnit(lc.getUnit());
			stySel.setReferenceSystem(lc.getReferenceSystem());
			PluginServices.getMDIManager().addWindow(stySel);
			ILabelStyle sty = (ILabelStyle) stySel.getSelectedObject();
			if (sty != null) {
				// gather the style and apply to the class
				lc.setLabelStyle(sty);
				lc.setUnit(stySel.getUnit());
				lc.setReferenceSystem(stySel.getReferenceSystem());

			}
		} else if (c.equals(cmbExpressions)) {
			// lc.setLabelExpression((String) cmbExpressions.getSelectedItem());
		}
		if (c.equals(btnSymbol)) {

			// here open symbol selector

			ISymbolSelector symSel = SymbolSelector.createSymbolSelector(
					lc.getTextSymbol(), FShape.TEXT, new SelectorFilter() {
						public boolean accepts(Object obj) {
							if (obj instanceof ISymbol) {
								ISymbol sym = (ISymbol) obj;
								return sym.getSymbolType() == FShape.TEXT;
							}
							return false;
						}
					});

			PluginServices.getMDIManager().addWindow(symSel);
			ISymbol sym = (ISymbol) symSel.getSelectedObject();
			if (sym != null) {
				// gather the symbol and apply to the class
				if (sym instanceof CartographicSupport) {
					CartographicSupport csSty = (CartographicSupport) sym;
					lc.setUnit(csSty.getUnit());
					lc.setReferenceSystem(csSty.getReferenceSystem());
				}
				lc.setTextSymbol((ITextSymbol) sym);

			}

			refreshTextSymbolPreviewer();
		}
	}

	private JButton getBtnLabelStyles() {
		if (btnLabelStyles == null) {
			btnLabelStyles = new JButton(PluginServices.getText(this,
					"label_styles") + "...");
			btnLabelStyles.setName("BTNLABELSTYLES");
			btnLabelStyles.addActionListener(this);
		}
		return btnLabelStyles;
	}

	private void refreshTextSymbolPreviewer() {
		ITextSymbol textSym = lc.getTextSymbol();
		textSym.setText(PluginServices.getText(this,
				"GeneralLabeling.sample_text"));
		getSymbolPreviewer().setSymbol(textSym);

	}

}
