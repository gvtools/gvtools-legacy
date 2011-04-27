package com.iver.cit.gvsig.fmap.core;

import java.rmi.server.UID;

import com.hardcode.gdbms.engine.values.NullValue;
import com.hardcode.gdbms.engine.values.Value;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public class DefaultRow implements IRow {
    private Value[] attributes;
    private String id;

    

    /**
     * FROM GEOTOOLS: Creates an ID from a hashcode.
     *
     * @return an id for the feature.
     */
    String defaultID() {
        return "fid-" + (new UID()).toString();
    }
    
    /**
     * Crea un nuevo DefaultRow.
     *
     * @param att DOCUMENT ME!
     */
    public DefaultRow(Value[] att) {
        this.attributes = att;
        this.id = defaultID();
    }

    /**
     * Crea un nuevo DefaultRow.
     *
     * @param att DOCUMENT ME!
     * @param id DOCUMENT ME!
     */
    public DefaultRow(Value[] att, String id) {
        this.attributes = att;
        this.id = id;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getID() {
        return id;
    }

    /**
     * DOCUMENT ME!
     *
     * @param fieldIndex DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Value getAttribute(int fieldIndex) {
    	if (attributes == null)
    		return new NullValue();
    	if (fieldIndex >= attributes.length)
    		return new NullValue();
        return attributes[fieldIndex];
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Value[] getAttributes() {
        return attributes;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public IRow cloneRow() {
        Value[] values = new Value[attributes.length];

        if (attributes != null) {
            values = (Value[]) attributes.clone();
        }

        DefaultRow dr = new DefaultRow(values, id);

        return dr;
    }
    
	public void setID(String ID) {
		id = ID;
	}

	public void setAttributes(Value[] att) {
		this.attributes = att;
	}
    
}
