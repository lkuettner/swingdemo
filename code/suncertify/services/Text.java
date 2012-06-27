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

package suncertify.services;

/**
 * Utility class wrapping text constants for the
 * <code>suncertify.services</code> package.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public final class Text
{
  /**
   * Private constructor since this is a utility class.
   */
  private Text()
  {
  }

  /**
   * ; nested exception is:\n.
   */
  static final String NESTED_EXCEPTION_IS = "; nested exception is:\n";
  /**
   * Can't get the Singleton instance of the data access class.
   */
  static final String CANT_GET_DATA_INSTANCE =
    "Can't get the Singleton instance of the data access class";
  /**
   * Can't perform the find by criteria exact matches operation.
   */
  static final String CANT_FIND_EXACT_MATCHES =
    "Can't perform the find by criteria exact matches operation";
  /**
   * Can't perform the book operation.
   */
  static final String CANT_BOOK = "Can't perform the book operation";
}
