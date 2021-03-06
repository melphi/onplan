package com.onplan.adviser.predicate.scripting;

import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.predicate.AbstractAdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.domain.transitory.PriceTick;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.adviser.ScriptingEngineUtils.*;

@TemplateMetaData(
    displayName = "JavaScript predicate",
    availableParameters = {JavaScripPredicate.PARAMETER_JAVASCRIPT_EXPRESSION})
public class JavaScripPredicate extends AbstractAdviserPredicate {
  public static final String PARAMETER_JAVASCRIPT_EXPRESSION = "javascriptExpression";

  private Invocable scriptEngine;
  private String javaScripExpression;

  public JavaScripPredicate(PredicateExecutionContext predicateExecutionContext) {
    super(predicateExecutionContext);
  }

  @Override
  public boolean apply(final PriceTick priceTick) {
    Object result = null;
    try {
      result = scriptEngine.invokeFunction(FUNCTION_APPLY, priceTick);
    } catch (Exception e) {
      // TODO(robertom): I think you can do better than this.
      throw new IllegalArgumentException(e);
    }
    return null != result && result.equals(true);
  }

  @Override
  public void init() throws Exception {
    javaScripExpression = checkNotNull(getParameterValue(PARAMETER_JAVASCRIPT_EXPRESSION));
    ScriptEngine engine = createJavaScriptEngine();
    /*
     * TODO(robertom): Wrap PredicateExecutionContext in a JavaScriptPredicateExecutionContext and
     * reintroduce PredicateExecutionContext.newBuilder().
     */
    engine.getContext().getBindings(ScriptContext.ENGINE_SCOPE)
        .put(BINDING_NAME_CONTEXT, predicateExecutionContext);
    try {
      engine.eval(javaScripExpression);
    } catch (Exception e) {
      throw new Exception(
          String.format("Error [%s] while parsing javascript expression [%s].",
              e.getMessage(),
              javaScripExpression),
          e);
    }
    this.scriptEngine = (Invocable) engine;
    try {
      scriptEngine.invokeFunction(FUNCTION_INIT);
    } catch (NoSuchMethodException e) {
      // Intentionally empty.
    } catch (ScriptException e) {
      throw new Exception(
          String.format("Error [%s] while calling initializing script with init().",
              e.getMessage()),
          e);
    }
  }
}
