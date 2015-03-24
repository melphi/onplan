package com.onplan.adviser.strategy;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.onplan.adviser.SeverityLevel;
import com.onplan.adviser.StrategyStatisticsSnapshot;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.domain.transitory.PriceTick;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractStrategy implements Strategy {
  protected final StrategyExecutionContext strategyExecutionContext;

  private final StrategyStatistics strategyStatistics = new StrategyStatistics();
  private final StrategyListener strategyListener;

  public AbstractStrategy(StrategyExecutionContext strategyExecutionContext) {
    this.strategyExecutionContext = checkNotNull(strategyExecutionContext);
    this.strategyListener = checkNotNull(strategyExecutionContext.getStrategyListener());
  }

  @Override
  public String getParameterValue(String parameterName) {
    checkNotNull(strategyExecutionContext);
    checkNotNullOrEmpty(parameterName);
    return strategyExecutionContext.getParameterValue(parameterName);
  }

  @Override
  public Map<String, String> getParametersCopy() {
    checkNotNull(strategyExecutionContext);
    return strategyExecutionContext.getParametersCopy();
  }

  public Collection<String> getRegisteredInstruments() {
    checkNotNull(strategyExecutionContext);
    return ImmutableSet.copyOf(strategyExecutionContext.getRegisteredInstruments());
  }

  public Optional<String> getStringValue(String parameterName) {
    checkNotNullOrEmpty(parameterName);
    String propertyValue = getParameterValue(parameterName);
    return Strings.isNullOrEmpty(propertyValue) ? Optional.empty() : Optional.of(propertyValue);
  }

  public Optional<Long> getLongValue(String parameterName) {
    Optional<String> propertyValue = getStringValue(parameterName);
    return propertyValue.isPresent() ? Optional.of(Long.parseLong(propertyValue.get()))
        : Optional.empty();
  }

  public Optional<Double> getDoubleValue(String parameterName) {
    Optional<String> propertyValue = getStringValue(parameterName);
    return propertyValue.isPresent() ? Optional.of(Double.parseDouble(propertyValue.get()))
        : Optional.empty();
  }

  public String getId() {
    return strategyExecutionContext.getStrategyId();
  }

  @Override
  public StrategyStatisticsSnapshot getStrategyStatisticsSnapshot() {
    synchronized (strategyStatistics) {
      long averageCompletionNanoTime = 0;
      if(strategyStatistics.getReceivedTicks() > 0) {
        averageCompletionNanoTime = Math.round(
            strategyStatistics.getCumulatedCompletionNanoTime() /
            strategyStatistics.getReceivedTicks());
      }
      return new StrategyStatisticsSnapshot(
          strategyStatistics.getLastReceivedTickTimestamp(),
          strategyStatistics.getReceivedTicks(),
          strategyStatistics.getEventsDispatchedCounter(),
          strategyStatistics.getLastCompletionNanoTime(),
          strategyStatistics.getMaxCompletionNanoTime(),
          averageCompletionNanoTime);
    }
  }

  protected void dispatchAlertEvent(
      final SeverityLevel severityLevel, final String message, final PriceTick priceTick) {
    long createdOn = DateTime.now().getMillis();
    AlertEvent alertEvent = new AlertEvent(getId(), severityLevel, priceTick, createdOn, message);
    strategyListener.onAlert(alertEvent);
  }

  protected void dispatchNewOrderEvent() {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  protected void updateStatistics(final PriceTick priceTick, final boolean eventFired) {
    final long lastCompletionNanoTime = System.nanoTime() - priceTick.getReceivedNanoTime();
    synchronized (strategyStatistics) {
      strategyStatistics.setLastCompletionNanoTime(lastCompletionNanoTime);
      strategyStatistics.incrementReceivedTicks();
      strategyStatistics.incrementCumulatedCompletionNanoTime(lastCompletionNanoTime);
      strategyStatistics.setLastReceivedTickTimestamp(priceTick.getTimestamp());
      if (lastCompletionNanoTime > strategyStatistics.getMaxCompletionNanoTime()) {
        strategyStatistics.setMaxCompletionNanoTime(lastCompletionNanoTime);
      }
      if(eventFired) {
        strategyStatistics.incrementEventsDispatchedCounter();
      }
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractStrategy strategy = (AbstractStrategy) o;
    return Objects.equal(this.strategyExecutionContext, strategy.strategyExecutionContext) &&
        Objects.equal(this.strategyStatistics, strategy.strategyStatistics);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(strategyExecutionContext, strategyStatistics);
  }
}
