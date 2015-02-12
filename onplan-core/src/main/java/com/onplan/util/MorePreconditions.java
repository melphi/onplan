package com.onplan.util;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;

public class MorePreconditions {
  public static final String TRUE = "true";
  public static final String FALSE = "false";

  public static String checkNotNullOrEmpty(final String value, final String errorMessage) {
    if (Strings.isNullOrEmpty(value)) {
      throw new IllegalArgumentException(errorMessage);
    }
    return value;
  }

  public static String checkNotNullOrEmpty(final String value) {
    return checkNotNullOrEmpty(value, "Required value is null or empty.");
  }

  public static String checkBoolean(final String value) {
    checkArgument(TRUE.equalsIgnoreCase(value) || FALSE.equalsIgnoreCase(value),
        String.format("Expected a boolean value of [%s] or [%s].", TRUE, FALSE));
    return value;
  }
}
