package com.onplan.adviser;

import com.onplan.domain.PriceTick;

public interface Adviser {
  /**
   * Processes a price tick.
   * @param priceTick The price tick.
   */
  public void onPriceTick(final PriceTick priceTick);
}
