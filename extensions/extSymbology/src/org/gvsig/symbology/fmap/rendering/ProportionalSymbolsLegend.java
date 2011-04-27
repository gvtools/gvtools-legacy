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
package org.gvsig.symbology.fmap.rendering;

import org.apache.log4j.Logger;
import org.gvsig.symbology.fmap.styles.SimpleMarkerFillPropertiesStyle;
import org.gvsig.symbology.fmap.symbols.MarkerFillSymbol;

import com.hardcode.gdbms.driver.exceptions.ReadDriverException;
import com.hardcode.gdbms.engine.instruction.IncompatibleTypesException;
import com.hardcode.gdbms.engine.values.Value;
import com.hardcode.gdbms.engine.values.ValueFactory;
import com.iver.andami.PluginServices;
import com.iver.cit.gvsig.fmap.Messages;
import com.iver.cit.gvsig.fmap.core.FShape;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ILineSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMarkerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.IMultiLayerSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.MultiLayerFillSymbol;
import com.iver.cit.gvsig.fmap.layers.LegendChangedEvent;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.IntervalLegendEvent;
import com.iver.cit.gvsig.fmap.rendering.LabelLegendEvent;
import com.iver.cit.gvsig.fmap.rendering.LegendClearEvent;
import com.iver.cit.gvsig.fmap.rendering.SymbolLegendEvent;
import com.iver.cit.gvsig.fmap.rendering.ValueLegendEvent;
import com.iver.cit.gvsig.fmap.rendering.VectorialUniqueValueLegend;
import com.iver.cit.gvsig.fmap.rendering.ZSort;
import com.iver.utiles.XMLEntity;


/**
 * Implements a legend which represents the quantitative information (numeric values). This
 * representation is possible thanks to a symbol whose size is different each time (depending on
 * the numeric values and if we want to use normalization or not).
 *
 * @author jaume dominguez faus - jaume.dominguez@iver.es
 */
public class ProportionalSymbolsLegend extends VectorialUniqueValueLegend  {

	@Override
	public void setClassifyingFieldNames(String[] fNames) {
		super.setClassifyingFieldNames(fNames);
		valueField = fNames[0];
		normalizationField = fNames[1];
	}

	private ISymbol backgroundSymbol;
	private String valueField;
	private String normalizationField;
	private double minSize;
	private double maxSize;
	private int templateShapeType;
	private boolean useNormalization;
	private double maxFeature, minFeature;
	private ZSort zSort = null;


	public ISymbol getSymbolByValue(Value key) {
		MultiLayerFillSymbol sym = (MultiLayerFillSymbol) getDefaultSymbol();
		return sym;
	}

	public ISymbol getSymbolByFeature(IFeature feat) {
		ISymbol theSymbol = getDefaultSymbol();
		ISymbol auxSymbol;
		Value value = ValueFactory.createValue(0);
		Value normValue = ValueFactory.createValue(0);
		double size;

		double separation = maxSize-minSize;
		if(separation == 0)
			separation = 1;

		try {

			value= feat.getAttribute(0);
			if(useNormalization) {
				normValue = feat.getAttribute(1);
				value= feat.getAttribute(0).producto(normValue.inversa());
				size = minSize + ((Double.valueOf(value.toString()) * separation)) ;
			}
			else {
				double difFeat = maxFeature - minFeature;
				double step = difFeat/separation;
				size = minSize + ((Double.valueOf(feat.getAttribute(0).toString()) - minFeature)/step);
			}

		} catch (IncompatibleTypesException e) {
			Logger.getLogger(getClass()).error(Messages.getString("incompatible_types_to_calculate_the_normalization"));
			return null;
		}

		if(size == Double.NaN) {
			Logger.getLogger(getClass()).error(Messages.getString("the size for the symbol is equal to NaN "));
			return null;
		}

		if ((feat.getGeometry().getGeometryType()% FShape.Z) == FShape.POLYGON && theSymbol instanceof IMarkerSymbol) {
			MarkerFillSymbol aux = new MarkerFillSymbol();
			((IMarkerSymbol) theSymbol).setSize(size);
			aux.setMarker((IMarkerSymbol) theSymbol);
			SimpleMarkerFillPropertiesStyle p = new SimpleMarkerFillPropertiesStyle();
			p.setFillStyle(SimpleMarkerFillPropertiesStyle.SINGLE_CENTERED_SYMBOL);
			aux.setMarkerFillProperties(p);
			theSymbol = aux;
		}
		else if ((feat.getGeometry().getGeometryType()%FShape.Z) == FShape.LINE) {
			ILineSymbol line = (ILineSymbol)theSymbol;
			line.setLineWidth(size);
			theSymbol = line;
		}
		else if ((feat.getGeometry().getGeometryType()%FShape.Z) == FShape.POINT) {
			IMarkerSymbol marker = (IMarkerSymbol) theSymbol;
			marker.setSize(size);
			theSymbol = marker;
		}
		if (backgroundSymbol != null) {
			MultiLayerFillSymbol multi = new MultiLayerFillSymbol() ;
			multi.addLayer(backgroundSymbol);
			multi.addLayer(theSymbol);
			return multi;
		}
		auxSymbol = theSymbol;

		return auxSymbol;

	}

