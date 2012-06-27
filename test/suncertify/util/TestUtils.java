package suncertify.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import suncertify.db.DBFileAccess;

/**
 * A utility class.
 *
 * @author kuettner
 */
public final class TestUtils {
    // / Location of the original database file which remains unchanged.
    private final static String origDatabaseLocation = "db-2x2.db";
    // / Location of the database file for testing purposes.
    private final static String testDatabaseLocation = "test-db-2x2.db";

    // Disallow instantiation of a utility class.
    private TestUtils() {
    }

    public static String provideTestDB() throws IOException {
        try {
            copyFile(new File(origDatabaseLocation), new File(
                    testDatabaseLocation));
        } catch (IOException e) {
            System.err.println("Can't copy \"" + origDatabaseLocation
                    + "\" to \"" + testDatabaseLocation + "\".");
            throw e;
        }
        return testDatabaseLocation;
    }

    public static void deleteTestDB() {
        assertTrue(new File(testDatabaseLocation).delete());
    }

    public static void copyFile(File in, File out) throws IOException {
        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    /**
     * Simple framework for timing concurrent execution
     * <p/>
     * From: Joshua Bloch. Effective Java 2nd ed., p. 275.
     *
     * @param executor
     * @param concurrency
     * @param action
     * @return
     * @throws InterruptedException
     */
    public static long time(Executor executor, int concurrency,
                            final Runnable action) throws InterruptedException {
        final CountDownLatch ready = new CountDownLatch(concurrency);
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch done = new CountDownLatch(concurrency);
        for (int i = 0; i < concurrency; i++) {
            executor.execute(new Runnable() {
                public void run() {
                    ready.countDown(); // Tell timer we're ready
                    try {
                        start.await(); // Wait till peers are ready
                        action.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        done.countDown(); // Tell timer we're done
                    }
                }
            });
        }
        ready.await(); // Wait for all workers to be ready
        long startNanos = System.nanoTime();
        start.countDown(); // And they're off!
        done.await(); // Wait for all workers to finish
        return System.nanoTime() - startNanos;
    }

    public static void printRecords(TreeMap<Long, String[]> records) {
        NavigableSet<Long> keySet = records.navigableKeySet();
        for (long recNo : keySet) {
            String[] data = records.get(recNo);
            System.out.print("[" + recNo + "]");
            for (String s : data) {
                System.out.print(" \"" + s + "\"");
            }
            System.out.println();
        }
    }

    public static TreeMap<Long, String[]> readRecords(DBFileAccess dbfa) {
        TreeMap<Long, String[]> records = new TreeMap<Long, String[]>();
        Iterator<Long> it = dbfa.iterator();
        while (it.hasNext()) {
            long recNo = it.next();
            String[] data = null;
            try {
                data = dbfa.readRecord(recNo);
            } catch (Exception e) {
                System.err.println("readRecord(" + recNo + ") failed");
                e.printStackTrace();
            }
            records.put(recNo, data);
        }
        return records;
    }

    public static void printRecords(DBFileAccess dbfa) {
        Iterator<Long> it = dbfa.iterator();
        while (it.hasNext()) {
            long recNo = it.next();
            String[] data = null;
            try {
                data = dbfa.readRecord(recNo);
            } catch (Exception e) {
                System.err.println("readRecord failed");
                e.printStackTrace();
            }
            System.out.print("[" + recNo + "]");
            for (String s : data) {
                System.out.print(" \"" + s + "\"");
            }
            System.out.println();
        }
    }

}
