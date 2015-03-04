package com.onplan.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public final class JsonUtils {
  private static final ObjectWriter objectWriter;
  private static final ObjectReader objectReader;

  static {
    ObjectMapper objectMapper = new ObjectMapper();
    objectWriter = objectMapper.writer();
    objectReader = objectMapper.reader();
  }

  // TODO(robertom): Check null values.
  public static String createJson(final Object object) throws Exception {
    return objectWriter.writeValueAsString(object);
  }

  // TODO(robertom): Check null values.
  public static <T> T createObject(final String json) throws Exception {
    return objectReader.readValue(json);
  }
}
