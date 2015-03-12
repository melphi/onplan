package com.onplan.adviser.predicate.scripting;

import com.google.common.collect.ImmutableMap;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.adviser.predicate.PredicateExecutionContext;
import com.onplan.domain.TestingPriceFactory;
import com.onplan.domain.transitory.PriceTick;
import com.onplan.service.TestingHistoricalPriceService;
import com.onplan.service.TestingInstrumentService;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;
import static com.onplan.util.TestingConstants.INSTRUMENT_EURUSD_ID;
import static org.junit.Assert.assertEquals;

public class JavaScripExpressionPredicateTest {
  @Test
  public void testApplyScriptReturningTrue() throws Exception {
    String script = "init = function() {};" +
        "apply = function(priceTick) { return priceTick.closePriceAsk == 0.00003; };";
    AdviserPredicate adviserPredicate = createAdviserPredicate(script);
    adviserPredicate.init();
    int trueCount = 0;
    for (PriceTick priceTick : createPriceTicks()) {
      if (adviserPredicate.apply(priceTick)) {
        trueCount++;
      }
    }
    assertEquals(1, trueCount);
  }

  @Test
  public void testApplyScriptReturningFalse() throws Exception {
    String script = "init = function() {};" +
        "apply = function(priceTick) { return priceTick.closePriceAsk == 0.123456; };";
    AdviserPredicate adviserPredicate = createAdviserPredicate(script);
    adviserPredicate.init();
    int trueCount = 0;
    for (PriceTick priceTick : createPriceTicks()) {
      if (adviserPredicate.apply(priceTick)) {
        trueCount++;
      }
    }
    assertEquals(0, trueCount);
  }

  @Test
  public void testApplyScriptReturningNull() throws Exception {
    String script = "init = function() {};" +
        "apply = function(priceTick) { return null; };";
    AdviserPredicate adviserPredicate = createAdviserPredicate(script);
    adviserPredicate.init();
    int trueCount = 0;
    for (PriceTick priceTick : createPriceTicks()) {
      if (adviserPredicate.apply(priceTick)) {
        trueCount++;
      }
    }
    assertEquals(0, trueCount);
  }

  @Test
  public void testApplyScriptNoReturnin() throws Exception {
    String script = "init = function() {};" +
        "apply = function(priceTick) {};";
    AdviserPredicate adviserPredicate = createAdviserPredicate(script);
    adviserPredicate.init();
    int trueCount = 0;
    for (PriceTick priceTick : createPriceTicks()) {
      if (adviserPredicate.apply(priceTick)) {
        trueCount++;
      }
    }
    assertEquals(0, trueCount);
  }

  @Test(expected = Exception.class)
  public void testApplyScriptBadSyntax() throws Exception {
    String script = "this syntax is invalid!";
    AdviserPredicate adviserPredicate = createAdviserPredicate(script);
    adviserPredicate.init();
  }

  @Test
  public void testApplyScriptWithoutInit() throws Exception {
    String script = "apply = function(priceTick) {};";
    AdviserPredicate adviserPredicate = createAdviserPredicate(script);
    adviserPredicate.init();
  }

  @Test
  public void testApplyScriptWithMissingParameters() throws Exception {
    String script = "apply = function() {};";
    AdviserPredicate adviserPredicate = createAdviserPredicate(script);
    adviserPredicate.init();
    for (PriceTick priceTick : createPriceTicks()) {
      adviserPredicate.apply(priceTick);
    }
  }

  @Test
  public void testApplyScriptWithWrongParameters() throws Exception {
    String script = "apply = function(a, b) {};";
    AdviserPredicate adviserPredicate = createAdviserPredicate(script);
    adviserPredicate.init();
    for (PriceTick priceTick : createPriceTicks()) {
      adviserPredicate.apply(priceTick);
    }
  }

  // TODO(roberotm): Complete and enable this test.
  @Ignore
  @Test
  public void testApplyScriptWithContextParameters() throws Exception {
    String script = "apply = function(priceTick) {" +
        "return (context.getInstrumentId() != null);"+
        "};";
    AdviserPredicate adviserPredicate =
        createAdviserPredicate(script, ImmutableMap.of("paramKey", "0.00002"));
    adviserPredicate.init();
    int trueCount = 0;
    for (PriceTick priceTick : createPriceTicks()) {
      if (adviserPredicate.apply(priceTick)) {
        trueCount++;
      }
    }
    assertEquals(3, trueCount);
  }

  private List<PriceTick> createPriceTicks() {
    return TestingPriceFactory.createPriceTicks(
        INSTRUMENT_EURUSD_ID,
        new long[] {1, 2, 3, 4},
        new double[] {0.00001, 0.00002, 0.00003, 0.00004},
        0.00001);
  }

  private AdviserPredicate createAdviserPredicate(String script) {
    return createAdviserPredicate(script, ImmutableMap.of());
  }

  private AdviserPredicate createAdviserPredicate(String script, Map<String, String> parameters) {
    checkNotNullOrEmpty(script);
    PredicateExecutionContext predicateExecutionContext = PredicateExecutionContext.newBuilder()
        .setExecutionParameters(
            ImmutableMap.<String, String>builder()
              .put(JavaScripExpressionPredicate.PARAMETER_JAVASCRIPT_EXPRESSION, script)
              .putAll(parameters)
              .build())
        .setInstrumentId(INSTRUMENT_EURUSD_ID)
        .setHistoricalPriceService(new TestingHistoricalPriceService())
        .setInstrumentService(new TestingInstrumentService())
        .build();
    return new JavaScripExpressionPredicate(predicateExecutionContext);
  }
}
