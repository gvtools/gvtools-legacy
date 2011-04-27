/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2007 IVER T.I. and Generalitat Valenciana.
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
 */
package org.gvsig.rastertools.properties.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;

import org.gvsig.fmap.raster.layers.FLyrRasterSE;
import org.gvsig.gui.beans.panelGroup.AbstractPanelGroup;
import org.gvsig.gui.beans.slidertext.listeners.SliderEvent;
import org.gvsig.gui.beans.slidertext.listeners.SliderListener;
import org.gvsig.raster.IProcessActions;
import org.gvsig.raster.dataset.FileNotOpenException;
import org.gvsig.raster.dataset.io.RasterDriverException;
import org.gvsig.raster.dataset.properties.DatasetListStatistics;
import org.gvsig.raster.grid.filter.FilterAddException;
import org.gvsig.raster.grid.filter.FilterTypeException;
import org.gvsig.raster.grid.filter.RasterFilter;
import org.gvsig.raster.grid.filter.RasterFilterList;
import org.gvsig.raster.grid.filter.RasterFilterListManager;
import org.gvsig.raster.grid.filter.enhancement.BrightnessContrastListManager;
import org.gvsig.raster.grid.filter.enhancement.BrightnessFilter;
import org.gvsig.raster.grid.filter.enhancement.ContrastFilter;
import org.gvsig.raster.grid.filter.enhancement.EnhancementStretchListManager;
import org.gvsig.raster.grid.filter.enhancement.LinearStretchEnhancementFilter;
import org.gvsig.raster.grid.filter.enhancement.LinearStretchParams;
import org.gvsig.raster.grid.filter.statistics.StatisticsListManager;
import org.gvsig.raster.grid.filter.statistics.TailTrimFilter;
import org.gvsig.raster.hierarchy.IRasterDataset;
import org.gvsig.raster.hierarchy.IRasterRendering;
import org.gvsig.raster.hierarchy.IStatistics;
import org.gvsig.raster.util.RasterToolsUtil;
import org.gvsig.rastertools.RasterModule;
import org.gvsig.rastertools.properties.RasterPropertiesTocMenuEntry;
import org.gvsig.rastertools.properties.panels.EnhancedBrightnessContrastPanel;
import org.gvsig.rastertools.properties.panels.EnhancedPanel;
import org.gvsig.rastertools.properties.panels.EnhancedWithTrimPanel;
import org.gvsig.rastertools.statistics.StatisticsProcess;

import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.layers.FLayer;

/**
 * Clase que hace de interfaz entre los objetos que contienen la información de
 * realce y el panel.
 *
 * @author Nacho Brodin (nachobrodin@gmail.com)
 */
public class EnhancedControl implements IProcessActions {
	private EnhancedPanel                   tPanel      = null;
	private RasterFilterList                filterList  = null;
	private EnhancedWithTrimPanel           ePanel      = null;
	private EnhancedBrightnessContrastPanel bcPanel     = null;
	private IRasterDataset                  dataset     = null;
	private FLyrRasterSE                    lyr         = null;
	private AbstractPanelGroup              panelGroup  = null;
	private IStatistics                     stats       = null;
	private int[]                           renderBands = new int[] { 0, 1, 2 };
	private boolean                         rgb         = true;

	/**
	 * Manejador de eventos de los slider de brillo y contraste.
	 * @author Nacho Brodin (nachobrodin@gmail.com)
	 */
	class BrightnessContrastListener implements ActionListener, SliderListener {
		JCheckBox active = null;
		/**
		 * Constructor. Registra los listener
		 * @param panel
		 */
		public BrightnessContrastListener(EnhancedBrightnessContrastPanel panel) {
			panel.addBrightnessValueChangedListener(this);
			panel.addContrastValueChangedListener(this);
			active = panel.getActive();
			active.addActionListener(this);
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (!RasterModule.autoRefreshView)
				return;

			if (e.getSource() == active)
				onlyApply();
		}

		/*
		 * (non-Javadoc)
		 * @see org.gvsig.gui.beans.slidertext.listeners.SliderListener#actionValueChanged(org.gvsig.gui.beans.slidertext.listeners.SliderEvent)
		 */
		public void actionValueChanged(SliderEvent e) {
			if (!RasterModule.autoRefreshView)
				return;

			onlyApply();
		}

		/*
		 * (non-Javadoc)
		 * @see org.gvsig.gui.beans.slidertext.listeners.SliderListener#actionValueDragged(org.gvsig.gui.beans.slidertext.listeners.SliderEvent)
		 */
		public void actionValueDragged(SliderEvent e) {
		}
	}

