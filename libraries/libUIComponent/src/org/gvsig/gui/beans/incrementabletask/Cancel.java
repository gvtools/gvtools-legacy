package org.gvsig.gui.beans.incrementabletask;

public class Cancel implements Cancellable {
	private boolean cancel = false;

	/**
	 * Crea un nuevo CancelDraw.
	 */
	public Cancel() {
	}

	/**
	 * Insertar si se debe cancelar el dibujado.
	 * 
	 * @param b
	 *            true si se debe cancelar el dibujado.
	 */
	public void setCanceled(boolean b) {
		cancel = b;
	}

	/**
	 * @see com.iver.utiles.swing.threads.Cancellable#isCanceled()
	 */
	public boolean isCanceled() {
		return cancel;
	}

}
