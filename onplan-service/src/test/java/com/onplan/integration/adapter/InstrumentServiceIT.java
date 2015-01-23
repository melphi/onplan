package com.onplan.integration.adapter;

import com.google.common.collect.Lists;
import com.onplan.adapter.InstrumentService;
import com.onplan.domain.InstrumentInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InstrumentServiceIT extends AbstractIntegrationTest {
  private static final String SEARCH_EXAMPLE_1 = "EUR";

  @Autowired
  private InstrumentService instrumentService;

  @Test
  public void testFindInstrumentsBySearchTerm() throws Exception {
    List<InstrumentInfo> result = instrumentService.findInstrumentsBySearchTerm(SEARCH_EXAMPLE_1);

    List<String> instrumentIds = Lists.newArrayList();
    assertTrue(!result.isEmpty());
    for (InstrumentInfo instrumentInfo : result) {
      assertNotNull(instrumentInfo.getInstrumentType());
      assertNotNull(instrumentInfo.getInstrumentName());
      assertNotNull(instrumentInfo.getExpiry());
      assertNotNull(instrumentInfo.getInstrumentId());
      assertNotNull(instrumentInfo.getPriceMinimalDecimalPosition());
      // Assert instrumentId uniqueness.
      assertTrue(!instrumentIds.contains(instrumentInfo.getInstrumentId()));
      instrumentIds.add(instrumentInfo.getInstrumentId());
    }
  }
}
