package com.onplan.adviser.predicate.scripting;

import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.predicate.AbstractAdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.domain.persistent.PriceTick;

import static com.onplan.util.PropertiesUtils.getRequiredStringValue;

// TODO(robertom): Implement JavaScripExpressionPredicate.
@TemplateMetaData(
    displayName = "JavaScript expression",
    availableParameters = {JavaScripExpressionPredicate.PARAMETER_JAVASCRIPT_EXPRESSION})
public class JavaScripExpressionPredicate extends AbstractAdviserPredicate {
  public static final String PARAMETER_JAVASCRIPT_EXPRESSION = "javascriptExpression";

  private String javaScripExpression;

  public JavaScripExpressionPredicate(PredicateExecutionContext predicateExecutionContext) {
    super(predicateExecutionContext);
  }

  @Override
  public boolean apply(PriceTick priceTick) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  @Override
  public void init() throws Exception {
    javaScripExpression = getRequiredStringValue(
        predicateExecutionContext.getExecutionParameters(), PARAMETER_JAVASCRIPT_EXPRESSION);
  }
}
