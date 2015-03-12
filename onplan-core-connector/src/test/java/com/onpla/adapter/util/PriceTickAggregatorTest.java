package com.onpla.adapter.util;

import com.onplan.connector.PriceListener;
import com.onplan.connector.util.PriceTickAggregator;
import com.onplan.domain.transitory.PriceBar;
import com.onplan.domain.PriceBarTimeFrame;
import com.onplan.domain.transitory.PriceTick;
import com.onplan.util.TestingConstants;
import org.junit.Test;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PriceTickAggregatorTest {
  @Test
  public void testAddPriceTick15Minutes() {
    testAddPriceTick(PriceBarTimeFrame.MINUTES_15);
  }

  @Test
  public void testAddPriceTick1Minutes() {
    testAddPriceTick(PriceBarTimeFrame.MINUTES_1);
  }

  protected void testAddPriceTick(PriceBarTimeFrame priceBarTimeFrame) {
    checkNotNull(priceBarTimeFrame);
    PriceTickAggregator priceTickAggregator = PriceTickAggregator.newBuilder()
        .setPriceBarTimeFrame(priceBarTimeFrame)
        .setPriceListener(new PriceAssertions(priceBarTimeFrame))
        .setInstrumentId(TestingConstants.INSTRUMENT_EURUSD_ID)
        .build();
    for(int i = 0; i < priceBarTimeFrame.getIntervalMilliseconds(); i++) {
      double closePriceAsk = i + 1;
      double closePriceBid = i;
      if (0 == i) {
        closePriceAsk += 2;
        closePriceBid += 2;
      } else if (priceBarTimeFrame.getIntervalMilliseconds() - 1 == i) {
        closePriceAsk -= 2;
        closePriceBid -= 2;
      }
      long timestamp = TestingConstants.DEFAULT_START_DATE.plusMillis(i).getMillis();
      PriceTick priceTick = new PriceTick(
          TestingConstants.INSTRUMENT_EURUSD_ID, timestamp, closePriceAsk, closePriceBid);
      priceTickAggregator.addPriceTick(priceTick);
    }
  }

  private class PriceAssertions implements PriceListener {
    private final PriceBarTimeFrame priceBarTimeFrame;

    public PriceAssertions(PriceBarTimeFrame priceBarTimeFrame) {
      this.priceBarTimeFrame = priceBarTimeFrame;
    }

    @Override
    public void onPriceTick(PriceTick priceTick) {
      // Intentionally empty.
    }

    @Override
    public void onPriceBar(PriceBar priceBar) {
      assertEquals(TestingConstants.INSTRUMENT_EURUSD_ID, priceBar.getInstrumentId());
      assertEquals(TestingConstants.DEFAULT_START_DATE.getMillis(), priceBar.getTimestamp());
      assertEquals(priceBarTimeFrame.getIntervalMilliseconds(), priceBar.getIntervalMilliseconds());

      assertTrue(priceBar.getOpenPriceAsk() > priceBar.getOpenPriceBid());
      assertTrue(priceBar.getClosePriceAsk() > priceBar.getClosePriceBid());
      assertTrue(priceBar.getHighPriceAsk() > priceBar.getHighPriceBid());
      assertTrue(priceBar.getLowPriceAsk() > priceBar.getLowPriceBid());

      assertTrue(priceBar.getOpenPriceAsk() < priceBar.getClosePriceAsk());
      assertTrue(priceBar.getLowPriceAsk() < priceBar.getHighPriceAsk());
      assertTrue(priceBar.getOpenPriceAsk() > priceBar.getLowPriceAsk());
      System.out.println(String.format(
          "VALUE_A: [%f] VALUE_B: [%f].",
          priceBar.getClosePriceAsk(),
          priceBar.getHighPriceAsk()));
      assertTrue(priceBar.getClosePriceAsk() < priceBar.getHighPriceAsk());

      assertTrue(priceBar.getOpenPriceBid() < priceBar.getClosePriceBid());
      assertTrue(priceBar.getLowPriceBid() < priceBar.getHighPriceBid());
      assertTrue(priceBar.getOpenPriceBid() > priceBar.getLowPriceBid());
      assertTrue(priceBar.getClosePriceBid() < priceBar.getHighPriceBid());
    }
  }
}
