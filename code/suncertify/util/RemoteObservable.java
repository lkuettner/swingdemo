/* 
 * @(#)RemoteObservable.java    1.0 21/06/2010 
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

/**
 * Remote interface for the Observable class.
 *
 * @author Lars Kuettner
 * @version 1.0
 * @see java.util.Observable
 */
public interface RemoteObservable extends Remote
{
  /**
   * Adds an observer to the set of observers for an observable object,
   * provided that it is not the same as some observer already in the set. The
   * order in which notifications will be delivered to multiple observers is
   * not specified.
   *
   * @param ob an observer to be added
   * @throws NullPointerException if the parameter <code>ob</code> is null
   * @throws RemoteException      on network error
   * @see java.util.Observable#addObserver(Observer)
   */
  void addObserver(RemoteObserver ob) throws RemoteException;

  /**
   * Deletes an observer from the set of observers of of an observable object.
   * Passing <code>null</code> to this method will have no effect.
   *
   * @param ob the observer to be deleted
   * @throws RemoteException on network error
   * @see java.util.Observable#deleteObserver(Observer)
   */
  void deleteObserver(RemoteObserver ob) throws RemoteException;

  /**
   * Clears the observer list so that the observable object no longer has any
   * observers.
   *
   * @throws RemoteException on network error
   * @see java.util.Observable#deleteObservers()
   */
  void deleteObservers() throws RemoteException;

  /**
   * Returns the number of observers of an observable object.
   *
   * @return the number of observers
   * @throws RemoteException on network error
   * @see java.util.Observable#countObservers()
   */
  int countObservers() throws RemoteException;

  /**
   * If the observable object has changed, as indicated by the
   * <code>hasChanged</code> method, then notify all of its observers and then
   * call the <code>clearChanged</code> method to indicate that the object has
   * no longer changed.
   * <p/>
   * This method will notify its observers with <code>null</code> as an
   * argument.
   *
   * @throws RemoteException on network error
   * @see java.util.Observable#notifyObservers()
   */
  void notifyObservers() throws RemoteException;

  /**
   * If the observable object has changed, as indicated by the
   * <code>hasChanged</code> method, then notify all of its observers and then
   * call the <code>clearChanged</code> method to indicate that the object has
   * no longer changed.
   * <p/>
   * Notifies observers of a change with the specified remote reference as an
   * argument.
   *
   * @param r the remote object to be passed to the observers
   * @throws RemoteException on network error
   * @see java.util.Observable#notifyObservers(Object)
   */
  void notifyObservers(Remote r) throws RemoteException;

  /**
   * If the observable object has changed, as indicated by the
   * <code>hasChanged</code> method, then notify all of its observers and then
   * call the <code>clearChanged</code> method to indicate that the object has
   * no longer changed.
   * <p/>
   * Notifies observers of a change with the specified serializable reference
   * as an argument.
   *
   * @param s the serializable object to be sent to the observers
   * @throws RemoteException on network error
   * @see java.util.Observable#notifyObservers(Object)
   */
  void notifyObservers(Serializable s) throws RemoteException;

  /**
   * Tests if the observable object has changed.
   *
   * @return <code>true</code> if and only if the <code>setChanged</code>
   *         method has been called more recently than the
   *         <code>clearChanged</code> method on the observable object;
   *         <code>false</code> otherwise.
   * @throws RemoteException on network error
   * @see java.util.Observable#hasChanged()
   */
  boolean hasChanged() throws RemoteException;
}
