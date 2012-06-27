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

package suncertify.db;

/**
 * Utility class wrapping text constants for the <code>suncertify.db</code>
 * package.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public final class Text {
    /** Private constructor since this is a utility class. */
    private Text() {
    }

    /** ; nested exception is:\n. */
    static final String NESTED_EXCEPTION_IS = "; nested exception is:\n";
    /** Database location mismatch. */
    static final String DATABASE_LOCATION_MISMATCH =
	"Database location mismatch";
    /** Can't get the Singleton instance of the database file access class. */ 
    static final String CANT_GET_FILE_ACCESS_INSTANCE =
	"Can't get the Singleton instance of the database file access class";
    /** Can't initialize database cache. */
    static final String CANT_INITIALIZE_DATABASE_CACHE =
	"Can't initialize database cache";
    /** Database closed. */
    static final String DATABASE_CLOSED = "Database closed";
    /** Record #%d not found. */
    static final String RECORD_NOT_FOUND = "Record #%d not found";
    /** Can't place new record into the database file. */
    static final String CANT_PLACE_NEW_RECORD =
	"Can't place new record into the database file";
    /** Can't update record in the database file. */
    static final String CANT_UPDATE_RECORD =
	"Can't update record in the database file";
    /** Can't delete record from the database file. */
    static final String CANT_DELETE_RECORD =
	"Can't delete record from the database file";
    /** Record #%d has not been locked. */
    static final String RECORD_NOT_LOCKED =
	"Record #%d has not been locked";
    /** Trying to access record #%d with wrong cookie %d. */
    static final String RECORD_COOKIE_MISMATCH =
	"Trying to access record #%d with wrong cookie %d";
    /** Database location is null. */
    static final String DATABASE_LOCATION_IS_NULL =
	"Database location is null";
    /** Can't retrieve canonical path. */
    static final String CANT_RETRIEVE_CANONICAL_PATH =
	"Can't retrieve canonical path";
    /** Invalid magic cookie: %d. */
    static final String INVALID_MAGIC_COOKIE =
	"Invalid magic cookie: %d";
    /** Invalid number of fields: %d. */
    static final String INVALID_NUMBER_OF_FIELDS =
	"Invalid number of fields: %d";
    /** Invalid field name length: %d. */
    static final String INVALID_FIELD_NAME_LENGTH =
	"Invalid field name length: %d";
    /** Invalid field name: %s. */
    static final String INVALID_FIELD_NAME = "Invalid field name: %s";
    /** Invalid field length %d for field %s. */
    static final String INVALID_FIELD_LENGTH =
	"Invalid field length %d for field %s";
    /** Can't open database file as a random access file. */
    static final String CANT_OPEN_DATABASE_FILE =
	"Can't open database file as a random access file";
    /** Premature end of file. */
    static final String PREMATURE_EOF = "Premature end of file";
    /** Can't access database file (seek, read, or write). */
    static final String CANT_ACCESS_DATABASE_FILE = 
	"Can't access database file (seek, read, or write)";
    /** No more valid record numbers. */
    static final String NO_MORE_VALID_RECORD_NUMBERS =
	"No more valid record numbers";
    /** The remove operation is not supported by this Iterator. */
    static final String REMOVE_OPERATION_NOT_SUPPORTED =
	"The remove operation is not supported by this Iterator";
    /** Record number %d out of range [0, %d). */
    static final String RECORD_NUMBER_OUT_OF_RANGE =
	"Record number %d out of range [0, %d)";
    /** Attempted to read invalid record: %d. */
    static final String ATTEMPTED_TO_READ_INVALID_RECORD =
	"Attempted to read invalid record: %d";
    /** Unexpected value of valid flag: %d != %d. */
    static final String UNEXPECTED_VALID_FLAG_VALUE =
	"Unexpected value of valid flag: %d != %d";
}
