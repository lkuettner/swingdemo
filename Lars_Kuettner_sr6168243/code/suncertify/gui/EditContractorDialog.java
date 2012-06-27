/* 
 * @(#)EditContractorDialog    1.0 21/06/2010 
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import suncertify.db.DBSchema;
import suncertify.services.BookResult;
import suncertify.services.BusinessServices;
import suncertify.services.Contractor;
import suncertify.services.ServicesException;

/**
 * Dialog that displays and allows to edit the properties of a contractor.
 * Currently, only the editing of the owner field is supported; however, this
 * could be easily extended.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class EditContractorDialog extends JDialog {
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

    /** Logger object to log messages in the scope of this class. */
    private Logger log = Logger.getLogger(EditContractorDialog.class.getName());

    /** The text field to input or display the contractor's name. */
    private JTextField nameField = new JTextField(DBSchema
	    .getFieldLength(DBSchema.NAME_INDEX));
    /** The text field to input or display the contractor's location. */
    private JTextField locationField = new JTextField(DBSchema
	    .getFieldLength(DBSchema.LOCATION_INDEX));
    /** The text field to input or display the contractor's specialties. */
    private JTextField specialtiesField = new JTextField(DBSchema
	    .getFieldLength(DBSchema.SPECIALTIES_INDEX));
    /** The text field to input or display the contractor's size. */
    private JTextField sizeField = new JTextField(DBSchema
	    .getFieldLength(DBSchema.SIZE_INDEX));
    /** The text field to input or display the contractor's rate. */
    private JTextField rateField = new JTextField(DBSchema
	    .getFieldLength(DBSchema.RATE_INDEX));
    /** The text field to input or display the contractor's owner. */
    private CheckedTextField ownerField = new CheckedTextField(DBSchema
	    .getFieldLength(DBSchema.OWNER_INDEX), new AbstractTextChecker() {
	@Override
	public boolean isValidInputCharacter(final char c, final int offs) {
	    return Character.isDigit(c)
		    && offs < DBSchema.getFieldLength(DBSchema.OWNER_INDEX);
	}

	@Override
	public boolean isValidFieldText(final String fieldText) {
	    return fieldText.length() == DBSchema
		    .getFieldLength(DBSchema.OWNER_INDEX);
	}
    });

    /** The cancel action event handler. */
    private ActionListener cancelActionHandler = new ActionListener() {
	public void actionPerformed(final ActionEvent ae) {
	    leaveDialog(ae.getActionCommand());
	}
    };

    /**
     * An event handler that translates ENTER into a click on the OK button and
     * ESCAPE into a click on the CANCEL button.
     */
    private KeyListener textFieldEnterKeyHandler = new KeyListener() {
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
	    switch (ke.getKeyChar()) {
	    case KeyEvent.VK_ENTER:
		// Programmatically perform a click on the ok button if enabled.
		if (okButton.isEnabled()) {
		    okButton.doClick(BUTTON_HOLD_DOWN_TIME);
		}
		break;
	    case KeyEvent.VK_ESCAPE:
		// Programmatically perform a click on the cancel button.
		if (cancelButton.isEnabled()) {
		    cancelButton.doClick(BUTTON_HOLD_DOWN_TIME);
		}
		break;
	    default:
		// Ignore
		break;
	    }
	}
    };

    /** The OK button to process and leave the dialog. */
    private JButton okButton = new JButton(Text.OK);
    /** The CANCEL button to leave the dialog unprocessed. */
    private JButton cancelButton = new JButton(Text.CANCEL);
    /** Flag indicating whether the contents of the owner field is valid. */
    private boolean validOwner = false;

    /**
     * Creates the edit contractor dialog without displaying it.
     * 
     * @param parent
     *            the parent frame, typically the client main frame
     */
    public EditContractorDialog(final JFrame parent) {
	super(parent, Text.EDIT_CONTRACTOR_TITLE, true); // modal

	JPanel panel = new JPanel();
	GridBagLayout gridbag = new GridBagLayout();
	panel.setLayout(gridbag);

	addRow(panel, nameField, DBSchema.getFieldName(DBSchema.NAME_INDEX)
		+ ":", DBSchema
		.getFieldDetailedDescription(DBSchema.NAME_INDEX));
	addRow(panel, locationField, DBSchema
		.getFieldName(DBSchema.LOCATION_INDEX)
		+ ":", DBSchema
		.getFieldDetailedDescription(DBSchema.LOCATION_INDEX));
	addRow(panel, specialtiesField, DBSchema
		.getFieldName(DBSchema.SPECIALTIES_INDEX)
		+ ":", DBSchema
		.getFieldDetailedDescription(DBSchema.SPECIALTIES_INDEX));
	addRow(panel, sizeField, DBSchema.getFieldName(DBSchema.SIZE_INDEX)
		+ ":", DBSchema
		.getFieldDetailedDescription(DBSchema.SIZE_INDEX));
	addRow(panel, rateField, DBSchema.getFieldName(DBSchema.RATE_INDEX)
		+ ":", DBSchema
		.getFieldDetailedDescription(DBSchema.RATE_INDEX));
	addRow(panel, ownerField, DBSchema.getFieldName(DBSchema.OWNER_INDEX)
		+ ":", DBSchema
		.getFieldDetailedDescription(DBSchema.OWNER_INDEX));

	okButton.setActionCommand(Text.OK);
	// The OK button's action listener must be added individually.

	cancelButton.setActionCommand(Text.CANCEL);
	cancelButton.addActionListener(cancelActionHandler);

	JOptionPane optionPane = new JOptionPane(panel,
		JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

	optionPane.setOptions(new Object[] { okButton, cancelButton });

	setContentPane(optionPane);
	pack();
	setResizable(false);

	// Leads to windowClosing being called on "crossing out" dialog.
	setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	addWindowListener(new WindowAdapter() {
	    public void windowClosing(final WindowEvent we) {
		leaveDialog(Text.CANCEL);
	    }
	});
	setLocationRelativeTo(parent);
    }

    /**
     * Adds a row to a panel.
     * 
     * @param panel
     *            the panel with a grid bag layout to add the row to
     * @param textField
     *            the text input field to add to the row. May be a
     *            <code>CheckedTextField</code>.
     * @param labelText
     *            the label text preceding the text field
     * @param toolTip
     *            the tool tip text
     */
    private void addRow(final JPanel panel, final JTextField textField,
	    final String labelText, final String toolTip) {
	GridBagLayout gridbag = (GridBagLayout) panel.getLayout();
	GridBagConstraints constraints = new GridBagConstraints();
	// Ensure there is always a gap between components.
	constraints.insets = new Insets(2, 2, 2, 2);

	// Build the name row (label and text field).
	JLabel label = new JLabel(labelText);
	constraints.anchor = GridBagConstraints.EAST;
	constraints.gridwidth = 1;
	gridbag.setConstraints(label, constraints);
	panel.add(label);
	textField.setToolTipText(toolTip);
	textField.setName(labelText);
	constraints.anchor = GridBagConstraints.WEST;
	constraints.gridwidth = GridBagConstraints.REMAINDER; // end row
	gridbag.setConstraints(textField, constraints);
	panel.add(textField);
    }

    /**
     * Configures the dialog as a book contractor dialog and displays it.
     * Returns when the dialog has been made invisible after confirmation or
     * cancellation.
     * 
     * @param recNo
     *            the number of the contractor record to book
     * @param contractor
     *            the contractor object corresponding to the given
     *            <code>recNo</code>
     * @param businessServices
     *            an object implementing the <code>BusinessServices</code>
     *            interface for the <code>book</code> request to the model
     * @return a <code>BookResult</code> with <code>BookStatus</code> and
     *         <code>Contractor</code> if booking was actually attempted (OK
     *         button pressed), otherwise (Cancel button pressed)
     *         <code>null</code>.
     */
    public final BookResult bookContractor(final long recNo,
	    final Contractor contractor,
	    final BusinessServices businessServices) {
	validOwner = false;

	BookActionHandler bookActionHandler = new BookActionHandler(recNo,
		businessServices);
	okButton.addActionListener(bookActionHandler);

	okButton.setEnabled(validOwner);
	// Activate the cancel button (possibly still deactivated from previous
	// invocation).
	cancelButton.setEnabled(true);

	nameField.setText(contractor.getName());
	nameField.setEnabled(false);

	locationField.setText(contractor.getLocation());
	locationField.setEnabled(false);

	specialtiesField.setText(contractor.getSpecialties());
	specialtiesField.setEnabled(false);

	sizeField.setText(contractor.getSize());
	sizeField.setEnabled(false);

	rateField.setText(contractor.getRate());
	rateField.setEnabled(false);

	// To validate the initial text, it is crucial that the validity
	// listener be added before setting the text.
	ownerField.addValidityChangeListener(new ValidityChangeListener() {
	    @Override
	    public void validityGained(final ValidityChangeEvent ve) {
		validOwner = true;
		okButton.setEnabled(validOwner);
	    }

	    @Override
	    public void validityLost(final ValidityChangeEvent ve) {
		validOwner = false;
		okButton.setEnabled(validOwner);
	    }
	});
	ownerField.setText(contractor.getOwner());
	ownerField.setEnabled(true);
	ownerField.addKeyListener(textFieldEnterKeyHandler);

	setTitle(Text.BOOK_CONTRACTOR_TITLE);
	setLocationRelativeTo(getParent());

	ownerField.requestFocusInWindow();

	setVisible(true);

	okButton.removeActionListener(bookActionHandler);

	// Return true if OK button has been pushed.
	JOptionPane optionPane = (JOptionPane) getContentPane();
	if (optionPane.getValue() instanceof Integer) {
	    int status = ((Integer) (optionPane.getValue())).intValue();
	    if (status == JOptionPane.OK_OPTION) {
		return bookActionHandler.bookResult;
	    }
	}
	return null;
    }

    /**
     * Action handler for the action of pressing the book button. Initiates the
     * actual <code>book</code> operation.
     * <p>
     * Other handlers extending the application would adhere to the same
     * pattern: <code>UnbookActionHandler</code>,
     * <code>UpdateActionHandler</code>, <code>DeleteActionHandler</code>, ...
     * 
     * @author Lars Kuettner
     * @version 1.0
     */
    private class BookActionHandler implements ActionListener {
	/** The record number needed for reference purposes. */
	private long recNo;
	/** The business services interface to the model. */
	private BusinessServices businessServices;
	/** The result of the booking operation. */
	private BookResult bookResult;

	/**
	 * Creates an instance of a book action handler.
	 * 
	 * @param recNo
	 *            the number of the record to book
	 * @param businessServices
	 *            the business services model interface
	 */
	public BookActionHandler(final long recNo,
		final BusinessServices businessServices) {
	    this.recNo = recNo;
	    this.businessServices = businessServices;
	    bookResult = null;
	}

	@Override
	public void actionPerformed(final ActionEvent ae) {

	    ownerField.setEnabled(false);
	    okButton.setEnabled(false);
	    cancelButton.setEnabled(false);

	    Contractor contractor = new Contractor();

	    contractor.setName(nameField.getText());
	    contractor.setLocation(locationField.getText());
	    contractor.setSpecialties(specialtiesField.getText());
	    contractor.setSize(sizeField.getText());
	    contractor.setRate(rateField.getText());
	    contractor.setOwner(ownerField.getText());

	    // Activate controller book method, then display success or
	    // failure info dialog (notification).
	    try {
		bookResult = businessServices.book(recNo, contractor);

		switch (bookResult.getBookStatus()) {
		case SUCCESSFULLY_BOOKED:
		    // Notification about booking success.
		    log.fine("[" + Thread.currentThread().getName() + "-"
			    + Thread.currentThread().getId() + "] Record #"
			    + recNo + " successfully booked.");
		    JOptionPane.showMessageDialog(EditContractorDialog.this,
			    Text.CONTRACTOR_SUCCESSFULLY_BOOKED,
			    Text.SUCCESS_NOTIFICATION,
			    JOptionPane.INFORMATION_MESSAGE);
		    break;
		case INTERMITTENTLY_GRABBED:
		    // Notification about booking failure because some
		    // other agent has grabbed, i.e. booked this record
		    log.fine("[" + Thread.currentThread().getName() + "-"
			    + Thread.currentThread().getId() + "] Record #"
			    + recNo + " intermittently grabbed"
			    + " by some other agent.");
		    JOptionPane.showMessageDialog(EditContractorDialog.this,
			    Text.CONTRACTOR_INTERMITTENTLY_GRABBED,
			    Text.WARNING, JOptionPane.WARNING_MESSAGE);
		    break;
		case INTERMITTENTLY_UPDATED:
		    // Notification about booking failure. Subtle.
		    // Record is still available but has been updated
		    // intermittently. Booking may be retried.
		    log.fine("Record #" + recNo + " intermittently updated."
			    + " You may retry booking this record.");
		    JOptionPane.showMessageDialog(EditContractorDialog.this,
			    Text.CONTRACTOR_INTERMITTENTLY_UPDATED,
			    Text.WARNING, JOptionPane.WARNING_MESSAGE);
		    break;
		case INTERMITTENTLY_DELETED:
		    // Notification about booking failure because record
		    // does not exist anymore.
		    log.fine("Record #" + recNo + " intermittently deleted.");
		    JOptionPane.showMessageDialog(EditContractorDialog.this,
			    Text.CONTRACTOR_INTERMITTENTLY_DELETED,
			    Text.WARNING, JOptionPane.WARNING_MESSAGE);
		    break;
		default:
		    break;
		}
	    } catch (ServicesException e) {
		// Booking operation failed.
		log.log(Level.SEVERE, "Can't book record #" + recNo, e);
		JOptionPane.showMessageDialog(EditContractorDialog.this,
			Text.BOOK_OPERATION_FAILED + Text.NESTED_EXCEPTION_IS
				+ e.getMessage(), "Error",
			JOptionPane.ERROR_MESSAGE);
	    } catch (RemoteException e) {
		// Booking operation failed.
		log.log(Level.SEVERE, "Can't book record #" + recNo, e);
		JOptionPane.showMessageDialog(EditContractorDialog.this,
			Text.BOOK_OPERATION_FAILED + Text.NESTED_EXCEPTION_IS
				+ e.getMessage(), "Error",
			JOptionPane.ERROR_MESSAGE);
	    }
	    leaveDialog(okButton.getActionCommand());
	}
    }

    /**
     * Sets the dialog's value for further reference before changing its
     * visibility status to invisible.
     * 
     * @param command
     *            the previously set command string to identify the source of
     *            this method invocation
     */
    private void leaveDialog(final String command) {
	JOptionPane optionPane = (JOptionPane) getContentPane();
	if (Text.OK.equals(command)) {
	    optionPane.setValue(JOptionPane.OK_OPTION);
	} else {
	    optionPane.setValue(JOptionPane.CANCEL_OPTION);
	}
	setVisible(false);
    }

    /**
     * Displays a notification dialog about the server being shut down
     * regardless of whether the edit contractor dialog itself is currently
     * visible or not.
     */
    public final void deactivate() {
	// Disable everything
	nameField.setEnabled(false);
	locationField.setEnabled(false);
	specialtiesField.setEnabled(false);
	sizeField.setEnabled(false);
	rateField.setEnabled(false);
	ownerField.setEnabled(false);
	okButton.setEnabled(false);
	cancelButton.setEnabled(false);

	// Display notification about server going down
	JOptionPane.showMessageDialog(isVisible() ? this : getParent(),
		Text.SERVER_IS_GOING_DOWN,
		Text.SERVER_TERMINATION_NOTIFICATION,
		JOptionPane.WARNING_MESSAGE);

	if (isVisible()) {
	    setVisible(false);
	}
    }
}
