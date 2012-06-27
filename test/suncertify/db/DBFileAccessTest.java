package suncertify.db;

import static org.junit.Assert.*;

import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.services.Contractor;
import suncertify.util.TestUtils;

public class DBFileAccessTest {

    private DBFileAccess dBFileAccess;

    @Before
    public void setUp() throws Exception {
        String databaseLocation = TestUtils.provideTestDB();

        System.out
                .println("DBFileAccess.getInstance(" + databaseLocation + ")");
        dBFileAccess = DBFileAccess.getInstance(databaseLocation);
        assertNotNull(dBFileAccess);
    }

    @After
    public void tearDown() throws Exception {
        System.out
                .println("dBFileAccess.terminate() - close random access file");
        dBFileAccess.terminate();

        TestUtils.deleteTestDB();
    }

    @Test
    public void testAccess() {
        TreeMap<Long, String[]> records = null;
        String[] data = null;
        Contractor c = new Contractor();
        long[] deleteRecNos = new long[]{21, 0, 2, 3, 8, 5, 13, 27, 1};
        long[] placeNewRecNos = new long[]{0, 2, 5, 13, 27, 28, 29};
        long[] updateRecNos = new long[]{0, 5, 27, 29};

        records = TestUtils.readRecords(dBFileAccess);
        assertTrue(records.size() == 28);

        // Print the initial set of valid records.
        TestUtils.printRecords(records);

        // Delete various records.
        for (long l : deleteRecNos) {
            System.out.println("deleteRecord(" + l + ")");
            try {
                dBFileAccess.deleteRecord(l);
            } catch (Exception e) {
                System.err.println("deleteRecord(" + l + ") failed");
                e.printStackTrace();
            }
        }
        records = TestUtils.readRecords(dBFileAccess);
        assertTrue(records.size() == 19);

        // Place various new records into some of the gaps just deleted.
        c.setName("Wendelin, Jakob und Lars Kuettner");
        c.setLocation("Schwarzwildweg 83, 14612 Falkensee OT Waldheim"
                + ", Brandenburg, Deutschland");
        c.setSpecialties("Programming, Cycling, Babysitting"
                + ", Like-a-bike riding, Toy-train playing, Reading");
        c.setSize("3");
        c.setRate("$0.00");
        c.setOwner("12345678");
        data = RecordMetaData.contractorToRecord(c);
        for (long l : placeNewRecNos) {
            System.out.println("placeNewRecord(" + l + ")");
            try {
                dBFileAccess.placeNewRecord(l, data);
            } catch (Exception e) {
                System.err.println("placeNewRecord(" + l + ") failed");
                e.printStackTrace();
            }
        }
        records = TestUtils.readRecords(dBFileAccess);
        assertTrue(records.size() == 26);

        // Update records.
        c.setOwner("");
        data = RecordMetaData.contractorToRecord(c);
        for (long l : updateRecNos) {
            System.out.println("updateRecord(" + l + ")");
            try {
                dBFileAccess.updateRecord(l, data);
            } catch (Exception e) {
                System.err.println("updateRecord(" + l + ") failed");
                e.printStackTrace();
            }
        }
        records = TestUtils.readRecords(dBFileAccess);
        assertTrue(records.size() == 26);

        // Print the final set of valid records.
        TestUtils.printRecords(records);
    }
}
