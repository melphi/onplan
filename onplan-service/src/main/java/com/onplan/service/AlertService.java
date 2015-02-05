package com.onplan.service;

import com.onplan.adviser.alert.Alert;
import com.onplan.domain.PriceTick;

import java.util.List;

public interface AlertService extends AlertServiceRemote {
  public void onPriceTick(final PriceTick priceTick);
  public List<Alert> getAlerts();
  public boolean hasAlerts();
}
