package com.onplan.domain.persistent;

import java.io.Serializable;

public interface PersistentObject extends Serializable {
  public String getId();
  public void setId(String id);
}
