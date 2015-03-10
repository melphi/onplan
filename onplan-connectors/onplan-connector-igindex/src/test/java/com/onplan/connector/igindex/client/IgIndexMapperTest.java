package com.onplan.connector.igindex.client;

import org.junit.Test;

import static com.onplan.connector.igindex.IgIndexMapper.getInstrumentIdByEpic;
import static org.junit.Assert.assertEquals;

public class IgIndexMapperTest {
  private static final String EPIC_EXAMPLE_1 = "CF.D.EURAUD.MAR.IP";
  private static final String EPIC_EXAMPLE_2 = "CHART:IX.D.DAX.DAILY.IP:TICK";
  private static final String INSTRUMENT_ID_EXAMPLE_1 = "CF.EURAUD.MAR";
  private static final String INSTRUMENT_ID_EXAMPLE_2 = "IX.DAX.DAILY";

  @Test
  public void testGetInstrumentIdByEpic() {
    assertEquals(INSTRUMENT_ID_EXAMPLE_1, getInstrumentIdByEpic(EPIC_EXAMPLE_1));
    assertEquals(INSTRUMENT_ID_EXAMPLE_2, getInstrumentIdByEpic(EPIC_EXAMPLE_2));
  }
}
