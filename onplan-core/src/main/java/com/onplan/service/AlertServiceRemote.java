package com.onplan.service;

import com.onplan.adviser.AlertInfo;
import com.onplan.domain.configuration.AlertConfiguration;

import java.io.Serializable;
import java.util.List;

public interface AlertServiceRemote extends Serializable {
  /**
   * Removes an alert by its id.
   * @param alertId the alert id.
   */
  public void removeAlert(String alertId) throws Exception;

  /**
   * Registers and initializes an alert with the provided configuration. If the alert id is
   * assigned it replaces the previous alert values with the new data.
   * @param alertConfigurationConfiguration the alert configuration.
   */
  public void addAlert(AlertConfiguration alertConfigurationConfiguration) throws Exception;

  /**
   * Loads a collection of sample alerts, deleting and replacing all the ones which have been
   * instantiated.
   * @throws Exception error while loading the collection of sample alerts.
   */
  public void loadSampleAlerts() throws Exception;

  /**
   * Returns the collection of alerts info.
   */
  public List<AlertInfo> getAlertsInfo();
}
