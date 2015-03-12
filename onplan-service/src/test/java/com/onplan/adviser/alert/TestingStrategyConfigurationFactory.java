package com.onplan.adviser.alert;

import com.google.common.collect.ImmutableSet;
import com.onplan.adviser.strategy.system.IntegrationTestStrategy;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.util.TestingConstants;

public class TestingStrategyConfigurationFactory {
  public static StrategyConfiguration createSampleStrategyConfigurationWithNullId() {
    return new StrategyConfiguration(
        null,
        IntegrationTestStrategy.class.getName(),
        TestingConstants.DEFAULT_ADVISER_PARAMETERS,
        ImmutableSet.of(TestingConstants.INSTRUMENT_EURUSD_ID));
  }
}
