package com.onplan.adviser.predicate;

import com.google.common.collect.ImmutableMap;
import com.onplan.adapter.HistoricalPriceService;
import com.onplan.adapter.InstrumentService;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class PredicateExecutionContext {
  private final HistoricalPriceService historicalPriceService;
  private final InstrumentService instrumentService;
  private final Map<String, String> executionParameters;
  private final String instrumentId;

  public static Builder newBuilder() {
    return new Builder();
  }

  private PredicateExecutionContext(HistoricalPriceService historicalPriceService,
      InstrumentService instrumentService, Map<String, String> executionParameters,
      String instrumentId) {
    this.historicalPriceService = checkNotNull(historicalPriceService);
    this.instrumentService = checkNotNull(instrumentService);
    this.executionParameters = ImmutableMap.copyOf(checkNotNull(executionParameters));
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
  }

  public HistoricalPriceService getHistoricalPriceService() {
    return historicalPriceService;
  }

  public InstrumentService getInstrumentService() {
    return instrumentService;
  }

  public Map<String, String> getExecutionParameters() {
    return executionParameters;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public static class Builder {
    private HistoricalPriceService historicalPriceService;
    private InstrumentService instrumentService;
    private Map<String, String> executionParameters;
    private String instrumentId;

    public Builder setHistoricalPriceService(HistoricalPriceService historicalPriceService) {
      this.historicalPriceService = checkNotNull(historicalPriceService);
      return this;
    }

    public Builder setInstrumentService(InstrumentService instrumentService) {
      this.instrumentService = checkNotNull(instrumentService);
      return this;
    }

    public Builder setExecutionParameters(Map<String, String> executionParameters) {
      this.executionParameters = checkNotNull(executionParameters);
      return this;
    }

    public Builder setInstrumentId(String instrumentId) {
      this.instrumentId = checkNotNullOrEmpty(instrumentId);
      return this;
    }

    public PredicateExecutionContext build() {
      return new PredicateExecutionContext(
          historicalPriceService, instrumentService, executionParameters, instrumentId);
    }
  }
}
