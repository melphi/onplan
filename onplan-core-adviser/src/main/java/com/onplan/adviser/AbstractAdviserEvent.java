package com.onplan.adviser;

import com.onplan.domain.persistent.PriceTick;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractAdviserEvent implements AdviserEvent {
  private final String adviserId;
  private final PriceTick priceTick;
  private final long createdOn;

  protected AbstractAdviserEvent(String adviserId, PriceTick priceTick, long createdOn) {
    this.adviserId = checkNotNullOrEmpty(adviserId);
    this.priceTick = checkNotNull(priceTick);
    checkArgument(createdOn > 0, "Expected a positive value for createdOn.");
    this.createdOn = createdOn;
  }

  public String getAdviserId() {
    return adviserId;
  }

  public PriceTick getPriceTick() {
    return priceTick;
  }

  public long getCreatedOn() {
    return createdOn;
  }
}
