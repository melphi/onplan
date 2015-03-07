package com.onplan.adapter.igindex.client;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.onplan.domain.transitory.InstrumentInfo;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IgIndexResponseParserTest {
  private static final String FILE_MARKET_SEARCH_TERM_RESPONSE = "marketsSearchTermResponse.json";
  private static final String FILE_MARKET_EPIC_RESPONSE = "marketsEpicResponse.json";

  @Test
  public void testCreateInstrumentInfoList() throws IOException {
    File fileMarketSearchTermResponse = new File(
        getClass().getClassLoader().getResource(FILE_MARKET_SEARCH_TERM_RESPONSE).getFile());
    List<InstrumentInfo> result = IgIndexResponseParser.createInstrumentInfoList(
        FileUtils.readFileToString(fileMarketSearchTermResponse));

    assertNotNull(result);
    assertTrue(result.size() == 50);
    List<String> instrumentIds = Lists.newArrayList();
    for (InstrumentInfo instrumentInfo : result) {
      assertInstrumentInfo(instrumentInfo);
      // Assert instrumentId uniqueness.
      assertTrue(!instrumentIds.contains(instrumentInfo.getInstrumentId()));
      instrumentIds.add(instrumentInfo.getInstrumentId());
    }
  }

  @Test
  public void testCreateInstrumentInfo() throws IOException {
    File fileMarketEpicResponse = new File(
        getClass().getClassLoader().getResource(FILE_MARKET_EPIC_RESPONSE).getFile());
    InstrumentInfo instrumentInfo = IgIndexResponseParser.createInstrumentInfo(
        FileUtils.readFileToString(fileMarketEpicResponse));
    assertInstrumentInfo(instrumentInfo);
  }

  private static void assertInstrumentInfo(final InstrumentInfo instrumentInfo) {
    assertNotNull(instrumentInfo.getInstrumentType());
    assertTrue(!Strings.isNullOrEmpty(instrumentInfo.getInstrumentName()));
    assertTrue(!Strings.isNullOrEmpty(instrumentInfo.getExpiry()));
    assertTrue(!Strings.isNullOrEmpty(instrumentInfo.getInstrumentId()));
    assertTrue(instrumentInfo.getPriceMinimalDecimalPosition() >= 0);
  }
}
