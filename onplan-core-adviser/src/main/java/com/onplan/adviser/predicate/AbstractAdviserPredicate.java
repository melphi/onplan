package com.onplan.adviser.predicate;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractAdviserPredicate implements AdviserPredicate {
  protected final PredicateExecutionContext predicateExecutionContext;

  public AbstractAdviserPredicate(final PredicateExecutionContext predicateExecutionContext) {
    this.predicateExecutionContext = checkNotNull(predicateExecutionContext);
  }

  @Override
  public Map<String, String> getExecutionParameters() {
    return predicateExecutionContext.getExecutionParameters();
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
