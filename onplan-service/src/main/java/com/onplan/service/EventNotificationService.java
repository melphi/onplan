package com.onplan.service;

import com.onplan.adapter.ServiceConnection;
import com.onplan.adapter.ServiceConnectionListener;
import com.onplan.notification.TwitterNotificationChannel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class EventNotificationService {
  private static final Logger LOGGER = Logger.getLogger(EventNotificationService.class);

  @Value("${notification.notify.service.connection}")
  private boolean notifyServiceConnection;

  @Value("${notification.notify.service.disconnection}")
  private boolean notifyServiceDisconnection;

  @Autowired
  private TwitterNotificationChannel twitterNotificationChannel;

  private ServiceConnection serviceConnection;
  private ServiceConnectionListener serviceConnectionListener = new ServiceConnectionListenerImpl();

  @Autowired
  public void setServiceConnection(ServiceConnection serviceConnection) {
    this.serviceConnection = checkNotNull(serviceConnection);
    serviceConnection.addServiceConnectionListener(serviceConnectionListener);
  }

  private class ServiceConnectionListenerImpl implements ServiceConnectionListener {
    @Override
    public void onConnectionEstablished() {
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
