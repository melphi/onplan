package com.onplan.adviser;

import com.onplan.domain.transitory.PriceTick;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

/**
 * Basic class for adviser events.
 */
public abstract class AbstractAdviserEvent implements AdviserEvent {
  protected final String adviserId;
  protected final PriceTick priceTick;
  protected final long createdOn;

  public String getAdviserId() {
    return adviserId;
  }

  @Override
  public PriceTick getPriceTick() {
    return priceTick;
  }

  @Override
  public long getCreatedOn() {
    return createdOn;
  }

  public AbstractAdviserEvent(String adviserId, PriceTick priceTick, long createdOn) {
    this.adviserId = checkNotNullOrEmpty(adviserId);
    this.priceTick = checkNotNull(priceTick);
    checkArgument(createdOn > 0, "Expected a positive value for createdOn.");
    this.createdOn = createdOn;
  }
}
