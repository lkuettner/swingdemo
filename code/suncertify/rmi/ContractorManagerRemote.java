/* 
 * @(#)ContractorManagerRemote.java    1.0 21/06/2010 
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

package suncertify.rmi;

/**
 * The <code>ContractorManagerRemote</code> interface enables the network client
 * not only to remotely access the business services methods (by extending
 * <code>Remote</code> and <code>BusinessServices</code>), but also to be a
 * remote observer itself getting notification about an imminent server shutdown
 * (by implementing <code>RemoteObservable</code>).
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public interface ContractorManagerRemote extends java.rmi.Remote,
  suncertify.services.BusinessServices, suncertify.util.RemoteObservable
{
}
