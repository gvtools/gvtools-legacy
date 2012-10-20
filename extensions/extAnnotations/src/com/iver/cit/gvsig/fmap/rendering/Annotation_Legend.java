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
package com.iver.cit.gvsig.fmap.rendering;

import java.awt.Color;
import java.util.ArrayList;

import com.hardcode.gdbms.engine.data.DataSource;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.CartographicSupport;
import com.iver.cit.gvsig.fmap.core.CartographicSupportToolkit;
import com.iver.cit.gvsig.fmap.core.IFeature;
import com.iver.cit.gvsig.fmap.core.SymbologyFactory;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.symbols.ITextSymbol;
import com.iver.cit.gvsig.fmap.core.symbols.SimpleTextSymbol;
import com.iver.cit.gvsig.fmap.layers.XMLException;
import com.iver.utiles.XMLEntity;

/**
 * Leyenda vectorial para labels.
 * 
 * @author Vicente Caballero Navarro
 */
public class Annotation_Legend extends AbstractLegend implements IVectorLegend {
	private String fieldName;

	protected int fieldId = -1;

	private String labelFieldName;

	private String labelFieldHeight;

	private String labelFieldRotation;

	private ITextSymbol defaultSymbol = SymbologyFactory
			.createDefaultTextSymbol();

	private int shapeType;

	private boolean useDefaultSymbol = false;

	// private boolean overwrite=true;

	private boolean avoidoverlapping = false;

	private boolean deloverlapping = false;

	private boolean isFontInPixels;

	private boolean pointVisible = true;

	private int units = CartographicSupportToolkit.DefaultMeasureUnit;

