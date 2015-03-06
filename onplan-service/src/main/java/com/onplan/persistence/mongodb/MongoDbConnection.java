package com.onplan.persistence.mongodb;

import com.google.common.base.Joiner;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.net.UnknownHostException;

import static com.google.common.base.Preconditions.checkNotNull;

@Singleton
public final class MongoDbConnection {
  private static final Logger LOGGER = Logger.getLogger(MongoDbConnection.class);

  @Inject
  @Named("mongodb.database")
  private String databaseName;

  @Inject
  @Named("mongodb.host")
  private String hostName;

  @Inject
  @Named("mongodb.port")
  private String portNumber;

  private DB database = null;

  public DB getDatabase() {
    if (null == database) {
      try {
        LOGGER.info(String.format("Connecting to MongoDd at [%s]:[%s].", hostName, portNumber));
        MongoClient mongoClient = new MongoClient(hostName, Integer.parseInt(portNumber));
        database = checkNotNull(mongoClient.getDB(databaseName));
        LOGGER.info(String.format(
            "MongoDb initialized, available collections [%s].",
            Joiner.on(", ").join(database.getCollectionNames())));
      } catch (UnknownHostException e) {
        LOGGER.error(
            String.format("Error while connecting to MongoDB at [%s]:[%s].", hostName, portNumber),
            e);
        throw new IllegalArgumentException(e);
      }
    }
    return database;
  }
}
