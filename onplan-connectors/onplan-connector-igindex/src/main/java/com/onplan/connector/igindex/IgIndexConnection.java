package com.onplan.connector.igindex;

import com.lightstreamer.ls_client.*;
import com.onplan.connector.AbstractServiceConnection;
import com.onplan.connector.igindex.client.IgIndexClient;
import com.onplan.connector.igindex.client.IgIndexConnectionCredentials;
import com.onplan.connector.igindex.client.IgIndexConstant;
import com.onplan.service.ServiceConnectionInfo;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

public final class IgIndexConnection extends AbstractServiceConnection {
  private static final Logger LOGGER = Logger.getLogger(IgIndexConnection.class);

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
    executeConnection();
  }

  @Override
  public void disconnect() {
    LOGGER.info("Closing IgIndex connection.");
    lightStreamerClient.closeConnection();
  }

  private void executeConnection() {
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
    } catch (Exception e) {
      LOGGER.error("Error while connecting to the service.", e);
    }
  }

  private void updateConnectionStatus(final boolean isConnected) {
    if (isConnected == this.isConnected) {
      LOGGER.warn(String.format("Attempt to update connected flag to [%s] but it was already [%s].",
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
    }

    @Override
    public void onSessionStarted(boolean isPolling) {
      LOGGER.info("Broker session started.");
      if (validateConnection()) {
        updateConnectionStatus(true);
      }
    }

    @Override
    public void onNewBytes(long bytes) {
      // Intentionally empty.
    }

    @Override
    public void onDataError(PushServerException e) {
      LOGGER.error("onDataError event.", e);
    }

    @Override
    public void onActivityWarning(boolean warningOn) {
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
}
