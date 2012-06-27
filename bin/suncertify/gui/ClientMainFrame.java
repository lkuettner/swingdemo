/* 
 * @(#)ClientMainFrame    1.0 21/06/2010 
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

import suncertify.db.DBSchema;
import suncertify.services.BookResult;
import suncertify.services.BusinessServices;
import suncertify.services.Contractor;
import suncertify.services.ServicesException;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The client main application window (network and standalone alike) with the
 * menu bar, search panel, results table, and function panel.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public class ClientMainFrame extends JFrame
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
   * Logger object to log messages in the scope of this class.
   */
  private final Logger log = Logger
    .getLogger(ClientMainFrame.class.getName());

  /**
   * The preferred width of the client main frame.
   */
  private static final int WINDOW_PREFERRED_SIZE_X = 800;
  /**
   * The preferred height of the client main frame.
   */
  private static final int WINDOW_PREFERRED_SIZE_Y = 600;
  /**
   * The minimum height of the client main frame.
   */
  private static final int WINDOW_MINIMUM_SIZE_Y = 250;

  /**
   * The length of the name search field, in columns or characters.
   */
  private static final int NAME_FIELD_LENGTH = Math.min(DBSchema
    .getFieldLength(DBSchema.NAME_INDEX), DBSchema
    .getFieldLength(DBSchema.LOCATION_INDEX));
  /**
   * The length of the location search field, in columns or characters.
   */
  private static final int LOCATION_FIELD_LENGTH = Math.min(DBSchema
    .getFieldLength(DBSchema.NAME_INDEX), DBSchema
    .getFieldLength(DBSchema.LOCATION_INDEX));

  /**
   * An experimentally determined factor to multiply to each column width, for
   * a pleasant display, at least under Windows.
   */
  private static final int COLUMN_WIDTH_FACTOR = 3;

  /**
   * The recyclable dialog to edit properties of a contractor.
   */
  private final EditContractorDialog editContractorDialog =
    new EditContractorDialog(this);

  /**
   * The business services interface implementation (local or remote).
   */
  private BusinessServices businessServices = null;

  /**
   * The <code>JTable</code> that displays the contractor records found by the
   * search mechanism.
   */
  private final JTable table = new JTable()
  {

    private static final long serialVersionUID = 4711L;

    private String[] columnToolTips = {
      DBSchema.getFieldDescription(DBSchema.NAME_INDEX),
      DBSchema.getFieldDescription(DBSchema.LOCATION_INDEX),
      DBSchema.getFieldDescription(DBSchema.SPECIALTIES_INDEX),
      DBSchema.getFieldDescription(DBSchema.SIZE_INDEX),
      DBSchema.getFieldDescription(DBSchema.RATE_INDEX),
      DBSchema.getFieldDescription(DBSchema.OWNER_INDEX) };

    // Implement table header tool tips.
    protected JTableHeader createDefaultTableHeader()
    {
      return new JTableHeader(columnModel)
      {

        private static final long serialVersionUID = 4711L;

        public String getToolTipText(final MouseEvent e)
        {
          java.awt.Point p = e.getPoint();
          int index = columnModel.getColumnIndexAtX(p.x);
          int realIndex = columnModel.getColumn(index)
            .getModelIndex();
          return columnToolTips[realIndex];
        }
      };
    }
  };
  /**
   * The internal data model for the table display.
   */
  private final ContractorTableModel tableData = new ContractorTableModel();
  /**
   * The name search input field.
   */
  private final JTextField nameSearchField =
    new JTextField(NAME_FIELD_LENGTH);
  /**
   * The location search input field.
   */
  private final JTextField locationSearchField = new JTextField(
    LOCATION_FIELD_LENGTH);
  /**
   * The button to search for records in the database. Will be grayed if the
   * server is down.
   */
  private JButton searchButton;
  /**
   * The button to book the selected contractor. Will be active only if the
   * contractor has not already been booked.
   */
  private JButton bookButton;
  /**
   * The search name of the most recent search. Needed to re-search.
   */
  private String prevSearchName = null;
  /**
   * The search location of the most recent search. Needed to re-search.
   */
  private String prevSearchLocation = null;
  /**
   * A key listener used to handle pressing the Enter key on the name and
   * location input text fields, to programmatically emulate a click on the
   * search button.
   */
  private KeyListener searchFieldEnterKeyHandler = new KeyListener()
  {
    //        @Override
    public void keyPressed(final KeyEvent ke)
    {
      // Keep idle - no action.
    }

    //        @Override
    public void keyReleased(final KeyEvent ke)
    {
      // Keep idle - no action.
    }

    //        @Override
    public void keyTyped(final KeyEvent ke)
    {
      if (ke.getKeyChar() == KeyEvent.VK_ENTER)
      {
        // Programmatically perform a click on the search button.
        searchButton.doClick(BUTTON_HOLD_DOWN_TIME);
      }
    }
  };

  /**
   * Builds and displays the main application window.
   *
   * @param title a string representing the title of the application window
   */
  public ClientMainFrame(final String title)
  {
    super(title);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Add the menu bar.
    setJMenuBar(makeMenuBar());

    // Add the content pane.
    add(makeContentPane());

    // Set table properties.
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.setToolTipText(Text.TABLE_TOOL_TIP_TEXT);

    // Provide a table model with no records in it.
    table.setModel(tableData);

    // Create and activate the default row sorter.
    table.setAutoCreateRowSorter(true);

    // Initially, sort all columns in ascending order.
    List<SortKey> sortKeys = new ArrayList<SortKey>();
    for (int i = 0; i < table.getColumnCount(); ++i)
    {
      sortKeys.add(new SortKey(i, SortOrder.ASCENDING));
    }
    table.getRowSorter().setSortKeys(sortKeys);

    // Size and rate need their own comparators as String comparisons won't
    // do.
    TableRowSorter<? extends TableModel> sorter =
      (TableRowSorter<? extends TableModel>) table.getRowSorter();
    sorter.setComparator(DBSchema.SIZE_INDEX, new SizeComparator());
    sorter.setComparator(DBSchema.RATE_INDEX, new RateComparator());

    table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

    TableColumnModel colmod = table.getColumnModel();
    colmod.getColumn(DBSchema.NAME_INDEX).setPreferredWidth(
      DBSchema.getFieldLength(DBSchema.NAME_INDEX)
        * COLUMN_WIDTH_FACTOR);
    colmod.getColumn(DBSchema.LOCATION_INDEX).setPreferredWidth(
      DBSchema.getFieldLength(DBSchema.LOCATION_INDEX)
        * COLUMN_WIDTH_FACTOR);
    colmod.getColumn(DBSchema.SPECIALTIES_INDEX).setPreferredWidth(
      DBSchema.getFieldLength(DBSchema.SPECIALTIES_INDEX)
        * COLUMN_WIDTH_FACTOR);
    colmod.getColumn(DBSchema.SIZE_INDEX).setPreferredWidth(
      DBSchema.getFieldLength(DBSchema.SIZE_INDEX)
        * COLUMN_WIDTH_FACTOR);
    colmod.getColumn(DBSchema.RATE_INDEX).setPreferredWidth(
      DBSchema.getFieldLength(DBSchema.RATE_INDEX)
        * COLUMN_WIDTH_FACTOR);
    colmod.getColumn(DBSchema.OWNER_INDEX).setPreferredWidth(
      DBSchema.getFieldLength(DBSchema.OWNER_INDEX)
        * COLUMN_WIDTH_FACTOR);

    table.getSelectionModel().addListSelectionListener(
      new ListSelectionListener()
      {
        // @Override
        public void valueChanged(final ListSelectionEvent e)
        {
          ListSelectionModel lsm = (ListSelectionModel) e
            .getSource();
          boolean stillAvailable = false;
          if (!lsm.isSelectionEmpty())
          {
            // Find out which indexes are selected.
            int selRowVw = lsm.getMinSelectionIndex();
            int selRow = table.convertRowIndexToModel(selRowVw);
            Contractor c = tableData.getContractorAt(selRow);
            stillAvailable = c.getOwner().isEmpty();
          }
          bookButton.setEnabled(stillAvailable
            && businessServices != null);
        }
      });

    // Enable DnD support. Allows to drag a row to one of the search fields
    // so that the name and/or location mustn't be typed manually.
    table.setDragEnabled(true);

    table.setTransferHandler(new FromTableTransferHandler());

    pack();

    // Let the framework determine the minimum x size by itself.
    Dimension autoPackedSize = getSize();
    setMinimumSize(new Dimension(autoPackedSize.width,
      WINDOW_MINIMUM_SIZE_Y));
    int prefWidth = Math.max(autoPackedSize.width, WINDOW_PREFERRED_SIZE_X);
    setPreferredSize(new Dimension(prefWidth, WINDOW_PREFERRED_SIZE_Y));
    setSize(prefWidth, WINDOW_PREFERRED_SIZE_Y);

    // Place the frame to the center of the screen.
    setLocationRelativeTo(null);

    // Disable book button as there is no selection yet.
    bookButton.setEnabled(false);

    // Disable everything as it cannot be used yet anyway.
    nameSearchField.setEnabled(false);
    locationSearchField.setEnabled(false);
    searchButton.setEnabled(false);
    table.setEnabled(false);

    // Display the frame.
    setVisible(true);
  }

  /**
   * Makes the menu bar.
   *
   * @return the menu bar just created
   */
  private JMenuBar makeMenuBar()
  {
    JMenuBar menuBar = new JMenuBar();
    JMenu fileMenu = new JMenu("File");
    JMenuItem exitMenuItem = new JMenuItem("Exit");
    exitMenuItem.addActionListener(new ExitApplication());
    exitMenuItem.setMnemonic(KeyEvent.VK_X);
    fileMenu.add(exitMenuItem);
    fileMenu.setMnemonic(KeyEvent.VK_F);
    menuBar.add(fileMenu);
    return menuBar;
  }

  /**
   * Composes the contents of the main client window (without the menu).
   *
   * @return the the main client window panel
   */
  private JPanel makeContentPane()
  {

    JPanel panel = new JPanel(new BorderLayout());

    JScrollPane tableScroll = new JScrollPane(table);

    panel.add(tableScroll, BorderLayout.CENTER);

    // Make the search panel to be positioned in the north.
    panel.add(makeSearchPanel(), BorderLayout.NORTH);

    // Make the function panel to be positioned in the south.
    panel.add(makeFunctionPanel(), BorderLayout.SOUTH);

    return panel;
  }

  /**
   * Makes the search panel located above the table.
   *
   * @return the search panel
   */
  private JPanel makeSearchPanel()
  {
    JPanel panel = new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    panel.setLayout(gridbag);

    GridBagConstraints constraints = new GridBagConstraints();
    // Ensure there is always a gap between components.
    constraints.insets = new Insets(2, 2, 2, 2);

    // Name
    JLabel nameLabel = new JLabel(DBSchema
      .getFieldName(DBSchema.NAME_INDEX)
      + ":");
    constraints.anchor = GridBagConstraints.EAST;
    gridbag.setConstraints(nameLabel, constraints);
    panel.add(nameLabel);

    nameSearchField.setText("");
    nameSearchField.setToolTipText(Text.SEARCH_NAME_TOOL_TIP);
    // nameSearchField.setName(SEARCH_NAME_LABEL);
    // Hitting Enter while focus is in the name search field will
    // immediately trigger a search.
    nameSearchField.addKeyListener(searchFieldEnterKeyHandler);
    // Set transfer handler for drop and paste.
    constraints.anchor = GridBagConstraints.WEST;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(nameSearchField, constraints);
    panel.add(nameSearchField);

    // Location
    JLabel locationLabel = new JLabel(DBSchema
      .getFieldName(DBSchema.LOCATION_INDEX)
      + ":");
    constraints.anchor = GridBagConstraints.EAST;
    constraints.gridwidth = 1;
    gridbag.setConstraints(locationLabel, constraints);
    panel.add(locationLabel);

    locationSearchField.setText("");
    locationSearchField.setToolTipText(Text.SEARCH_LOCATION_TOOL_TIP);
    // locationSearchField.setName(SEARCH_LOCATION_LABEL);
    // Hitting Enter while focus is in the location search field will
    // immediately trigger a search.
    locationSearchField.addKeyListener(searchFieldEnterKeyHandler);
    // Set transfer handler for drop and paste.
    constraints.anchor = GridBagConstraints.WEST;
    constraints.gridwidth = GridBagConstraints.RELATIVE;
    gridbag.setConstraints(locationSearchField, constraints);
    panel.add(locationSearchField);

    // Search Button
    searchButton = new JButton(Text.SEARCH_BUTTON_TEXT);
    searchButton.setName(Text.SEARCH_BUTTON_TEXT);
    searchButton.setMnemonic(KeyEvent.VK_S);
    searchButton.setToolTipText(Text.SEARCH_BUTTON_TOOL_TIP);
    // Make the search button refuse focus.
    searchButton.addActionListener(new SearchActionHandler());
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    gridbag.setConstraints(searchButton, constraints);
    panel.add(searchButton);

    panel.setBorder(BorderFactory.createEtchedBorder());

    JPanel aroundPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    aroundPanel.add(panel);
    return aroundPanel;
  }

  /**
   * Makes the function panel below the table.
   *
   * @return the function panel
   */
  private JPanel makeFunctionPanel()
  {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

    // Add book button.
    bookButton = new JButton(Text.BOOK_BUTTON_TEXT);
    bookButton.setMnemonic(KeyEvent.VK_B);
    bookButton.setToolTipText(Text.BOOK_BUTTON_TOOL_TIP);
    // Make the book button refuse focus.
    // Add an action listener.
    bookButton.setName(Text.BOOK_BUTTON_TEXT);
    bookButton.addActionListener(new SelectedRecordActionHandler());
    panel.add(bookButton);

    panel.setBorder(BorderFactory.createEtchedBorder());

    JPanel aroundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    aroundPanel.add(panel);

    return aroundPanel;
  }

  /**
   * Handler for the Search button-pressed event.
   *
   * @author Lars Kuettner
   * @version 1.0
   */
  private class SearchActionHandler implements ActionListener
  {
    //        @Override
    public void actionPerformed(final ActionEvent ae)
    {
      String name = nameSearchField.getText().trim();
      String location = locationSearchField.getText().trim();
      if (name.equals(""))
      {
        name = null;
      }
      if (location.equals(""))
      {
        location = null;
      }
      prevSearchName = name;
      prevSearchLocation = location;
      doSearch();
    }
  }

  /**
   * Performs the database search operation with previously set name and
   * location. Normally, the name and location would be set in the action
   * listener of the search button, which would then trigger a
   * <code>doSearch</code>. However, if <code>doSearch</code> is called
   * directly, the most recent search (a re-search) is repeated. The method is
   * first called immediately after establishing a connection to the server to
   * fill the table with all records available in the database.
   */
  public final void doSearch()
  {
    // Preserve the selection on the old table data.
    int selRowVw = table.getSelectedRow();
    long selRecNo = -1;
    if (selRowVw >= 0)
    {
      int selRow = table.convertRowIndexToModel(selRowVw);
      selRecNo = tableData.getRecordNumberAt(selRow);
    }
    // Perform the search operation.
    try
    {
      // Collect the search results into a map of recNo, Contractor pairs.
      Map<Long, Contractor> contractors = businessServices.search(
        prevSearchName, prevSearchLocation);
      // Convert map to table model.
      tableData.setContents(contractors);

      // Update of entire table - data only. Structure remains as is.
      tableData.fireTableDataChanged();

      // Reselect the previously selected record if it still exists.
      if (selRecNo >= 0)
      {
        for (int row = 0; row < table.getRowCount(); ++row)
        {
          if (tableData.getRecordNumberAt(row) == selRecNo)
          {
            int rowVw = table.convertRowIndexToView(row);
            // Select row.
            table.setRowSelectionInterval(rowVw, rowVw);
            // Scroll automatically so that selected row is always
            // visible, even if previously outside scroll view.
            Rectangle rect = table.getCellRect(rowVw, 0, false);
            table.scrollRectToVisible(rect);

            table.requestFocusInWindow();
            break;
          }
        }
      }
    }
    catch (ServicesException e)
    {
      log.log(Level.SEVERE, "Can't search (services exception)", e);
      JOptionPane.showMessageDialog(this, Text.SEARCH_OPERATION_FAILED
        + Text.REASON_IS + e.getMessage(), Text.ERROR,
        JOptionPane.ERROR_MESSAGE);
    }
    catch (RemoteException e)
    {
      log.log(Level.SEVERE, "Can't search (remote exception)", e);
      JOptionPane.showMessageDialog(this, Text.SEARCH_OPERATION_FAILED
        + Text.REASON_IS + e.getMessage(), Text.ERROR,
        JOptionPane.ERROR_MESSAGE);
    }
  }

  /**
   * Comparator for the size field. Company sizes must be compared as integers
   * rather than strings.
   *
   * @author Lars Kuettner
   * @version 1.0
   */
  private class SizeComparator implements Comparator<String>
  {
    //        @Override
    public int compare(final String sz1, final String sz2)
    {
      int i1 = Integer.parseInt(sz1);
      int i2 = Integer.parseInt(sz2);
      return compareIntegers(i1, i2);
    }
  }

  /**
   * Comparator for the rate field. For the sake of simplicity, currencies
   * including the currency symbol are compared as numbers excluding the
   * currency symbol rather than strings. The actual denomination of the
   * currency is not taken into account. So, $100.00 would be considered equal
   * to EUR 100.00.
   *
   * @author Lars Kuettner
   * @version 1.0
   */
  private class RateComparator implements Comparator<String>
  {
    //        @Override
    public int compare(final String rate1, final String rate2)
    {
      // Eliminate (filter) the arbitrary-length currency symbol.
      int i1 = extractCentsWithoutCurrencySymbol(rate1);
      int i2 = extractCentsWithoutCurrencySymbol(rate2);
      // Compare the cent values in disregard of the currency symbol (even
      // if they differed).
      return compareIntegers(i1, i2);
    }

    /**
     * Strips the currency string off the currency symbol, then parses the
     * currency denomination as a double value and converts it into an
     * integer.
     *
     * @param rate the rate string consisting of the currency symbol,
     *             followed by a currency value
     * @return the currency value times one hundred as an integer
     */
    private int extractCentsWithoutCurrencySymbol(final String rate)
    {
      int i;
      for (i = 0; i < rate.length(); ++i)
      {
        char c = rate.charAt(i);
        // A digit or a minus sign marks the beginning of the numerical
        // part following the currency symbol.
        if (Character.isDigit(c))
        {
          String numericalPart = rate.substring(i);
          double dollars = Double.parseDouble(numericalPart);
          int cents = (int) Math.floor(dollars * 100.0 + 0.5);
          return cents;
        }
      }
      // The field contents is not recognized as a currency string, yet
      // this should not derail the entire application.
      log.warning("Format error in rate string: \"" + rate + "\"");
      return Integer.MIN_VALUE;
    }
  }

  /**
   * Compares two integer values.
   *
   * @param i1 the first integer value
   * @param i2 the second integer value
   * @return -1 if <code>i1 < i2</code>, 1 if <code>i1 > i2</code>, 0 if
   *         <code>i1 == i2</code>
   */
  private int compareIntegers(final int i1, final int i2)
  {
    if (i1 < i2)
    {
      return -1;
    }
    else if (i1 > i2)
    {
      return 1;
    }
    else
    {
      return 0;
    }
  }

  /**
   * Handler for the Book button-pressed event (and possibly other buttons in
   * the future, like unbook, add and delete record).
   *
   * @author Lars Kuettner
   * @version 1.0
   */
  private class SelectedRecordActionHandler implements ActionListener
  {
    //        @Override
    public void actionPerformed(final ActionEvent ae)
    {
      if (Text.BOOK_BUTTON_TEXT.equals(ae.getActionCommand()))
      {
        // Get the selected row (there should be one).
        int selRowVw = table.getSelectedRow();
        if (selRowVw >= 0)
        { // Should always hold!
          int selRow = table.convertRowIndexToModel(selRowVw);
          long recNo = tableData.getRecordNumberAt(selRow);
          Contractor contractor = tableData.getContractorAt(selRow);

          // Open record editing dialog with everything but the owner
          // field grayed. This dialog has an ok button that will be
          // activated as soon as a valid owner has been entered.
          BookResult bookResult = editContractorDialog
            .bookContractor(recNo, contractor,
              businessServices);

          if (bookResult != null)
          {
            Contractor updatedContractor = bookResult
              .getContractor();
            if (updatedContractor != null)
            {
              // Update table. recNo remains unchanged.
              tableData.replaceContractorAt(selRow,
                updatedContractor);
            }
            else
            { // Contractor deleted. Delete row.
              // Won't be called as yet as currently there is no
              // delete record button.
              tableData.deleteRecordAt(selRow);
            }
            table.repaint();
          }
          table.requestFocusInWindow();

          // Update table but only if edit dialog has been left by
          // clicking the OK button. Deliberately do not update on
          // cancel or window closing (where bookResult == null).
          if (bookResult != null && businessServices != null)
          {
            // Refresh table by repeating the most recent search.
            doSearch();
          }
        }
        else
        {
          log.severe("Book button clicked yet no table row selected");
          assert false;
        }
      }
    }
  }

  /**
   * Transfer handler to enable Drag-and-drop support for the database records
   * table, to the effect that a single table field can be dragged from the
   * table (to a search input text field, for instance) rather than a whole
   * table row (as would be the default).
   *
   * @author Lars Kuettner
   * @version 1.0
   */
  private class FromTableTransferHandler extends TransferHandler
  {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    @Override
    public int getSourceActions(final JComponent arg0)
    {
      return TransferHandler.COPY;
    }

    @Override
    protected Transferable createTransferable(final JComponent component)
    {
      JTable t = (JTable) component;
      String selectedFieldValue = null;
      int selRowVw = t.getSelectedRow();
      int selColumnVw = t.getSelectedColumn();
      if (selRowVw >= 0 && selColumnVw >= 0)
      { // Should always hold!
        int selRow = t.convertRowIndexToModel(selRowVw);
        int selColumn = t.convertColumnIndexToModel(selColumnVw);

        ContractorTableModel ctm = (ContractorTableModel) t.getModel();

        selectedFieldValue = (String) ctm.getValueAt(selRow, selColumn);
      }
      return new StringSelection(selectedFieldValue);
    }
  }

  /**
   * Handles all application exit events.
   *
   * @author Lars Kuettner
   * @version 1.0
   */
  private class ExitApplication implements ActionListener
  {
    /**
     * Exits the application when invoked.
     *
     * @param ae The event triggering the exit operation.
     */
    public void actionPerformed(final ActionEvent ae)
    {
      System.exit(0);
    }
  }

  /**
   * Deactivates the client main frame in response to a server shutdown
   * notification.
   * <p/>
   * Called in the context of the network client on notification that server
   * is going down. This is a notification from the server that the server is
   * about to be going down. There may be no further requests to the server.
   * The only thing this client application should be allowed to do is exit.
   * In case of the standalone setting there is no server, so the terminology
   * is chosen appropriately.
   */
  public final void deactivate()
  {
    // Setting the business services member to null means the book button
    // will never again become enabled.
    businessServices = null;

    // Disable all the buttons that would call a business services method.
    nameSearchField.setEnabled(false);
    locationSearchField.setEnabled(false);
    searchButton.setEnabled(false);
    bookButton.setEnabled(false);
    table.setEnabled(false);

    // The edit contractor sub-dialog may be displayed.
    editContractorDialog.deactivate();
  }

  /**
   * Sets the business services member field, an object on which to conduct
   * the search and book operations.
   *
   * @param businessServices the business services object
   */
  public final void setBusinessServices(
    final BusinessServices businessServices)
  {
    this.businessServices = businessServices;

    // Enable everything initially deliberately disabled.
    nameSearchField.setEnabled(true);
    locationSearchField.setEnabled(true);
    searchButton.setEnabled(true);
    // book button gets special treatment
    table.setEnabled(true);

    // Put the focus into the name search field.
    nameSearchField.requestFocusInWindow();
  }
}