	public XMLEntity getXMLEntity() {
//		XMLEntity xml = super.getXMLEntity();
		XMLEntity xml = new XMLEntity();

		xml.putProperty("className", this.getClass().getName());
		xml.putProperty("valueField", getValueField());
		xml.putProperty("normalizationField", getNormalizationField());
		xml.putProperty("templateShapeType", getTemplateShapeType());
		xml.putProperty("minSize", getMinSize());
		xml.putProperty("maxSize", getMaxSize());
		xml.putProperty("useNormalization", getUseNormalization());

		xml.putProperty("maxFeature", getMaxFeature() );
		xml.putProperty("minFeature", getMinFeature() );

		xml.putProperty("useNormalization", getUseNormalization());

		if (getDefaultSymbol() != null) {
			XMLEntity defaultXML = getDefaultSymbol().getXMLEntity();
			defaultXML.putProperty("id", "defaultSymbol");
			xml.addChild(defaultXML);
		}
		if (backgroundSymbol != null) {
			XMLEntity backgroundXML = backgroundSymbol.getXMLEntity();
			backgroundXML.putProperty("idB", "backgroundSymbol");
			xml.addChild(backgroundXML);
		}

		return xml;
	}

	public void setXMLEntity(XMLEntity xml) {

//		super.setXMLEntity(xml);

		valueField = xml.getStringProperty("valueField");
		normalizationField = xml.getStringProperty("normalizationField");
		templateShapeType = xml.getIntProperty("templateShapeType");
		maxSize = xml.getDoubleProperty("maxSize");
		minSize = xml.getDoubleProperty("minSize");
		maxFeature = xml.getDoubleProperty("maxFeature");
		minFeature = xml.getDoubleProperty("minFeature");
		useNormalization = xml.getBooleanProperty("useNormalization");

		if (xml.firstChild("id", "defaultSymbol") != null) {
			XMLEntity defaultXML = xml.firstChild("id", "defaultSymbol");
			setDefaultSymbol(SymbologyFactory.createSymbolFromXML(defaultXML, "defaultSymbol"));
		}

		if (xml.firstChild("idB", "backgroundSymbol") != null) {
			XMLEntity backgroundXML = xml.firstChild("idB", "backgroundSymbol");
			backgroundSymbol = SymbologyFactory.createSymbolFromXML(backgroundXML, "backgroundSymbol");
		}

		String[] fieldNames = new String[2];
		fieldNames[0]= valueField;
		if(normalizationField.compareTo(PluginServices.getText(this, "none")) == 0)
			fieldNames[1]= fieldNames[0];
		else fieldNames[1] = normalizationField;

		setClassifyingFieldNames(fieldNames);

	}
	/**
	 * Gets the background symbol which only can appear when the shapetype of the layer
	 * is polygonal
	 *
	 * @return ISymbol the symbol for the background
	 */
	public ISymbol getBackgroundSymbol() {return backgroundSymbol;}
	/**
	 * Sets the background symbol which is used only when the shapetype of the layer is polygonal
	 *
	 * @param backgroundSymbol the symbol for the background
	 */
	public void setBackgroundSymbol(ISymbol backgroundSymbol) {this.backgroundSymbol = backgroundSymbol;}
	/**
	 * Obtains the classifying field name to be used to calculate the size of the symbol
	 *
	 * @return String  the name of the field
	 * @throws ReadDriverException 
	 */
	public String getValueField() {
//		try {
//			// TODO:
//			// Por los alias, al guardar un proyecto no podemos
//			// permitir que se guarde con campos que luego
//			// no van a existir.
////			int id = dataSource.getFieldIndexByName(valueField);
////			valueField = dataSource.getFieldName(id);
//		} catch (ReadDriverException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
		return valueField;
	}
	/**
	 * Sets the classifying field name to be used to calculate the size of the symbol
	 *
	 * @param String  the name of the field
	 */
	public void setValueField(String valueField) {this.valueField = valueField;}
	/**
	 * Obtains the classifying field name to be used to calculate the size of the symbol when the
	 * user is doing it with a normalization value.
	 *
	 * @return String  the name of the field
	 */
	public String getNormalizationField() {
		return normalizationField;
	}
	/**
	 * Sets the classifying field name to be used to calculate the size of the symbol when the
	 * user is doing it with a normalization value.
	 *
	 * @param String  the name of the field
	 */
	public void setNormalizationField(String normalizationField) {this.normalizationField = normalizationField;}
	/**
	 * Obtains the minimum size for the symbol
	 *
	 * @return double  the minimum size for the symbol
	 */
	public double getMinSize() {return minSize;}
	/**
	 * Sets the minimum size for the symbol
	 *
	 * @param minSize  the minimum size for the symbol
	 */
	public void setMinSize(double minSize) {this.minSize = minSize;}
	/**
	 * Obtains the maximum size for the symbol
	 *
	 * @return double  the minimum size for the symbol
	 */
	public double getMaxSize() {return maxSize;}
	/**
	 * Sets the maximum size for the symbol
	 *
	 * @param maxSize  the minimum size for the symbol
	 */
	public void setMaxSize(double maxSize) {this.maxSize = maxSize;}
	/**
	 * Obtains the shapetype of the template symbol
	 *
	 * @return int shape type for the template symbol
	 */
	public int getTemplateShapeType() {return templateShapeType;}
	/**
	 * Sets the shapetype of the template symbol
	 *
	 * @param templateShapeType shape type for the template symbol
	 */
	public void setTemplateShapeType(int templateShapeType) {
		if((getShapeType()%FShape.Z) == FShape.POLYGON ) {
			if((templateShapeType % FShape.Z) == FShape.POINT)
				this.templateShapeType = templateShapeType;
		}
		else if((templateShapeType % FShape.Z) == (getShapeType()%FShape.Z) || getShapeType() == FShape.NULL) {
			this.templateShapeType = templateShapeType;
		}
	}
	/**
	 * Obtains the boolean which is true if the user wants to calculate the size of the
	 * symbol using a normalization field.
	 *
	 * @return boolean true if the user wants normalization.Otherwise, false.
	 */
	public boolean getUseNormalization() {return useNormalization;}
	/**
	 * Sets the boolean which is true if the user wants to calculate the size of the
	 * symbol using a normalization field.
	 *
	 * @param useNormalization true if the user wants normalization.Otherwise, false.
	 */
	public void setUseNormalization(boolean useNormalization) {this.useNormalization = useNormalization;}
	/**
	 * Obtains the variable which represents the maximum value of the classifying field that is used
	 * to calculate the size of the symbol
	 *
	 * @return double  the maximum value of the classifying field
	 */
	public double getMaxFeature() {return maxFeature;}
	/**
	 * Sets the variable which represents the maximum value of the classifying field that is used
	 * to calculate the size of the symbol
	 *
	 * @param maxFeature
	 */
	public void setMaxFeature(double maxFeature) {this.maxFeature = maxFeature;}
	/**
	 * Obtains the variable which represents the minimum value of the classifying field that is used
	 * to calculate the size of the symbol
	 *
	 * @return double  the minimum value of the classifying field
	 */
	public double getMinFeature() {return minFeature;}
	/**
	 * Sets the variable which represents the minimum value of the classifying field that is used
	 * to calculate the size of the symbol
	 *
	 * @param minFeature
	 */
	public void setMinFeature(double minFeature) {this.minFeature = minFeature;}


