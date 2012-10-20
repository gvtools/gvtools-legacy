/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */

/* gvSIG. Sistema de Información Geográfica de la Generalitat Valenciana
 *
 * Copyright (C) 2006 Prodevelop and Generalitat Valenciana.
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
 *   +34 963862235
 *   gvsig@gva.es
 *   www.gvsig.gva.es
 *
 *    or
 *
 *   Prodevelop Integración de Tecnologías SL
 *   Conde Salvatierra de Álava , 34-10
 *   46004 Valencia
 *   Spain
 *
 *   +34 963 510 612
 *   +34 963 510 968
 *   gis@prodevelop.es
 *   http://www.prodevelop.es
 */
package org.gvsig.remoteClient.arcims.styling.renderers;

import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.gvsig.remoteClient.arcims.ArcXML;
import org.gvsig.remoteClient.arcims.exceptions.ArcImsException;
import org.gvsig.remoteClient.arcims.styling.symbols.ArcImsFSymbolFactory;
import org.gvsig.remoteClient.arcims.styling.symbols.IArcIMSSymbol;
import org.gvsig.remoteClient.arcims.utils.ArcImsValueFactory;
import org.gvsig.remoteClient.arcims.utils.FieldInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInfoTags;
import org.gvsig.remoteClient.arcims.utils.ServiceInformation;
import org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerFeatures;

import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.core.symbols.ISymbol;
import com.iver.cit.gvsig.fmap.core.v02.FConstant;
import com.iver.cit.gvsig.fmap.layers.SelectableDataSource;
import com.iver.cit.gvsig.fmap.rendering.FInterval;
import com.iver.cit.gvsig.fmap.rendering.IClassifiedVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.ILegend;
import com.iver.cit.gvsig.fmap.rendering.IVectorLegend;
import com.iver.cit.gvsig.fmap.rendering.LegendFactory;
import com.iver.cit.gvsig.fmap.rendering.NullIntervalValue;
import com.iver.cit.gvsig.fmap.rendering.NullUniqueValue;
import com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend;
import com.iver.cit.gvsig.fmap.rendering.VectorialIntervalLegend;
import com.iver.cit.gvsig.fmap.rendering.VectorialUniqueValueLegend;

/**
 * Factory class to produce FLegend's from ArcIMS Renderers defintions
 * 
 * @author jsanz
 * 
 */
public class ArcImsFLegendFactory {
	private static Logger logger = Logger.getLogger(ArcImsFLegendFactory.class
			.getName());
	private char ds;
	private ServiceInformationLayerFeatures silf;
	private ServiceInformation si;

	// We use a class variable to overwrite every label definition
	private ILegend auxLabelsOuter = null;

	// private String PATRON = ArcXML.PATRON;
	// DecimalFormat formatter;
	private int featType;

	/**
	 * @param ds
	 *            Decimal separator char
	 * @param msilf
	 *            An object with all the information to build the legend of a
	 *            vectorial layer
	 * @see org.gvsig.remoteClient.arcims.utils.ServiceInformationLayerFeatures
	 */
	public ArcImsFLegendFactory(ServiceInformation msi, String layerId) {
		si = msi;
		ds = msi.getSeparators().getDs();
		ArcImsFSymbolFactory.ds = ds;

		silf = (ServiceInformationLayerFeatures) this.si.getLayerById(layerId);
		this.featType = getSymbType(silf.getFclasstype());

		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator(ds);

		// formatter = new DecimalFormat(PATRON,dfs);
	}

	/**
	 * Main method on this class. It calls proper getLegend method based on type
	 * of render.
	 * 
	 * @param render
	 * @return
	 * @throws ArcImsException
	 */
	public ILegend getMainLegend(SelectableDataSource sds)
			throws ArcImsException {
		logger.info("======Start creating main layer Legend======");

		Renderer render = silf.getLayerMainRenderer();
		ILegend leg = getLegend(render, true, sds);
		logger.info("======Getting main Legend ("
				+ getClassName(leg.getClass()) + ")======");

		return leg;
	}

