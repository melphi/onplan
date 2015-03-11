package com.onplan.persistence.mongodb;

import com.onplan.domain.persistent.AlertEvent;
import com.onplan.persistence.AlertEventDao;

import javax.inject.Singleton;

@Singleton
public final class MongoDbAlertEventDao extends AbstractMongoDbDao<AlertEvent>
    implements AlertEventDao {
  private static final String ALERT_EVENT_COLLECTION = "alertEvent";

  public MongoDbAlertEventDao() {
    super(ALERT_EVENT_COLLECTION, AlertEvent.class);
  }
}
