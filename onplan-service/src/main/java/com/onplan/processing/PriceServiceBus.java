package com.onplan.processing;

import com.onplan.adapter.PriceListener;
import com.onplan.adapter.PriceService;
import com.onplan.domain.PriceBar;
import com.onplan.domain.PriceTick;
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

  public PriceServiceBus() {
    LOGGER.info("Price service alive!");
  }

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

  private class InternalPriceListener implements PriceListener {
    @Override
    public void onPriceTick(final PriceTick priceTick) {
      strategyService.onPriceTick(priceTick);
      if (alertService.hasAlerts()) {
        runAsync(new AlertServiceNotification(priceTick));
      }
    }

    @Override
    public void onPriceBar(PriceBar priceBar) {
      // Intentionally empty.
    }
  }

  private class AlertServiceNotification implements Runnable {
    private final PriceTick priceTick;

    public AlertServiceNotification(final PriceTick priceTick) {
      this.priceTick = priceTick;
    }

    @Override
    public void run() {
      alertService.onPriceTick(priceTick);
    }
  }

  private class InternalInstrumentSubscriptionListener implements InstrumentSubscriptionListener {
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
