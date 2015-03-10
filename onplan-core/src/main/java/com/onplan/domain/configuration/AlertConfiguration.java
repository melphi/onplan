package com.onplan.domain.configuration;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.onplan.adviser.SeverityLevel;
import com.onplan.domain.persistent.PersistentObject;

public class AlertConfiguration implements PersistentObject {
  private String id;
  private String message;
  private String instrumentId;
  private SeverityLevel severityLevel;
  private Iterable<AdviserPredicateConfiguration> predicatesChain;
  private long createOn;
  private boolean repeat;

  public Iterable<AdviserPredicateConfiguration> getPredicatesChain() {
    return predicatesChain;
  }

  public void setPredicatesChain(Iterable<AdviserPredicateConfiguration> predicatesChain) {
    this.predicatesChain = predicatesChain;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public void setInstrumentId(String instrumentId) {
    this.instrumentId = instrumentId;
  }

  public long getCreateOn() {
    return createOn;
  }

  public void setCreateOn(long createOn) {
    this.createOn = createOn;
  }

  public boolean getRepeat() {
    return repeat;
  }

  public void setRepeat(boolean repeat) {
    this.repeat = repeat;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public SeverityLevel getSeverityLevel() {
    return severityLevel;
  }

  public void setSeverityLevel(SeverityLevel severityLevel) {
    this.severityLevel = severityLevel;
  }

  public AlertConfiguration() {
    // Intentionally empty.
  }

  public AlertConfiguration(String id, String message, SeverityLevel severityLevel,
      String instrumentId, Iterable<AdviserPredicateConfiguration> predicatesChain, long createOn,
      boolean repeat) {
    this.id = id;
    this.message = message;
    this.severityLevel = severityLevel;
    this.instrumentId = instrumentId;
    this.predicatesChain = predicatesChain;
    this.createOn = createOn;
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
    AlertConfiguration alertConfiguration = (AlertConfiguration) o;
    return Objects.equal(this.id, alertConfiguration.id) &&
        Objects.equal(this.message, alertConfiguration.message) &&
        Objects.equal(this.severityLevel, alertConfiguration.severityLevel) &&
        Objects.equal(this.predicatesChain, alertConfiguration.predicatesChain) &&
        Objects.equal(this.instrumentId, alertConfiguration.instrumentId) &&
        Objects.equal(this.createOn, alertConfiguration.createOn) &&
        Objects.equal(this.repeat, alertConfiguration.repeat);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        id, message, severityLevel, predicatesChain, instrumentId, createOn, repeat);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("message", message)
        .add("severityLevel", severityLevel)
        .add("predicatesChain", predicatesChain)
        .add("instrumentId", instrumentId)
        .add("createOn", createOn)
        .add("repeat", repeat)
        .toString();
  }
}
