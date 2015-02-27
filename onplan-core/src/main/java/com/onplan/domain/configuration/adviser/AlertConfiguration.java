package com.onplan.domain.configuration.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.onplan.domain.PersistentObject;

public class AlertConfiguration implements PersistentObject {
  private String id;
  private String alertMessage;
  private String instrumentId;
  private Iterable<AdviserPredicateConfiguration> predicatesChain;
  private long createOn;
  private boolean repeat;

  public Iterable<AdviserPredicateConfiguration> getPredicatesChain() {
    return predicatesChain;
  }

  public void setPredicatesChain(Iterable<AdviserPredicateConfiguration> predicatesChain) {
    this.predicatesChain = predicatesChain;
  }

  public String getAlertMessage() {
    return alertMessage;
  }

  public void setAlertMessage(String alertMessage) {
    this.alertMessage = alertMessage;
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

  public AlertConfiguration() {
    // Intentionally empty.
  }

  public AlertConfiguration(String id, String alertMessage, String instrumentId,
      Iterable<AdviserPredicateConfiguration> predicatesChain, long createOn, boolean repeat) {
    this.id = id;
    this.alertMessage = alertMessage;
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
        Objects.equal(this.alertMessage, alertConfiguration.alertMessage) &&
        Objects.equal(this.predicatesChain, alertConfiguration.predicatesChain) &&
        Objects.equal(this.instrumentId, alertConfiguration.instrumentId) &&
        Objects.equal(this.createOn, alertConfiguration.createOn) &&
        Objects.equal(this.repeat, alertConfiguration.repeat);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, alertMessage, predicatesChain, instrumentId, createOn, repeat);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("alertMessage", alertMessage)
        .add("predicatesChain", predicatesChain)
        .add("instrumentId", instrumentId)
        .add("createOn", createOn)
        .add("repeat", repeat)
        .toString();
  }
}
