package com.onplan.domain.persistent;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.onplan.adviser.SeverityLevel;
import com.onplan.domain.transitory.PriceTick;

public final class AlertEventHistory implements PersistentObject {
  private String id;
  private String adviserId;
  private PriceTick priceTick;
  private String message;
  private SeverityLevel severityLevel;
  private long createdOn;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public SeverityLevel getSeverityLevel() {
    return severityLevel;
  }

  public void setSeverityLevel(SeverityLevel severityLevel) {
    this.severityLevel = severityLevel;
  }

  public String getAdviserId() {
    return adviserId;
  }

  public void setAdviserId(String adviserId) {
    this.adviserId = adviserId;
  }

  public PriceTick getPriceTick() {
    return priceTick;
  }

  public void setPriceTick(PriceTick priceTick) {
    this.priceTick = priceTick;
  }

  public long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(long createdOn) {
    this.createdOn = createdOn;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public AlertEventHistory() {
    // Intentionally empty.
  }

  public AlertEventHistory(String id, String adviserId, SeverityLevel severityLevel,
      PriceTick priceTick, long createdOn, String message) {
    this.adviserId = adviserId;
    this.severityLevel = severityLevel;
    this.priceTick = priceTick;
    this.message = message;
    this.createdOn = createdOn;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, adviserId, priceTick, createdOn, message, severityLevel);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AlertEventHistory alertEvent = (AlertEventHistory) o;
    return Objects.equal(this.id, alertEvent.id) &&
        Objects.equal(this.adviserId, alertEvent.adviserId) &&
        Objects.equal(this.createdOn, alertEvent.createdOn) &&
        Objects.equal(this.message, alertEvent.message) &&
        Objects.equal(this.severityLevel, alertEvent.severityLevel) &&
        Objects.equal(this.priceTick, alertEvent.priceTick);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("adviserId", adviserId)
        .add("createdOn", createdOn)
        .add("message", message)
        .add("severityLevel", severityLevel)
        .add("priceTick", priceTick)
        .toString();
  }
}
