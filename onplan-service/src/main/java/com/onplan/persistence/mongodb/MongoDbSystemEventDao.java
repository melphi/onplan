package com.onplan.persistence.mongodb;

import com.onplan.domain.persistent.SystemEventHistory;
import com.onplan.persistence.SystemEventHistoryDao;

import javax.inject.Singleton;

@Singleton
public final class MongoDbSystemEventDao extends AbstractMongoDbDao<SystemEventHistory>
    implements SystemEventHistoryDao {
  private static final String SYSTEM_EVENT_COLLECTION = "systemEvent";

  public MongoDbSystemEventDao() {
    super(SYSTEM_EVENT_COLLECTION, SystemEventHistory.class);
  }
}
