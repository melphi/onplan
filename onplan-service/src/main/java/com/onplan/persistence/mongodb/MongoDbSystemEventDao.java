package com.onplan.persistence.mongodb;

import com.onplan.domain.persistent.SystemEvent;
import com.onplan.persistence.SystemEventDao;

import javax.inject.Singleton;

@Singleton
public final class MongoDbSystemEventDao extends AbstractMongoDbDao<SystemEvent>
    implements SystemEventDao {
  private static final String SYSTEM_EVENT_COLLECTION = "systemEvent";

  public MongoDbSystemEventDao() {
    super(SYSTEM_EVENT_COLLECTION, SystemEvent.class);
  }
}
