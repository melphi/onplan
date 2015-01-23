package com.onplan.strategy.alert;

import com.onplan.domain.PriceTick;
import com.onplan.strategy.AbstractStrategy;
import com.onplan.strategy.StrategyTemplate;

@StrategyTemplate(name = "Day high / low")
public class DailyHighLowStrategy extends AbstractStrategy {
  @Override
  public void processPriceTick(PriceTick priceTick) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void initStrategy() {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
