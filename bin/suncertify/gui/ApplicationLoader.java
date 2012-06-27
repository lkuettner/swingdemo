/* 
 * @(#)ApplicationLoader.java    1.0 21/06/2010 
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

import javax.swing.*;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The application's entry point. Depending on the command-line argument, the
 * program will start as a server (<code>server</code>), a network client (no
 * command-line argument), or a standalone client (<code>alone</code>).
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public final class ApplicationLoader
{

  /**
   * Logger object to log messages in the scope of this class.
   */
  private Logger log = Logger.getLogger(ApplicationLoader.class.getName());

  /**
   * Application entry point. Launches either of the three incarnations of the
   * application (server, network client, standalone client), depending on the
   * command-line argument provided (or not provided).
   *
   * @param args the command-line arguments. May be empty (no command-line
   *             arguments: network client), "server" (server), or "alone"
   *             (standalone client).
   */
  public static void main(final String[] args)
  {
    new ApplicationLoader(args);
  }

  /**
   * Evaluates the command-line arguments and starts the proper incarnation of
   * the application: server, network client, or standalone client.
   *
   * @param args the command-line arguments. May be empty (no command-line
   *             arguments: network client), "server" (server), or "alone"
   *             (standalone client).
   */
  private ApplicationLoader(final String[] args)
  {
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (UnsupportedLookAndFeelException uex)
    {
      log.warning("Unsupported look and feel specified");
    }
    catch (ClassNotFoundException cex)
    {
      log.warning("Look and feel could not be located");
    }
    catch (InstantiationException iex)
    {
      log.warning("Look and feel could not be instanciated");
    }
    catch (IllegalAccessException iaex)
    {
      log.warning("Look and feel cannot be used on this platform");
    }

    if (args.length == 0)
    {
      // Schedule for the event-dispatching thread
      SwingUtilities.invokeLater(new Runnable()
      {
        //                @Override
        public void run()
        {
          try
          {
            // Create an instance of the network client application
            // view
            new NetworkClientView();
          }
          catch (RemoteException e)
          {
            // Remote only for the RemoteObserver
            log.log(Level.SEVERE,
              "Can't create network client view: "
                + e.getMessage(), e);
          }
        }
      });
    }
    else if ("alone".equals(args[0]))
    {
      // Schedule for the event-dispatching thread
      SwingUtilities.invokeLater(new Runnable()
      {
        //                @Override
        public void run()
        {
          // Create an instance of the standalone client application
          // view
          new StandaloneClientView();
        }
      });
    }
    else if ("server".equals(args[0]))
    {
      // Schedule for the event-dispatching thread:
      // Create and show the server GUI
      SwingUtilities.invokeLater(new Runnable()
      {
        //                @Override
        public void run()
        {
          new ServerView();
        }
      });
    }
    else
    {
      log.severe("Invalid command-line argument: " + args[0]);

      System.err.println("Invalid command-line argument: " + args[0]);
      System.err.println("Command line argument may be one of:");
      System.err.println("\"server\" - starts the server application");
      System.err.println("\"alone\"  - starts the standalone client");
      System.err.println("\"\"       - (no command-line argument) "
        + "starts the network client");
    }
  }
}
