package com.onplan.strategy.alert;

import com.google.common.collect.Iterables;
import com.onplan.domain.PriceBarTimeFrame;
import com.onplan.domain.PriceTick;
import com.onplan.strategy.AbstractStrategy;
import com.onplan.strategy.StrategyEvent;
import com.onplan.strategy.StrategyEventType;
import com.onplan.strategy.StrategyTemplate;
import com.onplan.util.PriceBarUtil;

import static com.onplan.util.PriceBarUtil.getCurrentBarCloseTimestamp;
import static com.onplan.util.PriceBarUtil.getCurrentBarOpenTimestamp;

@StrategyTemplate(
    name = "Price spike",
    availableParameters = {PriceSpikeStrategy.PROPERTY_MINIMUM_PIPS}
)
public class PriceSpikeStrategy extends AbstractStrategy {
  public static final String PROPERTY_MINIMUM_PIPS = "minimumPips";

  private int priceMinimalDecimalPosition;
  private double minimumPips;
  private long barOpenTimestamp = 0;
  private long barCloseTimestamp;
  private double openPriceAsk;
  private boolean eventDispatched = false;

  @Override
  public synchronized void processPriceTick(PriceTick priceTick) {
    if (barOpenTimestamp <= 0 || priceTick.getTimestamp() > barCloseTimestamp) {
      prepareNewBar(priceTick);
      return;
    }
    double pipsVariation = PriceBarUtil.getPricePips(
        priceTick.getClosePriceAsk() - openPriceAsk, priceMinimalDecimalPosition);
    if (pipsVariation >= minimumPips && ! eventDispatched) {
      dispatchEvent(createStrategyEvent(priceTick, pipsVariation));
      eventDispatched = true;
    }
  }

  @Override
  public void initStrategy() throws Exception {
    minimumPips = getRequiredDoubleProperty(PROPERTY_MINIMUM_PIPS);
    String instrumentId = Iterables.getOnlyElement(executionContext.getRegisteredInstruments());
    priceMinimalDecimalPosition = executionContext.getInstrumentService()
        .getInstrumentInfo(instrumentId).getPriceMinimalDecimalPosition();
  }

  private void prepareNewBar(PriceTick priceTick) {
    barOpenTimestamp =
        getCurrentBarOpenTimestamp(priceTick.getTimestamp(), PriceBarTimeFrame.MINUTES_1);
    barCloseTimestamp =
        getCurrentBarCloseTimestamp(priceTick.getTimestamp(), PriceBarTimeFrame.MINUTES_1);
    openPriceAsk = priceTick.getClosePriceAsk();
    eventDispatched = false;
  }

  private StrategyEvent createStrategyEvent(PriceTick priceTick, double variationPips) {
    StrategyEvent event = StrategyEvent.newBuilder()
        .setEventType(StrategyEventType.ALERT)
        .setInstrumentId(priceTick.getInstrumentId())
        .setTimestamp(priceTick.getTimestamp())
        .setPriceTick(priceTick)
        .setMessage(String.format(
            "[%s] [%.1f] pips spike.",
            priceTick.getInstrumentId(),
            variationPips))
        .setStrategyExecutionContext(executionContext)
        .build();
    return event;
  }
}
