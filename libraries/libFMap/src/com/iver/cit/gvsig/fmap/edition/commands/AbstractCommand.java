package com.iver.cit.gvsig.fmap.edition.commands;

import java.util.GregorianCalendar;


/**
 * DOCUMENT ME!
 *
 * @author Vicente Caballero Navarro
 */
public abstract class AbstractCommand implements Command {
    private String description;
    private int hour;
    private int minute;
    private int second;
    private int day;
    private int month;
    private int year;

    /**
     * Crea un nuevo AbstractCommand.
     */
    public AbstractCommand() {
        GregorianCalendar calendario = new GregorianCalendar();
        year = calendario.get(GregorianCalendar.YEAR);
        month = calendario.get(GregorianCalendar.MONTH);
        day = calendario.get(GregorianCalendar.DAY_OF_MONTH);
        hour = calendario.get(GregorianCalendar.HOUR_OF_DAY);
        minute = calendario.get(GregorianCalendar.MINUTE);
        second = calendario.get(GregorianCalendar.SECOND);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return description;
    }

    /**
     * DOCUMENT ME!
     *
     * @param descrip DOCUMENT ME!
     */
    public void setDescription(String descrip) {
        description = descrip;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDate() {
        return day + "/" + month + "/" + year;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTime() {
        return hour + ":" + minute + ":" + second;
    }
    public String toString() {
		return this.getType()+" "+ this.getDescription()+" "+this.getDate()+" "+this.getTime();
	}
}
