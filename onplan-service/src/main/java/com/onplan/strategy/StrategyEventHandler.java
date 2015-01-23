package com.onplan.strategy;

import com.onplan.domain.configuration.EventHandlerConfiguration;

import static com.google.common.base.Preconditions.checkNotNull;

public class StrategyEventHandler {
  private final StrategyEventType eventType;
  private final StrategyEventScheduler eventScheduler;

  public StrategyEventType getEventType() {
    return eventType;
  }

  public StrategyEventScheduler getEventScheduler() {
    return eventScheduler;
  }

  public StrategyEventHandler(EventHandlerConfiguration eventHandlerConfiguration) {
    checkNotNull(eventHandlerConfiguration);
    this.eventScheduler =
        new StrategyEventScheduler(eventHandlerConfiguration.getSchedulerConfiguration());
    this.eventType = checkNotNull(eventHandlerConfiguration.getEventType());
  }
}
