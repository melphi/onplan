package com.onplan.persistence;

import com.onplan.domain.configuration.adviser.AlertConfiguration;

import java.util.List;

public interface AlertConfigurationDao extends GenericDao<AlertConfiguration> {
  public List<AlertConfiguration> getSampleAlertsConfiguration();
}
