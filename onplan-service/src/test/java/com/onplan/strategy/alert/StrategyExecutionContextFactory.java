package com.onplan.strategy.alert;

import com.onplan.adapter.DummyPriceService;
import com.onplan.service.DummyInstrumentService;
import com.onplan.strategy.StrategyExecutionContext;
import com.onplan.strategy.StrategyListener;

import java.util.Map;
import java.util.Set;

public class StrategyExecutionContextFactory {
  private static final String STRATEGY_ID = "2j8Jl20jHJ";

  public static StrategyExecutionContext createStrategyExecutionContext(
      StrategyListener strategyListener, Map<String, String> executionParameters,
      Set<String> subscribedInstruments) {
    return StrategyExecutionContext.newBuilder()
        .setStrategyId(STRATEGY_ID)
        .setExecutionParameters(executionParameters)
        .setStrategyListener(strategyListener)
        .setInstrumentService(new DummyInstrumentService())
        .setPriceService(new DummyPriceService())
        .setRegisteredInstruments(subscribedInstruments)
        .build();
  }
}
