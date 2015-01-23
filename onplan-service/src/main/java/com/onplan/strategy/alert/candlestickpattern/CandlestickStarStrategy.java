package com.onplan.strategy.alert.candlestickpattern;

import com.onplan.domain.PriceTick;
import com.onplan.strategy.AbstractStrategy;
import com.onplan.strategy.StrategyTemplate;

@StrategyTemplate(name = "Candlestick star")
public class CandlestickStarStrategy extends AbstractStrategy {
  @Override
  public void processPriceTick(PriceTick priceTick) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void initStrategy() {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
