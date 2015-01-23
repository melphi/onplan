package com.onplan.adapter;

import com.onplan.domain.PriceBar;
import com.onplan.domain.PriceTick;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractPriceService implements PriceService {
  private PriceListener priceListener;

  @Override
  public void setPriceListener(PriceListener priceListener) {
    checkNotNull(priceListener);
    checkArgument(null == this.priceListener, "Price listener already set.");
    this.priceListener = priceListener;
  }

  protected void dispatchPriceTick(final PriceTick priceTick) {
    priceListener.onPriceTick(priceTick);
  }

  protected void dispatchPriceBar(final PriceBar priceBar) {
    priceListener.onPriceBar(priceBar);
  }
}
