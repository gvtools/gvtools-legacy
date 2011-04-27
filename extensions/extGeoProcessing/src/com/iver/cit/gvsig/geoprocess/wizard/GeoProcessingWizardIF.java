package com.iver.cit.gvsig.geoprocess.wizard;

/**
 * Models Wizard functionality of GeoProcessingPanel
 * GUI component.
 * 
 * @author azabala
 *
 */
public interface GeoProcessingWizardIF {
	/**
	 * It closes wizard dialog.
	 */
	public void closeDialog();
	/**
	 * Shows previous wizard step
	 */
	public void previousStep();
	/**
	 * Shows next wizard step.
	 *
	 */
	public void nextStep();
}
