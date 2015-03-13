package com.onplan.adviser.predicate.pricepattern;

import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.predicate.AbstractAdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.domain.PriceBarTimeFrame;
import com.onplan.domain.transitory.PriceTick;
import com.onplan.util.PriceBarUtil;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@TemplateMetaData(
    displayName = "Candlestick hammer",
    availableParameters = {
        CandlestickHammerPredicate.PROPERTY_TIME_FRAME,
        CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE
    }
)
public final class CandlestickHammerPredicate extends AbstractAdviserPredicate {
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

  public CandlestickHammerPredicate(PredicateExecutionContext predicateExecutionContext) {
    super(predicateExecutionContext);
  }

  @Override
  public boolean apply(PriceTick priceTick) {
    if (priceTick.getTimestamp() > barEndTimestamp) {
      if (isFirstBar) {
        if (barEndTimestamp > 0) {
          isFirstBar = false;
        }
      } else if(isHammerPattern(openPrice, closePrice, highPrice, lowPrice)) {
        return true;
      }
      prepareNewBar(priceTick);
    }
    updateBarValues(priceTick);
    return false;
  }

  @Override
  public void init() throws Exception {
    String priceBarTimeFrameText = checkNotNullOrEmpty(getParameterValue(PROPERTY_TIME_FRAME));
    priceBarTimeFrame = PriceBarTimeFrame.parseString(priceBarTimeFrameText);
    minimumCandleSize = getDoubleValue(PROPERTY_MINIMUM_CANDLE_SIZE).get();
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
}
