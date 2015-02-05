package com.onplan.adviser.predicate;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.AdviserPredicateInfo;
import com.onplan.adviser.TemplateMetaData;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AdviserPredicateUtil {
  public static AdviserPredicateInfo createAdviserPredicateInfo(
      final AdviserPredicate adviserPredicate) {
    checkNotNull(adviserPredicate);
    TemplateMetaData adviserTemplate =
        adviserPredicate.getClass().getAnnotation(TemplateMetaData.class);
    checkNotNull(adviserTemplate, String.format(
        "Adviser [%s] does not implement the annotation [%s].",
        adviserPredicate.getClass().getName(),
        TemplateMetaData.class.getName()));
    return new AdviserPredicateInfo(
        adviserTemplate.displayName(),
        adviserPredicate.getClass().getName(),
        ImmutableList.copyOf(adviserTemplate.availableParameters()),
        adviserPredicate.getExecutionParameters());
  }
}
