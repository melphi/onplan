package com.onplan.persistence.mongodb;

import com.google.common.base.Joiner;
import com.mongodb.WriteResult;
import com.onplan.domain.PersistentObject;
import com.onplan.persistence.GenericDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

public abstract class AbstractMongoDbDao<T extends PersistentObject> implements GenericDao<T> {
  private static final Logger logger = Logger.getLogger(AbstractMongoDbDao.class);

  private final String collectionName;
  private final Class<T> clazz;

  private MongoOperations mongoOperations;

  public AbstractMongoDbDao(String collectionName, Class<T> clazz) {
    this.collectionName = checkNotNullOrEmpty(collectionName);
    this.clazz = checkNotNull(clazz);
  }

  @Autowired
  private void setSimpleMongoDbFactory(SimpleMongoDbFactory simpleMongoDbFactory) {
    logger.info("Initializing MongoDb operations.");
    mongoOperations = new MongoTemplate(simpleMongoDbFactory);
    logger.info(String.format(
        "MongoDb initialized, available collections [%s].",
        Joiner.on(", ").join(mongoOperations.getCollectionNames())));
  }

  @Override
  public void insert(T object) {
    checkNotNull(object);
    mongoOperations.insert(object, collectionName);
  }

  @Override
  public void insertAll(Collection<T> objects) {
    checkNotNull(objects);
    mongoOperations.insert(objects, collectionName);
  }

  @Override
  public void update(T object) {
    checkNotNull(object);
    throw new IllegalArgumentException("Not yet implemented");
  }

  @Override
  public boolean remove(T object) {
    checkNotNull(object);
    WriteResult result = mongoOperations.remove(object, collectionName);
    return result.getN() > 0;
  }

  @Override
  public boolean removeById(String id) {
    checkNotNullOrEmpty(id);
    WriteResult result = mongoOperations.remove(query(where("_id").is(id)), collectionName);
    return result.getN() > 0;
  }

  @Override
  public void removeAll() {
    mongoOperations.dropCollection(collectionName);
  }

  @Override
  public T findById(String id) {
    checkNotNullOrEmpty(id);
    return mongoOperations.findById(id, clazz, collectionName);
  }

  @Override
  public List<T> findAll() {
    return mongoOperations.findAll(clazz, collectionName);
  }
}
