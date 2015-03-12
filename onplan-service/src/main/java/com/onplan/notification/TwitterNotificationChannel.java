package com.onplan.notification;

import com.onplan.adviser.alert.AlertEvent;
import com.onplan.service.SystemEvent;
import com.onplan.util.StringUtils;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Singleton
public final  class TwitterNotificationChannel implements NotificationChannel {
  private static final Logger LOGGER = Logger.getLogger(TwitterNotificationChannel.class);
  private static final int MESSAGE_MAX_LENGTH = 140;
  private static final String MESSAGE_TRIMMED_SUFFIX = "..";

  private final Twitter twitterClient = TwitterFactory.getSingleton();
  private final String recipientScreenName;

  @Inject
  public TwitterNotificationChannel(
      @Named("notification.twitter.recipientScreeName") String recipientScreenName) {
    this.recipientScreenName = checkNotNullOrEmpty(recipientScreenName);
  }

  private void sendDirectMessage(String destinationRecipientScreenName, String message)
      throws Exception {
    try {
      twitterClient.sendDirectMessage(
          destinationRecipientScreenName,
          StringUtils.trimText(message, MESSAGE_MAX_LENGTH, MESSAGE_TRIMMED_SUFFIX));
    } catch (TwitterException e) {
      LOGGER.error(String.format(
          "Error while sending Twitter notification to recipient [%s]: [%s].",
          destinationRecipientScreenName,
          e.getMessage()));
      throw new Exception(e);
    }
  }

  @Override
  public void notifySystemEvent(SystemEvent systemEvent) throws Exception {
    checkNotNull(systemEvent);
    String message = String.format(
        "[%s]: [%s] severity: [%s]",
        systemEvent.getClassName(),
        systemEvent.getMessage());
    LOGGER.info(String.format(
        "Sending system event Twitter notification to recipient [%s]: [%s]",
        recipientScreenName,
        message));
    sendDirectMessage(recipientScreenName, message);
  }

  @Override
  public void notifyAlertEvent(AlertEvent alertEvent) throws Exception {
    checkNotNull(alertEvent);
    String message = String.format(
        "[%s]: [%s] severity: [%s]",
        alertEvent.getPriceTick().getInstrumentId(),
        alertEvent.getMessage(),
        alertEvent.getSeverityLevel());
    LOGGER.info(String.format(
        "Sending alert event Twitter notification to recipient [%s]: [%s]",
        recipientScreenName,
        message));
    sendDirectMessage(recipientScreenName, message);
  }
}
