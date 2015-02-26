package com.onplan.adviser;

import com.onplan.domain.PriceTick;

import java.io.Serializable;

public interface AdviserEvent extends Serializable {
  public PriceTick getPriceTick();
  public long getCreatedOn();
}