	/**
	 * Manejador de eventos del panel EnhancedWithTrim.
	 *
	 * @version 14/06/2007
	 * @author BorSanZa - Borja Sánchez Zamorano (borja.sanchez@iver.es)
	 */
	class EnhancedWithTrimListener implements ActionListener, SliderListener {
		JCheckBox active = null;
		/**
		 * Constructor. Registra los listener
		 * @param panel
		 */
		public EnhancedWithTrimListener(EnhancedWithTrimPanel panel) {
			active = panel.getActive();
			active.addActionListener(this);
			panel.getRemoveCheck().addActionListener(this);
			panel.getTrimCheck().addActionListener(this);
			panel.getTrimSlider().addValueChangedListener(this);
		}

		/*
		 * (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (!RasterModule.autoRefreshView)
				return;

			onlyApply();
		}

		/*
		 * (non-Javadoc)
		 * @see org.gvsig.gui.beans.slidertext.listeners.SliderListener#actionValueChanged(org.gvsig.gui.beans.slidertext.listeners.SliderEvent)
		 */
		public void actionValueChanged(SliderEvent e) {
			if (!RasterModule.autoRefreshView)
				return;

			onlyApply();
		}

		/*
		 * (non-Javadoc)
		 * @see org.gvsig.gui.beans.slidertext.listeners.SliderListener#actionValueDragged(org.gvsig.gui.beans.slidertext.listeners.SliderEvent)
		 */
		public void actionValueDragged(SliderEvent e) {
		}
	}

	/**
	 * Constructor
	 * @param tp
	 */
	public EnhancedControl(AbstractPanelGroup panelGroup, EnhancedPanel tp, IRasterDataset dataset, FLayer lyr, RasterFilterList rfl) {
		this.panelGroup = panelGroup;
		this.tPanel = tp;
		this.dataset = dataset;
		this.filterList = rfl;
		if(lyr != null && lyr instanceof FLyrRasterSE)
			this.lyr = (FLyrRasterSE)lyr;
		rgb = this.lyr.isRGB();
		this.bcPanel = tPanel.getBrightnessContrastPanel();
		this.ePanel = tPanel.getEnhancedWithTrimPanel();
		new BrightnessContrastListener(bcPanel);
		new EnhancedWithTrimListener(ePanel);

		saveStatus();

		setValuesFromFilterToPanel();
	}

	/**
	 * Carga los valores del panel desde el filtro
	 */
	private void setValuesFromFilterToPanel() {
		// BRILLO
		BrightnessFilter bFilter = (BrightnessFilter) filterList.getByName(BrightnessFilter.names[0]);
		if (bFilter != null){
			int incr = ((Integer)bFilter.getParam("incrBrillo")).intValue();
			bcPanel.setBrightnessValue((double) incr);
		}else
			bcPanel.setBrightnessValue(0);

		// CONTRASTE
		ContrastFilter cFilter = (ContrastFilter) filterList.getByName(ContrastFilter.names[0]);
		if (cFilter != null){
			int incr = ((Integer)cFilter.getParam("incrContraste")).intValue();
			bcPanel.setContrastValue((double) incr);
		}
		else
			bcPanel.setContrastValue(0);

		if (bFilter != null || cFilter != null)
			bcPanel.setControlEnabled(true);
		else
			bcPanel.setControlEnabled(false);

		// REALCE LINEAL
		LinearStretchEnhancementFilter eFilter = (LinearStretchEnhancementFilter) filterList.getByName(LinearStretchEnhancementFilter.names[0]);
		if (eFilter != null) {
			ePanel.setControlEnabled(true);

			// Comprueba si esta activo eliminar extremos
			boolean removeEnds = false;
			if (eFilter.getParam("remove") != null)
				removeEnds = ((Boolean) eFilter.getParam("remove")).booleanValue();
			ePanel.setRemoveEndsActive(removeEnds);
			
			// Comprueba si hay recorte de colas
			LinearStretchParams stretchs = (LinearStretchParams) eFilter.getParam("stretchs");
			double[] tailTrimList;
			if (stretchs != null)
				tailTrimList = stretchs.getTailTrimList();
			else
				tailTrimList = new double[0];
			double median = 0;
			double nValues = tailTrimList.length;
			for (int i = 0; i < tailTrimList.length; i++) 
				median += tailTrimList[i];
			double tailTrim = new Double(nValues > 0 ? median / nValues : median).doubleValue();
			
			if (tailTrim != 0) {
				ePanel.setTailTrimCheckActive(true);
				ePanel.setTailTrimValue(tailTrim * 100);
			} else {
				ePanel.setTailTrimCheckActive(false);
				ePanel.setTailTrimValue(0);
			}
		} else {
			ePanel.setControlEnabled(false);
			ePanel.setRemoveEndsActive(false);
			ePanel.setTailTrimCheckActive(false);
			ePanel.setTailTrimValue(0);
		}
	}

