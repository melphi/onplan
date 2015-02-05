package com.onplan.adviser.predicate;

import com.onplan.service.DummiHistoricalPriceService;
import com.onplan.service.DummyInstrumentService;

import java.util.Map;

public class PredicateExecutionContextFactory {
  public static PredicateExecutionContext createPredicateExecutionContext(
      Map<String, String> executionParameters, String instrumentId) {
    return PredicateExecutionContext.newBuilder()
        .setExecutionParameters(executionParameters)
        .setInstrumentService(new DummyInstrumentService())
        .setHistoricalPriceService(new DummiHistoricalPriceService())
        .setInstrumentId(instrumentId)
        .build();
  }
}
