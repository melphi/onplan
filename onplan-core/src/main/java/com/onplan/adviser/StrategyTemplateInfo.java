package com.onplan.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Collection;

public class StrategyTemplateInfo implements Serializable {
  protected String displayName;
  protected String className;
  protected Collection<String> availableParameters;

  public String getDisplayName() {
    return displayName;
  }

  public String getClassName() {
    return className;
  }

  public Collection<String> getAvailableParameters() {
    return availableParameters;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public void setAvailableParameters(Collection<String> availableParameters) {
    this.availableParameters = availableParameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StrategyInfo strategyInfo = (StrategyInfo) o;
    return Objects.equal(this.displayName, strategyInfo.displayName) &&
        Objects.equal(this.className, strategyInfo.className) &&
        Objects.equal(this.availableParameters, strategyInfo.availableParameters);
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
