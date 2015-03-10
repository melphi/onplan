package com.onplan.adviser.alert;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Iterables;
import com.onplan.adviser.AbstractChainedAdviser;
import com.onplan.adviser.AdviserListener;
import com.onplan.adviser.SeverityLevel;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.domain.persistent.PriceTick;
import org.joda.time.DateTime;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class Alert extends AbstractChainedAdviser<AlertEvent> {
  private final String message;
  private final boolean repeat;
  private final SeverityLevel severityLevel;

  private volatile long lastFiredOn = 0;

  public long getLastFiredOn() {
    return lastFiredOn;
  }

  public boolean getRepeat() {
    return repeat;
  }

  public String getMessage() {
    return message;
  }

  public SeverityLevel getSeverityLevel() {
    return severityLevel;
  }

  private Alert(String id, Iterable<AdviserPredicate> predicatesChain,
      AdviserListener<AlertEvent> adviserListener, String instrumentId, String message,
      boolean repeat, SeverityLevel severityLevel, long createdOn) {
    super(id, predicatesChain, adviserListener, instrumentId, createdOn);
    this.message = checkNotNullOrEmpty(message);
    this.repeat = repeat;
    this.severityLevel = checkNotNull(severityLevel);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  protected Optional<AlertEvent> prepareAdviserEvent(final PriceTick priceTick) {
    // TODO(robertom): Implement a repeat delay to avoid an event for each subsequent tick.
    if (lastFiredOn > 0 && (!repeat)) {
      return Optional.empty();
    } else {
      this.lastFiredOn = DateTime.now().getMillis();
      AlertEvent alertEvent = new AlertEvent(severityLevel, priceTick, lastFiredOn, message);
     return Optional.of(alertEvent);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Alert alert = (Alert) o;
    return Objects.equal(this.id, alert.id) &&
        Objects.equal(this.instrumentId, alert.instrumentId) &&
        Objects.equal(this.predicatesChain, alert.predicatesChain) &&
        Objects.equal(this.createdOn, alert.createdOn) &&
        Objects.equal(this.message, alert.message) &&
        Objects.equal(this.repeat, alert.repeat) &&
        Objects.equal(this.severityLevel, alert.severityLevel);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        id, instrumentId, predicatesChain, createdOn, message, repeat, severityLevel);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("instrumentId", instrumentId)
        .add("predicatesChain", predicatesChain)
        .add("createdOn", createdOn)
        .add("message", message)
        .add("repeat", repeat)
        .add("severityLevel", severityLevel)
        .toString();
  }

  public static class Builder {
    private String id;
    private Iterable<AdviserPredicate> predicatesChain;
    private String alertMessage;
    private AdviserListener<AlertEvent> alertListener;
    private String instrumentId;
    private boolean repeat = false;
    private SeverityLevel severityLevel = SeverityLevel.MEDIUM;
    private long createdOn;

    public Builder setId(String id) {
      this.id = checkNotNullOrEmpty(id);
      return this;
    }

    public Builder setPredicatesChain(Iterable<AdviserPredicate> predicatesChain) {
      checkArgument(!Iterables.isEmpty(predicatesChain), "Predicate chain can not be empty.");
      this.predicatesChain = checkNotNull(predicatesChain);
      return this;
    }

    public Builder setAlertMessage(String alertMessage) {
      this.alertMessage = checkNotNullOrEmpty(alertMessage);
      return this;
    }

    public Builder setAlertListener(AdviserListener<AlertEvent> alertListener) {
      this.alertListener = checkNotNull(alertListener);
      return this;
    }

    public Builder setInstrumentId(String instrumentId) {
      this.instrumentId = checkNotNullOrEmpty(instrumentId);
      return this;
    }

    public Builder setRepeat(boolean repeat) {
      this.repeat = repeat;
      return this;
    }

    public Builder setSeverityLevel(SeverityLevel severityLevel) {
      this.severityLevel = checkNotNull(severityLevel);
      return this;
    }

    public Builder setCreatedOn(long createdOn) {
      checkArgument(createdOn > 0);
      this.createdOn = createdOn;
      return this;
    }

    public Alert build() {
      return new Alert(id, predicatesChain, alertListener, instrumentId, alertMessage, repeat,
          severityLevel, createdOn);
    }
  }
}