	@Override
	public boolean isSuitableForShapeType(int shapeType) {
		return (getShapeType()%FShape.Z) == (shapeType%FShape.Z) || ((getTemplateShapeType()%FShape.Z) == FShape.POINT && (shapeType%FShape.Z) == FShape.POLYGON);
	}


	public ISymbol[] getSymbols() {
		ISymbol[] auxSymbols=super.getSymbols();
		if(backgroundSymbol != null){
			ISymbol[] symbols=new ISymbol[auxSymbols.length+1];
			for (int i = 0; i < auxSymbols.length; i++) {
				symbols[i]=auxSymbols[i];
			}
			symbols[symbols.length-1]=backgroundSymbol;
			return symbols;
		} else {
			return auxSymbols;
		}
	}



	public ZSort getZSort() {
		if (zSort == null){
			zSort = new MyZSort(this);
		}
		return zSort;
	}

	public void setZSort(ZSort zSort){
		return;
	}

	private class MyZSort extends ZSort {

		public MyZSort(ILegend legend) {
			super(legend);
		}

		public void legendChanged(LegendChangedEvent e) {
		}

		public String getClassName() {
			return getClass().getName();
		}

		public int getLevelCount() {
			int levels = 0;
			if (backgroundSymbol!=null){
				if(backgroundSymbol instanceof IMultiLayerSymbol){
					levels += ((IMultiLayerSymbol)backgroundSymbol).getLayerCount();
				} else {
					levels += 1;
				}
			}
			ISymbol sym = getDefaultSymbol();
			if(getDefaultSymbol() instanceof IMultiLayerSymbol){
				levels += ((IMultiLayerSymbol)sym).getLayerCount();
			} else {
				levels += 1;
			}
			return levels+1;
		}


