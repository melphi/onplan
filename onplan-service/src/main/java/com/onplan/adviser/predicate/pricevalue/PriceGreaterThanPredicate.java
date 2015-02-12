package com.onplan.adviser.predicate.pricevalue;

import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.predicate.AbstractAdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.domain.PriceTick;

import static com.onplan.util.PropertiesUtil.getRequiredDoubleValue;

@TemplateMetaData(
    displayName = "Price greater than",
    availableParameters = {
        PriceGreaterThanPredicate.PARAMETER_PRICE_VALUE}
)
// TODO(robertom): Implement the ask/bid choice.
public class PriceGreaterThanPredicate extends AbstractAdviserPredicate {
  public static final String PARAMETER_PRICE_VALUE = "priceValue";

  private double priceValue;

  public PriceGreaterThanPredicate(PredicateExecutionContext predicateExecutionContext) {
    super(predicateExecutionContext);
  }

  @Override
  public boolean processPriceTick(PriceTick priceTick) {
    return priceTick.getClosePriceAsk() > priceValue;
  }

  @Override
  public void init() throws Exception {
    priceValue = getRequiredDoubleValue(
        predicateExecutionContext.getExecutionParameters(), PARAMETER_PRICE_VALUE);
  }
}
