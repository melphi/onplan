package com.onplan.strategy.alert;

import com.onplan.domain.PriceTick;
import com.onplan.strategy.AbstractStrategy;
import com.onplan.strategy.StrategyEvent;
import com.onplan.strategy.StrategyEventType;
import com.onplan.strategy.StrategyTemplate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@StrategyTemplate(name = "Integration test")
public class IntegrationTestStrategy extends AbstractStrategy {
  private int lastFiredOn = 0;

  @Override
  public void processPriceTick(PriceTick priceTick) {
    int dayOfMonth = DateTime.now(DateTimeZone.UTC).getDayOfMonth();
    if (lastFiredOn != dayOfMonth) {
      dispatchEvent(createStrategyEvent(priceTick));
      lastFiredOn = dayOfMonth;
    }
    updateStatistics(priceTick);
  }

  @Override
  public void initStrategy() throws Exception {
    // Intentionally empty.
  }

  private StrategyEvent createStrategyEvent(PriceTick priceTick) {
    StrategyEvent event = StrategyEvent.newBuilder()
        .setEventType(StrategyEventType.ALERT)
        .setInstrumentId(priceTick.getInstrumentId())
        .setTimestamp(priceTick.getTimestamp())
        .setPriceTick(priceTick)
        .setMessage(String.format("Ping from integration test! Price [%s].", priceTick))
        .setStrategyExecutionContext(executionContext)
        .build();
    return event;
  }
}
