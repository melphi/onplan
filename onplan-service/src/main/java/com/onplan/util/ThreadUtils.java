package com.onplan.util;

/**
 * Thread utility class.
 */
public final class ThreadUtils {
  /**
   * Executes a runnable object in a new thread.
   * @param runnable The runnable object.
   */
  public static void executeAsync(final Runnable runnable) {
    new Thread(runnable).start();
  }
}
