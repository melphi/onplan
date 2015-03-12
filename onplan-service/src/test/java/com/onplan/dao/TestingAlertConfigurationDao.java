package com.onplan.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.onplan.adviser.predicate.priceaction.PriceSpikePredicate;
import com.onplan.adviser.predicate.pricepattern.CandlestickHammerPredicate;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.persistence.AlertConfigurationDao;
import com.onplan.util.TestingConstants;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.onplan.adviser.AdviserConfigurationFactory.createAlertConfiguration;
import static com.onplan.util.TestingConstants.DEFAULT_CREATION_DATE;
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
        TestingConstants.INSTRUMENT_EURUSD_ID,
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips.",
        true,
        DEFAULT_CREATION_DATE.getMillis()));
    builder.add(createAlertConfiguration(
        PriceSpikePredicate.class,
        TestingConstants.INSTRUMENT_AUDUSD_ID,
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips.",
        true,
        DEFAULT_CREATION_DATE.getMillis()));
    builder.add(createAlertConfiguration(
        PriceSpikePredicate.class,
        TestingConstants.INSTRUMENT_DAX_ID,
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips.",
        true,
        DEFAULT_CREATION_DATE.getMillis()));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        TestingConstants.INSTRUMENT_DAX_ID,
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "10"),
        "Candlestick hammer.",
        true,
        DEFAULT_CREATION_DATE.getMillis()));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        TestingConstants.INSTRUMENT_FTSE100_ID,
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        "Candlestick hammer.",
        true,
        DEFAULT_CREATION_DATE.getMillis()));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        TestingConstants.INSTRUMENT_DAX_ID,
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        "Candlestick hammer.",
        true,
        DEFAULT_CREATION_DATE.getMillis()));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        TestingConstants.INSTRUMENT_FTSE100_ID,
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "6"),
        "Candlestick hammer.",
        true,
        DEFAULT_CREATION_DATE.getMillis()));
    List<AlertConfiguration> result = builder.build();
    checkArgument(result.size() == INITIAL_ALERTS_LIST_SIZE);
    return result;
  }
}
