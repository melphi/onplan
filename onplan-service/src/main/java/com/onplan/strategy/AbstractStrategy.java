package com.onplan.strategy;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractStrategy implements Strategy {
  protected StrategyExecutionContext executionContext;

  public void setExecutionContext(StrategyExecutionContext executionContext) {
    checkArgument(this.executionContext == null, "Execution context already set.");
    this.executionContext = checkNotNull(executionContext);
  }

  protected void dispatchEvent(StrategyEvent strategyEvent) {
    checkNotNull(strategyEvent);
    executionContext.getStrategyListener().onEvent(strategyEvent);
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
