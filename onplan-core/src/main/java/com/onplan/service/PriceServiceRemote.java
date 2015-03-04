package com.onplan.service;

import com.onplan.domain.PriceTick;

import java.io.Serializable;

public interface PriceServiceRemote extends Serializable {
  public boolean isConnected();
  public ServiceConnectionInfo getServiceConnectionInfo();
  public boolean isInstrumentSubscribed(String instrumentId);
  public PriceTick getLastPriceTick(String instrumentId);
}
