package com.iver.cit.gvsig.project.documents.table;

import java.math.BigDecimal;
import java.util.BitSet;
import java.util.Map;

import org.gvsig.exceptions.BaseException;

import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.NumericValue;
import com.hardcode.gdbms.engine.values.Value;
import com.iver.cit.gvsig.fmap.selection.ResettableIterator;

/**
 * <p>
 * Computes statistics for an iterator of Values.
 * </p>
 * 
 * <p>
 * In order to get an optimal precision, BigDecimals are used to perform
 * calculations. Anyway, calculations are reasonably fast, because statistics
 * are cached. For example, if we first call variance() and then stdDeviation(),
 * the second call just needs to perform the square root of the cached variance
 * value.
 * </p>
 * 
 * <p>
 * Warning: This object is not thread-safe!!! Use external synchronization if
 * you plan to use it from different threads, otherwise it is warranted to
 * fail!!
 * </p>
 * 
 * @author Cesar Martinez Izquierdo <cesar.martinez@iver.es> 18/02/2009
 * @author IVER T.I. <http://www.iver.es> 18/02/2009
 * 
 */
public class Statistics {
	protected ResettableIterator<Value> iterator;
	protected BitSet selection = null;
	protected long count = -1;
	protected BigDecimal sum = null;
	protected BigDecimal min = null;
	protected BigDecimal max = null;
	protected BigDecimal mean = null;
	protected BigDecimal variance = null;
	protected BigDecimal stdDeviation = null;

	/**
	 * <p>
	 * Creates a new statistic calculator. Calculations will be performed for
	 * values contained in the iterator.
	 * </p>
	 * 
	 */
	public Statistics(ResettableIterator<Value> iterator) {
		this.iterator = iterator;
	}

	public BigDecimal sum() throws NonNumericFieldException {
		if (sum != null) {
			return sum;
		} else {
			iterator.reset();
			Value value;
			sum = new BigDecimal(0);
			boolean computeMin, computeMax;

			// cache min, max and count values for future use
			computeMin = (min == null);
			computeMax = (max == null);
			count = 0;

			while (iterator.hasNext()) {
				value = iterator.next();
				if (value instanceof NumericValue) {
					double d = ((NumericValue) value).doubleValue();
					sum = sum.add(new BigDecimal(d));
					if (computeMin) {
						computeMin(d);
					}
					if (computeMax) {
						computeMax(d);
					}
					count++;
				} else if (!(value instanceof NullValue)) {
					throw new NonNumericFieldException();
				}
			}
			return sum;
		}
	}

	protected void computeMin(double value) {
		if (min == null) {
			min = new BigDecimal(value);
		} else {
			if (value < min.doubleValue()) {
				min = new BigDecimal(value);
			}
		}
	}

	protected void computeMax(double value) {
		if (max == null) {
			max = new BigDecimal(value);
		} else {
			if (value > max.doubleValue()) {
				max = new BigDecimal(value);
			}
		}
	}

	public BigDecimal min() throws NonNumericFieldException {
		if (min != null) {
			return min;
		} else {
			iterator.reset();
			Value value;

			// cache also Max and count
			boolean computeMax = (max == null);
			count = 0;

			while (iterator.hasNext()) {
				value = iterator.next();
				if (value instanceof NumericValue) {
					double d = ((NumericValue) value).doubleValue();
					computeMin(d);
					if (computeMax) {
						computeMax(d);
					}
					count++;
				} else if (!(value instanceof NullValue)) {
					throw new NonNumericFieldException();
				}
			}
			if (min == null) {
				min = new BigDecimal(0);
			}
			return min;
		}
	}

	public BigDecimal max() throws NonNumericFieldException {
		if (max != null) {
			return max;
		} else {
			iterator.reset();
			Value value;

			// cache also Min and count
			boolean computeMin = (min == null);
			count = 0;

			while (iterator.hasNext()) {
				value = iterator.next();
				if (value instanceof NumericValue) {
					double d = ((NumericValue) value).doubleValue();
					computeMax(d);
					if (computeMin) {
						computeMin(d);
					}
					count++;
				} else if (!(value instanceof NullValue)) {
					throw new NonNumericFieldException();
				}
			}
			if (max == null) {
				max = new BigDecimal(0);
			}
			return max;

		}
	}

	public long count() {
		if (count == -1) {
			Value value;
			count = 0;
			iterator.reset();
			while (iterator.hasNext()) {
				value = iterator.next();
				count++;
			}
		}
		return count;
	}

	public BigDecimal mean() throws NonNumericFieldException {
		if (mean == null) {
			long count = count();
			if (count == 0) {
				mean = new BigDecimal(0);
			} else {
				mean = sum().divide(new BigDecimal(count),
						BigDecimal.ROUND_HALF_DOWN);
			}
		}
		return mean;
	}

	public BigDecimal variance() throws NonNumericFieldException {
		if (variance == null) {
			variance = new BigDecimal(0);
			BigDecimal mean = mean();
			Value value;
			iterator.reset();
			while (iterator.hasNext()) {
				value = iterator.next();
				if (value instanceof NumericValue) {
					double d = ((NumericValue) value).doubleValue();
					BigDecimal dif = new BigDecimal(d).subtract(mean);
					variance = dif.multiply(dif).add(variance);
				}
			}
			if (count() != 0) {
				variance = variance.divide(new BigDecimal(count()),
						BigDecimal.ROUND_HALF_DOWN);
			}
		}
		return variance;
	}

	public BigDecimal stdDeviation() throws NonNumericFieldException {
		if (stdDeviation == null) {
			stdDeviation = new BigDecimal(Math.sqrt(variance().doubleValue()));
		}
		return stdDeviation;
	}

	public class NonNumericFieldException extends BaseException {
		protected Map values() {
			return null;
		}

	}
}
