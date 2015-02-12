package com.onplan.service;

import com.onplan.adviser.alert.AlertEvent;

public interface EventNotificationService {
  /**
   * Dispatches the message in a separated thread.
   * @param alertEvent The alert event.
   */
  public void notifyAlertAsync(AlertEvent alertEvent);
}
