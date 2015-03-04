package com.onplan.service.impl;

import com.onplan.service.InstrumentSubscriptionListener;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractAdviserService {
  private volatile InstrumentSubscriptionListener instrumentSubscriptionListener;

  public void setInstrumentSubscriptionListener(
      InstrumentSubscriptionListener instrumentSubscriptionListener) {
    this.instrumentSubscriptionListener = checkNotNull(instrumentSubscriptionListener);
  }

  protected void dispatchInstrumentSubscriptionRequired(final String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    if (null != instrumentSubscriptionListener) {
      instrumentSubscriptionListener.onInstrumentSubscriptionRequest(instrumentId);
    }
  }

  protected void dispatchInstrumentUnSubscriptionRequired(final String instrumentId) {
    checkNotNullOrEmpty(instrumentId);
    if (null != instrumentSubscriptionListener) {
      instrumentSubscriptionListener.onInstrumentUnSubscriptionRequest(instrumentId);
    }
  }
}
