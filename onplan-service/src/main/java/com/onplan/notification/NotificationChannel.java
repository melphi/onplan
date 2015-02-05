package com.onplan.notification;

public interface NotificationChannel {
  public void notifyMessage(String title, String body) throws Exception;
  public boolean isActive();
}
