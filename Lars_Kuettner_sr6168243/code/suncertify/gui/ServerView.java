/* 
 * @(#)ServerView    1.0 21/06/2010 
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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import suncertify.rmi.LaunchServerException;
import suncertify.rmi.ServerUtilities;

/**
 * The server view, managing appearance and functionality (listeners) of the
 * server application.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class ServerView {

    /** Logger object to log messages in the scope of this class. */
    private Logger log = Logger.getLogger(ServerView.class.getName());

    /**
     * The time to programmatically hold down a button when calling doClick,
     * in milliseconds.
     */
    static final int BUTTON_HOLD_DOWN_TIME = 200;

    /** The server main frame. */
    private JFrame mainFrame;

    /** The database location checked text field. */
    private CheckedTextField databaseLocationField = null;
    /** The browse button for the database location file chooser dialog. */
    private JButton browseButton = new JButton("...");
    /** The port number checked text field. */
    private CheckedTextField portNumberField = null;

    /**
     * When hitting Enter in any text field, emulates clicking the Start server
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
		// Programmatically perform a click on the start button if
		// enabled.
		if (startButton.isEnabled()) {
		    startButton.doClick(BUTTON_HOLD_DOWN_TIME);
		}
	    }
	}
    };

    /** A reference to the persistent configuration object. */
    private PersistentConfiguration config;

    /** The most recently checked database location. */
    private String prevDatabaseLocation;
    /** The most recently checked port number. */
    private String prevPortNumber;

    /** A flag indication whether the database location entered is valid. */
    private boolean validDatabaseLocation = false;
    /** A flag indication whether the port number entered is valid. */
    private boolean validPortNumber = false;

    /** The start server button. */
    private JButton startButton = new JButton(Text.START_BUTTON_TEXT);
    /** The exit button. */
    private JButton exitButton = new JButton(Text.EXIT_BUTTON_TEXT);
    /** The status line. */
    private JLabel statusLabel = new JLabel();

    /**
     * Creates and displays the server view.
     */
    public ServerView() {
	mainFrame = new JFrame(Text.SERVER_TITLE);

	mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	mainFrame.addWindowListener(new WindowAdapter() {
	    public void windowClosing(final WindowEvent we) {
		log.fine("Exiting server via windowClosing");
		exitApplication();
	    }
	});
	mainFrame.setResizable(false);

	mainFrame.setJMenuBar(makeMenuBar());

	// Replace default border layout with box layout
	// Actually: getContentPane().setLayout()
	mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane(),
		BoxLayout.PAGE_AXIS));

	// Build the configuration panel.
	mainFrame.add(makeConfigurationPanel());

	// Build the command button panel (start server, exit).
	mainFrame.add(makeCommandPanel());

	// Build the status panel. As its size depends on the frame size,
	// it can only be set after the container has been "packed".
	JPanel statusPanel = new JPanel(new BorderLayout());
	statusLabel.setBorder(BorderFactory
		.createBevelBorder(BevelBorder.LOWERED));
	statusLabel.setText(Text.INITIAL_STATUS);
	statusPanel.add(statusLabel, BorderLayout.CENTER);
	mainFrame.add(statusPanel);

	mainFrame.pack();

	// After packing the container, fix the size of the status panel.
	statusPanel.setPreferredSize(statusPanel.getSize());

	mainFrame.setLocationRelativeTo(null);

	databaseLocationField.requestFocusInWindow();

	mainFrame.setVisible(true);
    }

    /**
     * Makes a menu bar.
     * 
     * @return the menu bar just created
     */
    private JMenuBar makeMenuBar() {
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu(Text.MENU_FILE_TEXT);
	JMenuItem exitMenuItem = new JMenuItem(Text.MENU_EXIT_TEXT);
	exitMenuItem.addActionListener(new ExitApplication());
	exitMenuItem.setMnemonic(Text.MENU_EXIT_MNEMONIC);
	fileMenu.add(exitMenuItem);
	fileMenu.setMnemonic(Text.MENU_FILE_MNEMONIC);
	menuBar.add(fileMenu);
	return menuBar;
    }

    /**
     * Makes the configuration panel component of the server application.
     * 
     * @return the configuration panel just created
     */
    private JPanel makeConfigurationPanel() {
	// Load saved configuration.
	config = PersistentConfiguration.getPersistentConfiguration();
	prevDatabaseLocation = config
		.getParameter(PersistentConfiguration.DATABASE_LOCATION);
	prevPortNumber = config
		.getParameter(PersistentConfiguration.SERVER_PORT);

	ConfigurationPanel panel = new ConfigurationPanel();

	databaseLocationField = panel.addDatabaseLocationRow(browseButton);
	databaseLocationField.addValidityChangeListener(
		new DatabaseLocationValidityChangeHandler());
	databaseLocationField.addKeyListener(textFieldEnterKeyListener);
	databaseLocationField.setText(prevDatabaseLocation);

	portNumberField = panel.addPortNumberRow();
	portNumberField.addValidityChangeListener(
		new PortNumberValidityChangeHandler());
	portNumberField.addKeyListener(textFieldEnterKeyListener);
	portNumberField.setText(prevPortNumber);

	panel.setBorder(BorderFactory.createTitledBorder("Configuration"));

	return panel;
    }

    /**
     * Makes the command panel component of the server application.
     * 
     * @return the command panel just created
     */
    private JPanel makeCommandPanel() {
	JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

	startButton.setToolTipText(Text.START_BUTTON_TOOLTIP);
	startButton.addActionListener(new StartServer());
	panel.add(startButton);

	exitButton.setToolTipText(Text.EXIT_BUTTON_TOOLTIP);
	exitButton.addActionListener(new ExitApplication());
	panel.add(exitButton);

	return panel;
    }

    /**
     * Updates the activation state of the Start server button depending on the
     * state of the various text input fields.
     */
    private void updateControls() {
	startButton.setEnabled(validDatabaseLocation && validPortNumber);
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
	    ValidityChangeListener {
	@Override
	public void validityGained(final ValidityChangeEvent e) {
	    validDatabaseLocation = true;
	    updateControls();
	}

	@Override
	public void validityLost(final ValidityChangeEvent e) {
	    validDatabaseLocation = false;
	    updateControls();
	}
    }

    /**
     * Validity change handler for the port number text input field. Depending
     * on the validity of the port number, the activation state of the Start
     * server button is updated via {@link updateControls}.
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
     * This handler is executed when the Start server button is pressed. The
     * contents of the text input fields (database location and port number) is
     * persisted and then the server is started.
     * 
     * @author Lars Kuettner
     * @version 1.0
     */
    private class StartServer implements ActionListener {
	/** {@inheritDoc} */
	@Override
	public void actionPerformed(final ActionEvent ae) {
	    databaseLocationField.setEnabled(false);
	    browseButton.setEnabled(false);
	    portNumberField.setEnabled(false);
	    exitButton.requestFocusInWindow();

	    // We know the database location must be valid because the start
	    // button had to be enabled to get here.
	    String location = databaseLocationField.getText().trim();
	    if (!location.equals(prevDatabaseLocation)) {
		config.setParameter(PersistentConfiguration.DATABASE_LOCATION,
			location);
		prevDatabaseLocation = location;
	    }
	    // We know the port number must be valid.
	    String port = portNumberField.getText();
	    if (!port.equals(prevPortNumber)) {
		config.setParameter(PersistentConfiguration.SERVER_PORT, port);
		prevPortNumber = port;
	    }

	    try {
		// Start the server. If this fails for whatever reason,
		// re-enable the configuration parameters input fields.
		log.fine(Text.SERVER_STARTING + " location=" + location
			+ ", port=" + port);
		statusLabel.setText(Text.SERVER_STARTING);

		ServerUtilities.startServer(location, port);

		log.fine(Text.SERVER_RUNNING);
		startButton.setEnabled(false);
		statusLabel.setText(Text.SERVER_RUNNING);
	    } catch (LaunchServerException e) {
		log.log(Level.WARNING, "CANT_START_SERVER" + " (location="
			+ location + ", port=" + port + ")", e);
		JOptionPane.showMessageDialog(mainFrame, Text.CANT_START_SERVER
			+ Text.NESTED_EXCEPTION_IS + e.getMessage(),
			Text.WARNING, JOptionPane.WARNING_MESSAGE);

		// Enable re-configuration to restart.
		databaseLocationField.setEnabled(true);
		browseButton.setEnabled(true);
		portNumberField.setEnabled(true);
		startButton.setEnabled(true);
		// Hand the focus over to the database location field.
		databaseLocationField.requestFocusInWindow();
		statusLabel.setText(Text.INITIAL_STATUS);
	    }
	}
    }

    /**
     * Handles all application exit events.
     * 
     * @author Lars Kuettner
     * @version 1.0
     */
    private class ExitApplication implements ActionListener {
	/**
	 * Exits the application when invoked.
	 * 
	 * @param ae
	 *            The event triggering the exit operation.
	 */
	public void actionPerformed(final ActionEvent ae) {
	    exitApplication();
	}
    }

    /**
     * Exits the server application. All the different ways to achieve this (via
     * the Exit button and closing the window) are bundled here. Before actually
     * exiting the application, a notification message is sent to all the remote
     * observers (network clients).
     */
    private void exitApplication() {
	log.fine("Exit the server application");

	exitButton.setEnabled(false);

	// Gracefully terminate the server by closing the database file.
	// Moreover, notify the network clients having registered as
	// observers for exactly this shutdown event.
	ServerUtilities.terminate();

	System.exit(0);
    }
}
