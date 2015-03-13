package com.onplan.util;

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class PropertiesUtils {
  private static final String PROPERTIES_FILE_EXTENSION = "properties";

  /**
   * Loads a property file from the class path as base path and returns the
   * {@link java.util.Properties} object.
   *
   * @param propertiesFile The property file.
   * @throws IOException Error while loading the properties file.
   */
  public static Properties loadPropertiesFromFile(String propertiesFile)
      throws Exception {
    checkNotNullOrEmpty(propertiesFile);
    URL fileUrl = PropertiesUtils.class.getClassLoader().getResource(propertiesFile);
    checkNotNull(fileUrl, String.format("File [%s] not found.", propertiesFile));
    return loadPropertiesFromFile(new File(fileUrl.getFile()));
  }

  /**
   * Loads all the property files found in the class path and returns the collection of
   * {@link java.util.Properties} object.
   *
   * @throws IOException Error while loading the properties file.
   */
  public static Collection<Properties> loadAllPropertiesFromClassPath()
      throws Exception {
    ImmutableList.Builder<Properties> result = ImmutableList.builder();
    URL baseUrl = PropertiesUtils.class.getClassLoader().getResource(".");
    checkNotNull(baseUrl);
    Collection<File> files = FileUtils.listFiles(
        new File(baseUrl.getFile()), new String[]{PROPERTIES_FILE_EXTENSION}, false);
    for (File file : files) {
      result.add(loadPropertiesFromFile(file));
    }
    return result.build();
  }

  private static Properties loadPropertiesFromFile(File file) throws IOException {
    checkNotNull(file);
    checkArgument(file.exists(), "File [%s] not found.", file.getName());
    InputStream inputStream = new FileInputStream(file);
    Properties properties = new Properties();
    properties.load(inputStream);
    return properties;
  }
}
