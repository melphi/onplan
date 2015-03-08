package com.onplan.startup.servlet;

import com.caucho.hessian.server.HessianServlet;
import com.onplan.service.VirtualMachineService;
import com.onplan.service.VirtualMachineServiceRemote;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

@Singleton
public class VirtualMachineServiceServlet extends HessianServlet {
  @Inject
  private VirtualMachineService virtualMachineService;

  @Override
  public void init() throws ServletException {
    setHome(virtualMachineService);
    setHomeAPI(VirtualMachineServiceRemote.class);
  }
}
