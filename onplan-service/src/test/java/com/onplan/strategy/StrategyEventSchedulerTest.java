package com.onplan.strategy;

import com.onplan.domain.configuration.SchedulerConfiguration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class StrategyEventSchedulerTest {
  private static final long START_OF_THE_DAY_OFFSET =
      DateTime.now().withTimeAtStartOfDay().getMillis();
  private static final DateTimeFormatter dateFormatter =
      DateTimeFormat.forPattern("HH:mm").withZone(DateTimeZone.UTC);
  private static final String VALID_FROM_TIME = "08:00";
  private static final String VALID_TO_TIME = "17:00";
  private static final DateTime IN_RANGE_DATE_1 = DateTime.parse(VALID_FROM_TIME, dateFormatter)
      .plus(START_OF_THE_DAY_OFFSET);
  private static final DateTime IN_RANGE_DATE_2 = DateTime.parse(VALID_TO_TIME, dateFormatter)
      .plus(START_OF_THE_DAY_OFFSET);;
  private static final DateTime IN_RANGE_DATE_3 = DateTime.parse(VALID_FROM_TIME, dateFormatter)
      .plus(START_OF_THE_DAY_OFFSET).plusHours(1).plusMinutes(10).plusSeconds(11);
  private static final DateTime IN_RANGE_DATE_4 = DateTime.parse(VALID_TO_TIME, dateFormatter)
      .plus(START_OF_THE_DAY_OFFSET).minusSeconds(1);
  private static final DateTime OUT_OF_RANGE_DATE_1 = DateTime.parse(VALID_FROM_TIME, dateFormatter)
      .plus(START_OF_THE_DAY_OFFSET).minusSeconds(1);
  private static final DateTime OUT_OF_RANGE_DATE_2 = DateTime.parse(VALID_TO_TIME, dateFormatter)
      .plus(START_OF_THE_DAY_OFFSET).plusSeconds(1);

  @Test
  public void testAlwaysValidScheduler() {
    StrategyEventScheduler strategyEventScheduler =
        new StrategyEventScheduler(createSchedulerConfiguration());
    assertTrue(strategyEventScheduler.isTimeValid(IN_RANGE_DATE_1));
    assertTrue(strategyEventScheduler.isTimeValid(IN_RANGE_DATE_2));
    assertTrue(strategyEventScheduler.isTimeValid(IN_RANGE_DATE_3));
    assertTrue(strategyEventScheduler.isTimeValid(IN_RANGE_DATE_4));
    assertTrue(strategyEventScheduler.isTimeValid(OUT_OF_RANGE_DATE_1));
    assertTrue(strategyEventScheduler.isTimeValid(OUT_OF_RANGE_DATE_2));
  }

  @Test
  public void testValidDate() {
    StrategyEventScheduler strategyEventScheduler =
        new StrategyEventScheduler(createSchedulerConfiguration(VALID_FROM_TIME, VALID_TO_TIME));
    assertTrue(strategyEventScheduler.isTimeValid(IN_RANGE_DATE_1));
    assertTrue(strategyEventScheduler.isTimeValid(IN_RANGE_DATE_2));
    assertTrue(strategyEventScheduler.isTimeValid(IN_RANGE_DATE_3));
    assertTrue(strategyEventScheduler.isTimeValid(IN_RANGE_DATE_4));
  }

  @Test
  public void testNotValidDate() {
    StrategyEventScheduler strategyEventScheduler =
        new StrategyEventScheduler(createSchedulerConfiguration(VALID_FROM_TIME, VALID_TO_TIME));
    assertTrue(!strategyEventScheduler.isTimeValid(OUT_OF_RANGE_DATE_1));
    assertTrue(!strategyEventScheduler.isTimeValid(OUT_OF_RANGE_DATE_2));
  }

  private SchedulerConfiguration createSchedulerConfiguration() {
    SchedulerConfiguration schedulerConfiguration = new SchedulerConfiguration();
    return schedulerConfiguration;
  }

  private SchedulerConfiguration createSchedulerConfiguration(String fromTime, String toTime) {
    SchedulerConfiguration schedulerConfiguration = new SchedulerConfiguration();
    schedulerConfiguration.setValidFromTime(fromTime);
    schedulerConfiguration.setValidToTime(toTime);
    return schedulerConfiguration;
  }
}
