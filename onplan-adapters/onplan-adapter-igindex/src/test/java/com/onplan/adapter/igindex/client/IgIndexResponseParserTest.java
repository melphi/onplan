package com.onplan.adapter.igindex.client;

import com.google.common.collect.Lists;
import com.onplan.domain.InstrumentInfo;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IgIndexResponseParserTest {
  @Test
  public void testCreateInstrumentInfoList() throws IOException {
    File fileMarketSearchTermResponse = new File(
        getClass().getClassLoader().getResource("marketsSearchTermResponse.json").getFile());
    List<InstrumentInfo> result = IgIndexResponseParser.createInstrumentInfoList(
        FileUtils.readFileToString(fileMarketSearchTermResponse));

    assertNotNull(result);
    assertTrue(result.size() == 50);
    List<String> instrumentIds = Lists.newArrayList();
    for (InstrumentInfo instrumentInfo : result) {
      assertNotNull(instrumentInfo.getInstrumentId());
      assertNotNull(instrumentInfo.getExpiry());
      assertNotNull(instrumentInfo.getInstrumentName());
      assertNotNull(instrumentInfo.getInstrumentType());
      // Assert instrumentId uniqueness.
      assertTrue(!instrumentIds.contains(instrumentInfo.getInstrumentId()));
      instrumentIds.add(instrumentInfo.getInstrumentId());
    }
  }
}
