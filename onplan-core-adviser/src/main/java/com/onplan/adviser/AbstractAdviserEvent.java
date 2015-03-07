package com.onplan.adviser;

import com.onplan.domain.persistent.PriceTick;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractAdviserEvent implements AdviserEvent {
  private final PriceTick priceTick;
  private final long createdOn;

  protected AbstractAdviserEvent(PriceTick priceTick, long createdOn) {
    this.priceTick = checkNotNull(priceTick);
    checkArgument(createdOn > 0, "Expected a positive value for createdOn.");
    this.createdOn = createdOn;
  }

  public PriceTick getPriceTick() {
    return priceTick;
  }

  public long getCreatedOn() {
    return createdOn;
  }
}
