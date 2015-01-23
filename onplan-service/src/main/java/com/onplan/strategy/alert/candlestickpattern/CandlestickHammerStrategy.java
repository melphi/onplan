package com.onplan.strategy.alert.candlestickpattern;

import com.onplan.domain.PriceBarTimeFrame;
import com.onplan.domain.PriceTick;
import com.onplan.strategy.AbstractStrategy;
import com.onplan.strategy.StrategyEvent;
import com.onplan.strategy.StrategyEventType;
import com.onplan.strategy.StrategyTemplate;
import com.onplan.util.PriceBarUtil;
import org.springframework.context.annotation.Lazy;

@Lazy
@StrategyTemplate(
    name = "Candlestick hammer",
    availableParameters = {
        CandlestickHammerStrategy.PROPERTY_TIME_FRAME,
        CandlestickHammerStrategy.PROPERTY_MINIMUM_CANDLE_SIZE
    }
)
public class CandlestickHammerStrategy extends AbstractStrategy {
  public static final String PROPERTY_TIME_FRAME = "timeFrame";
  public static final String PROPERTY_MINIMUM_CANDLE_SIZE = "minimumCandleSize";
  private PriceBarTimeFrame priceBarTimeFrame;
  private long barEndTimestamp = -1;
  private double openPrice;
  private double closePrice;
  private double highPrice;
  private double lowPrice;
  private boolean isFirstBar = true;
  private double minimumCandleSize;

  @Override
  public void processPriceTick(PriceTick priceTick) {
    if (priceTick.getTimestamp() > barEndTimestamp) {
      if (isFirstBar) {
        if (barEndTimestamp > 0) {
          isFirstBar = false;
        }
      } else if(isHammerPattern(openPrice, closePrice, highPrice, lowPrice)) {
        dispatchEvent(createStrategyEvent(priceTick));
      }
      prepareNewBar(priceTick);
    }
    updateBarValues(priceTick);
  }

  private StrategyEvent createStrategyEvent(PriceTick priceTick) {
    StrategyEvent event = StrategyEvent.newBuilder()
        .setEventType(StrategyEventType.ALERT)
        .setInstrumentId(priceTick.getInstrumentId())
        .setTimestamp(priceTick.getTimestamp())
        .setPriceTick(priceTick)
        .setMessage(String.format(
            "[%s] [%s] Hammer pattern detected.",
            priceTick.getInstrumentId(),
            priceBarTimeFrame))
        .setStrategyExecutionContext(executionContext)
        .build();
    return event;
  }

  private boolean isHammerPattern(
      double openPrice, double closePrice, double highPrice, double lowPrice) {
    if (Math.abs(highPrice - lowPrice) < minimumCandleSize) {
      return false;
    }
    // Open and close must be in the upper or lower 2/5 of the bar.
    double offset = Math.abs(highPrice - lowPrice) * 0.4;
    double upperLimit = highPrice - offset;
    double lowerLimit = lowPrice + offset;
    return (openPrice > upperLimit && closePrice > upperLimit) ||
        (openPrice < lowerLimit && closePrice < lowerLimit);
  }

  private void updateBarValues(PriceTick priceTick) {
    closePrice = priceTick.getClosePriceAsk();
    if (priceTick.getClosePriceAsk() > highPrice) {
      highPrice = priceTick.getClosePriceAsk();
    } else if (priceTick.getClosePriceAsk() < lowPrice) {
      lowPrice = priceTick.getClosePriceAsk();
    }
  }

  private void prepareNewBar(PriceTick priceTick) {
    openPrice = priceTick.getClosePriceAsk();
    closePrice = priceTick.getClosePriceAsk();
    highPrice = priceTick.getClosePriceAsk();
    lowPrice = priceTick.getClosePriceAsk();
    barEndTimestamp =
        PriceBarUtil.getNextBarOpenTimestamp(priceTick.getTimestamp(), priceBarTimeFrame);
  }

  @Override
  public void initStrategy() {
    priceBarTimeFrame =
        PriceBarTimeFrame.parseString(getRequiredStringProperty(PROPERTY_TIME_FRAME));
    minimumCandleSize = getRequiredDoubleProperty(PROPERTY_MINIMUM_CANDLE_SIZE);
  }
}
