/*
 * @(#)RecordNotFoundException.java    1.0 21/06/2010 
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
 * not be used out of this context and must be used exclusively by Sun 
 * Microsystems.
 */

package suncertify.db;

/**
 * A checked exception that is thrown when a record as given by its record
 * number is not found as a valid record in the database.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class RecordNotFoundException extends Exception {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * Creates the default instance of the record not found exception class with
     * which to signal that a record has not been found as a valid record in the
     * database.
     */
    public RecordNotFoundException() {
    }

    /**
     * Creates an instance of the record not found exception class with which to
     * signal that a record has not been found as a valid record in the
     * database.
     * 
     * @param message
     *            the string describing the exception
     */
    public RecordNotFoundException(final String message) {
	super(message);
    }
}
