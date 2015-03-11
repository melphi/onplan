package com.onplan.domain.persistent;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class SystemEvent implements PersistentObject {
  private String id;
  private String className;
  private String message;
  private long createdOn;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public long getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(long createdOn) {
    this.createdOn = createdOn;
  }

  public SystemEvent() {
    // Intentionally empty.
  }

  public SystemEvent(Class clazz, String message, long createdOn) {
    checkArgument(createdOn > 0);
    this.className = checkNotNull(clazz).getName();
    this.message = checkNotNullOrEmpty(message);
    this.createdOn = createdOn;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, className, message, createdOn);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SystemEvent systemEvent = (SystemEvent) o;
    return Objects.equal(this.id, systemEvent.id) &&
        Objects.equal(this.className, systemEvent.className) &&
        Objects.equal(this.message, systemEvent.message) &&
        Objects.equal(this.createdOn, systemEvent.createdOn);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("className", className)
        .add("message", message)
        .add("createdOn", createdOn)
        .toString();
  }
}
