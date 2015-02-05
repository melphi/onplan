package com.onplan.adviser.strategy;

import com.onplan.adviser.Adviser;
import com.onplan.adviser.StrategyStatistics;

import java.util.Collection;
import java.util.Map;

public interface Strategy extends Adviser {
  /**
   * Returns the strategy unique id;
   */
  public String getId();

  /**
   * Sets the execution context.
   * @param strategyExecutionContext The strategy execution context.
   */
  public void setStrategyExecutionContext(final StrategyExecutionContext strategyExecutionContext);

  /**
   * Initializes the strategy.
   */
  public void init() throws Exception;

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
