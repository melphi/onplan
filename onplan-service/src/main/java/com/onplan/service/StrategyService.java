package com.onplan.service;

import com.onplan.strategy.Strategy;

import java.util.List;
import java.util.Set;

public interface StrategyService extends StrategyServiceRemote {
  public List<Strategy> getStrategies();
  public Set<String> getSubscribedInstruments();
  public void loadAllStrategies() throws Exception;
  public void unLoadAllStrategies() throws Exception;
}
