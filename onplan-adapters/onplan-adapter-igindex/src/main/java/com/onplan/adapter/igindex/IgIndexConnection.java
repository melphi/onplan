package com.onplan.adapter.igindex;

import com.lightstreamer.ls_client.*;
import com.onplan.adapter.AbstractServiceConnection;
import com.onplan.adapter.igindex.client.IgIndexClient;
import com.onplan.adapter.igindex.client.IgIndexConnectionCredentials;
import com.onplan.adapter.igindex.client.IgIndexConstant;
import com.onplan.service.ServiceConnectionInfo;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public class IgIndexConnection extends AbstractServiceConnection {
  private static final Logger LOGGER = Logger.getLogger(IgIndexPriceService.class);

  private final String apiKey;
  private final String username;
  private final String password;
  private final IgIndexClient igIndexClient;
  private final ServiceConnectionListener serviceConnectionListener =
      new ServiceConnectionListener();
  private final LSClient lightStreamerClient = new LSClient();

  private volatile boolean isConnected = false;
  private volatile Optional<DateTime> connectionUpdateDate = Optional.empty();

  public IgIndexConnection(String apiKey, String username, String password, String severUrl) {
    this.apiKey = checkNotNullOrEmpty(apiKey);
    this.username = checkNotNullOrEmpty(username);
    this.password = checkNotNullOrEmpty(password);
    this.igIndexClient = new IgIndexClient(checkNotNullOrEmpty(severUrl));
  }

  @Override
  public boolean isConnected() {
    return isConnected;
  }

  @Override
  public ServiceConnectionInfo getConnectionInfo() {
    Long connectionUpdateTimestamp =
        connectionUpdateDate.isPresent() ? connectionUpdateDate.get().getMillis() : null;
    return new ServiceConnectionInfo(
        IgIndexConstant.PROVIDER_NAME, isConnected, connectionUpdateTimestamp);
  }

  public LSClient getLightStreamerClient() {
    return lightStreamerClient;
  }

  public IgIndexClient getIgIndexClient() {
    return igIndexClient;
  }

  @Override
  public void connect() {
    LOGGER.info("Opening IgIndex connection.");
    if (isConnected()) {
      LOGGER.warn("Service already connected, no need to reconnect.");
      return;
    }
    (new ConnectionThread()).start();
  }

  @Override
  public void disconnect() {
    LOGGER.info("Closing IgIndex connection.");
    lightStreamerClient.closeConnection();
  }

  private void updateConnectionStatus(final boolean isConnected) {
    if (isConnected == this.isConnected) {
      LOGGER.warn(String.format("Attempt to update connected flag to [%s] but it was already [%s]",
          isConnected, this.isConnected));
      return;
    }
    this.connectionUpdateDate = Optional.of(DateTime.now());
    if(this.isConnected && (!isConnected)) {
      this.isConnected = false;
      dispatchServiceDisconnectedEvent();
    } else if((!this.isConnected) && isConnected) {
      this.isConnected = true;
      dispatchServiceConnectionEstablishedEvent();
    }
  }

  private boolean validateConnection() {
    try {
      LOGGER.warn("Trying to send a ping to the service..");
      lightStreamerClient.sendMessage("Ping");
      // If it reaches this point it means that the connection is broken.
      LOGGER.error("..service ping failed");
      return false;
    } catch (PushServerException | PushUserException | PushConnException e1) {
      LOGGER.warn("..service ping replied.");
      return true;
    }
  }

  private class ServiceConnectionListener implements ConnectionListener {
    @Override
    public void onConnectionEstablished() {
      LOGGER.info("Broker connection established.");
      if (validateConnection()) {
        updateConnectionStatus(true);
      }
    }

    @Override
    public void onSessionStarted(boolean b) {
      LOGGER.info("Broker session started.");
    }

    @Override
    public void onNewBytes(long l) {
      // Intentionally empty.
    }

    @Override
    public void onDataError(PushServerException e) {
      LOGGER.error("onDataError event.", e);
    }

    @Override
    public void onActivityWarning(boolean b) {
      // Intentionally empty.
    }

    @Override
    public void onClose() {
      LOGGER.warn("Broker connection closed.");
      updateConnectionStatus(false);
    }

    @Override
    public void onEnd(int i) {
      // Intentionally empty.
    }

    @Override
    public void onFailure(PushServerException e) {
      LOGGER.error("onFailure event.", e);
      validateConnection();
    }

    @Override
    public void onFailure(PushConnException e) {
      LOGGER.error("onFailure.", e);
      validateConnection();
    }
  }

  private class ConnectionThread extends Thread {
    @Override
    public void run() {
      checkNotNull(lightStreamerClient);
      try {
        LOGGER.info("Retrieving IgIndex connection info.");
        IgIndexConnectionCredentials connectionCredentials =
            igIndexClient.login(apiKey, username, password);
        ConnectionInfo connectionInfo = new ConnectionInfo();
        connectionInfo.user = username;
        connectionInfo.password = String.format("CST-%s|XST-%s",
            connectionCredentials.getClientSessionToken(),
            connectionCredentials.getAccountSessionToken());
        connectionInfo.pushServerUrl = connectionCredentials.getLightStreamerEndpoint();
        LOGGER.info("Connecting to IgIndex LightStreamer.");
        lightStreamerClient.openConnection(connectionInfo, serviceConnectionListener);
      } catch (PushConnException | PushUserException| PushServerException | IOException e) {
        LOGGER.error("Error while connecting to the service.", e);
      }
    }
  }
}
