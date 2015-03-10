package com.onplan.connector.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.EnumUtils;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides a complete key / value mapping in which the return value is an enumerated type.
 * In addition assures that all the possible values of the enumerated type are mapped.
 *
 * @param <V> Return value of type enum.
 * @param <K> Key value.
 */
public final class CompleteEnumMapper<K, V extends Enum> implements Mapper<K, V> {
  private final ImmutableMap<K, V> map;

  public static <K, V extends Enum>Builder newBuilder(Class<V> enumClass) {
    return new Builder<K, V>(enumClass);
  }

  private CompleteEnumMapper(
      ImmutableMap<K, V> mapped, ImmutableSet<V> unmapped, Class<V> enumClass) {
    checkMapping(mapped.values(), unmapped, enumClass);
    this.map = checkNotNull(mapped);
  }

  @Override
  public V map(K key) {
    checkNotNull(key);
    return checkNotNull(map.get(key), String.format("Element [%s] not mapped.", key));
  }

  private void checkMapping(Collection<V> mapped, Collection<V> unmapped, Class<V> enumClass) {
    checkNotNull(mapped);
    checkNotNull(unmapped);
    checkNotNull(enumClass);
    for (V value : unmapped) {
      checkArgument(!mapped.contains(value),
          String.format("The unmapped element [%s] can not be in the mapped list.", value));
    }
    List<V> values = EnumUtils.getEnumList(enumClass);
    for (V value : values) {
      checkArgument(mapped.contains(value) || unmapped.contains(value),
          String.format("Element [%s] not mapped.", value));
    }
  }

  public static class Builder<K, V extends Enum> {
    private final ImmutableMap.Builder<K, V> mapped = ImmutableMap.builder();
    private final Class<V> enumClass;

    private ImmutableSet.Builder<V> unmapped = ImmutableSet.builder();

    public Builder(Class<V> enumClass) {
      this.enumClass = checkNotNull(enumClass);
    }

    public Builder<K, V>  put(V value, K... keys) {
      checkNotNull(keys);
      checkNotNull(value);
      for (K key : keys) {
        mapped.put(key, value);
      }
      return this;
    }

    public Builder<K, V>  setUnmapped(V...values) {
      checkNotNull(values);
      unmapped = ImmutableSet.<V>builder().add(values);
      return this;
    }

    public CompleteEnumMapper<K, V> build() {
      return new CompleteEnumMapper<K, V>(mapped.build(), unmapped.build(), enumClass);
    }
  }
}
