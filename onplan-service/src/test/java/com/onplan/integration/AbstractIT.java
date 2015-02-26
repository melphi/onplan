package com.onplan.integration;

import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract class AbstractIT {
  protected final Injector injector;

  public AbstractIT() {
    injector = Guice.createInjector(new GuiceTestingModule());
  }
}
