/* 
 * @(#)ContractorManagerConnector.java    1.0 21/06/2010 
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

import suncertify.services.ContractorManager;
import suncertify.services.ServicesException;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * Client-side utility class providing static methods for either a network or
 * standalone client to access the contractor manager (local or remote) which,
 * in the role of the model in the MVC, implements the business services
 * interface.
 *
 * @author Lars Kuettner
 * @version 1.0
 * @see ServerUtilities
 * @see ContractorManagerRemoteImpl
 */
public final class ContractorManagerConnector
{

  /**
   * Private constructor to disallow instantiation since this is a utility
   * class.
   */
  private ContractorManagerConnector()
  {
  }

  /**
   * Factory method to get a new instance of the contractor manager
   * (implementing {@link suncertify.services.BusinessServices}) for the
   * standalone client.
   *
   * @param databaseLocation a string representing the path name to the database file
   * @return the new instance of the contractor manager
   * @throws ServicesException if the Singleton instance of the data access class could not
   *                           be obtained
   */
  public static ContractorManager getLocal(final String databaseLocation)
    throws ServicesException
  {
    return new ContractorManager(databaseLocation);
  }

  /**
   * Factory method to get a <code>ContractorManagerRemote</code> stub and
   * with it an implementation of the
   * {@link suncertify.services.BusinessServices} interface. Called by network
   * client in remote setting.
   *
   * @param hostname a string representing the host name or IP address of the
   *                 server
   * @param port     the RMI port number in string representation
   * @return the looked-up contractor manager remote object
   * @throws RemoteException if the lookup failed because of a malformed URL or because
   *                         the RMI service has not been bound to the given hostname and
   *                         port
   */
  public static ContractorManagerRemote getRemote(
    final String hostname, final String port) throws RemoteException
  {
    String url = "rmi://" + hostname + ":" + port + "/"
      + Text.RMI_SERVICE_NAME;
    ContractorManagerRemote cmr = null;
    try
    {
      cmr = (ContractorManagerRemote) Naming.lookup(url);
    }
    catch (MalformedURLException e)
    {
      throw new RemoteException(String.format(Text.CANT_CONNECT_TO_URL,
        url)
        + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
    }
    catch (NotBoundException e)
    {
      throw new RemoteException(String.format(
        Text.SERVICE_NAME_NOT_REGISTERED, Text.RMI_SERVICE_NAME)
        + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
    }
    return cmr;
  }
}