package com.onplan.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.onplan.adviser.strategy.system.IntegrationTestStrategy;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.util.TestingConstants;

import java.util.List;

import static com.onplan.adviser.StrategyConfigurationFactory.createStrategyConfiguration;

public class TestingStrategyConfigurationDao
    extends TestingAbstractDao<StrategyConfiguration> implements StrategyConfigurationDao {
  public TestingStrategyConfigurationDao() throws Exception {
    removeAll();
    insertAll(getSampleStrategiesConfiguration());
  }

  @Override
  public List<StrategyConfiguration> getSampleStrategiesConfiguration() {
    return ImmutableList.of(
        createStrategyConfiguration(
            IntegrationTestStrategy.class,
            TestingConstants.INSTRUMENT_EURUSD_ID,
            ImmutableMap.of()),
        createStrategyConfiguration(
            IntegrationTestStrategy.class,
            TestingConstants.INSTRUMENT_AUDUSD_ID,
            ImmutableMap.of()),
        createStrategyConfiguration(
            IntegrationTestStrategy.class,
            TestingConstants.INSTRUMENT_DAX_ID,
            ImmutableMap.of()),
        createStrategyConfiguration(
            IntegrationTestStrategy.class,
            TestingConstants.INSTRUMENT_FTSE100_ID,
            ImmutableMap.of()));
  }
}
