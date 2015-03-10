package com.onplan.integration;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractIT {
  protected final Injector injector;

  public AbstractIT() {
    this.injector = checkNotNull(Guice.createInjector(new GuiceTestingModule()));
  }
}
