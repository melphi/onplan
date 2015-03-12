package com.onplan.adviser.predicate.priceaction;

import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.predicate.AbstractAdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.domain.PriceBarTimeFrame;
import com.onplan.domain.transitory.PriceTick;

import static com.onplan.util.PropertiesUtils.getRequiredLongValue;
import static com.onplan.util.PriceBarUtil.*;

@TemplateMetaData(
    displayName = "Price spike",
    availableParameters = {PriceSpikePredicate.PROPERTY_MINIMUM_PIPS}
)
public final class PriceSpikePredicate extends AbstractAdviserPredicate {
  public static final String PROPERTY_MINIMUM_PIPS = "minimumPips";

  private int priceMinimalDecimalPosition;
  private double minimumPips;
  private long barOpenTimestamp = 0;
  private long barCloseTimestamp;
  private double openPriceAsk;
  private boolean eventDispatched = false;

  public PriceSpikePredicate(PredicateExecutionContext predicateExecutionContext) {
    super(predicateExecutionContext);
  }

  @Override
  public boolean apply(PriceTick priceTick) {
    if (barOpenTimestamp <= 0 || priceTick.getTimestamp() > barCloseTimestamp) {
      prepareNewBar(priceTick);
      return false;
    }
    final double pipsVariation = getPricePips(
        priceTick.getClosePriceAsk() - openPriceAsk, priceMinimalDecimalPosition);
    if (pipsVariation >= minimumPips && ! eventDispatched) {
      eventDispatched = true;
      return true;
    }
    return false;
  }

  @Override
  public void init() throws Exception {
    minimumPips = getRequiredLongValue(
        predicateExecutionContext.getExecutionParameters(), PROPERTY_MINIMUM_PIPS);
    String instrumentId = predicateExecutionContext.getInstrumentId();
    priceMinimalDecimalPosition = predicateExecutionContext.getInstrumentService()
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
}
