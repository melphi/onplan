package com.onplan.domain;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public enum PriceBarTimeFrame {
  TICK(0),
  MINUTES_1(60 * 1000),
  MINUTES_5(60 * 5 * 1000),
  MINUTES_15(60 * 15 * 1000),
  HOURS_1(60 * 60 * 1000),
  HOURS_4(60 * 60 * 4 * 1000),
  DAYS_1(60 * 60 * 24 * 1000);

  private final int milliseconds;

  public int getIntervalMilliseconds() {
    return milliseconds;
  }

  private PriceBarTimeFrame(int milliseconds) {
    this.milliseconds = milliseconds;
  }

  public static PriceBarTimeFrame parseString(String timeFrame) {
    switch (checkNotNullOrEmpty(timeFrame)) {
      case "TICK":
        return TICK;
      case "MINUTES_1":
        return MINUTES_1;
      case "MINUTES_5":
        return MINUTES_5;
      case "MINUTES_15":
        return MINUTES_15;
      case "HOURS_1":
        return HOURS_1;
      case "HOURS_4":
        return HOURS_4;
      case "DAYS_1":
        return DAYS_1;
      default:
        throw new IllegalArgumentException(
            String.format("No matching value found for [%s].", timeFrame));
    }
  }
}
