package com.onplan.service.placeholder;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.AlertInfo;
import com.onplan.adviser.alert.Alert;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.domain.transitory.PriceTick;
import com.onplan.service.AlertService;
import com.onplan.service.InstrumentSubscriptionListener;

import javax.inject.Singleton;
import java.util.List;

/**
 * To save resources the AlertService can be replaced by this placeholder.
 */
@Singleton
public final class AlertServicePlaceholder implements AlertService {
  private static final List<Alert> ALERTS = ImmutableList.of();

  @Override
  public void onPriceTick(PriceTick priceTick) {
    // Intentionally empty.
  }

  @Override
  public void setInstrumentSubscriptionListener(
      InstrumentSubscriptionListener instrumentSubscriptionListener) {
    // Intentionally empty.
  }

  @Override
  public List<Alert> getAlerts() {
    return ALERTS;
  }

  @Override
  public boolean hasAlerts() {
    return false;
  }

  @Override
  public void loadAllAlerts() throws Exception {
    // Intentionally empty.
  }

  @Override
  public void unLoadAllAlerts() throws Exception {
    // Intentionally empty.
  }

  @Override
  public boolean removeAlert(String alertId) throws Exception {
    return false;
  }

  @Override
  public String addAlert(AlertConfiguration alertConfigurationConfiguration) throws Exception {
    return null;
  }

  @Override
  public void loadSampleAlerts() throws Exception {
    // Intentionally empty.
  }

  @Override
  public List<AlertInfo> getAlertsInfo() {
    return null;
  }
}
