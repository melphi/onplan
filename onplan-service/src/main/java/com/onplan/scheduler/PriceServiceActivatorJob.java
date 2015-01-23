package com.onplan.scheduler;

import com.onplan.adapter.ServiceConnection;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static com.google.common.base.Preconditions.checkNotNull;

public class PriceServiceActivatorJob implements Runnable {
  private static final Logger logger = Logger.getLogger(PriceServiceActivatorJob.class);
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormat.forPattern("HH:mm").withZone(DateTimeZone.UTC);

  @Value(value = "${market.forex.openTime}")
  private String forexOpenTime;

  @Value(value = "${market.forex.closeTime}")
  private String forexCloseTime;

  @Autowired
  private ServiceConnection serviceConnection;

  @Override
  public void run() {
    DateTime dateTime = DateTime.now(DateTimeZone.UTC);
    if(isMarketOpen(dateTime)) {
      if(!serviceConnection.isConnected()) {
        logger.warn("PriceService disconnected, reestablishing connection.");
        serviceConnection.connect();
      }
    } else {
      if(serviceConnection.isConnected()) {
        logger.warn("Out of market hours, disconnecting price service.");
        serviceConnection.disconnect();
      }
    }
  }

  private boolean isMarketOpen(DateTime dateTime) {
    checkNotNull(dateTime);
    int dayOfWeek = dateTime.dayOfWeek().get();
    switch (dayOfWeek) {
      case DateTimeConstants.SATURDAY:
        return false;
      case DateTimeConstants.FRIDAY:
        DateTime marketCloseTime = DateTime.parse(forexCloseTime, DATE_TIME_FORMATTER)
            .plus(dateTime.withTimeAtStartOfDay().getMillis());
        return dateTime.compareTo(marketCloseTime) < 0;
      case DateTimeConstants.SUNDAY:
        DateTime marketOpenTime = DateTime.parse(forexOpenTime, DATE_TIME_FORMATTER)
            .plus(dateTime.withTimeAtStartOfDay().getMillis());
        return dateTime.compareTo(marketOpenTime) >= 0;
      case DateTimeConstants.MONDAY:
      case DateTimeConstants.TUESDAY:
      case DateTimeConstants.WEDNESDAY:
      case DateTimeConstants.THURSDAY:
        return true;
      default:
        throw new IllegalArgumentException(
            String.format("Unsupported day of the week [%d]", dayOfWeek));
    }
  }
}
