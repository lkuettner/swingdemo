package suncertify.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import suncertify.services.Contractor;
import suncertify.util.TestUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class DataTest {
    // # of records originally in database
    private final long nRecords = 28;

    private Data DATA;
    private final Contractor contractor = new Contractor();
    // Concurrency tests - fixed thread pool size.
    private final int nThreads = 42;

    @Before
    public void setUp() throws Exception {
        String databaseLocation = TestUtils.provideTestDB();

        DATA = Data.getInstance(databaseLocation);
        assertNotNull(DATA);
        // Initialize contractor
        contractor.setName("Wendelin, Jakob und Lars Kuettner");
        contractor.setLocation("Schwarzwildweg 83, 14612 Falkensee OT Waldheim"
                + ", Brandenburg, Deutschland");
        contractor.setSpecialties("Programming, Cycling, Babysitting"
                + ", Like-a-bike riding, Toy-train playing, Reading");
        contractor.setSize("3");
        contractor.setRate("$0.00");
        contractor.setOwner("12345678");
    }

    @After
    public void tearDown() throws Exception {
        Data.terminateInstance();

        TestUtils.deleteTestDB();
    }

    @Test
    public void testReadRecord() {
        System.out.println("testReadRecord");
        TreeMap<Long, String[]> records = new TreeMap<Long, String[]>();
        HashSet<Long> recNos = new HashSet<Long>();
        for (long l = 0; l < nRecords; ++l) {
            recNos.add(l);
        }
        records.clear();
        for (long recNo : recNos) {
            String[] data = null;
            try {
                data = DATA.readRecord(recNo);
            } catch (RecordNotFoundException e) {
                System.err.println("readRecord(" + recNo + ") failed");
                e.printStackTrace();
            }
            records.put(recNo, data);
        }
        TestUtils.printRecords(records);
        assertTrue("records.size(=" + records.size(),
                records.size() == nRecords);
    }

    @Test
    public void testCreateRecord() {
        System.out.println("testCreateRecord");
        // Delete some records to produce gaps.
        long[] delRecNos = new long[]{1, 2, 4, 8, 16, 27};
        final int nRecordsToCreate = 7;

        for (long recNo : delRecNos) {
            long cookie = -1;
            try {
                cookie = DATA.lockRecord(recNo);
            } catch (RecordNotFoundException e) {
                System.err.println("lockRecord(" + recNo + ") failed");
                e.printStackTrace();
            }
            try {
                System.out.println("deleteRecord(" + recNo + ")");
                DATA.deleteRecord(recNo, cookie);
            } catch (SecurityException e) {
                System.err.println("deleteRecord(" + recNo + ") failed");
                e.printStackTrace();
            } catch (RecordNotFoundException e) {
                System.err.println("deleteRecord(" + recNo + ") failed");
                e.printStackTrace();
            }
            // unlockRecord not necessary
        }
        // 6 records just deleted, 7 records will be created.
        String[] data = RecordMetaData.contractorToRecord(contractor);
        long recNo = -1;
        for (int i = 0; i < nRecordsToCreate; ++i) {
            recNo = DATA.createRecord(data);
            System.out.println("createRecord() => " + recNo);
        }
        assertTrue("recNo=" + recNo, recNo == nRecords);

        // Find all records to print.
        Contractor c = new Contractor();
        String[] criteria = RecordMetaData.contractorToRecord(c);
        Map<Long, String[]> records = DATA.findByCriteriaExactMatches(criteria);
        TestUtils.printRecords(new TreeMap<Long, String[]>(records));
        assertTrue("records.size()=" + records.size(),
                records.size() == nRecords - delRecNos.length
                        + nRecordsToCreate);
    }

    // Concurrency tests
    @Test
    public void testConcurrentUpdateRecord() {
        System.out.println("testConcurrentUpdateRecord w/ " + nThreads
                + " threads.");
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);

        try {
            long t = TestUtils.time(threadPool, nThreads, new Runnable() {
                //		@Override
                public void run() {

//		    System.out.println("Thread running: "
//			    + Thread.currentThread().getName());

                    for (long recNo = 0; recNo < nRecords; ++recNo) {
                        try {

//			    System.out.println("Before lockRecord " + recNo
//				    + ": " + Thread.currentThread().getName());

                            long cookie = DATA.lockRecord(recNo);

//			    System.out.println("After lockRecord " + recNo
//				    + ": " + Thread.currentThread().getName());

                            String[] data = DATA.readRecord(recNo);
                            Contractor c = RecordMetaData
                                    .recordToContractor(data);
                            String owner = c.getOwner();
                            int incr = (owner.equals("") ? 0 : Integer
                                    .valueOf(owner)) + 1;
                            owner = String.valueOf(incr);
                            c.setOwner(owner);
                            data = RecordMetaData.contractorToRecord(c);
                            DATA.updateRecord(recNo, data, cookie);
                            DATA.unlockRecord(recNo, cookie);
                        } catch (RecordNotFoundException e) {
                            System.err.println("Cannot lock, read, "
                                    + "or update record with recNo=" + recNo);
                            e.printStackTrace();
                        }
                    }
                }
            });
            System.out.format(Locale.US,
                    "All threads terminated after %.3f secs.%n",
                    (double) t / 1e9);
        } catch (InterruptedException e) {
            System.err.println("Framework for timing concurrent execution"
                    + " - time() - interrupted.");
            e.printStackTrace();
        }
        threadPool.shutdown();

        // Find all records with owner == nThreads.
        Contractor c = new Contractor();
        c.setOwner(String.valueOf(nThreads));
        String[] criteria = RecordMetaData.contractorToRecord(c);
        Map<Long, String[]> records = DATA.findByCriteriaExactMatches(criteria);
        TestUtils.printRecords(new TreeMap<Long, String[]>(records));
        assertTrue("records.size()=" + records.size(),
                records.size() == nRecords);
    }

    @Test
    public void testConcurrentCreateRecord() {
        System.out.println("testConcurrentCreateRecord w/ " + nThreads
                + " threads.");
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
        final int nRecordsToCreate = 10; // per thread

        try {
            long t = TestUtils.time(threadPool, nThreads, new Runnable() {
                //		@Override
                public void run() {
                    for (int i = 0; i < nRecordsToCreate; ++i) {
                        Contractor c = new Contractor();
                        c.setName(Thread.currentThread().getName());
                        c.setSize(String.format("%d", i + 1));
                        c.setOwner(String.valueOf(Thread.currentThread()
                                .getId()));
                        String[] data = RecordMetaData.contractorToRecord(c);
                        DATA.createRecord(data);
                    }
                }
            });
            System.out.format(Locale.US,
                    "All threads terminated after %.3f secs.%n",
                    (double) t / 1e9);
        } catch (InterruptedException e) {
            System.err.println("Framework for timing concurrent execution"
                    + " - time() - interrupted.");
            e.printStackTrace();
        }
        threadPool.shutdown();

        // Print all records.
        Contractor c = new Contractor();
        String[] criteria = RecordMetaData.contractorToRecord(c);
        Map<Long, String[]> records = DATA.findByCriteriaExactMatches(criteria);
        TestUtils.printRecords(new TreeMap<Long, String[]>(records));
        assertTrue("records.size()=" + records.size(),
                records.size() == nRecords + nThreads * nRecordsToCreate);
    }

    @Test
    public void testFindByCriteriaExactMatches() {
        System.out.println("testFindByCriteriaExactMatches w/ " + nThreads
                + " threads.");
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
        final AtomicInteger nThreadsSucceeded = new AtomicInteger();
        final String dogsWithToolsName = "Dogs With Tools";
        final long[] dogsWithToolsRecNos = new long[]{0, 4, 5, 8, 16, 25};
        final String lendmarchName = "Lendmarch";
        final long[] lendmarchRecNos = new long[]{25, 26, 27};
        final long[] intersectionRecNos = new long[]{25};
        try {
            long t = TestUtils.time(threadPool, nThreads, new Runnable() {
                //		@Override
                public void run() {
                    Contractor c;
                    String[] criteria;
                    Map<Long, String[]> records;
                    // All records.
                    c = new Contractor();
                    criteria = RecordMetaData.contractorToRecord(c);
                    records = DATA.findByCriteriaExactMatches(criteria);
                    assertTrue("records.size()=" + records.size(), records
                            .size() == nRecords);
                    // name="Dogs With Tools" only
                    c = new Contractor();
                    c.setName(dogsWithToolsName);
                    criteria = RecordMetaData.contractorToRecord(c);
                    records = DATA.findByCriteriaExactMatches(criteria);
                    assertTrue("records.size()=" + records.size(), records
                            .size() == dogsWithToolsRecNos.length);
                    for (long recNo : dogsWithToolsRecNos) {
                        assertTrue("recNo=" + recNo, records.containsKey(recNo));
                    }
                    // location="Lendmarch" only
                    c = new Contractor();
                    c.setLocation(lendmarchName);
                    criteria = RecordMetaData.contractorToRecord(c);
                    records = DATA.findByCriteriaExactMatches(criteria);
                    assertTrue("records.size()=" + records.size(), records
                            .size() == lendmarchRecNos.length);
                    for (long recNo : lendmarchRecNos) {
                        assertTrue("recNo=" + recNo, records.containsKey(recNo));
                    }
                    // name="Dogs With Tools" AND location="Lendmarch"
                    c = new Contractor();
                    c.setName(dogsWithToolsName);
                    c.setLocation(lendmarchName);
                    criteria = RecordMetaData.contractorToRecord(c);
                    records = DATA.findByCriteriaExactMatches(criteria);
                    assertTrue("records.size()=" + records.size(), records
                            .size() == intersectionRecNos.length);
                    for (long recNo : intersectionRecNos) {
                        assertTrue("recNo=" + recNo, records.containsKey(recNo));
                    }
                    nThreadsSucceeded.incrementAndGet();
                }
            });
            System.out.format(Locale.US,
                    "All threads terminated after %.3f secs.%n",
                    (double) t / 1e9);
        } catch (InterruptedException e) {
            System.err.println("Framework for timing concurrent execution"
                    + " - time() - interrupted.");
            e.printStackTrace();
        }
        threadPool.shutdown();
        assertTrue("nThreadsSucceeded=" + nThreadsSucceeded, nThreadsSucceeded
                .intValue() == nThreads);
    }

    @Test
    public void testFindByCriteria() {
        System.out.println("testFindByCriteria w/ " + nThreads + " threads.");
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
        final AtomicInteger nThreadsSucceeded = new AtomicInteger();
        final String dogsWithToolsName = "Dogs W";
        final long[] dogsWithToolsRecNos = new long[]{0, 4, 5, 8, 16, 25};
        final String lendmarchName = "L";
        final long[] lendmarchRecNos = new long[]{25, 26, 27};
        final long[] intersectionRecNos = new long[]{25};
        try {
            long t = TestUtils.time(threadPool, nThreads, new Runnable() {
                //		@Override
                public void run() {
                    Contractor c;
                    String[] criteria;
                    long[] recNos;
                    // All records.
                    c = new Contractor();
                    criteria = RecordMetaData.contractorToRecord(c);
                    recNos = DATA.findByCriteria(criteria);
                    assertTrue("recNos.length=" + recNos.length,
                            recNos.length == nRecords);
                    // name="Dogs With Tools" only
                    c = new Contractor();
                    c.setName(dogsWithToolsName);
                    criteria = RecordMetaData.contractorToRecord(c);
                    recNos = DATA.findByCriteria(criteria);
                    assertTrue("recNos.length=" + recNos.length,
                            recNos.length == dogsWithToolsRecNos.length);
                    Arrays.sort(recNos);
                    assertTrue(Arrays.equals(recNos, dogsWithToolsRecNos));
                    // location="Lendmarch" only
                    c = new Contractor();
                    c.setLocation(lendmarchName);
                    criteria = RecordMetaData.contractorToRecord(c);
                    recNos = DATA.findByCriteria(criteria);
                    assertTrue("recNos.length=" + recNos.length,
                            recNos.length == lendmarchRecNos.length);
                    Arrays.sort(recNos);
                    assertTrue(Arrays.equals(recNos, lendmarchRecNos));
                    // name="Dogs With Tools" AND location="Lendmarch"
                    c = new Contractor();
                    c.setName(dogsWithToolsName);
                    c.setLocation(lendmarchName);
                    criteria = RecordMetaData.contractorToRecord(c);
                    recNos = DATA.findByCriteria(criteria);
                    assertTrue("recNos.length=" + recNos.length,
                            recNos.length == intersectionRecNos.length);
                    Arrays.sort(recNos);
                    assertTrue(Arrays.equals(recNos, intersectionRecNos));

                    nThreadsSucceeded.incrementAndGet();
                }
            });
            System.out.format(Locale.US,
                    "All threads terminated after %.3f secs.%n",
                    (double) t / 1e9);
        } catch (InterruptedException e) {
            System.err.println("Framework for timing concurrent execution"
                    + " - time() - interrupted.");
            e.printStackTrace();
        }
        threadPool.shutdown();
        assertTrue("nThreadsSucceeded=" + nThreadsSucceeded, nThreadsSucceeded
                .intValue() == nThreads);
    }
}
