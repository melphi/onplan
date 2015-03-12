package com.onplan.adviser.alert;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.onplan.adviser.AbstractAdviserEvent;
import com.onplan.adviser.SeverityLevel;
import com.onplan.domain.transitory.PriceTick;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class AlertEvent extends AbstractAdviserEvent {
  private final String message;
  private final SeverityLevel severityLevel;

  public String getMessage() {
    return message;
  }

  public SeverityLevel getSeverityLevel() {
    return severityLevel;
  }

  public AlertEvent(String adviserId, SeverityLevel severityLevel, PriceTick priceTick,
      long createdOn, String message) {
    super(adviserId, priceTick, createdOn);
    this.message = checkNotNullOrEmpty(message);
    this.severityLevel = checkNotNull(severityLevel);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(adviserId, priceTick, createdOn, message, severityLevel);
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
    return Objects.equal(this.adviserId, alertEvent.adviserId) &&
        Objects.equal(this.createdOn, alertEvent.createdOn) &&
        Objects.equal(this.message, alertEvent.message) &&
        Objects.equal(this.severityLevel, alertEvent.severityLevel) &&
        Objects.equal(this.priceTick, alertEvent.priceTick);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("adviserId", adviserId)
        .add("createdOn", createdOn)
        .add("message", message)
        .add("severityLevel", severityLevel)
        .add("priceTick", priceTick)
        .toString();
  }
}
