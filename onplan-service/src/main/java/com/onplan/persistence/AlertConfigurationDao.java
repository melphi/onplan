package com.onplan.persistence;

import com.onplan.domain.configuration.AlertConfiguration;

import java.util.List;

public interface AlertConfigurationDao extends GenericDao<AlertConfiguration> {
  public List<AlertConfiguration> getSampleAlertsConfiguration();
}
