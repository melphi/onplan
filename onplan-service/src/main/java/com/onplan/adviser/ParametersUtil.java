package com.onplan.adviser;

import com.google.common.base.Strings;

import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class ParametersUtil {
  public static Optional<String> getStringValue(Map<String, String> parameters, String key) {
    checkNotNullOrEmpty(key);
    String propertyValue = checkNotNull(parameters).get(key);
    return Strings.isNullOrEmpty(propertyValue) ? Optional.empty() : Optional.of(propertyValue);
  }

  public static Optional<Long> getLongValue(Map<String, String> parameters, String key) {
    Optional<String> propertyValue = getStringValue(parameters, key);
    return propertyValue.isPresent() ? Optional.of(Long.parseLong(propertyValue.get()))
        : Optional.empty();
  }

  public static Optional<Double> getDoubleProperty(Map<String, String> parameters, String key) {
    Optional<String> propertyValue = getStringValue(parameters, key);
    return propertyValue.isPresent() ? Optional.of(Double.parseDouble(propertyValue.get()))
        : Optional.empty();
  }

  public static String getRequiredStringValue(Map<String, String> parameters, String key) {
    Optional<String> propertyValue = getStringValue(parameters, key);
    checkArgument(
        propertyValue.isPresent(), String.format("Property [%s] not set or empty.", key));
    return propertyValue.get();
  }

  public static Long getRequiredLongValue(Map<String, String> parameters, String key) {
    Optional<Long> propertyValue = getLongValue(parameters, key);
    checkArgument(
        propertyValue.isPresent(), String.format("Property [%s] not set or empty.", key));
    return propertyValue.get();
  }

  public static Double getRequiredDoubleValue(Map<String, String> parameters, String key) {
    Optional<Double> propertyValue = getDoubleProperty(parameters, key);
    checkArgument(
        propertyValue.isPresent(), String.format("Property [%s] not set or empty.", key));
    return propertyValue.get();
  }
}
