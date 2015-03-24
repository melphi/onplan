package com.onplan.integration.persistence;

import com.onplan.domain.persistent.PersistentObject;
import com.onplan.integration.AbstractIT;
import com.onplan.persistence.GenericDao;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;
import static org.junit.Assert.*;

public abstract class AbstractDaoIT<T extends PersistentObject> extends AbstractIT {
  protected static final int INITIAL_COLLECTION_SIZE = 10;

  private static final String INVALID_OBJECT_ID = "InvalidObjectId";

  private final GenericDao<T> dao;

  public AbstractDaoIT(Class<? extends GenericDao<T>> daoClass) {
    this.dao = injector.getInstance(daoClass);
  }

  @Before
  public void setup() throws Exception {
    dao.removeAll();
    dao.insertAll(createSampleObjectsWithNullId());
  }

  @Test
  public void testInsertValidObject() throws Exception {
    T object = createSampleObjectWithNullId();
    String id = checkNotNullOrEmpty(dao.insert(object));
    T savedObject = dao.findById(id);
    object.setId(id);
    assertEquals(object, savedObject);
    assertValidObject(savedObject);
  }

  @Test(expected = Exception.class)
  public void testInsertInvalidObject() throws Exception {
    T object = createSampleObjectWithNullId();
    object.setId("id");
    dao.insert(object);
  }

  @Test
  public void testInsertAllValidObjects() throws Exception {
    Collection<T> previousObjects = dao.findAll();
    Collection<T> newObjects = createSampleObjectsWithNullId();
    dao.insertAll(newObjects);
    Collection<T> savedObjects = dao.findAll();
    assertTrue(savedObjects.size() == previousObjects.size() + newObjects.size());
    for (T object : savedObjects) {
      assertValidObject(object);
    }
  }

  @Test
  public void testSaveNew() throws Exception {
    T object = createSampleObjectWithNullId();
    String id = checkNotNullOrEmpty(dao.save(object));
    T savedObject = dao.findById(id);
    object.setId(id);
    assertValidObject(savedObject);
    assertEquals(object, savedObject);
  }

  @Test
  public void testSaveAndGet() throws Exception {
    T object = createSampleObjectWithNullId();
    T returnedObject = checkNotNull(dao.saveAndGet(object));
    T savedObject = dao.findById(returnedObject.getId());
    object.setId(returnedObject.getId());
    assertValidObject(savedObject);
    assertEquals(object, savedObject);
    assertEquals(returnedObject, savedObject);
  }

  @Test
  public void testSaveExisting() throws Exception {
    T object = createSampleObjectWithNullId();
    String id = checkNotNullOrEmpty(dao.save(object));
    T firstCopy = dao.findById(id);
    id = dao.save(firstCopy);
    T secondCopy = dao.findById(id);
    assertValidObject(firstCopy);
    assertEquals(firstCopy, secondCopy);
  }

  @Test
  public void testRemove() throws Exception {
    T object = createSampleObjectWithNullId();
    String id = dao.insert(object);
    T savedObject = dao.findById(id);
    assertNotNull(savedObject);
    assertTrue(dao.remove(savedObject));
    assertTrue(dao.findById(id) == null);
    assertTrue(dao.findAll().size() == INITIAL_COLLECTION_SIZE);
  }

  @Test
  public void testNotRemoved() throws Exception {
    assertFalse(dao.removeById(INVALID_OBJECT_ID));
  }

  @Test
  public void testNotRemovedById() throws Exception {
    T object = createSampleObjectWithNullId();
    assertFalse(dao.remove(object));
  }

  @Test
  public void testRemoveById() throws Exception {
    T object = createSampleObjectWithNullId();
    String id = dao.insert(object);
    assertNotNull(dao.findById(id));
    assertTrue(dao.removeById(id));
    assertTrue(dao.findById(id) == null);
    assertTrue(dao.findAll().size() == INITIAL_COLLECTION_SIZE);
  }

  @Test
  public void testRemoveAll() {
    assertTrue(dao.findAll().size() == INITIAL_COLLECTION_SIZE);
    dao.removeAll();
    assertTrue(dao.findAll().size() == 0);
  }

  @Test
  public void testFindById() throws Exception {
    T object = createSampleObjectWithNullId();
    String id = dao.insert(object);
    T savedObject = dao.findById(id);
    object.setId(id);
    assertValidObject(savedObject);
    assertEquals(object, savedObject);
  }

  @Test
  public void testFindAll() {
    Collection<T> result = dao.findAll();
    assertTrue(result.size() == INITIAL_COLLECTION_SIZE);
    for (T object : result) {
      assertValidObject(object);
    }
  }

  protected abstract T createSampleObjectWithNullId();

  protected abstract Collection<T> createSampleObjectsWithNullId();

  protected void assertValidObject(T object) {
    checkNotNull(object);
    checkNotNullOrEmpty(object.getId());
    checkNotNullOrEmpty(object.toString());
    assertEquals(object, object);
    assertTrue(object.hashCode() != 0);
  }
}
