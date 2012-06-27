/* 
 * @(#)ServerUtilities.java    1.0 21/06/2010 
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

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;

import suncertify.services.ServicesException;

/**
 * Server-side utility class to start and to terminate a server.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public final class ServerUtilities {

    /** Logger object to log messages in the scope of this class. */
    private static final Logger LOG = Logger.getLogger(ServerUtilities.class
	    .getName());

    /** A reference to the RMI registry. */
    private static Registry registry = null;

    /** The contractor manager remote implementation of the RMI skeleton. */
    private static ContractorManagerRemoteImpl contractorManagerRemote = null;

    /**
     * Prohibits instantiation as this is a utility class.
     */
    private ServerUtilities() {
    }

    /**
     * Creates an implementation instance of the ContractorManagerRemote
     * interface and binds it to the name ContractorManager. Called by server to
     * register the ContractorManager with the RMI framework.
     * 
     * @param databaseLocation
     *            the location of the database file on disk
     * @param port
     *            the port the RMI registry will listen on. System default is
     *            java.rmi.registry.Registry.REGISTRY_PORT.
     * @throws LaunchServerException
     *             if the server can't be launched for whatever reason
     */
    public static synchronized void startServer(final String databaseLocation,
	    final String port) throws LaunchServerException {
	Registry r = null;
	ContractorManagerRemoteImpl cmri = null;
	try {
	    // This really should never ever fail as the input field is checked.
	    int portNo = Integer.parseInt(port);
	    // Locate registry.
	    r = LocateRegistry.createRegistry(portNo);

	    // Start server. Throws its own exception(s).
	    cmri = new ContractorManagerRemoteImpl(databaseLocation);

	    // Bind to service name which is ContractorManager.
	    r.rebind(Text.RMI_SERVICE_NAME, cmri);

	} catch (NumberFormatException e) {
	    // Should not occur as the port number has already been verified.
	    LOG.log(Level.SEVERE,
		    String.format(Text.INVALID_PORT_NUMBER, port), e);
	    throw new LaunchServerException(String.format(
		    Text.INVALID_PORT_NUMBER, port)
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	} catch (AccessException e) {
	    LOG.log(Level.SEVERE, Text.ACCESS_DENIED_TO_REBIND_SERVICE, e);
	    throw new LaunchServerException(Text.ACCESS_DENIED_TO_REBIND_SERVICE
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	} catch (RemoteException e) {
	    // We cannot start the registry. Since we have not defined our
	    // classpath, we cannot easily attach to an already running
	    // registry.
	    LOG.log(Level.SEVERE, Text.CANT_CREATE_OR_REBIND_REGISTRY, e);
	    throw new LaunchServerException(Text.CANT_CREATE_OR_REBIND_REGISTRY
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	} catch (ServicesException e) {
	    LOG.log(Level.SEVERE, Text.CANT_CREATE_CONTRACTOR_MANAGER, e);
	    throw new LaunchServerException(Text.CANT_CREATE_CONTRACTOR_MANAGER
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	}
	registry = r;
	contractorManagerRemote = cmri;
    }

    /**
     * Terminates the server. Called upon exiting the server application.
     */
    public static synchronized void terminate() {
	// There is a slim chance that the application is terminated prior to
	// registration (if an error occurred) - therefore the synchronization
	// Broadcast server termination event info to all the clients.
	if (contractorManagerRemote != null) {
	    contractorManagerRemote.terminate();
	}

	if (registry != null) {
	    try {
		registry.unbind(Text.RMI_SERVICE_NAME);
	    } catch (Exception e) {
		LOG.log(Level.WARNING, "Can't unbind registry from "
			+ Text.RMI_SERVICE_NAME, e);
	    }
	    registry = null;
	}
    }
}