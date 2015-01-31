package com.onplan.strategy;

import com.onplan.domain.PriceTick;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface Strategy extends Serializable {
  /**
   * Returns the strategy unique id;
   */
  public String getId();

  /**
   * Processes a price tick.
   * @param priceTick The price tick.
   */
  public void processPriceTick(final PriceTick priceTick);

  /**
   * Sets the execution context.
   * @param strategyExecutionContext The strategy execution context.
   */
  public void setExecutionContext(StrategyExecutionContext strategyExecutionContext);

  /**
   * Initializes the strategy.
   */
  public void initStrategy() throws Exception;

  /**
   * Returns a copy of the execution parameters which are defined in the
   * {@link StrategyExecutionContext}.
   */
  public Map<String, String> getExecutionParameters();

  /**
   * Returns a copy of the instruments which the strategy listens to as defined in the
   * {@link StrategyExecutionContext}.
   */
  public Collection<String> getRegisteredInstruments();

  /**
   * Returns a copy of the current strategy statistics.
   */
  public StrategyStatistics getStrategyStatistics();
}
