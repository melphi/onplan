package com.onplan.adviser.strategy;

import com.onplan.adviser.alert.AlertEvent;
import com.onplan.adviser.automatedorder.AutomatedOrderEvent;

// TODO(robertom): Looks cool but is it fast? Profile please!
public interface StrategyListener {
  public void onNewOrder(final AutomatedOrderEvent automatedOrderEvent);
  public void onAlert(final AlertEvent alertEvent);
}
