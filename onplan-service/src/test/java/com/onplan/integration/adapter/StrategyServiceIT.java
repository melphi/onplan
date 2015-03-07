package com.onplan.integration.adapter;

import com.onplan.adviser.StrategyInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.adviser.strategy.Strategy;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.integration.AbstractIT;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.service.StrategyService;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class StrategyServiceIT extends AbstractIT {
  private StrategyService strategyService = injector.getInstance(StrategyService.class);
  private StrategyConfigurationDao strategyConfigurationDao =
      injector.getInstance(StrategyConfigurationDao.class);

  @Test
  public void testGetStrategiesTemplateInfo() {
    List<TemplateInfo> result = strategyService.getStrategiesTemplateInfo();
    assertTrue(!result.isEmpty());
    for (TemplateInfo templateInfo : result) {
      assertNotNull(templateInfo.getDisplayName());
      assertNotNull(templateInfo.getClassName());
      assertNotNull(templateInfo.getAvailableParameters());
    }
  }

  @Test
  public void testGetStrategiesInfo() {
    List<StrategyInfo> result = strategyService.getStrategiesInfo();
    assertTrue(!result.isEmpty());
    for (StrategyInfo strategyInfo : result) {
      assertNotNull(strategyInfo.getId());
      assertNotNull(strategyInfo.getDisplayName());
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
