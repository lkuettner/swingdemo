/**
 *
 */
package suncertify.rmi;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import suncertify.services.BusinessServices;
import suncertify.services.Contractor;
import suncertify.services.ServicesException;
import suncertify.util.TestUtils;

/**
 * @author kuettner
 */
public class RMITest {

    String databaseLocation;
    // # of records originally in database
    // TODO migrate to TestUtils
    private final long nRecords = 28;

    private final int nThreads = 42;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        databaseLocation = TestUtils.provideTestDB();

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        // TestUtils.deleteTestDB();
    }

    @Test
    public void testConnection() {
        final String hostname = "localhost";
        // final int rmiPort = Registry.REGISTRY_PORT;
        final String rmiPort = "4711";

        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);

        // Register ContractorManager (to be called by server).
        System.out.println("Register contractor manager on port #" + rmiPort);
        try {
            ServerUtilities.startServer(databaseLocation, rmiPort);
        } catch (LaunchServerException e) {
            System.err.println("Can't register contractor manager on port #"
                    + rmiPort);
            assert (false);
        }
        System.out.println("Registry created on port #" + rmiPort
                + " and contractor manager successfully registered by server");

        // client-side
        try {
            long t = TestUtils.time(threadPool, nThreads, new Runnable() {
                @Override
                public void run() {
                    // Get remote BusinessServices interface stub (to be called
                    // by clients).
                    System.out.println("[client-side] "
                            + "Get remote BusinessServices interface "
                            + "stub from hostname=" + hostname + ", port="
                            + rmiPort);
                    BusinessServices services = null;
                    try {
                        services = ContractorManagerConnector.getRemote(
                                hostname, rmiPort);
                    } catch (RemoteException re) {
                        System.err.println("Can't get remote BusinessServices "
                                + "stub from" + " hostname=" + hostname
                                + ", port=" + rmiPort);
                        assert (false);
                    }
                    assertNotNull(services);
                    System.out.println("[client-side] BusinessServices stub "
                            + "available");

                    String name = null;
                    String location = null;
                    Map<Long, Contractor> contractors = null;
                    System.out.println("[client-side] "
                            + "search database for all records");
                    try {
                        contractors = services.search(name, location);
                    } catch (RemoteException e) {
                        System.err.println("services.search throws "
                                + "remote exception: " + e.getMessage());
                    } catch (ServicesException e) {
                        System.err.println("services.search throws "
                                + "services exception: " + e.getMessage());
                    }
                    assertNotNull(contractors);
                    System.out.println("[client-side] " + contractors.size()
                            + " records found");
                    assertTrue("contractors.size()=" + contractors.size(),
                            contractors.size() == nRecords);
                }
            });
            System.out.format(Locale.US,
                    "All client threads terminated after %.3f secs.%n",
                    (double) t / 1e9);
        } catch (InterruptedException e) {
            System.err.println("Framework for timing concurrent execution"
                    + " - time() - interrupted.");
            e.printStackTrace();
        }
        threadPool.shutdown();

        // server-side
        System.out.println("Terminate contractor manager");
        ServerUtilities.terminate();
    }

}
