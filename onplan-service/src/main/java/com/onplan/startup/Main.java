package com.onplan.startup;

import com.google.inject.servlet.GuiceFilter;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.FilterInfo;
import io.undertow.servlet.api.ListenerInfo;
import org.apache.log4j.Logger;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;

/**
 * Application entry point, executes the application via an embedded server.
 */
// TODO(robertom): Check https://github.com/rohitdev/project-templates/tree/master/UndertowSpringDeployment
// for a better implementation.
public class Main {
  private static final Logger logger = Logger.getLogger(Main.class);
  private static final String GUICE_FILTER_NAME = "guiceFilter";
  private static final String DEFAULT_DEPLOYMENT_NAME = "onplan-server";
  private static final String DEFAULT_HOST = "localhost";
  private static final String DEFAULT_CONTEXT_PATH = "/";
  private static final int DEFAULT_PORT = 8181;

  public static void main(String[] args) {
    logger.info("Starting server.");
    buildServer().start();
  }

  private static Undertow buildServer() {
    FilterInfo guiceFilter = new FilterInfo(GUICE_FILTER_NAME, GuiceFilter.class);
    ListenerInfo guiceListener = new ListenerInfo(GuiceListener.class);

    DeploymentInfo deploymentInfo = Servlets.deployment()
        .setClassLoader(Main.class.getClassLoader())
        .setContextPath(DEFAULT_CONTEXT_PATH)
        .addFilter(guiceFilter)
        .addFilterUrlMapping(GUICE_FILTER_NAME, "/*", DispatcherType.ASYNC)
        .addListener(guiceListener)
        .setDeploymentName(DEFAULT_DEPLOYMENT_NAME);

    DeploymentManager manager = Servlets.defaultContainer()
        .addDeployment(deploymentInfo);
    manager.deploy();

    PathHandler path = null;
    try {
      path = Handlers.path(Handlers.redirect(DEFAULT_CONTEXT_PATH))
          .addPrefixPath(DEFAULT_CONTEXT_PATH, manager.start());
    } catch (ServletException e) {
      logger.error(e);
      throw new IllegalArgumentException(e);
    }

    return Undertow.builder()
        .addHttpListener(DEFAULT_PORT, DEFAULT_HOST)
        .setHandler(path)
        .build();
  }
}
