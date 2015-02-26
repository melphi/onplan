package com.onplan.notification;

import com.google.common.base.Strings;
import com.onplan.util.StringUtil;
import org.apache.log4j.Logger;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.inject.Named;
import javax.inject.Singleton;

import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Singleton
public class TwitterNotificationChannel implements NotificationChannel {
  private static final Logger LOGGER = Logger.getLogger(TwitterNotificationChannel.class);
  private static final int MESSAGE_MAX_LENGTH = 140;
  private static final String MESSAGE_TRIMMED_SUFFIX = "..";

  private final Twitter twitterClient = TwitterFactory.getSingleton();

  @Named("twitter.destination.recipientScreenName")
  private String destinationRecipentScreenName;

  @Override
  public void notifyMessage(String title, String body) throws Exception {
    checkNotNullOrEmpty(destinationRecipentScreenName);
    String message = String.format("[%s] %s", title, body);
    LOGGER.info(String.format(
        "Sending Twitter notification to recipient [%s]: [%s]",
        destinationRecipentScreenName,
        message));
    sendDirectMessage(destinationRecipentScreenName, message);
  }

  private void sendDirectMessage(String destinationRecipentScreenName, String message)
      throws Exception {
    try {
      twitterClient.sendDirectMessage(
          destinationRecipentScreenName,
          StringUtil.trimText(message, MESSAGE_MAX_LENGTH, MESSAGE_TRIMMED_SUFFIX));
    } catch (TwitterException e) {
      LOGGER.error(String.format(
          "Error while sending Twitter notification to recipient [%s]: [%s].",
          destinationRecipentScreenName,
          e.getMessage()));
      throw new Exception(e);
    }
  }

  @Override
  public boolean isActive() {
    try {
      return ! Strings.isNullOrEmpty(twitterClient.getAccountSettings().getScreenName());
    } catch (TwitterException e) {
      return false;
    }
  }
}
