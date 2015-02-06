package com.onplan.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public class TemplateInfo implements Serializable {
  protected String displayName;
  protected String className;
  protected Iterable<String> availableParameters;

  public TemplateInfo() {
    // Intentionally empty.
  }

  public TemplateInfo(String displayName, String className, Iterable<String> availableParameters) {
    this.displayName = displayName;
    this.className = className;
    this.availableParameters = availableParameters;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getClassName() {
    return className;
  }

  public Iterable<String> getAvailableParameters() {
    return availableParameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TemplateInfo adviserPredicateInfo = (TemplateInfo) o;
    return Objects.equal(this.displayName, adviserPredicateInfo.displayName) &&
        Objects.equal(this.className, adviserPredicateInfo.className) &&
        Objects.equal(this.availableParameters, adviserPredicateInfo.availableParameters);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(displayName, className, availableParameters);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("displayName", displayName)
        .add("className", className)
        .add("availableParameters", availableParameters)
        .toString();
  }
}
