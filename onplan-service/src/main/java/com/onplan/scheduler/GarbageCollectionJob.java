package com.onplan.scheduler;

import com.google.common.base.MoreObjects;
import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collection;

@DisallowConcurrentExecution
public class GarbageCollectionJob implements Job {
  private static final Logger LOGGER = Logger.getLogger(GarbageCollectionJob.class);

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

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    // TODO(robertom): Manage virtual machine statistics in a more meaningful way.
    LOGGER.info(String.format(
        "Virtual machine statistics: [%s].", getVirtualMachineStatistics()));
    LOGGER.warn("Calling System.gc()");
    System.gc();
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
