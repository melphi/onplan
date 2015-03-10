package com.onplan.adviser.strategy;

import com.onplan.service.TestingHistoricalPriceService;
import com.onplan.service.TestingInstrumentService;

import java.util.Map;
import java.util.Set;

import static com.onplan.util.TestingConstants.DEFAULT_STRATEGY_ID;

public class TestingStrategyExecutionContextFactory {
  public static StrategyExecutionContext createStrategyExecutionContext(
      StrategyListener strategyListener, Map<String, String> executionParameters,
      Set<String> subscribedInstruments) {
    return StrategyExecutionContext.newBuilder()
        .setStrategyId(DEFAULT_STRATEGY_ID)
        .setExecutionParameters(executionParameters)
        .setStrategyListener(strategyListener)
        .setInstrumentService(new TestingInstrumentService())
        .setHistoricalPriceService(new TestingHistoricalPriceService())
        .setRegisteredInstruments(subscribedInstruments)
        .build();
  }
}
