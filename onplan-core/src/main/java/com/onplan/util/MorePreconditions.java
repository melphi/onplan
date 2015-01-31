package com.onplan.util;

import com.google.common.base.Strings;

public class MorePreconditions {
  public static String checkNotNullOrEmpty(final String value, final String errorMessage) {
    if (Strings.isNullOrEmpty(value)) {
      throw new IllegalArgumentException(errorMessage);
    }
    return value;
  }

  public static String checkNotNullOrEmpty(final String value) {
    return checkNotNullOrEmpty(value, "Required value is null or empty.");
  }
}
