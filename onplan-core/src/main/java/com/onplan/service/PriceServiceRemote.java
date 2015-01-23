package com.onplan.service;

import com.onplan.domain.PriceTick;

import java.io.Serializable;
import java.util.Collection;

public interface PriceServiceRemote extends Serializable {
  public boolean isConnected();
  public ServiceConnectionInfo getServiceConnectionInfo();
  public Collection<String> getSubscribedInstruments();
  public PriceTick getLastPriceTick(String instrumentId);
}
