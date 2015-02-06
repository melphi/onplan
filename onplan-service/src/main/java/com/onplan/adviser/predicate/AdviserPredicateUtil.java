package com.onplan.adviser.predicate;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.AdviserPredicateInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.adviser.TemplateMetaData;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AdviserPredicateUtil {
  public static TemplateInfo createAdviserPredicateTemplateInfo(
      Class<? extends AdviserPredicate> clazz) {
    checkNotNull(clazz);
    TemplateMetaData adviserTemplate = clazz.getAnnotation(TemplateMetaData.class);
    checkNotNull(adviserTemplate, String.format(
        "Adviser predicate [%s] does not implement the annotation [%s].",
        clazz.getName(),
        TemplateMetaData.class.getName()));
    return new TemplateInfo(
        adviserTemplate.displayName(),
        clazz.getName(),
        ImmutableList.copyOf(adviserTemplate.availableParameters()));
  }

  public static AdviserPredicateInfo createAdviserPredicateInfo(
      final AdviserPredicate adviserPredicate) {
    checkNotNull(adviserPredicate);
    TemplateInfo templateInfo = createAdviserPredicateTemplateInfo(adviserPredicate.getClass());
    return new AdviserPredicateInfo(
        templateInfo.getDisplayName(),
        templateInfo.getClassName(),
        templateInfo.getAvailableParameters(),
        adviserPredicate.getExecutionParameters());
  }
}
