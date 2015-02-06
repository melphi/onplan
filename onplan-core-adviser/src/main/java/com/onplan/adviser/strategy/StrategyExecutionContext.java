package com.onplan.adviser.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.service.HistoricalPriceServiceRemote;
import com.onplan.service.InstrumentServiceRemote;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class StrategyExecutionContext implements Serializable {
  private final String strategyId;
  private final HistoricalPriceServiceRemote historicalPriceService;
  private final InstrumentServiceRemote instrumentService;
  private final StrategyListener strategyListener;
  private final Map<String, String> executionParameters;
  private final Set<String> registeredInstruments;

  public static Builder newBuilder() {
    return new Builder();
  }

  public String getStrategyId() {
    return this.strategyId;
  }

  public HistoricalPriceServiceRemote getHistoricalPriceService() {
    return historicalPriceService;
  }

  public InstrumentServiceRemote getInstrumentService() {
    return instrumentService;
  }

  public StrategyListener getStrategyListener() {
    return strategyListener;
  }

  public Map<String, String> getExecutionParameters() {
    return executionParameters;
  }

  public Set<String> getRegisteredInstruments() {
    return registeredInstruments;
  }

  private StrategyExecutionContext(String strategyId,
      HistoricalPriceServiceRemote historicalPriceService,
      InstrumentServiceRemote instrumentService, StrategyListener strategyListener,
      Map<String, String> executionParameters, Set<String> registeredInstruments) {
    this.strategyId = checkNotNullOrEmpty(strategyId);
    this.historicalPriceService = checkNotNull(historicalPriceService);
    this.instrumentService = checkNotNull(instrumentService);
    this.strategyListener = checkNotNull(strategyListener);
    this.executionParameters = checkNotNull(executionParameters);
    this.registeredInstruments = checkNotNull(registeredInstruments);
  }

  public static class Builder {
    private String strategyId;
    private HistoricalPriceServiceRemote historicalPriceService;
    private InstrumentServiceRemote instrumentService;
    private StrategyListener strategyListener;
    private Map<String, String> executionParameters;
    private Set<String> registeredInstruments;

    public Builder setStrategyId(String strategyId) {
      this.strategyId = checkNotNullOrEmpty(strategyId);
      return this;
    }

    public Builder setHistoricalPriceService(HistoricalPriceServiceRemote historicalPriceService) {
      this.historicalPriceService = checkNotNull(historicalPriceService);
      return this;
    }

    public Builder setInstrumentService(InstrumentServiceRemote instrumentService) {
      this.instrumentService = checkNotNull(instrumentService);
      return this;
    }

    public Builder setStrategyListener(StrategyListener strategyListener) {
      this.strategyListener = checkNotNull(strategyListener);
      return this;
    }

    public Builder setExecutionParameters(Map<String, String> executionParameters) {
      this.executionParameters = ImmutableMap.copyOf(checkNotNull(executionParameters));
      return this;
    }

    public Builder setRegisteredInstruments(Set<String> registeredInstruments) {
      this.registeredInstruments = ImmutableSet.copyOf(checkNotNull(registeredInstruments));
      return this;
    }

    public StrategyExecutionContext build() {
      return new StrategyExecutionContext(strategyId, historicalPriceService, instrumentService,
          strategyListener, executionParameters, registeredInstruments);
    }
  }
}
