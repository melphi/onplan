package com.onplan.service.impl;

import com.google.common.collect.ImmutableList;
import com.onplan.adapter.HistoricalPriceService;
import com.onplan.adapter.InstrumentService;
import com.onplan.adviser.AdviserListener;
import com.onplan.adviser.alert.Alert;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.adviser.strategy.Strategy;
import com.onplan.adviser.strategy.StrategyExecutionContext;
import com.onplan.adviser.strategy.StrategyListener;
import com.onplan.domain.configuration.adviser.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.adviser.AlertConfiguration;
import com.onplan.domain.configuration.adviser.StrategyConfiguration;

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
      StrategyExecutionContext strategyExecutionContext = StrategyExecutionContext.newBuilder()
          .setStrategyId(strategyConfiguration.getId())
          .setExecutionParameters(strategyConfiguration.getExecutionParameters())
          .setStrategyListener(strategyListener)
          .setInstrumentService(instrumentService)
          .setHistoricalPriceService(historicalPriceService)
          .setRegisteredInstruments(strategyConfiguration.getInstruments())
          .build();
      Class clazz = ClassLoader.getSystemClassLoader().loadClass(strategyConfiguration.getClassName());
      return (Strategy) clazz.getConstructor(StrategyExecutionContext.class)
          .newInstance(strategyExecutionContext);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new Exception(String.format(
          "[%] exception while loading strategy id [%s] for class [%s]: [%s].",
          e,
          strategyConfiguration.getId(),
          strategyConfiguration.getClassName(),
          e.getMessage()));
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
        .setAlertMessage(alertConfiguration.getAlertMessage())
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
      PredicateExecutionContext predicateExecutionContext = PredicateExecutionContext.newBuilder()
          .setExecutionParameters(adviserPredicateConfiguration.getParameters())
          .setInstrumentId(instrumentId)
          .setInstrumentService(instrumentService)
          .setHistoricalPriceService(historicalPriceService)
          .build();
      Class clazz = ClassLoader.getSystemClassLoader()
          .loadClass(adviserPredicateConfiguration.getClassName());
      return (AdviserPredicate) clazz.getConstructor(PredicateExecutionContext.class)
          .newInstance(predicateExecutionContext);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      throw new Exception(String.format(
          "[%] exception while loading predicate [%s] with parameters [%s]: [%s].",
          e,
          adviserPredicateConfiguration.getClassName(),
          adviserPredicateConfiguration.getParameters(),
          e.getMessage()));
    }
  }
}
