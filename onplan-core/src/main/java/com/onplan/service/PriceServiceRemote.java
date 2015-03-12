package com.onplan.service;

import com.onplan.domain.transitory.PriceTick;

import java.io.Serializable;

public interface PriceServiceRemote extends Serializable {
  public ServiceConnectionInfo getServiceConnectionInfo();
  public PriceTick getLastPriceTick(String instrumentId);
}
