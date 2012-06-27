/* 
 * @(#)LockInfo.java    1.0 21/06/2010 
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

import java.util.concurrent.atomic.AtomicLong;

/**
 * A Container that manages the information belonging to a single reentrant lock
 * which is applied to database records. This class is <em>not</em> thread-safe.
 * <p/>
 * Each time a record is locked via the <code>lockRecord</code> method, a new
 * <code>LockInfo</code> object is created and put into a collection of locked
 * records, more precisely, a map with record numbers as keys and
 * <code>LockInfo</code> objects as values. To find out whether a record is
 * currently locked, a request is made to the collection. If the collection
 * (map) does not contain an entry for a given record number, this record has
 * not been locked. However, if the map does contain a <code>LockInfo</code>
 * entry, the record is currently locked. The lock may be reentered if it was
 * initially granted to the current thread.
 * <p/>
 * As locks are reentrant, the current thread ID as well as a hold count are
 * stored, and, in addition, a cookie uniquely identifying the lock.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public class LockInfo
{
  /**
   * A generator for the next cookie.
   */
  private static AtomicLong nextLockCookie = new AtomicLong();

  /**
   * The ID of the thread to which the lock was granted.
   */
  private final long lockingThreadId = Thread.currentThread().getId();

  /**
   * The cookie assigned to the lock.
   */
  private final long cookie = nextLockCookie.getAndIncrement();

  /**
   * The hold count that is to be incremented whenever the lock is reentered,
   * and decremented on each relinquish (unlock).
   */
  private int holdCount = 1;

  /**
   * Attempts to reenter the current lock that is already granted to some
   * thread. On success, the hold count is incremented.
   *
   * @return true if the lock has been reentered, false otherwise
   */
  public final boolean tryReenterLock()
  {
    if (Thread.currentThread().getId() == lockingThreadId)
    {
      ++holdCount;
      return true;
    }
    else
    {
      return false;
    }
  }

  /**
   * Attempts to relinquish the current lock. This will succeed if the cookie
   * provided matches the cookie stored.
   *
   * @param cookie a long value representing the cookie that must match to grant
   *               permission
   * @return true if the lock has been relinquished, false otherwise
   */
  public final boolean tryRelinquishLock(final long cookie)
  {
    if (cookie == this.cookie)
    { // matching cookie
      --holdCount;
      return true;
    }
    else
    { // wrong cookie
      return false;
    }
  }

  /**
   * Getter for the cookie that was assigned to the lock upon creation.
   *
   * @return a long value representing the cookie
   */
  public final long getCookie()
  {
    return cookie;
  }

  /**
   * Getter for the current hold count that, if zero, indicates the record is
   * to be considered unlocked.
   *
   * @return the current hold count
   */
  public final int getHoldCount()
  {
    return holdCount;
  }
}
