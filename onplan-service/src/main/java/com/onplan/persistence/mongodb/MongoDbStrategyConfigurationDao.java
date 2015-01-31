package com.onplan.persistence.mongodb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.strategy.Strategy;
import com.onplan.strategy.alert.IntegrationTestStrategy;
import com.onplan.strategy.alert.PriceSpikeStrategy;
import com.onplan.strategy.alert.candlestickpattern.CandlestickHammerStrategy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class MongoDbStrategyConfigurationDao extends AbstractMongoDbDao<StrategyConfiguration>
    implements StrategyConfigurationDao {
  public MongoDbStrategyConfigurationDao() {
    super("strategyConfiguration", StrategyConfiguration.class);
  }

  @Override
  public List<StrategyConfiguration> getSampleStrategiesConfiguration() {
    ImmutableList.Builder builder = ImmutableList.builder();
    builder.add(createStrategyConfiguration(
        IntegrationTestStrategy.class,
        "CS.EURUSD.TODAY",
        ImmutableMap.of()));
    builder.add(createStrategyConfiguration(
        PriceSpikeStrategy.class,
        "CS.EURUSD.TODAY",
        ImmutableMap.of(PriceSpikeStrategy.PROPERTY_MINIMUM_PIPS, "15")));
    builder.add(createStrategyConfiguration(
        PriceSpikeStrategy.class,
        "CS.AUDUSD.TODAY",
        ImmutableMap.of(PriceSpikeStrategy.PROPERTY_MINIMUM_PIPS, "15")));
    builder.add(createStrategyConfiguration(
        PriceSpikeStrategy.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(PriceSpikeStrategy.PROPERTY_MINIMUM_PIPS, "15")));
    builder.add(createStrategyConfiguration(
        CandlestickHammerStrategy.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(
            CandlestickHammerStrategy.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE, "10")));
    builder.add(createStrategyConfiguration(
        CandlestickHammerStrategy.class,
        "IX.FTSE.DAILY",
        ImmutableMap.of(
            CandlestickHammerStrategy.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE, "8")));
    builder.add(createStrategyConfiguration(
        CandlestickHammerStrategy.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(
            CandlestickHammerStrategy.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE, "8")));
    builder.add(createStrategyConfiguration(
        CandlestickHammerStrategy.class,
        "IX.FTSE.DAILY",
        ImmutableMap.of(
            CandlestickHammerStrategy.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE, "6")));
    return builder.build();
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
