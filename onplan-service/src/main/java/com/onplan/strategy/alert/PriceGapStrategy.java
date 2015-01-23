package com.onplan.strategy.alert;

import com.onplan.domain.PriceTick;
import com.onplan.strategy.AbstractStrategy;
import com.onplan.strategy.StrategyTemplate;

@StrategyTemplate(name = "Price gap")
public class PriceGapStrategy extends AbstractStrategy {
  @Override
  public void processPriceTick(PriceTick priceTick) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void initStrategy() {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
