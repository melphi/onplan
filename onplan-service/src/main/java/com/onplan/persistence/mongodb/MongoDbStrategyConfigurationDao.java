package com.onplan.persistence.mongodb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.adviser.strategy.Strategy;
import com.onplan.adviser.strategy.system.IntegrationTestStrategy;
import com.onplan.domain.configuration.adviser.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Singleton
public class MongoDbStrategyConfigurationDao extends AbstractMongoDbDao<StrategyConfiguration>
    implements StrategyConfigurationDao {
  private static final String STRATEGY_CONFIGURATION_COLLECTION = "strategyConfiguration";

  public MongoDbStrategyConfigurationDao() {
    super(STRATEGY_CONFIGURATION_COLLECTION, StrategyConfiguration.class);
  }

  @Override
  public List<StrategyConfiguration> getSampleStrategiesConfiguration() {
    return ImmutableList.of(createStrategyConfiguration(
        IntegrationTestStrategy.class,
        "CS.EURUSD.TODAY",
        ImmutableMap.of()));
  }

  private StrategyConfiguration createStrategyConfiguration(Class<? extends Strategy> clazz,
      String instrumentId, Map<String, String> properties) {
    StrategyConfiguration strategyConfiguration = new StrategyConfiguration();
    strategyConfiguration.setClassName(clazz.getName());
    strategyConfiguration.setInstruments(ImmutableSet.of(instrumentId));
    strategyConfiguration.setExecutionParameters(properties);
    return strategyConfiguration;
  }
}
