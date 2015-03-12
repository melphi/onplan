package com.onplan.persistence.mongodb;

import com.onplan.domain.persistent.AlertEventHistory;
import com.onplan.persistence.AlertEventHistoryDao;

import javax.inject.Singleton;

@Singleton
public final class MongoDbAlertEventDao extends AbstractMongoDbDao<AlertEventHistory>
    implements AlertEventHistoryDao {
  private static final String ALERT_EVENT_COLLECTION = "alertEvent";

  public MongoDbAlertEventDao() {
    super(ALERT_EVENT_COLLECTION, AlertEventHistory.class);
  }
}