	/**
	 * Main method that calls proper getLengend methods according to the type of
	 * renderer is passed.
	 * 
	 * @param render
	 * @param isMainRender
	 * @return
	 * @throws ArcImsException
	 */
	private IVectorLegend getLegend(Renderer render, boolean isMainRender,
			SelectableDataSource sds) throws ArcImsException {
		IVectorLegend leg = null;

		/*
		 * if (render instanceof SimpleRenderer) { SimpleRenderer srender =
		 * (SimpleRenderer) render; leg = getLegend(srender); } else if (render
		 * instanceof ValueMapRenderer) { ValueMapRenderer srender =
		 * (ValueMapRenderer) render; leg = getLegend(srender); } // In the main
		 * legend, a ScaleDependent and a GroupRenderer can be // treated in the
		 * same way else if (isMainRender && (render instanceof GroupRenderer))
		 * { GroupRenderer groupRender = (GroupRenderer) render; leg =
		 * getLegend(groupRender); } // Otherwise, we will use their proper
		 * method else if (!isMainRender && (render instanceof GroupRenderer)) {
		 * if (render instanceof ScaleDependentRenderer) {
		 * ScaleDependentRenderer sdRender = (ScaleDependentRenderer) render;
		 * leg = getLegend(sdRender); }
		 * 
		 * // In the main legend, a ScaleDependent and a GroupRenderer can be //
		 * treated in the same way else if (render instanceof GroupRenderer) {
		 * GroupRenderer groupRender = (GroupRenderer) render; leg =
		 * getLegend(groupRender); } } else leg = getDefaultLegend();
		 */

		if (render instanceof SimpleRenderer) {
			SimpleRenderer srender = (SimpleRenderer) render;
			leg = getLegend(srender);
		} else if (render instanceof ValueMapRenderer) {
			ValueMapRenderer srender = (ValueMapRenderer) render;
			leg = getLegend(srender, sds);
		}
		// In the main legend, a ScaleDependent and a GroupRenderer can be
		// treated in the same way (ScaleDependent inherits from GroupRenderer)
		else if (render instanceof GroupRenderer) {
			GroupRenderer groupRender = (GroupRenderer) render;
			leg = getLegend(groupRender, sds);
		} else {
			leg = getDefaultLegend();
		}

		if (leg != null) {
			logger.info("Getting Render of type: "
					+ getClassName(render.getClass()));

			return leg;
		} else {
			throw new ArcImsException("arcims_legend_error");
		}
	}

	/**
	 * Method to generate a valid simple legend.
	 * 
	 * @return
	 */
	private IVectorLegend getDefaultLegend() {
		SingleSymbolLegend leg = new SingleSymbolLegend();
		leg.setDefaultSymbol(ArcImsFSymbolFactory.getDefaultFSymbol(featType));
		leg.setShapeType(featType);
		logger.info("Getting default legend");

		return leg;
	}

	/**
	 * Gets a simple Legend with only one symbol
	 * 
	 * @see com.iver.cit.gvsig.fmap.rendering.SingleSymbolLegend
	 * @param renderer
	 * @return
	 */
	private IVectorLegend getLegend(SimpleRenderer renderer) {
		// Get the ArcIMS Simbol
		IArcIMSSymbol imsSimb = renderer.getSymbol();

		ISymbol simb = imsSimb.getFSymbol();

		// Creates the legend
		SingleSymbolLegend leg = new SingleSymbolLegend();
		leg.setDefaultSymbol(simb);
		leg.setShapeType(this.featType);
		logger.info("Getting a Simple Renderer");

		return (IVectorLegend) leg;
	}

