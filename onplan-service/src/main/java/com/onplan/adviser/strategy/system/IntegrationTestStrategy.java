package com.onplan.adviser.strategy.system;

import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.strategy.AbstractStrategy;
import com.onplan.domain.PriceTick;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

@TemplateMetaData(displayName = "Integration test")
public class IntegrationTestStrategy extends AbstractStrategy {
  private int lastFiredOn = 0;

  @Override
  public void onPriceTick(PriceTick priceTick) {
    int dayOfMonth = DateTime.now(DateTimeZone.UTC).getDayOfMonth();
    if (lastFiredOn != dayOfMonth) {
      String message = String.format("Ping from integration test! Price [%s].", priceTick);
      strategyExecutionContext.getStrategyListener().onAlert(message);
      lastFiredOn = dayOfMonth;
      updateStatistics(priceTick, true);
    } else {
      updateStatistics(priceTick, false);
    }
  }

  @Override
  public void init() throws Exception {
    // Intentionally empty.
  }
}
