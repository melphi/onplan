package com.onplan.persistence.mongodb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.onplan.adviser.strategy.system.IntegrationTestStrategy;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;

import javax.inject.Singleton;
import java.util.List;

import static com.onplan.adviser.StrategyConfigurationFactory.createStrategyConfiguration;

@Singleton
public final class MongoDbStrategyConfigurationDao extends AbstractMongoDbDao<StrategyConfiguration>
    implements StrategyConfigurationDao {
  private static final String STRATEGY_CONFIGURATION_COLLECTION = "strategyConfiguration";

  public MongoDbStrategyConfigurationDao() {
    super(STRATEGY_CONFIGURATION_COLLECTION, StrategyConfiguration.class);
  }

  // TODO(robertom): Load the sample strategies from a file.
  @Override
  public List<StrategyConfiguration> getSampleStrategiesConfiguration() {
    return ImmutableList.of(createStrategyConfiguration(
        IntegrationTestStrategy.class,
        "CS.EURUSD.TODAY",
        ImmutableMap.of()));
  }
}
