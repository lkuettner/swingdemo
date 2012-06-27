/* 
 * @(#)PersistentConfiguration    1.0 21/06/2010 
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

package suncertify.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides read/write access to the persistent state of the configuration
 * parameters.
 *
 * @author Lars Kuettner
 * @version 1.0
 */
public final class PersistentConfiguration
{

  /**
   * Logger object to log messages in the scope of this class.
   */
  private static final Logger LOG =
    Logger.getLogger(PersistentConfiguration.class.getName());

  /**
   * Key in Properties indicating that the value will be the database
   * location.
   */
  public static final String DATABASE_LOCATION = "DatabaseLocation";

  /**
   * Key in Properties indicating that the value will be the RMI registry
   * server address.
   */
  public static final String SERVER_ADDRESS = "ServerAddress";

  /**
   * Key in Properties indicating that the value will be the port of the RMI
   * registry.
   */
  public static final String SERVER_PORT = "ServerPort";

  /**
   * The default database file name.
   */
  private static final String DEFAULT_DATABASE_LOCATION = "db-2x2.db";
  /**
   * The default server address.
   */
  private static final String DEFAULT_SERVER_ADDRESS = "localhost";
  /**
   * The default server port number.
   */
  private static final String DEFAULT_SERVER_PORT = ""
    + java.rmi.registry.Registry.REGISTRY_PORT;

  /**
   * The Properties for this application.
   */
  private Properties parameters = null;

  /**
   * The name of the directory where to locate the configuration file.
   */
  private static final String BASE_DIRECTORY = ".";

  /**
   * The name of the configuration file.
   */
  private static final String PROPERTIES_FILENAME = "suncertify.properties";

  /**
   * The file containing the saved configuration.
   */
  private static File propertiesFile = new File(BASE_DIRECTORY,
    PROPERTIES_FILENAME);

  /**
   * The Singleton version of the persistent configuration.
   */
  private static PersistentConfiguration persistentConfiguration =
    new PersistentConfiguration();

  /**
   * Creates a new instance of PersistentConfiguration. As there should only
   * ever be a single instance, the constructor has been made private.
   */
  private PersistentConfiguration()
  {
    parameters = loadParametersFromFile();

    if (parameters == null)
    {
      parameters = new Properties();
      parameters
        .setProperty(DATABASE_LOCATION, DEFAULT_DATABASE_LOCATION);
      parameters.setProperty(SERVER_ADDRESS, DEFAULT_SERVER_ADDRESS);
      parameters.setProperty(SERVER_PORT, DEFAULT_SERVER_PORT);
    }
  }

  /**
   * Returns the Singleton instance of the
   * <code>PersitentConfiguration</code>.
   *
   * @return the Singleton instance of the <code>PersitentConfiguration</code>
   */
  public static PersistentConfiguration getPersistentConfiguration()
  {
    return persistentConfiguration;
  }

  /**
   * Returns the value of the named parameter.
   *
   * @param parameterName the name of the parameter for which the value is requested
   * @return the value of the named parameter
   */
  public synchronized String getParameter(final String parameterName)
  {
    return parameters.getProperty(parameterName);
  }

  /**
   * Updates the saved parameter with the new value and saves all the
   * parameters immediately. Doing it this way, though somewhat inefficient,
   * guarantees that the user of this class need not be concerned about
   * explicitly saving the parameters.
   *
   * @param parameterName  the name of the parameter
   * @param parameterValue the value to be stored for the parameter
   */
  public synchronized void setParameter(final String parameterName,
                                        final String parameterValue)
  {
    parameters.setProperty(parameterName, parameterValue);
    saveParametersToFile();
  }

  /**
   * Saves the parameters to the configuration file from which they will be
   * loaded the next time the application starts.
   */
  private void saveParametersToFile()
  {
    try
    {
      synchronized (propertiesFile)
      {
        if (propertiesFile.exists())
        {
          propertiesFile.delete();
        }
        propertiesFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(propertiesFile);
        parameters.store(fos, "suncertify properties");
        fos.close();
      }
    }
    catch (IOException e)
    {
      LOG.log(Level.WARNING, "Unable to save user parameters to file: "
        + e.getMessage()
        + " (They won't be remembered next time you start.)", e);
    }
  }

  /**
   * Attempts to load the saved parameters from the configuration file.
   *
   * @return <code>Properties</code> loaded from file, or null with a warning
   *         message written to the log
   */
  private Properties loadParametersFromFile()
  {
    Properties loadedProperties = null;

    if (propertiesFile.exists() && propertiesFile.canRead())
    {
      synchronized (propertiesFile)
      {
        try
        {
          FileInputStream fis = new FileInputStream(propertiesFile);
          loadedProperties = new Properties();
          loadedProperties.load(fis);
          fis.close();
        }
        catch (FileNotFoundException e)
        {
          LOG.log(Level.WARNING,
            "File not found after existence verified.\n"
              + "Unable to load user parameters: "
              + e.getMessage()
              + "(Default values will be used.)", e);
        }
        catch (IOException e)
        {
          LOG.log(Level.WARNING,
            "Bad data in configuration properties file.\n"
              + "Unable to load user parameters: "
              + e.getMessage()
              + "(Default values will be used.)", e);
        }
      }
    }
    return loadedProperties;
  }
}