package com.onplan.startup;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.onplan.startup.servlet.*;

public class GuiceServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    bind(StartUpServlet.class).in(Singleton.class);

    // Servlet.
    serve("/").with(StartUpServlet.class);
    serve("/resetConfiguration").with(ResetConfigurationServlet.class);

    // Remoting servlet.
    serve("/instrumentService").with(InstrumentServiceServlet.class);
    serve("/priceService").with(PriceServiceServlet.class);
    serve("/alertService").with(AlertServiceServlet.class);
    serve("/strategyService").with(StrategyServiceServlet.class);
    serve("/virtualMachineService").with(VirtualMachineServiceServlet.class);
  }
}
