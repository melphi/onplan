package com.onplan.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.adapter.InstrumentService;
import com.onplan.adapter.PriceService;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class StrategyExecutionContext implements Serializable {
  private final String strategyId;
  private final PriceService priceService;
  private final InstrumentService instrumentService;
  private final StrategyListener strategyListener;
  private final Map<String, String> executionParameters;
  private final Set<String> registeredInstruments;

  public static Builder newBuilder() {
    return new Builder();
  }

  public String getStrategyId() {
    return this.strategyId;
  }

  public PriceService getPriceService() {
    return priceService;
  }

  public InstrumentService getInstrumentService() {
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

  private StrategyExecutionContext(String strategyId, PriceService priceService,
      InstrumentService instrumentService, StrategyListener strategyListener,
      Map<String, String> executionParameters, Set<String> registeredInstruments) {
    this.strategyId = checkNotNullOrEmpty(strategyId);
    this.priceService = checkNotNull(priceService);
    this.instrumentService = checkNotNull(instrumentService);
    this.strategyListener = checkNotNull(strategyListener);
    this.executionParameters = checkNotNull(executionParameters);
    this.registeredInstruments = checkNotNull(registeredInstruments);
  }

  public static class Builder {
    private String strategyId;
    private PriceService priceService;
    private InstrumentService instrumentService;
    private StrategyListener strategyListener;
    private Map<String, String> executionParameters;
    private Set<String> registeredInstruments;

    public Builder setStrategyId(String strategyId) {
      this.strategyId = checkNotNullOrEmpty(strategyId);
      return this;
    }

    public Builder setPriceService(PriceService priceService) {
      this.priceService = checkNotNull(priceService);
      return this;
    }

    public Builder setInstrumentService(InstrumentService instrumentService) {
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
      return new StrategyExecutionContext(strategyId, priceService, instrumentService, strategyListener,
          executionParameters, registeredInstruments);
    }
  }
}
