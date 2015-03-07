package com.onplan.service;

import com.onplan.domain.persistent.PriceTick;

import java.io.Serializable;

public interface PriceServiceRemote extends Serializable {
  public ServiceConnectionInfo getServiceConnectionInfo();
  public PriceTick getLastPriceTick(String instrumentId);
}
