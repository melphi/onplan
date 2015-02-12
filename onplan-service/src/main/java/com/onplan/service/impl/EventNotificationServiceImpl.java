package com.onplan.service.impl;

import com.onplan.adapter.ServiceConnection;
import com.onplan.adapter.ServiceConnectionListener;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.notification.TwitterNotificationChannel;
import com.onplan.service.EventNotificationService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkBoolean;

@Singleton
public class EventNotificationServiceImpl implements EventNotificationService {
  private static final Logger LOGGER = Logger.getLogger(EventNotificationServiceImpl.class);

  private boolean notifyServiceConnection;

  private boolean notifyServiceDisconnection;

  @Inject
  private TwitterNotificationChannel twitterNotificationChannel;

  private ServiceConnection serviceConnection;
  private ServiceConnectionListener serviceConnectionListener = new ServiceConnectionListenerImpl();

  @Inject
  public void setNotifyServiceConnection(
      @Named("notification.notify.service.connection") String value) {
    checkBoolean(value);
    notifyServiceConnection = Boolean.parseBoolean(value);
  }

  @Inject
  public void setNotifyServiceDisconnection(
      @Named("notification.notify.service.disconnection") String value) {
    checkBoolean(value);
    notifyServiceDisconnection = Boolean.parseBoolean(value);
  }

  @Inject
  public void setServiceConnection(ServiceConnection serviceConnection) {
    this.serviceConnection = checkNotNull(serviceConnection);
    serviceConnection.addServiceConnectionListener(serviceConnectionListener);
  }

  // TODO(robertom): Just send the message to the JMS and that is it.
  @Override
  public void notifyAlertAsync(final AlertEvent alertEvent) {
    String title = String.format(
        "[%s] alert, severity: [%s]",
        alertEvent.getPriceTick().getInstrumentId(),
        alertEvent.getSeverityLevel());
    String message = alertEvent.getMessage();
    (new ChannelNotificationThread(title, message)).run();
  }

  private class ChannelNotificationThread extends Thread {
    private final String title;
    private final String message;

    private ChannelNotificationThread(String title, String message) {
      this.title = title;
      this.message = message;
    }

    @Override
    public void run() {
      try {
        LOGGER.info(String.format("Notifying message [%s] [%s].", title, message));
        twitterNotificationChannel.notifyMessage(title, message);
      } catch (Exception e) {
        LOGGER.equals(String.format(
            "Error [%s] while sending notification message [%s] [%s].",
            e.getMessage(),
            title,
            message));
      }
    }
  }

  private class ServiceConnectionListenerImpl implements ServiceConnectionListener {
    @Override
    public void onConnectionEstablished() {
      if (!serviceConnection.isConnected()) {
        LOGGER.error("onConnectionEstablished event dispatched but service is not connected.");
        return;
      }
      if(notifyServiceConnection) {
        LOGGER.info("Notifying ServiceConnection connection.");
        try {
          twitterNotificationChannel.notifyMessage(
              "ServiceConnection.", "Service connected $_$");
        } catch (Exception e) {
          LOGGER.error(e);
        }
      }
    }

    @Override
    public void onDisconnected() {
      if (!serviceConnection.isConnected()) {
        LOGGER.error("onDisconnected event dispatched but service is connected.");
        return;
      }
      if(notifyServiceDisconnection) {
        LOGGER.info("Notifying service disconnection.");
        try {
          twitterNotificationChannel.notifyMessage(
              "ServiceConnection.", "Service disconnected (-_-)zzZ");
        } catch (Exception e) {
          LOGGER.error(e);
        }
      }
    }
  }
}
