package com.onplan.notification;

import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
public class SmtpNotificationChannel implements NotificationChannel {
  public SmtpNotificationChannel(
      @Named("smtp.host") String host,
      @Named("smtp.username") String username,
      @Named("smtp.password") String password) {
    // TODO(robertom): Implement this code.
  }

  @Override
  public void notifyMessage(String title, String body) throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public boolean isActive() {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
