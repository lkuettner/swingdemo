/* 
 * @(#)ContractorManager.java    1.0 21/06/2010 
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

import suncertify.db.DBSearchFriendlyAccess;
import suncertify.db.Data;
import suncertify.db.DatabaseException;
import suncertify.db.RecordMetaData;
import suncertify.db.RecordNotFoundException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Actually implements the <code>BusinessServices</code> interface designed to
 * support lean clients. Fulfills client requests in close proximity to the data
 * access point. In the model-view-controller (MVC) setting, the
 * <code>ContractorManager</code> represents the role of the server-side model.
 * <p/>
 * Each client request (search, book) is mapped to exactly one distinguished
 * method call of the <code>ContractorManager</code> object. In this method, the
 * request is translated into a sequence of database operations to be executed
 * in rapid succession in the context of a single thread. The contractor manager
 * acts as the liaison between the client and the data access class and
 * abstracts from the data access operations.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public class ContractorManager implements BusinessServices
{

  /**
   * The link to the data access class implementing the extended
   * (search-friendly) data access interface.
   */
  private DBSearchFriendlyAccess dBSearchFriendlyAccess = null;

  /**
   * Creates an instance of the contractor manager that provides an
   * implementation for the <code>BusinessServices</code> interface.
   * <p/>
   * The contractor manager functions as the model component in the MVC
   * setting.
   *
   * @param databaseLocation a string representing the path name to the database file
   * @throws ServicesException if the Singleton instance of the data access class could not
   *                           be obtained
   */
  public ContractorManager(final String databaseLocation)
    throws ServicesException
  {
    try
    {
      dBSearchFriendlyAccess = Data.getInstance(databaseLocation);
    }
    catch (DatabaseException e)
    {
      throw new ServicesException(Text.CANT_GET_DATA_INSTANCE
        + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
    }
  }

  /**
   * Terminates the contractor manager. Relays the termination request to the
   * data access class. Called upon exit.
   */
  public final void terminate()
  {
    Data.terminateInstance();
  }

  @Override
  public final Map<Long, Contractor> search(final String name,
                                            final String location) throws ServicesException
  {
    // Convert contractor-style template to database record-style criteria.
    Contractor searchTemplate = new Contractor();
    searchTemplate.setName(name);
    searchTemplate.setLocation(location);
    String[] criteria = RecordMetaData.contractorToRecord(searchTemplate);
    Map<Long, String[]> matches;
    try
    {
      matches = dBSearchFriendlyAccess
        .findByCriteriaExactMatches(criteria);
    }
    catch (DatabaseException e)
    {
      throw new ServicesException(Text.CANT_FIND_EXACT_MATCHES
        + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
    }
    // Convert record-based matches map to contractor-based results map.
    Map<Long, Contractor> results = new HashMap<Long, Contractor>();
    Iterator<Map.Entry<Long, String[]>> it = matches.entrySet().iterator();
    while (it.hasNext())
    {
      Map.Entry<Long, String[]> pairs = it.next();
      Long recNo = pairs.getKey();
      String[] record = pairs.getValue();
      Contractor contractor = RecordMetaData.recordToContractor(record);
      results.put(recNo, contractor);
    }
    return results;
  }

  @Override
  public final BookResult book(final long recNo, final Contractor contractor)
    throws ServicesException
  {
    // Before updating the record given by its record #, there must be an
    // integrity check as there is a chance that the record has already been
    // modified or even deleted for some other client.
    BookResult bookResult = null;
    long cookie = -1;
    try
    {
      cookie = dBSearchFriendlyAccess.lockRecord(recNo);

      // Verify that record (still) matches the contractor. We know that
      // when calling the search method, it did indeed match. However,
      // there might have been changes to the database record in the
      // meantime. A mismatch is possible and perfectly legal.
      String[] currDBRecord = dBSearchFriendlyAccess.readRecord(recNo);
      Contractor currDBContractor = RecordMetaData
        .recordToContractor(currDBRecord);

      if (!currDBContractor.getOwner().equals(""))
      {
        // Contractor from database already owned by someone.
        bookResult = new BookResult(BookStatus.INTERMITTENTLY_GRABBED,
          currDBContractor);
      }
      else
      {
        // Contractor from database not owned by anyone.
        // Conduct integrity check: compare the two contractors. Do not
        // use the Contractor.equals method directly, though.
        // Spare the owner fields from comparison.
        boolean unchanged = compareContractorsExcludingOwnerField(
          currDBContractor, contractor);
        if (unchanged)
        {
          String[] record = RecordMetaData
            .contractorToRecord(contractor);
          dBSearchFriendlyAccess.updateRecord(recNo, record, cookie);
          bookResult = new BookResult(BookStatus.SUCCESSFULLY_BOOKED,
            contractor);
        }
        else
        {
          bookResult = new BookResult(
            BookStatus.INTERMITTENTLY_UPDATED,
            currDBContractor);
        }
      }
      // Otherwise:
      // Contractors and thus records do not match anymore. Interim
      // change of database record. Contractor/record cannot be
      // booked. This is a regular outcome of invoking this method
      // rather than an exceptional condition.
    }
    catch (RecordNotFoundException e)
    {
      // Record might have been intermittently deleted.
      bookResult = new BookResult(BookStatus.INTERMITTENTLY_DELETED, null);
    }
    catch (DatabaseException e)
    {
      throw new ServicesException(Text.CANT_BOOK
        + Text.NESTED_EXCEPTION_IS + e.getMessage(), e);
    }
    finally
    {
      if (cookie >= 0)
      { // meaning the lock op has been successful
        dBSearchFriendlyAccess.unlockRecord(recNo, cookie);
      }
    }
    return bookResult;
  }

  /**
   * Compare all fields (properties) of two contractors except the owner
   * field.
   *
   * @param c1 the first contractor
   * @param c2 the second contractor
   * @return true if all the properties match, false if the two contractors
   *         differ in at least one property (the owner field in both cases
   *         excluded)
   */
  private boolean compareContractorsExcludingOwnerField(final Contractor c1,
                                                        final Contractor c2)
  {
    return c1.getName().equals(c2.getName())
      && c1.getLocation().equals(c2.getLocation())
      && c1.getSpecialties().equals(c2.getSpecialties())
      && c1.getSize().equals(c2.getSize())
      && c1.getRate().equals(c2.getRate());
  }
}
