/* gvSIG. Geographic Information System of the Valencian Government
 *
 * Copyright (C) 2007-2008 Infrastructures and Transports Department
 * of the Valencian Government (CIT)
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
 * 2008 IVER T.I. S.A.   {{Task}}
 */
package org.gvsig.tools.observer;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

//TODO Actualizar Javadoc... sobretodo que se almacenan WeakRef en vez de referencias

public class DefaultObservable implements Observable{

	private boolean propageNotifications=true;
	private boolean inComplex=false;
	private ComplexNotification complexNotification= null;

	private boolean changed = false;
    private Vector obs;

    /** Construct an DefaultObservable with zero Observers. */

    public DefaultObservable() {
		obs = new Vector();
		this.inComplex=false;
    }

    /**
     * Adds an observer to the set of observers for this object, provided
     * that it is not the same as some observer already in the set.
     * The order in which notifications will be delivered to multiple
     * observers is not specified. See the class comment.
     *
     * @param   o   an observer to be added.
     * @throws NullPointerException   if the parameter o is null.
     */
    public synchronized void addObserver(Observer o) {
        this.addObserver(new WeakReference(o));

    }

    public synchronized void addObserver(Reference ref) {
        if (ref == null || ref.get() == null) {
			throw new NullPointerException();
		}
        Observer o = (Observer)ref.get();
		if (!contains(o)) {
	    	obs.addElement(ref);
		}
    }

    public synchronized void addObservers(DefaultObservable o){
    	Iterator iter = o.obs.iterator();
    	o.clearDeadReferences();
    	while (iter.hasNext()){
    		this.addObserver((Reference) iter.next());
    	}

    }

    private boolean contains(Observer o){
    	if (obs.contains(o)){
    		return true;
    	}
        Iterator iter = obs.iterator();
        Object obj;
        Reference ref1;
        while (iter.hasNext()){
        	obj = iter.next();
        	if (obj instanceof Reference){
        		ref1 = (Reference)obj;
        		if (o.equals(ref1.get())){
        			return true;
        		}
        	}
        }
        return false;
    }

    /**
     * Deletes an observer from the set of observers of this object.
     * Passing <CODE>null</CODE> to this method will have no effect.
     * @param   o   the observer to be deleted.
     */
    public synchronized void deleteObserver(Observer o) {
        if (!obs.removeElement(o)){
        	this.deleteObserverReferenced(o);
        }
    }

    private void deleteObserverReferenced(Observer o){
        Iterator iter = obs.iterator();
        Object obj;
        ArrayList toRemove = new ArrayList();
        Reference ref1;
        while (iter.hasNext()){
        	obj = iter.next();
        	if (obj instanceof Reference){
        		ref1 = (Reference)obj;
        		if (ref1.get() == null || o.equals(ref1.get())){
        			toRemove.add(obj);
        		}
        	}
        }
        iter = toRemove.iterator();
        while (iter.hasNext()){
        	obs.remove(iter.next());
        }
    }

