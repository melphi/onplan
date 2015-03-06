package com.onplan.adapter;

import com.onplan.domain.PriceBar;
import com.onplan.domain.PriceTick;

public interface PriceListener {
  public void onPriceTick(final PriceTick priceTick);
  public void onPriceBar(final PriceBar priceBar);
}
