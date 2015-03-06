package com.onplan.adviser.automatedorder;

import com.onplan.adviser.AbstractAdviserEvent;
import com.onplan.domain.PriceTick;

// TODO(robertom): Implement this class.
public final class AutomatedOrderEvent extends AbstractAdviserEvent {
  protected AutomatedOrderEvent(String instrumentId, PriceTick priceTick, long createdOn) {
    super(priceTick, createdOn);
  }
}
