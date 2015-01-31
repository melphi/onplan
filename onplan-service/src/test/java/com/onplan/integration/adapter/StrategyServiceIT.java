package com.onplan.integration.adapter;

import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.service.StrategyService;
import com.onplan.strategy.Strategy;
import com.onplan.strategy.StrategyInfo;
import com.onplan.strategy.StrategyTemplateInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class StrategyServiceIT extends AbstractIntegrationTest {
  @Autowired
  private StrategyService strategyService;

  @Autowired
  private StrategyConfigurationDao strategyConfigurationDao;

  @Test
  public void testGetStrategiesTemplateInfo() {
    List<StrategyTemplateInfo> result = strategyService.getStrategiesTemplateInfo();
    assertTrue(!result.isEmpty());
    for (StrategyTemplateInfo strategyTemplateInfo : result) {
      assertNotNull(strategyTemplateInfo.getName());
      assertNotNull(strategyTemplateInfo.getClassName());
      assertNotNull(strategyTemplateInfo.getAvailableParameters());
    }
  }

  @Test
  public void testGetStrategiesInfo() {
    List<StrategyInfo> result = strategyService.getStrategiesInfo();
    assertTrue(!result.isEmpty());
    for (StrategyInfo strategyInfo : result) {
      assertNotNull(strategyInfo.getId());
      assertNotNull(strategyInfo.getName());
      assertNotNull(strategyInfo.getClassName());
      assertNotNull(strategyInfo.getAvailableParameters());
      assertNotNull(strategyInfo.getRegisteredInstruments());
      assertTrue(!strategyInfo.getRegisteredInstruments().isEmpty());
    }
  }

  @Test
  public void testLoadSampleStrategies() throws Exception {
    strategyService.loadSampleStrategies();
    List<Strategy> strategies = strategyService.getStrategies();

    List<StrategyConfiguration> sampleConfigurations =
        strategyConfigurationDao.getSampleStrategiesConfiguration();
    List<StrategyConfiguration> savedConfigurations =
        strategyConfigurationDao.findAll();
    assertEquals(sampleConfigurations, savedConfigurations);

    fail("Complete this method by checking the loaded strategies and registered instruments.");
  }
}
