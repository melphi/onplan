package com.onplan.web.rest;

import com.onplan.adviser.StrategyInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.service.StrategyServiceRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/strategies", produces = "application/json")
public class StrategyServiceRest implements StrategyServiceRemote {
  @Autowired
  private StrategyServiceRemote strategyService;

  @Override
  public void removeStrategy(String strategyId) throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void addStrategy(StrategyConfiguration strategyConfiguration) throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void loadSampleStrategies() throws Exception {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  @RequestMapping(value = "/")
  public List<StrategyInfo> getStrategiesInfo() {
    return strategyService.getStrategiesInfo();
  }

  @Override
  public List<TemplateInfo> getStrategiesTemplateInfo() {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public TemplateInfo getStrategyTemplateInfo(String className) {
    throw new IllegalArgumentException("Not yet implemented.");
  }
}
