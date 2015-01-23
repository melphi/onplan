package com.onplan.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class TestingConstants {
  public static final DateTime DEFAULT_START_DATE =
      DateTime.parse("2010-05-05 08:00", DateTimeFormat.forPattern("yyyy-MM-dd hh:mm"));
  public static final String INSTRUMENT_EURUSD_ID = "EURUSD";
  public static final String INSTRUMENT_AUDUSD_ID = "AUDUSD";
  public static final String INSTRUMENT_DAX_ID = "DAX";
  public static final String INSTRUMENT_FTSE100_ID = "FTSE100";
  public static final long DEFAULT_SPREAD = 1;
}
