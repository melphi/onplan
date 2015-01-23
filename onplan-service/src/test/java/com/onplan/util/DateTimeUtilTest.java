package com.onplan.util;

import com.onplan.domain.PriceBarTimeFrame;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import static com.onplan.util.PriceBarUtil.getCurrentBarCloseTimestamp;
import static com.onplan.util.PriceBarUtil.getCurrentBarOpenTimestamp;
import static org.junit.Assert.assertEquals;

public class DateTimeUtilTest {
  private static final DateTime EXAMPLE_DATE =
      DateTime.parse("2001-02-02 08.00.00", DateTimeFormat.forPattern("yyyy-MM-dd hh.mm.ss"));

  @Test
  public void testGetCurrentBarOpenTimestamp15m() {
    long openTimestamp = getCurrentBarOpenTimestamp(
        EXAMPLE_DATE.toDate().getTime(), PriceBarTimeFrame.MINUTES_15);
    assertEquals(EXAMPLE_DATE.getMillis(), openTimestamp);
    openTimestamp = getCurrentBarOpenTimestamp(
        EXAMPLE_DATE.plusMinutes(14).plusSeconds(10).getMillis(),
        PriceBarTimeFrame.MINUTES_15);
    assertEquals(EXAMPLE_DATE.getMillis(), openTimestamp);
  }

  @Test
  public void testGetCurrentBarCloseTimestamp15m() {
    long closeTimestamp = getCurrentBarCloseTimestamp(
        EXAMPLE_DATE.minusMinutes(14).minusSeconds(10).minusMillis(100).toDate().getTime(),
        PriceBarTimeFrame.MINUTES_15);
    assertEquals(EXAMPLE_DATE.minusMillis(1).getMillis(), closeTimestamp);
    closeTimestamp = getCurrentBarCloseTimestamp(
        EXAMPLE_DATE.getMillis(), PriceBarTimeFrame.MINUTES_15);
    assertEquals(EXAMPLE_DATE.plusMinutes(15).minusMillis(1).getMillis(), closeTimestamp);
  }

  @Test
  public void testGetCurrentBarOpenTimestamp1m() {
    long openTimestamp = getCurrentBarOpenTimestamp(
        EXAMPLE_DATE.toDate().getTime(), PriceBarTimeFrame.MINUTES_1);
    assertEquals(EXAMPLE_DATE.getMillis(), openTimestamp);
    openTimestamp = getCurrentBarOpenTimestamp(
        EXAMPLE_DATE.plusSeconds(59).plusMillis(1).getMillis(),
        PriceBarTimeFrame.MINUTES_1);
    assertEquals(EXAMPLE_DATE.getMillis(), openTimestamp);
  }

  @Test
  public void testGetCurrentBarCloseTimestamp1() {
    long closeTimestamp = getCurrentBarCloseTimestamp(
        EXAMPLE_DATE.minusSeconds(50).minusMillis(1).toDate().getTime(),
        PriceBarTimeFrame.MINUTES_1);
    assertEquals(EXAMPLE_DATE.minusMillis(1).getMillis(), closeTimestamp);
    closeTimestamp = getCurrentBarCloseTimestamp(
        EXAMPLE_DATE.getMillis(), PriceBarTimeFrame.MINUTES_1);
    assertEquals(EXAMPLE_DATE.plusMinutes(1).minusMillis(1).getMillis(), closeTimestamp);
  }
}
