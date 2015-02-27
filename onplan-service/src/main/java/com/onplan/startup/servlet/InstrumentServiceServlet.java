package com.onplan.startup.servlet;

import com.caucho.hessian.server.HessianServlet;
import com.onplan.adapter.InstrumentService;

import javax.inject.Inject;
import javax.servlet.ServletException;

public class InstrumentServiceServlet extends HessianServlet {
  @Inject
  private InstrumentService instrumentService;

  @Override
  public void init() throws ServletException {
    setHome(instrumentService);
    setHomeAPI(InstrumentService.class);
  }
}
