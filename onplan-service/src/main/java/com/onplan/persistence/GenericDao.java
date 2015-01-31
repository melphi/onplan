package com.onplan.persistence;

import com.onplan.domain.PersistentObject;

import java.util.Collection;
import java.util.List;

public interface GenericDao<T extends PersistentObject> {
  public void insert(T object);
  public void insertAll(Collection<T> objects);
  public void update(T object);
  public T save(T object);
  public boolean remove(T object);;
  public boolean removeById(String id);;
  public void removeAll();
  public T findById(String id);
  public List<T> findAll();
}
