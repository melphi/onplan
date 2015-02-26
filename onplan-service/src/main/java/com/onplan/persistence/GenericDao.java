package com.onplan.persistence;

import com.onplan.domain.PersistentObject;

import java.util.Collection;
import java.util.List;

public interface GenericDao<T extends PersistentObject> {
  /**
   * Saves an object , making sure that its id is not already set.
   * Returns the object with its id set.
   *
   * @param object The object to be saved.
   * @exception java.lang.Exception Error while inserting object.
   */
  public T insert(T object) throws Exception;

  /**
   * Inserts a collection of object, making sure that their id are not already set.
   *
   * @param objects The collection of objects.
   * @exception java.lang.Exception Error while inserting objects.
   */
  public void insertAll(Collection<T> objects) throws Exception;

  /**
   * Inserts an object when its id is not already set otherwise updates the object from the
   * database.
   *
   * @param object The object to be inserted or updated.
   * @exception java.lang.Exception Error while saving objects.
   */
  public T save(T object) throws Exception;

  /**
   * Remove an object from the database. Returns true if the object was found, false otherwise.
   *
   * @param object The object to be removed.
   */
  public boolean remove(T object);

  /**
   * Removes an object from the database by its id. Returns true if the object was found,
   * false otherwise.
   *
   * @param id The object id.
   */
  public boolean removeById(String id);

  /**
   * Removes all the object which are stored in the current collection.
   */
  public void removeAll();

  /**
   * Returns an object by its id.
   *
   * @param id The object id.
   */
  public T findById(String id);

  /**
   * Returns all the objects saved in the current collection.
   */
  public List<T> findAll();
}
