/* 
 * @(#)DatabaseFileException.java    1.0 21/06/2010 
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
 * A checked exception with which an abnormal condition in the database file
 * access class {@link DBFileAccess} is signaled.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class DatabaseFileException extends Exception {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * Creates an instance of the database file exception class with which to
     * signal an abnormal condition occurring inside the database file access
     * class {@link DBFileAccess}.
     * 
     * @param message
     *            the string describing the exception
     */
    public DatabaseFileException(final String message) {
	super(message);
    }

    /**
     * Creates an instance of the database file exception class with which to
     * signal an abnormal condition occurring inside the database file access
     * class {@link DBFileAccess}.
     * 
     * @param message
     *            the string describing the exception
     * @param cause
     *            the cause of the exception in a lower layer (exception
     *            chaining)
     */
    public DatabaseFileException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
