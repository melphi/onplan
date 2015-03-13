package com.onplan.adviser.strategy;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.onplan.adviser.predicate.scripting.JavaScripPredicate;
import com.onplan.adviser.strategy.scripting.JavaScriptStrategy;
import com.onplan.domain.TestingPriceFactory;
import com.onplan.domain.transitory.PriceTick;
import com.onplan.service.TestingHistoricalPriceService;
import com.onplan.service.TestingInstrumentService;
import com.onplan.util.TestingConstants;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;
import static com.onplan.util.TestingConstants.INSTRUMENT_EURUSD_ID;
import static org.mockito.Mockito.mock;

public class JavaScriptStrategyTest {
  private StrategyListener strategyListener = mock(StrategyListener.class);

  @Test(expected = Exception.class)
  public void testApplyScriptBadSyntax() throws Exception {
    String script = "this syntax is invalid!";
    Strategy strategy = createStrategy(script);
    strategy.init();
  }

  @Test
  public void testApplyScriptWithoutInit() throws Exception {
    String script = "apply = function(priceTick) {};";
    Strategy strategy = createStrategy(script);
    strategy.init();
  }

  @Test
  public void testApplyScriptWithMissingParameters() throws Exception {
    String script = "apply = function() {};";
    Strategy strategy = createStrategy(script);
    strategy.init();
    for (PriceTick priceTick : createPriceTicks()) {
      strategy.onPriceTick(priceTick);
    }
  }

  @Test
  public void testApplyScriptWithWrongParameters() throws Exception {
    String script = "apply = function(a, b) {};";
    Strategy strategy = createStrategy(script);
    strategy.init();
    for (PriceTick priceTick : createPriceTicks()) {
      strategy.onPriceTick(priceTick);
    }
  }

  private List<PriceTick> createPriceTicks() {
    return TestingPriceFactory.createPriceTicks(
        INSTRUMENT_EURUSD_ID,
        new long[]{1, 2, 3, 4},
        new double[]{0.00001, 0.00002, 0.00003, 0.00004},
        0.00001);
  }

  private Strategy createStrategy(String script) {
    return createStrategy(script, ImmutableMap.of());
  }

  private Strategy createStrategy(String script, Map<String, String> parameters) {
    checkNotNullOrEmpty(script);
    StrategyExecutionContext strategyExecutionContext = new StrategyExecutionContext(
        TestingConstants.DEFAULT_STRATEGY_ID,
        new TestingHistoricalPriceService(),
        new TestingInstrumentService(),
        strategyListener,
        ImmutableMap.<String, String>builder()
            .put(JavaScripPredicate.PARAMETER_JAVASCRIPT_EXPRESSION, script)
            .putAll(parameters)
            .build(),
        ImmutableSet.of(INSTRUMENT_EURUSD_ID));
    return new JavaScriptStrategy(strategyExecutionContext);
  }
}
