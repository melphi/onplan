package com.onplan.notification;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class SystemEvent {
  private final String className;
  private final String message;
  private final long createdOn;

  public SystemEvent(Class clazz, String message, long createdOn) {
    checkArgument(createdOn > 0);
    this.className = checkNotNull(clazz).getName();
    this.message = checkNotNullOrEmpty(message);
    this.createdOn = createdOn;
  }

  public long getCreatedOn() {
    return createdOn;
  }

  public String getClassName() {
    return className;
  }

  public String getMessage() {
    return message;
  }
}
