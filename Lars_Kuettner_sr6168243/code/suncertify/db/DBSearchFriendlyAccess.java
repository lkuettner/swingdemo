/* 
 * @(#)DBSearchFriendlyAccess.java    1.0 21/06/2010 
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

import java.util.Map;

/**
 * An extension of the supplied database access interface {@link DBAccess} that
 * permits concise searches and remedies minor method-naming inconsistencies of
 * the original database access interface.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public interface DBSearchFriendlyAccess extends DBAccess {
    /**
     * @throws DatabaseException
     *             if the database file access point has been closed
     */
    @Override
    String[] readRecord(long recNo) throws RecordNotFoundException;

    /**
     * @throws DatabaseException
     *             if the database file access point has been closed
     */
    @Override
    void updateRecord(long recNo, String[] data, long lockCookie)
	    throws RecordNotFoundException, SecurityException;

    /**
     * @throws DatabaseException
     *             if the database file access point has been closed
     */
    @Override
    void deleteRecord(long recNo, long lockCookie)
	    throws RecordNotFoundException, SecurityException;

    /**
     * @throws DatabaseException
     *             if the database file access point has been closed
     */
    @Override
    long[] findByCriteria(String[] criteria);

    /**
     * 
     * @throws DatabaseException
     *             if the database file access point has been closed
     */
    @Override
    long createRecord(String[] data);

    /**
     * @throws DatabaseException
     *             if the database file access point has been closed
     */
    @Override
    long lockRecord(long recNo) throws RecordNotFoundException;

    /**
     * @throws DatabaseException
     *             if the database file access point has been closed
     */
    @Override
    void unlock(long recNo, long cookie) throws SecurityException;

    /**
     * Retrieves all database records that exactly match the given criteria.
     * Field n in the database file is described by criteria[n]. A null value in
     * criteria[n] matches any field value. A non-null value in criteria[n]
     * matches any field value that exactly matches criteria[n].
     * 
     * @param criteria
     *            the string array representing the search criteria
     * @throws DatabaseException
     *             if the database file access point has been closed
     * @return a map of (record number, record data) pairs of all matching
     *         records
     */
    Map<Long, String[]> findByCriteriaExactMatches(String[] criteria);

    /**
     * Locks a record so that it can only be updated or deleted by this client.
     * Returned value is a cookie that must be used when the record is unlocked,
     * updated, or deleted. If the specified record is already locked by a
     * different client, the current thread gives up the CPU and consumes no CPU
     * cycles until the record is unlocked.
     * 
     * @param recNo
     *            the long value representing the record number that uniquely
     *            identifies the record to be locked
     * @return a long value representing the cookie that must be used when the
     *         record is unlocked, updated, or deleted
     * @throws DatabaseException
     *             if the database file access point has been closed
     * @throws RecordNotFoundException
     *             if there is no valid record corresponding to
     *             <code>recNo</code>
     * @see DBAccess#unlock(long, long)
     * @see DBAccess#lockRecord(long)
     */
    long lock(long recNo) throws RecordNotFoundException;

    /**
     * Releases the lock on a record. Cookie must be the cookie returned when
     * the record was locked; otherwise throws SecurityException.
     * 
     * @param recNo
     *            the long value representing the record number that uniquely
     *            identifies the record to be unlocked
     * @param cookie
     *            the long value representing the cookie having been returned
     *            from an earlier call to <code>lockRecord</code> on the same
     *            <code>recNo</code>
     * @throws DatabaseException
     *             if the database file access point has been closed
     * @throws SecurityException
     *             if the record is not locked at all or if the record is locked
     *             with a cookie other than lockCookie
     * @see DBAccess#lockRecord(long)
     * @see DBAccess#unlock(long, long)
     */
    void unlockRecord(long recNo, long cookie) throws SecurityException;

}
