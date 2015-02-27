package com.onplan.util;

import com.google.common.base.Strings;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class StringUtil {
  /**
   * When the text exceeds the maxLength returns the text trimmed with the trimmedSuffix, otherwise
   * returns the full text. The resulting string always contains up to maxLength characters.
   *
   * @param text The text string.
   * @param maxLength The maximum length of the resulting string.
   * @param trimmedSuffix The trimmed suffix.
   */
  public static String trimText(String text, int maxLength, String trimmedSuffix) {
    checkNotNullOrEmpty(trimmedSuffix);
    if (Strings.isNullOrEmpty(text)) {
      return text;
    } else if (text.length() > maxLength) {
      text = text.substring(maxLength - trimmedSuffix.length());
      text += trimmedSuffix;
    }
    return text;
  }
}
