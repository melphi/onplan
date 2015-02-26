package com.onplan.domain.configuration.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Map;

public class AdviserPredicateConfiguration implements Serializable {
  private String className;
  private Map<String, String> parameters;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public Map<String, String> getParameters() {
    return parameters;
  }

  public void setParameters(Map<String, String> parameters) {
    this.parameters = parameters;
  }

  public AdviserPredicateConfiguration() {
    // Intentionally empty.
  }

  public AdviserPredicateConfiguration(String className, Map<String, String> parameters) {
    this.className = className;
    this.parameters = parameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdviserPredicateConfiguration predicateConfiguration = (AdviserPredicateConfiguration) o;
    return Objects.equal(this.className, predicateConfiguration.className) &&
        Objects.equal(this.parameters, predicateConfiguration.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(className, parameters);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("className", className)
        .add("parameters", parameters)
        .toString();
  }
}