	/**
	 * Gets a Legend based on intervals
	 * 
	 * @see com.iver.cit.gvsig.fmap.rendering.VectorialIntervalLegend
	 * @param renderer
	 * @return
	 * @throws ArcImsException
	 */
	private IVectorLegend getLegend(ValueMapRenderer renderer,
			SelectableDataSource sds) throws ArcImsException {
		// Get the proper Field Information object
		FieldInformation fi = silf.getFieldInformation(renderer
				.getLookupfield());

		// Gets the array of ranges
		ArrayList values = renderer.getValues();
		Iterator it = values.iterator();

		// Creates the two types of legends
		VectorialIntervalLegend viLeg = new VectorialIntervalLegend();
		VectorialUniqueValueLegend vuLeg = new VectorialUniqueValueLegend();

		// Initialize counters
		int nIntervals = 0;
		int nUniques = 0;

		// Create a default simbol
		ISymbol defSimb = ArcImsFSymbolFactory.getDefaultFSymbol(this.featType);
		defSimb.setDescription("Default");

		boolean hasDefSymbol = false;

		// Fills with intervals and symbols
		while (it.hasNext()) {
			// Only RangeValueMaps are allowed
			TypeValueMap tvm = (TypeValueMap) it.next();

			// Gets the symbol
			IArcIMSSymbol simb = tvm.getSymbol();
			ISymbol fsimb = simb.getFSymbol();

			// Set the description
			String descr = tvm.getLabel();

			if (descr == null) {
				descr = "";
			}

			fsimb.setDescription(descr);

			if (tvm instanceof RangeValueMap) {
				RangeValueMap rvm = (RangeValueMap) tvm;

				// Get the bounds of the intverval
				String sFrom = rvm.getLower();
				String sTo = rvm.getUpper();
				double from = Double.parseDouble(sFrom.replace(ds, '.'));
				double to = Double.parseDouble(sTo.replace(ds, '.'));

				// Creates the Interval
				FInterval interv = new FInterval(from, to);

				// Adds the symbol to the legend
				viLeg.addSymbol(interv, fsimb);

				// viLeg.setIntervalSymbol(interv,fsimb);
				nIntervals++;
			} else if (tvm instanceof ExactValueMap) {
				ExactValueMap evm = (ExactValueMap) tvm;

				// We have to build a Value object
				String strVal = evm.getValue();

				try {
					Value val = ArcImsValueFactory.createValueByType(strVal,
							fi.getType(), this.ds);
					vuLeg.addSymbol(val, fsimb);
				} catch (ParseException e) {
					logger.error(e.getMessage(), e);
					throw new ArcImsException("arcims_legend_error");
				}

				nUniques++;
			} else if (tvm instanceof OtherValueMap) {
				hasDefSymbol = true;
				defSimb = fsimb;
			}
		}

		/*
		 * Determine what type of legend the method will return (Unique or
		 * Interval) The condition will be the legend with more classes
		 * (intervals, values)
		 */
		IVectorLegend leg = null;
		IClassifiedVectorLegend cleg = null;

		if (nUniques >= nIntervals) {
			leg = vuLeg;
			cleg = vuLeg;
		} else {
			leg = viLeg;
			cleg = viLeg;
		}

		/*
		 * Finally we can add the field name and default symbol
		 */

		// Set the field name
		cleg.setClassifyingFieldNames(new String[] { ArcXML
				.replaceUnwantedCharacters(fi.getName()) });

		try {
			cleg.setDataSource(sds);
		} catch (Exception ex) {
			throw new ArcImsException("While setting data source. ", ex);
		}

		// Set the default symbol, if it is used and asign the symbol to a
		// nullvalue
		leg.setDefaultSymbol(defSimb);
		leg.useDefaultSymbol(hasDefSymbol);

		if (hasDefSymbol) {
			if (leg instanceof VectorialUniqueValueLegend) {
				vuLeg.addSymbol(new NullUniqueValue(), leg.getDefaultSymbol());
			} else {
				viLeg.addSymbol(new NullIntervalValue(), leg.getDefaultSymbol());
			}
		}

		logger.info("Getting a Value Map Renderer");

		return leg;
	}

