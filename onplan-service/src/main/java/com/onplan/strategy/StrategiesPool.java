package com.onplan.strategy;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.onplan.domain.PriceTick;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class StrategiesPool {
  private static final Logger logger = Logger.getLogger(StrategiesPool.class);

  private Map<String, Iterable<Strategy>> strategies = ImmutableMap.of();

  public void setStrategies(Map<String, Iterable<Strategy>> strategies) {
    this.strategies = ImmutableMap.copyOf(checkNotNull(strategies));
  }

  public Iterable<String> getInstruments() {
    return strategies.keySet();
  }

  public List<Strategy> getStrategiesList() {
    ImmutableList.Builder result = ImmutableList.<Strategy>builder();
    for (Iterable<Strategy> entries : strategies.values()) {
      result.addAll(entries);
    }
    return result.build();
  }

  public void removeStrategy(String strategyId) {
    checkNotNullOrEmpty(strategyId);
    Predicate<Strategy> strategyHasNotId = createStrategyHasNotIdPredicate(strategyId);
    ImmutableMap.Builder<String, Iterable<Strategy>> result = ImmutableMap.builder();
    boolean elementRemoved = false;
    for (Map.Entry<String, Iterable<Strategy>> entry : strategies.entrySet()) {
      if (Iterables.all(entry.getValue(), strategyHasNotId)) {
        result.put(entry.getKey(), entry.getValue());
      } else {
        Iterable<Strategy> newList = Iterables.filter(entry.getValue(), strategyHasNotId);
        if (!Iterables.isEmpty(newList)) {
          result.put(entry.getKey(), newList);
        }
        elementRemoved = true;
      }
    }
    if (elementRemoved) {
      strategies = result.build();
    } else {
      logger.warn(String.format("Tried to remove strategy id [%s] but was not found.", strategyId));
    }
  }

  public void processPriceTick(PriceTick priceTick) {
    for (Strategy strategy : strategies.get(priceTick.getInstrumentId())) {
      strategy.processPriceTick(priceTick);
    }
  }

  private Predicate<Strategy> createStrategyHasNotIdPredicate(final String id) {
    return new Predicate<Strategy>() {
      @Override
      public boolean apply(Strategy strategy) {
        return !id.equals(strategy.getId());
      }
    };
  }
}
