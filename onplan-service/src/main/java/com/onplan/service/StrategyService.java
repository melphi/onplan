package com.onplan.service;

import com.onplan.adviser.strategy.Strategy;
import com.onplan.domain.persistent.PriceTick;

import java.util.List;

public interface StrategyService extends StrategyServiceRemote {
  public void onPriceTick(final PriceTick priceTick);
  public void setInstrumentSubscriptionListener(
      InstrumentSubscriptionListener instrumentSubscriptionListener);
  public List<Strategy> getStrategies();
  public void loadAllStrategies() throws Exception;
  public void unLoadAllStrategies() throws Exception;
}
