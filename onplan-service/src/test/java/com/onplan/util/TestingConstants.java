package com.onplan.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Map;

public class TestingConstants {
  private static final DateTimeFormatter DATE_TIME_FORMAT =
      DateTimeFormat.forPattern("yyyy-MM-dd hh:mm");

  public static final DateTime DEFAULT_CREATION_DATE =
      DateTime.parse("2012-11-06 08:00", DATE_TIME_FORMAT);
  public static final DateTime DEFAULT_START_DATE =
      DateTime.parse("2014-05-08 08:00", DATE_TIME_FORMAT);
  public static final DateTime DEFAULT_END_DATE =
      DateTime.parse("2014-05-08 08:15", DATE_TIME_FORMAT);
  public static final long DEFAULT_SPREAD = 1;
  public static final String DEFAULT_STRATEGY_ID = "2j8Jl20jHJ";
  public static final String DEFAULT_ALERT_ID = "s2lwer09HJ";
  public static final Map<String, String> DEFAULT_ADVISER_PARAMETERS =
      ImmutableMap.of("key1", "value1", "key2", "value2");
  public static final String DEFAULT_ALERT_MESSAGE = "Sample alert message.";

  public static final String INSTRUMENT_EURUSD_ID = "CS.EURUSD.TODAY";
  public static final String INSTRUMENT_AUDUSD_ID = "CS.AUDUSD.TODAY";
  public static final String INSTRUMENT_DAX_ID = "IX.DAX.DAILY";
  public static final String INSTRUMENT_FTSE100_ID = "IX.FTSE.DAILY";
  public static final List<String> INSTRUMENT_IDS = ImmutableList.of(
      INSTRUMENT_EURUSD_ID,
      INSTRUMENT_AUDUSD_ID,
      INSTRUMENT_DAX_ID,
      INSTRUMENT_FTSE100_ID);

  public static final int INITIAL_ALERTS_LIST_SIZE = 7;
  public static final int INITIAL_PREDICATES_LIST_SIZE = 5;
  public static final int INITIAL_STRATEGIES_LIST_SIZE = 4;

  public static final double PRICE_VALUE_1 = 1.00223423;
}
