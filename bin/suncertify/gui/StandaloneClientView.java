/* 
 * @(#)StandaloneClientView    1.0 21/06/2010 
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
import suncertify.services.ContractorManager;
import suncertify.services.ServicesException;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The view for the standalone client, wrapping the client main frame as the
 * window actually being displayed. Creates and displays the client main frame,
 * connects to the contractor manager based on the information retrieved from
 * the likewise-managed configuration parameters dialog, and performs an initial
 * search to display the whole contents of the database.
 * <p/>
 * Unlike the <code>NetworkClientView</code>, the
 * <code>StandaloneClientView</code> does not need observer functionality as
 * there is no separate server process to observe.
 * <p/>
 * Due to the thin client design approach, this view plays the combined roles of
 * the view as well as the controller in the model-view-controller (MVC)
 * setting.
 *
 * @author Lars Kuettner
 * @version 1.0
 * @see NetworkClientView
 */
public class StandaloneClientView
{

  /**
   * Logger object to log messages in the scope of this class.
   */
  private Logger log = Logger.getLogger(StandaloneClientView.class.getName());

  /**
   * A reference to the client main frame.
   */
  private ClientMainFrame mainFrame;

  /**
   * The contractor manager object, primarily needed here to gracefully
   * terminate the application.
   */
  private ContractorManager contractorManager = null;

  /**
   * Creates the standalone client view and displays it. Displays a
   * {@link StandaloneClientParametersDialog} on top of it and tries to
   * establish a connection to the database until successful, or exits. If
   * successfully connected to the database, performs a search for all records
   * and populates the table accordingly.
   */
  public StandaloneClientView()
  {

    mainFrame = new ClientMainFrame(Text.STANDALONE_CLIENT_TITLE);

    // Get the model (the business services)
    // Display the fetch-parameters dialog.
    StandaloneClientParametersDialog paramsDlg =
      new StandaloneClientParametersDialog(mainFrame);

    while (contractorManager == null)
    {
      // Show the parameters dialog.
      paramsDlg.setVisible(true);

      if (paramsDlg.hasBeenCanceled())
      {
        log.info("The standalone client parameters dialog"
          + " has been canceled. Exiting the JVM");
        System.exit(0);
      }
      // Open the database file - may fail as well, but for other reasons.
      try
      {
        // Connect to the local database (the model).
        contractorManager = ContractorManagerConnector
          .getLocal(paramsDlg.getDatabaseLocation());
      }
      catch (ServicesException e)
      {
        // Connection to database failed.
        log.log(Level.WARNING, String.format(
          Text.CANT_CONNECT_TO_DATABASE, paramsDlg
          .getDatabaseLocation()), e);
        JOptionPane.showMessageDialog(mainFrame, String.format(
          Text.CANT_CONNECT_TO_DATABASE, paramsDlg
          .getDatabaseLocation())
          + Text.NESTED_EXCEPTION_IS + e.getMessage(),
          Text.WARNING, JOptionPane.WARNING_MESSAGE);
      }
    }

    mainFrame.setBusinessServices(contractorManager);

    // Fill the table with all records available.
    mainFrame.doSearch();

    // This shutdown hook is quite handy and only necessary for the
    // standalone client, as the server can and does explicitly terminate
    // its contractor manager. The main frame of the standalone client,
    // however, only knows about a business services interface that does
    // (and should) not know about how to terminate itself, as the network
    // client that also uses the same business services interface, never
    // needs to do (a network client cannot "terminate" a contractor manager
    // proxy and certainly not the server).
    Runtime.getRuntime().addShutdownHook(new Thread()
    {
      @Override
      public void run()
      {
        // The method to terminate the Singleton Data instance is not
        // accessible via the interface DBSearchFriendlyAccess.
        // Also, the shutdown hook is not in Data itself as that would
        // impair testing.
        if (contractorManager != null)
        {
          contractorManager.terminate();
        }
      }
    });
  }
}
