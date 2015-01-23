package com.onplan.util;

import com.onplan.domain.PriceBarTimeFrame;

public class PriceBarUtil {
  public static double getPricePips(final double price, final int minimalDecimalPosition) {
    return price * Math.pow(10, minimalDecimalPosition);
  }

  public static long getCurrentBarOpenTimestamp(
      long currentTimestamp, PriceBarTimeFrame priceBarTimeFrame) {
    long intervalMilliseconds = priceBarTimeFrame.getIntervalMilliseconds();
    switch (priceBarTimeFrame) {
      case MINUTES_1:
      case MINUTES_15:
        return intervalMilliseconds * Math.floorDiv(currentTimestamp, intervalMilliseconds);
      default:
        throw new IllegalArgumentException(
            String.format("TimeFrame not supported [%s].", priceBarTimeFrame));
    }
  }

  public static long getCurrentBarCloseTimestamp(
      long currentTimestamp, PriceBarTimeFrame priceBarTimeFrame) {
    long intervalMilliseconds = priceBarTimeFrame.getIntervalMilliseconds();
    switch (priceBarTimeFrame) {
      case MINUTES_1:
      case MINUTES_15:
        long multiplier = Math.floorDiv(currentTimestamp, intervalMilliseconds) + 1;
        return (intervalMilliseconds * multiplier) - 1;
      default:
        throw new IllegalArgumentException(
            String.format("TimeFrame not supported [%s].", priceBarTimeFrame));
    }
  }

  public static long getNextBarOpenTimestamp(
      long currentTimestamp, PriceBarTimeFrame priceBarTimeFrame) {
    return getCurrentBarCloseTimestamp(currentTimestamp, priceBarTimeFrame) + 1;
  }
}
