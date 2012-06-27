/* 
 * @(#)ValidityChangeEvent    1.0 21/06/2010 
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

import java.util.EventObject;

/**
 * Event that is fired whenever the validity state of a {@link CheckedTextField}
 * with a registered {@link ValidityChangeListener} changes, either from valid
 * to invalid or vice versa.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class ValidityChangeEvent extends EventObject {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * Creates a new validity change event.
     * 
     * @param source
     *            the object whose state change is responsible for triggering
     *            this event. As of this version, only {@link CheckedTextField}
     *            supports this event.
     */
    public ValidityChangeEvent(final Object source) {
	super(source);
    }
}
