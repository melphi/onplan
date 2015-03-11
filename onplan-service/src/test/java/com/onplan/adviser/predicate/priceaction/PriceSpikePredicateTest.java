package com.onplan.adviser.predicate.priceaction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.adviser.predicate.TestingPredicateExecutionContextFactory;
import com.onplan.domain.persistent.PriceTick;
import com.onplan.domain.TestingPriceFactory;
import com.onplan.util.TestingConstants;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.fail;

public class PriceSpikePredicateTest {
  private static final Map<String, String> PROPERTIES =
      ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "10");

  @Test
  public void testPredicateTriggeredEURUSD() throws Exception {
    Iterable<PriceTick> priceTicks = TestingPriceFactory.createPriceTicks(
        TestingConstants.INSTRUMENT_EURUSD_ID,
        Range.closed(
            TestingConstants.DEFAULT_START_DATE.getMillis(),
            TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis()),
        Range.closed(1.23101, 1.23211),
        1);
    PriceSpikePredicate priceSpikePredicate =
        createPriceSpikePredicate(TestingConstants.INSTRUMENT_EURUSD_ID);
    for (PriceTick priceTick : priceTicks) {
      if (priceSpikePredicate.apply(priceTick)) {
        return;
      }
    }
    fail("Predicate not triggered.");
  }

  @Test
  public void testPredicateNotTriggeredEURUSD() throws Exception {
    Iterable<PriceTick> priceTicks = TestingPriceFactory.createPriceTicks(
        TestingConstants.INSTRUMENT_EURUSD_ID,
        Range.closed(
            TestingConstants.DEFAULT_START_DATE.getMillis(),
            TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis()),
        Range.closed(1.23101, 1.23172),
        1);
    PriceSpikePredicate priceSpikePredicate =
        createPriceSpikePredicate(TestingConstants.INSTRUMENT_EURUSD_ID);
    for (PriceTick priceTick : priceTicks) {
      if (priceSpikePredicate.apply(priceTick)) {
        fail("Predicate triggered.");
      }
    }
  }

  @Test
  public void testPredicateTriggeredDAX() throws Exception {
    Iterable<PriceTick> priceTicks = TestingPriceFactory.createPriceTicks(
        TestingConstants.INSTRUMENT_DAX_ID,
        Range.closed(
            TestingConstants.DEFAULT_START_DATE.getMillis(),
            TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis()),
        Range.closed(9983.16, 9993.17),
        1);
    PriceSpikePredicate priceSpikePredicate =
        createPriceSpikePredicate(TestingConstants.INSTRUMENT_DAX_ID);
    for (PriceTick priceTick : priceTicks) {
      if (priceSpikePredicate.apply(priceTick)) {
        return;
      }
    }
    fail("Predicate not triggered.");
  }

  @Test
  public void testEventNotTriggeredDAX() throws Exception {
    Iterable<PriceTick> priceTicks = TestingPriceFactory.createPriceTicks(
        TestingConstants.INSTRUMENT_DAX_ID,
        Range.closed(
            TestingConstants.DEFAULT_START_DATE.getMillis(),
            TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis()),
        Range.closed(9983.16, 9985.26),
        1);
    PriceSpikePredicate priceSpikePredicate =
        createPriceSpikePredicate(TestingConstants.INSTRUMENT_DAX_ID);
    for (PriceTick priceTick : priceTicks) {
      if (priceSpikePredicate.apply(priceTick)) {
        fail("Predicate triggered.");
      }
    }
  }

  private PriceSpikePredicate createPriceSpikePredicate(String instrumentId) throws Exception {
    PredicateExecutionContext predicateExecutionContext =
        TestingPredicateExecutionContextFactory.createPredicateExecutionContext(PROPERTIES, instrumentId);
    PriceSpikePredicate strongPriceVariationStrategy =
        new PriceSpikePredicate(predicateExecutionContext);
    strongPriceVariationStrategy.init();
    return strongPriceVariationStrategy;
  }
}
