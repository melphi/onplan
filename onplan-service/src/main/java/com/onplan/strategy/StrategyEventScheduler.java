package com.onplan.strategy;

import com.google.common.base.Strings;
import com.onplan.domain.configuration.SchedulerConfiguration;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class StrategyEventScheduler {
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormat.forPattern("HH:mm").withZone(DateTimeZone.UTC);

  private final boolean alwaysValid;
  private final String validFromTime;
  private final String validToTime;

  private volatile long startOfDay;
  private volatile long periodStart;
  private volatile long periodEnd;

  public StrategyEventScheduler(SchedulerConfiguration schedulerConfiguration) {
    checkNotNull(schedulerConfiguration);
    validFromTime = schedulerConfiguration.getValidFromTime();
    validToTime = schedulerConfiguration.getValidToTime();
    alwaysValid = Strings.isNullOrEmpty(validFromTime) && Strings.isNullOrEmpty(validToTime);
    if (!alwaysValid) {
      checkArgument(!(Strings.isNullOrEmpty(validFromTime) || Strings.isNullOrEmpty(validToTime)),
          "Invalid from time and to time values.");
      updateDatePeriodValues();
    }
  }

  private void updateDatePeriodValues() {
    startOfDay = DateTime.now().withTimeAtStartOfDay().getMillis();
    periodStart = DateTime.parse(validFromTime, DATE_TIME_FORMATTER).plus(startOfDay).getMillis();
    periodEnd = DateTime.parse(validToTime, DATE_TIME_FORMATTER).plus(startOfDay).getMillis();
  }

  public boolean isTimeValid(DateTime dateTime) {
    if (alwaysValid) {
      return true;
    }
    if (startOfDay != DateTime.now().withTimeAtStartOfDay().getMillis()) {
      updateDatePeriodValues();
    }
    return dateTime.getMillis() >= periodStart && dateTime.getMillis() <= periodEnd;
  }
}
