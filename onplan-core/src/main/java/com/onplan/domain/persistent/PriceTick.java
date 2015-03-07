package com.onplan.domain.persistent;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class PriceTick implements Price {
  private final String instrumentId;
  private final long timestamp;
  private final double closePriceAsk;
  private final double closePriceBid;

  private transient final long receivedNanoTime;

  public String getInstrumentId() {
    return instrumentId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public double getClosePriceAsk() {
    return closePriceAsk;
  }

  public double getClosePriceBid() {
    return closePriceBid;
  }

  public long getReceivedNanoTime() {
    return receivedNanoTime;
  }

  public PriceTick(
      String instrumentID, long timestamp, double closePriceAsk, double closePriceBid) {
    this.instrumentId = checkNotNullOrEmpty(instrumentID);
    this.timestamp = timestamp;
    this.closePriceAsk = closePriceAsk;
    this.closePriceBid = closePriceBid;
    this.receivedNanoTime = System.nanoTime();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceTick priceTick = (PriceTick) o;
    return Objects.equal(this.instrumentId, priceTick.instrumentId) &&
        Objects.equal(this.timestamp, priceTick.timestamp) &&
        Objects.equal(this.closePriceAsk, priceTick.closePriceAsk) &&
        Objects.equal(this.closePriceBid, priceTick.closePriceBid);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(instrumentId, timestamp, closePriceAsk, closePriceBid);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("instrumentId", instrumentId)
        .add("timestamp", timestamp)
        .add("closePriceAsk", closePriceAsk)
        .add("closePriceBid", closePriceBid)
        .toString();
  }
}
