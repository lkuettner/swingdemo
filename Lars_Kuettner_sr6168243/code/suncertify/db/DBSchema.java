/*
 * @(#)DBSchema.java    1.0 21/06/2010 
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
 * Represents the database record schema definition according to the written
 * specification. The information in this uninstantiable utility class are
 * independent of any database file.
 * 
 * @author Lars Kuettner
 * @version 1.0
 */
public final class DBSchema {

    /**
     * The private constructors makes this an uninstantiable utility class.
     */
    private DBSchema() {
    }

    /**
     * The subcontractor name index with which all the name-related information
     * can be retrieved.
     */
    public static final int NAME_INDEX = 0;

    /**
     * The location index with which all the location-related information can be
     * retrieved.
     */
    public static final int LOCATION_INDEX = 1;

    /**
     * The specialties index with which all the specialties-related information
     * can be retrieved.
     */
    public static final int SPECIALTIES_INDEX = 2;

    /**
     * The size index with which all the size-related information can be
     * retrieved.
     */
    public static final int SIZE_INDEX = 3;

    /**
     * The rate index with which all the rate-related information can be
     * retrieved.
     */
    public static final int RATE_INDEX = 4;

    /**
     * The owner index with which all the owner-related information can be
     * retrieved.
     */
    public static final int OWNER_INDEX = 5;

    /**
     * The field names as they should appear in the GUI, in indexed order.
     */
    private static final String[] FIELD_NAMES = {
	    "Name",
	    "Location",
	    "Specialties",
	    "Size",
	    "Rate",
	    "Owner"
    };

    /**
     * Brief field descriptions, in indexed order.
     */
    private static final String[] FIELD_DESCRIPTIONS = {
	"Subcontractor name",
	"City",
	"Types of work performed",
	"Number of staff in organization",
	"Hourly charge",
	"Customer holding this record"
    };

    /**
     * Detailed field descriptions, in indexed order.
     */
    private static final String[] FIELD_DETAILED_DESCRIPTIONS = {
	    "The name of the subcontractor this record relates to",
	    "The locality in which this contractor works",
	    "Comma-separated list of types of work this contractor can perform",
	    "The number of workers available when this record is booked",
	    "Charge per hour for the subcontractor. "
		    + "This field includes the currency symbol",
	    "The id value (an 8 digit number) of the customer "
		    + "who has booked/is about to book this" };

    /**
     * The maximum lengths of the record fields, in indexed order.
     */
    private static final int[] FIELD_LENGTHS = {
	    32, // name
	    64, // location
	    64, // specialties
	    6, // size
	    8, // rate
	    8 // owner
    };

    static {
	assert FIELD_NAMES.length == FIELD_DESCRIPTIONS.length;
	assert FIELD_NAMES.length == FIELD_DETAILED_DESCRIPTIONS.length;
	assert FIELD_NAMES.length == FIELD_LENGTHS.length;
    }

    /**
     * Provides the number of fields of a record.
     * 
     * @return the number of fields of a record
     */
    public static int getNumberOfFields() {
	return FIELD_NAMES.length;
    }

    /**
     * Provides the field name belonging to a given field index. Field indexes
     * are given as public static constants of this class.
     * 
     * @param fieldIndex
     *            the field index represented by one of the index constants of
     *            this class
     * @return the field name corresponding to the index given
     */
    public static String getFieldName(final int fieldIndex) {
	return FIELD_NAMES[fieldIndex];
    }

    /**
     * Provides the (brief) field description belonging to a given field index.
     * Field indexes are given as public static constants of this class.
     * 
     * @param fieldIndex
     *            the field index represented by one of the index constants of
     *            this class
     * @return the brief field description corresponding to the index given
     */
    public static String getFieldDescription(final int fieldIndex) {
	return FIELD_DESCRIPTIONS[fieldIndex];
    }

    /**
     * Provides the detailed field description belonging to a given field index.
     * Field indexes are given as public static constants of this class.
     * 
     * @param fieldIndex
     *            the field index represented by one of the index constants of
     *            this class
     * @return the detailed field description corresponding to the index given
     */
    public static String getFieldDetailedDescription(final int fieldIndex) {
	return FIELD_DETAILED_DESCRIPTIONS[fieldIndex];
    }

    /**
     * Provides the maximum field length belonging to a given field index. Field
     * indexes are given as public static constants of this class.
     * 
     * @param fieldIndex
     *            the field index represented by one of the index constants of
     *            this class
     * @return the maximum field length corresponding to the index given
     */
    public static int getFieldLength(final int fieldIndex) {
	return FIELD_LENGTHS[fieldIndex];
    }

    /**
     * Returns the field index given the field name. The field name must exactly
     * match one of the field names specified in advance
     * 
     * @param fieldName
     *            a string representing the field name whose index is to be
     *            determined
     * @return the index of the field name or -1 if no matching field name could
     *         be found (via <code>equalsIgnoreCase</code>)
     */
    public static int getFieldIndex(final String fieldName) {
	for (int i = 0; i < FIELD_NAMES.length; ++i) {
	    if (fieldName.equals(FIELD_NAMES[i])) {
		return i;
	    }
	}
	return -1;
    }

    /**
     * Converts a field name given in arbitrary case letters, upper or lower,
     * into the exact field name as specified as a constant string in this
     * class, which then can be used to retrieve the field index.
     * 
     * @param fieldNameAnyCase
     *            a string representing the case-insensitive field name whose
     *            case-sensitive equivalent is to be found
     * @return a string representing the case-sensitive equivalent of the
     *         case-insensitive field name provided, or <code>null</code> if
     *         there is no match. The match is checked using
     *         <code>equalsIgnoreCase</code>.
     */
    public static String getFieldName(final String fieldNameAnyCase) {
	for (int i = 0; i < FIELD_NAMES.length; ++i) {
	    if (fieldNameAnyCase.equalsIgnoreCase(FIELD_NAMES[i])) {
		return FIELD_NAMES[i];
	    }
	}
	return null;
    }
}
