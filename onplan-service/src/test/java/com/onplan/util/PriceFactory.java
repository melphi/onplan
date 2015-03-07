package com.onplan.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.onplan.domain.persistent.PriceTick;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class PriceFactory {
  /**
   * Generates a collection of {@value com.onplan.domain.persistent.PriceTick}.
   *
   * The first price ask value is the lower endpoint, the last price ask value is the
   * higher endpoint, all the remaining prices are randomly generated.
   */
  public static List<PriceTick> createPriceTicks(
      String instrumentId, Range<Long> timestampRange, Range<Double> priceRange, double spread) {
    checkArgument(priceRange.hasLowerBound(), "Feature not implemented.");
    checkArgument(priceRange.hasUpperBound(), "Feature not implemented.");

    List<PriceTick> priceTicks = Lists.newArrayList();
    double priceDifference = priceRange.upperEndpoint() - priceRange.lowerEndpoint();
    double priceAsk;

    for(long i = timestampRange.lowerEndpoint(); i <= timestampRange.upperEndpoint(); i++) {
      if(timestampRange.lowerEndpoint() == i) {
        priceAsk = priceRange.lowerEndpoint();
      } else if(timestampRange.upperEndpoint() == i) {
        priceAsk = priceRange.upperEndpoint();
      } else {
        priceAsk = priceRange.lowerEndpoint() + (priceDifference * Math.random());
      }
      priceTicks.add(new PriceTick(instrumentId, i, priceAsk, priceAsk + spread));
    }
    return ImmutableList.copyOf(priceTicks);
  }

  public static List<PriceTick> createCandlestick(String instrumentId, Range<Long> timestampRange,
      double openPrice, double highPrice, double lowPrice, double closePrice, double spread) {
    checkNotNullOrEmpty(instrumentId);
    checkArgument(timestampRange.upperEndpoint() - timestampRange.lowerEndpoint() > 3,
        "timestamp range must be have at least 3 milliseconds span.");
    return ImmutableList.of(
        new PriceTick(instrumentId, timestampRange.lowerEndpoint(), openPrice, openPrice + spread),
        new PriceTick(
            instrumentId, timestampRange.lowerEndpoint() + 1, lowPrice, openPrice + spread),
        new PriceTick(
            instrumentId, timestampRange.upperEndpoint() - 1, highPrice, openPrice + spread),
        new PriceTick(
            instrumentId, timestampRange.upperEndpoint(), closePrice, closePrice + spread));
  }
}
