package com.onplan.adviser;

public interface AdviserListener<T extends AdviserEvent> {
  public void onEvent(T event);
}
