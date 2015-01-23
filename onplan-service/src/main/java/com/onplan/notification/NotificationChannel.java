package com.onplan.notification;

import com.onplan.strategy.StrategyEvent;

public interface NotificationChannel {
  public void notifyStrategyEvent(StrategyEvent strategyEvent) throws Exception;
  public void notifyMessage(String title, String body) throws Exception;
  public boolean isActive();
}
