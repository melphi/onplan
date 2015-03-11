package com.onplan.startup.servlet;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.onplan.processing.PriceServiceBus;
import com.onplan.scheduler.GarbageCollectionJob;
import com.onplan.scheduler.ServicesActivationJob;
import com.onplan.startup.GuiceJobFactory;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Singleton
public class StartUpServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger(StartUpServlet.class);
  private static final String HOME_PAGE_CONTENT = "<html><body>" +
      "<h1>OnPlan Service</h1>" +
      "<p>Service is running!</p>" +
      "<p>Click <a href=\"resetConfiguration\">here</a> to reset the configuration.</p>" +
      "</body></html>";

  @Inject
  private GuiceJobFactory guiceJobFactory;

  @Inject
  private Injector injector;

  @Override
  public void init() throws ServletException {
    LOGGER.info("Application startup.");
    startPriceServiceBus();
    startScheduler();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.getWriter().print(HOME_PAGE_CONTENT);
    resp.getWriter().flush();
  }

  private void startScheduler() {
    LOGGER.info("Starting scheduler.");
    try {
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.setJobFactory(guiceJobFactory);
      for (Map.Entry<JobDetail, Trigger> entry : createJobDetails().entrySet()) {
        scheduler.scheduleJob(entry.getKey(), entry.getValue());
      }
      scheduler.start();
      LOGGER.info("Scheduler started.");
    } catch (SchedulerException e) {
      LOGGER.error(String.format("Error while starting scheduler: [%s]", e.getMessage()), e);
    }
  }

  private void startPriceServiceBus() {
    LOGGER.info("Starting price service bus.");
    injector.getInstance(PriceServiceBus.class);
  }

  private Map<JobDetail, Trigger> createJobDetails() {
    ImmutableMap.Builder<JobDetail, Trigger> result = ImmutableMap.builder();
    result.put(
        newJob(ServicesActivationJob.class)
            .withIdentity("servicesActivationJob")
            .build(),
        newTrigger()
            .withIdentity("everyMinuteTrigger")
            .withSchedule(simpleSchedule().withIntervalInMinutes(1).repeatForever())
            .build());
    result.put(
        newJob(GarbageCollectionJob.class)
            .withIdentity("garbageCollectionJob")
            .build(),
        newTrigger()
            .withIdentity("preMarketTrigger")
            .withSchedule(cronSchedule("0 55 6 * * ?"))
            .build());
    return result.build();
  }
}
