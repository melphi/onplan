package com.onplan.notification;

import com.onplan.adviser.alert.AlertEvent;
import com.onplan.domain.persistent.AlertEventHistory;
import com.onplan.domain.persistent.SystemEventHistory;
import com.onplan.persistence.AlertEventHistoryDao;
import com.onplan.persistence.SystemEventHistoryDao;
import com.onplan.service.SystemEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A notification channel which persists the events in the MongoDB database.
 */
@Singleton
public class MongoDbNotificationChannel implements NotificationChannel {
  @Inject
  private AlertEventHistoryDao alertEventHistoryDao;

  @Inject
  private SystemEventHistoryDao systemEventHistoryDao;

  @Override
  public void notifySystemEvent(final SystemEvent systemEvent) throws Exception {
    checkNotNull(systemEvent);
    SystemEventHistory systemEventHistory = new SystemEventHistory(
        null,
        systemEvent.getClassName(),
        systemEvent.getMessage(),
        systemEvent.getCreatedOn());
    systemEventHistoryDao.insert(systemEventHistory);
  }

  @Override
  public void notifyAlertEvent(final AlertEvent alertEvent) throws Exception {
    checkNotNull(alertEvent);
    AlertEventHistory alertEventHistory = new AlertEventHistory(
        null,
        alertEvent.getAdviserId(),
        alertEvent.getSeverityLevel(),
        alertEvent.getPriceTick(),
        alertEvent.getCreatedOn(),
        alertEvent.getMessage());
    alertEventHistoryDao.insert(alertEventHistory);
  }
}
