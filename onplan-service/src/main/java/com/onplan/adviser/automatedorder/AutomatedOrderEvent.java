package com.onplan.adviser.automatedorder;

import com.onplan.adviser.AbstractAdviserEvent;
import com.onplan.domain.PriceTick;

public class AutomatedOrderEvent extends AbstractAdviserEvent {
  protected AutomatedOrderEvent(String instrumentId, PriceTick priceTick, long createdOn) {
    super(instrumentId, priceTick, createdOn);
  }
}
