package org.gvsig.graph.gui.wizard.servicearea;

import java.util.Arrays;

import javax.swing.table.AbstractTableModel;

import org.gvsig.graph.core.NetworkUtils;

import com.iver.cit.gvsig.fmap.layers.FBitSet;

public abstract class AbstractPointsModel extends AbstractTableModel {

	public final String COSTS_SEPARATOR = ";";

	protected String uniqueMainCosts;
	protected String uniqueSecondaryCosts;

	protected FBitSet bitsetEnabled;
	protected String[] mainCosts;
	protected String[] secondaryCosts;

	public AbstractPointsModel() {
		this.uniqueMainCosts = "";
		this.uniqueSecondaryCosts = "";

		this.bitsetEnabled = new FBitSet();

		this.mainCosts = new String[0];
		this.secondaryCosts = new String[0];
	}

	protected String getTotalCosts(String costs) throws NumberFormatException {
		if (!costs.trim().equals("")) {
			double[] costsArray = NetworkUtils.string2doubleArray(costs,
					COSTS_SEPARATOR);
			String s = "";
			Arrays.sort(costsArray);
			for (int i = 0; i < costsArray.length; i++) {
				s += costsArray[i] + COSTS_SEPARATOR;
			}
			s = s.trim();
			if (s.endsWith(COSTS_SEPARATOR)) {
				s = s.substring(0, s.length() - 1);
			}

			return s;
		}
		return "";
	}

	public void enableUniqueMainCost(String cost) throws InvalidCostException {
		try {
			this.uniqueMainCosts = this.getTotalCosts(cost);
		} catch (NumberFormatException except) {
			throw new InvalidCostException(
					"El coste principal único es incorrecto");
		}
	}

	public void disableUniqueMainCost() {
		this.uniqueMainCosts = "";
	}

	public boolean isUniqueMainCostEnabled() {
		return !(this.uniqueMainCosts == "");
	}

	public void enableUniqueSecondaryCost(String cost)
			throws InvalidCostException {
		try {
			this.uniqueSecondaryCosts = this.getTotalCosts(cost);
		} catch (NumberFormatException except) {
			throw new InvalidCostException(
					"El coste secundario único es incorrecto");
		}
	}

	public void disableUniqueSecondaryCost() {
		this.uniqueSecondaryCosts = "";
	}

	public boolean isUniqueSecondaryCostEnabled() {
		return !(this.uniqueSecondaryCosts == "");
	}

	public boolean isRowEnabled(int rowIndex) throws IndexOutOfBoundsException {
		return this.bitsetEnabled.get(rowIndex);
	}

	public int getEnabledRowsCount() {
		return this.bitsetEnabled.cardinality();
	}

	public abstract String toString();

	protected class InvalidCostFieldException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8833597115486345583L;

		public InvalidCostFieldException(String message) {
			super(message);
		}
	}

	protected class InvalidCostException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2050469106154011724L;

		public InvalidCostException(String message) {
			super(message);
		}
	}
}
