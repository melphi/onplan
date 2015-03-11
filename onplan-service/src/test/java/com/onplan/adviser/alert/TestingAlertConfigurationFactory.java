package com.onplan.adviser.alert;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.SeverityLevel;
import com.onplan.domain.configuration.AlertConfiguration;

import java.util.List;

import static com.onplan.adviser.predicate.TestingAdviserPredicateConfigurationFactory.createSampleAdviserPredicateConfigurations;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;
import static com.onplan.util.TestingConstants.*;

public class TestingAlertConfigurationFactory {
  public static AlertConfiguration createSampleAlertConfigurationWithNullId() {
    return createSampleAlertConfigurationWithNullId(
        INSTRUMENT_EURUSD_ID, DEFAULT_ALERT_MESSAGE, true);
  }

  public static AlertConfiguration createSampleAlertConfigurationWithNullId(
      String instrumentId, String message, boolean repeat) {
    checkNotNullOrEmpty(instrumentId);
    checkNotNullOrEmpty(message);
    return new AlertConfiguration(
        null,
        message,
        SeverityLevel.HIGH,
        instrumentId,
        createSampleAdviserPredicateConfigurations(),
        DEFAULT_CREATION_DATE.getMillis(),
        repeat);
  }

  public static List<AlertConfiguration> createSampleAlertConfigurationsWithNullId() {
    ImmutableList.Builder<AlertConfiguration> result = ImmutableList.builder();
    for (int i = 0; i < INITIAL_ALERTS_LIST_SIZE; i++) {
      result.add(createSampleAlertConfigurationWithNullId(
          String.format("instrumentId%d", i),
          String.format("Alert message %d", i),
          (i % 2 == 0)));
    }
    return result.build();
  }
}
