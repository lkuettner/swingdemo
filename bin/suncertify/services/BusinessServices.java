/* 
 * @(#)BusinessServices.java    1.0 21/06/2010 
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

import java.rmi.RemoteException;
import java.util.Map;

/**
 * This business services interface is the one that is exposed to clients. It is
 * designed so that each of its methods reflects a certain client request. By
 * using this interface, clients are relieved of the burden of low-level direct
 * interaction with the data access class.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public interface BusinessServices
{
  /**
   * Searches the database for all contractor records with a given name and/or
   * location, and returns a collection of the matching records together with
   * their record numbers for further reference. The search items must match
   * exactly, or be null meaning anything matches.
   *
   * @param name     A string representing the contractor name to search for. If
   *                 not null, an exact match is required. If null, embraces all
   *                 records.
   * @param location A string representing the location to search for. If not null,
   *                 an exact match is required. If null, embraces all records.
   * @return a map of all matching (record number, contractor) pairs
   * @throws RemoteException   if a problem with the remote invocation via RMI is
   *                           encountered. Only relevant in a network setting. In a
   *                           standalone setting, this exception is guaranteed never to be
   *                           thrown.
   * @throws ServicesException if an unexpected problem with the data access class is
   *                           encountered
   */
  Map<Long, Contractor> search(String name, String location)
    throws RemoteException, ServicesException;

  /**
   * Attempts to book a record given by its record number. Successfully
   * booking the record in this context means updating the record with the
   * contractor provided. A record should only be updated if it:
   * <ol>
   * <li>is still a <em>valid</em> record, that is, if it has not been
   * deleted,
   * <li>has not been intermittently updated or replaced, and
   * <li>has not been booked by some other agent
   * </ol>
   *
   * @param recNo      the record number of the record to book acting as the primary
   *                   key into the database
   * @param contractor the <code>Contractor</code> object with which to update the
   *                   record pending the above-mentioned constraints
   * @return A <code>BookResult</code> object representing the result of the
   *         booking operation. Consists of the <code>BookStatus</code> and
   *         the <code>Contractor</code> object post-booking.
   * @throws RemoteException   if a problem with the remote invocation via RMI is
   *                           encountered. Only relevant in a network setting. In a
   *                           standalone setting, this exception is guaranteed never to be
   *                           thrown.
   * @throws ServicesException if an unexpected problem with the data access class is
   *                           encountered. A record having been intermittently deleted does
   *                           not trigger this exception as this should be reckoned with.
   */
  BookResult book(long recNo, final Contractor contractor)
    throws RemoteException, ServicesException;
}
