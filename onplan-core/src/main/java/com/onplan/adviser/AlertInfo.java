package com.onplan.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * Contains information about an alert.
 */
public final class AlertInfo implements Serializable {
  private String id;
  private String instrumentId;
  private Iterable<AdviserPredicateInfo> predicatesChainInfo;
  private String message;
  private long createdOn;
  private long lastFiredOn;
  private boolean repeat;
  private SeverityLevel severityLevel;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public void setInstrumentId(String instrumentId) {
    this.instrumentId = instrumentId;
  }

  public Iterable<AdviserPredicateInfo> getPredicatesChainInfo() {
    return predicatesChainInfo;
  }

  public void setPredicatesChainInfo(Iterable<AdviserPredicateInfo> predicatesChainInfo) {
    this.predicatesChainInfo = predicatesChainInfo;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(long createdOn) {
    this.createdOn = createdOn;
  }

  public long getLastFiredOn() {
    return lastFiredOn;
  }

  public void setLastFiredOn(long lastFiredOn) {
    this.lastFiredOn = lastFiredOn;
  }

  public boolean getRepeat() {
    return repeat;
  }

  public void setRepeat(boolean repeat) {
    this.repeat = repeat;
  }

  public SeverityLevel getSeverityLevel() {
    return severityLevel;
  }

  public void setSeverityLevel(SeverityLevel severityLevel) {
    this.severityLevel = severityLevel;
  }

  public AlertInfo() {
    // Intentionally empty.
  }

  public AlertInfo(String id, String instrumentId, SeverityLevel severityLevel,
                   Iterable<AdviserPredicateInfo> predicatesChainInfo, String message, long createdOn,
                   long lastFiredOn, boolean repeat) {
    this.id = id;
    this.instrumentId = instrumentId;
    this.predicatesChainInfo = predicatesChainInfo;
    this.message = message;
    this.createdOn = createdOn;
    this.lastFiredOn = lastFiredOn;
    this.repeat = repeat;
    this.severityLevel = severityLevel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AlertInfo alertInfo = (AlertInfo) o;
    return Objects.equal(this.id, alertInfo.id) &&
        Objects.equal(this.instrumentId, alertInfo.instrumentId) &&
        Objects.equal(this.severityLevel, alertInfo.severityLevel) &&
        Objects.equal(this.predicatesChainInfo, alertInfo.predicatesChainInfo) &&
        Objects.equal(this.message, alertInfo.message) &&
        Objects.equal(this.createdOn, alertInfo.createdOn) &&
        Objects.equal(this.lastFiredOn, alertInfo.lastFiredOn) &&
        Objects.equal(this.repeat, alertInfo.repeat);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        id, instrumentId, severityLevel, predicatesChainInfo, message, createdOn, lastFiredOn, repeat);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("instrumentId", instrumentId)
        .add("severityLevel", severityLevel)
        .add("predicatesChainInfo", predicatesChainInfo)
        .add("message", message)
        .add("createdOn", createdOn)
        .add("lastFiredOn", lastFiredOn)
        .add("repeat", repeat)
        .toString();
  }
}
