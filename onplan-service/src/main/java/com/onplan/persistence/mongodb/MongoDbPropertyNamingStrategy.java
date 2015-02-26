package com.onplan.persistence.mongodb;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * Renames the object id field to make it MongoDb compatible during the JSON transformation.
 */
public class MongoDbPropertyNamingStrategy extends PropertyNamingStrategy {
  private static final String OBJECT_ID_FIELD_NAME = "id";
  private static final String MONGO_ID_FIELD_NAME = "_id";

  private static MongoDbPropertyNamingStrategy instance;

  private MongoDbPropertyNamingStrategy() {
    // Intentionally emtpy.
  }

  public static MongoDbPropertyNamingStrategy getInstance() {
    if (null == instance) {
      instance = new MongoDbPropertyNamingStrategy();
    }
    return instance;
  }

  @Override
  public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
    return OBJECT_ID_FIELD_NAME.equals(defaultName)
        ? MONGO_ID_FIELD_NAME
        : super.nameForField(config, field, defaultName);
  }

  @Override
  public String nameForSetterMethod(
      MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
    return OBJECT_ID_FIELD_NAME.equals(defaultName)
        ? MONGO_ID_FIELD_NAME
        : super.nameForSetterMethod(config, method, defaultName);
  }

  @Override
  public String nameForGetterMethod(
      MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
    return OBJECT_ID_FIELD_NAME.equals(defaultName)
        ? MONGO_ID_FIELD_NAME
        : super.nameForGetterMethod(config, method, defaultName);
  }
}
