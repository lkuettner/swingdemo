/* 
 * @(#)ConfigurationPanel    1.0 21/06/2010 
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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Configurable configuration panel providing the building blocks to compose the
 * various configuration dialogs (in the case of the network and standalone
 * clients) or integrated panel (in the case of the server). This design helps
 * to avoid duplicate configuration code.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class ConfigurationPanel extends JPanel {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /** The number of columns of the server hostname field. */
    private static final int SERVER_HOSTNAME_FIELD_LENGTH = 24;
    /** The number of columns of the database location field. */
    private static final int DATABASE_LOCATION_FIELD_LENGTH = 42;
    /** The number of columns of the port number field. */
    private static final int PORT_NUMBER_FIELD_LENGTH = 5;

    /** The lowest accepted port number marking the lower range boundary. */
    private static final int LOWEST_PORT = 0;
    /** The highest accepted port number marking the upper range boundary. */
    private static final int HIGHEST_PORT = 65535;

    /** The initial path at which to search for the database file. */
    private String databaseLocationPathName = ".";

    /** The database location checked text input field. */
    private CheckedTextField databaseLocationField = null;

    /** The grid bag layout needed by all components. */
    private GridBagLayout gridbag = new GridBagLayout();

    /**
     * Creates an empty configuration panel. Some row must subsequently be added
     * via <code>addServerHostnameRow</code>,
     * <code>addDatabaseLocationRow</code>, and/or
     * <code>addPortNumberRow.</code>
     */
    public ConfigurationPanel() {
	setLayout(gridbag);
    }

    /**
     * Builds the server hostname row and adds it to the configuration panel.
     * This row consists of a suitable label, followed by a text input field.
     * 
     * @return the checked text input field allowing validity checks while
     *         entering text
     */
    public final CheckedTextField addServerHostnameRow() {
	CheckedTextField serverHostnameField = new CheckedTextField(
		SERVER_HOSTNAME_FIELD_LENGTH, new AbstractTextChecker() {
		    @Override
		    public boolean isValidInputCharacter(final char c,
			    final int offs) {
			return Character.isDigit(c) || Character.isLetter(c)
				|| c == '-' || c == '.';
		    }

		    @Override
		    public boolean isValidFieldText(final String fieldText) {
			return !fieldText.isEmpty();
		    }
		});

	GridBagConstraints constraints = new GridBagConstraints();
	// Ensure there is always a gap between components.
	constraints.insets = new Insets(2, 2, 2, 2);

	// Build the server host name row.
	JLabel serverHostnameLabel = new JLabel(Text.SERVER_HOSTNAME_LABEL);
	constraints.anchor = GridBagConstraints.EAST;
	constraints.gridwidth = 1;
	gridbag.setConstraints(serverHostnameLabel, constraints);
	add(serverHostnameLabel);

	serverHostnameField.setToolTipText(Text.SERVER_HOSTNAME_TOOL_TIP);
	serverHostnameField.setName(Text.SERVER_HOSTNAME_LABEL);
	constraints.anchor = GridBagConstraints.WEST;
	constraints.gridwidth = GridBagConstraints.REMAINDER; // end row
	gridbag.setConstraints(serverHostnameField, constraints);
	add(serverHostnameField);

	return serverHostnameField;
    }

    /**
     * Builds the database location row and adds it to the configuration panel.
     * This row consists of a suitable label, followed by a text input field,
     * followed by a browse button to raise a file chooser dialog.
     * 
     * @param browseButton
     *            a <code>JButton</code> representing the pre-constructed browse
     *            button to be put in the row
     * @return the checked text input field allowing validity checks while
     *         entering text
     */
    public final CheckedTextField addDatabaseLocationRow(
	    final JButton browseButton) {
	GridBagConstraints constraints = new GridBagConstraints();
	// Ensure there is always a gap between components.
	constraints.insets = new Insets(2, 2, 2, 2);

	JLabel databaseLocationLabel = new JLabel(Text.DATABASE_LOCATION_LABEL);
	constraints.gridwidth = 1;
	gridbag.setConstraints(databaseLocationLabel, constraints);
	add(databaseLocationLabel);

	// The database location field is a text field being checked while
	// input is taking place. To check the text, a text checker and a
	// a validity change listener are provided.
	databaseLocationField = new CheckedTextField(
		DATABASE_LOCATION_FIELD_LENGTH, new AbstractTextChecker() {
		    public boolean isValidFieldText(final String fieldText) {
			File f = new File(fieldText);
			return f.exists() && f.canRead() && f.canWrite();
		    }
		});
	databaseLocationField.setToolTipText(Text.DATABASE_LOCATION_TOOL_TIP);
	databaseLocationField.setName(Text.DATABASE_LOCATION_LABEL);

	// Second and next-to-last in row.
	constraints.gridwidth = GridBagConstraints.RELATIVE;
	gridbag.setConstraints(databaseLocationField, constraints);
	add(databaseLocationField);

	browseButton.addActionListener(new BrowseForDatabaseLocation());
	constraints.gridwidth = GridBagConstraints.REMAINDER; // end row
	gridbag.setConstraints(browseButton, constraints);
	add(browseButton);

	return databaseLocationField;
    }

    /**
     * Builds the port number row and adds it to the configuration panel. This
     * row consists of a suitable label, followed by a text input field.
     * 
     * @return the checked text input field allowing validity checks while
     *         entering text
     */
    public final CheckedTextField addPortNumberRow() {
	CheckedTextField portNumberField = null;

	GridBagConstraints constraints = new GridBagConstraints();
	// Ensure there is always a gap between components.
	constraints.insets = new Insets(2, 2, 2, 2);

	// Build the Server port row.
	JLabel portNumberLabel = new JLabel(Text.SERVER_PORT_LABEL);
	// constraints.weightx = 0.0;
	constraints.gridwidth = 1;
	constraints.anchor = GridBagConstraints.EAST;
	gridbag.setConstraints(portNumberLabel, constraints);
	add(portNumberLabel);
	// To validate the initial text, it is crucial that the validity
	// listener be added before setting the initial text.
	portNumberField = new CheckedTextField(PORT_NUMBER_FIELD_LENGTH,
		new TextChecker() {
		    @Override
		    public boolean isValidInputCharacter(final char c,
			    final int offs) {
			return Character.isDigit(c)
				&& offs < PORT_NUMBER_FIELD_LENGTH;
		    }

		    @Override
		    public boolean isValidFieldText(final String fieldText) {
			if (!fieldText.isEmpty()) {
			    int port = Integer.parseInt(fieldText);
			    return port >= LOWEST_PORT && port <= HIGHEST_PORT;
			}
			return false;
		    }
		});
	portNumberField.setToolTipText(Text.SERVER_PORT_TOOL_TIP);
	portNumberField.setName(Text.SERVER_PORT_LABEL);
	constraints.gridwidth = GridBagConstraints.REMAINDER; // end row
	constraints.anchor = GridBagConstraints.WEST;
	gridbag.setConstraints(portNumberField, constraints);
	add(portNumberField);

	return portNumberField;
    }

    /**
     * A utility class that provides the user with the ability to browse for the
     * database rather than forcing them to remember (and type in) a fully
     * qualified database location.
     */
    private class BrowseForDatabaseLocation implements ActionListener {
	/** {@inheritDoc} */
	public void actionPerformed(final ActionEvent ae) {
	    JFileChooser chooser = new JFileChooser(databaseLocationPathName);
	    chooser.addChoosableFileFilter(
		    new javax.swing.filechooser.FileFilter() {
			/**
			 * display files ending in ".db" or any other object
			 * (directory or other selectable device).
			 */
			public boolean accept(final File f) {
			    if (f.isFile()) {
				return f.getName().endsWith(
					Text.DATABASE_EXTENSION);
			    } else {
				return true;
			    }
			}

			/**
			 * provide a description for the types of files we are
			 * allowing to be selected.
			 */
			public String getDescription() {
			    return Text.DATABASE_FILE_CHOOSER_DESCRIPTION;
			}
		    });

	    // if the user selected a file, update the file name on screen
	    if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(null)) {
		File selectedFile = chooser.getSelectedFile();
		String fieldText = selectedFile.toString();
		databaseLocationField.setText(fieldText);
		databaseLocationPathName = selectedFile.getPath();
		// Put the focus away from the browse button and back into the
		// related input text field.
		databaseLocationField.requestFocusInWindow();
	    }
	}
    }
}
