/* 
 * @(#)NetworkClientParametersDialog    1.0 21/06/2010 
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * An input dialog to enter the configuration parameters for the network client
 * application. Appears when invoking the network client.
 * 
 * @author Lars Kuettner
 * @version 1.0
 * @see StandaloneClientParametersDialog
 */
public class NetworkClientParametersDialog extends JDialog {
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

    /** The CONNECT button. */
    private JButton connectButton = new JButton(Text.CONNECT_BUTTON_TEXT);
    /** The EXIT button. */
    private JButton exitButton = new JButton(Text.EXIT_BUTTON_TEXT);

    /** The server hostname checked text input field. */
    private CheckedTextField serverHostnameField = null;
    /** The port number checked text input field. */
    private CheckedTextField portNumberField = null;

    /** A flag indication whether the present server hostname may be valid. */
    private boolean validServerHostname = false;
    /** A flag indication whether the present port number may be valid. */
    private boolean validPortNumber = false;

    /** A reference to the persistent configuration data object. */
    private PersistentConfiguration config;

    /** The most recently checked server hostname. */
    private String serverHostname;
    /** The most recently checked port number. */
    private String portNumber;

    /**
     * When hitting Enter in any text field, emulates clicking the Connect
     * button.
     */
    private KeyListener textFieldEnterKeyListener = new KeyListener() {
	@Override
	public void keyPressed(final KeyEvent ke) {
	    // Keep idle - no action.
	}

	@Override
	public void keyReleased(final KeyEvent ke) {
	    // Keep idle - no action.
	}

	@Override
	public void keyTyped(final KeyEvent ke) {
	    if (ke.getKeyChar() == KeyEvent.VK_ENTER) {
		// Programmatically perform a click on the connect button if
		// enabled.
		if (connectButton.isEnabled()) {
		    connectButton.doClick(BUTTON_HOLD_DOWN_TIME);
		}
	    }
	}
    };

