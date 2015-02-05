package com.onplan.scheduler;

import com.google.common.base.MoreObjects;
import org.apache.log4j.Logger;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collection;

public class GarbageCollectionJob implements Runnable {
  private static final Logger LOGGER = Logger.getLogger(GarbageCollectionJob.class);

  @Override
  public void run() {
    // TODO(robertom): Manage virtual machine statistics in a more meaningful way.
    LOGGER.info(String.format(
        "Virtual machine statistics: [%s].", getVirtualMachineStatistics()));
    LOGGER.warn("Calling System.gc()");
    System.gc();
  }

  private VirtualMachineStatistics getVirtualMachineStatistics() {
    long collectionsCount = 0;
    long accumulatedTime = 0;
    Collection<GarbageCollectorMXBean> garbageCollectors =
        ManagementFactory.getGarbageCollectorMXBeans();
    for (GarbageCollectorMXBean garbageCollector : garbageCollectors) {
      collectionsCount += garbageCollector.getCollectionCount();
      accumulatedTime += garbageCollector.getCollectionTime();
    }
    return new VirtualMachineStatistics(
        collectionsCount, Math.floorDiv(accumulatedTime, collectionsCount));
  }

  private class VirtualMachineStatistics {
    private final long collectionsCount;
    private final long averageCollectionTime;

    private VirtualMachineStatistics(long collectionsCount, long averageCollectionTime) {
      this.collectionsCount = collectionsCount;
      this.averageCollectionTime = averageCollectionTime;
    }

    public long getCollectionsCount() {
      return collectionsCount;
    }

    public long getAverageCollectionTime() {
      return averageCollectionTime;
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("collectionsCount", collectionsCount)
          .add("averageCollectionTime", averageCollectionTime)
          .toString();
    }  }
}
