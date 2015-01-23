package com.onplan.strategy.alert;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import com.onplan.domain.PriceTick;
import com.onplan.strategy.StrategyEvent;
import com.onplan.strategy.StrategyEventType;
import com.onplan.strategy.StrategyExecutionContext;
import com.onplan.strategy.StrategyListener;
import com.onplan.util.PriceFactory;
import com.onplan.util.TestingConstants;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PriceSpikeStrategyTest {
  private static final Map<String, String> PROPERTIES =
      ImmutableMap.of(PriceSpikeStrategy.PROPERTY_MINIMUM_PIPS, "10");

  private StrategyListener strategyListener = mock(StrategyListener.class);

  @Test
  public void testEventTriggeredEURUSD() throws Exception {
    Iterable<PriceTick> priceTicks = PriceFactory.createPriceTicks(
        TestingConstants.INSTRUMENT_EURUSD_ID,
        Range.closed(
            TestingConstants.DEFAULT_START_DATE.getMillis(),
            TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis()),
        Range.closed(1.23101, 1.23211),
        1);
    PriceSpikeStrategy priceSpikeStrategy =
        createPriceSpikeStrategy(strategyListener, TestingConstants.INSTRUMENT_EURUSD_ID);
    for (PriceTick priceTick : priceTicks) {
      priceSpikeStrategy.processPriceTick(priceTick);
    }
    assertEventFired(strategyListener, TestingConstants.INSTRUMENT_EURUSD_ID);
  }

  @Test
  public void testEventNotTriggeredEURUSD() throws Exception {
    Iterable<PriceTick> priceTicks = PriceFactory.createPriceTicks(
        TestingConstants.INSTRUMENT_EURUSD_ID,
        Range.closed(
            TestingConstants.DEFAULT_START_DATE.getMillis(),
            TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis()),
        Range.closed(1.23101, 1.23172),
        1);
    PriceSpikeStrategy priceSpikeStrategy =
        createPriceSpikeStrategy(strategyListener, TestingConstants.INSTRUMENT_EURUSD_ID);
    for (PriceTick priceTick : priceTicks) {
      priceSpikeStrategy.processPriceTick(priceTick);
    }
    assertEventNotFired(strategyListener);
  }

  @Test
  public void testEventTriggeredDAX() throws Exception {
    Iterable<PriceTick> priceTicks = PriceFactory.createPriceTicks(
        TestingConstants.INSTRUMENT_DAX_ID,
        Range.closed(
            TestingConstants.DEFAULT_START_DATE.getMillis(),
            TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis()),
        Range.closed(9983.16, 9993.17),
        1);
    PriceSpikeStrategy priceSpikeStrategy =
        createPriceSpikeStrategy(strategyListener, TestingConstants.INSTRUMENT_DAX_ID);
    for (PriceTick priceTick : priceTicks) {
      priceSpikeStrategy.processPriceTick(priceTick);
    }
    assertEventFired(strategyListener, TestingConstants.INSTRUMENT_DAX_ID);
  }

  @Test
  public void testEventNotTriggeredDAX() throws Exception {
    Iterable<PriceTick> priceTicks = PriceFactory.createPriceTicks(
        TestingConstants.INSTRUMENT_DAX_ID,
        Range.closed(
            TestingConstants.DEFAULT_START_DATE.getMillis(),
            TestingConstants.DEFAULT_START_DATE.plusMinutes(1).getMillis()),
        Range.closed(9983.16, 9985.26),
        1);
    PriceSpikeStrategy priceSpikeStrategy =
        createPriceSpikeStrategy(strategyListener, TestingConstants.INSTRUMENT_DAX_ID);
    for (PriceTick priceTick : priceTicks) {
      priceSpikeStrategy.processPriceTick(priceTick);
    }
    assertEventNotFired(strategyListener);
  }

  private void assertEventFired(StrategyListener strategyListener, String instrumentId) {
    ArgumentCaptor<StrategyEvent> strategyEvent = ArgumentCaptor.forClass(StrategyEvent.class);
    verify(strategyListener, times(1)).onEvent(strategyEvent.capture());
    assertEquals(strategyEvent.getValue().getEventType(), StrategyEventType.ALERT);
    assertEquals(strategyEvent.getValue().getInstrumentId(), instrumentId);
  }

  private void assertEventNotFired(StrategyListener strategyListener) {
    ArgumentCaptor<StrategyEvent> strategyEvent = ArgumentCaptor.forClass(StrategyEvent.class);
    verify(strategyListener, times(0)).onEvent(strategyEvent.capture());
  }

  private PriceSpikeStrategy createPriceSpikeStrategy(
      StrategyListener strategyListener, String instrumentId) throws Exception {
    StrategyExecutionContext strategyExecutionContext =
        StrategyExecutionContextFactory.createStrategyExecutionContext(
            strategyListener, PROPERTIES, ImmutableSet.of(instrumentId));
    PriceSpikeStrategy strongPriceVariationStrategy = new PriceSpikeStrategy();
    strongPriceVariationStrategy.setExecutionContext(strategyExecutionContext);
    strongPriceVariationStrategy.initStrategy();
    return strongPriceVariationStrategy;
  }
}
