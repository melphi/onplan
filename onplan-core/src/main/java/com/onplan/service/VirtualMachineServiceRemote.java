package com.onplan.service;

import com.onplan.domain.transitory.VirtualMachineInfo;

public interface VirtualMachineServiceRemote {
  /**
   * Returns the JVM information.
   */
  VirtualMachineInfo getVirtualMachineInfo();
}
