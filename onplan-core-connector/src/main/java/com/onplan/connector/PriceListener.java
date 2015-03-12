package com.onplan.connector;

import com.onplan.domain.transitory.PriceBar;
import com.onplan.domain.transitory.PriceTick;

public interface PriceListener {
  public void onPriceTick(final PriceTick priceTick);
  public void onPriceBar(final PriceBar priceBar);
}
