package com.onplan.adapter;

import com.onplan.domain.persistent.PriceBar;
import com.onplan.domain.persistent.PriceTick;

public interface PriceListener {
  public void onPriceTick(final PriceTick priceTick);
  public void onPriceBar(final PriceBar priceBar);
}
