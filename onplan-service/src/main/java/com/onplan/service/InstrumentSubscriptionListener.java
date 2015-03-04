package com.onplan.service;

/**
 * Allows services to require an instrument subscription / un-subscription.
 */
public interface InstrumentSubscriptionListener {
  /**
   * Requires an instrument subscription.
   *
   * @param instrumentId The instrument id.
   */
  public void onInstrumentSubscriptionRequest(final String instrumentId);

  /**
   * Requires an instrument un-subscription.
   *
   * @param instrumentId The instrument id.
   */
  public void onInstrumentUnSubscriptionRequest(final String instrumentId);
}
