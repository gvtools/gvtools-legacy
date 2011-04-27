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
package com.iver.utiles.connections;


/**
 * Class of data connection.
 *
 * @author Vicente Caballero Navarro
 */
public class ConnectionTrans extends ConnectionSettings {
    private String password;
    private boolean savePassword = true;
    private boolean connected = false;
    private String connBeginning;

    /**
     * Returns password.
     *
     * @return Password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Inserts the password.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns a string that represents de connection.
     *
     * @return string of connection.
     */
    public String toString() {
        return getName() + " (" + getDriver() + ") <" + getDb() + ">";
    }

    protected boolean isEquals(String s1,String s2){
    	if (s1==null && s2 ==null)
    		return true;
    	if ((s1==null || s2==null))
    		return false;
    	if (!s1.equals(s2)) {
            return false;
        }
    	return true;
    }
    /**
     * Verifies if are equals two connections
     *
     * @param arg0 Connection
     *
     * @return True if is equals.
     */
    public boolean equals(Object arg0) {
        if (arg0 instanceof ConnectionTrans) {
            ConnectionTrans ct = (ConnectionTrans) arg0;

            if (!isEquals(ct.getDriver(),this.getDriver())) {
                return false;
            }

            if (!isEquals(ct.getName(),this.getName())) {
                return false;
            }

            if (!isEquals(ct.getHost(),this.getHost())) {
                return false;
            }

            if (!isEquals(ct.getPort(),this.getPort())) {
                return false;
            }

            if (!isEquals(ct.getUser(),this.getUser())) {
                return false;
            }

            //if (ct.isSavePassword()!=this.isSavePassword()) return false;
            if (ct.isSavePassword() && 
                    !isEquals(ct.getPassword(),this.getPassword())) {
                return false;
            }

            if (!isEquals(ct.getDb(),this.getDb())) {
                return false;
            }
            if (!isEquals(ct.getConnBeginning(),this.getConnBeginning())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if the password has been kept.
     *
     * @return True if has been kept
     */
    public boolean isSavePassword() {
        return savePassword;
    }

    /**
     * Inserts the password
     *
     * @param savePassword Password
     */
    public void setSavePassword(boolean savePassword) {
        this.savePassword = savePassword;
    }

    /**
     * Returns true if is connected.
     *
     * @return True if is connected.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Inserts true if is connected.
     *
     * @param connected true if is connected.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

	public String getConnBeginning() {
		return connBeginning;
	}

	public void setConnBegining(String connBegining) {
		this.connBeginning = connBegining;
	}
}
