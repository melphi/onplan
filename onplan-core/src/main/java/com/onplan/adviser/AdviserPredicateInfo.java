package com.onplan.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Map;

/**
 * Contains information about an adviser predicate.
 */
public final class AdviserPredicateInfo extends TemplateInfo {
  private Map<String, String> executionParameters;

  public Map<String, String> getExecutionParameters() {
    return executionParameters;
  }

  public void setExecutionParameters(Map<String, String> executionParameters) {
    this.executionParameters = executionParameters;
  }

  public AdviserPredicateInfo() {
    // Intentionally empty.
  }

  public AdviserPredicateInfo(String displayName, String className,
      Iterable<String> availableParameters, Map<String, String> executionParameters) {
    super(displayName, className, availableParameters);
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
    return Objects.equal(this.displayName, adviserPredicateInfo.displayName) &&
        Objects.equal(this.className, adviserPredicateInfo.className) &&
        Objects.equal(this.availableParameters, adviserPredicateInfo.availableParameters) &&
        Objects.equal(this.executionParameters, adviserPredicateInfo.executionParameters);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(
        displayName, className, availableParameters, executionParameters, executionParameters);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("displayName", displayName)
        .add("className", className)
        .add("availableParameters", availableParameters)
        .add("executionParameters", executionParameters)
        .toString();
  }
}
