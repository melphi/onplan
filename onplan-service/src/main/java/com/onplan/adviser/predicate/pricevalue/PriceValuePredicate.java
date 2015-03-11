package com.onplan.adviser.predicate.pricevalue;

import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.predicate.AbstractAdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.domain.persistent.PriceTick;

import java.util.function.Function;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;
import static com.onplan.util.PropertiesUtils.getRequiredDoubleValue;
import static com.onplan.util.PropertiesUtils.getRequiredStringValue;

// TODO(robertom): Parametrize evaluation on ask/bid values.
@TemplateMetaData(
    displayName = "Price value",
    availableParameters = {
        PriceValuePredicate.PARAMETER_PRICE_VALUE,
        PriceValuePredicate.PARAMETER_COMPARISON_OPERATOR})
public final class PriceValuePredicate extends AbstractAdviserPredicate {
  public static final String PARAMETER_PRICE_VALUE = "priceValue";
  public static final String PARAMETER_COMPARISON_OPERATOR = "comparisonOperator";
  // TODO(robertom): Move these constants in a dedicated class.
  public static final String OPERATOR_EQUALS = "=";
  public static final String OPERATOR_LESS_THAN = "<";
  public static final String OPERATOR_LESS_OR_EQUALS = "<=";
  public static final String OPERATOR_GREATER_THAN = ">";
  public static final String OPERATOR_GREATER_OR_EQUALS = ">=";

  private Function<PriceTick, Boolean> comparisonFunction;

  public PriceValuePredicate(PredicateExecutionContext predicateExecutionContext) {
    super(predicateExecutionContext);
  }

  @Override
  public boolean apply(PriceTick priceTick) {
    return comparisonFunction.apply(priceTick);
  }

  @Override
  public void init() throws Exception {
    final double priceValue = getRequiredDoubleValue(
        predicateExecutionContext.getExecutionParameters(), PARAMETER_PRICE_VALUE);
    String comparisonOperator = getRequiredStringValue(
        predicateExecutionContext.getExecutionParameters(), PARAMETER_COMPARISON_OPERATOR);
    checkNotNullOrEmpty(comparisonOperator);
    switch (comparisonOperator) {
      case OPERATOR_EQUALS:
        comparisonFunction = new PriceEqualsFunction(priceValue);
        break;

      case OPERATOR_LESS_THAN:
        comparisonFunction = new PriceLessThanFunction(priceValue);
        break;

      case OPERATOR_LESS_OR_EQUALS:
        comparisonFunction = new PriceLessOrEqualsFunction(priceValue);
        break;

      case OPERATOR_GREATER_THAN:
        comparisonFunction = new PriceGreaterThanFunction(priceValue);
        break;

      case OPERATOR_GREATER_OR_EQUALS:
        comparisonFunction = new PriceGreaterOrEqualsFunction(priceValue);
        break;

      default:
        throw new IllegalArgumentException(
            String.format("Unsupported comparison operator [%s].", comparisonOperator));
    }
  }

  private static abstract class AbstractComparisonFunction implements Function<PriceTick, Boolean> {
    protected final double priceValue;

    public AbstractComparisonFunction(final double priceValue) {
      this.priceValue = priceValue;
    }
  }

  private final static class PriceEqualsFunction extends AbstractComparisonFunction {
    public PriceEqualsFunction(double priceValue) {
      super(priceValue);
    }

    @Override
    public Boolean apply(PriceTick priceTick) {
      return priceTick.getClosePriceAsk() == priceValue;
    }
  }

  private final static class PriceLessThanFunction extends AbstractComparisonFunction {
    public PriceLessThanFunction(double priceValue) {
      super(priceValue);
    }

    @Override
    public Boolean apply(PriceTick priceTick) {
      return priceTick.getClosePriceAsk() < priceValue;
    }
  }

  private final static class PriceLessOrEqualsFunction extends AbstractComparisonFunction {
    public PriceLessOrEqualsFunction(double priceValue) {
      super(priceValue);
    }

    @Override
    public Boolean apply(PriceTick priceTick) {
      return priceTick.getClosePriceAsk() <= priceValue;
    }
  }

  private final static class PriceGreaterThanFunction extends AbstractComparisonFunction {
    public PriceGreaterThanFunction(double priceValue) {
      super(priceValue);
    }

    @Override
    public Boolean apply(PriceTick priceTick) {
      return priceTick.getClosePriceAsk() > priceValue;
    }
  }

  private final static class PriceGreaterOrEqualsFunction extends AbstractComparisonFunction {
    public PriceGreaterOrEqualsFunction(double priceValue) {
      super(priceValue);
    }

    @Override
    public Boolean apply(PriceTick priceTick) {
      return priceTick.getClosePriceAsk() >= priceValue;
    }
  }
}
