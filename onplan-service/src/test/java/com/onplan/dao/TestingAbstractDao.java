package com.onplan.dao;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.onplan.domain.persistent.PersistentObject;
import com.onplan.persistence.GenericDao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public abstract class TestingAbstractDao<T extends PersistentObject>  implements GenericDao<T> {
  private final List<T> records = Lists.newArrayList();

  @Override
  public String insert(T object) throws Exception {
    checkNotNull(object);
    checkArgument(Strings.isNullOrEmpty(object.getId()));
    String id = createNewId();
    checkArgument(!containsObject(id));
    object.setId(id);
    records.add(object);
    return id;
  }

  @Override
  public void insertAll(Collection<T> objects) throws Exception {
    for (T object : objects) {
      insert(object);
    }
  }

  @Override
  public String save(T object) throws Exception {
    checkNotNull(object);
    if (Strings.isNullOrEmpty(object.getId())) {
      return insert(object);
    } else {
      checkArgument(containsObject(object.getId()));
      for (T record : records) {
        if (object.getId().equals(record.getId())) {
          records.remove(record);
          break;
        }
      }
      records.add(object);
      return object.getId();
    }
  }

  @Override
  public T saveAndGet(T object) throws Exception {
    checkNotNull(object);
    return findById(save(object));
  }

  @Override
  public boolean remove(T object) {
    checkNotNull(object);
    return removeById(object.getId());
  }

  @Override
  public boolean removeById(String id) {
    checkNotNullOrEmpty(id);
    for (T record : records) {
      if (id.equals(record.getId())) {
        records.remove(record);
        return true;
      }
    }
    return false;
  }

  @Override
  public void removeAll() {
    records.clear();
  }

  @Override
  public T findById(String id) {
    for (T record : records) {
      if (id.equals(record.getId())) {
        return record;
      }
    }
    return null;
  }

  @Override
  public List<T> findAll() {
    return ImmutableList.copyOf(records);
  }

  private String createNewId() {
    return UUID.randomUUID().toString();
  }

  private boolean containsObject(String id) {
    if (records.stream().anyMatch(record -> id.equals(record.getId()))) {
      return true;
    }
    return false;
  }
}
