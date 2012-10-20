/*
 * Created on 10-abr-2006
 *
 * gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
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
/* CVS MESSAGES:
 *
 * $Id: 
 * $Log: 
 */
package org.gvsig.topology.ui.util;

import java.awt.BorderLayout;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import com.iver.andami.ui.mdiManager.IWindow;
import com.iver.andami.ui.mdiManager.WindowInfo;

public class HTMLPanel extends JPanel implements IWindow {
	private static final long serialVersionUID = 1490785370826172129L;

	private JScrollPane scrollPanel;
	private JEditorPane htmlPanel;
	private StringBuffer htmlText = new StringBuffer();
	private WindowInfo viewInfo;

	private String title;

	public HTMLPanel(String title, String htmlBody) {
		super();
		this.title = title;

		setLayout(new BorderLayout());
		scrollPanel = new JScrollPane();
		htmlPanel = new JEditorPane();
		htmlPanel.setEditable(false);
		htmlPanel.setEditorKit(new HTMLEditorKit());

		initialize(htmlBody);

		scrollPanel.setViewportView(htmlPanel);
		add(scrollPanel, BorderLayout.CENTER);
	}

	private void initialize(String htmlBody) {
		htmlText.append("<head>");
		htmlText.append("<style type='text/css'>");
		htmlText.append("</style>");
		htmlText.append("</head>");
		htmlText.append("<body>");

		htmlText.append(htmlBody);

		htmlText.append("</body>");

		htmlPanel.setText(htmlText.toString());

	}

	public WindowInfo getWindowInfo() {
		if (viewInfo == null) {
			viewInfo = new WindowInfo(WindowInfo.MODELESSDIALOG
					| WindowInfo.RESIZABLE | WindowInfo.MAXIMIZABLE
					| WindowInfo.ICONIFIABLE | WindowInfo.PALETTE);
			viewInfo.setTitle(title);
			viewInfo.setWidth(200);
			viewInfo.setHeight(250);
		}
		return viewInfo;
	}

	public Object getWindowProfile() {
		return WindowInfo.TOOL_PROFILE;
	}

}
