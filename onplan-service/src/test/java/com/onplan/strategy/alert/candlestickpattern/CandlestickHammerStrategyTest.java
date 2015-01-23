package com.onplan.strategy.alert.candlestickpattern;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.onplan.domain.PriceTick;
import com.onplan.strategy.Strategy;
import com.onplan.strategy.StrategyEventType;
import com.onplan.strategy.StrategyListener;
import com.onplan.strategy.alert.AbstractStrategyTest;
import com.onplan.util.PriceFactory;
import com.onplan.util.TestingConstants;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

public class CandlestickHammerStrategyTest extends AbstractStrategyTest<CandlestickHammerStrategy> {
  private static final Map<String, String> PROPERTIES = ImmutableMap.of(
      CandlestickHammerStrategy.PROPERTY_TIME_FRAME, "MINUTES_1",
      CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE, "10.0");
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
    StrategyListener strategyListener = mock(StrategyListener.class);
    Strategy candlestickHammerStrategy = initStrategy(
        new CandlestickHammerStrategy(),
        strategyListener,
        PROPERTIES,
        ImmutableSet.of(INSTRUMENT));
    for (PriceTick priceTick : priceTicks) {
      candlestickHammerStrategy.processPriceTick(priceTick);
    }
   assertEventFired(strategyListener, 1, INSTRUMENT, StrategyEventType.ALERT);
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
    StrategyListener strategyListener = mock(StrategyListener.class);
    Strategy candlestickHammerStrategy = initStrategy(
        new CandlestickHammerStrategy(),
        strategyListener,
        PROPERTIES,
        ImmutableSet.of(INSTRUMENT));
    for (PriceTick priceTick : priceTicks) {
      candlestickHammerStrategy.processPriceTick(priceTick);
    }
    assertEventNotFired(strategyListener);
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
    StrategyListener strategyListener = mock(StrategyListener.class);
    Strategy candlestickHammerStrategy = initStrategy(
        new CandlestickHammerStrategy(),
        strategyListener,
        PROPERTIES,
        ImmutableSet.of(INSTRUMENT));
    for (PriceTick priceTick : priceTicks) {
      candlestickHammerStrategy.processPriceTick(priceTick);
    }
    assertEventFired(strategyListener, 1, INSTRUMENT, StrategyEventType.ALERT);
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
    StrategyListener strategyListener = mock(StrategyListener.class);
    Strategy candlestickHammerStrategy = initStrategy(
        new CandlestickHammerStrategy(),
        strategyListener,
        PROPERTIES,
        ImmutableSet.of(INSTRUMENT));
    for (PriceTick priceTick : priceTicks) {
      candlestickHammerStrategy.processPriceTick(priceTick);
    }
    assertEventNotFired(strategyListener);
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
    StrategyListener strategyListener = mock(StrategyListener.class);
    Strategy candlestickHammerStrategy = initStrategy(
        new CandlestickHammerStrategy(),
        strategyListener,
        PROPERTIES,
        ImmutableSet.of(INSTRUMENT));
    for (PriceTick priceTick : priceTicks) {
      candlestickHammerStrategy.processPriceTick(priceTick);
    }
    assertEventNotFired(strategyListener);
  }

  private List<PriceTick> createPriceTicks(String instrumentId, Range<Long> timestampRange,
      double openPrice, double highPrice, double lowPrice, double closePrice) {
    List<PriceTick> priceTicks = PriceFactory.createCandlestick(
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
}
