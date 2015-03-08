package com.onplan.web.rest;

import com.onplan.domain.transitory.VirtualMachineInfo;
import com.onplan.service.VirtualMachineServiceRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest/virtualmachine", produces = "application/json")
public class VirtualMachineServiceRest implements VirtualMachineServiceRemote {
  @Autowired
  private VirtualMachineServiceRemote virtualMachineService;

  @Override
  @RequestMapping(value = "/virtualmachineinfo")
  public VirtualMachineInfo getVirtualMachineInfo() {
    return virtualMachineService.getVirtualMachineInfo();
  }
}
