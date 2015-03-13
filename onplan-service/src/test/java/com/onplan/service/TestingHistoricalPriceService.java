package com.onplan.service;

import com.onplan.connector.HistoricalPriceService;
import com.onplan.domain.transitory.PriceBar;

import java.util.Collection;

import static com.onplan.util.TestingConstants.DEFAULT_SERVICE_CONNECTION_INFO;

public class TestingHistoricalPriceService implements HistoricalPriceService {
  @Override
  public ServiceConnectionInfo getServiceConnectionInfo() {
    return DEFAULT_SERVICE_CONNECTION_INFO;
  }

  @Override
  public Collection<PriceBar> getHistoricalPriceBars(
      String instrumentId, long barPeriod, long startTimestamp, long endTimestamp) {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
