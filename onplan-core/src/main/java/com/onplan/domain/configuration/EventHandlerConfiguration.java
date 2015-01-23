package com.onplan.domain.configuration;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.onplan.strategy.StrategyEventType;

public class EventHandlerConfiguration {
  private StrategyEventType eventType;
  private SchedulerConfiguration schedulerConfiguration;

  public StrategyEventType getEventType() {
    return eventType;
  }

  public void setEventType(StrategyEventType eventType) {
    this.eventType = eventType;
  }

  public SchedulerConfiguration getSchedulerConfiguration() {
    return schedulerConfiguration;
  }

  public void setSchedulerConfiguration(SchedulerConfiguration scheduler) {
    this.schedulerConfiguration = scheduler;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EventHandlerConfiguration eventHandlerConfiguration = (EventHandlerConfiguration) o;
    return Objects.equal(this.eventType, eventHandlerConfiguration.eventType) &&
        Objects.equal(
            this.schedulerConfiguration, eventHandlerConfiguration.schedulerConfiguration);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(eventType, schedulerConfiguration);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("eventType", eventType)
        .add("schedulerConfiguration", schedulerConfiguration)
        .toString();
  }
}
