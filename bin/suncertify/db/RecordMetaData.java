/* 
 * @(#)RecordMetaData.java    1.0 21/06/2010 
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

package suncertify.db;

import suncertify.services.Contractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Enables to collect and verify and provides the record meta data (also
 * referred to as schema description) for a database record, as specified in the
 * header section of the database file.
 * <p/>
 * Although the written specification is quite explicit about field names,
 * lengths, and descriptive field names, it still leaves the order of the fields
 * in a database record unspecified. A <code>RecordMetaData</code> object is
 * filled when reading the database file header while initializing the
 * {@link DBFileAccess} Singleton instance.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public final class RecordMetaData
{
  /**
   * The Singleton instance.
   */
  private static final RecordMetaData INSTANCE = new RecordMetaData();

  /**
   * The length of a record in bytes. Initialized with the length of the valid
   * flag.
   */
  private int recordLength = Short.SIZE / Byte.SIZE;

  /**
   * Mapping from field names to record indexes. As the <em>order</em> of the
   * fields is not specified in the assignment document, any order will be
   * tolerated. Rather than assuming a fixed order, the order is taken from
   * the schema definition in the header section of a database file and may
   * potentially vary from file to file.
   * <p/>
   * The mapping from field names to record indexes is essential in converting
   * forth and back between a property-based {@link Contractor} object and a
   * string array-based record.
   */
  private Map<String, Integer> fieldNamesToIndexes =
    new HashMap<String, Integer>();

  /**
   * Maximum lengths of the record fields in the order of the fields'
   * appearance as specified in the database file header section.
   */
  private List<Integer> fieldLengths = new ArrayList<Integer>();

  /**
   * Private constructor enforcing the usage as a Singleton.
   */
  private RecordMetaData()
  {
  }

  /**
   * Returns the Singleton instance of the <code>RecordMetaData</code> object.
   *
   * @return the Singleton instance of the <code>RecordMetaData</code> object
   */
  public static RecordMetaData getInstance()
  {
    return INSTANCE;
  }

  /**
   * Clears the Singleton instance to reuse it in a subsequent program run.
   * Especially useful in a testing environment.
   */
  public void clear()
  {
    // Re-init record length to the length of the "valid" flag in bytes
    recordLength = Short.SIZE / Byte.SIZE;
    fieldNamesToIndexes.clear();
    fieldLengths.clear();
  }

  /**
   * Adds the record field metadata <code>fieldName</code> and
   * <code>fieldLength</code> as read from the database file header, and
   * updates the record length on the way.
   *
   * @param fieldName   a string representing the name of the field
   * @param fieldLength the field length in bytes
   */
  public void addField(final String fieldName, final int fieldLength)
  {
    // Index of this field name is the size of the map before adding this.
    fieldNamesToIndexes.put(fieldName, fieldNamesToIndexes.size());
    // Append field length. The index corresponds to the position in the
    // record.
    fieldLengths.add(fieldLength);
    // Update record length.
    recordLength += fieldLength;
  }

  /**
   * Returns the record length which is updated when adding the fields.
   *
   * @return the length of the record in bytes
   */
  public int getRecordLength()
  {
    return recordLength;
  }

  /**
   * Returns an index given a field name.
   *
   * @param fieldName the field name string
   * @return the index corresponding to the field name
   */
  public int getFieldNameIndex(final String fieldName)
  {
    return fieldNamesToIndexes.get(fieldName);
  }

  /**
   * Returns the maximum length of a record field given its index.
   *
   * @param fieldIndex the index of the record field
   * @return the maximum length of the field in bytes
   */
  public int getFieldLength(final int fieldIndex)
  {
    return fieldLengths.get(fieldIndex);
  }

  /**
   * Converts a string array-based database record to a property-based
   * {@link Contractor} object.
   *
   * @param record the string array representing the database record
   * @return the corresponding <code>Contractor</code> object
   * @throws IllegalStateException if the <code>RecordMetaData</code> instance has not been
   *                               fully initialized or if the length of the record does not
   *                               correspond to the expected number of fields of a database
   *                               record
   */
  public static Contractor recordToContractor(final String[] record)
  {
    if (INSTANCE.fieldNamesToIndexes.size()
      != DBSchema.getNumberOfFields())
    {
      throw new IllegalStateException("INSTANCE not initialized");
    }
    if (record.length != DBSchema.getNumberOfFields())
    {
      throw new IllegalStateException("Illegal record length: "
        + record.length);
    }
    Contractor contractor = new Contractor();
    contractor.setName(record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.NAME_INDEX))]);
    contractor.setLocation(record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.LOCATION_INDEX))]);
    contractor.setSpecialties(record[INSTANCE.fieldNamesToIndexes
      .get(DBSchema.getFieldName(DBSchema.SPECIALTIES_INDEX))]);
    contractor.setSize(record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.SIZE_INDEX))]);
    contractor.setRate(record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.RATE_INDEX))]);
    contractor.setOwner(record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.OWNER_INDEX))]);
    return contractor;
  }

  /**
   * Converts a {@link Contractor} object to a string array-based record. The
   * order in which the individual properties are organized in the string
   * array is determined by the schema of the database file.
   *
   * @param contractor the <code>Contractor</code> object
   * @return the string array representing the corresponding database record
   * @throws IllegalStateException if the <code>RecordMetaData</code> instance has not been
   *                               fully initialized
   */
  // Convert to string array. The order is determined by the schema of the
  // database file.
  public static String[] contractorToRecord(final Contractor contractor)
  {
    if (INSTANCE.fieldNamesToIndexes.size()
      != DBSchema.getNumberOfFields())
    {
      throw new IllegalStateException("INSTANCE not initialized");
    }
    String[] record = new String[DBSchema.getNumberOfFields()];
    record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.NAME_INDEX))] = contractor.getName();
    record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.LOCATION_INDEX))] = contractor
      .getLocation();
    record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.SPECIALTIES_INDEX))] = contractor
      .getSpecialties();
    record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.SIZE_INDEX))] = contractor.getSize();
    record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.RATE_INDEX))] = contractor.getRate();
    record[INSTANCE.fieldNamesToIndexes.get(DBSchema
      .getFieldName(DBSchema.OWNER_INDEX))] = contractor.getOwner();
    return record;
  }
}
