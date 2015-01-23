package com.onplan.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PriceUtilTest {
  @Test
  public void testGetPricePips() {
    assertEquals(12488.1, PriceBarUtil.getPricePips(1.24881, 4), 0);
    assertEquals(8520.9, PriceBarUtil.getPricePips(0.85209, 4), 0);
    assertEquals(99505.1, PriceBarUtil.getPricePips(9950.51, 1), 0);
    assertEquals(11830.1, PriceBarUtil.getPricePips(118.301, 2), 0);
    assertEquals(1187.4, PriceBarUtil.getPricePips(1187.4, 0), 0);
    assertEquals(10731, PriceBarUtil.getPricePips(10731, 0), 0);
  }
}
