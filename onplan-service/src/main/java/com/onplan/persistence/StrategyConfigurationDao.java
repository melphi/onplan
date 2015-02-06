package com.onplan.persistence;

import com.onplan.domain.configuration.adviser.StrategyConfiguration;

import java.util.List;

public interface StrategyConfigurationDao extends GenericDao<StrategyConfiguration> {
  public List<StrategyConfiguration> getSampleStrategiesConfiguration();
}
