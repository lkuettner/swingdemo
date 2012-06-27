/* 
 * @(#)CheckedTextField    1.0 21/06/2010 
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

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension of JTextField that restricts the text being entered in a certain,
 * configurable way.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public class CheckedTextField extends JTextField
{
  /**
   * A magic version number for this class so that serialization can occur
   * without worrying about the underlying class changing between
   * serialization and deserialization.
   */
  private static final long serialVersionUID = 4711L;

  /**
   * The internal text checker object.
   */
  private TextChecker textChecker;

  /**
   * The list of validity change listeners to notify on a validity change.
   */
  private final List<ValidityChangeListener> validityChangeListeners =
    new ArrayList<ValidityChangeListener>();

  /**
   * Creates a new empty checked text field with the specified number of
   * columns (i.e., size) and a text checker object whose methods are called
   * on each input.
   *
   * @param nColumns    the number of columns (the width) of the checked text field
   * @param textChecker a <code>TextChecker</code> specifying the constraints the
   *                    input text in the checked text field is subjected to
   */
  public CheckedTextField(final int nColumns, final TextChecker textChecker)
  {
    super(nColumns);
    this.textChecker = textChecker;
  }

  /**
   * Adds a new validity change listener to the list of validity change
   * listeners.
   *
   * @param validityChangeListener the validity change listener to add
   */
  public final void addValidityChangeListener(
    final ValidityChangeListener validityChangeListener)
  {
    if (validityChangeListener != null)
    {
      validityChangeListeners.add(validityChangeListener);
    }
  }

  /**
   * Creates the default implementation of the model to be used at
   * construction. An instance of NumericDocument is returned.
   *
   * @return NumericDocument a document which only allows positive integers
   */
  protected final Document createDefaultModel()
  {
    return new CheckedTextDocument();
  }

  /**
   * A document that only allows characters to be entered that are approved by
   * textChecker.isValidInputCharacter.
   * <p/>
   *
   * @see javax.swing.text.PlainDocument for further implementation issues and
   *      issues with Serialization.
   */
  private class CheckedTextDocument extends PlainDocument
  {
    /**
     * A version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * Inserts some content into the document. Inserting content causes a
     * write lock to be held while the actual changes are taking place,
     * followed by notification to the observers on the thread that grabbed
     * the write lock.
     *
     * @param offs    the starting offset >= 0
     * @param str     the string to insert; does nothing with null/empty strings
     * @param attrSet the attributes for the inserted content
     * @throws BadLocationException the given insert position is not a valid position within
     *                              the document
     */
    public void insertString(final int offs, final String str,
                             final AttributeSet attrSet) throws BadLocationException
    {
      if (str == null)
      {
        return;
      }
      char[] input = str.toCharArray();
      char[] filteredInput = new char[input.length];
      int filteredInputLength = 0;
      for (int i = 0; i < input.length; i++)
      {
        if (textChecker.isValidInputCharacter(input[i], offs + i))
        {
          filteredInput[filteredInputLength++] = input[i];
        }
      }
      if (filteredInputLength > 0)
      {
        String filteredStr = new String(filteredInput, 0,
          filteredInputLength);
        super.insertString(offs, filteredStr, attrSet);
      }
    }

    /**
     * Monitors an insert operation into the text field.
     *
     * @param ev   the change event describing the dit
     * @param attr the set of attributes for the inserted text
     */
    @Override
    protected void insertUpdate(
      final AbstractDocument.DefaultDocumentEvent ev,
      final AttributeSet attr)
    {
      // Propagate to super class. Doesn't matter when this happens.
      super.insertUpdate(ev, attr);

      // Field text after the insert operation.
      String currFieldText = CheckedTextField.this.getText();
      // Compute field text before the insert operation.
      String prevFieldText = "";
      if (ev.getOffset() > 0)
      {
        prevFieldText += currFieldText.substring(0, ev.getOffset());
      }
      int indexAfterInsertion = ev.getOffset() + ev.getLength();
      if (indexAfterInsertion < currFieldText.length())
      {
        prevFieldText += currFieldText.substring(indexAfterInsertion);
      }
      // Fire event if validity checks yield different results.
      boolean prevValid = textChecker.isValidFieldText(prevFieldText);
      final boolean currValid = textChecker
        .isValidFieldText(currFieldText);
      if (currValid != prevValid)
      {
        // Fire a validity change event.
        for (final ValidityChangeListener vcl
          : validityChangeListeners)
        {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              if (currValid)
              {
                vcl.validityGained(new ValidityChangeEvent(
                  CheckedTextField.this));
              }
              else
              {
                vcl.validityLost(new ValidityChangeEvent(
                  CheckedTextField.this));
              }
            }
          });
        }
      }
    }

    /**
     * Monitors a remove operation from the text field.
     *
     * @param ev the change event describing the edit
     */
    @Override
    protected void removeUpdate(
      final AbstractDocument.DefaultDocumentEvent ev)
    {
      super.removeUpdate(ev);

      // Field text before the remove operation.
      String prevFieldText = CheckedTextField.this.getText();
      // Compute field text after the remove operation.
      String currFieldText = "";
      if (ev.getOffset() > 0)
      {
        currFieldText += prevFieldText.substring(0, ev.getOffset());
      }
      int indexAfterRemoval = ev.getOffset() + ev.getLength();
      if (indexAfterRemoval < prevFieldText.length())
      {
        currFieldText += prevFieldText.substring(indexAfterRemoval);
      }
      // Fire event if validity checks yield different results.
      boolean prevValid = textChecker.isValidFieldText(prevFieldText);
      final boolean currValid = textChecker
        .isValidFieldText(currFieldText);
      if (currValid != prevValid)
      {
        // Fire a validity change event.
        for (final ValidityChangeListener vcl
          : validityChangeListeners)
        {
          SwingUtilities.invokeLater(new Runnable()
          {
            public void run()
            {
              if (currValid)
              {
                vcl.validityGained(new ValidityChangeEvent(
                  CheckedTextField.this));
              }
              else
              {
                vcl.validityLost(new ValidityChangeEvent(
                  CheckedTextField.this));
              }
            }
          });
        }
      }
    }
  }
}
