package com.onplan.domain.configuration;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class SchedulerConfiguration {
  private String validFromTime;
  private String validToTime;

  public String getValidFromTime() {
    return validFromTime;
  }

  public void setValidFromTime(String validFromTime) {
    this.validFromTime = validFromTime;
  }

  public String getValidToTime() {
    return validToTime;
  }

  public void setValidToTime(String validToTime) {
    this.validToTime = validToTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SchedulerConfiguration schedulerConfiguration = (SchedulerConfiguration) o;
    return Objects.equal(this.validFromTime, schedulerConfiguration.validFromTime) &&
        Objects.equal(this.validToTime, schedulerConfiguration.validToTime);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(validFromTime, validToTime);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("validFromTime", validFromTime)
        .add("validToTime", validToTime)
        .toString();
  }
}
