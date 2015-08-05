package com.onplan.connector.igindex.client;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.onplan.domain.transitory.InstrumentInfo;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IgIndexResponseParserTest {
  private static final String FILE_MARKET_SEARCH_TERM_RESPONSE = "marketsSearchTermResponse.json";
  private static final String FILE_MARKET_EPIC_RESPONSE = "marketsEpicResponse.json";

  @Test
  public void testCreateInstrumentInfoList() throws IOException {
    InputStream fileMarketSearchTermResponse =
        ClassLoader.getSystemResourceAsStream(FILE_MARKET_SEARCH_TERM_RESPONSE);
    List<InstrumentInfo> result = IgIndexResponseParser.createInstrumentInfoList(
        IOUtils.toString(fileMarketSearchTermResponse));
    fileMarketSearchTermResponse.close();

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
    InputStream fileMarketEpicResponse =
        ClassLoader.getSystemResourceAsStream(FILE_MARKET_EPIC_RESPONSE);
    InstrumentInfo instrumentInfo =
        IgIndexResponseParser.createInstrumentInfo(IOUtils.toString(fileMarketEpicResponse));
    fileMarketEpicResponse.close();
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
