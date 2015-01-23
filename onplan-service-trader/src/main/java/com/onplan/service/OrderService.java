package com.onplan.service;

import com.onplan.domain.TradingOrder;

import java.util.List;

public interface OrderService {
  public void sendOrder(TradingOrder order);
  public void cancelOrder(String orderId);
  public List<TradingOrder> getOpenOrders();
}
