package com.onplan.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.onplan.adviser.SeverityLevel;
import com.onplan.adviser.predicate.priceaction.PriceSpikePredicate;
import com.onplan.adviser.predicate.pricepattern.CandlestickHammerPredicate;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.persistence.AlertConfigurationDao;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.onplan.factory.TestingAlertConfigurationFactory.createAlertConfiguration;
import static com.onplan.util.TestingConstants.INITIAL_ALERTS_LIST_SIZE;

public class TestingAlertConfigurationDao
    extends TestingAbstractDao<AlertConfiguration> implements AlertConfigurationDao {
  public TestingAlertConfigurationDao() throws Exception {
    removeAll();
    insertAll(getSampleAlertsConfiguration());
  }

  @Override
  public List<AlertConfiguration> getSampleAlertsConfiguration() {
    ImmutableList.Builder builder = ImmutableList.builder();
    builder.add(createAlertConfiguration(
        PriceSpikePredicate.class,
        "CS.EURUSD.TODAY",
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips.",
        SeverityLevel.LOW));
    builder.add(createAlertConfiguration(
        PriceSpikePredicate.class,
        "CS.AUDUSD.TODAY",
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips.",
        SeverityLevel.MEDIUM));
    builder.add(createAlertConfiguration(
        PriceSpikePredicate.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips.",
        SeverityLevel.HIGH));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "10"),
        "Candlestick hammer.",
        SeverityLevel.LOW));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.FTSE.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        "Candlestick hammer.",
        SeverityLevel.MEDIUM));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        "Candlestick hammer.",
        SeverityLevel.HIGH));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.FTSE.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "6"),
        "Candlestick hammer.",
        SeverityLevel.LOW));
    List<AlertConfiguration> result = builder.build();
    checkArgument(result.size() == INITIAL_ALERTS_LIST_SIZE);
    return result;
  }
}
