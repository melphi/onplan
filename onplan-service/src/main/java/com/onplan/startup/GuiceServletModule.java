package com.onplan.startup;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.onplan.startup.servlet.AlertServiceServlet;
import com.onplan.startup.servlet.InstrumentServiceServlet;
import com.onplan.startup.servlet.StartUpServlet;

public class GuiceServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    bind(StartUpServlet.class).in(Singleton.class);
    bind(AlertServiceServlet.class).in(Singleton.class);
    bind(InstrumentServiceServlet.class).in(Singleton.class);

    serve("/").with(StartUpServlet.class);
    serve("/alertService").with(AlertServiceServlet.class);
    serve("/instrumentService").with(InstrumentServiceServlet.class);
  }
}
