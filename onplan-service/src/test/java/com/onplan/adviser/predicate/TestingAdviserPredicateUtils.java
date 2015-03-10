package com.onplan.adviser.predicate;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class TestingAdviserPredicateUtils {
  public static final void checkAdviserPredicate(AdviserPredicate adviserPredicate) {
    checkNotNull(adviserPredicate);
    checkNotNull(adviserPredicate.getExecutionParameters());
    checkArgument(!adviserPredicate.getExecutionParameters().isEmpty());
    for (Map.Entry<String, String> parameter :
        adviserPredicate.getExecutionParameters().entrySet()) {
      checkNotNullOrEmpty(parameter.getValue());
      checkNotNullOrEmpty(parameter.getKey());
    }
  }
}
