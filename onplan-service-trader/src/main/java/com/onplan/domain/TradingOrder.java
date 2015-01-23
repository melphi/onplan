package com.onplan.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class TradingOrder {
  private final String orderId;
  private final TradingOrderType orderType;
  private final String instrumentId;
  private final String orderReference;
  private final String positionReference;
  private double quantity;

  private TradingOrder(String orderId, TradingOrderType orderType, String instrumentId,
      String orderReference, String positionReference, double quantity) {
    checkArgument(quantity > 0);
    this.orderId = checkNotNullOrEmpty(orderId);
    this.orderType = checkNotNull(orderType);
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
    this.orderReference = orderReference;
    this.positionReference = positionReference;
    this.quantity = quantity;
  }
}
