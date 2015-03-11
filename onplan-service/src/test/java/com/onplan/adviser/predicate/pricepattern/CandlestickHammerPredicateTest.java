package com.onplan.adviser.predicate.pricepattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.adviser.predicate.TestingPredicateExecutionContextFactory;
import com.onplan.domain.persistent.PriceTick;
import com.onplan.domain.TestingPriceFactory;
import com.onplan.util.TestingConstants;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

public class CandlestickHammerPredicateTest {
  private static final Map<String, String> PROPERTIES = ImmutableMap.of(
      CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_1",
      CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "10.0");
  private static final String INSTRUMENT = TestingConstants.INSTRUMENT_DAX_ID;
  private static final Range<Long> DATE_RANGE = Range.closed(
      TestingConstants.DEFAULT_START_DATE.getMillis(),
      TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis());

  @Test
  public void testValidHammerCandlestick() throws Exception {
    List<PriceTick> priceTicks = createPriceTicks(
        INSTRUMENT,
        DATE_RANGE,
        9905.0,
        9914.0,
        9985.5,
        9909.8);
    CandlestickHammerPredicate candlestickHammerPredicate = createCandlestickHammerPredicate();
    for (PriceTick priceTick : priceTicks) {
      if (candlestickHammerPredicate.apply(priceTick)) {
        return;
      }
    }
    fail("Predicate not triggered.");
  }

  @Test
  public void testTooSmallHammerCandlestick() throws Exception {
    List<PriceTick> priceTicks = createPriceTicks(
        INSTRUMENT,
        DATE_RANGE,
        9909.3,
        9910.3,
        9902.5,
        9908.0);
    CandlestickHammerPredicate candlestickHammerPredicate = createCandlestickHammerPredicate();
    for (PriceTick priceTick : priceTicks) {
      if (candlestickHammerPredicate.apply(priceTick)) {
        fail("Predicate triggered.");
      }
    }
  }

  @Test
  public void testValidInvertedHammerCandlestick() throws Exception {
    List<PriceTick> priceTicks = createPriceTicks(
        INSTRUMENT,
        DATE_RANGE,
        10022.5,
        10040.3,
        10016.3,
        10020.0);
    CandlestickHammerPredicate candlestickHammerPredicate = createCandlestickHammerPredicate();
    for (PriceTick priceTick : priceTicks) {
      if (candlestickHammerPredicate.apply(priceTick)) {
        return;
      }
    }
    fail("Predicate not triggered.");
  }

  @Test
  public void testTooSmallInvertedHammerCandlestick() throws Exception {
    List<PriceTick> priceTicks = createPriceTicks(
        INSTRUMENT,
        DATE_RANGE,
        9965.8,
        9969.0,
        9963.8,
        9965.0);
    CandlestickHammerPredicate candlestickHammerPredicate = createCandlestickHammerPredicate();
    for (PriceTick priceTick : priceTicks) {
      if (candlestickHammerPredicate.apply(priceTick)) {
        fail("Predicate triggered.");
      }
    }
  }

  @Test
  public void testInvalidShape() throws Exception {
    List<PriceTick> priceTicks = createPriceTicks(
        INSTRUMENT,
        DATE_RANGE,
        9928.3,
        9932.5,
        9920.0,
        9925.5);
    CandlestickHammerPredicate candlestickHammerPredicate = createCandlestickHammerPredicate();
    for (PriceTick priceTick : priceTicks) {
      if (candlestickHammerPredicate.apply(priceTick)) {
        fail("Predicate triggered.");
      }
    }
  }

  private List<PriceTick> createPriceTicks(String instrumentId, Range<Long> timestampRange,
      double openPrice, double highPrice, double lowPrice, double closePrice) {
    List<PriceTick> priceTicks = TestingPriceFactory.createCandlestick(
        instrumentId, timestampRange, openPrice, highPrice, lowPrice, closePrice, 1);
    return ImmutableList.<PriceTick>builder()
        .add(new PriceTick(
            TestingConstants.INSTRUMENT_DAX_ID,
            TestingConstants.DEFAULT_START_DATE.minusMinutes(1).minusSeconds(1).getMillis(),
            0,
            0))
        .addAll(priceTicks)
        .add(new PriceTick(
            TestingConstants.INSTRUMENT_DAX_ID,
            TestingConstants.DEFAULT_START_DATE.plusMinutes(1).plusSeconds(1).getMillis(),
            0,
            0))
        .build();
  }

  private CandlestickHammerPredicate createCandlestickHammerPredicate() throws Exception {
    PredicateExecutionContext predicateExecutionContext =
        TestingPredicateExecutionContextFactory.createPredicateExecutionContext(PROPERTIES, INSTRUMENT);
    CandlestickHammerPredicate candlestickHammerPredicate =
        new CandlestickHammerPredicate(predicateExecutionContext);
    candlestickHammerPredicate.init();
    return candlestickHammerPredicate;
  }
}
