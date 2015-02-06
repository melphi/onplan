package com.onplan.adviser;

import com.onplan.domain.PriceTick;

public interface AdviserEvent {
  public PriceTick getPriceTick();
  public long getCreatedOn();
}
