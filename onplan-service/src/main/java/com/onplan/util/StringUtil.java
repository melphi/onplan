package com.onplan.util;

import com.google.common.base.Strings;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class StringUtil {
  public static String trimMessage(String message, int maxLength, String trimmedSuffix) {
    checkNotNullOrEmpty(trimmedSuffix);
    if (Strings.isNullOrEmpty(message)) {
      return message;
    } else if (message.length() > maxLength) {
      message = message.substring(maxLength - trimmedSuffix.length());
      message += trimmedSuffix;
    }
    return message;
  }
}
