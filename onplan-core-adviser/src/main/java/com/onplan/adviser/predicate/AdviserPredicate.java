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
   * Returns the parameter value.
   *
   * @param parameterName The parameter key.
   */
  public String getParameterValue(String parameterName);

  /**
   * Returns a copy of the parameters set.
   */
  public Map<String, String> getParametersCopy();
}
