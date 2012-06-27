/* 
 * @(#)DBAccess.java    1.0 21/06/2010 
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
 * The Sun-provided database access interface to be implemented by the data
 * access class {@link Data}.
 *
 * @author Sun Microsystems
 * @author Lars Kuettner
 * @version 1.0
 */
public interface DBAccess
{

  /**
   * Reads a record from the file. Returns an array where each element is a
   * record value.
   *
   * @param recNo the long value representing the record number that uniquely
   *              identifies the record to be read
   * @return a string array representing the record-to-read
   * @throws RecordNotFoundException if there is no valid record corresponding to
   *                                 <code>recNo</code>
   */
  String[] readRecord(long recNo) throws RecordNotFoundException;

  /**
   * Modifies the fields of a record. The new value for field n appears in
   * data[n]. Throws SecurityException if the record is locked with a cookie
   * other than lockCookie.
   *
   * @param recNo      the long value representing the record number that uniquely
   *                   identifies the record to be updated
   * @param data       the string array representing the record with which to update
   *                   the current record
   * @param lockCookie the long value representing the cookie having been returned
   *                   from an earlier call to <code>lockRecord</code> on the same
   *                   <code>recNo</code>
   * @throws RecordNotFoundException if there is no valid record corresponding to
   *                                 <code>recNo</code>
   * @throws SecurityException       if the record is not locked at all or if the record is locked
   *                                 with a cookie other than lockCookie
   */
  void updateRecord(long recNo, String[] data, long lockCookie)
    throws RecordNotFoundException, SecurityException;

  /**
   * Deletes a record, making the record number and associated disk storage
   * available for reuse. Throws SecurityException if the record is locked
   * with a cookie other than lockCookie.
   *
   * @param recNo      the long value representing the record number that uniquely
   *                   identifies the record to be deleted
   * @param lockCookie the long value representing the cookie having been returned
   *                   from an earlier call to <code>lockRecord</code> on the same
   *                   <code>recNo</code>
   * @throws RecordNotFoundException if there is no valid record corresponding to
   *                                 <code>recNo</code>
   * @throws SecurityException       if the record is not locked at all or if the record is locked
   *                                 with a cookie other than lockCookie
   */
  void deleteRecord(long recNo, long lockCookie)
    throws RecordNotFoundException, SecurityException;

  /**
   * Returns an array of record numbers that match the specified criteria.
   * Field n in the database file is described by criteria[n]. A null value in
   * criteria[n] matches any field value. A non-null value in criteria[n]
   * matches any field value that begins with criteria[n]. (For example,
   * "Fred" matches "Fred" or "Freddy".)
   *
   * @param criteria the string array representing the search criteria
   * @return a long array of record numbers of the matching records
   */
  long[] findByCriteria(String[] criteria);

  /**
   * Creates a new record in the database (possibly reusing a deleted entry).
   * Inserts the given data, and returns the record number of the new record.
   *
   * @param data the string array representing the record to be created
   * @return a long value representing the record number of the newly created
   *         record
   * @throws DuplicateKeyException is actually never thrown (in this version at least) as it was
   *                               decided that records with identical fields are perfectly
   *                               legal
   */
  long createRecord(String[] data) throws DuplicateKeyException;

  /**
   * Locks a record so that it can only be updated or deleted by this client.
   * Returned value is a cookie that must be used when the record is unlocked,
   * updated, or deleted. If the specified record is already locked by a
   * different client, the current thread gives up the CPU and consumes no CPU
   * cycles until the record is unlocked.
   *
   * @param recNo the long value representing the record number that uniquely
   *              identifies the record to be locked
   * @return a long value representing the cookie that must be used when the
   *         record is unlocked, updated, or deleted
   * @throws RecordNotFoundException if there is no valid record corresponding to
   *                                 <code>recNo</code>
   */
  long lockRecord(long recNo) throws RecordNotFoundException;

  /**
   * Releases the lock on a record. Cookie must be the cookie returned when
   * the record was locked; otherwise throws SecurityException.
   *
   * @param recNo  the long value representing the record number that uniquely
   *               identifies the record to be unlocked
   * @param cookie the long value representing the cookie having been returned
   *               from an earlier call to <code>lockRecord</code> on the same
   *               <code>recNo</code>
   * @throws SecurityException if the record is not locked at all or if the record is locked
   *                           with a cookie other than lockCookie
   */
  void unlock(long recNo, long cookie) throws SecurityException;

}
