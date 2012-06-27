/* 
 * @(#)NetworkClientView    1.0 21/06/2010 
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

package suncertify.gui;

import suncertify.rmi.ContractorManagerConnector;
import suncertify.rmi.ContractorManagerRemote;
import suncertify.util.RemoteObservable;
import suncertify.util.RemoteObserver;

import javax.swing.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The view for the network client, wrapping the client main frame as the window
 * actually being displayed. Creates and displays the client main frame,
 * connects to the contractor manager based on the information retrieved from
 * the likewise-managed configuration parameters dialog, and performs an initial
 * search to display the whole contents of the database.
 * <p/>
 * This <code>NetworkClientView</code> has been made an observer getting
 * notified automatically when the server shuts down.
 * <p/>
 * Due to the thin client design approach, this view plays the combined roles of
 * the view as well as the controller in the model-view-controller (MVC)
 * setting.
 *
 * @author Lars Kuettner
 * @version 1.0
 * @see StandaloneClientView
 */
public class NetworkClientView extends UnicastRemoteObject implements
  RemoteObserver
{
  /**
   * A magic version number for this class so that serialization can occur
   * without worrying about the underlying class changing between
   * serialization and deserialization.
   */
  private static final long serialVersionUID = 4711L;

  /**
   * Logger object to log messages in the scope of this class.
   */
  private Logger log = Logger.getLogger(NetworkClientView.class.getName());

  /**
   * A reference to the client main frame.
   */
  private ClientMainFrame mainFrame;

  /**
   * The business services in its contractor manager remote variant.
   */
  private ContractorManagerRemote contractorManagerRemote = null;

  /**
   * Creates a new network client view and displays it. Displays a
   * {@link NetworkClientParametersDialog} on top of it and tries to establish
   * a connection to the server until successful, or exits. If successfully
   * connected to the server, performs a search for all records in the
   * database and populates the table accordingly.
   * <p/>
   * Unlike the {@link StandaloneClientView}, the network client view acts as
   * a remote observer. The only change observed, however, is a server
   * shutdown. In particular, database changes are <em>not</em> automatically
   * communicated from the server down to its network clients, as might be
   * expected. However, it is easily possible to get a current snapshot of the
   * database by manually repeating the last search operation. This is done
   * automatically after each book operation.
   *
   * @throws RemoteException on network error
   */
  public NetworkClientView() throws RemoteException
  {

    mainFrame = new ClientMainFrame(Text.NETWORK_CLIENT_TITLE);

    // Get the model (the business services)
    // Collect the necessary configuration info first (host name or IP
    // address and port number) by means of a dialog.
    // Display the fetch parameters dialog.
    NetworkClientParametersDialog paramsDlg =
      new NetworkClientParametersDialog(mainFrame);

    contractorManagerRemote = null;
    while (contractorManagerRemote == null)
    {
      paramsDlg.setVisible(true);

      if (paramsDlg.hasBeenCanceled())
      {
        log.info("The network client parameters dialog"
          + " has been canceled. Exiting the JVM");
        System.exit(0);
      }
      // Make a connection to the server.
      try
      {
        // Connect to remote contractor manager with the business
        // services and remote observable interfaces.
        contractorManagerRemote = ContractorManagerConnector.getRemote(
          paramsDlg.getServerHostname(),
          paramsDlg.getPortNumber());

        // Add this client view as a remote observer of the server.
        contractorManagerRemote.addObserver(this);

      }
      catch (RemoteException e)
      {
        // Connection to database failed.
        log.log(Level.WARNING,
          Text.CANT_CONNECT_TO_REMOTE_CONTRACTOR_MANAGER
            + " (hostname=" + paramsDlg.getServerHostname()
            + ", port=" + paramsDlg.getPortNumber() + ")",
          e);
        JOptionPane.showMessageDialog(mainFrame,
          Text.CANT_CONNECT_TO_REMOTE_CONTRACTOR_MANAGER
            + Text.NESTED_EXCEPTION_IS + e.getMessage(),
          Text.WARNING, JOptionPane.WARNING_MESSAGE);
      }
    }
    mainFrame.setBusinessServices(contractorManagerRemote);

    // Fill the table with all records available.
    mainFrame.doSearch();

    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      @Override
      public void run()
      {
        // The method to terminate the Singleton Data instance is not
        // accessible via the interface DBSearchFriendlyAccess.
        // Also, the shutdown hook is not in Data itself as that would
        // impair testing.
        if (contractorManagerRemote != null)
        {
          try
          {
            contractorManagerRemote
              .deleteObserver(NetworkClientView.this);
          }
          catch (RemoteException e)
          {
            // Just ignore. Server will log an error, though.
            // Logging-mechanism doesn't work here anymore.
            ;
          }
        }
      }
    });

  }

  /**
   * Called from the server application when the server is about to be
   * shutting down. Causes the client main frame to deactivate itself so that
   * exit is the only possible operation left enabled.
   */
//    @Override
  public final void update(final RemoteObservable obs, final Object arg)
    throws RemoteException
  {
    log.fine("Observed notification about server going down right now");
    if (arg == null)
    {
      // Called from an RMI thread. Schedule for later execution in the
      // context of the event dispatching thread.
      SwingUtilities.invokeLater(new Runnable()
      {
        //		@Override
        public void run()
        {
          mainFrame.deactivate();
        }
      });
    }
  }
}
