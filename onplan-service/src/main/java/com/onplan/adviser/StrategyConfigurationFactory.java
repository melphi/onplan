package com.onplan.adviser;

import com.google.common.collect.ImmutableSet;
import com.onplan.adviser.strategy.Strategy;
import com.onplan.domain.configuration.StrategyConfiguration;

import java.util.Map;

public final class StrategyConfigurationFactory {
  public static StrategyConfiguration createStrategyConfiguration(Class<? extends Strategy> clazz,
      String instrumentId, Map<String, String> properties) {
    StrategyConfiguration strategyConfiguration = new StrategyConfiguration();
    strategyConfiguration.setClassName(clazz.getName());
    strategyConfiguration.setInstruments(ImmutableSet.of(instrumentId));
    strategyConfiguration.setExecutionParameters(properties);
    return strategyConfiguration;
  }
}