	/**
	 * Carga los valores del filtro desde el panel
	 * @throws FilterTypeException
	 * @throws FilterAddException 
	 */
	private void setValuesFromPanelToFilter() throws FilterTypeException, FilterAddException {
		RasterFilterListManager manager = new RasterFilterListManager(filterList);

		// REALCE
		EnhancementStretchListManager eManager = (EnhancementStretchListManager) manager.getManagerByClass(EnhancementStretchListManager.class);
		if (ePanel.getActive().isSelected()) {
			if(dataset != null && dataset.getDataSource() != null) {
				stats = dataset.getDataSource().getStatistics();
				if (lyr instanceof IRasterRendering)
					renderBands = ((IRasterRendering) lyr).getRender().getRenderBands();
				// En este caso siempre es necesario el máximo y mínimo
				try {
					if(!stats.isCalculated() && lyr instanceof FLyrRasterSE) 
						StatisticsProcess.launcher((FLyrRasterSE)lyr, this);
					else {
						if (ePanel.isTailTrimCheckSelected()) {
							LinearStretchParams leParams = LinearStretchParams.createStandardParam(((FLyrRasterSE) lyr).getRenderBands(), (double) (ePanel.getTrimValue() / 100D), stats, rgb);
							eManager.addEnhancedStretchFilter(leParams, stats, renderBands, ePanel.isRemoveEndsSelected());
						} else {
							filterList.remove(TailTrimFilter.class);
							LinearStretchParams leParams = LinearStretchParams.createStandardParam(((FLyrRasterSE) lyr).getRenderBands(), 0, stats, rgb);
							eManager.addEnhancedStretchFilter(leParams, stats, renderBands, ePanel.isRemoveEndsSelected());
						}
					}
				} catch (FileNotOpenException e) {
					throw new FilterAddException("No se han podido calcular estadisticas. Error al añadir realce;" + e.getMessage());
				} catch (RasterDriverException e) {
					throw new FilterAddException("No se han podido calcular estadisticas. Error al añadir realce; " + e.getMessage());
				}
			}
		} else {
			filterList.remove(LinearStretchEnhancementFilter.class);
			filterList.remove(TailTrimFilter.class);
		}

		// BRILLO Y CONTRASTE
		BrightnessContrastListManager bcManager = (BrightnessContrastListManager) manager.getManagerByClass(BrightnessContrastListManager.class);

		if (bcPanel.getActive().isSelected() && ((int) bcPanel.getBrightnessValue() != 0))
			bcManager.addBrightnessFilter((int) bcPanel.getBrightnessValue());
		else
			filterList.remove(BrightnessFilter.class);

		if (bcPanel.getActive().isSelected() && ((int) bcPanel.getContrastValue() != 0))
			bcManager.addContrastFilter((int) bcPanel.getContrastValue());
		else
			filterList.remove(ContrastFilter.class);

		endActionsForFilterSettings();
	}
	
	/**
	 * Acciones realizadas al final la aplicación de filtros
	 * @throws FilterTypeException
	 */
	@SuppressWarnings("unchecked")
	private void endActionsForFilterSettings() throws FilterTypeException {
		ArrayList listOrder = (ArrayList) panelGroup.getProperties().get("filterOrder");
		ArrayList listCopy = filterList.getStatusCloned();
		int cont = 0;
		for (int i = 0; i < listOrder.size(); i++) {
			int pos = hasFilter(listCopy, ((RasterFilter) listOrder.get(i)).getName());
			if (pos != -1) {
				// Esta pero en posicion equivocada
				if (pos != cont) {
					Object copy = listCopy.remove(pos);
					listCopy.add(cont, copy);
				}
				cont++;
			}
		}
		filterList.setStatus(listCopy);
		filterList.controlTypes();

		// Redibujamos
		if (lyr != null)
			lyr.getMapContext().invalidate();
	}

	/**
	 * Acciones a ejecutar cuando se acepta
	 */
	public void accept() {
		try {
			setValuesFromPanelToFilter();
		} catch (FilterTypeException e) {
			RasterToolsUtil.messageBoxError(PluginServices.getText(this, "error_adding_filters"), this, e);
		} catch (FilterAddException e) {
			RasterToolsUtil.messageBoxError(PluginServices.getText(this, "error_adding_filters"), this, e);
		}
	}

	/**
	 * Acciones a ejecutar cuando se aplica
	 */
	public void apply() {
		onlyApply();
		saveStatus();
	}

	/**
	 * Acciones a ejecutar cuando se aplica
	 */
	public void onlyApply() {
		if (RasterPropertiesTocMenuEntry.enableEvents)
			try {
				setValuesFromPanelToFilter();
			} catch (FilterTypeException e) {
				RasterToolsUtil.messageBoxError(PluginServices.getText(this, "error_adding_filters"), this, e);
			} catch (FilterAddException e) {
				RasterToolsUtil.messageBoxError(PluginServices.getText(this, "error_adding_filters"), this, e);
			}
	}

