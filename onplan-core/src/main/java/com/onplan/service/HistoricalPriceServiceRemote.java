package com.onplan.service;

import com.onplan.domain.PriceBar;

import java.io.Serializable;
import java.util.Collection;

public interface HistoricalPriceServiceRemote extends Serializable {
  public ServiceConnectionInfo getServiceConnectionInfo();
  public Collection<PriceBar> getHistoricalPriceBars(
      String instrumentId, long barPeriod, long startTimestamp, long endTimestamp);
}
