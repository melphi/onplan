package com.onplan.startup;

import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.onplan.startup.servlet.StartUpServlet;

public class GuiceServletModule extends ServletModule {
  @Override
  protected void configureServlets() {
    bind(StartUpServlet.class).in(Singleton.class);
    serve("/").with(StartUpServlet.class);
  }
}
