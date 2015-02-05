package com.onplan.processor;

import com.onplan.adapter.PriceListener;
import com.onplan.adapter.PriceService;
import com.onplan.domain.PriceBar;
import com.onplan.domain.PriceTick;
import com.onplan.service.AlertService;
import com.onplan.service.StrategyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Component
public final class PriceServiceBus {
  private static final Logger LOGGER = Logger.getLogger(PriceServiceBus.class);

  private final PriceListener priceListener = new InternalPriceListener();

  private PriceService priceService;

  @Autowired
  private StrategyService strategyService;

  @Autowired
  private AlertService alertService;

  @Autowired
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
        (new AlertServiceNotifyThread(priceTick)).start();
      }
    }

    @Override
    public void onPriceBar(PriceBar priceBar) {
      // Intentionally empty.
    }
  }

  private class AlertServiceNotifyThread extends Thread {
    private final PriceTick priceTick;

    public AlertServiceNotifyThread(final PriceTick priceTick) {
      this.priceTick = priceTick;
    }

    @Override
    public void run() {
      alertService.onPriceTick(priceTick);
    }
  }
}
