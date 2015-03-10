package com.onplan.factory;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.SeverityLevel;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.AlertConfiguration;

import java.util.List;
import java.util.Map;

import static com.onplan.factory.TestingAdviserPredicateConfigurationFactory.createSampleAdviserPredicateConfigurations;
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

  public static AlertConfiguration createAlertConfiguration(
      Class<? extends AdviserPredicate> predicateClass, String instrumentId,
      Map<String, String> parameters, String alertMessage, SeverityLevel severityLevel) {
    AdviserPredicateConfiguration predicate = new AdviserPredicateConfiguration(
        predicateClass.getName(), parameters);
    List<AdviserPredicateConfiguration> predicateChain = ImmutableList.of(predicate);
    return new AlertConfiguration(
        null,
        alertMessage,
        severityLevel,
        instrumentId,
        predicateChain,
        DEFAULT_CREATION_DATE.getMillis(),
        true);
  }
}
