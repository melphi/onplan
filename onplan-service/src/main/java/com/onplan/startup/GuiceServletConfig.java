package com.onplan.startup;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class GuiceServletConfig extends GuiceServletContextListener {
  @Override
  protected Injector getInjector() {
    return Guice.createInjector(
        new IgIndexGuiceModule(),
        new GuiceModule(),
        new GuiceServletModule());
  }
}
