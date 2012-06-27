/* 
 * @(#)StandaloneClientParametersDialog    1.0 21/06/2010 
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * An input dialog to enter the configuration parameters for the standalone
 * client application. Appears when invoking the standalone client.
 *
 * @author Lars Kuettner
 * @version 1.0
 * @see NetworkClientParametersDialog
 */
public class StandaloneClientParametersDialog extends JDialog
{
  /**
   * A magic version number for this class so that serialization can occur
   * without worrying about the underlying class changing between
   * serialization and deserialization.
   */
  private static final long serialVersionUID = 4711L;

  /**
   * The time to programmatically hold down a button when calling doClick,
   * in milliseconds.
   */
  static final int BUTTON_HOLD_DOWN_TIME = 200;

  /**
   * The database location checked text field.
   */
  private CheckedTextField databaseLocationField = null;
  /**
   * The browse button for the database location file chooser dialog.
   */
  private JButton browseButton = new JButton("...");

  /**
   * The connect button.
   */
  private JButton connectButton = new JButton(Text.CONNECT_BUTTON_TEXT);
  /**
   * The exit button.
   */
  private JButton exitButton = new JButton(Text.EXIT_BUTTON_TEXT);

  /**
   * A flag indicating whether the database location is valid.
   */
  private boolean validDatabaseLocation = false;

  /**
   * A reference to the persistent configuration object.
   */
  private PersistentConfiguration config;

  /**
   * The most recently checked database location.
   */
  private String prevDatabaseLocation;

  /**
   * When hitting Enter in any text field, emulates clicking the Connect
   * button.
   */
  private KeyListener textFieldEnterKeyListener = new KeyListener()
  {
    @Override
    public void keyPressed(final KeyEvent ke)
    {
      // Keep idle - no action.
    }

    @Override
    public void keyReleased(final KeyEvent ke)
    {
      // Keep idle - no action.
    }

    @Override
    public void keyTyped(final KeyEvent ke)
    {
      if (ke.getKeyChar() == KeyEvent.VK_ENTER)
      {
        // Programmatically perform a click on the connect button if
        // enabled.
        if (connectButton.isEnabled())
        {
          connectButton.doClick(BUTTON_HOLD_DOWN_TIME);
        }
      }
    }
  };

  /**
   * Creates the standalone client parameters dialog that is repeatedly
   * displayed when starting the standalone client until a database connection
   * can be established or exit is pressed.
   *
   * @param parent the parent frame of the parameters dialog which would be the
   *               client main frame
   */
  public StandaloneClientParametersDialog(final JFrame parent)
  {
    super(parent, true); // A modal dialog.

    // Load saved configuration.
    config = PersistentConfiguration.getPersistentConfiguration();
    prevDatabaseLocation = config
      .getParameter(PersistentConfiguration.DATABASE_LOCATION);

    ConfigurationPanel panel = new ConfigurationPanel();

    databaseLocationField = panel.addDatabaseLocationRow(browseButton);
    databaseLocationField.addValidityChangeListener(
      new DatabaseLocationValidityChangeHandler());
    databaseLocationField.addKeyListener(textFieldEnterKeyListener);
    databaseLocationField.setText(prevDatabaseLocation);

    connectButton.setActionCommand(Text.CONNECT_BUTTON_TEXT);
    connectButton.addActionListener(new ConnectHandler());

    exitButton.setActionCommand(Text.EXIT_BUTTON_TEXT);
    exitButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(final ActionEvent ae)
      {
        leaveDialog(ae.getActionCommand());
      }
    });

    JOptionPane optionPane = new JOptionPane(panel,
      JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

    optionPane.setOptions(new Object[]{ connectButton, exitButton });

    setContentPane(optionPane);
    setTitle(Text.STANDALONE_CLIENT_CONNECTION_PARAMETERS_TITLE);
    pack();
    setResizable(false);

    // Leads to windowClosing being called on "crossing out" dialog.
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(final WindowEvent we)
      {
        leaveDialog(Text.EXIT_BUTTON_TEXT);
      }
    });

    setLocationRelativeTo(parent);

    databaseLocationField.requestFocusInWindow();
  }

  /**
   * Updates the activation state of the Connect button depending on the state
   * of the database location text input field.
   */
  private void updateControls()
  {
    connectButton.setEnabled(validDatabaseLocation);
  }

  /**
   * Validity change handler for validity change events fired by the database
   * location text input field. Depending on the validity of the database
   * location, the activation state of the connect button is updated via
   * {@link updateControls}.
   *
   * @author Lars Kuettner
   * @version 1.0
   */
  private class DatabaseLocationValidityChangeHandler implements
    ValidityChangeListener
  {
    @Override
    public void validityGained(final ValidityChangeEvent e)
    {
      validDatabaseLocation = true;
      updateControls();
    }

    @Override
    public void validityLost(final ValidityChangeEvent e)
    {
      validDatabaseLocation = false;
      updateControls();
    }
  }

  /**
   * This handler is executed when the Connect button is pressed. The contents
   * of the database location text input field is persisted and after that the
   * dialog is made invisible. Establishing a connection to the database will
   * be attempted from outside this dialog. If the connection attempt fails,
   * the dialog will be displayed again until either a database connection can
   * be established or the dialog is exited.
   *
   * @author Lars Kuettner
   * @version 1.0
   */
  private class ConnectHandler implements ActionListener
  {
    @Override
    public void actionPerformed(final ActionEvent ae)
    {
      // We know the database location must be valid because the connect
      // button had to be enabled to get here.
      String location = databaseLocationField.getText().trim();
      if (!location.equals(prevDatabaseLocation))
      {
        config.setParameter(PersistentConfiguration.DATABASE_LOCATION,
          location);
        prevDatabaseLocation = location;
      }
      leaveDialog(ae.getActionCommand());
    }
  }

  /**
   * Leaves the dialog by setting its status before making it invisible as a
   * result of clicking Connect, Exit, or closing the window.
   *
   * @param command a string representing the action that occurred
   */
  private void leaveDialog(final String command)
  {
    JOptionPane optionPane = (JOptionPane) getContentPane();
    if (Text.CONNECT_BUTTON_TEXT.equals(command))
    {
      optionPane.setValue(JOptionPane.OK_OPTION);
    }
    else
    {
      optionPane.setValue(JOptionPane.CANCEL_OPTION);
    }
    setVisible(false);
  }

  /**
   * Returns whether the dialog has been canceled or confirmed.
   *
   * @return <code>true</code> if the dialog has been canceled, that is, if
   *         the Exit button has been pressed or the window closed, and
   *         <code>false</code> if the dialog has been confirmed pressing the
   *         Connect button
   */
  public final boolean hasBeenCanceled()
  {
    JOptionPane optionPane = (JOptionPane) getContentPane();
    if (optionPane.getValue() instanceof Integer)
    {
      int status = ((Integer) (optionPane.getValue())).intValue();
      return status != JOptionPane.OK_OPTION;
    }
    else
    {
      return false;
    }
  }

  /**
   * Returns the database location from the corresponding text input field.
   *
   * @return the database location in string representation
   */
  public final String getDatabaseLocation()
  {
    return databaseLocationField.getText().trim();
  }
}
