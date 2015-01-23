package com.onplan.service;

import com.onplan.strategy.StrategyInfo;
import com.onplan.strategy.StrategyTemplateInfo;

import java.io.Serializable;
import java.util.List;

public interface StrategyServiceRemote extends Serializable {
  /**
   * Removes an instantiated strategy by its id.
   * @param strategyId the strategy id.
   */
  public void removeStrategy(String strategyId);

  /**
   * Loads a collection of sample strategies, deleting and replacing all the ones which have been
   * instantiated.
   * @throws Exception error while loading the collection of sample strategies.
   */
  public void loadSampleStrategies() throws Exception;

  /**
   * Returns the collection of strategies info which have been instantiated.
   */
  public List<StrategyInfo> getStrategiesInfo();

  /**
   * Returns the collection of available strategy templates info.
   */
  public List<StrategyTemplateInfo> getStrategiesTemplateInfo();

  /**
   * Returns the strategy template info by the its class name or null if the template was not found.
   * @param className The class name.
   */
  public StrategyTemplateInfo getStrategyTemplateInfo(String className);
}
