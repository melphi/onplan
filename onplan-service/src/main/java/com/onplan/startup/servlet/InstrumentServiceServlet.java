package com.onplan.startup.servlet;

import com.caucho.hessian.server.HessianServlet;
import com.onplan.connector.InstrumentService;
import com.onplan.service.InstrumentServiceRemote;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

@Singleton
public class InstrumentServiceServlet extends HessianServlet {
  @Inject
  private InstrumentService instrumentService;

  @Override
  public void init() throws ServletException {
    setHome(instrumentService);
    setHomeAPI(InstrumentServiceRemote.class);
  }
}
