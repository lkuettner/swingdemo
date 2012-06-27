/* 
 * @(#)DatabaseException.java    1.0 21/06/2010 
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
 * A runtime exception with which an abnormal condition in the data access class
 * {@link Data} is signaled.
 * <p>
 * As the provided database interface is fixed, the methods in the data access
 * class cannot throw this exception as a checked exception, hence it has become
 * a runtime exception.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class DatabaseException extends RuntimeException {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * Creates an instance of the database exception class with which to signal
     * an abnormal condition occurring inside the data access class
     * {@link Data}.
     * 
     * @param message
     *            the string describing the exception
     */
    public DatabaseException(final String message) {
	super(message);
    }

    /**
     * Creates an instance of the database exception class with which to signal
     * an abnormal condition occurring inside the data access class
     * {@link Data}.
     * 
     * @param message
     *            the string describing the exception
     * @param cause
     *            the cause of the exception in a lower layer (exception
     *            chaining)
     */
    public DatabaseException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
