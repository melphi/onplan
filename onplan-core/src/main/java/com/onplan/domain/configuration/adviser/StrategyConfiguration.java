package com.onplan.domain.configuration.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.onplan.domain.PersistentObject;

import java.util.Map;
import java.util.Set;

public class StrategyConfiguration implements PersistentObject {
  private String id;
  private String className;
  private Map<String, String> executionParameters;
  private Set<String> instruments;

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public Map<String, String> getExecutionParameters() {
    return executionParameters;
  }

  public void setExecutionParameters(Map<String, String> parameters) {
    this.executionParameters = parameters;
  }

  public Set<String> getInstruments() {
    return instruments;
  }

  public void setInstruments(Set<String> instruments) {
    this.instruments = instruments;
  }

  public StrategyConfiguration() {
    // Intentionally empty.
  }

  public StrategyConfiguration(String id, String className, Map<String, String> executionParameters,
      Set<String> instruments) {
    this.id = id;
    this.className = className;
    this.executionParameters = executionParameters;
    this.instruments = instruments;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StrategyConfiguration strategyConfiguration = (StrategyConfiguration) o;
    return Objects.equal(this.id, strategyConfiguration.id) &&
        Objects.equal(this.className, strategyConfiguration.className) &&
        Objects.equal(this.executionParameters, strategyConfiguration.executionParameters) &&
        Objects.equal(this.instruments, strategyConfiguration.instruments);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, className, executionParameters, instruments);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("className", className)
        .add("executionParameters", executionParameters)
        .add("instruments", instruments)
        .toString();
  }
}
