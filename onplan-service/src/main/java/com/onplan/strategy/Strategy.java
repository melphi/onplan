package com.onplan.strategy;

import com.onplan.domain.PriceTick;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface Strategy extends Serializable {
  public String getId();
  public void processPriceTick(final PriceTick priceTick);
  public void setExecutionContext(StrategyExecutionContext strategyExecutionContext);
  public void initStrategy() throws Exception;
  public Map<String, String> getExecutionParameters();
  public Collection<String> getRegisteredInstruments();
}
