package com.onplan.persistence.mongodb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.mongodb.*;
import com.mongodb.util.JSON;
import com.onplan.domain.PersistentObject;
import com.onplan.persistence.GenericDao;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class AbstractMongoDbDao<T extends PersistentObject> implements GenericDao<T> {
  private static final Logger LOGGER = Logger.getLogger(AbstractMongoDbDao.class);

  private final String collectionName;
  private final Class<T> clazz;
  private final ObjectWriter objectWriter;
  private final ObjectReader objectReader;

  private DBCollection dbCollection;

  @Inject
  public void setMongoDbConnection(MongoDbConnection mongoDbConnection) {
    checkNotNull(mongoDbConnection);
    checkArgument(null == this.dbCollection, "MongoDb connection already set.");
    DB database = checkNotNull(mongoDbConnection.getDatabase());
    this.dbCollection = checkNotNull(database.getCollection(collectionName));
  }

  public AbstractMongoDbDao(String collectionName, Class<T> clazz) {
    this.collectionName = checkNotNullOrEmpty(collectionName);
    this.clazz = checkNotNull(clazz);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setPropertyNamingStrategy(MongoDbPropertyNamingStrategy.getInstance());
    this.objectWriter = objectMapper.writer();
    this.objectReader = objectMapper.reader(clazz);
  }

  @Override
  public String insert(T object) throws Exception{
    checkNotNull(object);
    checkIdIsNotSet(object);
    object.setId(createNewObjectId());
    dbCollection.insert(createDbObject(object));
    return object.getId();
  }

  @Override
  public void insertAll(Collection<T> objects) throws Exception {
    checkNotNull(objects);
    List<DBObject> list = new ArrayList<DBObject>(objects.size());
    for (T object : objects) {
      checkIdIsNotSet(object);
      object.setId(createNewObjectId());
      list.add(createDbObject(object));
    }
    dbCollection.insert(list);
  }

  @Override
  public String save(T object) throws Exception {
    checkNotNull(object);
    if (Strings.isNullOrEmpty(object.getId())) {
      return insert(object);
    } else {
      dbCollection.save(createDbObject(object));
      return object.getId();
    }
  }

  @Override
  public boolean remove(T object) {
    checkNotNull(object);
    WriteResult writeResult = dbCollection.remove(createDbObject(object));
    return  writeResult.getN() > 0;
  }

  @Override
  public boolean removeById(String id) {
    checkNotNullOrEmpty(id);
    BasicDBObject basicDBObject = new BasicDBObject("_id", id);
    WriteResult writeResult = dbCollection.remove(basicDBObject);
    return writeResult.getN() > 0;
  }

  @Override
  public void removeAll() {
    // An empty object passed as argument instructs MongoDb to remove all the objects
    // from the collection.
    dbCollection.remove(new BasicDBObject());
  }

  @Override
  public T findById(String id) {
    checkNotNullOrEmpty(id);
    DBObject dbObject = dbCollection.findOne(id);
    return createObject(dbObject);
  }

  @Override
  public List<T> findAll() {
    ImmutableList.Builder<T> result = ImmutableList.builder();
    DBCursor dbCursor = dbCollection.find();
    while (dbCursor.hasNext()) {
      result.add(createObject(dbCursor.next()));
    }
    return result.build();
  }

  protected DBObject createDbObject(final T object) {
    try {
      String jsonString = objectWriter.writeValueAsString(object);
      // TODO(robertom): I am not a big fan of casting, does a better solution exist?
      return (DBObject) JSON.parse(jsonString);
    } catch (JsonProcessingException e) {
      LOGGER.error(String.format("JSON serialization error for the object [%s].", object), e);
      throw new IllegalArgumentException(e);
    }
  }

  protected T createObject(final DBObject dbObject) {
    if (null == dbObject) {
      return null;
    }
    String jsonString = JSON.serialize(dbObject);
    try {
      return objectReader.readValue(jsonString);
    } catch (IOException e) {
      LOGGER.error(
          String.format(
              "Error while retrieving object of type [%s] from [%s].",
              clazz.getName(),
              jsonString),
          e);
      throw new IllegalArgumentException(e);
    }
  }

  private static void checkIdIsNotSet(PersistentObject object) {
    checkArgument(
        Strings.isNullOrEmpty(object.getId()), "Object id must be null to perform insert.");
  }

  private static String createNewObjectId() {
    return new ObjectId().toString();
  }
}
