/*
 * @(#)Data.java    1.0 21/06/2010
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The data access class that implements the required public database interface
 * <code>DBAccess</code> via its extension, <code>DBSearchFriendlyAccess</code>.
 * All access to the database from the outside has to occur through the
 * Singleton instance of this class. The class is thread-safe and optimized for
 * high performance by means of a write-through cache.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public final class Data implements DBSearchFriendlyAccess
{

  /**
   * Logger object to log messages in the scope of this class.
   */
  private static final Logger LOG = Logger.getLogger(Data.class.getName());

  /**
   * The Singleton instance.
   */
  private static final Data INSTANCE = new Data();

  /**
   * The handle to the database file access point.
   */
  private DBFileAccess dBFileAccess = null;

  /**
   * The actual cache, implemented as a map of the valid (record number,
   * record data) pairs.
   */
  private Map<Long, String[]> cachedRecords = null;

  /**
   * The collection of recyclable record numbers that is fed from deleted
   * records not yet having been reassigned, implemented as a tree set because
   * maintaining the order of the recyclable record numbers is important.
   */
  private TreeSet<Long> recyclableRecordNumbers = null;

  /**
   * The collection of currently locked records, implemented as a map of
   * (record number, lock info) pairs. On each successful lock record
   * operation, an entry into this map will be generated. Likewise, an unlock
   * record operation leads to an entry being removed from this map. If a new
   * locking request is made and this map already contains an entry from
   * another thread, the locking operation will block until the lock will have
   * been released. Enables reentrant locking.
   */
  private Map<Long, LockInfo> lockedRecordsInfo = null;

  /**
   * Enables locking access on the entire cache efficiently by means of a
   * read/write lock so that multiple non-modifying read operations can take
   * place in parallel. Fairness policy explicitly set to "fair".
   */
  private ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

  /**
   * The read lock component of the reentrant read/write lock, needed for the
   * read record and find operations that can thus take place in parallel.
   */
  private Lock readLock = readWriteLock.readLock();

  /**
   * The write lock component of the reentrant read/write lock, needed for all
   * operations other than read record and find, such as create, update,
   * delete, lock, unlock record, but also to enforce serial access to
   * administrative methods like get and terminate instance.
   */
  private Lock writeLock = readWriteLock.writeLock();

  /**
   * A condition with which to signal the removal of a lock from the
   * <code>lockedRecordsInfo</code> map. When a thread tries to acquire a lock
   * on a record that has already been locked by another thread, the thread
   * blocks on the condition. Upon signaling the condition, the thread resumes
   * its operation in that it checks the collection of locked records again,
   * and if the record which it is trying to acquire the lock for has been
   * removed, acquires the lock.
   */
  private Condition lockInfoRemoved = writeLock.newCondition();

  /**
   * The Singleton constructor.
   */
  private Data()
  {
  }

  /**
   * Factory method to get the Singleton instance of the data access class
   * through which all database requests from the outside have to be
   * conducted. This method may be called more than once provided the database
   * location is the same every time.
   *
   * @param databaseLocation a string representing the path name to the database file
   * @return the Singleton instance of the data access class
   * @throws DatabaseException if there is a problem with either the
   *                           <code>databaseLocation</code> parameter or the database file
   *                           the parameter refers to
   */
  public static Data getInstance(final String databaseLocation)
  {
    DBFileAccess dBFileAccess = null;
    try
    {
      INSTANCE.writeLock.lock();

      if (INSTANCE.dBFileAccess == null)
      {
        // DBFileAccess.getInstance must be called only once - here!
        try
        {
          dBFileAccess = DBFileAccess.getInstance(databaseLocation);
        }
        catch (DatabaseFileException e)
        {
          throw new DatabaseException(
            Text.CANT_GET_FILE_ACCESS_INSTANCE
              + Text.NESTED_EXCEPTION_IS + e.getMessage(),
            e);
        }

        try
        {
          INSTANCE.initialize(dBFileAccess);
        }
        catch (DatabaseException e)
        {
          // To guarantee failure atomicity: undo
          // DBFileAccess.getInstance
          dBFileAccess.terminate();
          dBFileAccess = null;
          // Re-throw database exception
          throw e;
        }
      }
      else
      { // INSTANCE has already been initialized
        // Verify that database location matches
        boolean match = false;
        try
        {
          match = INSTANCE.dBFileAccess
            .matchDatabaseLocation(databaseLocation);
        }
        catch (DatabaseFileException e)
        {
          LOG.log(Level.WARNING, "Can't match database location: "
            + e.getMessage(), e);
          // A match status of false rightly triggers an exception
        }
        if (match == false)
        { // wrong/different or no path
          throw new DatabaseException(
            Text.DATABASE_LOCATION_MISMATCH);
        }
        // INSTANCE is NOT initialized a second time
      }
    }
    finally
    {
      INSTANCE.writeLock.unlock();
    }
    return INSTANCE;
  }

  /**
   * Iterates through the records in the database file and fills with them the
   * write-through record cache. All other member variables are also
   * initialized.
   *
   * @param dBFileAccess the Singleton instance of the database file access point
   * @throws DatabaseException if some problem occurred reading the records from the
   *                           database file
   */
  private void initialize(final DBFileAccess dBFileAccess)
  {
    Map<Long, String[]> cachedRecords = new HashMap<Long, String[]>();
    TreeSet<Long> recyclableRecordNumbers = new TreeSet<Long>();

    assert this.dBFileAccess == null;

    // Fill map of cached records
    try
    {
      Iterator<Long> validRecordNumberIterator = dBFileAccess.iterator();
      long prevRecNo = -1; // To determine gaps b/w consecutive record
      // #'s.
      while (validRecordNumberIterator.hasNext())
      {
        long recNo = validRecordNumberIterator.next();
        String[] recData = null;

        recData = dBFileAccess.readRecord(recNo);
        cachedRecords.put(recNo, recData);

        // Update recyclableRecordNumbers if necessary (i.e., on gaps in
        // the sequence of valid record numbers). Each missing record #
        // in
        // the sequence of valid record numbers corresponds to an empty,
        // recyclable slot in the database file.
        while (++prevRecNo < recNo)
        {
          recyclableRecordNumbers.add(prevRecNo);
        }
      }
    }
    catch (Exception e)
    {
      // Either a runtime exception thrown by the iterator, or a record
      // not found exception or a database file exception thrown by the
      // read record method.
      throw new DatabaseException(Text.CANT_INITIALIZE_DATABASE_CACHE
        + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
    }

    // Assignments here b/o failure atomicity
    this.dBFileAccess = dBFileAccess;
    this.cachedRecords = cachedRecords;
    this.recyclableRecordNumbers = recyclableRecordNumbers;

    lockedRecordsInfo = new HashMap<Long, LockInfo>();
  }

  /**
   * Gracefully terminates the data access mechanism.
   * <p/>
   * Usually called when terminating the
   * {@link suncertify.services.ContractorManager} (then in the context of a
   * shutdown hook) - or directly while unit testing.
   */
  public static void terminateInstance()
  {
    // Lock the database so that no other thread (think of RMI) can access
    // it anymore.
    try
    {
      INSTANCE.writeLock.lock();
      // Propagate to file access layer.
      // This test is because this method may be called explicitly
      // (especially while unit testing) and also via the shutdown hook.
      if (INSTANCE.dBFileAccess != null)
      {
        INSTANCE.dBFileAccess.terminate();

        INSTANCE.dBFileAccess = null;
        INSTANCE.cachedRecords = null;
        INSTANCE.recyclableRecordNumbers = null;
        INSTANCE.lockedRecordsInfo = null;
      }
    }
    finally
    {
      INSTANCE.writeLock.unlock();
    }
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public String[] readRecord(final long recNo)
    throws RecordNotFoundException
  {
    String[] data;
    try
    {
      readLock.lock();

      if (dBFileAccess == null)
      {
        throw new DatabaseException(Text.DATABASE_CLOSED);
      }

      // Acquire data of record.
      data = cachedRecords.get(recNo);
      if (data == null)
      {
        throw new RecordNotFoundException(String.format(
          Text.RECORD_NOT_FOUND, recNo));
      }
      else
      {
        // Clone to decouple the returned record data from the cache.
        data = data.clone();
      }
    }
    finally
    {
      readLock.unlock();
    }
    return data;
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public long createRecord(final String[] data)
  {
    long recNo = -1;
    try
    {
      writeLock.lock();

      if (dBFileAccess == null)
      {
        throw new DatabaseException(Text.DATABASE_CLOSED);
      }

      // Retrieve the first unused record number. If there is no unused
      // record number, assign as the next free record number the size of
      // the cache.
      recNo = recyclableRecordNumbers.isEmpty() ? cachedRecords.size()
        : recyclableRecordNumbers.pollFirst();
      assert (cachedRecords.containsKey(recNo) == false);
      // Write-through to database file
      try
      {
        // Note: In the process of writing to the database file,
        // individual data strings may be cropped (truncated) and thus
        // replaced.
        dBFileAccess.placeNewRecord(recNo, data);
      }
      catch (RecordNotFoundException e)
      {
        // A programming error (that never occurs, of course)
        LOG.log(Level.SEVERE, Text.CANT_PLACE_NEW_RECORD, e);
        assert false;
      }
      catch (DatabaseFileException e)
      {
        // Replace checked exception with an unchecked one
        throw new DatabaseException(Text.CANT_PLACE_NEW_RECORD
          + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
      }
      // Write the possibly cropped data from the database file into the
      // cache.
      cachedRecords.put(recNo, data);
    }
    finally
    {
      writeLock.unlock();
    }
    return recNo;
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public void updateRecord(final long recNo, final String[] data,
                           final long lockCookie) throws RecordNotFoundException,
    SecurityException
  {
    try
    {
      writeLock.lock();

      if (dBFileAccess == null)
      {
        throw new DatabaseException(Text.DATABASE_CLOSED);
      }

      // Verify that the record is in cache.
      if (cachedRecords.containsKey(recNo) == false)
      {
        throw new RecordNotFoundException(String.format(
          Text.RECORD_NOT_FOUND, recNo));
      }
      // Verify that record has been properly locked, i.e., locked at all
      // and with the right cookie.
      LockInfo lockInfo = lockedRecordsInfo.get(recNo);
      if (lockInfo == null)
      {
        throw new SecurityException(String.format(
          Text.RECORD_NOT_LOCKED, recNo));
      }
      if (lockInfo.getCookie() != lockCookie)
      {
        throw new SecurityException(String.format(
          Text.RECORD_COOKIE_MISMATCH, recNo, lockCookie));
      }
      // Write-through to database file
      try
      {
        // Note: In the process of writing to the database file,
        // individual data strings may be cropped (truncated) and thus
        // replaced.
        dBFileAccess.updateRecord(recNo, data);
      }
      catch (RecordNotFoundException e)
      {
        // A programming error (that never occurs, of course)
        LOG.log(Level.SEVERE, Text.CANT_UPDATE_RECORD, e);
        assert false;
      }
      catch (DatabaseFileException e)
      {
        // Replace checked exception with an unchecked one
        throw new DatabaseException(Text.CANT_UPDATE_RECORD
          + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
      }
      // Write the possibly cropped data from the database file into the
      // cache.
      cachedRecords.put(recNo, data);
    }
    finally
    {
      writeLock.unlock();
    }
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public void deleteRecord(final long recNo, final long lockCookie)
    throws RecordNotFoundException, SecurityException
  {
    try
    {
      writeLock.lock();

      if (dBFileAccess == null)
      {
        throw new DatabaseException(Text.DATABASE_CLOSED);
      }

      if (cachedRecords.containsKey(recNo) == false)
      {
        throw new RecordNotFoundException("record #" + recNo
          + " not found");
      }
      // Verify that record has been properly locked, i.e., locked at all
      // and with the right cookie.
      LockInfo lockInfo = lockedRecordsInfo.get(recNo);
      if (lockInfo == null)
      {
        throw new SecurityException(String.format(
          Text.RECORD_NOT_LOCKED, recNo));
      }
      if (lockInfo.getCookie() != lockCookie)
      {
        throw new SecurityException(String.format(
          Text.RECORD_COOKIE_MISMATCH, recNo, lockCookie));
      }
      // Remove record from cache.
      cachedRecords.remove(recNo);
      // Memorize record number for recycling.
      recyclableRecordNumbers.add(recNo);
      // Remove record from database file.
      try
      {
        dBFileAccess.deleteRecord(recNo);
      }
      catch (RecordNotFoundException e)
      {
        // A programming error (that never occurs, of course)
        LOG.log(Level.SEVERE, Text.CANT_UPDATE_RECORD, e);
        assert false;
      }
      catch (DatabaseFileException e)
      {
        // Replace checked exception with an unchecked one
        throw new DatabaseException(Text.CANT_DELETE_RECORD
          + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
      }
      // Remove lock from locked records info.
      lockedRecordsInfo.remove(recNo);
      // Signal release of lock info.
      lockInfoRemoved.signalAll();
    }
    finally
    {
      writeLock.unlock();
    }
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public long[] findByCriteria(final String[] criteria)
  {
    long[] recordNumbers = null;
    ArrayList<Long> recNos = new ArrayList<Long>();
    try
    {
      readLock.lock();

      if (dBFileAccess == null)
      {
        throw new DatabaseException(Text.DATABASE_CLOSED);
      }

      // Iterate over map
      Iterator<Map.Entry<Long, String[]>> it = cachedRecords.entrySet()
        .iterator();
      while (it.hasNext())
      {
        Map.Entry<Long, String[]> pairs = it.next();
        String[] record = pairs.getValue();
        if (testPartialMatch(record, criteria))
        {
          recNos.add(pairs.getKey());
        }
      }
    }
    finally
    {
      readLock.unlock();
    }
    // Convert ArrayList<Long> to long[].
    recordNumbers = new long[recNos.size()];
    for (int i = 0; i < recNos.size(); ++i)
    {
      recordNumbers[i] = recNos.get(i);
    }
    return recordNumbers;
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public Map<Long, String[]> findByCriteriaExactMatches(
    final String[] criteria)
  {
    Map<Long, String[]> matchingRecords = new HashMap<Long, String[]>();
    try
    {
      readLock.lock();

      if (dBFileAccess == null)
      {
        throw new DatabaseException(Text.DATABASE_CLOSED);
      }

      // Iterate over map.
      Iterator<Map.Entry<Long, String[]>> it = cachedRecords.entrySet()
        .iterator();
      while (it.hasNext())
      {
        Map.Entry<Long, String[]> pairs = it.next();
        String[] record = pairs.getValue();
        if (testExactMatch(record, criteria))
        {
          matchingRecords.put(pairs.getKey(), pairs.getValue()
            .clone());
        }
      }
    }
    finally
    {
      readLock.unlock();
    }
    return matchingRecords;
  }

  /**
   * Matching algorithm for the original interface.
   *
   * @param record   a string array representing the record to match
   * @param criteria a string array representing the criteria the record is matched
   *                 against
   * @return <code>true</code> if <code>record</code> matches
   *         <code>criteria</code>, otherwise <code>false</code>
   */
  private boolean testPartialMatch(final String[] record,
                                   final String[] criteria)
  {
    for (int i = 0; i < criteria.length; ++i)
    {
      if (criteria[i] != null
        && (record.length <= i || record[i] == null || record[i]
        .startsWith(criteria[i]) == false))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Matching algorithm that is actually needed (exact rather than partial
   * match required).
   *
   * @param record   a string array representing the record to match
   * @param criteria a string array representing the criteria the record is matched
   *                 against
   * @return <code>true</code> if <code>record</code> matches
   *         <code>criteria</code>, otherwise <code>false</code>
   */
  private boolean testExactMatch(final String[] record,
                                 final String[] criteria)
  {
    for (int i = 0; i < criteria.length; ++i)
    {
      if (criteria[i] != null
        && (record.length <= i || record[i] == null || record[i]
        .equals(criteria[i]) == false))
      {
        return false;
      }
    }
    return true;
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public long lockRecord(final long recNo) throws RecordNotFoundException
  {
    // Implements reentrant locking. If the current thread already holds the
    // lock, a counter is incremented and the established cookie is
    // returned.
    LockInfo lockInfo = null;
    try
    {
      writeLock.lock();

      if (dBFileAccess == null)
      {
        throw new DatabaseException(Text.DATABASE_CLOSED);
      }

      while ((lockInfo = lockedRecordsInfo.get(recNo)) != null
        && lockInfo.tryReenterLock() == false)
      {
        lockInfoRemoved.awaitUninterruptibly();
      }
      if (lockInfo == null)
      {
        // Only if there is a corresponding record does it make sense to
        // acquire a lock.
        if (cachedRecords.containsKey(recNo) == false)
        {
          // There is no corresponding record.
          throw new RecordNotFoundException(String.format(
            Text.RECORD_NOT_FOUND, recNo));
        }
        // There is a record in the map with recNo as its key.
        lockInfo = new LockInfo();
        lockedRecordsInfo.put(recNo, lockInfo);
      }
    }
    finally
    {
      writeLock.unlock();
    }
    assert (lockInfo != null);
    return lockInfo.getCookie();
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public void unlockRecord(final long recNo, final long cookie)
    throws SecurityException
  {
    try
    {
      writeLock.lock();

      if (dBFileAccess == null)
      {
        throw new DatabaseException(Text.DATABASE_CLOSED);
      }

      LockInfo lockInfo = lockedRecordsInfo.get(recNo);
      if (lockInfo != null)
      {
        // Lock may be held several times. Try to relinquish the lock by
        // decrementing the counter first but only on matching cookies.
        if (lockInfo.tryRelinquishLock(cookie) == false)
        {
          // cookie mismatch
          throw new SecurityException(String.format(
            Text.RECORD_COOKIE_MISMATCH, recNo, cookie));
        }
        else if (lockInfo.getHoldCount() == 0)
        {
          lockedRecordsInfo.remove(recNo);
          lockInfoRemoved.signalAll();
        }
      }
      else
      { // lockInfo == null
        // Attempt to unlock a record that has already been unlocked -
        // or deleted. No error. Keep idle.
        ;
      }
    }
    finally
    {
      writeLock.unlock();
    }
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public long lock(final long recNo) throws RecordNotFoundException
  {
    return lockRecord(recNo);
  }

  /**
   * @throws DatabaseException if the database file access point has been closed
   */
  @Override
  public void unlock(final long recNo, final long cookie)
    throws SecurityException
  {
    unlockRecord(recNo, cookie);
  }
}
