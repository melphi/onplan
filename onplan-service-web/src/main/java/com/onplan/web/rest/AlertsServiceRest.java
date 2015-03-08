package com.onplan.web.rest;

import com.onplan.adviser.AlertInfo;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.service.AlertServiceRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/alerts", produces = "application/json")
public class AlertsServiceRest implements AlertServiceRemote {
  @Autowired
  private AlertServiceRemote alertService;

  @Override
  public void removeAlert(String alertId) throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void addAlert(AlertConfiguration alertConfigurationConfiguration) throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void loadSampleAlerts() throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public List<AlertInfo> getAlertsInfo() {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
