package com.onplan.web.integration.rest;

import com.onplan.adviser.StrategyInfo;
import com.onplan.web.rest.StrategyServiceRest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StrategyServiceRestIT extends AbstractIntegrationTest {
  @Autowired
  private StrategyServiceRest strategyServiceRest;

  @Test
  public void testAvailableStrategies() {
    List<StrategyInfo> result = strategyServiceRest.getStrategiesInfo();
    assertTrue(!result.isEmpty());
    for (StrategyInfo strategyInfo : result) {
      assertNotNull(strategyInfo.getDisplayName());
      assertNotNull(strategyInfo.getAvailableParameters());
    }
  }
}
