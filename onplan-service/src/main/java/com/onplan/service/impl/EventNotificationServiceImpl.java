package com.onplan.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;
import com.onplan.connector.ServiceConnection;
import com.onplan.connector.ServiceConnectionListener;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.notification.*;
import com.onplan.service.EventNotificationService;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkAndGetBoolean;
import static com.onplan.util.ThreadUtils.executeAsync;

@Singleton
public final class EventNotificationServiceImpl implements EventNotificationService {
  private static final Logger LOGGER = Logger.getLogger(EventNotificationServiceImpl.class);
  private static final String TEXT_SERVICE_CONNECTED = "Service connected $_$";
  private static final String TEXT_SERVICE_DISCONNECTED = "Service disconnected (-_-)zzZ";

  private final boolean notifyServiceConnection;
  private final boolean notifyServiceDisconnection;
  private final Iterable<NotificationChannel> notificationChannels;

  private ServiceConnection serviceConnection;
  private ServiceConnectionListener serviceConnectionListener = new ServiceConnectionListenerImpl();

  @Inject
  private Injector injector;

  @Inject
  public void setServiceConnection(ServiceConnection serviceConnection) {
    this.serviceConnection = checkNotNull(serviceConnection);
    serviceConnection.addServiceConnectionListener(serviceConnectionListener);
  }

  @Inject
  public EventNotificationServiceImpl(
      @Named("notification.notify.service.connection") String notifyServiceConnection,
      @Named("notification.notify.service.disconnection") String notifyServiceDisconnection,
      @Named("notification.twitter.enabled") String enableTwitterNotification,
      @Named("notification.smtp.enabled") String enableSmtpNotification,
      @Named("notification.amazonsqs.enabled") String enableAmazonSqsNotification) {
    this.notifyServiceConnection = checkAndGetBoolean(notifyServiceConnection);
    this.notifyServiceDisconnection = checkAndGetBoolean(notifyServiceDisconnection);
    ImmutableList.Builder<NotificationChannel> notificationChannels = ImmutableList.builder();
    if (checkAndGetBoolean(enableAmazonSqsNotification)) {
      notificationChannels.add(injector.getInstance(AmazonSQSNotificationChannel.class));
    }
    if (checkAndGetBoolean(enableTwitterNotification)) {
      notificationChannels.add(injector.getInstance(TwitterNotificationChannel.class));
    }
    if (checkAndGetBoolean(enableSmtpNotification)) {
      notificationChannels.add(injector.getInstance(SmtpNotificationChannel.class));
    }
    this.notificationChannels = notificationChannels.build();
  }

  @Override
  public void notifyAlertEventAsync(final AlertEvent alertEvent) {
    executeAsync(new AlertEventNotification(alertEvent));
  }

  @Override
  public void notifySystemEventAsync(final SystemEvent systemEvent) {
    executeAsync(new SystemEventNotification(systemEvent));
  }

  private class AlertEventNotification implements Runnable {
    private final AlertEvent alertEvent;

    public AlertEventNotification(AlertEvent alertEvent) {
      this.alertEvent = alertEvent;
    }

    @Override
    public void run() {
      LOGGER.info(String.format("Notifying alert event [%s].", alertEvent.getMessage()));
      for (NotificationChannel notificationChannel : notificationChannels) {
        try {
          notificationChannel.notifyAlertEvent(alertEvent);
        } catch (Exception e) {
          LOGGER.equals(String.format(
              "Error [%s] while sending alert notification [%s] to [%s].",
              e.getMessage(),
              alertEvent.getMessage(),
              notificationChannel.getClass().getName()));
        }
      }
    }
  }

  private class SystemEventNotification implements Runnable {
    private final SystemEvent systemEvent;

    private SystemEventNotification(SystemEvent systemEvent) {
      this.systemEvent = systemEvent;
    }

    @Override
    public void run() {
      LOGGER.info(String.format("Notifying system event [%s] [%s].",
          systemEvent.getClassName(), systemEvent.getMessage()));
      for (NotificationChannel notificationChannel : notificationChannels) {
        try {
          notificationChannel.notifySystemEvent(systemEvent);
        } catch (Exception e) {
          LOGGER.equals(String.format(
              "Error [%s] while sending system event notification [%s] [%s] to [%s].",
              e.getMessage(),
              systemEvent.getClassName(),
              systemEvent.getMessage(),
              notificationChannel.getClass().getName()));
        }
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
        SystemEvent systemEvent = new SystemEvent(
            EventNotificationService.class,
            TEXT_SERVICE_CONNECTED,
            DateTime.now().getMillis());
        notifySystemEventAsync(systemEvent);
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
        SystemEvent systemEvent = new SystemEvent(
            EventNotificationService.class,
            TEXT_SERVICE_DISCONNECTED,
            DateTime.now().getMillis());
        notifySystemEventAsync(systemEvent);
      }
    }
  }
}
