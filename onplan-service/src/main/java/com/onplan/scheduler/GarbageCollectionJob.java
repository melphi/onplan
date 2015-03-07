package com.onplan.scheduler;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@DisallowConcurrentExecution
public class GarbageCollectionJob implements Job {
  private static final Logger LOGGER = Logger.getLogger(GarbageCollectionJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LOGGER.warn("Calling System.gc().");
    System.gc();
  }
}
