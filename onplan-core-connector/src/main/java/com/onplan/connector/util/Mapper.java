package com.onplan.connector.util;

public interface Mapper<K, V> {
  public V map(K object);
}
