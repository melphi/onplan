package com.onplan.adviser.predicate;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Strings;

import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractAdviserPredicate implements AdviserPredicate {
  protected final PredicateExecutionContext predicateExecutionContext;

  public AbstractAdviserPredicate(final PredicateExecutionContext predicateExecutionContext) {
    this.predicateExecutionContext = checkNotNull(predicateExecutionContext);
  }

  @Override
  public String getParameterValue(String parameterName) {
    return predicateExecutionContext.getParameterValue(parameterName);
  }

  @Override
  public Map<String, String> getParametersCopy() {
    return predicateExecutionContext.getParametersCopy();
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractAdviserPredicate adviserPredicate = (AbstractAdviserPredicate) o;
    return Objects.equal(
        this.predicateExecutionContext, adviserPredicate.predicateExecutionContext);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(predicateExecutionContext);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("predicateExecutionContext", predicateExecutionContext)
        .toString();
  }
}
