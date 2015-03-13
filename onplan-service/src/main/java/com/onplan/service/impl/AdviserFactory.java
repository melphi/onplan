package com.onplan.service.impl;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.AdviserListener;
import com.onplan.adviser.alert.Alert;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.adviser.strategy.Strategy;
import com.onplan.adviser.strategy.StrategyExecutionContext;
import com.onplan.adviser.strategy.StrategyListener;
import com.onplan.connector.HistoricalPriceService;
import com.onplan.connector.InstrumentService;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.domain.configuration.StrategyConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;

public final class AdviserFactory {
  public static Strategy createStrategy(StrategyConfiguration strategyConfiguration,
      StrategyListener strategyListener, InstrumentService instrumentService,
      HistoricalPriceService historicalPriceService) throws Exception {
    checkNotNull(strategyConfiguration, "strategyConfiguration is null");
    checkNotNull(strategyListener, "strategyListener is null.");
    checkNotNull(instrumentService, "instrumentService is null.");
    checkNotNull(historicalPriceService, "historicalPriceService is null.");
    try {
      StrategyExecutionContext strategyExecutionContext = new StrategyExecutionContext(
          strategyConfiguration.getId(),
          historicalPriceService,
          instrumentService,
          strategyListener,
          strategyConfiguration.getExecutionParameters(),
          strategyConfiguration.getInstruments());
      Class clazz = getClass(strategyConfiguration.getClassName());
      return (Strategy) clazz.getConstructor(StrategyExecutionContext.class)
          .newInstance(strategyExecutionContext);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new Exception(String.format(
          "[%s] exception while loading strategy id [%s] for class [%s]: [%s].",
          e.getClass(),
          strategyConfiguration.getId(),
          strategyConfiguration.getClassName(),
          e.getMessage()),
          e);
    }
  }

  public static Alert createAlert(AlertConfiguration alertConfiguration,
      AdviserListener<AlertEvent> alertEventListener, InstrumentService instrumentService,
      HistoricalPriceService historicalPriceService) throws Exception {
    checkNotNull(alertConfiguration, "alertConfiguration is null");
    checkNotNull(alertEventListener, "alertEventListener is null.");
    checkNotNull(instrumentService, "instrumentService is null.");
    checkNotNull(historicalPriceService, "historicalPriceService is null.");
    ImmutableList.Builder<AdviserPredicate> predicatesChain = ImmutableList.builder();
    for (AdviserPredicateConfiguration adviserPredicateConfiguration
        : alertConfiguration.getPredicatesChain()) {
      predicatesChain.add(createAdviserPredicate(
          adviserPredicateConfiguration,
          alertConfiguration.getInstrumentId(),
          instrumentService,
          historicalPriceService));
    }

    return Alert.newBuilder()
        .setId(alertConfiguration.getId())
        .setAlertMessage(alertConfiguration.getMessage())
        .setSeverityLevel(alertConfiguration.getSeverityLevel())
        .setInstrumentId(alertConfiguration.getInstrumentId())
        .setCreatedOn(alertConfiguration.getCreateOn())
        .setRepeat(alertConfiguration.getRepeat())
        .setAlertListener(alertEventListener)
        .setPredicatesChain(predicatesChain.build())
        .build();
  }

  public static AdviserPredicate createAdviserPredicate(
      AdviserPredicateConfiguration adviserPredicateConfiguration, String instrumentId,
      InstrumentService instrumentService, HistoricalPriceService historicalPriceService)
      throws Exception {
    checkNotNull(adviserPredicateConfiguration, "adviserPredicateConfiguration is null");
    checkNotNull(instrumentId, "instrumentId is null.");
    checkNotNull(instrumentService, "instrumentService is null.");
    checkNotNull(historicalPriceService, "historicalPriceService is null.");
    try {
      PredicateExecutionContext predicateExecutionContext = new PredicateExecutionContext(
          historicalPriceService,
          instrumentService,
          adviserPredicateConfiguration.getParameters(),
          instrumentId);
      Class clazz = getClass(adviserPredicateConfiguration.getClassName());
      return (AdviserPredicate) clazz.getConstructor(PredicateExecutionContext.class)
          .newInstance(predicateExecutionContext);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new Exception(String.format(
          "[%s] exception while loading predicate [%s] with parameters [%s]: [%s].",
          e.getClass(),
          adviserPredicateConfiguration.getClassName(),
          adviserPredicateConfiguration.getParameters(),
          e.getMessage()),
          e);
    }
  }

  private static Class getClass(String className) throws ClassNotFoundException {
    return AdviserFactory.class.getClassLoader().loadClass(className);
  }
}
