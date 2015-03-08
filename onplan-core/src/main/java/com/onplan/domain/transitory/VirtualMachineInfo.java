package com.onplan.domain.transitory;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

public final class VirtualMachineInfo implements Serializable {
  private final int availableProcessors;
  private final long maxMemory;
  private final long totalMemory;
  private final long freeMemory;
  private final long collectionsCount;
  private final double averageCollectionTime;

  public VirtualMachineInfo(int availableProcessors, long maxMemory, long totalMemory,
      long freeMemory, long collectionsCount, double averageCollectionTime) {
    this.availableProcessors = availableProcessors;
    this.maxMemory = maxMemory;
    this.totalMemory = totalMemory;
    this.freeMemory = freeMemory;
    this.collectionsCount = collectionsCount;
    this.averageCollectionTime = averageCollectionTime;
  }

  public long getCollectionsCount() {
    return collectionsCount;
  }

  public double getAverageCollectionTime() {
    return averageCollectionTime;
  }

  public long getTotalMemory() {
    return totalMemory;
  }

  public long getMaxMemory() {
    return maxMemory;
  }

  public long getFreeMemory() {
    return freeMemory;
  }

  public int getAvailableProcessors() {
    return availableProcessors;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("availableProcessors", availableProcessors)
        .add("maxMemory", maxMemory)
        .add("totalMemory", totalMemory)
        .add("freeMemory", freeMemory)
        .add("collectionsCount", collectionsCount)
        .add("averageCollectionTime", averageCollectionTime)
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(availableProcessors, maxMemory, totalMemory, freeMemory,
        collectionsCount, averageCollectionTime);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VirtualMachineInfo virtualMachineInfo = (VirtualMachineInfo) o;
    return Objects.equal(this.availableProcessors, virtualMachineInfo.availableProcessors) &&
        Objects.equal(this.maxMemory, virtualMachineInfo.maxMemory) &&
        Objects.equal(this.totalMemory, virtualMachineInfo.totalMemory) &&
        Objects.equal(this.freeMemory, virtualMachineInfo.freeMemory) &&
        Objects.equal(this.collectionsCount, virtualMachineInfo.collectionsCount) &&
        Objects.equal(this.averageCollectionTime, virtualMachineInfo.averageCollectionTime);
  }
}
