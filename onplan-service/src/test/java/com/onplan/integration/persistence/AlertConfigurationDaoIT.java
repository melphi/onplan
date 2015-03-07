package com.onplan.integration.persistence;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.persistence.AlertConfigurationDao;
import org.joda.time.DateTime;

import java.util.Collection;

public class AlertConfigurationDaoIT extends AbstractDaoIT<AlertConfiguration> {
  public AlertConfigurationDaoIT() {
    super(AlertConfigurationDao.class);
  }

  @Override
  protected AlertConfiguration createSampleObjectWithNullId() {
    return new AlertConfiguration(
        null,
        "Alert message %d",
        "instrumentId%d",
        createAdviserPredicates(),
        DateTime.now().getMillis(),
        true);
  }

  @Override
  protected Collection<AlertConfiguration> createSampleObjectsWithNullId() {
    ImmutableList.Builder<AlertConfiguration> result = ImmutableList.builder();
    for (int i = 0; i < INITIAL_COLLECTION_SIZE; i++) {
      result.add(new AlertConfiguration(
          null,
          String.format("Alert message %d", i),
          String.format("instrumentId%d", i),
          createAdviserPredicates(),
          DateTime.now().getMillis(),
          i > 5));
    }
    return result.build();
  }

  private Collection<AdviserPredicateConfiguration> createAdviserPredicates() {
    ImmutableList.Builder<AdviserPredicateConfiguration> result = ImmutableList.builder();
    for (int i = 0; i < 5; i ++) {
      result.add(new AdviserPredicateConfiguration(
          String.format("org.class.name%d", i),
          ImmutableMap.of("param1", "value1", "param2", "value2")
      ));
    }
    return result.build();
  }
}
