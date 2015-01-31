package com.onplan.domain;

import java.io.Serializable;

public interface PersistentObject extends Serializable {
  public String getId();
  public void setId(String id);
}
