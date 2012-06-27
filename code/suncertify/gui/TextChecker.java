/* 
 * @(#)TextChecker    1.0 21/06/2010 
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
 * Interface that has to be implemented when using the {@link CheckedTextField}
 * to specify the constraints for the checked input text field.
 *
 * @author Lars Kuettner
 * @version 1.0
 * @see CheckedTextField#CheckedTextField(int, TextChecker)
 * @see AbstractTextChecker
 */
public interface TextChecker
{
  /**
   * Validity check for a single input character. A character that does not
   * pass this check is not accepted as a valid input character of the
   * corresponding {@link CheckedTextField}.
   *
   * @param c    he character to be checked
   * @param offs the offset or position of the character among the input field
   *             text
   * @return <code>true</code> if <code>c</code> is a valid input character to
   *         be accepted, <code>false</code> if <code>c</code> is to be
   *         rejected as invalid (and thus not to be displayed)
   */
  boolean isValidInputCharacter(char c, int offs);

  /**
   * Validity check for the overall field text. Whenever the validity state
   * changes, either from invalid to valid or vice versa, a validity change
   * event is triggered that can be handled by registering a validity change
   * listener for a checked text field. Called on each new character input or
   * delete operation.
   *
   * @param fieldText the text to be checked
   * @return <code>true</code> if <code>fieldText</code> is considered valid,
   *         <code>false</code> otherwise (if <code>fieldText</code> is
   *         considered invalid)
   * @see ValidityChangeListener
   */
  boolean isValidFieldText(String fieldText);
}
