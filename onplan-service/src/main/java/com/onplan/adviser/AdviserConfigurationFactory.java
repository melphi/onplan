package com.onplan.adviser;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.AlertConfiguration;

import java.util.List;
import java.util.Map;

public final class AdviserConfigurationFactory {
  public static AlertConfiguration createAlertConfiguration(
      Class<? extends AdviserPredicate> predicateClass, String instrumentId,
      Map<String, String> parameters, String alertMessage, boolean repeat,
      long createdOn) {
    AdviserPredicateConfiguration predicate = new AdviserPredicateConfiguration();
    predicate.setClassName(predicateClass.getName());
    predicate.setParameters(parameters);
    List<AdviserPredicateConfiguration> predicateChain = ImmutableList.of(predicate);
    return new AlertConfiguration(
        null,
        alertMessage,
        SeverityLevel.MEDIUM,
        instrumentId,
        predicateChain,
        createdOn,
        repeat);
  }
}
