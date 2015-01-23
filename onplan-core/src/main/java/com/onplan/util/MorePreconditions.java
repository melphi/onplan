package com.onplan.util;

import com.google.common.base.Strings;

public class MorePreconditions {
  public static String checkNotNullOrEmpty(final String value) {
    if (Strings.isNullOrEmpty(value)) {
      throw new IllegalArgumentException();
    }
    return value;
  }
}
