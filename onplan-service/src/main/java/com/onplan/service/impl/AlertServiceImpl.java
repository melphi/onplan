package com.onplan.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onplan.adviser.AlertInfo;
import com.onplan.adviser.alert.Alert;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.domain.PriceTick;
import com.onplan.domain.configuration.adviser.AlertConfiguration;
import com.onplan.service.AlertService;

import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.adviser.predicate.AdviserPredicateUtil.createAdviserPredicateInfo;

@Singleton
public class AlertServiceImpl implements AlertService {
  private final List<Alert> alerts = Lists.newArrayList();
  private final Map<String, Iterable<Alert>> alertsMapping = Maps.newHashMap();

  private volatile boolean hasAlerts = false;

  @Override
  public void onPriceTick(final PriceTick priceTick) {
    checkNotNull(priceTick);
    synchronized (alertsMapping) {
      Iterable<Alert> alerts = alertsMapping.get(priceTick.getInstrumentId());
      for (Alert alert : alerts) {
        alert.onPriceTick(priceTick);
      }
    }
  }

  @Override
  public List<Alert> getAlerts() {
    return ImmutableList.copyOf(alerts);
  }

  @Override
  public boolean hasAlerts() {
    return hasAlerts;
  }

  @Override
  public void removeAlert(String alertId) throws Exception {
    synchronized (alertsMapping) {
      hasAlerts = !alerts.isEmpty();
      throw new IllegalArgumentException("Not yet implemented.");
    }
  }

  @Override
  public void addAlert(AlertConfiguration alertConfigurationConfiguration) throws Exception {
    synchronized (alertsMapping) {
      hasAlerts = !alerts.isEmpty();
      throw new IllegalArgumentException("Not yet implemented.");
    }
  }

  @Override
  public List<AlertInfo> getAlertsInfo() {
    ImmutableList.Builder result = ImmutableList.builder();
    for (Alert alert : alerts) {
      result.add(createAlertInfo(alert));
    }
    return result.build();
  }

  private AlertInfo createAlertInfo(final Alert alert) {
    checkNotNull(alert);
    ImmutableList.Builder predicatesInfo = ImmutableList.builder();
    for (AdviserPredicate adviserPredicate : alert.getPredicatesChain()) {
      predicatesInfo.add(createAdviserPredicateInfo(adviserPredicate));
    }
    return new AlertInfo(
        alert.getId(),
        predicatesInfo.build(),
        alert.getMessage(),
        alert.getCreatedOn(),
        alert.getLastFiredOn(),
        alert.getRepeat());
  }
}
