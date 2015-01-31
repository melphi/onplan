package com.onplan.strategy;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.domain.PriceTick;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractStrategy implements Strategy {
  protected StrategyExecutionContext executionContext;

  private final StrategyStatistics strategyStatistics = new StrategyStatistics();

  private long receivedTicks = 0;
  private long eventsDispatchedCounter = 0;
  private long maxCompletionNanoTime = 0;

  public void setExecutionContext(StrategyExecutionContext executionContext) {
    checkArgument(this.executionContext == null, "Execution context already set.");
    this.executionContext = checkNotNull(executionContext);
  }

  protected void dispatchEvent(StrategyEvent strategyEvent) {
    checkNotNull(strategyEvent);
    executionContext.getStrategyListener().onEvent(strategyEvent);
    synchronized (strategyStatistics) {
      strategyStatistics.setEventsDispatchedCounter(++eventsDispatchedCounter);
    }
  }

  public Map<String, String> getExecutionParameters() {
    checkNotNull(executionContext);
    return ImmutableMap.copyOf(executionContext.getExecutionParameters());
  }

  public Collection<String> getRegisteredInstruments() {
    checkNotNull(executionContext);
    return ImmutableSet.copyOf(executionContext.getRegisteredInstruments());
  }

  public String getId() {
    return executionContext.getStrategyId();
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

  public void updateStatistics(final PriceTick priceTick) {
    final long lastCompletionNanoTime = System.nanoTime() - priceTick.getReceivedNanoTime();
    synchronized (strategyStatistics) {
      strategyStatistics.setLastCompletionNanoTime(lastCompletionNanoTime);
      strategyStatistics.setReceivedTicks(++receivedTicks);
      strategyStatistics.setLastReceivedTickTimestamp(priceTick.getTimestamp());
      strategyStatistics.setMaxCompletionNanoTime(
          Math.max(maxCompletionNanoTime, lastCompletionNanoTime));
    }
    System.out.println("Update statistics " + strategyStatistics);
  }

  protected Optional<String> getStringProperty(String propertyName) {
    checkNotNullOrEmpty(propertyName);
    String propertyValue = executionContext.getExecutionParameters().get(propertyName);
    return Strings.isNullOrEmpty(propertyValue) ? Optional.empty() : Optional.of(propertyValue);
  }

  protected Optional<Long> getLongProperty(String propertyName) {
    Optional<String> propertyValue = getStringProperty(propertyName);
    return propertyValue.isPresent() ? Optional.of(Long.parseLong(propertyValue.get()))
        : Optional.empty();
  }

  protected Optional<Double> getDoubleProperty(String propertyName) {
    Optional<String> propertyValue = getStringProperty(propertyName);
    return propertyValue.isPresent() ? Optional.of(Double.parseDouble(propertyValue.get()))
        : Optional.empty();
  }

  protected String getRequiredStringProperty(String propertyName) {
    Optional<String> propertyValue = getStringProperty(propertyName);
    checkArgument(
        propertyValue.isPresent(), String.format("Property [%s] not set or empty.", propertyName));
    return propertyValue.get();
  }

  protected Long getRequiredLongProperty(String propertyName) {
    Optional<Long> propertyValue = getLongProperty(propertyName);
    checkArgument(
        propertyValue.isPresent(), String.format("Property [%s] not set or empty.", propertyName));
    return propertyValue.get();
  }

  protected Double getRequiredDoubleProperty(String propertyName) {
    Optional<Double> propertyValue = getDoubleProperty(propertyName);
    checkArgument(
        propertyValue.isPresent(), String.format("Property [%s] not set or empty.", propertyName));
    return propertyValue.get();
  }
}