    /**
     * Creates the network client parameters dialog that is repeatedly displayed
     * when starting the network client until a connection with the server can
     * be established or exit is pressed.
     * 
     * @param parent
     *            the parent frame of the parameters dialog which would be the
     *            client main frame
     */
    public NetworkClientParametersDialog(final JFrame parent) {
	super(parent, Text.NETWORK_CLIENT_CONNECTION_PARAMETERS_TITLE, true);

	// Load saved configuration.
	config = PersistentConfiguration.getPersistentConfiguration();
	serverHostname = config
		.getParameter(PersistentConfiguration.SERVER_ADDRESS);
	portNumber = config.getParameter(PersistentConfiguration.SERVER_PORT);

	ConfigurationPanel panel = new ConfigurationPanel();

	serverHostnameField = panel.addServerHostnameRow();
	serverHostnameField.addValidityChangeListener(
		new ServerHostnameValidityChangeHandler());
	serverHostnameField.addKeyListener(textFieldEnterKeyListener);
	serverHostnameField.setText(serverHostname);

	portNumberField = panel.addPortNumberRow();
	portNumberField.addValidityChangeListener(
		new PortNumberValidityChangeHandler());
	portNumberField.addKeyListener(textFieldEnterKeyListener);
	portNumberField.setText(portNumber);

	connectButton.setActionCommand(Text.CONNECT_BUTTON_TEXT);
	connectButton.addActionListener(new ConnectHandler());

	exitButton.setActionCommand(Text.EXIT_BUTTON_TEXT);
	exitButton.addActionListener(new ActionListener() {
	    public void actionPerformed(final ActionEvent ae) {
		leaveDialog(ae.getActionCommand());
	    }
	});

	JOptionPane optionPane = new JOptionPane(panel, // the object to display
		JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

	optionPane.setOptions(new Object[] { connectButton, exitButton });

	setContentPane(optionPane);
	pack();
	setResizable(false);

	// Leads to windowClosing being called on "crossing out" dialog.
	setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	addWindowListener(new WindowAdapter() {
	    public void windowClosing(final WindowEvent we) {
		leaveDialog(Text.EXIT_BUTTON_TEXT);
	    }
	});
	setLocationRelativeTo(parent);

	serverHostnameField.requestFocusInWindow();
    }

    /**
     * Updates the activation state of the Connect button depending on the state
     * of the text input fields.
     */
    private void updateControls() {
	connectButton.setEnabled(validServerHostname && validPortNumber);
    }

    /**
     * Validity change handler for validity change events fired by the server
     * hostname text input field. Validity is lost when the input field becomes
     * empty. Validity is gained when a character is entered into an empty input
     * field. Depending on the validity of the server hostname, the activation
     * state of the connect button is updated via {@link updateControls}.
     * 
     * @author Lars Kuettner
     * @version 1.0
     */
    private class ServerHostnameValidityChangeHandler implements
	    ValidityChangeListener {
	@Override
	public void validityGained(final ValidityChangeEvent e) {
	    validServerHostname = true;
	    updateControls();
	}

	@Override
	public void validityLost(final ValidityChangeEvent e) {
	    validServerHostname = false;
	    updateControls();
	}
    }

    /**
     * Validity change handler for the port number text input field. Depending
     * on the validity of the port number, the activation state of the connect
     * button is updated via {@link updateControls}.
     * 
     * @author Lars Kuettner
     * @version 1.0
     */
    private class PortNumberValidityChangeHandler implements
	    ValidityChangeListener {
	@Override
	public void validityGained(final ValidityChangeEvent ve) {
	    validPortNumber = true;
	    updateControls();
	}

	@Override
	public void validityLost(final ValidityChangeEvent ve) {
	    validPortNumber = false;
	    updateControls();
	}
    }

    /**
     * This handler is executed when the Connect button is pressed. The contents
     * of the text input fields (server hostname and port number) is persisted
     * before the dialog is made invisible. Establishing a connection to the
     * server will be attempted from outside this dialog. If the connection
     * attempt fails, the dialog will be displayed again until either a
     * connection can be established or the dialog is exited.
     * 
     * @author Lars Kuettner
     * @version 1.0
     */
    private class ConnectHandler implements ActionListener {
	@Override
	public void actionPerformed(final ActionEvent ae) {
	    // Accept the server hostname. It might not be a valid hostname or
	    // IP address, though. This will be handled later when it leads to a
	    // failure in establishing a connection, which must be handled
	    // anyway.
	    String hostname = serverHostnameField.getText();
	    if (!hostname.equals(serverHostname)) {
		config.setParameter(PersistentConfiguration.SERVER_ADDRESS,
			hostname);
		serverHostname = hostname;
	    }
	    // The port number is guaranteed to be in the range specified.
	    String port = portNumberField.getText();
	    if (!port.equals(portNumber)) {
		config.setParameter(PersistentConfiguration.SERVER_PORT, port);
		portNumber = port;
	    }
	    leaveDialog(ae.getActionCommand());
	}
    }

    /**
     * Leaves the dialog by setting its status before making it invisible as a
     * result of clicking Connect, Exit, or closing the window.
     * 
     * @param command
     *            a string representing the action that occurred
     */
    private void leaveDialog(final String command) {
	JOptionPane optionPane = (JOptionPane) getContentPane();
	if (Text.CONNECT_BUTTON_TEXT.equals(command)) {
	    optionPane.setValue(JOptionPane.OK_OPTION);
	} else {
	    optionPane.setValue(JOptionPane.CANCEL_OPTION);
	}
	setVisible(false);
    }

    /**
     * Returns whether the dialog has been canceled or confirmed.
     * 
     * @return <code>true</code>´if the dialog has been canceled, that is, if
     *         the Exit button has been pressed or the window closed, and
     *         <code>false</code> if the dialog has been confirmed pressing the
     *         Connect button
     */
    public final boolean hasBeenCanceled() {
	JOptionPane optionPane = (JOptionPane) getContentPane();
	if (optionPane.getValue() instanceof Integer) {
	    int status = ((Integer) (optionPane.getValue())).intValue();
	    return status != JOptionPane.OK_OPTION;
	} else {
	    return false;
	}
    }

    /**
     * Returns the server hostname from the corresponding text input field.
     * 
     * @return the server hostname in string representation
     */
    public final String getServerHostname() {
	return serverHostname;
    }

    /**
     * Returns the port number from the corresponding text input field.
     * 
     * @return the port number in string representation
     */
    public final String getPortNumber() {
	return portNumber;
    }
}
