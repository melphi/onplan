package com.onplan.persistence.mongodb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.domain.configuration.EventHandlerConfiguration;
import com.onplan.domain.configuration.SchedulerConfiguration;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.strategy.Strategy;
import com.onplan.strategy.StrategyEventType;
import com.onplan.strategy.alert.IntegrationTestStrategy;
import com.onplan.strategy.alert.PriceSpikeStrategy;
import com.onplan.strategy.alert.candlestickpattern.CandlestickHammerStrategy;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class StrategyConfigurationMondgoDbDao extends AbstractMongoDbDao<StrategyConfiguration>
    implements StrategyConfigurationDao {
  public StrategyConfigurationMondgoDbDao() {
    super("strategyConfiguration", StrategyConfiguration.class);
  }

  @Override
  public List<StrategyConfiguration> getSampleStrategiesConfiguration() {
    SchedulerConfiguration schedulerConfiguration0To23 = new SchedulerConfiguration();
    schedulerConfiguration0To23.setValidFromTime("00:01");
    schedulerConfiguration0To23.setValidToTime("23:59");
    SchedulerConfiguration schedulerConfiguration8To14 = new SchedulerConfiguration();
    schedulerConfiguration8To14.setValidFromTime("08:00");
    schedulerConfiguration8To14.setValidToTime("14:00");
    SchedulerConfiguration schedulerConfiguration8To9 = new SchedulerConfiguration();
    schedulerConfiguration8To9.setValidFromTime("08:00");
    schedulerConfiguration8To9.setValidToTime("09:00");

    ImmutableList.Builder builder = ImmutableList.builder();
    builder.add(createStrategyConfiguration(
        IntegrationTestStrategy.class,
        "EURUSD",
        ImmutableMap.of(),
        schedulerConfiguration0To23));
    builder.add(createStrategyConfiguration(
        PriceSpikeStrategy.class,
        "EURUSD",
        ImmutableMap.of(PriceSpikeStrategy.PROPERTY_MINIMUM_PIPS, "15"),
        new SchedulerConfiguration()));
    builder.add(createStrategyConfiguration(
        PriceSpikeStrategy.class,
        "AUDUSD",
        ImmutableMap.of(PriceSpikeStrategy.PROPERTY_MINIMUM_PIPS, "15"),
        new SchedulerConfiguration()));
    builder.add(createStrategyConfiguration(
        PriceSpikeStrategy.class,
        "DAX",
        ImmutableMap.of(PriceSpikeStrategy.PROPERTY_MINIMUM_PIPS, "15"),
        new SchedulerConfiguration()));
    builder.add(createStrategyConfiguration(
        CandlestickHammerStrategy.class,
        "DAX",
        ImmutableMap.of(
            CandlestickHammerStrategy.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE, "10"),
        schedulerConfiguration8To14));
    builder.add(createStrategyConfiguration(
        CandlestickHammerStrategy.class,
        "FTSE",
        ImmutableMap.of(
            CandlestickHammerStrategy.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        schedulerConfiguration8To14));
    builder.add(createStrategyConfiguration(
        CandlestickHammerStrategy.class,
        "DAX",
        ImmutableMap.of(
            CandlestickHammerStrategy.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        schedulerConfiguration8To9));
    builder.add(createStrategyConfiguration(
        CandlestickHammerStrategy.class,
        "FTSE",
        ImmutableMap.of(
            CandlestickHammerStrategy.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE, "6"),
        schedulerConfiguration8To9));
    return builder.build();
  }

  private StrategyConfiguration createStrategyConfiguration(Class<? extends Strategy> clazz,
      String instrumentId, Map<String, String> properties,
      SchedulerConfiguration schedulerConfiguration) {
    EventHandlerConfiguration eventHandlerConfiguration = new EventHandlerConfiguration();
    eventHandlerConfiguration.setEventType(StrategyEventType.ALERT);
    eventHandlerConfiguration.setSchedulerConfiguration(schedulerConfiguration);
    StrategyConfiguration strategyConfiguration = new StrategyConfiguration();
    strategyConfiguration.setClassName(clazz.getName());
    strategyConfiguration.setInstruments(ImmutableSet.of(instrumentId));
    strategyConfiguration.setExecutionParameters(properties);
    return strategyConfiguration;
  }
}
