/* 
 * @(#)Contractor.java    1.0 21/06/2010 
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
 * Property-based representation of a database record, and also used for search
 * criteria on the object level, as opposed to the string array-based record
 * representation.
 * <p>
 * Unlike with the string array-based record representation, the order of the
 * fields is not an issue here, regardless of what is specified in the metadata
 * section of the database file. As a client does not have access to the
 * database file, the <code>Contractor</code> object is the means to exchange
 * information with the server, or between model and view.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public class Contractor implements Serializable {
    /**
     * Declared for serialization compatibility between version. Automatically
     * generated.
     */
    private static final long serialVersionUID = 4711L;

    /**
     * Subcontractor name. The name of the subcontractor the corresponding
     * record relates to.
     */
    private String name;

    /**
     * City. The locality in which this contractor works.
     */
    private String location;

    /**
     * Types of work performed. Comma separated list of types of work this
     * contractor can perform.
     */
    private String specialties;

    /**
     * Number of staff in organization. The number of workers available when the
     * corresponding record is booked.
     */
    private String size;

    /**
     * Hourly charge. Charge per hour for the subcontractor. This field includes
     * the currency symbol.
     */
    private String rate;

    /**
     * Customer holding the corresponding record. The id value (an 8 digit
     * number) of the customer who has booked this. If this field is all blanks,
     * the record is available for sale.
     */
    private String owner;

    /**
     * Gets the subcontractor name.
     * 
     * @return the subcontractor name. The name of the subcontractor the
     *         corresponding record relates to.
     */
    public final String getName() {
	return name;
    }

    /**
     * Sets the subcontractor name.
     * 
     * @param name
     *            the subcontractor name. The name of the subcontractor the
     *            corresponding record relates to.
     */
    public final void setName(final String name) {
	this.name = name;
    }

    /**
     * Gets the location.
     * 
     * @return the city. The locality in which this contractor works.
     */
    public final String getLocation() {
	return location;
    }

    /**
     * Sets the location.
     *
     * @param location
     *            the city. The locality in which this contractor works.
     */
    public final void setLocation(final String location) {
	this.location = location;
    }

    /**
     * Gets the specialties.
     * 
     * @return the types of work performed. Comma separated list of types of
     *         work this contractor can perform.
     */
    public final String getSpecialties() {
	return specialties;
    }

    /**
     * Sets the specialties.
     * 
     * @param specialties
     *            types of work performed. Comma separated list of types of work
     *            this contractor can perform.
     */
    public final void setSpecialties(final String specialties) {
	this.specialties = specialties;
    }

    /**
     * Gets the size.
     * 
     * @return the number of staff in organization. The number of workers
     *         available when the corresponding record is booked.
     */
    public final String getSize() {
	return size;
    }

    /**
     * Sets the size.
     * 
     * @param size
     *            the number of staff in organization. The number of workers
     *            available when the corresponding record is booked.
     */
    public final void setSize(final String size) {
	this.size = size;
    }

    /**
     * Gets the rate.
     * 
     * @return the hourly charge. Charge per hour for the subcontractor. This
     *         field includes the currency symbol.
     */
    public final String getRate() {
	return rate;
    }

    /**
     * Sets the rate.
     * 
     * @param rate
     *            the hourly charge. Charge per hour for the subcontractor. This
     *            field includes the currency symbol.
     */
    public final void setRate(final String rate) {
	this.rate = rate;
    }

    /**
     * Gets the owner.
     * 
     * @return the customer holding the corresponding record. The id value (an 8
     *         digit number) of the customer who has booked this. If this field
     *         is all blanks, the record is available for sale.
     */
    public final String getOwner() {
	return owner;
    }

    /**
     * Sets the owner.
     * 
     * @param owner
     *            the customer holding the corresponding record. The id value
     *            (an 8 digit number) of the customer who has booked this. If
     *            this field is all blanks, the record is available for sale.
     */
    public final void setOwner(final String owner) {
	this.owner = owner;
    }
}
