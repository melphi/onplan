package com.onplan.adviser.predicate;

import com.onplan.domain.transitory.PriceTick;

import java.util.Map;

public interface AdviserPredicate {
  /**
   * Processes a {@link com.onplan.domain.transitory.PriceTick}, returns true if the predicate
   * is satisfied, false otherwise.
   * @param priceTick The price tick.
   */
  public boolean apply(final PriceTick priceTick);

  /**
   * Initializes the predicate.
   */
  public void init() throws Exception;

  /**
   * Returns a copy of the execution parameters.
   */
  public Map<String, String> getExecutionParameters();
}
