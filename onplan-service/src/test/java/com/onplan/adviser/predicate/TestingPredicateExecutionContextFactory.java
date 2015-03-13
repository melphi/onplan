package com.onplan.adviser.predicate;

import com.onplan.service.TestingHistoricalPriceService;
import com.onplan.service.TestingInstrumentService;

import java.util.Map;

public class TestingPredicateExecutionContextFactory {
  public static PredicateExecutionContext createPredicateExecutionContext(
      Map<String, String> executionParameters, String instrumentId) {
    return new PredicateExecutionContext(
        new TestingHistoricalPriceService(),
        new TestingInstrumentService(),
        executionParameters,
        instrumentId);
  }
}
