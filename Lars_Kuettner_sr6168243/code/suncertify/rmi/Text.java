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

package suncertify.rmi;

/**
 * Utility class wrapping text constants for the <code>suncertify.rmi</code>
 * package.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public final class Text {
    /** Private constructor since this is a utility class. */
    private Text() {
    }

    /** ContractorManager. */
    static final String RMI_SERVICE_NAME = "ContractorManager";
    /** ; nested exception is:\n. */
    static final String NESTED_EXCEPTION_IS = "; nested exception is:\n";
    /** Can't connect to %s. */
    static final String CANT_CONNECT_TO_URL = "Can't connect to %s";
    /** \"%s\" service name not registered. */
    static final String SERVICE_NAME_NOT_REGISTERED =
	"\"%s\" service name not registered";
    /** Invalid port number: %s. */
    static final String INVALID_PORT_NUMBER = "Invalid port number: %s";
    /** Access denied to rebind ContractorManager. */
    static final String ACCESS_DENIED_TO_REBIND_SERVICE =
	"Access denied to rebind " + RMI_SERVICE_NAME;
    /** Can't create or rebind the RMI registry. */
    static final String CANT_CREATE_OR_REBIND_REGISTRY =
	"Can't create or rebind the RMI registry";
    /** Can't create contractor manager. */
    static final String CANT_CREATE_CONTRACTOR_MANAGER =
	"Can't create contractor manager";
}
