package com.onplan.util;

import java.io.*;
import java.net.URL;
import java.util.Properties;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class PropertiesUtils {
  private static final FileFilter PROPERTIES_FILE_FILTER = new FileFilter() {
    @Override
    public boolean accept(File file) {
      return file.getName().endsWith(".properties");
    }
  };

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
   * Loads all the property files found in the class path and merges the result in a single
   * {@link java.util.Properties} object. In case of duplicated keys raises an exception.
   *
   * @throws Exception Error while loading the properties file or files contain duplicated keys.
   */
  public static Properties loadAllPropertiesFromClassPath()
      throws Exception {
    Properties result = new Properties();
    URL baseUrl = checkNotNull(ClassLoader.getSystemClassLoader().getResource("."));
    File[] files = new File(baseUrl.getFile()).listFiles(PROPERTIES_FILE_FILTER);
    for (File file : files) {
      InputStream fileStream = new FileInputStream(file);
      Properties newProperties = new Properties();
      newProperties.load(fileStream);
      fileStream.close();
      checkNotIntersects(newProperties.keySet(), result.keySet());
      result.putAll(newProperties);
    }
    return result;
  }

  private static Properties loadPropertiesFromFile(File file) throws IOException {
    checkNotNull(file);
    checkArgument(file.exists(), "File [%s] not found.", file.getName());
    InputStream inputStream = new FileInputStream(file);
    Properties properties = new Properties();
    properties.load(inputStream);
    inputStream.close();
    return properties;
  }

  private static void checkNotIntersects(Set set1, Set set2) {
    checkNotNull(set1);
    checkNotNull(set2);
    for (Object object : set1) {
      if (set2.contains(object)) {
        throw new IllegalArgumentException(String.format("Element [%s] is duplicated.", object));
      }
    }
  }
}