	// /**
	// * Gets the most detailed legend from the ScaleDependentRender
	// * @param sdrender
	// * @return
	// */
	// private VectorialLegend getLegend(ScaleDependentRenderer sdrender) throws
	// ArcImsException {
	// VectorialLegend leg = getLegend((GroupRenderer) sdrender);
	// logger.info("Getting a ScaleDependent Renderer");
	// return leg;
	// }
	private IVectorLegend getLegend(GroupRenderer groupRender,
			SelectableDataSource sds) throws ArcImsException {
		ArrayList renderers = groupRender.renderers;
		ArrayList legends = new ArrayList();
		IVectorLegend biggerScale = null;
		IVectorLegend leg = null;
		double[] limit = { Double.MAX_VALUE, 0 };
		Iterator it = renderers.iterator();

		// We use a class variable to overwrite every label definition
		IVectorLegend auxLabelsInner = null;

		while (it.hasNext()) {
			leg = null;

			Renderer iRender = (Renderer) it.next();

			// If we don't have a Label renderer use the generic method
			if (!(iRender instanceof ILabelRenderer)) {
				leg = getLegend(iRender, false, sds);
			}
			// Otherwise, use the specific method for labels
			else {
				auxLabelsInner = getLegend((ILabelRenderer) iRender);
			}

			// If renderer is Scale Dependent, we inspect their scale limits
			if (iRender instanceof ScaleDependentRenderer) {
				ScaleDependentRenderer iSDRender = (ScaleDependentRenderer) iRender;
				String strLow = iSDRender.getLower();
				String strUpp = iSDRender.getUpper();
				double low = 0;
				double upp = Double.MAX_VALUE;

				if (strLow != null) {
					low = Double.parseDouble(strLow.replace(ds, '.'));
				}

				if (strUpp != null) {
					upp = Double.parseDouble(strUpp.replace(ds, '.'));
				}

				// First loop
				if (biggerScale == null) {
					biggerScale = leg;
					limit[0] = low;
					limit[1] = upp;
				}
				/*
				 * Next loops we allways get the minimum interval If lower
				 * bounds are equal, we get the lower upper value
				 */
				else if ((low <= limit[0]) && (upp <= limit[1])) {
					limit[0] = low;
					limit[1] = upp;
					biggerScale = leg;
				}
			}

			if (leg != null) {
				legends.add(leg);
			}
		}

		/*
		 * TODO At this time we will return the first (bottom) legend of the
		 * ArrayList or the most detailed legend if there are any
		 * ScaleDependentRenderers Probably I will add more "logic" into this
		 * code
		 */
		IVectorLegend finalLegend = null;
		boolean onlyLabels = false;

		/*
		 * Check if auxLabels is equal to the last element of the array Legends
		 * and if it's true, merge it with the first one (its label definition)
		 */
		if (auxLabelsOuter != null && legends.size() > 0) {

			for (int i = 0; i < legends.size(); i++) {
				ISymbol simb1 = auxLabelsOuter.getDefaultSymbol();
				ISymbol simb2 = ((IVectorLegend) legends.get(i))
						.getDefaultSymbol();

				if (simb1 == simb2) {
					// Null the biggerScale to force the next if statement
					biggerScale = null;
					// Merge the auxLabels object into the first legend of the
					// arrayList
					// IVectorLegend legend =
					// setLabelDefIntoSymbol(auxLabelsOuter,(IVectorLegend)
					// legends.get(0));

					// legends.set(0,legend);
					// Null the auxlabelsinner to avoid the last if
					auxLabelsInner = null;
					break;
				}
			}

		}

		// We don't have any scaledependent
		if (((biggerScale == null) && (legends.size() > 0))) {
			// if (!(groupRender instanceof ScaleDependentRenderer)){
			logger.info("Getting the bottom renderer of the Group Renderer");
			finalLegend = (IVectorLegend) legends.get(0);
		}
		// We have any scaledpendent
		else if (biggerScale != null) {
			logger.info("Getting the most detailed ScaleDependent renderer of the Group Renderer");
			finalLegend = biggerScale;
		}
		// We don't have any legend parsed (maybe the layer only have label
		// definitions
		else {
			finalLegend = LegendFactory.createSingleSymbolLegend(this.featType);
			onlyLabels = true;
		}

		/*
		 * Finally if some label renderer is found, we have to pass the label
		 * properties to the final legend
		 */

		/*
		 * if (auxLabelsInner != null) {
		 * 
		 * // finalLegend = setLabelDefIntoSymbol(auxLabelsInner,finalLegend);
		 * // finalLegend.getDefaultSymbol().setShapeVisible(!onlyLabels); //
		 * ISymbol sym = finalLegend.getDefaultSymbol(); ((FSymbol)
		 * finalLegend.getDefaultSymbol()).setShapeVisible(!onlyLabels);
		 * auxLabelsOuter = finalLegend; }
		 */
		return finalLegend;
	}

