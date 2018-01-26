package de.uniulm.omi.cloudiator.visor.bash;

import de.uniulm.omi.cloudiator.visor.exceptions.MeasurementNotAvailableException;
import de.uniulm.omi.cloudiator.visor.exceptions.SensorInitializationException;
import de.uniulm.omi.cloudiator.visor.monitoring.AbstractSensor;
import de.uniulm.omi.cloudiator.visor.monitoring.Measurement;
import de.uniulm.omi.cloudiator.visor.monitoring.MonitorContext;
import de.uniulm.omi.cloudiator.visor.monitoring.SensorConfiguration;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A probe for calling bash scripts that do the actual measuring. TODO: this class calls Process
 * very often. It assumes that the output of this external process is not huge. In case it is, the
 * system will block.
 */
public class BashSensorWrapper extends AbstractSensor<String> {

  /**
   * do we need to make this configurable?
   */
  private static final String DEFAULT_SENSOR_SOURCE = "/opt/sensors/";

  private static final String FILE_RETRIEVAL_SOURCE_KEY = "sensor.bash.get.where";
  private static final String FILE_RETRIEVAL_TYPE_KEY = "sensor.bash.get.how";
  /**
   * file can be downloaded from URI
   */
  private static final String FILE_RETRIEVAL_TYPE_DOWNLOAD = "download";
  /**
   * file is passed as argument in based64 encoding
   */
  private static final String FILE_RETRIEVAL_TYPE_ARGUMENT = "base64argument";
  /**
   * file is available on that machine
   */
  private static final String FILE_RETRIEVAL_TYPE_INSTALLED = "installed";

  private final String sensorPrefix;
  private final Set<URI> httpAddresses;
  private volatile File scriptFile;

  public BashSensorWrapper() {
    // TODO: make this configurable //
    sensorPrefix = DEFAULT_SENSOR_SOURCE;

    httpAddresses = new HashSet<>();
    // TODO: make this configurable //
    httpAddresses.add(URI.create("https://github.com/cactos/"));
    httpAddresses.add(URI.create("https://github.com/cloudiator/visor"));
    httpAddresses.add(URI.create("https://omi-gitlab.e-technik.uni-ulm.de/"));
  }

  private static int getRealPortNr(URI uri) {
    int port = uri.getPort();
    if (port != -1) {
      return port;
    }
    String schema = uri.getScheme();
    if (schema == null) {
      return port;
    }
    if ("http".equals(schema)) {
      return 80;
    }
    if ("https".equals(schema)) {
      return 443;
    }
    return port;
  }

  private static boolean isWindows(String os) {
    return (os.indexOf("win") >= 0);
  }

  private static boolean isMac(String os) {
    return (os.indexOf("mac") >= 0);
  }

