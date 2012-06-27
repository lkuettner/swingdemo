/* 
 * @(#)AbstractTextChecker    1.0 21/06/2010 
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

/**
 * This abstract text checker class provides a default implementation for the
 * <code>TextChecker</code> interface.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public abstract class AbstractTextChecker implements TextChecker {
    @Override
    public boolean isValidInputCharacter(final char c, final int offs) {
	return true;
    }

    @Override
    public boolean isValidFieldText(final String fieldText) {
	return !fieldText.isEmpty();
    }
}
