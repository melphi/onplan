package com.onplan.adviser.predicate;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.onplan.service.HistoricalPriceServiceRemote;
import com.onplan.service.InstrumentServiceRemote;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class PredicateExecutionContext {
  private final HistoricalPriceServiceRemote historicalPriceService;
  private final InstrumentServiceRemote instrumentService;
  private final Map<String, String> parameters;
  private final String instrumentId;

  public PredicateExecutionContext(HistoricalPriceServiceRemote historicalPriceService,
      InstrumentServiceRemote instrumentService, Map<String, String> executionParameters,
      String instrumentId) {
    this.historicalPriceService = checkNotNull(historicalPriceService);
    this.instrumentService = checkNotNull(instrumentService);
    this.parameters = ImmutableMap.copyOf(checkNotNull(executionParameters));
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
  }

  public HistoricalPriceServiceRemote getHistoricalPriceService() {
    return historicalPriceService;
  }

  public InstrumentServiceRemote getInstrumentService() {
    return instrumentService;
  }

  public String getParameterValue(String parameterName) {
    return parameters.get(parameterName);
  }

  public Map<String, String> getParametersCopy() {
    return ImmutableMap.copyOf(parameters);
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PredicateExecutionContext executionContext = (PredicateExecutionContext) o;
    return Objects.equal(this.historicalPriceService, executionContext.historicalPriceService) &&
        Objects.equal(this.instrumentService, executionContext.instrumentService) &&
        Objects.equal(this.instrumentId, executionContext.instrumentId) &&
        Objects.equal(this.parameters, executionContext.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        historicalPriceService, instrumentService, instrumentId, parameters);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("instrumentId", instrumentId)
        .add("parameters", parameters)
        .toString();
  }
}
