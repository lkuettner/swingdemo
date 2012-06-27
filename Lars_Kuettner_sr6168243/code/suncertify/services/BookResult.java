/* 
 * @(#)BookResult.java    1.0 21/06/2010 
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

import java.io.Serializable;

/**
 * The result of an attempted book operation. As the database cannot and should
 * not be locked between subsequent business services operations, the state of
 * the database record at the moment of booking may have changed since the last
 * search operation, and the database record may, in the meantime, have been
 * deleted, updated, or booked by some other agent. These conditions are
 * reflected in the <code>BookResult</code>. Furthermore, the post-booking
 * contractor is also part of the <code>BookResult</code> so that, after an
 * attempted booking request, the respective record may be immediately updated.
 * 
 * @author Lars Kuettner
 * @version 1.0
 * @see BusinessServices#book
 */
public class BookResult implements Serializable {
    /**
     * A magic version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and deserialization.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * The status of the booking operation indicating success, contractor
     * intermittently grabbed, updated, or deleted.
     */
    private BookStatus bookStatus;

    /**
     * The post-booking contractor. If the contractor record turns out to have
     * been intermittently deleted, this field will be null.
     */
    private Contractor contractor;

    /**
     * Creates a <code>BookResult</code> object and is the only means of
     * assigning values to the fields.
     * 
     * @param bookStatus
     *            An enumeration constant representing the status of the booking
     *            operation. Due to the concurrent multi-client environment, it
     *            is perfectly legal for a booking operation to result in
     *            something other than success.
     * @param contractor
     *            A <code>Contractor</code> object representing the record field
     *            values with which to update the record if booking is possible.
     */
    public BookResult(final BookStatus bookStatus,
	    final Contractor contractor) {
	this.bookStatus = bookStatus;
	this.contractor = contractor;
    }

    /**
     * Provides the status component of the <code>BookResult</code> object.
     * 
     * @return the status enumeration constant indicating whether the record has
     *         been successfully booked or not, and if not, why not: because the
     *         record has been intermittently grabbed by some other agent,
     *         deleted, or modified
     */
    public final BookStatus getBookStatus() {
	return bookStatus;
    }

    /**
     * Provides the <code>Contractor</code> object that reflects the state of
     * the database record under consideration upon return of the booking
     * operation.
     * 
     * @return the <code>Contractor</code> object reflecting the state of the
     *         database record after the booking attempt, <code>null</code> on a
     *         record that has been intermittently deleted
     */
    public final Contractor getContractor() {
	return contractor;
    }
}
