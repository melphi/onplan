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
   * Initializes the strategy.
   */
  public void init() throws Exception;

  /**
   * Returns the parameter value.
   *
   * @param parameterName The parameter name.
   */
  public String getParameterValue(String parameterName);

  /**
   * Returns a copy of the parameters set.
   */
  public Map<String, String> getParametersCopy();

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
