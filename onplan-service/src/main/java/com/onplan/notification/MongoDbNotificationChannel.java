package com.onplan.notification;

import com.onplan.domain.persistent.AlertEvent;
import com.onplan.domain.persistent.SystemEvent;
import com.onplan.persistence.AlertEventDao;
import com.onplan.persistence.SystemEventDao;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A notification channel which persists the events in the MongoDB database.
 */
@Singleton
public class MongoDbNotificationChannel implements NotificationChannel {
  @Inject
  private AlertEventDao alertEventDao;

  @Inject
  private SystemEventDao systemEventDao;

  @Override
  public void notifySystemEvent(SystemEvent systemEvent) throws Exception {
    checkNotNull(systemEvent);
    systemEventDao.insert(systemEvent);
  }

  @Override
  public void notifyAlertEvent(AlertEvent alertEvent) throws Exception {
    checkNotNull(alertEvent);
    alertEventDao.insert(alertEvent);
  }
}
