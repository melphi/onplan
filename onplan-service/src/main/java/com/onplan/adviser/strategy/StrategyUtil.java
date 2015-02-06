package com.onplan.adviser.strategy;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.StrategyInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.adviser.TemplateMetaData;

import static com.google.common.base.Preconditions.checkNotNull;

public final class StrategyUtil {
  public static TemplateInfo getStrategyTemplateInfo(Class<? extends Strategy> clazz) {
    TemplateMetaData templateMetaData = clazz.getAnnotation(TemplateMetaData.class);
    checkNotNull(templateMetaData, String.format(
        "Strategy [%s] does not implement the annotation [%s].",
        clazz.getName(),
        TemplateMetaData.class.getName()));
    return new TemplateInfo(
        templateMetaData.displayName(),
        clazz.getName(),
        ImmutableList.copyOf(templateMetaData.availableParameters()));
  }

  public static StrategyInfo getStrategyInfo(final Strategy strategy) {
    TemplateInfo templateInfo = getStrategyTemplateInfo(strategy.getClass());
    return new StrategyInfo(
        templateInfo.getDisplayName(),
        templateInfo.getClassName(),
        templateInfo.getAvailableParameters(),
        strategy.getId(),
        strategy.getExecutionParameters(),
        strategy.getRegisteredInstruments(),
        strategy.getStrategyStatistics());
  }
}