		public void setUsingZSort(boolean usingZSort) {
		}

		public void setLevels(ISymbol sym, int[] values) {
		}


		public void setLevels(int row, int[] values) {
		}

		public int[] getLevels(ISymbol sym) {
			return getLevels(0);
		}

		public int[] getLevels(int row) {
			int levelsCount = getLevelCount();
			int[] levels = new int[levelsCount];
			int bgLevels = 1;
			if (backgroundSymbol!=null){
				if(backgroundSymbol instanceof IMultiLayerSymbol){
					bgLevels = ((IMultiLayerSymbol)backgroundSymbol).getLayerCount();
				}
				for (int i=0; i<bgLevels; i++) {
					levels[i]=i;
				}
			}

			ISymbol sym = getDefaultSymbol();
			int frLevels = 1;
			if(getDefaultSymbol() instanceof IMultiLayerSymbol){
				frLevels = ((IMultiLayerSymbol)sym).getLayerCount();
			}
			for (int i=0; i<frLevels; i++) {
				levels[i+bgLevels]=i+bgLevels;
			}
			levels[frLevels+bgLevels]=frLevels+bgLevels;
			return levels;
		}


		public boolean isUsingZSort() {
			return backgroundSymbol!=null;
		}


		public ISymbol[] getSymbols() {
			return getSymbols();
		}

		public String[] getDescriptions() {
			return getDescriptions();
		}

		public int getTopLevelIndexAllowed() {
			return getLevelCount();
		}

		@Override
		public String toString() {
			String out = "ZSort for ProportionalSymbolLegend";
			return out;
		}

		public boolean symbolChanged(SymbolLegendEvent e) {
			return true;
		}

		public boolean classifiedSymbolChange(SymbolLegendEvent e) {
			return true;
		}

		public boolean intervalChange(IntervalLegendEvent e) {
			return false;
		}

		public boolean valueChange(ValueLegendEvent e) {
			return false;
		}

		// TODO should not exist here
		public boolean labelFieldChange(LabelLegendEvent e) {
			return false;
		}

		public void legendCleared(LegendClearEvent event) {
		}

//		public void setXMLEntity(XMLEntity xml) {
//		}
//
//		public XMLEntity getXMLEntity() {
//			XMLEntity xml = new XMLEntity();
//			xml.putProperty("className", getClassName());
//
//			/*
//			 * ADVICE:
//			 * don't try to persist symbols!!
//			 * they are already persisted by the legend!!!
//			 */
//
//			xml.putProperty("usingZSort", isUsingZSort());
//			return xml;
//
//		}


	}




}