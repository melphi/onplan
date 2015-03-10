package com.onplan.factory;

import com.google.common.collect.ImmutableList;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.adviser.predicate.priceaction.PriceSpikePredicate;
import com.onplan.adviser.predicate.pricepattern.CandlestickHammerPredicate;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.TestingConstants.DEFAULT_ADVISER_PARAMETERS;

public class TestingAdviserPredicateConfigurationFactory {
  public static AdviserPredicateConfiguration createSampleAdviserPredicateConfiguration(
      Class<? extends AdviserPredicate> clazz) {
    checkNotNull(clazz);
    return new AdviserPredicateConfiguration(clazz.getName(), DEFAULT_ADVISER_PARAMETERS);
  }

  public static Collection<AdviserPredicateConfiguration>
      createSampleAdviserPredicateConfigurations() {
    return ImmutableList.of(
        createSampleAdviserPredicateConfiguration(PriceSpikePredicate.class),
        createSampleAdviserPredicateConfiguration(CandlestickHammerPredicate.class));
  }
}
