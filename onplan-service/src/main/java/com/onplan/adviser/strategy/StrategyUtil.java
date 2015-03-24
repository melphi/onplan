package com.onplan.adviser.strategy;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.StrategyInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.adviser.TemplateMetaData;

import static com.google.common.base.Preconditions.checkNotNull;

public final class StrategyUtil {
  public static TemplateInfo getTemplateInfo(Class<? extends Strategy> clazz) {
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

  public static String getTemplateId(Class<? extends Strategy> clazz) {
    return getTemplateInfo(clazz).getClassName();
  }

  public static StrategyInfo getStrategyInfo(final Strategy strategy) {
    TemplateInfo templateInfo = getTemplateInfo(strategy.getClass());
    return new StrategyInfo(
        templateInfo.getDisplayName(),
        templateInfo.getClassName(),
        templateInfo.getAvailableParameters(),
        strategy.getId(),
        strategy.getParametersCopy(),
        strategy.getRegisteredInstruments(),
        strategy.getStrategyStatisticsSnapshot());
  }
}
