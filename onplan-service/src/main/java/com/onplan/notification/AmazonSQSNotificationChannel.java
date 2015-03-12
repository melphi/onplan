package com.onplan.notification;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.service.SystemEvent;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.JsonUtils.createJson;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Singleton
public final class AmazonSQSNotificationChannel implements NotificationChannel {
  private static final Logger LOGGER = Logger.getLogger(AmazonSQSNotificationChannel.class);

  private final AmazonSQS sqsClient;
  private final String alertEventQueueUrl;
  private final String systemEventQueueUrl;

  @Inject
  public AmazonSQSNotificationChannel(
      @Named("amazonaws.accesskeyid") String accessKeyId,
      @Named("amazonaws.secretaccesskey") String secretAccessKey,
      @Named("notification.amazonsqs.alertEventQueueUrl") String alertEventQueueUrl,
      @Named("notification.amazonsqs.systemEventQueueUrl") String systemEventQueueUrl) {
    checkNotNullOrEmpty(accessKeyId);
    checkNotNullOrEmpty(secretAccessKey);
    // TODO(robertom): Encrypt passwords with something like ProfileCredentialsProvider
    AWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
    this.sqsClient = new AmazonSQSClient(awsCredentials);
    this.alertEventQueueUrl = checkNotNullOrEmpty(alertEventQueueUrl);
    this.systemEventQueueUrl = checkNotNullOrEmpty(systemEventQueueUrl);
  }

  @Override
  public void notifySystemEvent(final SystemEvent systemEvent) throws Exception {
    checkNotNull(systemEvent);
    LOGGER.info(String.format("Sending system event to Amazon SQS [%s] [%s].",
        systemEvent.getClassName(), systemEvent.getMessage()));
    sqsClient.sendMessage(systemEventQueueUrl, createJson(systemEvent));
  }

  @Override
  public void notifyAlertEvent(final AlertEvent alertEvent) throws Exception {
    checkNotNull(alertEvent);
    LOGGER.info(String.format(
        "Sending alert event to Amazon SQS with message [%s].", alertEvent.getMessage()));
    sqsClient.sendMessage(alertEventQueueUrl, createJson(alertEvent));
  }
}
