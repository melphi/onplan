package com.onplan.adviser.strategy.scripting;

import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.strategy.AbstractStrategy;
import com.onplan.adviser.strategy.StrategyExecutionContext;
import com.onplan.domain.transitory.PriceTick;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.adviser.ScriptingEngineUtils.*;

@TemplateMetaData(
    displayName = "JavaScript strategy",
    availableParameters = {JavaScriptStrategy.PARAMETER_JAVASCRIPT_EXPRESSION})
public class JavaScriptStrategy extends AbstractStrategy {
  public static final String PARAMETER_JAVASCRIPT_EXPRESSION = "javascriptExpression";

  private Invocable scriptEngine;
  private String javaScripExpression;

  public JavaScriptStrategy(StrategyExecutionContext strategyExecutionContext) {
    super(strategyExecutionContext);
  }

  @Override
  public void init() throws Exception {
    javaScripExpression = checkNotNull(getParameterValue(PARAMETER_JAVASCRIPT_EXPRESSION));
    ScriptEngine engine = createJavaScriptEngine();
    /*
     * TODO(robertom): Wrap StrategyExecutionContext in a JavaScriptStrategyExecutionContext,
     * and reintroduce PredicateExecutionContext.newBuilder().
     */
    engine.getContext().getBindings(ScriptContext.ENGINE_SCOPE)
        .put(BINDING_NAME_CONTEXT, strategyExecutionContext);
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

  @Override
  public void onPriceTick(PriceTick priceTick) {
    try {
      scriptEngine.invokeFunction(FUNCTION_APPLY, priceTick);
    } catch (Exception e) {
      // TODO(robertom): Exceptions need to be managed.
      throw new IllegalArgumentException(e);
    }
    // TODO(robertom): Update statistics.
  }
}
