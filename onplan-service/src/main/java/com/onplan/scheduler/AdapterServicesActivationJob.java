package com.onplan.scheduler;

import com.google.common.base.Joiner;
import com.onplan.adapter.PriceService;
import com.onplan.adapter.ServiceConnection;
import com.onplan.adapter.ServiceConnectionListener;
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
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

/**
 * Activates / deactivates the system services at the desired time. Also implements a reconnection
 * mechanism after an unexpected disconnection.
 */
@DisallowConcurrentExecution
public class AdapterServicesActivationJob implements Job {
  private static final Logger LOGGER = Logger.getLogger(AdapterServicesActivationJob.class);
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
  private PriceService priceService;

  private ServiceConnection serviceConnection;

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
        LOGGER.warn("Out of market hours, closing adapter service connection.");
        serviceConnection.disconnect();
      }
    }
  }

  @Inject
  private void setServiceConnection(ServiceConnection serviceConnection) {
    this.serviceConnection = checkNotNull(serviceConnection);
    serviceConnection.addServiceConnectionListener(new InternalServiceConnectionListener());
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
      LOGGER.info("Service connection established, subscribing all strategies.");
      try {
        strategyService.loadAllStrategies();
      } catch (Exception e) {
        LOGGER.error(String.format(
            "Error while loading all registered strategies [%s].", e.getMessage()));
      }
      LOGGER.info("Subscribing price service instruments.");
      Set<String> instruments = strategyService.getSubscribedInstruments();
      for (String instrumentId : instruments) {
        try {
          priceService.subscribeInstrument(instrumentId);
        } catch (Exception e) {
          LOGGER.error(String.format(
              "Error while subscribing instrument [%s]: [%s].", instrumentId, e.getMessage()));
          return;
        }
      }
      LOGGER.info(String.format(
          "[%d] Subscribed instruments: [%s].",
          instruments.size(),
          Joiner.on(", ").join(instruments)));
    }

    @Override
    public void onDisconnected() {
      LOGGER.info("Service connection lost, un-subscribing all strategies.");
      try {
        strategyService.unLoadAllStrategies();
      } catch (Exception e) {
        LOGGER.error(String.format(
            "Error while un-loading all registered strategies [%s].", e.getMessage()));
      }
      performServiceActivation();
    }
  }
}
