package com.onplan.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class PriceBar implements Price {
  private final String instrumentId;
  private final long timestamp;
  private final int intervalMilliseconds;
  private final double openPriceAsk;
  private final double openPriceBid;
  private final double closePriceAsk;
  private final double closePriceBid;
  private final double lowPriceAsk;
  private final double lowPriceBid;
  private final double highPriceAsk;
  private final double highPriceBid;

  public String getInstrumentId() {
    return instrumentId;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public int getIntervalMilliseconds() {
    return intervalMilliseconds;
  }

  public double getOpenPriceAsk() {
    return openPriceAsk;
  }

  public double getOpenPriceBid() {
    return openPriceBid;
  }

  public double getClosePriceAsk() {
    return closePriceAsk;
  }

  public double getClosePriceBid() {
    return closePriceBid;
  }

  public double getLowPriceAsk() {
    return lowPriceAsk;
  }

  public double getLowPriceBid() {
    return lowPriceBid;
  }

  public double getHighPriceAsk() {
    return highPriceAsk;
  }

  public double getHighPriceBid() {
    return highPriceBid;
  }

  public PriceBar(String instrumentID, long timestamp, int intervalSeconds, double openPriceAsk,
      double openPriceBid, double closePriceAsk, double closePriceBid, double lowPriceAsk,
      double lowPriceBid, double highPriceAsk, double highPriceBid) {
    this.instrumentId = checkNotNullOrEmpty(instrumentID);
    this.timestamp = timestamp;
    this.intervalMilliseconds = intervalSeconds;
    this.openPriceAsk = openPriceAsk;
    this.openPriceBid = openPriceBid;
    this.closePriceAsk = closePriceAsk;
    this.closePriceBid = closePriceBid;
    this.lowPriceAsk = lowPriceAsk;
    this.lowPriceBid = lowPriceBid;
    this.highPriceAsk = highPriceAsk;
    this.highPriceBid = highPriceBid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PriceBar priceBar = (PriceBar) o;
    return Objects.equal(this.instrumentId, priceBar.instrumentId) &&
        Objects.equal(this.timestamp, priceBar.timestamp) &&
        Objects.equal(this.openPriceAsk, priceBar.openPriceAsk) &&
        Objects.equal(this.openPriceBid, priceBar.openPriceBid) &&
        Objects.equal(this.closePriceAsk, priceBar.closePriceAsk) &&
        Objects.equal(this.closePriceBid, priceBar.closePriceBid) &&
        Objects.equal(this.highPriceAsk, priceBar.highPriceAsk) &&
        Objects.equal(this.highPriceBid, priceBar.highPriceBid) &&
        Objects.equal(this.lowPriceAsk, priceBar.lowPriceAsk) &&
        Objects.equal(this.lowPriceBid, priceBar.lowPriceBid);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(instrumentId, intervalMilliseconds, timestamp, openPriceAsk, openPriceBid, closePriceAsk,
        closePriceBid, highPriceAsk, highPriceBid, lowPriceAsk, lowPriceBid);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("instrumentId", instrumentId)
        .add("intervalMilliseconds", intervalMilliseconds)
        .add("timestamp", timestamp)
        .add("openPriceAsk", openPriceAsk)
        .add("openPriceBid", openPriceBid)
        .add("closePriceAsk", closePriceAsk)
        .add("closePriceBid", closePriceBid)
        .add("highPriceAsk", highPriceAsk)
        .add("highPriceBid", highPriceBid)
        .add("lowPriceAsk", lowPriceAsk)
        .add("lowPriceBid", lowPriceBid)
        .toString();
  }
}
