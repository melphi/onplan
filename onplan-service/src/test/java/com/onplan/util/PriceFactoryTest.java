package com.onplan.util;

import com.google.common.collect.Range;
import com.onplan.domain.persistent.PriceTick;
import org.junit.Test;

import java.util.List;

import static com.onplan.util.TestingConstants.DEFAULT_START_DATE;
import static com.onplan.util.TestingConstants.INSTRUMENT_EURUSD_ID;
import static com.onplan.util.TestingConstants.DEFAULT_SPREAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PriceFactoryTest {
  @Test
  public void testCreatePriceTicks(){
    Range<Long> dateRange = Range.closed(
        DEFAULT_START_DATE.getMillis(),
        TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis());
    Range<Double> priceRange = Range.closed(10.1, 20.2);
    List<PriceTick> priceTicks = PriceFactory.createPriceTicks(
        INSTRUMENT_EURUSD_ID, dateRange, priceRange, DEFAULT_SPREAD);

    int lastPriceTickIndex = priceTicks.size() - 1;
    assertEquals(dateRange.upperEndpoint() - dateRange.lowerEndpoint(), lastPriceTickIndex);
    assertEquals(dateRange.lowerEndpoint().longValue(), priceTicks.get(0).getTimestamp());
    assertEquals(
        dateRange.upperEndpoint().longValue(), priceTicks.get(lastPriceTickIndex).getTimestamp());
    assertEquals(priceRange.lowerEndpoint(), priceTicks.get(0).getClosePriceAsk(), 0);
    assertEquals(
        priceRange.upperEndpoint(), priceTicks.get(lastPriceTickIndex).getClosePriceAsk(), 0);
    assertEquals(
        priceRange.lowerEndpoint() + DEFAULT_SPREAD, priceTicks.get(0).getClosePriceBid(), 0);
    assertEquals(priceRange.upperEndpoint() + DEFAULT_SPREAD,
        priceTicks.get(lastPriceTickIndex).getClosePriceBid(), 0);
    for(PriceTick priceTick : priceTicks) {
      assertTrue(priceRange.contains(priceTick.getClosePriceAsk()));
      assertTrue(priceRange.contains(priceTick.getClosePriceBid() - DEFAULT_SPREAD));
    }
  }
}
