package com.iver.cit.gvsig.project.documents.layout.fframes;

import java.security.KeyException;
import java.util.Iterator;
import java.util.Map;

import com.iver.andami.PluginServices;
import com.iver.utiles.extensionPoints.ExtensionPoint;
import com.iver.utiles.extensionPoints.ExtensionPoints;
import com.iver.utiles.extensionPoints.ExtensionPointsSingleton;
import com.iver.utiles.extensionPoints.IExtensionBuilder;

/**
 * Factory of FFrame.
 *
 * @author Vicente Caballero Navarro
 */
public abstract class FrameFactory implements IExtensionBuilder {

	public static IFFrame createFrameFromName(String s) {
		ExtensionPoints extensionPoints =
			ExtensionPointsSingleton.getInstance();

		ExtensionPoint extensionPoint =(ExtensionPoint)extensionPoints.get("FFrames");
		Iterator iterator = extensionPoint.keySet().iterator();
		while (iterator.hasNext()) {
			try {
				FrameFactory frameFactory = (FrameFactory)extensionPoint.create((String)iterator.next());
				if (frameFactory.getRegisterName().equals(s)) {
					IFFrame frame=frameFactory.createFrame();
					if (frame == null) return null;
					frame.setFrameLayoutFactory(frameFactory);
					return frame;
				}
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


    /**
     * Returns the name of FFrame.
     *
     * @return Name of fframe.
     */
    public String getNameType() {
        return PluginServices.getText(this, "frame");
    }

    /**
     * Create a new IFFrame.
     *
     * @return IFFrame.
     */
    public abstract IFFrame createFrame();

    /**
     * Returns the name of registration in the point of extension.
     *
     * @return Name of registration
     */
    public abstract String getRegisterName();

    /**
     * Create a FrameLayoutFactory.
     *
     * @return FrameLayoutFactory.
     */
    public Object create() {
        return this;
    }

    /**
     * Create a FrameLayoutFactory.
     *
     * @param args
     *
     * @return FrameLayoutFactory.
     */
    public Object create(Object[] args) {
        return this;
    }

    /**
     * Create a FrameLayoutFactory.
     *
     * @param args
     *
     * @return FrameLayoutFactory.
     */
    public Object create(Map args) {
        return this;
    }

    /**
     * Registers in the points of extension the Factory with alias.
     *
     * @param registerName Register name.
     * @param obj Class of register.
     * @param alias Alias.
     */
    public static void register(String registerName, Object obj, String alias) {
        ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
        extensionPoints.add("FFrames", registerName, obj);

        ExtensionPoint extPoint = ((ExtensionPoint) extensionPoints.get(
                "FFrames"));

        try {
            extPoint.addAlias(registerName, alias);
        } catch (KeyException e) {
            e.printStackTrace();
        }
    }

    /**Registers in the points of extension the Factory
     *
     * @param registerName Register name.
     * @param obj Class of register.
     */
    public static void register(String registerName, Object obj) {
        ExtensionPoints extensionPoints = ExtensionPointsSingleton.getInstance();
        extensionPoints.add("FFrames", registerName, obj);
    }
}
