package com.onplan.adapter.util;

import com.onplan.adapter.PriceListener;
import com.onplan.domain.persistent.PriceBar;
import com.onplan.domain.PriceBarTimeFrame;
import com.onplan.domain.persistent.PriceTick;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;
import static com.onplan.util.PriceBarUtil.getCurrentBarCloseTimestamp;
import static com.onplan.util.PriceBarUtil.getCurrentBarOpenTimestamp;

public final class PriceTickAggregator {
  private final PriceListener priceListener;
  private final PriceBarTimeFrame priceBarTimeFrame;
  private final String instrumentId;

  private long currentBarOpenTimestamp;
  private long currentBarCloseTimestamp = -1;

  private double priceValueAskOpen;
  private double priceValueAskClose;
  private double priceValueAskHigh;
  private double priceValueAskLow;
  private double priceValueBidOpen;
  private double priceValueBidClose;
  private double priceValueBidHigh;
  private double priceValueBidLow;

  public static Builder newBuilder() {
    return new Builder();
  }

  public void addPriceTick(PriceTick priceTick) {
    if(currentBarCloseTimestamp <= 0) {
      prepareNewBar(priceTick);
    } else if (priceTick.getTimestamp() > currentBarCloseTimestamp) {
      PriceBar priceBar = createCurrentPriceBar();
      priceListener.onPriceBar(priceBar);
      prepareNewBar(priceTick);
    } else {
      updateCurrentBarValues(priceTick);
    }
  }

  private PriceBar createCurrentPriceBar() {
    return new PriceBar(
        instrumentId,
        currentBarOpenTimestamp,
        priceBarTimeFrame.getIntervalMilliseconds(),
        priceValueAskOpen,
        priceValueBidOpen,
        priceValueAskClose,
        priceValueBidClose,
        priceValueAskLow,
        priceValueBidLow,
        priceValueAskHigh,
        priceValueBidHigh);
  }

  private void updateCurrentBarValues(PriceTick priceTick) {
    priceValueAskClose = priceTick.getClosePriceAsk();
    if(priceValueAskHigh < priceTick.getClosePriceAsk()) {
      priceValueAskHigh = priceTick.getClosePriceAsk();
    }
    if(priceValueAskLow > priceTick.getClosePriceAsk()) {
      priceValueAskLow = priceTick.getClosePriceAsk();
    }
    priceValueBidClose = priceTick.getClosePriceBid();
    if(priceValueBidHigh < priceTick.getClosePriceBid()) {
      priceValueBidHigh = priceTick.getClosePriceBid();
    }
    if(priceValueBidLow > priceTick.getClosePriceBid()) {
      priceValueBidLow = priceTick.getClosePriceBid();
    }
  }

  private void prepareNewBar(PriceTick firstTick) {
    this.priceValueAskOpen = firstTick.getClosePriceAsk();
    this.priceValueAskClose = firstTick.getClosePriceAsk();
    this.priceValueAskHigh = firstTick.getClosePriceAsk();
    this.priceValueAskLow = firstTick.getClosePriceAsk();
    this.priceValueBidOpen = firstTick.getClosePriceBid();
    this.priceValueBidClose = firstTick.getClosePriceBid();
    this.priceValueBidHigh = firstTick.getClosePriceBid();
    this.priceValueBidLow = firstTick.getClosePriceBid();
    this.currentBarOpenTimestamp =
        getCurrentBarOpenTimestamp(firstTick.getTimestamp(), priceBarTimeFrame);
    this.currentBarCloseTimestamp =
        getCurrentBarCloseTimestamp(firstTick.getTimestamp(), priceBarTimeFrame);
  }

  private PriceTickAggregator(
      String instrumentId, PriceListener priceListener, PriceBarTimeFrame priceBarTimeFrame) {
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
    this.priceListener = checkNotNull(priceListener);
    this.priceBarTimeFrame = checkNotNull(priceBarTimeFrame);
  }

  public static class Builder {
    private String instrumentId;
    private PriceBarTimeFrame priceBarTimeFrame;
    private PriceListener priceListener;

    public Builder setInstrumentId(String instrumentId) {
      this.instrumentId = checkNotNullOrEmpty(instrumentId);
      return this;
    }

    public Builder setPriceListener(PriceListener priceListener) {
      this.priceListener = checkNotNull(priceListener);
      return this;
    }

    public Builder setPriceBarTimeFrame(PriceBarTimeFrame priceBarTimeFrame) {
      this.priceBarTimeFrame = checkNotNull(priceBarTimeFrame);
      return this;
    }

    public PriceTickAggregator build() {
      return new PriceTickAggregator(instrumentId, priceListener, priceBarTimeFrame);
    }
  }
}
