package com.onplan.service.impl;

import com.onplan.domain.transitory.VirtualMachineInfo;
import com.onplan.service.VirtualMachineServiceRemote;

import javax.inject.Singleton;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collection;

@Singleton
public final class VirtualMachineServiceImpl implements VirtualMachineServiceRemote {
  @Override
  public VirtualMachineInfo getVirtualMachineInfo() {
    long collectionsCount = 0;
    long accumulatedTime = 0;
    double averageCollectionTime = 0;
    Collection<GarbageCollectorMXBean> garbageCollectors =
        ManagementFactory.getGarbageCollectorMXBeans();
    for (GarbageCollectorMXBean garbageCollector : garbageCollectors) {
      collectionsCount += garbageCollector.getCollectionCount();
      accumulatedTime += garbageCollector.getCollectionTime();
    }
    if(collectionsCount > 0) {
      averageCollectionTime = accumulatedTime / collectionsCount;
    }

    long freeMemory = Runtime.getRuntime().freeMemory();
    long totalMemory = Runtime.getRuntime().totalMemory();
    long maxMemory = Runtime.getRuntime().maxMemory();

    return new VirtualMachineInfo(maxMemory, totalMemory, freeMemory,
        collectionsCount, averageCollectionTime);
  }
}
