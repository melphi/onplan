package com.onplan.adviser.predicate;

import com.onplan.domain.PriceTick;

import java.util.Map;

public interface AdviserPredicate {
  /**
   * Processes a {@link com.onplan.domain.PriceTick}, if the predicate is satisfied returns true,
   * otherwise returns false.
   * @param priceTick The price tick.
   */
  public boolean processPriceTick(final PriceTick priceTick);

  /**
   * Initializes the predicate.
   */
  public void init() throws Exception;

  /**
   * Returns a copy of the execution parameters.
   */
  public Map<String, String> getExecutionParameters();
}
