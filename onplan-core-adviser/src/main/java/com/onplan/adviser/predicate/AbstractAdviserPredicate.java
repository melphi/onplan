package com.onplan.adviser.predicate;

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
}
