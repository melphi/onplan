package com.onplan.startup.servlet;

import com.caucho.hessian.server.HessianServlet;
import com.onplan.connector.PriceService;
import com.onplan.service.PriceServiceRemote;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

@Singleton
public class PriceServiceServlet extends HessianServlet {
  @Inject
  private PriceService priceService;

  @Override
  public void init() throws ServletException {
    setHome(priceService);
    setHomeAPI(PriceServiceRemote.class);
  }
}
