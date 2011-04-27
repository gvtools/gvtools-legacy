/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Gobernment (CIT)
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 */

/*
 * AUTHORS (In addition to CIT):
 * 2008 {DiSiD Technologies}   {Create a base Locator implementation}
 */
package org.gvsig.tools.locator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.gvsig.tools.extensionPoint.ExtensionPoint;
import org.gvsig.tools.extensionPoint.ExtensionPoints;
import org.gvsig.tools.extensionPoint.ExtensionPointsSingleton;

/**
 * Locator implementation based on the use of the ExtensionPoints.
 * 
 * @author <a href="mailto:cordin@disid.com">Cèsar Ordiñana</a>
 */
public abstract class AbstractLocator implements Locator {

    private Map instances = new HashMap();

    private Object lock = new Object();

    public Object get(String name) throws LocatorException {
        Object instance = null;

        // Synchronize the creation and storage of instances
        synchronized (lock) {
            instance = instances.get(name);
            if (instance == null) {
                try {
                    instance = getExtensionPoint().create(name);
                } catch (Exception ex) {
                    throw new LocatorException(ex, name, this);
                }
                instances.put(name, instance);
            }
        }

        return instance;
    }

    public String[] getNames() {
        ExtensionPoint extensionPoint = getExtensionPoint();
        Set names = extensionPoint.keySet();
        return names == null || names.size() == 0 ? null
                : (String[]) names
                .toArray(new String[names.size()]);
    }

    public void register(String name, Class clazz) {
        ExtensionPointsSingleton.getInstance()
                .add(getLocatorName(), name, clazz);
    }

    public void register(String name, String description, Class clazz) {
        ExtensionPointsSingleton.getInstance().add(getLocatorName(), name,
                description, clazz);
    }

    public void register(String name, LocatorObjectFactory factory) {
        ExtensionPointsSingleton.getInstance().add(getLocatorName(), name,
                factory);
    }

    public void register(String name, String description,
            LocatorObjectFactory factory) {
        ExtensionPointsSingleton.getInstance().add(getLocatorName(), name,
                description, factory);
    }
    
    public String toString() {
        return getLocatorName();
    }

    /**
     * Returns the ExtensionPoint to use for the Locator values.
     */
    private ExtensionPoint getExtensionPoint() {
        ExtensionPoints extensionPoints = ExtensionPointsSingleton
                .getInstance();
        String moduleName = getLocatorName();
        // synchronize the retrieval of the ExtensionPoint
        synchronized (lock) {

            ExtensionPoint extensionPoint = (ExtensionPoint) extensionPoints
                    .get(moduleName);

            if (extensionPoint == null) {
                extensionPoint = new ExtensionPoint(moduleName);
                extensionPoints.put(moduleName, extensionPoint);
            }

            return extensionPoint;
        }
    }
}