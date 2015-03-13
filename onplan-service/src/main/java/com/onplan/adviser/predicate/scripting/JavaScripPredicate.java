package com.onplan.adviser.predicate.scripting;

import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.predicate.AbstractAdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.domain.transitory.PriceTick;

import javax.script.*;

import static com.google.common.base.Preconditions.checkNotNull;

@TemplateMetaData(
    displayName = "JavaScript expression",
    availableParameters = {JavaScripPredicate.PARAMETER_JAVASCRIPT_EXPRESSION})
public class JavaScripPredicate extends AbstractAdviserPredicate {
  public static final String PARAMETER_JAVASCRIPT_EXPRESSION = "javascriptExpression";

  private static final String BINDING_NAME_CONTEXT = "executionContext";
  private static final String JAVASCRIPT_ENGINE_NAME = "nashorn";
  private static final String FUNCTION_INIT = "init";
  private static final String FUNCTION_APPLY = "apply";

  private static ScriptEngineManager scriptEngineManager;

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

  private ScriptEngine createJavaScriptEngine() {
    if (null == scriptEngineManager) {
      scriptEngineManager = new ScriptEngineManager();
    }
    return checkNotNull(
        scriptEngineManager.getEngineByName(JAVASCRIPT_ENGINE_NAME),
        String.format("Scripting engine [%s] not found.", JAVASCRIPT_ENGINE_NAME));
  }
}
