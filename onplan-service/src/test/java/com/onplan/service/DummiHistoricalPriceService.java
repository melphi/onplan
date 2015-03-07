package com.onplan.service;

import com.onplan.adapter.HistoricalPriceService;
import com.onplan.domain.persistent.PriceBar;

import java.util.Collection;

public class DummiHistoricalPriceService implements HistoricalPriceService {
  @Override
  public ServiceConnectionInfo getServiceConnectionInfo() {
    return null;
  }

  @Override
  public Collection<PriceBar> getHistoricalPriceBars(
      String instrumentId, long barPeriod, long startTimestamp, long endTimestamp) {
    return null;
  }
}