	/**
	 * Acciones a ejecutar cuando se cancela
	 */
	public void cancel() {
		restoreStatus();
	}

	/**
	 * Consulta si el filtro especificado en el parámetro name está dentro
	 * de la lista filter.
	 * @param filter Lista donde se consulta
	 * @param name Nombre a comprobar si está en la lista
	 * @return true si está en la lista y false si no está.
	 */
	@SuppressWarnings("unchecked")
	private int hasFilter(ArrayList filter, String name) {
		for (int i = 0; i < filter.size(); i++) {
			if (((RasterFilter) filter.get(i)).getName().equals(name))
				return i;
		}
		return -1;
	}

	@SuppressWarnings("unchecked")
	public void saveStatus() {
		panelGroup.getProperties().put("filterStatus", filterList.getStatusCloned());

		ArrayList filterOrder = filterList.getStatusCloned();
		int posEnhanced = hasFilter(filterOrder, "enhanced_stretch");
		int posTailTrim = hasFilter(filterOrder, "tailTrim");
		// Si tiene realce comprobamos el tailtrim
		if (posEnhanced != -1) {
			// Si no tiene el tailTrim, insertamos uno antes del realce
			if (posTailTrim == -1) {
				RasterFilter aux = StatisticsListManager.createTailFilter(0, 0, false, null);
				filterOrder.add(posEnhanced, aux);
			}
		} else {
			// Si existe un tailTrim lo borramos pq no tiene realce
			if (posTailTrim != -1)
				filterOrder.remove(posTailTrim);
			// Insertamos primero el tailtrim y luego el realce para conservar un orden logico
			filterOrder.add(0, StatisticsListManager.createTailFilter(0, 0, false, null));
			filterOrder.add(1, EnhancementStretchListManager.createEnhancedFilter(null, null, null, false));
		}

		// Si no tiene brillo, lo insertamos
		if (hasFilter(filterOrder, "brightness") == -1)
			filterOrder.add(BrightnessContrastListManager.createBrightnessFilter(0));

		// Si no tiene el contraste, lo insertamos
		if (hasFilter(filterOrder, "contrast") == -1)
			filterOrder.add(BrightnessContrastListManager.createContrastFilter(0));

		panelGroup.getProperties().put("filterOrder", filterOrder);
	}

	@SuppressWarnings("unchecked")
	public void restoreStatus() {
		filterList.setStatus((ArrayList) panelGroup.getProperties().get("filterStatus"));

		if (lyr != null)
			lyr.getMapContext().invalidate();
	}

	/*
	 * (non-Javadoc)
	 * @see org.gvsig.raster.IProcessActions#end(java.lang.Object)
	 */
	public void end(Object param) {
		DatasetListStatistics st = null;
		if(param instanceof FLyrRasterSE && ((FLyrRasterSE)param).getDataSource() != null) 
			st = ((FLyrRasterSE)param).getDataSource().getStatistics();
		IStatistics statistics = (st == null) ? stats : st;
		
		try {		
			RasterFilterListManager manager = new RasterFilterListManager(filterList);
			EnhancementStretchListManager eManager = (EnhancementStretchListManager) manager.getManagerByClass(EnhancementStretchListManager.class);
			if (ePanel.isTailTrimCheckSelected()) {
				LinearStretchParams leParams = LinearStretchParams.createStandardParam(((FLyrRasterSE) lyr).getRenderBands(), (double) (ePanel.getTrimValue() / 100D), statistics, rgb);
				eManager.addEnhancedStretchFilter(leParams, statistics, renderBands, ePanel.isRemoveEndsSelected());
			} else {
				filterList.remove(TailTrimFilter.class);
				LinearStretchParams leParams = LinearStretchParams.createStandardParam(((FLyrRasterSE) lyr).getRenderBands(), 0, statistics, rgb);
				eManager.addEnhancedStretchFilter(leParams, statistics, renderBands, ePanel.isRemoveEndsSelected());
			}
			endActionsForFilterSettings();
		} catch (FileNotOpenException e) {
			RasterToolsUtil.messageBoxError(PluginServices.getText(this, "error_adding_stats"), this, e);
		} catch (RasterDriverException e) {
			RasterToolsUtil.messageBoxError(PluginServices.getText(this, "error_adding_stats"), this, e);
		} catch (FilterTypeException e) {
			RasterToolsUtil.messageBoxError(PluginServices.getText(this, "error_adding_stats"), this, e);
		}
	}

	public void interrupted() {
	}
}