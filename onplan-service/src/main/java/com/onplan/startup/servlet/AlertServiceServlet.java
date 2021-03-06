package com.onplan.startup.servlet;

import com.caucho.hessian.server.HessianServlet;
import com.onplan.service.AlertService;
import com.onplan.service.AlertServiceRemote;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

@Singleton
public class AlertServiceServlet extends HessianServlet {
  @Inject
  private AlertService alertService;

  @Override
  public void init() throws ServletException {
    setHome(alertService);
    setHomeAPI(AlertServiceRemote.class);
  }
}
