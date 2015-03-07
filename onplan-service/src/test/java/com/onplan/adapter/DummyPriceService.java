package com.onplan.adapter;

import com.onplan.domain.persistent.PriceTick;
import com.onplan.service.ServiceConnectionInfo;

public class DummyPriceService implements PriceService {
  @Override
  public boolean isConnected() {
    return false;
  }

  @Override
  public ServiceConnectionInfo getServiceConnectionInfo() {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public boolean isInstrumentSubscribed(String instrumentId) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public PriceTick getLastPriceTick(String instrumentId) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void setPriceListener(PriceListener listener) {
    // Intentionally empty.
  }

  @Override
  public void subscribeInstrument(String instrumentId) throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void unSubscribeInstrument(String instrumentId) throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
