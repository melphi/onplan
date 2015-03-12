 package com.onplan.service.impl;

import com.google.common.annotations.VisibleForTesting;
import com.onplan.adviser.strategy.Strategy;
import com.onplan.domain.transitory.PriceTick;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

 /**
 * Collects the active strategies and dispatches the price tick to the strategies which are
 * listening to the specific instrument. Thread safe and optimized for speed.
 */
 // TODO(robertom): This class needs to be tested extensively.
public final class StrategyPool {
  /** Contains the ordered list of instrument id. */
  private String[] poolKeys = {};

  /** Contains the strategies grouped by instrument id, matching the order of poolKeys. */
  private Strategy[][] poolStrategies = {};

  /**
  * Returns the number of registered strategies.
  */
  public synchronized long poolSize() {
    long counter = 0;
    for (int i = 0; i < poolStrategies.length; i++) {
      for (int j = 0; j <poolStrategies[i].length; j++) {
        counter++;
      }
    }
    return counter;
  }

  // TODO(robertom): Sort strategies by priority / average milliseconds.
  public synchronized void addStrategy(Strategy strategy) {
    checkNotNull(strategy);
    for (String instrumentId : strategy.getRegisteredInstruments()) {
      int index = Arrays.binarySearch(poolKeys, instrumentId);
      if (index >= 0) {
        poolStrategies[index] = ArrayUtils.add(poolStrategies[index], strategy);
      } else {
        poolStrategies = ArrayUtils.add(poolStrategies, new Strategy[]{strategy});
        poolKeys = ArrayUtils.add(poolKeys, instrumentId);
        sortArrays();
      }
    }
  }

  /**
   * Removes a strategy from the pool, returns true if the strategy was found, false otherwise.
   *
   * @param strategyId The strategy id.
   */
  public synchronized boolean removeStrategy(String strategyId) {
    checkNotNullOrEmpty(strategyId);
    for (int i = 0; i < poolStrategies.length; i++) {
      for (int j = 0; j < poolStrategies[i].length; j++) {
        if (strategyId.equals(poolStrategies[i][j].getId())) {
          if (poolStrategies[i].length == 1) {
            poolKeys = ArrayUtils.remove(poolKeys, i);
            poolStrategies = ArrayUtils.remove(poolStrategies, i);
          } else {
            poolStrategies[i] = ArrayUtils.remove(poolStrategies[i], j);
          }
          return true;
        }
      }
    }
    return false;
  }

  // TODO(robertom): Run high priority strategies in a separated thread.
  public synchronized void processPriceTick(final PriceTick priceTick) {
    int index = Arrays.binarySearch(poolKeys, priceTick.getInstrumentId());
    if (index >= 0) {
      // TODO(robertom): Some strategies can take long time, avoid to keep poolKeys locked.
      Strategy[] strategies = poolStrategies[index];
      for (int i = 0; i < strategies.length; i++) {
        strategies[i].onPriceTick(priceTick);
      }
    }
  }

  @VisibleForTesting
  private synchronized String[] getPoolKeys() {
    return poolKeys;
  }

  @VisibleForTesting
  private synchronized Strategy[][] getPoolStrategies() {
    return poolStrategies;
  }

  private void sortArrays() {
    String[] unsortedPoolKeys = Arrays.copyOf(poolKeys, poolKeys.length);
    Arrays.sort(poolKeys);
    for (int i = 0; i < poolKeys.length; i++) {
      int oldIndex = ArrayUtils.indexOf(unsortedPoolKeys, poolKeys[i]);
      if (oldIndex != i) {
        Strategy[] temp = poolStrategies[i];
        poolStrategies[i] = poolStrategies[oldIndex];
        poolStrategies[oldIndex] = temp;
        break;
      }
    }
  }
}
