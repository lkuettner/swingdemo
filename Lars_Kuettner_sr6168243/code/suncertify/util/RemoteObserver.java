/* 
 * @(#)RemoteObserver.java    1.0 21/06/2010 
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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface for observers.
 * 
 * @author Lars Kuettner
 * @version 1.0
 * @see java.util.Observer
 */
public interface RemoteObserver extends Remote {
    /**
     * Called whenever the observed object is changed. An application calls an
     * <code>Observable</code> object's <code>notifyObservers</code> method to
     * have all the object's observers notified of the change.
     * 
     * @param o
     *            the observable object
     * @param arg
     *            an argument passed to the <code>notifyObservers</code> method
     * @throws RemoteException
     *             on network error
     * @see java.util.Observer#update(java.util.Observable, Object)
     */
    void update(RemoteObservable o, Object arg) throws RemoteException;
}
