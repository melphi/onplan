package com.onplan.service;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class SystemEvent implements Serializable {
  private final String className;
  private final String message;
  private final long createdOn;

  public String getClassName() {
    return className;
  }

  public String getMessage() {
    return message;
  }

  public long getCreatedOn() {
    return createdOn;
  }

  public SystemEvent(Class clazz, String message, long createdOn) {
    checkArgument(createdOn > 0);
    this.className = checkNotNull(clazz).getName();
    this.message = checkNotNullOrEmpty(message);
    this.createdOn = createdOn;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(className, message, createdOn);
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
    return Objects.equal(this.className, systemEvent.className) &&
        Objects.equal(this.message, systemEvent.message) &&
        Objects.equal(this.createdOn, systemEvent.createdOn);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("className", className)
        .add("message", message)
        .add("createdOn", createdOn)
        .toString();
  }
}
