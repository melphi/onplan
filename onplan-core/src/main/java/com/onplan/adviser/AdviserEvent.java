package com.onplan.adviser;

import com.onplan.domain.persistent.PriceTick;

import java.io.Serializable;

/**
 * Generic adviser event interface.
 */
public interface AdviserEvent extends Serializable {
  public PriceTick getPriceTick();
  public long getCreatedOn();
}
