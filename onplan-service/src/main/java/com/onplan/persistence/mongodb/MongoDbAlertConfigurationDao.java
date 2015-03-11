package com.onplan.persistence.mongodb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.onplan.adviser.predicate.priceaction.PriceSpikePredicate;
import com.onplan.adviser.predicate.pricepattern.CandlestickHammerPredicate;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.persistence.AlertConfigurationDao;
import org.joda.time.DateTime;

import javax.inject.Singleton;
import java.util.List;

import static com.onplan.adviser.AdviserConfigurationFactory.createAlertConfiguration;

@Singleton
public final class MongoDbAlertConfigurationDao extends AbstractMongoDbDao<AlertConfiguration>
    implements AlertConfigurationDao {
  private static final String ALERT_CONFIGURATION_COLLECTION = "alertConfiguration";

  public MongoDbAlertConfigurationDao() {
    super(ALERT_CONFIGURATION_COLLECTION, AlertConfiguration.class);
  }

  // TODO(robertom): Load the sample alerts from JSON file.
  @Override
  public List<AlertConfiguration> getSampleAlertsConfiguration() {
    ImmutableList.Builder builder = ImmutableList.builder();
    builder.add(createAlertConfiguration(
        PriceSpikePredicate.class,
        "CS.EURUSD.TODAY",
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips.",
        true,
        DateTime.now().getMillis()));
    builder.add(createAlertConfiguration(
        PriceSpikePredicate.class,
        "CS.AUDUSD.TODAY",
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips.",
        true,
        DateTime.now().getMillis()));
    builder.add(createAlertConfiguration(
        PriceSpikePredicate.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips.",
        true,
        DateTime.now().getMillis()));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "10"),
        "Candlestick hammer.",
        true,
        DateTime.now().getMillis()));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.FTSE.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        "Candlestick hammer.",
        true,
        DateTime.now().getMillis()));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        "Candlestick hammer.",
        true,
        DateTime.now().getMillis()));
    builder.add(createAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.FTSE.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "6"),
        "Candlestick hammer.",
        true,
        DateTime.now().getMillis()));
    return builder.build();
  }
}
