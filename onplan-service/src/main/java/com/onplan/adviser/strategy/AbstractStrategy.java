package com.onplan.adviser.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.adviser.StrategyStatistics;
import com.onplan.domain.PriceTick;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractStrategy implements Strategy {
  protected StrategyExecutionContext strategyExecutionContext;

  private final StrategyStatistics strategyStatistics = new StrategyStatistics();

  private long receivedTicks = 0;
  private long eventsDispatchedCounter = 0;
  private long maxCompletionNanoTime = 0;

  public void setStrategyExecutionContext(StrategyExecutionContext strategyExecutionContext) {
    checkArgument(this.strategyExecutionContext == null, "Execution context already set.");
    this.strategyExecutionContext = checkNotNull(strategyExecutionContext);
  }

  public Map<String, String> getExecutionParameters() {
    checkNotNull(strategyExecutionContext);
    return ImmutableMap.copyOf(strategyExecutionContext.getExecutionParameters());
  }

  public Collection<String> getRegisteredInstruments() {
    checkNotNull(strategyExecutionContext);
    return ImmutableSet.copyOf(strategyExecutionContext.getRegisteredInstruments());
  }

  public String getId() {
    return strategyExecutionContext.getStrategyId();
  }

  public StrategyStatistics getStrategyStatistics() {
    synchronized (strategyStatistics) {
      return new StrategyStatistics(
          strategyStatistics.getLastReceivedTickTimestamp(),
          strategyStatistics.getReceivedTicks(),
          strategyStatistics.getEventsDispatchedCounter(),
          strategyStatistics.getLastCompletionNanoTime(),
          strategyStatistics.getMaxCompletionNanoTime());
    }
  }

  public void updateStatistics(final PriceTick priceTick, final boolean eventFired) {
    final long lastCompletionNanoTime = System.nanoTime() - priceTick.getReceivedNanoTime();
    synchronized (strategyStatistics) {
      strategyStatistics.setLastCompletionNanoTime(lastCompletionNanoTime);
      strategyStatistics.setReceivedTicks(++receivedTicks);
      strategyStatistics.setLastReceivedTickTimestamp(priceTick.getTimestamp());
      strategyStatistics.setMaxCompletionNanoTime(
          Math.max(maxCompletionNanoTime, lastCompletionNanoTime));
      strategyStatistics.setEventsDispatchedCounter(++eventsDispatchedCounter);
    }
  }
}