  private static boolean isUnix(String os) {
    return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0);
  }

  private static boolean isSolaris(String os) {
    return (os.indexOf("sunos") >= 0);
  }

  private static boolean isLinux(String osName) throws SensorInitializationException {
    if (osName == null || isMac(osName) || isWindows(osName) || isSolaris(osName)) {
      return false;
    }
    if (!isUnix(osName)) {
      throw new SensorInitializationException("found unsupported OS, running Mythos?: " + osName);
    }

    // String osArch = System.getProperty("os.arch");
    // String osVersion = System.getProperty("os.version");

    return true;
  }

  @Override
  protected void initialize(MonitorContext monitorContext,
      SensorConfiguration sensorConfiguration) throws SensorInitializationException {

    String osName = System.getProperty("os.name");
    if (!isLinux(osName)) {
      throw new SensorInitializationException(
          "BashSensorWrapper only available for Linux based systems, but found " + osName);
    }
    super.initialize(monitorContext, sensorConfiguration);
    checkForSensorDir();
    scriptFile = provideFile(sensorConfiguration).getAbsoluteFile();
  }

  @Override
  protected Measurement<String> measureSingle() throws MeasurementNotAvailableException {
    try {
      Process p = Runtime.getRuntime().exec(scriptFile.getAbsolutePath());
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      InputStream in = p.getInputStream();

      int j = -2;
      while (j != -1) {
        j = in.read();
        out.write(j);
      }

      int i = p.waitFor();
      if (i == 0) {
        byte[] b = out.toByteArray();
        return measurementBuilder(String.class).now()
            .value(new String(b, 0, b.length, Charset.defaultCharset())).build();
      }
      throw new MeasurementNotAvailableException("measurement failed; exit code is " + i);
    } catch (InterruptedException | IOException ex) {
      throw new MeasurementNotAvailableException(ex);
    }
  }

  private String getSourceValue(SensorConfiguration sensorConfiguration)
      throws SensorInitializationException {
    Optional<String> type = sensorConfiguration.getValue(FILE_RETRIEVAL_SOURCE_KEY);
    if (type.isPresent()) {
      return type.get();
    }
    throw new SensorInitializationException(
        "file retrieval source (" + FILE_RETRIEVAL_SOURCE_KEY + ") not set.");
  }

  private File provideFile(SensorConfiguration sensorConfiguration)
      throws SensorInitializationException {
    Optional<String> type = sensorConfiguration.getValue(FILE_RETRIEVAL_TYPE_KEY);
    if (type.isPresent()) {
      String value = getSourceValue(sensorConfiguration);
      if (FILE_RETRIEVAL_TYPE_DOWNLOAD.equals(type.get())) {
        return downloadFromUrl(value);
      } else if (FILE_RETRIEVAL_TYPE_ARGUMENT.equals(type.get())) {
        fileAsParameter(value);
      } else if (FILE_RETRIEVAL_TYPE_INSTALLED.equals(type.get())) {
        return checkForLocalDir(value);
      }
    }
    throw new SensorInitializationException(
        "file retrieval type (" + FILE_RETRIEVAL_TYPE_KEY + ") not set or set to unknown value: "
            + type);
  }

  private void checkForSensorDir() throws SensorInitializationException {
    File f = new File(sensorPrefix);
    if (!f.exists() || !f.isDirectory()) {
      throw new SensorInitializationException("directory " + sensorPrefix + " not found.");
    }
    if (f.canWrite() && f.canRead() && f.canExecute()) {
      return;
    }
    throw new SensorInitializationException("unsufficient wrx priviledges.");
  }

  private File checkForLocalDir(String source) throws SensorInitializationException {
    if (source == null || source.isEmpty()) {
      throw new SensorInitializationException("filename not set. null or empty.");
    }
    String fullSource = sensorPrefix + source;
    File f = new File(fullSource);
    if (f.exists() && !f.isDirectory() && f.canExecute()) {
      return f;
    }
    throw new SensorInitializationException(
        "file " + f + " either not found, or not an executable, or not a file.");
  }

  private boolean uriAllowed(URI uri) throws SensorInitializationException {
    final String host = uri.getHost();
    final String path = uri.getPath();
    final int port = getRealPortNr(uri);
    for (URI u : httpAddresses) {
      final String uHost = u.getHost();
      final String uPath = u.getPath();
      final int uPort = getRealPortNr(u);
      if (uHost.equals(host) && uPort == port) {
        if (uPath == null || uPath.isEmpty()) {
          throw new SensorInitializationException("path component not set: " + uri);
        }
        if (uPath.startsWith(path)) {
          return true;
        }
      }
    }
    return false;
  }

  private File downloadFromUrl(String source) throws SensorInitializationException {
    if (source == null || source.isEmpty()) {
      throw new SensorInitializationException("filename not set. null or empty.");
    }
    URI uri = URI.create(source);
    if (!uri.isAbsolute() || !"http".equals(uri.getScheme())) {
      throw new SensorInitializationException("wrong path. not an http address: " + source);
    }

    if (!uriAllowed(uri)) {
      throw new SensorInitializationException(
          "download uri not contained in ALLOWED set. " + httpAddresses);
    }

    File sensorDir = new File(sensorPrefix);
    try {
      File f = File.createTempFile("sensor", ".sh", sensorDir);
      doDownloadFile(uri, f);
      f.setExecutable(true);
      return f;
    } catch (IOException ioe) {
      throw new SensorInitializationException("could not create file for downloading", ioe);
    }
  }

  private boolean commandAvailable(String command) throws IOException {
    int i = -2;
    try {
      Process p = Runtime.getRuntime().exec("which command");
      i = p.waitFor();
    } catch (InterruptedException ex) {
      throw new IOException(ex);
    }
    if (i == 0) {
      return true;
    }
    if (i == 1) {
      return false;
    }
    // if(i == 2)
    throw new IOException("could not execute 'which' properly: " + i);
  }

  private void doDownloadFile(URI uri, File f) throws SensorInitializationException {
    try {
      String command = null;
      Process p = null;
      if (commandAvailable("wget")) {
        command = "wget";
        p = Runtime.getRuntime()
            .exec("wget -o /dev/null -O " + f.getAbsolutePath() + " " + uri.toString());
      } else if (commandAvailable("curl")) {
        command = "curl";
        p = Runtime.getRuntime()
            .exec("curl -o " + f.getAbsolutePath() + " " + uri.toString() + " >/dev/null");
      } else {
        throw new SensorInitializationException(
            "cannot download file. neither wget nor curl was found.");
      }
      int i = p.waitFor();
      if (i != 0) {
        throw new SensorInitializationException(
            "downloading with '" + command + "' failed. exit status: " + i);
      }
    } catch (IOException | InterruptedException ex) {
      throw new SensorInitializationException("downloading failed", ex);
    }
  }

  private File fileAsParameter(String source) throws SensorInitializationException {
    throw new SensorInitializationException(
        "passing file as parameter currently not supported due to security concerns.");
  }
}
