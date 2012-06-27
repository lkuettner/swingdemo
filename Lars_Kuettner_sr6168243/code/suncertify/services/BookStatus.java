/* 
 * @(#)BookStatus.java    1.0 21/06/2010 
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
 * Enumerates the possible returned statuses of an attempted booking operation.
 * 
 * @author Lars Kuettner
 * @version 1.0
 * @see BusinessServices#book
 */
public enum BookStatus {
    /**
     * Contractor record successfully booked.
     */
    SUCCESSFULLY_BOOKED,
    /**
     * Contractor record intermittently grabbed (booked) by another agent and
     * thus <em>not</em> booked by the current caller.
     */
    INTERMITTENTLY_GRABBED,
    /**
     * Contractor record intermittently deleted indicating the decision to book
     * this record being based on stale information.
     */
    INTERMITTENTLY_DELETED,
    /**
     * Contractor record intermittently updated or even replaced as record
     * numbers are recycled. No booking performed. May be retried on the basis
     * of the new record data.
     */
    INTERMITTENTLY_UPDATED
}
