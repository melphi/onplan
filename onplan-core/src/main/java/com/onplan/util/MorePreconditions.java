package com.onplan.util;

import com.google.common.base.Strings;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Additional preconditions.
 */
public class MorePreconditions {
  public static final String TRUE = "true";
  public static final String FALSE = "false";

  /**
   * Returns the string passed as argument, making sure that the string is not null or empty.
   *
   * @param value The string value.
   * @param errorMessage The message to include in the exception if raised.
   * @return The string value passed as argument.
   * @exception java.lang.IllegalArgumentException The string is null or empty.
   */
  public static String checkNotNullOrEmpty(final String value, final String errorMessage)
      throws IllegalArgumentException {
    if (Strings.isNullOrEmpty(value)) {
      throw new IllegalArgumentException(errorMessage);
    }
    return value;
  }

  /**
   * Returns the string passed as argument, making sure that the string is not null or empty.
   *
   * @param value The string value.
   * @return The string value passed as argument.
   * @exception java.lang.IllegalArgumentException The string is null or empty.
   */
  public static String checkNotNullOrEmpty(final String value) throws IllegalArgumentException {
    return checkNotNullOrEmpty(value, "Required value is null or empty.");
  }

  /**
   * Returns the boolean translation of the string, making sure that the string has
   * a correct value (true or false).
   *
   * @param value The string which represents a boolean value (true or false).
   * @return The boolean translation of the string.
   * @exception java.lang.IllegalArgumentException The string can not be converted to a boolean.
   */
  public static boolean checkBoolean(final String value) throws IllegalArgumentException{
    checkArgument(TRUE.equalsIgnoreCase(value) || FALSE.equalsIgnoreCase(value),
        String.format("Expected a boolean value of [%s] or [%s].", TRUE, FALSE));
    return Boolean.getBoolean(value);
  }
}
