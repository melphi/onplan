package com.onplan.persistence.mongodb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.onplan.adviser.SeverityLevel;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.adviser.predicate.priceaction.PriceSpikePredicate;
import com.onplan.adviser.predicate.pricepattern.CandlestickHammerPredicate;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.persistence.AlertConfigurationDao;
import org.joda.time.DateTime;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

@Singleton
public final class MongoDbAlertConfigurationDao extends AbstractMongoDbDao<AlertConfiguration>
    implements AlertConfigurationDao {
  private static final String ALERT_CONFIGURATION_COLLECTION = "alertConfiguration";

  public MongoDbAlertConfigurationDao() {
    super(ALERT_CONFIGURATION_COLLECTION, AlertConfiguration.class);
  }

  // TODO(robertom): Load the sample alerts from a file.
  @Override
  public List<AlertConfiguration> getSampleAlertsConfiguration() {
    ImmutableList.Builder builder = ImmutableList.builder();
    builder.add(createSampleAlertConfiguration(
        PriceSpikePredicate.class,
        "CS.EURUSD.TODAY",
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips."));
    builder.add(createSampleAlertConfiguration(
        PriceSpikePredicate.class,
        "CS.AUDUSD.TODAY",
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips."));
    builder.add(createSampleAlertConfiguration(
        PriceSpikePredicate.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "15"),
        "Price spike > 15 pips."));
    builder.add(createSampleAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "10"),
        "Candlestick hammer."));
    builder.add(createSampleAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.FTSE.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_15",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        "Candlestick hammer."));
    builder.add(createSampleAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.DAX.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "8"),
        "Candlestick hammer."));
    builder.add(createSampleAlertConfiguration(
        CandlestickHammerPredicate.class,
        "IX.FTSE.DAILY",
        ImmutableMap.of(
            CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_5",
            CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "6"),
        "Candlestick hammer."));
    return builder.build();
  }

  private AlertConfiguration createSampleAlertConfiguration(
      Class<? extends AdviserPredicate> predicateClass, String instrumentId,
      Map<String, String> parameters, String alertMessage) {
    AdviserPredicateConfiguration predicate = new AdviserPredicateConfiguration();
    predicate.setClassName(predicateClass.getName());
    predicate.setParameters(parameters);
    List<AdviserPredicateConfiguration> predicateChain = ImmutableList.of(predicate);
    return new AlertConfiguration(
        null,
        alertMessage,
        SeverityLevel.MEDIUM,
        instrumentId,
        predicateChain,
        DateTime.now().getMillis(),
        true);
  }
}
