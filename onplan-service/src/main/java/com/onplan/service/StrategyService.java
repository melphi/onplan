package com.onplan.service;

import com.onplan.strategy.Strategy;

import java.util.List;

public interface StrategyService extends StrategyServiceRemote {
  public List<Strategy> getStrategies();
}
