package com.onplan.notification;

import com.onplan.adviser.alert.AlertEvent;

/**
 * Common interface for events notification media.
 */
public interface NotificationChannel {
  /**
   * Notifies a {@link com.onplan.notification.SystemEvent}.
   *
   * @param systemEvent The system event to be notified.
   * @throws Exception Error while notifying the event.
   */
  public void notifySystemEvent(final SystemEvent systemEvent) throws Exception;

  /**
   * Notifies a {@link com.onplan.adviser.alert.AlertEvent}.
   *
   * @param alertEvent The alert event to be notified.
   * @throws Exception Error while notifying the event.
   */
  public void notifyAlertEvent(final AlertEvent alertEvent) throws Exception;

  /**
   * Returns true when the connection is active.
   * This can be a resource expensive operation.
   */
  public boolean isActive();
}
