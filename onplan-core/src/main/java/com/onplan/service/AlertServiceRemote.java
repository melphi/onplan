package com.onplan.service;

import com.onplan.adviser.AlertInfo;
import com.onplan.domain.configuration.AlertConfiguration;

import java.io.Serializable;
import java.util.List;

public interface AlertServiceRemote extends Serializable {
  /**
   * Removes an alert by its id, returns true if the alert was found, false otherwise.
   *
   * @param alertId the alert id.
   */
  public boolean removeAlert(String alertId) throws Exception;

  /**
   * Loads an alert with the provided configuration. Returns the id assigned to the alert.
   * If the alert id is set then replaces the previous alert with the new data.
   *
   * @param alertConfigurationConfiguration the alert configuration.
   */
  public String addAlert(AlertConfiguration alertConfigurationConfiguration) throws Exception;

  /**
   * Loads a collection of sample alerts, deleting and replacing all the ones which have been
   * instantiated.
   *
   * @throws Exception error while loading the collection of sample alerts.
   */
  public void loadSampleAlerts() throws Exception;

  /**
   * Returns the collection of alerts info.
   */
  public List<AlertInfo> getAlertsInfo();
}