	/**
	 * Method that returns a SingleSymboLegend with Font definition and field to
	 * retrieve labels. These properties will be transferred to the main layer
	 * legend.
	 * 
	 * @param renderer
	 * @return
	 */
	private IVectorLegend getLegend(ILabelRenderer renderer) {
		// Get the proper Field Information object
		FieldInformation fi = silf.getFieldInformation(renderer.getField());

		// Create a legend
		SingleSymbolLegend leg = (SingleSymbolLegend) LegendFactory
				.createSingleSymbolLegend(this.featType);
		IArcIMSSymbol arcSimb = null;

		// Get the ArcIMS Symbol with Font definition
		if (renderer instanceof SimpleLabelRenderer) {
			SimpleLabelRenderer slrender = (SimpleLabelRenderer) renderer;
			arcSimb = slrender.getSymbol();
		} else if (renderer instanceof ValueMapLabelRenderer) {
			ValueMapLabelRenderer vmlrender = (ValueMapLabelRenderer) renderer;
			arcSimb = vmlrender.getValue(0).getSymbol();
		}

		// Asign it (or a default) to the legend
		if (arcSimb != null) {
			leg.setDefaultSymbol(arcSimb.getFSymbol());
		} else {
			leg.setDefaultSymbol(ArcImsFSymbolFactory
					.getDefaultFSymbol(featType));
		}

		// Set the label field
		// leg.setLabelField(ArcXML.replaceUnwantedCharacters(fi.getName()));

		// Return the legend
		return leg;
	}

	/**
	 * @return Returns the featType.
	 */
	public int getFeatType() {
		return featType;
	}

	/**
	 * @param featType
	 *            The featType to set.
	 */
	public void setFeatType(int featType) {
		this.featType = featType;
	}

	/**
	 * Gets the simple class name
	 * 
	 * @param mclass
	 * @return
	 */
	private String getClassName(Class mclass) {
		String[] classSplitted = mclass.getName().split("\\.");

		return classSplitted[classSplitted.length - 1];
	}

	/**
	 * Private method to set into a legend the label definition of another
	 * legend
	 * 
	 * @param withLabels
	 * @param finalLegend
	 * @return
	 */

	// A SER SUSTITUIDO POR UN GETLABELINGSTRATEGY
	// private IVectorLegend setLabelDefIntoSymbol(IVectorLegend withLabels,
	// IVectorLegend finalLegend) {
	//
	// //Set the label
	// finalLegend.setLabelField(withLabels.getLabelField());
	// //Get simbol with font definition
	//
	// // FSymbol simbWithFont = withLabels.getDefaultSymbol();
	// FSymbol simbWithFont = (FSymbol) withLabels.getDefaultSymbol();
	//
	// //Get simbol to insert font definition
	// // FSymbol simb = finalLegend.getDefaultSymbol();
	// FSymbol simb = (FSymbol) finalLegend.getDefaultSymbol();
	//
	// //Insert into simb the font properties
	// simb.setFont(simbWithFont.getFont());
	// simb.setFontColor(simbWithFont.getFontColor());
	// simb.setFontSize(simbWithFont.getFontSize());
	// simb.setFontSizeInPixels(true);
	// //Set simbol modified into final legend to return
	// finalLegend.setDefaultSymbol(simb);
	//
	// return finalLegend;
	// }

	/**
	 * Converts ArcIMS feature type to a FConstant value
	 * 
	 * @param fclasstype
	 * @return
	 */
	private int getSymbType(String fclasstype) {
		if (fclasstype.equals(ServiceInfoTags.aPOLYGON)) {
			return FConstant.SYMBOL_TYPE_FILL;
		}

		if (fclasstype.equals(ServiceInfoTags.aPOLYLINE)) {
			return FConstant.SYMBOL_TYPE_LINE;
		}

		if (fclasstype.equals(ServiceInfoTags.aMULTIPOINT)) {
			return FConstant.SYMBOL_TYPE_POINT;
		}

		return FConstant.SHAPE_TYPE_NULL;
	}
}
