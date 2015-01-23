package com.onplan.adapter;

import com.onplan.domain.PriceBar;
import com.onplan.domain.PriceTick;

public interface PriceListener {
  public void onPriceTick(PriceTick priceTick);
  public void onPriceBar(PriceBar priceBar);
}
