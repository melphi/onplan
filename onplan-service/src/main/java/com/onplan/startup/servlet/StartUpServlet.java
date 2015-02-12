package com.onplan.startup.servlet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.scheduler.AdapterServicesActivationJob;
import com.onplan.scheduler.GarbageCollectionJob;
import com.onplan.startup.GuiceJobFactory;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class StartUpServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger(StartUpServlet.class);

  @Inject
  private GuiceJobFactory guiceJobFactory;

  @Override
  public void init() throws ServletException {
    LOGGER.info("Application startup.");
    startScheduler();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.getWriter().print("Service is running!");
    resp.getWriter().flush();
  }

  private void startScheduler() {
    LOGGER.info("Starting scheduler.");
    try {
      Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
      scheduler.setJobFactory(guiceJobFactory);
      scheduler.scheduleJobs(createJobDetails(), true);
      scheduler.start();
      LOGGER.info("Scheduler started.");
    } catch (SchedulerException e) {
      LOGGER.error(String.format("Error while starting scheduler: [%s]", e.getMessage()), e);
    }
  }

  private Map<JobDetail, Set<? extends Trigger>> createJobDetails() {
    ImmutableMap.Builder<JobDetail, Set<? extends Trigger>> result = ImmutableMap.builder();
    Set<Trigger> adapterServicesActivationTriggers = ImmutableSet.of(
        newTrigger()
            .withIdentity("immediateTrigger")
            .startNow()
            .build(),
        newTrigger()
            .withIdentity("everyMinuteTrigger")
            .withSchedule(simpleSchedule().withIntervalInMinutes(1).repeatForever())
            .build());
    result.put(
        newJob(AdapterServicesActivationJob.class)
          .withIdentity("adapterServicesActivationJob")
          .build(),
        adapterServicesActivationTriggers);
    Set<Trigger> garbageCollectionTriggers = ImmutableSet.of(
        newTrigger()
            .withIdentity("preMarketTrigger")
            .withSchedule(cronSchedule("0 55 6 * * ?"))
            .build());
    result.put(
        newJob(GarbageCollectionJob.class)
          .withIdentity("garbageCollectionJob")
          .build(),
        garbageCollectionTriggers);
    return result.build();
  }
}
