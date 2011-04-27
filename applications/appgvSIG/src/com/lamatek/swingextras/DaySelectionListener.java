package com.lamatek.swingextras;

/**
 * Interface used by {@link com.lamatek.swingextras.JDateChooser JDateChooser}
 * to notify when the user has selected a date.
 */
public interface DaySelectionListener {
	
	/**
	 * Called when a user selects a date.
	 */
	public void daySelected(int day);
	
}