	public Annotation_Legend() {
		isFontInPixels = true;
		defaultSymbol.setTextColor(Color.black);
		((SimpleTextSymbol) defaultSymbol).setRotation(0);
		defaultSymbol.setFontSize(10);
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.UniqueValueLegend#addSymbol(java.lang.Object,
	 *      ISymbol)
	 */
	public void addSymbol(Object key, ISymbol symbol) {
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.ClassifiedLegend#clear()
	 */
	public void clear() {
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.ClassifiedLegend#getDescriptions()
	 */
	public String[] getDescriptions() {
		return null;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.ClassifiedLegend#getSymbols()
	 */
	public ISymbol[] getSymbols() {
		return null;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.ClassifiedLegend#getFieldName()
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.VectorialLegend#setDefaultSymbol(ISymbol)
	 */
	public void setDefaultSymbol(ISymbol s) {
		defaultSymbol = (ITextSymbol) s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.rendering.VectorialLegend#getLabelField()
	 */
	public String getLabelField() {
		return labelFieldName;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.Legend#setLabelField(int)
	 */
	public void setLabelField(String fieldName) {
		labelFieldName = fieldName;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.ClassifiedLegend#setField()
	 */
	public void setFieldName(String str) {
		fieldName = str;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.VectorialLegend#getSymbol(int)
	 */
	public ISymbol getSymbol(int recordIndex) {
		return null;
	}

	/**
	 * Devuelve un símbolo a partir de una IFeature. OJO!! Cuando usamos un
	 * feature iterator de base de datos el único campo que vendrá rellenado es
	 * el de fieldID. Los demás vendrán a nulos para ahorra tiempo de creación.
	 * 
	 * @param feat
	 *            IFeature
	 * 
	 * @return Símbolo.
	 */
	public ISymbol getSymbolByFeature(IFeature feat) {
		return null;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.Legend#getDefaultSymbol()
	 */
	public ISymbol getDefaultSymbol() {
		return defaultSymbol;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.UniqueValueLegend#getValues()
	 */
	public Object[] getValues() {
		// return symbols.keySet().toArray(new Object[0]);
		return null;
	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.Legend#getXMLEntity()
	 */
	public XMLEntity getXMLEntity() {
		XMLEntity xml = new XMLEntity();
		xml.putProperty("className", this.getClass().getName());
		xml.putProperty("fieldName", fieldName);
		xml.putProperty("labelfield", labelFieldName);
		xml.putProperty("labelFieldHeight", labelFieldHeight);
		xml.putProperty("labelFieldRotation", labelFieldRotation);
		xml.putProperty("avoidoverlapping", avoidoverlapping);
		xml.putProperty("deloverlapping", deloverlapping);

		// xml.putProperty("overwrite",overwrite);
		xml.putProperty("units", units);
		xml.putProperty("isFontInPixels", isFontInPixels);
		xml.putProperty("pointVisible", pointVisible);

		xml.putProperty("useDefaultSymbol", useDefaultSymbol);
		xml.addChild(getDefaultSymbol().getXMLEntity());
		return xml;
	}

	/**
	 * Inserta el XMLEntity.
	 * 
	 * @param xml
	 *            XMLEntity.
	 */
	public void setXMLEntity03(XMLEntity xml) {

	}

	/**
	 * Inserta el XMLEntity.
	 * 
	 * @param xml
	 *            XMLEntity.
	 */
	public void setXMLEntity(XMLEntity xml) {
		clear();
		setFieldName(xml.getStringProperty("fieldName"));
		setLabelField(xml.getStringProperty("labelfield"));

		if (xml.contains("labelFieldHeight")) {
			setLabelHeightField(xml.getStringProperty("labelFieldHeight"));
		}

		if (xml.contains("labelFieldRotation")) {
			setLabelRotationField(xml.getStringProperty("labelFieldRotation"));
		}

		useDefaultSymbol = xml.getBooleanProperty("useDefaultSymbol");
		if (xml.contains("avoidoverlapping")) {
			avoidoverlapping = xml.getBooleanProperty("avoidoverlapping");
			deloverlapping = xml.getBooleanProperty("deloverlapping");
		}
		if (xml.contains("pointVisible")) {
			pointVisible = xml.getBooleanProperty("pointVisible");
		}
		if (xml.contains("units")) {
			units = xml.getIntProperty("units");
		}
		if (xml.contains("isFontInPixels")) {
			isFontInPixels = xml.getBooleanProperty("isFontInPixels");
			defaultSymbol = (ITextSymbol) SymbologyFactory.createSymbolFromXML(
					xml.getChild(0), "default symbol");

		} else {
			ITextSymbol symbol = (ITextSymbol) SymbologyFactory
					.createSymbolFromXML(xml.getChild(0), "default symbol");
			isFontInPixels = ((CartographicSupport) symbol).getUnit() == -1;
			pointVisible = symbol.isShapeVisible();
			defaultSymbol = (SimpleTextSymbol) SymbologyFactory
					.createDefaultTextSymbol();
		}

	}

	/**
	 * @see com.iver.cit.gvsig.fmap.rendering.Legend#cloneLegend()
	 */
	public ILegend cloneLegend() throws XMLException {
		return LegendFactory.createFromXML(getXMLEntity());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.rendering.VectorialLegend#setDataSource(com.hardcode
	 * .gdbms.engine.data.DataSource)
	 */
	public void setDataSource(DataSource ds) {
		// try {
		// dataSource = ds;
		// ds.start();
		// fieldId = ds.getFieldIndexByName(fieldName);
		// ds.stop();
		// } catch (com.hardcode.gdbms.engine.data.driver.DriverException e) {
		// throw new DriverException(e);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.rendering.UniqueValueLegend#getSymbolByValue(
	 * com.hardcode.gdbms.engine.values.Value)
	 */
	public ISymbol getSymbolByValue(Value key) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.rendering.VectorialLegend#getShapeType()
	 */
	public int getShapeType() {
		return shapeType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.rendering.VectorialLegend#getLabelHeightField()
	 */
	public String getLabelHeightField() {
		return labelFieldHeight;
	}

	/**
	 * Inserta el alto de campo.
	 * 
	 * @param str
	 *            alto.
	 */
	public void setLabelHeightField(String str) {
		labelFieldHeight = str;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.iver.cit.gvsig.fmap.rendering.VectorialLegend#getLabelRotationField()
	 */
	public String getLabelRotationField() {
		return labelFieldRotation;
	}

	/**
	 * Inserta rotación.
	 * 
	 * @param str
	 *            Rotación.
	 */
	public void setLabelRotationField(String str) {
		labelFieldRotation = str;
	}

	/**
	 * Introduce si se tiene que representar el resto de valores o no.
	 * 
	 * @param b
	 *            True si se utiliza el resto de valores.
	 */
	public void useDefaultSymbol(boolean b) {
		useDefaultSymbol = b;
	}

	/**
	 * Devuelve si se utiliza o no el resto de valores para representarse.
	 * 
	 * @return True si se utiliza el resto de valores.
	 */
	public boolean isUseDefaultSymbol() {
		return useDefaultSymbol;
	}

	/**
	 * Elimina el símbolo que tiene como clave el valor que se pasa como
	 * parámetro.
	 * 
	 * @param key
	 *            clave.
	 */
	public void delSymbol(Object key) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iver.cit.gvsig.fmap.rendering.VectorialLegend#getUsedFields()
	 */
	public String[] getUsedFields() {
		ArrayList usedFields = new ArrayList();
		if (getFieldName() != null)
			usedFields.add(getFieldName());
		if (getLabelField() != null)
			usedFields.add(getLabelField());
		if (getLabelHeightField() != null)
			usedFields.add(getLabelHeightField());
		if (getLabelRotationField() != null)
			usedFields.add(getLabelRotationField());

		return (String[]) usedFields.toArray(new String[0]);

	}

	public void setShapeType(int shapeType) {
		// TODO Auto-generated method stub

	}

	public String getSLDString(String layerName) {
		// TODO Auto-generated method stub
		return null;
	}

	// public void setIsOverWrite(boolean b) {
	// overwrite=b;
	//
	// }

	// public boolean isOverWrite() {
	// return overwrite;
	// }

	public boolean isAvoidOverLapping() {
		return avoidoverlapping;
	}

	public void setAvoidOverLapping(boolean avoidoverlapping) {
		this.avoidoverlapping = avoidoverlapping;
	}

	public boolean isDelOverLapping() {
		return deloverlapping;
	}

	public void setDelOverLapping(boolean deloverlapping) {
		this.deloverlapping = deloverlapping;
	}

	public ZSort getZSort() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setZSort(ZSort zSort) {
		// TODO Auto-generated method stub

	}

	public String getClassName() {
		return this.getClass().getName();
	}

	public boolean isFontSizeInPixels() {
		return isFontInPixels;
	}

	public void setFontInPixels(boolean isFontInPixels) {
		this.isFontInPixels = isFontInPixels;
	}

	public int getUnits() {
		return units;
	}

	public void setUnits(int units) {
		if (units == -1) {
			this.isFontInPixels = true;
		} else {
			this.isFontInPixels = false;
		}
		this.units = units;
	}

	public void setPointVisible(boolean b) {
		this.pointVisible = b;

	}

	public boolean isPointVisible() {
		return pointVisible;
	}

	public boolean isSuitableForShapeType(int shapeType) {
		return this.shapeType == shapeType;
	}
}
