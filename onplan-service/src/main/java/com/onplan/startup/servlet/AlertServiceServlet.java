package com.onplan.startup.servlet;

import com.caucho.hessian.server.HessianServlet;
import com.onplan.service.AlertService;

import javax.inject.Inject;
import javax.servlet.ServletException;

public class AlertServiceServlet extends HessianServlet {
  @Inject
  private AlertService alertService;

  @Override
  public void init() throws ServletException {
    setHome(alertService);
    setHomeAPI(AlertService.class);
  }
}
