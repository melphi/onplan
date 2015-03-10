package com.onplan.scheduler;

import com.onplan.connector.PriceService;
import com.onplan.connector.ServiceConnection;
import com.onplan.connector.ServiceConnectionListener;
import com.onplan.service.AlertService;
import com.onplan.service.StrategyService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import javax.inject.Inject;
import javax.inject.Named;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

/**
 * Activates / deactivates the system services at the desired time. Also implements a reconnection
 * mechanism after an unexpected disconnection.
 */
@DisallowConcurrentExecution
public class ServicesActivationJob implements Job {
  private static final Logger LOGGER = Logger.getLogger(ServicesActivationJob.class);
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormat.forPattern("HH:mm").withZone(DateTimeZone.UTC);

  @Inject
  @Named("market.forex.openTime")
  private String forexOpenTime;

  @Inject
  @Named("market.forex.closeTime")
  private String forexCloseTime;

  @Inject
  private StrategyService strategyService;

  @Inject
  private AlertService alertService;

  @Inject
  private PriceService priceService;

  private ServiceConnection serviceConnection;

  @Inject
  public void setServiceConnection(ServiceConnection serviceConnection) {
    checkArgument(null == this.serviceConnection, "Service connection already set.");
    this.serviceConnection = checkNotNull(serviceConnection);
    this.serviceConnection.addServiceConnectionListener(new InternalServiceConnectionListener());
  }

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    performServiceActivation();
  }

  private void performServiceActivation() {
    DateTime dateTime = DateTime.now(DateTimeZone.UTC);
    if(isMarketOpen(dateTime)) {
      if(!serviceConnection.isConnected()) {
        LOGGER.warn("Adapter service not connected, establishing connection.");
        serviceConnection.connect();
      }
    } else {
      if(serviceConnection.isConnected()) {
        LOGGER.warn("Out of market hours, closing connector service connection.");
        serviceConnection.disconnect();
      }
    }
  }

  private boolean isMarketOpen(DateTime dateTime) {
    checkNotNull(dateTime);
    checkNotNullOrEmpty(forexCloseTime);
    checkNotNullOrEmpty(forexOpenTime);
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

  private class InternalServiceConnectionListener implements ServiceConnectionListener {
    @Override
    public void onConnectionEstablished() {
      LOGGER.info("Service connection established, loading the advisers.");
      try {
        strategyService.loadAllStrategies();
        alertService.loadAllAlerts();
      } catch (Exception e) {
        LOGGER.error(String.format(
            "Error while loading all registered strategies [%s].", e.getMessage()));
      }
    }

    @Override
    public void onDisconnected() {
      LOGGER.info("Service connection lost, un-subscribing the advisers.");
      try {
        strategyService.unLoadAllStrategies();
        alertService.unLoadAllAlerts();
      } catch (Exception e) {
        LOGGER.error(String.format(
            "Error while un-loading all registered strategies [%s].", e.getMessage()));
      }
      performServiceActivation();
    }
  }
}
