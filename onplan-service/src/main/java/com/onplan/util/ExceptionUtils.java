package com.onplan.util;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ExceptionUtils {
  public static String stackTraceToString(final Exception e) {
    checkNotNull(e);
    StringBuilder result = new StringBuilder();
    StackTraceElement[] stackTraceElements = e.getStackTrace();
    for (StackTraceElement stackTraceElement : stackTraceElements) {
      result.append(stackTraceElement.toString()).append('\n');
    }
    return result.toString();
  }
}
