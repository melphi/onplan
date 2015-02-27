package com.onplan.service.impl;

import com.google.inject.Injector;
import com.onplan.adapter.ServiceConnection;
import com.onplan.adapter.ServiceConnectionListener;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.notification.SmtpNotificationChannel;
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

  private volatile boolean notifyServiceConnection;
  private volatile boolean notifyServiceDisconnection;
  private volatile boolean enableTwitterNotification;
  private volatile boolean enableSmtpNotification;

  private volatile TwitterNotificationChannel twitterNotificationChannel;
  private volatile SmtpNotificationChannel smtpNotificationChannel;

  private ServiceConnection serviceConnection;
  private ServiceConnectionListener serviceConnectionListener = new ServiceConnectionListenerImpl();

  @Inject
  private Injector injector;

  @Inject
  public void setNotifyServiceConnection(
      @Named("notification.notify.service.connection") String value) {
    notifyServiceConnection = checkBoolean(value);
  }

  @Inject
  public void setNotifyServiceDisconnection(
      @Named("notification.notify.service.disconnection") String value) {
    notifyServiceDisconnection = checkBoolean(value);
  }

  @Inject
  public void setEnableTwitterNotification(
      @Named("notification.enable.twitter") String value) {
    this.enableTwitterNotification = checkBoolean(value);
    if (this.enableTwitterNotification) {
      this.twitterNotificationChannel = injector.getInstance(TwitterNotificationChannel.class);
    } else {
      this.twitterNotificationChannel = null;
    }
  }

  @Inject
  public void setEnableSmtpNotification(
      @Named("notification.enable.smtp") String value) {
    this.enableSmtpNotification = checkBoolean(value);
    if (this.enableSmtpNotification) {
      this.smtpNotificationChannel = injector.getInstance(SmtpNotificationChannel.class);
    } else {
      this.smtpNotificationChannel = null;
    }
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
    (new ChannelsNotificationThread(title, message)).run();
  }

  private class ChannelsNotificationThread extends Thread {
    private final String title;
    private final String message;

    private ChannelsNotificationThread(String title, String message) {
      this.title = title;
      this.message = message;
    }

    @Override
    public void run() {
      LOGGER.info(String.format("Notifying message [%s] [%s].", title, message));
      if (enableTwitterNotification) {
        try {
          twitterNotificationChannel.notifyMessage(title, message);
        } catch (Exception e) {
          LOGGER.equals(String.format(
              "Error [%s] while sending Twitter notification message [%s] [%s].",
              e.getMessage(),
              title,
              message));
        }
      }
      if (enableSmtpNotification) {
        try {
          smtpNotificationChannel.notifyMessage(title, message);
        } catch (Exception e) {
          LOGGER.equals(String.format(
              "Error [%s] while sending SMTP notification message [%s] [%s].",
              e.getMessage(),
              title,
              message));
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
        try {
          new ChannelsNotificationThread("ServiceConnection.", "Service connected $_$").run();
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
          new ChannelsNotificationThread(
              "ServiceConnection.", "Service disconnected (-_-)zzZ").run();
        } catch (Exception e) {
          LOGGER.error(e);
        }
      }
    }
  }
}
