/* 
 * @(#)ValidityChangeListener    1.0 21/06/2010 
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

import java.util.EventListener;

/**
 * A listener responding to a change of validity of the text in a
 * {@link CheckedTextField}. With the validity changing while text is being
 * entered into the checked text field, other controls may be activated or
 * deactivated automatically in an event-driven manner.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public interface ValidityChangeListener extends EventListener {
    /**
     * Fired at the moment when the validity of the text in a
     * {@link CheckedTextField} changes from invalid to valid according to the
     * employed {@link TextChecker} implementation.
     * 
     * @param e
     *            the validity change event being fired
     */
    void validityGained(ValidityChangeEvent e);

    /**
     * Fired at the moment when the validity of the text in a
     * {@link CheckedTextField} changes from valid to invalid according to the
     * employed {@link TextChecker} implementation.
     * 
     * @param e
     *            the validity change event being fired
     */
    void validityLost(ValidityChangeEvent e);
}
