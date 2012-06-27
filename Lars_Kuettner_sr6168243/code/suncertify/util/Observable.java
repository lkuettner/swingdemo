/* 
 * @(#)Observable.java    1.0 21/06/2010 
 *  
 * Candidate: Lars Kuettner 
 * Prometric ID: sr6168243 
 * Candidate ID: SUN581781 
 *  
 * Sun Certified Developer for Java 2 Platform, Standard Edition Programming 
 * Assignment (CX-310-252A) 
 *  
 * This class is part of the Programming Assignment of the Sun Certified 
 * Developer for Java 2 Platform, Standard Edition certification program, must 
 * not be used out of this context and may be used exclusively by Sun 
 * Microsystems.
 */

package suncertify.util;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Distributed version of the standard Java Observable class that uses the exact
 * same API as the Java class. Implements <code>RemoteObservable</code> rather
 * than <code>Observable</code> - the otherwise exact same methods in
 * <code>RemoteObservable</code> all throw <code>RemoteException</code>s.
 * 
 * @author Lars Kuettner
 * @version 1.0
 * @see java.util.Observable
 */
public class Observable
	extends UnicastRemoteObject implements RemoteObservable {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
     private static final long serialVersionUID = 4711L;

     /** Logger object to log messages in the scope of this class. */
    private static final Logger LOG =
	Logger.getLogger(Observable.class.getName());

    /**
     * Ready for notification of its observers.
     */
    private boolean changed = false;

    /**
     * Collection of <code>RemoteObserver</code>s.
     */
    private Vector<RemoteObserver> observers = new Vector<RemoteObserver>();

    /**
     * Creates a new observable.
     * 
     * @throws RemoteException
     *             on network error
     * @see java.util.Observable#Observable()
     */
    public Observable() throws RemoteException {
	super();
    }

    /**
     * Marks this <code>Observable</code> object as having been changed; the
     * <code>hasChanged</code> method will now return <code>true</code>.
     * 
     * @see java.util.Observable#setChanged()
     */
    protected final synchronized void setChanged() {
	changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has already
     * notified all of its observers of its most recent change, so that the
     * <code>hasChanged</code> method will now return <code>false</code>. This
     * method is called automatically by the <code>notifyObservers</code>
     * methods.
     * 
     * @see java.util.Observable#clearChanged()
     */
    protected final synchronized void clearChanged() {
	changed = false;
    }

    @Override
    public final synchronized boolean hasChanged() {
	return changed;
    }

    @Override
    public final synchronized int countObservers() {
	return observers.size();
    }

    @Override
    public final synchronized void addObserver(final RemoteObserver ob) {
	if (!observers.contains(ob)) {
	    observers.addElement(ob);
	}
    }

    @Override
    public final synchronized void deleteObserver(final RemoteObserver ob) {
	if (observers.contains(ob)) {
	    observers.removeElement(ob);
	}
    }

    @Override
    public final synchronized void deleteObservers() {
	observers = new Vector<RemoteObserver>();
    }

    @Override
    public final void notifyObservers() {
	doNotify(null);
    }

    @Override
    public final void notifyObservers(final Remote r) {
	doNotify(r);
    }

    @Override
    public final void notifyObservers(final Serializable s) {
	doNotify(s);
    }

    /**
     * Performs actual observer notification. It first copies the observers
     * Vector into an array. This allows it to move out of the synchronized
     * block to avoid deadlocks and avoid holding up other threads while it
     * notifies observers across potentially nasty Internet links.
     * 
     * @param o
     *            the object (remote, serializable, null) that is passed to the
     *            observers' <code>update</code> method as the second argument
     */
    protected final void doNotify(final Object o) {
	RemoteObserver[] obs;
	int count;

	synchronized (this) {
	    if (!hasChanged()) {
		return;
	    }
	    count = observers.size();
	    obs = new RemoteObserver[count];
	    observers.copyInto(obs);
	    clearChanged();
	}
	for (int i = 0; i < count; i++) {
	    if (obs[i] != null) {
		try {
		    obs[i].update(this, o);
		} catch (RemoteException e) {
		    LOG.log(Level.WARNING, "Can't update observer: "
			    + e.getMessage(), e);
		}
	    }
	}
    }
}
