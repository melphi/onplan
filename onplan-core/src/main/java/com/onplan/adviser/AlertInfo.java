package com.onplan.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public final class AlertInfo implements Serializable {
  private String id;
  private String instrumentId;
  private Iterable<AdviserPredicateInfo> predicatesInfo;
  private String message;
  private long createdOn;
  private long lastFiredOn;
  private boolean repeat;

  public AlertInfo() {
    // Intentionally empty.
  }

  public AlertInfo(String id, String instrumentId, Iterable<AdviserPredicateInfo> predicatesInfo,
      String message, long createdOn, long lastFiredOn, boolean repeat) {
    this.id = id;
    this.instrumentId = instrumentId;
    this.predicatesInfo = predicatesInfo;
    this.message = message;
    this.createdOn = createdOn;
    this.lastFiredOn = lastFiredOn;
    this.repeat = repeat;
  }

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

  public Iterable<AdviserPredicateInfo> getPredicatesInfo() {
    return predicatesInfo;
  }

  public void setPredicatesInfo(Iterable<AdviserPredicateInfo> predicatesInfo) {
    this.predicatesInfo = predicatesInfo;
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

  public boolean isRepeat() {
    return repeat;
  }

  public void setRepeat(boolean repeat) {
    this.repeat = repeat;
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
        Objects.equal(this.predicatesInfo, alertInfo.predicatesInfo) &&
        Objects.equal(this.message, alertInfo.message) &&
        Objects.equal(this.createdOn, alertInfo.createdOn) &&
        Objects.equal(this.lastFiredOn, alertInfo.lastFiredOn) &&
        Objects.equal(this.repeat, alertInfo.repeat);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        id, instrumentId, predicatesInfo, message, createdOn, lastFiredOn, repeat);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("instrumentId", instrumentId)
        .add("predicatesInfo", predicatesInfo)
        .add("message", message)
        .add("createdOn", createdOn)
        .add("lastFiredOn", lastFiredOn)
        .add("repeat", repeat)
        .toString();
  }
}
