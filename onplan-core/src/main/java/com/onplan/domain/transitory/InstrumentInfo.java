package com.onplan.domain.transitory;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.onplan.domain.InstrumentType;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class InstrumentInfo implements Serializable {
  private final String instrumentId;
  private final String instrumentName;
  private final int priceMinimalDecimalPosition;
  private final InstrumentType instrumentType;
  private final String expiry;

  public InstrumentInfo(String instrumentId, String instrumentName, InstrumentType instrumentType,
      int priceMinimalDecimalPosition, String expiry) {
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
    this.instrumentName = checkNotNullOrEmpty(instrumentName);
    this.instrumentType = checkNotNull(instrumentType);
    this.priceMinimalDecimalPosition = priceMinimalDecimalPosition;
    this.expiry = checkNotNullOrEmpty(expiry);
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public int getPriceMinimalDecimalPosition() {
    return priceMinimalDecimalPosition;
  }

  public String getInstrumentName() {
    return instrumentName;
  }

  public InstrumentType getInstrumentType() {
    return instrumentType;
  }

  public String getExpiry() {
    return expiry;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InstrumentInfo instrumentInfo = (InstrumentInfo) o;
    return Objects.equal(this.instrumentId, instrumentInfo.instrumentId) &&
        Objects.equal(this.instrumentName, instrumentInfo.instrumentName) &&
        Objects.equal(this.instrumentType, instrumentInfo.instrumentType) &&
        Objects.equal(this.expiry, instrumentInfo.expiry) &&
        Objects.equal(this.priceMinimalDecimalPosition, instrumentInfo.priceMinimalDecimalPosition);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        instrumentId, instrumentName, instrumentType, expiry, priceMinimalDecimalPosition);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("instrumentId", instrumentId)
        .add("instrumentName", instrumentName)
        .add("instrumentType", instrumentType)
        .add("expiry", expiry)
        .add("priceMinimalDecimalPosition", priceMinimalDecimalPosition)
        .toString();
  }
}
