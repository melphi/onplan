package com.onplan.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Map;

public class AdviserPredicateInfo implements Serializable {
  private String name;
  private String className;
  private Iterable<String> availableParameters;
  private Map<String, String> executionParameters;

  public AdviserPredicateInfo() {
    // Intentionally empty.
  }

  public AdviserPredicateInfo(String name, String className, Iterable<String> availableParameters,
      Map<String, String> executionParameters) {
    this.name = name;
    this.className = className;
    this.availableParameters = availableParameters;
    this.executionParameters = executionParameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AdviserPredicateInfo adviserPredicateInfo = (AdviserPredicateInfo) o;
    return Objects.equal(this.name, adviserPredicateInfo.name) &&
        Objects.equal(this.className, adviserPredicateInfo.className) &&
        Objects.equal(this.availableParameters, adviserPredicateInfo.availableParameters) &&
        Objects.equal(this.executionParameters, adviserPredicateInfo.executionParameters);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name, className, availableParameters, executionParameters);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("displayName", name)
        .add("className", className)
        .add("availableParameters", availableParameters)
        .add("executionParameters", executionParameters)
        .toString();
  }
}
