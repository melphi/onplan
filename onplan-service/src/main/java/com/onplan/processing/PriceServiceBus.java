package com.onplan.processing;

import com.onplan.connector.PriceListener;
import com.onplan.connector.PriceService;
import com.onplan.domain.persistent.PriceBar;
import com.onplan.domain.persistent.PriceTick;
import com.onplan.service.AlertService;
import com.onplan.service.InstrumentSubscriptionListener;
import com.onplan.service.StrategyService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.CompletableFuture.runAsync;

@Singleton
public final class PriceServiceBus {
  private static final Logger LOGGER = Logger.getLogger(PriceServiceBus.class);

  private final PriceListener priceListener = new InternalPriceListener();
  private final InstrumentSubscriptionListener instrumentSubscriptionListener =
      new InternalInstrumentSubscriptionListener();

  private PriceService priceService;
  private StrategyService strategyService;
  private AlertService alertService;

  @Inject
  public void setStrategyService(StrategyService strategyService) {
    checkArgument(null == this.strategyService, "Strategy service already set.");
    this.strategyService = checkNotNull(strategyService);
    this.strategyService.setInstrumentSubscriptionListener(instrumentSubscriptionListener);
  }

  @Inject
  public void setAlertService(AlertService alertService) {
    checkArgument(null == this.alertService, "Alert service already set.");
    this.alertService = checkNotNull(alertService);
    this.alertService.setInstrumentSubscriptionListener(instrumentSubscriptionListener);
  }

  @Inject
  private void setPriceService(PriceService priceService) {
    checkArgument(null == this.priceService, "Price service already set.");
    this.priceService = checkNotNull(priceService);
    this.priceService.setPriceListener(priceListener);
    LOGGER.info(String.format("PriceService provided by [%s] assigned to PriceServiceBus.",
        priceService.getServiceConnectionInfo().getProviderName()));
  }

  private final class InternalPriceListener implements PriceListener {
    @Override
    public void onPriceTick(final PriceTick priceTick) {
      strategyService.onPriceTick(priceTick);
      if (alertService.hasAlerts()) {
        runAsync(new AlertServiceNotification(priceTick));
        /* TODO(robertom): Benchmark if there is any speed / memory improvement with lambda.
         * For example: new Thread(() -> alertService.onPriceTick(priceTick)).start();
         */
      }
    }

    @Override
    public void onPriceBar(PriceBar priceBar) {
      // Intentionally empty.
    }
  }

  private final class AlertServiceNotification implements Runnable {
    private final PriceTick priceTick;

    public AlertServiceNotification(final PriceTick priceTick) {
      this.priceTick = priceTick;
    }

    @Override
    public void run() {
      alertService.onPriceTick(priceTick);
    }
  }

  private final class InternalInstrumentSubscriptionListener
      implements InstrumentSubscriptionListener {
    @Override
    public void onInstrumentSubscriptionRequest(final String instrumentId) {
      if (!priceService.isInstrumentSubscribed(instrumentId)) {
        LOGGER.info(String.format("New instrument subscription required [%s].", instrumentId));
        try {
          priceService.subscribeInstrument(instrumentId);
        } catch (Exception e) {
          LOGGER.error(String.format(
              "Exception while subscribing instrument [%s]: [%s].", instrumentId, e.getMessage()));
        }
      }
    }

    @Override
    public void onInstrumentUnSubscriptionRequest(String instrumentId) {
      // TODO(robertom): Implement this code.
    }
  }
}
