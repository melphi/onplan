package com.onplan.startup;

import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides Guice support to scheduled Quartz jobs.
 */
public class GuiceJobFactory implements JobFactory {
  private static final Logger LOGGER = Logger.getLogger(GuiceJobFactory.class);

  @Inject
  private Injector injector;

  @Override
  public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler)
      throws SchedulerException {
    checkNotNull(injector);
    JobDetail jobDetail = triggerFiredBundle.getJobDetail();
    Class jobClass = jobDetail.getJobClass();
    try {
      return (Job) injector.getInstance(jobClass);
    } catch (Exception e) {
      LOGGER.error(
          String.format("Error while creating job class [%s]: [%s].", jobClass, e.getMessage()), e);
      throw new SchedulerException(e);
    }
  }
}
