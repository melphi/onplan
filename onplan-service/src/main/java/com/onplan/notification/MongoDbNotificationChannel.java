package com.onplan.notification;

import com.onplan.domain.persistent.AlertEvent;
import com.onplan.domain.persistent.SystemEvent;
import com.onplan.persistence.AlertEventDao;
import com.onplan.persistence.SystemEventDao;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A notification channel which saves the events in the MongoDB database.
 */
@Singleton
public class MongoDbNotificationChannel implements NotificationChannel {
  @Inject
  private AlertEventDao alertEventDao;

  @Inject
  private SystemEventDao systemEventDao;

  @Override
  public void notifySystemEvent(SystemEvent systemEvent) throws Exception {
    systemEventDao.insert(systemEvent);
  }

  @Override
  public void notifyAlertEvent(AlertEvent alertEvent) throws Exception {
    alertEventDao.insert(alertEvent);
  }

  @Override
  public boolean isActive() {
    return true;
  }
}
