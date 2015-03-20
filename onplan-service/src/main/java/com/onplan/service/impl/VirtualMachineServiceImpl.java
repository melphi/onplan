package com.onplan.service.impl;

import com.onplan.domain.transitory.VirtualMachineInfo;
import com.onplan.service.VirtualMachineService;

import javax.inject.Singleton;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Collection;

@Singleton
public final class VirtualMachineServiceImpl implements VirtualMachineService {
  @Override
  public VirtualMachineInfo getVirtualMachineInfo() {
    long collectionsCount = 0;
    long cumulatedTime = 0;
    double averageCollectionTime = 0;
    Collection<GarbageCollectorMXBean> garbageCollectors =
        ManagementFactory.getGarbageCollectorMXBeans();
    for (GarbageCollectorMXBean garbageCollector : garbageCollectors) {
      collectionsCount += garbageCollector.getCollectionCount();
      cumulatedTime += garbageCollector.getCollectionTime();
    }
    if(collectionsCount > 0) {
      averageCollectionTime = cumulatedTime / collectionsCount;
    }

    int availableProcessors = Runtime.getRuntime().availableProcessors();
    long freeMemory = Runtime.getRuntime().freeMemory();
    long totalMemory = Runtime.getRuntime().totalMemory();
    long maxMemory = Runtime.getRuntime().maxMemory();

    return new VirtualMachineInfo(availableProcessors, maxMemory, totalMemory, freeMemory,
        collectionsCount, averageCollectionTime);
  }
}
