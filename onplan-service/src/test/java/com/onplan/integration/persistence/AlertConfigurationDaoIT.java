package com.onplan.integration.persistence;

import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.persistence.AlertConfigurationDao;

import java.util.Collection;

import static com.onplan.factory.TestingAlertConfigurationFactory.createSampleAlertConfigurationWithNullId;
import static com.onplan.factory.TestingAlertConfigurationFactory.createSampleAlertConfigurationsWithNullId;

public class AlertConfigurationDaoIT extends AbstractDaoIT<AlertConfiguration> {
  public AlertConfigurationDaoIT() {
    super(AlertConfigurationDao.class);
  }

  @Override
  protected AlertConfiguration createSampleObjectWithNullId() {
    return createSampleAlertConfigurationWithNullId();
  }

  @Override
  protected Collection<AlertConfiguration> createSampleObjectsWithNullId() {
    return createSampleAlertConfigurationsWithNullId();
  }
}
