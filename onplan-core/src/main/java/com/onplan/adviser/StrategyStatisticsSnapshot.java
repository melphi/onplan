package com.onplan.adviser;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

/**
 * Strategy statistics snapshot. The completion time is the difference between the tick
 * received time and the strategy completion time, this includes the delay due to any other
 * strategy or routine which is executed before this strategy.
 */
public final class StrategyStatisticsSnapshot implements Serializable {
  private final long lastReceivedTickTimestamp;
  private final long receivedTicks;
  private final long eventsDispatchedCounter;
  private final long lastCompletionNanoTime;
  private final long maxCompletionNanoTime;
  private final long averageCompletionNanoTime;

  /**
   * Returns the completion time for the last received tick.
   */
  public long getLastCompletionNanoTime() {
    return lastCompletionNanoTime;
  }

  public long getEventsDispatchedCounter() {
    return eventsDispatchedCounter;
  }

  public long getLastReceivedTickTimestamp() {
    return lastReceivedTickTimestamp;
  }

  public long getReceivedTicks() {
    return receivedTicks;
  }

  public long getMaxCompletionNanoTime() {
    return maxCompletionNanoTime;
  }

  public long getAverageCompletionNanoTime() {
    return averageCompletionNanoTime;
  }

  public StrategyStatisticsSnapshot(long lastReceivedTickTimestamp, long receivedTicks,
      long eventsDispatchedCounter, long lastCompletionNanoTime, long maxCompletionNanoTime,
      long averageCompletionNanoTime) {
    this.lastReceivedTickTimestamp = lastReceivedTickTimestamp;
    this.receivedTicks = receivedTicks;
    this.eventsDispatchedCounter = eventsDispatchedCounter;
    this.lastCompletionNanoTime = lastCompletionNanoTime;
    this.maxCompletionNanoTime = maxCompletionNanoTime;
    this.averageCompletionNanoTime = averageCompletionNanoTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    StrategyStatisticsSnapshot strategyStatistics = (StrategyStatisticsSnapshot) o;
    return Objects.equal(
            this.lastReceivedTickTimestamp, strategyStatistics.lastReceivedTickTimestamp) &&
        Objects.equal(this.receivedTicks, strategyStatistics.receivedTicks) &&
        Objects.equal(this.eventsDispatchedCounter, strategyStatistics.eventsDispatchedCounter) &&
        Objects.equal(this.lastCompletionNanoTime, strategyStatistics.lastCompletionNanoTime) &&
        Objects.equal(this.maxCompletionNanoTime, strategyStatistics.maxCompletionNanoTime) &&
        Objects.equal(this.averageCompletionNanoTime, strategyStatistics.averageCompletionNanoTime);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lastReceivedTickTimestamp, receivedTicks, eventsDispatchedCounter,
        lastCompletionNanoTime, maxCompletionNanoTime, averageCompletionNanoTime);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("lastReceivedTickTimestamp", lastReceivedTickTimestamp)
        .add("receivedTicks", receivedTicks)
        .add("eventsDispatchedCounter", eventsDispatchedCounter)
        .add("lastCompletionNanoTime", lastCompletionNanoTime)
        .add("maxCompletionNanoTime", maxCompletionNanoTime)
        .add("averageCompletionNanoTime", averageCompletionNanoTime)
        .toString();
  }
}
