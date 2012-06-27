/*
 * @(#)Text.java    1.0 21/06/2010
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

import java.awt.event.KeyEvent;

/**
 * Utility class wrapping mainly text constants (and some mnemonic dependent on
 * text constants) for the <code>suncertify.gui</code> package.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public final class Text
{
  /**
   * Private constructor since this is a utility class.
   */
  private Text()
  {
  }

  /**
   * Enter the name of the subcontractor to search -
   * or leave empty for any name.
   */
  static final String SEARCH_NAME_TOOL_TIP =
    "Enter the name of the subcontractor to search - "
      + "or leave empty for any name";
  /**
   * Enter the location at which to search for a subcontractor -
   * or leave empty for any location.
   */
  static final String SEARCH_LOCATION_TOOL_TIP =
    "Enter the location at which to search for a subcontractor - "
      + "or leave empty for any location";
  /**
   * Search.
   */
  static final String SEARCH_BUTTON_TEXT = "Search";
  /**
   * Search for matching records in the database.
   */
  static final String SEARCH_BUTTON_TOOL_TIP =
    "Search for matching records in the database";
  /**
   * Book.
   */
  static final String BOOK_BUTTON_TEXT = "Book";
  /**
   * Book selected contractor.
   */
  static final String BOOK_BUTTON_TOOL_TIP = "Book selected contractor";
  /**
   * Select a contractor record.
   */
  static final String TABLE_TOOL_TIP_TEXT = "Select a contractor record";
  /**
   * The \"search\" operation failed.
   */
  static final String SEARCH_OPERATION_FAILED =
    "The \"search\" operation failed";
  /**
   * ; the reason for failure is:\n.
   */
  static final String REASON_IS = "; the reason for failure is:\n";
  /**
   * Error.
   */
  static final String ERROR = "Error";
  /**
   * Hostname:.
   */
  static final String SERVER_HOSTNAME_LABEL = "Hostname:";
  /**
   * The hostname or IP address of the server where the database is located.
   */
  static final String SERVER_HOSTNAME_TOOL_TIP = "The hostname or IP address "
    + "of the server where the database is located";
  /**
   * Database location:.
   */
  static final String DATABASE_LOCATION_LABEL = "Database location:";
  /**
   * The location of the database on an accessible hard drive.
   */
  static final String DATABASE_LOCATION_TOOL_TIP =
    "The location of the database on an accessible hard drive";
  /**
   * db.
   */
  static final String DATABASE_EXTENSION = "db";
  /**
   * Database files (*.db).
   */
  static final String DATABASE_FILE_CHOOSER_DESCRIPTION = "Database files (*."
    + DATABASE_EXTENSION + ")";
  /**
   * Port:.
   */
  static final String SERVER_PORT_LABEL = "Port:";
  /**
   * The port number the server uses to listen for requests.
   */
  static final String SERVER_PORT_TOOL_TIP =
    "The port number the server uses to listen for requests";
  /**
   * Edit Contractor.
   */
  static final String EDIT_CONTRACTOR_TITLE = "Edit Contractor";
  /**
   * Book Contractor.
   */
  static final String BOOK_CONTRACTOR_TITLE = "Book Contractor";
  /**
   * OK.
   */
  static final String OK = "OK";
  /**
   * Cancel.
   */
  static final String CANCEL = "Cancel";
  /**
   * Success Notification.
   */
  static final String SUCCESS_NOTIFICATION = "Success Notification";
  /**
   * Warning.
   */
  static final String WARNING = "Warning";
  /**
   * Contractor successfully booked.
   */
  static final String CONTRACTOR_SUCCESSFULLY_BOOKED =
    "Contractor successfully booked";
  /**
   * Contractor intermittently grabbed (booked) by some other agent.
   */
  static final String CONTRACTOR_INTERMITTENTLY_GRABBED =
    "Contractor intermittently grabbed (booked) by some other agent";
  /**
   * Contractor intermittently updated.\n
   * Retrying to book this contractor may succeed.
   */
  static final String CONTRACTOR_INTERMITTENTLY_UPDATED =
    "Contractor intermittently updated.\n"
      + "Retrying to book this contractor may succeed";
  /**
   * Contractor intermittently deleted from the database.
   */
  static final String CONTRACTOR_INTERMITTENTLY_DELETED =
    "Contractor intermittently deleted from the database";
  /**
   * The \"book\" operation failed.
   */
  static final String BOOK_OPERATION_FAILED = "The \"book\" operation failed";
  /**
   * ; nested exception is:\n.
   */
  static final String NESTED_EXCEPTION_IS = "; nested exception is:\n";
  /**
   * Server is going down.\n"
   * "All operations apart from exit have been disabled.
   */
  static final String SERVER_IS_GOING_DOWN = "Server is going down.\n"
    + "All operations apart from exit have been disabled";
  /**
   * Server Termination Notification.
   */
  static final String SERVER_TERMINATION_NOTIFICATION =
    "Server Termination Notification";
  /**
   * Specify Network Client Connection Parameters.
   */
  static final String NETWORK_CLIENT_CONNECTION_PARAMETERS_TITLE =
    "Specify Network Client Connection Parameters";
  /**
   * Connect.
   */
  static final String CONNECT_BUTTON_TEXT = "Connect";
  /**
   * Bodgitt and Scarper, LLC. - Network Client.
   */
  static final String NETWORK_CLIENT_TITLE =
    "Bodgitt and Scarper, LLC. - Network Client";
  /**
   * Can't connect to the remote contractor manager.
   */
  static final String CANT_CONNECT_TO_REMOTE_CONTRACTOR_MANAGER =
    "Can't connect to the remote contractor manager";
  /**
   * Bodgitt & Scarper, LLC. - Server.
   */
  static final String SERVER_TITLE = "Bodgitt & Scarper, LLC. - Server";
  /**
   * File.
   */
  static final String MENU_FILE_TEXT = "File";
  /**
   * KeyEvent.VK_F.
   */
  static final int MENU_FILE_MNEMONIC = KeyEvent.VK_F;
  /**
   * Exit.
   */
  static final String MENU_EXIT_TEXT = "Exit";
  /**
   * KeyEvent.VK_X.
   */
  static final int MENU_EXIT_MNEMONIC = KeyEvent.VK_X;
  /**
   * Start server.
   */
  static final String START_BUTTON_TEXT = "Start server";
  /**
   * Starts the server.
   */
  static final String START_BUTTON_TOOLTIP = "Starts the server";
  /**
   * Exit.
   */
  static final String EXIT_BUTTON_TEXT = "Exit";
  /**
   * Stops the server as soon as it is safe to do so.
   */
  static final String EXIT_BUTTON_TOOLTIP =
    "Stops the server as soon as it is safe to do so";
  /**
   * Enter configuration parameters and click \"Start server\".
   */
  static final String INITIAL_STATUS = "Enter configuration parameters "
    + "and click \"" + START_BUTTON_TEXT + "\"";
  /**
   * Starting the RMI registry and registering the server.
   */
  static final String SERVER_STARTING = "Starting the RMI registry"
    + " and registering the server";
  /**
   * Server running.
   */
  static final String SERVER_RUNNING = "Server running";
  /**
   * Can't start the server with this configuration.
   */
  static final String CANT_START_SERVER =
    "Can't start the server with this configuration";
  /**
   * Specify Database Location for Standalone Client and Connect.
   */
  static final String STANDALONE_CLIENT_CONNECTION_PARAMETERS_TITLE =
    "Specify Database Location for Standalone Client and Connect";
  /**
   * Bodgitt and Scarper, LLC. - Standalone Client.
   */
  static final String STANDALONE_CLIENT_TITLE =
    "Bodgitt and Scarper, LLC. - Standalone Client";
  /**
   * Can't connect to the database at %s.
   */
  static final String CANT_CONNECT_TO_DATABASE =
    "Can't connect to the database at %s";
}
