package com.onplan.adviser.alert;

import com.onplan.adviser.AbstractAdviserEvent;
import com.onplan.domain.PriceTick;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class AlertEvent extends AbstractAdviserEvent {
  private final String message;

  public AlertEvent(String instrumentId, PriceTick priceTick, long createdOn, String message) {
    super(instrumentId, priceTick, createdOn);
    this.message = checkNotNullOrEmpty(message);
  }

  public String getMessage() {
    return message;
  }
}
