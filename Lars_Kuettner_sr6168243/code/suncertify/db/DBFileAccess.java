/* 
 * @(#)DBFileAccess.java    1.0 21/06/2010 
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

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sole access point to the database file. All file-level access operations on
 * the database file are bundled in this class. Not thread-safe. The class is
 * designed as a singleton and as such to be accessed exclusively through the
 * thread-safe singleton instance of the data access class {@link Data}.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public final class DBFileAccess {

    /** Logger object to log messages in the scope of this class. */
    private static final Logger LOG = Logger.getLogger(DBFileAccess.class
	    .getName());

     /** The magic cookie identifying this as a valid database file. */
    private static final int MAGIC_COOKIE = 0x0202;

    /** The character encoding of the file. */
    private static final String ENCODING = "US-ASCII";

    /** The value that identifies a record as being valid. */
    private static final short RECORD_VALID = 0;
    /** The value that identifies a record as being deleted. */
    private static final short RECORD_DELETED = (short) 0x8000;

    /** The Singleton instance. */
    private static final DBFileAccess INSTANCE = new DBFileAccess();

    /** The path name of the database file. */
    private String databaseLocation = null;

    /** The random access file handle to the database file. */
    private RandomAccessFile randomAccessFile;

    /**
     * The offset in bytes from the beginning of the file to the first record.
     */
    private int offsetToStart;

    /**
     * The record meta data or schema, as extracted from the database file
     * header.
     */
    private RecordMetaData recordMetaData = RecordMetaData.getInstance();

    /**
     * The total number of records in the database file, regardless of whether
     * they are valid or deleted.
     */
    private long totalNumberOfRecords;

    /**
     * Private constructor enforcing the singleton property.
     */
    private DBFileAccess() {
    }

    /**
     * Factory method to get the Singleton instance of DBFileAcess through which
     * all database file access operations have to be conducted.
     * <p>
     * This method may be called more than once provided the database location
     * is the same every time. The instance is initialized upon the first method
     * invocation.
     * 
     * @param databaseLocation
     *            a string representing the path name to the database file
     * @return the Singleton instance of DBFileAccess
     * @throws DatabaseFileException
     *             if there is a problem with either the
     *             <code>databaseLocation</code> parameter or the database file
     *             the parameter refers to
     */
    public static DBFileAccess getInstance(final String databaseLocation)
	    throws DatabaseFileException {
	LOG.fine("database location is \"" + databaseLocation + "\"");

	// Check validity of databaseLocation and that it is not already
	// assigned to another file name. If pristine and file name valid,
	// return true.
	boolean doInitialize = INSTANCE.shouldBeInitialized(databaseLocation);
	if (doInitialize) {
	    // Open the database location file and read the database schema,
	    // i.e., the metadata (only).
	    INSTANCE.initialize(databaseLocation);
	}
	return INSTANCE;
    }

    /**
     * Determines whether the singleton instance should be initialized. For this
     * to be true, the <code>databaseLocation</code> parameter must not be null
     * and the singleton instance must not already have been initialized.
     * 
     * @param databaseLocation
     *            a string representing the path name to the database file
     * @return a boolean value indicating whether the <code>initialize</code>
     *         method should be called with the given
     *         <code>databaseLocation</code> parameter
     * @throws DatabaseFileException
     *             if the database location is null or if its canonical path
     *             name cannot be retrieved or if the canonical path name is not
     *             equal to the canonical path name of an an already registered
     *             database location (if so)
     */
    private boolean shouldBeInitialized(final String databaseLocation)
	    throws DatabaseFileException {
	// Validity checks.
	// databaseLocation must not be empty.
	if (databaseLocation == null) {
	    // Argument "databaseLocation" must not be empty.
	    throw new DatabaseFileException(Text.DATABASE_LOCATION_IS_NULL);
	}

	if (this.databaseLocation != null) {
	    // Database already opened and successfully processed
	    // Absolute pathnames not equal => exception
	    boolean match = matchDatabaseLocation(databaseLocation);
	    if (match == false) {
		// The current database location does not correspond to the
		// database location given previously.
		throw new DatabaseFileException(
			Text.DATABASE_LOCATION_MISMATCH);
	    }
	    return false; // no further processing necessary
	} else { // databaseLocation not null and database not yet opened.
	    // Try to open databaseLocation and read contents of database.
	    // Make the assignment to this.databaseLocation only on success.
	    return true;
	}
    }

    /**
     * Opens the database file as a random access file and reads and validates
     * the database schema information, also referred to as record metadata. The
     * actual database records are left untouched.
     * <p>
     * Upon successful processing of the database file, a {@link Converter}
     * utility class for conversion between {@link Contractor} objects and
     * string array records is also initialized and can be used thereafter.
     * 
     * @param databaseLocation
     *            a string representing the path name to the database file
     * @throws DatabaseFileException
     *             on any format or access error on the database file
     */
    private void initialize(final String databaseLocation)
	    throws DatabaseFileException {
	// Open databaseLocation as a random access file.
	RandomAccessFile raf = null;
	int ots = -1; // offset to start
	long tnor = -1; // total number of records
	File databaseFile = new File(databaseLocation);
	String canonicalDatabaseLocation = null;

	recordMetaData.clear();

	try {
	    canonicalDatabaseLocation = databaseFile.getCanonicalPath();
	} catch (Exception e) { // canonical path retrieval
	    throw new DatabaseFileException(Text.CANT_RETRIEVE_CANONICAL_PATH
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	}

	try {
	    raf = new RandomAccessFile(databaseFile, "rw");

	    // Read header info.
	    final int magicCookie = raf.readInt();
	    if (magicCookie != MAGIC_COOKIE) {
		// File is not marked as a database file.
		// Actually, a format exception.
		throw new DatabaseFileException(String.format(
			Text.INVALID_MAGIC_COOKIE, magicCookie));
	    }
	    // Read offset to start with the first record.
	    ots = raf.readInt();

	    // Read schema description - also known as record meta data.
	    // Do not read the records. Those will be read from the cache.
	    // Read the record meta data to be found in the header of the
	    // database file.
	    // Read the database file header (stopping short at the first
	    // record).
	    // Verify the header to the specification.
	    final int numberOfFields = raf.readShort();
	    if (numberOfFields != DBSchema.getNumberOfFields()) {
		throw new DatabaseFileException(String.format(
			Text.INVALID_NUMBER_OF_FIELDS, numberOfFields));
	    }
	    // Read schema description section.
	    // For each field of a record:
	    for (int i = 0; i < numberOfFields; ++i) {
		// length of field name
		final int fieldNameLength = raf.readShort();
		if (fieldNameLength <= 0) {
		    throw new DatabaseFileException(String.format(
			    Text.INVALID_FIELD_NAME_LENGTH, fieldNameLength));
		}
		// field name
		final byte[] fieldNameByteArray = new byte[fieldNameLength];
		raf.readFully(fieldNameByteArray);
		final String fieldNameAnyCase = new String(fieldNameByteArray,
			ENCODING);
		String fieldName = DBSchema.getFieldName(fieldNameAnyCase);
		if (fieldName == null) {
		    throw new DatabaseFileException(String.format(
			    Text.INVALID_FIELD_NAME, fieldNameAnyCase));
		}
		// Verify that field name matches the specification.
		int fieldIndex = DBSchema.getFieldIndex(fieldName);
		if (fieldIndex < 0) { // field name not contained
		    throw new DatabaseFileException(String.format(
			    Text.INVALID_FIELD_NAME, fieldName));
		}
		// field length
		final int fieldLength = raf.readShort();
		if (fieldLength != DBSchema.getFieldLength(fieldIndex)) {
		    throw new DatabaseFileException(String.format(
			    Text.INVALID_FIELD_LENGTH, fieldLength, fieldName));
		}

		// Append (fieldName, fieldLength) pair to schema.
		recordMetaData.addField(fieldName, fieldLength);
	    }
	    tnor = (raf.length() - ots) / recordMetaData.getRecordLength();

	} catch (FileNotFoundException e) {
	    // Database location cannot be opened for reading and writing
	    recordMetaData.clear(); // b/o failure atomicity

	    throw new DatabaseFileException(Text.CANT_OPEN_DATABASE_FILE
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	} catch (EOFException e) { // Premature end of file
	    recordMetaData.clear(); // b/o failure atomicity
	    try {
		raf.close();
	    } catch (Exception ex) {
		LOG.log(Level.WARNING, "Can't close database location file "
			+ databaseLocation, ex);
	    }
	    throw new DatabaseFileException(Text.PREMATURE_EOF
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	} catch (IOException e) { // Format error
	    recordMetaData.clear(); // b/o failure atomicity
	    try {
		raf.close();
	    } catch (Exception ex) {
		LOG.log(Level.WARNING, "Can't close database location file "
			+ databaseLocation, ex);
	    }
	    throw new DatabaseFileException(Text.CANT_ACCESS_DATABASE_FILE
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	} catch (DatabaseFileException e) {
	    recordMetaData.clear(); // b/o failure atomicity
	    throw e; // Re-throw
	}
	// Header data successfully read.
	// Set database location et. al. to indicate overall success.
	// This way, the principle of failure atomicity is honored.
	this.databaseLocation = canonicalDatabaseLocation;
	// The raf handle is needed to access the database file later.
	randomAccessFile = raf;
	// The offset in bytes to start with the first record.
	offsetToStart = ots;
	// The total number of records, both valid or deleted.
	totalNumberOfRecords = tnor;
    }

    /**
     * Compares the canonical representation of the database location path name
     * with the (canonical) database location path name property.
     * 
     * @param databaseLocation
     *            A string representing the path name to the database file
     * @return a boolean value that is true if the canonical representation of
     *         the database location parameter matches the database location
     *         property, and false otherwise
     * @throws DatabaseFileException
     *             if the canonical path cannot be retrieved
     */
    public boolean matchDatabaseLocation(final String databaseLocation)
	    throws DatabaseFileException {
	try {
	    File databaseFile = new File(databaseLocation);
	    String canonicalDatabaseLocation = databaseFile.getCanonicalPath();
	    boolean match = this.databaseLocation
		    .equals(canonicalDatabaseLocation);
	    return match;
	} catch (IOException e) { // canonical path retrieval
	    throw new DatabaseFileException(Text.CANT_RETRIEVE_CANONICAL_PATH
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	}
    }

    /**
     * Closes the random access file and marks the singleton instance as
     * uninitialized so that it may be reused. This latter feature is
     * particularly useful for testing purposes. The method will usually be
     * called from the encapsulating {@link Data} class singleton instance.
     */
    public void terminate() {
	if (databaseLocation != null) {
	    try {
		randomAccessFile.close();
	    } catch (IOException e) {
		LOG.log(Level.WARNING, "Can't close database location file: "
			+ databaseLocation, e);
	    }
	    databaseLocation = null; // This is the one that's important.
	    randomAccessFile = null;
	    recordMetaData.clear();
	}
    }

    /**
     * An iterator through the record numbers of valid records only. Invalid
     * records are quietly skipped in the course of iteration.
     * 
     * @author Lars Kuettner
     * @version 1.0
     */
    private final class ValidRecordNumberIterator implements Iterator<Long> {
	/**
	 * The next valid record number. Initialized with minus one so that zero
	 * is the first increment.
	 */
	private long nextValidRecordNumber = -1;

	/**
	 * Creates an iterator instance.
	 */
	private ValidRecordNumberIterator() {
	    // Find the first valid record number. Search begins at index zero.
	    incrNextValidRecordNumber();
	}

	/**
	 * Increments <code>nextValidRecordNumber</code> until it refers to the
	 * next valid record number. If there is no more valid record, sets
	 * <code>nextValidRecordNumber</code> to minus one.
	 */
	private void incrNextValidRecordNumber() {
	    while (++nextValidRecordNumber < totalNumberOfRecords) {
		// Check if nextValidRecordNumber is really valid.
		long pos = offsetToStart + nextValidRecordNumber
			* recordMetaData.getRecordLength();
		short valid;
		try {
		    randomAccessFile.seek(pos);
		    valid = randomAccessFile.readShort();
		    if (valid == RECORD_VALID) {
			return;
		    }
		} catch (IOException e) {
		    // Convert a legitimate checked exception into a runtime
		    // exception as the iterator interface is non-modifiable.
		    throw new IllegalStateException(
			    Text.CANT_ACCESS_DATABASE_FILE
				    + Text.NESTED_EXCEPTION_IS + e.getMessage(),
			    e);
		}
	    }
	    nextValidRecordNumber = -1;
	}

	@Override
	public boolean hasNext() {
	    return nextValidRecordNumber >= 0;
	}

	@Override
	public Long next() {
	    if (nextValidRecordNumber < 0) {
		throw new NoSuchElementException(
			Text.NO_MORE_VALID_RECORD_NUMBERS);
	    }
	    long nvrn = nextValidRecordNumber;
	    incrNextValidRecordNumber();
	    return nvrn;
	}

	@Override
	public void remove() {
	    throw new UnsupportedOperationException(
		    Text.REMOVE_OPERATION_NOT_SUPPORTED);
	}
    }

    /**
     * Provides an iterator over valid record numbers in the succession of their
     * appearance in the database file. Only the records marked valid are
     * considered. The records marked invalid are quietly skipped.
     * 
     * @return an iterator instance conforming to the {@link Iterator} interface
     */
    public Iterator<Long> iterator() {
	return new ValidRecordNumberIterator();
    }

    /**
     * Reads a single valid record from the database file and returns its
     * fields.
     * <p>
     * Note that the record must be marked valid in order to be readable. An
     * attempt to read an invalid (deleted) record would trigger a
     * <code>RecordNotFoundException</code>.
     * 
     * @param recNo
     *            the long value representing the record number that uniquely
     *            identifies the record to be read
     * @return a string array representing the requested record
     * @throws RecordNotFoundException
     *             if there is no valid record corresponding to
     *             <code>recNo</code>
     * @throws DatabaseFileException
     *             if the database file can't be accessed as needed
     */
    public String[] readRecord(final long recNo) throws RecordNotFoundException,
	    DatabaseFileException {
	if (recNo < 0 || recNo >= totalNumberOfRecords) {
	    throw new RecordNotFoundException(String.format(
		    Text.RECORD_NUMBER_OUT_OF_RANGE, recNo,
		    totalNumberOfRecords));
	}
	long pos = offsetToStart + recNo * recordMetaData.getRecordLength();
	short valid;
	try {
	    randomAccessFile.seek(pos);
	    valid = randomAccessFile.readShort();
	} catch (IOException e) { // Can't seek or read a short value.
	    throw new DatabaseFileException(Text.CANT_ACCESS_DATABASE_FILE
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	}
	if (valid != RECORD_VALID) {
	    throw new RecordNotFoundException(String.format(
		    Text.ATTEMPTED_TO_READ_INVALID_RECORD, recNo));
	}
	// File pointer already at the right position.
	String[] data = new String[DBSchema.getNumberOfFields()];
	for (int i = 0; i < DBSchema.getNumberOfFields(); ++i) {
	    byte[] bytes = new byte[recordMetaData.getFieldLength(i)];
	    try {
		randomAccessFile.readFully(bytes);
		// Convert bytes to String to be processed.
		String s = new String(bytes, ENCODING);
		// Eliminate all trailing zeroes (if any). More precisely,
		// everything from the first zero to the end is cut (or nothing
		// at all, if there is no zero to be found).
		int ix = s.indexOf(0);
		if (ix >= 0) {
		    s = s.substring(0, ix); // Might even become empty string.
		}
		// Trim the remaining string whose zeroes have been cut already.
		// I.e., eliminate leading and trailing spaces.
		data[i] = s.trim();
	    } catch (IOException e) {
		// Format error (valid record has been found!).
		throw new DatabaseFileException(Text.CANT_ACCESS_DATABASE_FILE
			+ Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	    }
	}
	return data;
    }

    /**
     * Places a new record at a predetermined empty slot in the database file.
     * There are no restrictions about the contents of such a record, even if
     * this leads to records with identical contents in the database.
     * <p>
     * Knowledge about the re-assignability (emptiness) of a record slot must be
     * managed from the outside. The record number <code>recNo</code> determines
     * the slot. It is assumed that record numbers are assigned in ascending
     * order, beginning with zero at the first record in the database file.
     * Slots of records previously marked deleted may thus be recycled.
     * 
     * @param recNo
     *            the long value representing the record number that uniquely
     *            identifies the record slot to be populated
     * @param data
     *            A string array representing the fields of the new record to be
     *            written to the database file. The maximum length of a field is
     *            taken from the record metadata (schema),
     *            {@link RecordMetaData}. Any field that exceeds its maximum
     *            length is quietly truncated, with just a warning log message
     *            printed.
     * @throws RecordNotFoundException
     *             if <code>recNo</code> is out of range, that is, less than
     *             zero or larger than the total number of records (valid and
     *             deleted together), or if <code>recNo</code> refers to a
     *             record that is not marked deleted
     * @throws DatabaseFileException
     *             if the database file can't be accessed as needed
     */
    public void placeNewRecord(final long recNo, final String[] data)
	    throws RecordNotFoundException, DatabaseFileException {
	if (recNo < 0 || recNo > totalNumberOfRecords) { // >, not >=
	    throw new RecordNotFoundException(String.format(
		    Text.RECORD_NUMBER_OUT_OF_RANGE, recNo,
		    totalNumberOfRecords));
	}
	long pos = offsetToStart + recNo * recordMetaData.getRecordLength();
	try {
	    // Place file pointer to the beginning of the record slot.
	    randomAccessFile.seek(pos);
	    // Verify that record is either marked deleted or record is appended
	    // to the end of the file
	    if (pos != randomAccessFile.length()) {
		short valid = randomAccessFile.readShort();
		if (valid != RECORD_DELETED) {
		    // Illegal state: record expected to be deleted!
		    throw new RecordNotFoundException(String.format(
			    Text.UNEXPECTED_VALID_FLAG_VALUE, valid,
			    RECORD_DELETED));
		}
		// Rewind to original seek position.
		randomAccessFile.seek(pos);
	    }
	    randomAccessFile.writeShort(RECORD_VALID);
	    for (int i = 0; i < DBSchema.getNumberOfFields(); ++i) {
		// Copy data bytes into a byte array with the expected length.
		// Arrays.copyOf(): Copies the specified array, truncating or
		// padding with zeros (if necessary) so the copy has the
		// specified length.
		// Transform null pointer to empty string.
		if (data[i] == null) {
		    data[i] = "";
		}
		byte[] bytes = Arrays.copyOf(data[i].getBytes(ENCODING),
			recordMetaData.getFieldLength(i));
		randomAccessFile.write(bytes);
		if (data[i].length() > bytes.length) {
		    LOG.warning("\"" + data[i] + "\" truncated to \""
			    + new String(bytes, ENCODING) + "\"");
		    // Update data[i] with the truncated string. This is for the
		    // cache. Otherwise (no truncation performed), data[i]
		    // remains "as is".
		    data[i] = new String(bytes);
		}
	    }
	    // If new record has been appended to the end of the database file,
	    // i.e., not recycling an existing record slot (marked deleted),
	    // then increment the total # of records member variable.
	    if (recNo == totalNumberOfRecords) {
		++totalNumberOfRecords;
	    }
	} catch (IOException e) {
	    throw new DatabaseFileException(Text.CANT_ACCESS_DATABASE_FILE
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	}
    }

    /**
     * Replaces the contents of a valid record in the database file. A valid
     * record may be updated without restrictions, even if this leads to several
     * records with identical contents.
     * 
     * @param recNo
     *            the long value representing the record number that uniquely
     *            identifies the record to be updated
     * @param data
     *            A string array representing the fields of the new record to be
     *            written to the database file. The maximum length of a field is
     *            taken from the record metadata (schema),
     *            {@link RecordMetaData}. Any field that exceeds its maximum
     *            length is quietly truncated, with just a warning log message
     *            printed.
     * @throws RecordNotFoundException
     *             if there is no valid record corresponding to
     *             <code>recNo</code>
     * @throws DatabaseFileException
     *             if the database file can't be accessed as needed
     */
    public void updateRecord(final long recNo, final String[] data)
	    throws RecordNotFoundException, DatabaseFileException {
	if (recNo < 0 || recNo >= totalNumberOfRecords) { // >=, not >
	    throw new RecordNotFoundException(String.format(
		    Text.RECORD_NUMBER_OUT_OF_RANGE, recNo,
		    totalNumberOfRecords));
	}
	long pos = offsetToStart + recNo * recordMetaData.getRecordLength();
	try {
	    // Place file pointer to the beginning of the record slot.
	    randomAccessFile.seek(pos);
	    // Verify that record is marked valid as a deleted record cannot be
	    // updated.
	    short valid = randomAccessFile.readShort();
	    if (valid != RECORD_VALID) {
		// Illegal state: record expected to be valid!
		throw new RecordNotFoundException(String.format(
			Text.UNEXPECTED_VALID_FLAG_VALUE, valid, RECORD_VALID));
	    }
	    // Overwrite record data just like when creating a new record.
	    for (int i = 0; i < DBSchema.getNumberOfFields(); ++i) {
		// Copy data bytes into a byte array with the expected length.
		// Arrays.copyOf(): Copies the specified array, truncating or
		// padding with zeros (if necessary) so the copy has the
		// specified length.
		// Transform null pointer to empty string.
		if (data[i] == null) {
		    data[i] = "";
		}
		byte[] bytes = Arrays.copyOf(data[i].getBytes(ENCODING),
			recordMetaData.getFieldLength(i));
		randomAccessFile.write(bytes);
		if (data[i].length() > bytes.length) {
		    LOG.warning("\"" + data[i] + "\" truncated to \""
			    + new String(bytes, ENCODING) + "\"");
		    // Update data[i] with the truncated string. This is for the
		    // cache. Otherwise (no truncation performed), data[i]
		    // remains "as is".
		    data[i] = new String(bytes);
		}
	    }
	} catch (IOException e) {
	    throw new DatabaseFileException(Text.CANT_ACCESS_DATABASE_FILE
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	}
    }

    /**
     * Deletes a valid record specified by its record number.
     * <p>
     * The deletion will be conducted so that the space the record occupied,
     * together with its record number, may be subsequently reassigned to a new
     * record.
     * 
     * @param recNo
     *            the long value representing the record number of the valid
     *            record to be deleted
     * @throws RecordNotFoundException
     *             if there is no valid record corresponding to
     *             <code>recNo</code>
     * @throws DatabaseFileException
     *             if the database file can't be accessed as needed
     */
    public void deleteRecord(final long recNo) throws RecordNotFoundException,
	    DatabaseFileException {
	if (recNo < 0 || recNo >= totalNumberOfRecords) { // >=, not >
	    throw new RecordNotFoundException(String.format(
		    Text.RECORD_NUMBER_OUT_OF_RANGE, recNo,
		    totalNumberOfRecords));
	}
	long pos = offsetToStart + recNo * recordMetaData.getRecordLength();
	try {
	    // Place file pointer to the beginning of the record slot.
	    randomAccessFile.seek(pos);
	    // Verify that record is marked valid as a deleted record cannot
	    // (should not) be deleted again.
	    short valid = randomAccessFile.readShort();
	    if (valid != RECORD_VALID) {
		// Illegal state: record expected to be valid!
		throw new RecordNotFoundException(String.format(
			Text.UNEXPECTED_VALID_FLAG_VALUE, valid, RECORD_VALID));
	    }
	    // Rewind to original seek position.
	    randomAccessFile.seek(pos);
	    randomAccessFile.writeShort(RECORD_DELETED);
	} catch (IOException e) {
	    throw new DatabaseFileException(Text.CANT_ACCESS_DATABASE_FILE
		    + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
	}
    }
}
