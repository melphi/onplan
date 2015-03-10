package com.onplan.integration.connector;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.onplan.connector.InstrumentService;
import com.onplan.domain.transitory.InstrumentInfo;
import com.onplan.integration.AbstractIT;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InstrumentServiceIT extends AbstractIT {
  private static final String SEARCH_EXAMPLE = "EUR";
  private static final String EPIC_EXAMPLE = "CF.EURAUD.MAR";

  private InstrumentService instrumentService = injector.getInstance(InstrumentService.class);

  @Test
  public void testFindInstrumentsBySearchTerm() throws Exception {
    List<InstrumentInfo> result = instrumentService.findInstrumentsBySearchTerm(SEARCH_EXAMPLE);
    List<String> instrumentIds = Lists.newArrayList();
    assertTrue(!result.isEmpty());
    for (InstrumentInfo instrumentInfo : result) {
      checkInstrumentInfo(instrumentInfo);
      // Assert instrumentId uniqueness.
      assertTrue(!instrumentIds.contains(instrumentInfo.getInstrumentId()));
      instrumentIds.add(instrumentInfo.getInstrumentId());
    }
  }

  @Test
  public void testGetInstrumentInfo() throws Exception {
    InstrumentInfo instrumentInfo = instrumentService.getInstrumentInfo(EPIC_EXAMPLE);
    checkInstrumentInfo(instrumentInfo);
  }

  private static void checkInstrumentInfo(final InstrumentInfo instrumentInfo) {
    assertNotNull(instrumentInfo.getInstrumentType());
    assertTrue(!Strings.isNullOrEmpty(instrumentInfo.getInstrumentName()));
    assertTrue(!Strings.isNullOrEmpty(instrumentInfo.getExpiry()));
    assertTrue(!Strings.isNullOrEmpty(instrumentInfo.getInstrumentId()));
    assertTrue(instrumentInfo.getPriceMinimalDecimalPosition() >= 0);
  }
}
