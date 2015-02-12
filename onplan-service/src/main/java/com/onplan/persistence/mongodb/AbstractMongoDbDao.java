package com.onplan.persistence.mongodb;

import com.google.common.base.Strings;
import com.onplan.domain.PersistentObject;
import com.onplan.persistence.GenericDao;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractMongoDbDao<T extends PersistentObject> implements GenericDao<T> {
  private static final Logger LOGGER = Logger.getLogger(AbstractMongoDbDao.class);

  private final String collectionName;
  private final Class<T> clazz;

//  private MongoOperations mongoOperations;

  public AbstractMongoDbDao(String collectionName, Class<T> clazz) {
    this.collectionName = checkNotNullOrEmpty(collectionName);
    this.clazz = checkNotNull(clazz);
  }

  @Override
  public void insert(T object) {
    checkNotNull(object);
    checkArgument(
        Strings.isNullOrEmpty(object.getId()), "Object id should be null to perform insert.");
    object.setId(new ObjectId().toString());
//    mongoOperations.insert(object, collectionName);
  }

  @Override
  public void insertAll(Collection<T> objects) {
    checkNotNull(objects);
//    mongoOperations.insert(objects, collectionName);
  }

  @Override
  public void update(T object) {
    checkNotNull(object);
    checkNotNullOrEmpty(object.getId(), "Object id should be set to perform update.");
//    mongoOperations.save(object, collectionName);
  }

  @Override
  public T save(T object) {
    checkNotNull(object);
    if (Strings.isNullOrEmpty(object.getId())) {
      object.setId(new ObjectId().toString());
//      mongoOperations.insert(object, collectionName);
    } else {
//      mongoOperations.save(object, collectionName);
    }
    return object;
  }

  @Override
  public boolean remove(T object) {
    checkNotNull(object);
//    WriteResult result = mongoOperations.remove(object, collectionName);
//    return result.getN() > 0;
    return false;
  }

  @Override
  public boolean removeById(String id) {
    checkNotNullOrEmpty(id);
//    WriteResult result = mongoOperations.remove(query(where("_id").is(id)), collectionName);
//    return result.getN() > 0;
    return false;
  }

  @Override
  public void removeAll() {
//    mongoOperations.dropCollection(collectionName);
  }

  @Override
  public T findById(String id) {
    checkNotNullOrEmpty(id);
//    return mongoOperations.findById(id, clazz, collectionName);
    return null;
  }

  @Override
  public List<T> findAll() {
//    return mongoOperations.findAll(clazz, collectionName);
    return null;
  }

//  @Autowired
//  private void setSimpleMongoDbFactory(SimpleMongoDbFactory simpleMongoDbFactory) {
//    LOGGER.info("Initializing MongoDb operations.");
//    mongoOperations = new MongoTemplate(simpleMongoDbFactory);
//    LOGGER.info(String.format(
//        "MongoDb initialized, available collections [%s].",
//        Joiner.on(", ").join(mongoOperations.getCollectionNames())));
//  }
}
