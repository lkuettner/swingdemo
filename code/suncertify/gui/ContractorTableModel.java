/* 
 * @(#)ContractorTableModel    1.0 21/06/2010 
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
import suncertify.services.Contractor;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * The table model for the main <code>JTable</code> used to display the database
 * records.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public class ContractorTableModel extends AbstractTableModel
{
  /**
   * A magic version number for this class so that serialization can occur
   * without worrying about the underlying class changing between
   * serialization and deserialization.
   */
  private static final long serialVersionUID = 4711L;

  /**
   * The list of contractors to be displayed in the table.
   */
  private ArrayList<String[]> contractors = new ArrayList<String[]>();
  /**
   * The list of record numbers corresponding to the contractors' list.
   */
  private ArrayList<Long> recordNumbers = new ArrayList<Long>();

  /**
   * Creates new instance of a table model to display contractor records.
   */
  public ContractorTableModel()
  {
  }

  @Override
  public final int getColumnCount()
  {
    return DBSchema.getNumberOfFields();
  }

  @Override
  public final int getRowCount()
  {
    return contractors.size();
  }

  @Override
  public final Object getValueAt(final int row, final int column)
  {
    return contractors.get(row)[column];
  }

  @Override
  public final Class<?> getColumnClass(final int column)
  {
    switch (column)
    {
      // Right-align the entries of certain columns (Numbers are
      // right-aligned by default)
      case DBSchema.SIZE_INDEX: // fallthrough
      case DBSchema.RATE_INDEX: // fallthrough
      case DBSchema.OWNER_INDEX:
        return Number.class;
      default:
        return String.class;
    }
  }

  @Override
  public final void setValueAt(final Object obj, final int row,
                               final int column)
  {
    Object[] rowValues = contractors.get(row);
    rowValues[column] = obj;
  }

  @Override
  public final String getColumnName(final int column)
  {
    return DBSchema.getFieldName(column);
  }

  /**
   * Sets (replaces) the contents of the table model. Requires an explicit
   * call to <code>fireTableDataChanged</code> to actually update the table
   * view.
   *
   * @param contractors a map of (record number, contractor) pairs with the
   *                    contractors being displayed and the record numbers kept for
   *                    internal reference purposes
   */
  public final void setContents(final Map<Long, Contractor> contractors)
  {
    Iterator<Map.Entry<Long, Contractor>> it = contractors.entrySet()
      .iterator();
    recordNumbers.clear();
    this.contractors.clear();
    while (it.hasNext())
    {
      Map.Entry<Long, Contractor> pair = it.next();
      long recNo = pair.getKey();
      Contractor c = pair.getValue();
      String[] data = new String[getColumnCount()];
      data[DBSchema.NAME_INDEX] = c.getName();
      data[DBSchema.LOCATION_INDEX] = c.getLocation();
      data[DBSchema.SPECIALTIES_INDEX] = c.getSpecialties();
      data[DBSchema.SIZE_INDEX] = c.getSize();
      data[DBSchema.RATE_INDEX] = c.getRate();
      data[DBSchema.OWNER_INDEX] = c.getOwner();

      recordNumbers.add(recNo);
      this.contractors.add(data);
    }
  }

  /**
   * Returns the contractor at the row given by its index.
   *
   * @param row a row index
   * @return the contractor at <code>row</code>
   */
  public final Contractor getContractorAt(final int row)
  {
    String[] data = contractors.get(row);
    Contractor c = new Contractor();
    c.setName(data[DBSchema.NAME_INDEX]);
    c.setLocation(data[DBSchema.LOCATION_INDEX]);
    c.setSpecialties(data[DBSchema.SPECIALTIES_INDEX]);
    c.setSize(data[DBSchema.SIZE_INDEX]);
    c.setRate(data[DBSchema.RATE_INDEX]);
    c.setOwner(data[DBSchema.OWNER_INDEX]);
    return c;
  }

  /**
   * Returns the record number at the row given by its index.
   *
   * @param row a row index
   * @return the record number at <code>row</code>
   */
  public final long getRecordNumberAt(final int row)
  {
    return recordNumbers.get(row);
  }

  /**
   * Replaces the contractor at the row given by its index.
   *
   * @param row the index of the row whose contractor is to be replaced
   * @param c   the new contractor with which to replace an existing
   *            contractor
   */
  public final void replaceContractorAt(final int row, final Contractor c)
  {
    String[] data = new String[getColumnCount()];

    data[DBSchema.NAME_INDEX] = c.getName();
    data[DBSchema.LOCATION_INDEX] = c.getLocation();
    data[DBSchema.SPECIALTIES_INDEX] = c.getSpecialties();
    data[DBSchema.SIZE_INDEX] = c.getSize();
    data[DBSchema.RATE_INDEX] = c.getRate();
    data[DBSchema.OWNER_INDEX] = c.getOwner();

    contractors.set(row, data);
  }

  /**
   * Deletes the record (contractor and record number) at the row given by its
   * index.
   *
   * @param row the index of the row for which the record is to be deleted
   */
  public final void deleteRecordAt(final int row)
  {
    contractors.remove(row);
    recordNumbers.remove(row);
  }
}
