package com.onplan.domain.persistent;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.onplan.adviser.AbstractAdviserEvent;
import com.onplan.adviser.SeverityLevel;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class AlertEvent extends AbstractAdviserEvent {
  private String message;
  private SeverityLevel severityLevel;

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

  public AlertEvent() {
    // Intentionally empty.
  }

  public AlertEvent(String adviserId, SeverityLevel severityLevel, PriceTick priceTick,
      long createdOn, String message) {
    super(adviserId, priceTick, createdOn);
    this.message = checkNotNullOrEmpty(message);
    this.severityLevel = checkNotNull(severityLevel);
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
    AlertEvent alertEvent = (AlertEvent) o;
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
