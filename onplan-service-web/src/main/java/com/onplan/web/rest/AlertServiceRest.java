package com.onplan.web.rest;

import com.onplan.adviser.AlertInfo;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.service.AlertServiceRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/alerts", produces = "application/json")
public class AlertServiceRest implements AlertServiceRemote {
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
  @RequestMapping(value = "/loadsamplealerts", method = RequestMethod.GET)
  public void loadSampleAlerts() throws Exception {
    alertService.loadSampleAlerts();
  }

  @Override
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public List<AlertInfo> getAlertsInfo() {
    return alertService.getAlertsInfo();
  }
}
