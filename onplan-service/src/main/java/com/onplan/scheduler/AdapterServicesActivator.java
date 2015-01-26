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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Activates / deactivates the system services at the desired time. Also implements a reconnection
 * mechanism after an unexpected disconnection.
 */
public class AdapterServicesActivator implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(AdapterServicesActivator.class);
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormat.forPattern("HH:mm").withZone(DateTimeZone.UTC);

  @Value(value = "${market.forex.openTime}")
  private String forexOpenTime;

  @Value(value = "${market.forex.closeTime}")
  private String forexCloseTime;

  @Autowired
  private StrategyService strategyService;

  @Autowired
  private PriceService priceService;

  private ServiceConnection serviceConnection;

  @Override
  public void run() {
    DateTime dateTime = DateTime.now(DateTimeZone.UTC);
    if(isMarketOpen(dateTime)) {
      if(!serviceConnection.isConnected()) {
        LOGGER.warn("PriceService disconnected, reestablishing connection.");
        serviceConnection.connect();
      }
    } else {
      if(serviceConnection.isConnected()) {
        LOGGER.warn("Out of market hours, disconnecting price service.");
        serviceConnection.disconnect();
      }
    }
  }

  @Autowired
  private void setServiceConnection(ServiceConnection serviceConnection) {
    this.serviceConnection = checkNotNull(serviceConnection);
    serviceConnection.addServiceConnectionListener(new InternalServiceConnectionListener());
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

  private class InternalServiceConnectionListener implements ServiceConnectionListener {
    @Override
    public void onConnectionEstablished() {
      LOGGER.info("Connection established, subscribing instruments.");
      Set<String> instruments = strategyService.getSubscribedInstruments();
      for (String instrumentId : instruments) {
        try {
          priceService.subscribeInstrument(instrumentId);
        } catch (Exception e) {
          LOGGER.error(String.format(
              "Error while subscribing instrument [%s]: [%s].", instrumentId, e.getMessage()));
        }
      }
      LOGGER.info(String.format(
          "[%d] Subscribed instruments: [%s].",
          instruments.size(),
          Joiner.on(", ").join(instruments)));
    }

    @Override
    public void onDisconnected() {
      run();
    }
  }
}
