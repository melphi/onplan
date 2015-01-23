package com.onplan.strategy;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.onplan.domain.PriceTick;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class StrategyEvent {
  private final String instrumentId;
  private final String message;
  private final StrategyEventType eventType;
  private final long timestamp;
  private final PriceTick priceTick;
  private final StrategyExecutionContext strategyExecutionContext;

  public String getMessage() {
    return message;
  }

  public StrategyEventType getEventType() {
    return eventType;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public PriceTick priceTick() {
    return priceTick;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public StrategyExecutionContext getStrategyExecutionContext() {
    return strategyExecutionContext;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  private StrategyEvent(String instrumentId, String message, StrategyEventType eventType,
      long timestamp, PriceTick priceTick, StrategyExecutionContext strategyExecutionContext) {
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
    this.message = checkNotNullOrEmpty(message);
    this.eventType = checkNotNull(eventType);
    checkArgument(timestamp > 0);
    this.timestamp = timestamp;
    this.priceTick = checkNotNull(priceTick);
    this.strategyExecutionContext = checkNotNull(strategyExecutionContext);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("instrumentId", instrumentId)
        .add("message", message)
        .add("eventType", eventType)
        .add("timestamp", timestamp)
        .add("priceTick", priceTick)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StrategyEvent priceBar = (StrategyEvent) o;
    return Objects.equal(this.instrumentId, priceBar.instrumentId) &&
        Objects.equal(this.message, priceBar.message) &&
        Objects.equal(this.eventType, priceBar.eventType) &&
        Objects.equal(this.timestamp, priceBar.timestamp) &&
        Objects.equal(this.priceTick, priceBar.priceTick) &&
        Objects.equal(this.strategyExecutionContext, priceBar.strategyExecutionContext);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(instrumentId, message, eventType, timestamp, strategyExecutionContext);
  }

  public static class Builder {
    private String instrumentId;
    private String message;
    private StrategyEventType eventType;
    private long timestamp;
    private PriceTick priceTick;
    private StrategyExecutionContext strategyExecutionContext;

    public Builder setInstrumentId(String instrumentId) {
      this.instrumentId = checkNotNullOrEmpty(instrumentId);
      return this;
    }

    public Builder setMessage(String message) {
      this.message = checkNotNullOrEmpty(message);
      return this;
    }

    public Builder setEventType(StrategyEventType eventType) {
      this.eventType = checkNotNull(eventType);
      return this;
    }

    public Builder setTimestamp(long timestamp) {
      checkArgument(timestamp > 0);
      this.timestamp = timestamp;
      return this;
    }

    public Builder setPriceTick(PriceTick priceTick) {
      this.priceTick = checkNotNull(priceTick);
      return this;
    }

    public Builder setStrategyExecutionContext(StrategyExecutionContext strategyExecutionContext) {
      this.strategyExecutionContext = checkNotNull(strategyExecutionContext);
      return this;
    }

    public StrategyEvent build() {
      return new StrategyEvent(
          instrumentId, message, eventType, timestamp, priceTick, strategyExecutionContext);
    }
  }
}
