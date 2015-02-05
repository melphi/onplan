package com.onplan.service;

import com.onplan.domain.PriceTick;
import com.onplan.adviser.strategy.Strategy;

import java.util.List;
import java.util.Set;

public interface StrategyService extends StrategyServiceRemote {
  public void onPriceTick(final PriceTick priceTick);
  public List<Strategy> getStrategies();
  public Set<String> getSubscribedInstruments();
  public void loadAllStrategies() throws Exception;
  public void unLoadAllStrategies() throws Exception;
}
