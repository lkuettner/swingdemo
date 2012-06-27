/* 
 * @(#)DuplicateKeyException.java    1.0 21/06/2010 
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
 * A checked exception which is thrown when an attempt is made to insert a
 * record into the database whose primary key is identical to the primary key of
 * an existing record.
 * <p>
 * As the record number as the primary key is automatically assigned, there is
 * virtually no way this exception can ever be triggered, so it won't.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class DuplicateKeyException extends Exception {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * Creates the default instance of the duplicate key exception class.
     */
    public DuplicateKeyException() {
    }

    /**
     * Creates an instance of the duplicate key exception class.
     * 
     * @param message
     *            the string describing the exception
     */
    public DuplicateKeyException(final String message) {
	super(message);
    }
}