    public synchronized void deleteObserver(Reference ref) {
    	Observer o = (Observer)ref.get();
        obs.removeElement(ref);
        if (o == null) {
			return;
		}
        deleteObserverReferenced(o);
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to
     * indicate that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and <code>null</code>. In other
     * words, this method is equivalent to:
     * <blockquote><tt>
     * notifyObservers(null)</tt></blockquote>
     *
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void notifyObservers() {
    	throw new UnsupportedOperationException("Notify requires an notification Object");
    }

    /**
     * If this object has changed, as indicated by the
     * <code>hasChanged</code> method, then notify all of its observers
     * and then call the <code>clearChanged</code> method to indicate
     * that this object has no longer changed.
     * <p>
     * Each observer has its <code>update</code> method called with two
     * arguments: this observable object and the <code>arg</code> argument.
     *
     * @param   arg   any object.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#hasChanged()
     * @see     java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void notifyObservers(Observable observable,Object arg) {
    	if (!this.inComplex){
			this.setChanged();
			notify(observable,arg);
		}else{
			complexNotification.addNotification(arg);
		}

    }

    /**
     * Clears the observer list so that this object no longer has any observers.
     */
    public synchronized void deleteObservers() {
    	obs.removeAllElements();
    }

    /**
     * Marks this <tt>DefaultObservable</tt> object as having been changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
    protected synchronized void setChanged() {
    	changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has
     * already notified all of its observers of its most recent change,
     * so that the <tt>hasChanged</tt> method will now return <tt>false</tt>.
     * This method is called automatically by the
     * <code>notifyObservers</code> methods.
     *
     * @see     java.util.Observable#notifyObservers()
     * @see     java.util.Observable#notifyObservers(java.lang.Object)
     */
    protected synchronized void clearChanged() {
    	changed = false;
    }

    /**
     * Tests if this object has changed.
     *
     * @return  <code>true</code> if and only if the <code>setChanged</code>
     *          method has been called more recently than the
     *          <code>clearChanged</code> method on this object;
     *          <code>false</code> otherwise.
     * @see     java.util.Observable#clearChanged()
     * @see     java.util.Observable#setChanged()
     */
    public synchronized boolean hasChanged() {
    	return changed;
    }

    /**
     * Returns the number of observers of this <tt>DefaultObservable</tt> object.
     *
     * @return  the number of observers of this object.
     */
    public synchronized int countObservers() {
    	clearDeadReferences();
    	return obs.size();
    }

	public void enableNotifications(){
		clearDeadReferences();
		this.propageNotifications =true;
	}

	public void diableNotifications(){
		this.propageNotifications =false;
	}

	public boolean isEnabledNotifications(){
		return this.propageNotifications;
	}

	public boolean inComplex(){
		return this.inComplex;
	}

	public void beginComplexNotification(ComplexNotification complex){
		clearDeadReferences();
		this.clearChanged();
		inComplex=true;
		complexNotification=complex;
		complexNotification.clear();
	}

	public void endComplexNotification(Observable observable){
		inComplex=false;
		this.setChanged();

		Iterator iter=complexNotification.getIterator();
		while(iter.hasNext()){
			notify(observable,iter.next());
		}
		complexNotification = null;
	}

	protected synchronized void clearDeadReferences(){
        Iterator iter = obs.iterator();
        Object obj;
        ArrayList toRemove = new ArrayList();
        Reference ref1;
        while (iter.hasNext()){
        	obj = iter.next();
        	if (obj instanceof Reference){
        		ref1 = (Reference)obj;
        		if (ref1.get() == null){
        			toRemove.add(obj);
        		}
        	}
        }
        iter = toRemove.iterator();
        while (iter.hasNext()){
        	obs.remove(iter.next());
        }

	}

	private void notify(Observable observable, Object object) {
		/*
         * a temporary array buffer, used as a snapshot of the state of
         * current Observers.
         */
        Object[] arrLocal;

	synchronized (this) {
	    /* We don't want the Observer doing callbacks into
	     * arbitrary code while holding its own Monitor.
	     * The code where we extract each DefaultObservable from
	     * the Vector and store the state of the Observer
	     * needs synchronization, but notifying observers
	     * does not (should not).  The worst result of any
	     * potential race-condition here is that:
	     * 1) a newly-added Observer will miss a
	     *   notification in progress
	     * 2) a recently unregistered Observer will be
	     *   wrongly notified when it doesn't care
	     */
	    if (!changed) {
			return;
		}
            arrLocal = obs.toArray();
            clearChanged();
        }

		Object obj;
		Observer observer;
		ArrayList toRemove = new ArrayList();
        for (int i = arrLocal.length-1; i>=0; i--){
        	obj = arrLocal[i];
        	observer = (Observer)((Reference)obj).get();
        	if (observer == null){
        		toRemove.add(obj);
        		continue;
        	}
            observer.update(observable, object);
        }

        Iterator iter = toRemove.iterator();
        while (iter.hasNext()){
        	obs.remove(iter.next());
        }

	}
}
