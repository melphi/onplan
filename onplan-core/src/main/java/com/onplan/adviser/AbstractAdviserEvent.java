package com.onplan.adviser;

import com.onplan.domain.persistent.PersistentObject;
import com.onplan.domain.persistent.PriceTick;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

/**
 * Basic class for adviser events.
 */
public abstract class AbstractAdviserEvent implements AdviserEvent, PersistentObject {
  protected String id;
  protected String adviserId;
  protected PriceTick priceTick;
  protected long createdOn;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getAdviserId() {
    return adviserId;
  }

  public void setAdviserId(String adviserId) {
    this.adviserId = adviserId;
  }

  @Override
  public PriceTick getPriceTick() {
    return priceTick;
  }

  public void setPriceTick(PriceTick priceTick) {
    this.priceTick = priceTick;
  }

  @Override
  public long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(long createdOn) {
    this.createdOn = createdOn;
  }

  public AbstractAdviserEvent() {
    // Intentionally empty.
  }

  public AbstractAdviserEvent(String adviserId, PriceTick priceTick, long createdOn) {
    this.adviserId = checkNotNullOrEmpty(adviserId);
    this.priceTick = checkNotNull(priceTick);
    checkArgument(createdOn > 0, "Expected a positive value for createdOn.");
    this.createdOn = createdOn;
  }
}
