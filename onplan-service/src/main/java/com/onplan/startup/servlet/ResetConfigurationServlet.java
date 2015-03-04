package com.onplan.startup.servlet;

import com.onplan.service.AlertService;
import com.onplan.service.StrategyService;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResetConfigurationServlet extends HttpServlet {
  private static final String SUCCESS_PAGE_CONTENT = "<html><body>" +
      "<h1>OnPlan Service</h1>" +
      "<p>Configuration reset.</p>" +
      "</body></html>";
  private static final String ERROR_PAGE_CONTENT = "<html><body>" +
      "<h1>OnPlan Service</h1>" +
      "<p>Error while resetting configuration:</p>" +
      "<p>%s</p>" +
      "</body></html>";

  @Inject
  private StrategyService strategyService;

  @Inject
  private AlertService alertService;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {
      resetConfiguration();
      resp.getWriter().print(SUCCESS_PAGE_CONTENT);
    } catch (Exception e) {
      resp.getWriter().print(
          String.format(ERROR_PAGE_CONTENT, e.getMessage()));
    }
    resp.getWriter().flush();
  }

  private void resetConfiguration() throws Exception{
    alertService.loadSampleAlerts();
    strategyService.loadSampleStrategies();
  }
}
