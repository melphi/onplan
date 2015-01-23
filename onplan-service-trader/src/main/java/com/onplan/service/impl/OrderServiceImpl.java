package com.onplan.service.impl;

import com.onplan.domain.TradingOrder;
import com.onplan.service.OrderService;

import java.util.List;

public class OrderServiceImpl implements OrderService {
  @Override
  public void sendOrder(TradingOrder order) {
  }

  @Override
  public void cancelOrder(String orderId) {
  }

  @Override
  public List<TradingOrder> getOpenOrders() {
    return null;
  }
}
