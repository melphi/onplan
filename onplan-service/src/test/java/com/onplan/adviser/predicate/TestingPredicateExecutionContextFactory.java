package com.onplan.adviser.predicate;

import com.onplan.service.TestingHistoricalPriceService;
import com.onplan.service.TestingInstrumentService;

import java.util.Map;

public class TestingPredicateExecutionContextFactory {
  public static PredicateExecutionContext createPredicateExecutionContext(
      Map<String, String> executionParameters, String instrumentId) {
    return PredicateExecutionContext.newBuilder()
        .setExecutionParameters(executionParameters)
        .setInstrumentService(new TestingInstrumentService())
        .setHistoricalPriceService(new TestingHistoricalPriceService())
        .setInstrumentId(instrumentId)
        .build();
  }
}
