package com.onplan.adviser.strategy;

import com.onplan.domain.PriceTick;

public interface StrategyListener {
  // TODO(robertom): Implement onNewOrder event.
  public void onNewOrder(final PriceTick priceTick);
  public void onAlert(final String message);
}
