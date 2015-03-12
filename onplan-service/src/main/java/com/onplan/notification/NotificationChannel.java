package com.onplan.notification;

import com.onplan.adviser.alert.AlertEvent;
import com.onplan.service.SystemEvent;

/**
 * Common interface for events notification media.
 */
public interface NotificationChannel {
  /**
   * Notifies a {@link com.onplan.domain.persistent.SystemEventHistory}.
   *
   * @param systemEvent The system event to be notified.
   * @throws Exception Error while notifying the event.
   */
  public void notifySystemEvent(final SystemEvent systemEvent) throws Exception;

  /**
   * Notifies a {@link com.onplan.domain.persistent.AlertEventHistory}.
   *
   * @param alertEvent The alert event to be notified.
   * @throws Exception Error while notifying the event.
   */
  public void notifyAlertEvent(final AlertEvent alertEvent) throws Exception;
}
