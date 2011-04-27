/*
 * Created on 18.05.2006 for PIROL
 *
 * SVN header information:
 *  $Author: LBST-PF-3\orahn $
 *  $Rev: 2446 $
 *  $Date: 2006-09-12 14:57:25 +0200 (Di, 12 Sep 2006) $
 *  $Id: ValueRange.java 2446 2006-09-12 14:57:25 +0200 (Di, 12 Sep 2006) LBST-PF-3\orahn $
 */
package org.gvsig.fmap.algorithm.triangulation.pirol.comparisonAndSorting;


/**
 * TODO: comment class
 *
 * @author Ole Rahn
 * <br>
 * <br>FH Osnabr&uuml;ck - University of Applied Sciences Osnabr&uuml;ck,
 * <br>Project: PIROL (2006),
 * <br>Subproject: Daten- und Wissensmanagement
 * 
 * @version $Rev: 2446 $
 * 
 */
public class ValueRange {

    protected double minValue, maxValue;
    protected boolean includingMin = true, includingMax = true;
    
    public ValueRange(double minValue, boolean includeMin, double maxValue, boolean includeMax) {
        this.minValue = minValue;
        this.includingMin = includeMin;
        this.maxValue = maxValue;
        this.includingMax = includeMax;
        
        if (this.minValue > this.maxValue)
            throw new IllegalArgumentException("invalid range, min>max: " + this.minValue + ">" + this.maxValue);
    }
    
    public boolean isIncludingMax() {
        return includingMax;
    }

    public void setIncludingMax(boolean includingMax) {
        this.includingMax = includingMax;
    }

    public boolean isIncludingMin() {
        return includingMin;
    }

    public void setIncludingMin(boolean includingMin) {
        this.includingMin = includingMin;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(double maxValue) {
        if (maxValue != this.maxValue){
            if (maxValue < this.minValue)
                this.minValue = maxValue;
            this.maxValue = maxValue;
        }
    }

    public double getMinValue() {
        return minValue;
    }

    public void setMinValue(double minValue) {
        if (minValue != this.minValue){
            if (minValue > this.maxValue)
                this.maxValue = minValue;
            this.minValue = minValue;
        }
    }



    /**
     *@inheritDoc
     */
    public boolean isResponsibleForValue(Object value) {
        double dValue = ObjectComparator.getDoubleValue(value);
        return this.isResponsibleForValue(dValue);
    }

    /**
     *@inheritDoc
     */
    public boolean isResponsibleForValue(double value) {
        boolean minOk = (this.includingMin)?value>=this.minValue:value>this.minValue;
        boolean maxOk = (this.includingMax)?value<=this.maxValue:value<this.maxValue;
        return minOk && maxOk;
    }


    /**
     *@inheritDoc
     */
    public Comparable getCompareValue() {
        return new Double((this.minValue+this.maxValue)/2.0);
    }

    /**
     *@inheritDoc
     */
    public String toString() {
        return (this.includingMin?"[":"(") + ValueRange.doubleToString(this.minValue, 5) + " - " + ValueRange.doubleToString(this.maxValue,5) + (this.includingMax?"]":")");
    }
    
    /**
     * Creates a String representing a given double with a max. number of <code>postkomma</code> digits after the decimal separator.
     *@param toString double to be represented
     *@param postkomma numbers of postkomma digits in the String
     *@return String representing a given double.
     */
    protected static String doubleToString(double toString, int postkomma){
        String representation = Double.toString(toString);
        int pointPos = representation.indexOf(".");
        
        if (pointPos > -1){
            pointPos += 1;
            int EPos = representation.indexOf("E");
            
            if (EPos < 0) EPos = representation.length();
            
            if (EPos > pointPos + postkomma){
                representation = representation.substring(0, pointPos + postkomma) +  representation.substring(EPos);
            }
        }
        
        return representation;
    }

}
