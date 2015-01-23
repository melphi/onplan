package com.onplan.processor;

import com.onplan.notification.TwitterNotificationChannel;
import com.onplan.strategy.StrategyEvent;
import com.onplan.strategy.StrategyEventType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Component
public class StrategyEventProcessor {
  private static Logger logger = Logger.getLogger(StrategyEventProcessor.class);

  private TwitterNotificationChannel twitterNotificationChannel;

  @Autowired
  public void setTwitterNotificationChannel(TwitterNotificationChannel twitterNotificationChannel) {
    this.twitterNotificationChannel = checkNotNull(twitterNotificationChannel);
    if(twitterNotificationChannel.isActive()) {
      logger.info("TwitterNotificationChannel loaded and active.");
    } else {
      logger.warn("TwitterNotificationChannel is not active.");
    }
  }

  public void processStrategyEvent(StrategyEvent strategyEvent) throws Exception {
    checkNotNull(strategyEvent);
    checkArgument(StrategyEventType.ALERT.equals(strategyEvent.getEventType()),
        String.format("Unsupported strategy event type [%s].", strategyEvent.getEventType()));
    // TODO(robertom): Implement event handlers.
    twitterNotificationChannel.notifyStrategyEvent(strategyEvent);
    /*
    Iterable<StrategyEventHandler> eventHandlers = ImmutableList.of()
        strategyEvent.getStrategyExecutionContext().getStrategyEventHandlers();
    checkArgument(!Iterables.isEmpty(eventHandlers), "No event handlers assigned.");
    for (StrategyEventHandler eventHandler : eventHandlers) {
      checkArgument(StrategyEventType.ALERT.equals(eventHandler.getEventType()),
          String.format("Unsupported event handler type [%s].", eventHandler.getEventType()));
      if (eventHandler.getEventScheduler().isTimeValid(DateTime.now())) {
        twitterNotificationChannel.notifyStrategyEvent(strategyEvent);
      } else {
        logger.info(String.format(
            "Event not processed because out of scheduled interval [%s].", strategyEvent));
      }
    }
    */
  }
}
