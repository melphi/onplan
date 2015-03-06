package com.onplan.startup.servlet;

import com.caucho.hessian.server.HessianServlet;
import com.onplan.service.StrategyService;
import com.onplan.service.StrategyServiceRemote;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;

@Singleton
public class StrategyServiceServlet extends HessianServlet {
  @Inject
  private StrategyService strategyService;

  @Override
  public void init() throws ServletException {
    setHome(strategyService);
    setHomeAPI(StrategyServiceRemote.class);
  }
}
