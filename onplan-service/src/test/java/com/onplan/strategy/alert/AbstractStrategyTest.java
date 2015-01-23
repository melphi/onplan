package com.onplan.strategy.alert;

import com.onplan.strategy.*;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AbstractStrategyTest<T extends Strategy> {
  protected void assertEventFired(StrategyListener strategyListener, int times,
      String instrumentId, StrategyEventType eventType) {
    checkNotNullOrEmpty(instrumentId);
    checkNotNull(eventType);
    checkNotNull(strategyListener);
    ArgumentCaptor<StrategyEvent> strategyEvent = ArgumentCaptor.forClass(StrategyEvent.class);
    verify(strategyListener, times(times)).onEvent(strategyEvent.capture());
    assertEquals(instrumentId, strategyEvent.getValue().getInstrumentId());
    assertEquals(eventType, strategyEvent.getValue().getEventType());
  }

  protected void assertEventNotFired(StrategyListener strategyListener) {
    ArgumentCaptor<StrategyEvent> strategyEvent = ArgumentCaptor.forClass(StrategyEvent.class);
    verify(strategyListener, times(0)).onEvent(strategyEvent.capture());
  }

  protected T initStrategy(T strategy, StrategyListener strategyListener,
      Map<String, String> properties, Set<String> instruments) throws Exception {
    StrategyExecutionContext executionContext =
        StrategyExecutionContextFactory.createStrategyExecutionContext(
            strategyListener, properties, instruments);
    strategy.setExecutionContext(executionContext);
    strategy.initStrategy();
    return strategy;
  }
}
