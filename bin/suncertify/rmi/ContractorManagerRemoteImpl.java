/* 
 * @(#)ContractorManagerRemoteImpl.java    1.0 21/06/2010 
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

package suncertify.rmi;

import suncertify.services.BookResult;
import suncertify.services.Contractor;
import suncertify.services.ContractorManager;
import suncertify.services.ServicesException;
import suncertify.util.Observable;
import suncertify.util.RemoteObserver;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

/**
 * Server-side skeleton implementation of the remote contractor manager. Wrapper
 * for the {@link ContractorManager} where the real work is relayed to.
 * <p/>
 * In addition: (i) Throws RemoteException (unlike
 * <code>ContractorManager</code> which doesn't know anything about remote
 * stuff). (ii) Remote observable being observed by client views and used to
 * broadcast the termination message.
 *
 * @author Lars Kuettner
 * @version 1.0
 * @see ContractorManagerConnector
 */
public class ContractorManagerRemoteImpl extends UnicastRemoteObject implements
  ContractorManagerRemote
{
  /**
   * A magic version number for this class so that serialization can occur
   * without worrying about the underlying class changing between
   * serialization and deserialization.
   */
  private static final long serialVersionUID = 4711L;

  /**
   * The contractor manager where all the real work is done.
   */
  private ContractorManager contractorManager;

  /**
   * An embedded observable where clients may register for the termination
   * message.
   */
  private EmbeddedObservable observable = new EmbeddedObservable();

  /**
   * Creates an instance of the remote contractor manager implementation
   * class. This instance is to play the role of the server-side model in the
   * overall model-view-controller (MVC) setting.
   *
   * @param databaseLocation the path name to the database file
   * @throws ServicesException on any database-related error
   * @throws RemoteException   on network error
   */
  public ContractorManagerRemoteImpl(final String databaseLocation)
    throws ServicesException, RemoteException
  {
    contractorManager = new ContractorManager(databaseLocation);
  }

  /**
   * Terminates the remote contractor manager implementation instance. Entails
   * broadcasting a notification message about the imminent server shutdown to
   * all registered observers (typically network clients). Moreover, the
   * embedded contractor manager is explicitly terminated.
   */
  public final void terminate()
  {
    // Broadcast a termination notification to all registered obsrvers
    observable.broadcastTerminate();

    // Explicitly terminate the embedded (non-remote) contractor manager
    contractorManager.terminate();
  }

  /**
   * The embedded <code>RemoteObservable</code>.
   *
   * @author Lars Kuettner
   * @version 1.0
   */
  private class EmbeddedObservable extends Observable
  {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * Creates an instance of an embedded observable.
     *
     * @throws RemoteException on network error
     */
    public EmbeddedObservable() throws RemoteException
    {
      super();
    }

    /**
     * Broadcasts the termination (server shutdown) message to all
     * registered observers (typically network clients).
     */
    public void broadcastTerminate()
    {
      setChanged();
      notifyObservers();
    }
  }

  @Override
  public final Map<Long, Contractor> search(final String name,
                                            final String location)
    throws ServicesException, RemoteException
  {
    return contractorManager.search(name, location);
  }

  @Override
  public final BookResult book(final long recNo, final Contractor contractor)
    throws ServicesException, RemoteException
  {
    return contractorManager.book(recNo, contractor);
  }

  @Override
  public final void addObserver(final RemoteObserver ob)
    throws RemoteException
  {
    observable.addObserver(ob);
  }

  @Override
  public final int countObservers() throws RemoteException
  {
    return observable.countObservers();
  }

  @Override
  public final void deleteObserver(final RemoteObserver ob)
    throws RemoteException
  {
    observable.deleteObserver(ob);
  }

  @Override
  public final void deleteObservers() throws RemoteException
  {
    observable.deleteObservers();

  }

  @Override
  public final boolean hasChanged() throws RemoteException
  {
    return observable.hasChanged();
  }

  @Override
  public final void notifyObservers() throws RemoteException
  {
    observable.notifyObservers();
  }

  @Override
  public final void notifyObservers(final Remote r) throws RemoteException
  {
    observable.notifyObservers(r);
  }

  @Override
  public final void notifyObservers(final Serializable s)
    throws RemoteException
  {
    observable.notifyObservers(s);
  }
}
