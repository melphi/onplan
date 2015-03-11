package com.onplan.adviser.predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.onplan.adviser.predicate.priceaction.PriceSpikePredicate;
import com.onplan.adviser.predicate.pricepattern.CandlestickHammerPredicate;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TestingAdviserPredicateConfigurationFactory {
  public static AdviserPredicateConfiguration createSampleAdviserPredicateConfiguration(
      Class<? extends AdviserPredicate> clazz, Map<String, String> parameters) {
    checkNotNull(clazz);
    return new AdviserPredicateConfiguration(clazz.getName(), ImmutableMap.copyOf(parameters));
  }

  public static Collection<AdviserPredicateConfiguration>
      createSampleAdviserPredicateConfigurations() {
    return ImmutableList.of(
        createSampleAdviserPredicateConfiguration(
            PriceSpikePredicate.class,
            ImmutableMap.of(PriceSpikePredicate.PROPERTY_MINIMUM_PIPS, "10")),
        createSampleAdviserPredicateConfiguration(
            CandlestickHammerPredicate.class,
            ImmutableMap.of(
                CandlestickHammerPredicate.PROPERTY_TIME_FRAME, "MINUTES_1",
                CandlestickHammerPredicate.PROPERTY_MINIMUM_CANDLE_SIZE, "10")));
  }
}
