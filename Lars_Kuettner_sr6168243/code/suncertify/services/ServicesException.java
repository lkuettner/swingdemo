/* 
 * @(#)ServicesException.java    1.0 21/06/2010 
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

package suncertify.services;

/**
 * A checked exception signaled by the {@link BusinessServices} implementation
 * to indicate a problem with the data access. Mainly used to wrap and relay an
 * (unchecked) {@link suncertify.db.DatabaseException}.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class ServicesException extends Exception {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * Creates an instance of the services exception class with which to signal
     * an abnormal condition occurring inside the
     * {@link suncertify.services.ContractorManager}.
     * 
     * @param message
     *            the string describing the exception
     */
    public ServicesException(final String message) {
	super(message);
    }

    /**
     * Creates an instance of the services exception class with which to signal
     * an abnormal condition occurring inside the
     * {@link suncertify.services.ContractorManager}.
     * 
     * @param message
     *            the string describing the exception
     * @param cause
     *            the cause of the exception in a conceptually lower layer
     *            (exception chaining)
     */
    public ServicesException(final String message, final Throwable cause) {
	super(message, cause);
    }
}
