package com.onplan.integration.persistence;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.domain.configuration.adviser.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;

import java.util.Collection;

public class StrategyConfigurationDaoIT extends AbstractDaoIT<StrategyConfiguration> {
  public StrategyConfigurationDaoIT() {
    super(StrategyConfigurationDao.class);
  }

  @Override
  protected StrategyConfiguration createSampleObjectWithNullId() {
    return new StrategyConfiguration(
        null,
        "className",
        ImmutableMap.of("key1", "value1", "key2", "value2"),
        ImmutableSet.of("instrument1", "instrument2"));
  }

  @Override
  protected Collection<StrategyConfiguration> createSampleObjectsWithNullId() {
    ImmutableList.Builder<StrategyConfiguration> result = ImmutableList.builder();
    for (int i = 0; i < INITIAL_COLLECTION_SIZE; i++) {
      result.add(new StrategyConfiguration(
          null,
          String.format("className-%s", i),
          ImmutableMap.of("key1", "value1", "key2", "value2"),
          ImmutableSet.of("instrument1", "instrument2")));
    }
    return result.build();
  }
}
