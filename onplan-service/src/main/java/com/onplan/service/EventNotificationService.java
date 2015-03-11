package com.onplan.service;

import com.onplan.domain.persistent.AlertEvent;
import com.onplan.domain.persistent.SystemEvent;

/**
 * Notifies alerts and system events to the registered channels (eg. SMTP, JMS, Twitter, etc).
 */
public interface EventNotificationService {
  /**
   * Dispatches the alert event to via the active channels in a separated thread.
   *
   * @param alertEvent The alert event.
   */
  public void notifyAlertEventAsync(AlertEvent alertEvent);

  /**
   * Dispatches the system event to via the active channels in a separated thread.
   *
   * @param systemEvent The system event.
   */
  public void notifySystemEventAsync(final SystemEvent systemEvent);
}
