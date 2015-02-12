package com.onplan.adviser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.domain.PriceTick;
import org.joda.time.DateTime;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

/**
 * Base class for advisers which are triggered by a satisfied chained list of predicates.
 * @param <T> The adviser event type to be dispatched.
 */
public abstract class AbstractChainedAdviser<T extends AdviserEvent> implements Adviser {
  protected final String id;
  protected final String instrumentId;
  protected final Iterable<AdviserPredicate> predicatesChain;
  protected final AdviserListener<T> adviserListener;
  protected final long createdOn;

  protected AbstractChainedAdviser(String id, Iterable<AdviserPredicate> predicatesChain,
      AdviserListener<T> adviserListener, String instrumentId) {
    this.id = checkNotNullOrEmpty(id);
    this.instrumentId = checkNotNullOrEmpty(instrumentId);
    this.predicatesChain = ImmutableList.copyOf(checkNotNull(predicatesChain));
    this.adviserListener = checkNotNull(adviserListener);
    this.createdOn = DateTime.now().getMillis();
    checkArgument(Iterables.isEmpty(predicatesChain), "Expected a non empty predicates chain.");
  }

  @Override
  public void onPriceTick(final PriceTick priceTick) {
    checkNotNull(priceTick);
    checkArgument(instrumentId.equals(priceTick.getInstrumentId()), String.format(
        "Expected instrument id [%s] but price was for [%s]",
        instrumentId,
        priceTick.getInstrumentId()));
    for (AdviserPredicate adviserPredicate : predicatesChain) {
      if (!adviserPredicate.processPriceTick(priceTick)) {
        return;
      }
    }
    Optional<T> adviserEvent = prepareAdviserEvent(priceTick);
    if (adviserEvent.isPresent()) {
      adviserListener.onEvent(adviserEvent.get());
    }
  }

  public String getId() {
    return id;
  }

  public Iterable<AdviserPredicate> getPredicatesChain() {
    return predicatesChain;
  }

  public long getCreatedOn() {
    return createdOn;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  /**
   * Called once the predicates chain has been satisfied. Returns the event to be dispatched
   * or an empty optional if other conditions are not satisfied.
   * @param priceTick The price tick.
   */
  protected abstract Optional<T> prepareAdviserEvent(final PriceTick priceTick);
}
