package com.onplan.adviser.automatedorder;

import com.onplan.adviser.AbstractChainedAdviser;
import com.onplan.adviser.AdviserListener;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.domain.PriceTick;

import java.util.Optional;

public final class AutomatedOrder extends AbstractChainedAdviser<AutomatedOrderEvent> {
  protected AutomatedOrder(String id, Iterable<AdviserPredicate> predicatesChain,
      AdviserListener<AutomatedOrderEvent> adviserListener, String instrumentId) {
    super(id, predicatesChain, adviserListener, instrumentId);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  protected Optional<AutomatedOrderEvent> prepareAdviserEvent(PriceTick priceTick) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  public static class Builder {
    private String id;
    private Iterable<AdviserPredicate> predicatesChain;
    private String instrumentId;
  }
}