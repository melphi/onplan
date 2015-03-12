package com.onplan.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onplan.adviser.AdviserListener;
import com.onplan.adviser.AlertInfo;
import com.onplan.adviser.alert.Alert;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.connector.HistoricalPriceService;
import com.onplan.connector.InstrumentService;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.domain.transitory.PriceTick;
import com.onplan.persistence.AlertConfigurationDao;
import com.onplan.service.AlertService;
import com.onplan.service.EventNotificationService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.onplan.adviser.predicate.AdviserPredicateUtil.createAdviserPredicateInfo;
import static com.onplan.service.impl.AdviserFactory.createAlert;
import static com.onplan.util.ExceptionUtils.stackTraceToString;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Singleton
public final class AlertServiceImpl extends AbstractAdviserService implements AlertService {
  private static final Logger LOGGER = Logger.getLogger(AlertServiceImpl.class);

  private final Map<String, Collection<Alert>> alertsMapping = Maps.newTreeMap();
  private final AdviserListener<AlertEvent> alertEventListener = new InternalAlertListener();

  private volatile boolean hasAlerts = false;

  private EventNotificationService eventNotificationService;
  private AlertConfigurationDao alertConfigurationDao;
  private InstrumentService instrumentService;
  private HistoricalPriceService historicalPriceService;

  @Inject
  public void setEventNotificationService(EventNotificationService eventNotificationService) {
    this.eventNotificationService = eventNotificationService;
  }

  @Inject
  public void setAlertConfigurationDao(AlertConfigurationDao alertConfigurationDao) {
    this.alertConfigurationDao = alertConfigurationDao;
  }

  @Inject
  public void setInstrumentService(InstrumentService instrumentService) {
    this.instrumentService = instrumentService;
  }

  @Inject
  public void setHistoricalPriceService(HistoricalPriceService historicalPriceService) {
    this.historicalPriceService = historicalPriceService;
  }

  @Override
  public void onPriceTick(final PriceTick priceTick) {
    checkNotNull(priceTick);
    synchronized (alertsMapping) {
      Iterable<Alert> alerts = alertsMapping.get(priceTick.getInstrumentId());
      for (Alert alert : alerts) {
        alert.onPriceTick(priceTick);
      }
    }
  }

  @Override
  public List<Alert> getAlerts() {
    ImmutableList.Builder result = ImmutableList.builder();
    synchronized (alertsMapping) {
      for (Collection<Alert> alerts : alertsMapping.values()) {
        result.addAll(alerts);
      }
    }
    return result.build();
  }

  @Override
  public boolean hasAlerts() {
    return hasAlerts;
  }

  @Override
  public boolean removeAlert(String alertId) throws Exception {
    checkNotNullOrEmpty(alertId);
    boolean result;
    try {
      alertConfigurationDao.removeById(alertId);
      result = unLoadAlert(alertId);
    } catch (Exception e) {
      LOGGER.error(String.format(
          "Error while un-loading alert [%s]: [%s] [%s].",
          alertId,
          e.getMessage(),
          stackTraceToString(e)));
      throw e;
    }
    synchronized (alertsMapping) {
      hasAlerts = !alertsMapping.isEmpty();
    }
    return result;
  }

  @Override
  public String addAlert(AlertConfiguration alertConfiguration) throws Exception {
    checkAlertConfiguration(alertConfiguration);
    String id = alertConfigurationDao.save(alertConfiguration);
    try {
      if (!isNullOrEmpty(alertConfiguration.getId())) {
        unLoadAlert(id);
      }
      alertConfiguration = alertConfigurationDao.findById(id);
      loadAlert(alertConfiguration);
    } catch (Exception e) {
      LOGGER.error(String.format(
          "Error while loading alert configuration [%s]: [%s].",
          alertConfiguration,
          e.getMessage()));
      alertConfigurationDao.removeById(id);
      throw e;
    }
    synchronized (alertsMapping) {
      hasAlerts = !alertsMapping.isEmpty();
    }
    LOGGER.info(String.format("Alert [%s] saved in database.", id));
    return id;
  }

  @Override
  public List<AlertInfo> getAlertsInfo() {
    ImmutableList.Builder result = ImmutableList.builder();
    synchronized (alertsMapping) {
      for (Collection<Alert> alerts : alertsMapping.values()) {
        for (Alert alert : alerts) {
          result.add(createAlertInfo(alert));
        }
      }
    }
    return result.build();
  }

  @Override
  public void loadSampleAlerts() throws Exception {
    List<AlertConfiguration> oldAlerts = alertConfigurationDao.findAll();
    List<AlertConfiguration> sampleAlerts = alertConfigurationDao.getSampleAlertsConfiguration();
    LOGGER.info(String.format(
        "Replacing [%d] old alerts with [%d] sample alerts.",
        oldAlerts.size(),
        sampleAlerts.size()));
    try {
      unLoadAllAlerts();
      alertConfigurationDao.removeAll();
      alertConfigurationDao.insertAll(sampleAlerts);
      loadAllAlerts();
    } catch (Exception e) {
      LOGGER.error(String.format("Error while loading alerts: [%s].", e.getMessage()));
      alertConfigurationDao.removeAll();
      alertConfigurationDao.insertAll(oldAlerts);
      throw e;
    }
  }

  @Override
  public void loadAllAlerts() throws Exception {
    checkArgument(alertsMapping.isEmpty(), "Remove all the previously registered alerts first.");
    LOGGER.info("Loading alerts configuration from database.");
    List<AlertConfiguration> alertConfigurations = alertConfigurationDao.findAll();
    LOGGER.info(String.format("[%s] alerts found in database.", alertConfigurations.size()));
    for (AlertConfiguration alertConfiguration : alertConfigurations) {
      try {
        loadAlert(alertConfiguration);
      } catch (Exception e) {
        LOGGER.error(String.format("Error while loading alert [%s]: [%s] [%s].",
            alertConfiguration.getId(), e.getMessage(), stackTraceToString(e)));
        throw e;
      }
    }
    LOGGER.info(String.format(
        "[%d] alerts loaded for instruments [%s].",
        alertConfigurations.size(),
        Joiner.on(", ").join(alertsMapping.keySet())));
  }

  @Override
  public void unLoadAllAlerts() {
    synchronized (alertsMapping) {
      if (!alertsMapping.isEmpty()) {
        for (String instrumentId : alertsMapping.keySet()) {
          dispatchInstrumentUnSubscriptionRequired(instrumentId);
        }
        alertsMapping.clear();
      }
      hasAlerts = !alertsMapping.isEmpty();
    }
  }

  private static void checkAlertConfiguration(final AlertConfiguration alertConfiguration) {
    checkNotNull(alertConfiguration);
    checkNotNullOrEmpty(alertConfiguration.getMessage());
    checkNotNull(alertConfiguration.getInstrumentId());
    checkNotNull(alertConfiguration.getPredicatesChain());
    checkNotNull(alertConfiguration.getSeverityLevel());
    checkArgument(!Iterables.isEmpty(alertConfiguration.getPredicatesChain()));
    for (AdviserPredicateConfiguration predicateConfiguration
        : alertConfiguration.getPredicatesChain()) {
      checkNotNullOrEmpty(predicateConfiguration.getClassName());
      checkNotNull(predicateConfiguration.getParameters());
    }
  }

  private static AlertInfo createAlertInfo(final Alert alert) {
    checkNotNull(alert);
    ImmutableList.Builder predicatesInfo = ImmutableList.builder();
    for (AdviserPredicate adviserPredicate : alert.getPredicatesChain()) {
      predicatesInfo.add(createAdviserPredicateInfo(adviserPredicate));
    }
    return new AlertInfo(
        alert.getId(),
        alert.getInstrumentId(),
        alert.getSeverityLevel(),
        predicatesInfo.build(),
        alert.getMessage(),
        alert.getCreatedOn(),
        alert.getLastFiredOn(),
        alert.getRepeat());
  }

  private void loadAlert(AlertConfiguration alertConfiguration) throws Exception {
    checkAlertConfiguration(alertConfiguration);
    Alert alert = createAlert(
        alertConfiguration, alertEventListener, instrumentService, historicalPriceService);
    alert.init();
    synchronized (alertsMapping) {
      Collection<Alert> alertsEntry = alertsMapping.get(alert.getInstrumentId());
      if (null == alertsEntry) {
        alertsEntry = Lists.newArrayList();
        alertsMapping.put(alert.getInstrumentId(), alertsEntry);
        dispatchInstrumentSubscriptionRequired(alert.getInstrumentId());
      }
      alertsEntry.add(alert);
      hasAlerts = !alertsMapping.isEmpty();
    }
    LOGGER.info(
        String.format("Alert [%s] for [%s] loaded.", alert.getId(), alert.getInstrumentId()));
  }

  private boolean unLoadAlert(String alertId) throws Exception {
    checkNotNullOrEmpty(alertId);
    for (Map.Entry<String, Collection<Alert>> entry : alertsMapping.entrySet()) {
      for (Alert alert : entry.getValue()) {
        if (alertId.equals(alert.getId())) {
          entry.getValue().remove(alert);
          if (entry.getValue().isEmpty()) {
            alertsMapping.remove(entry.getKey());
            dispatchInstrumentUnSubscriptionRequired(entry.getKey());
          }
          LOGGER.info(String.format("Alert [%s] unloaded.", alertId));
          if (hasAlerts && alertsMapping.isEmpty()) {
            hasAlerts = !alertsMapping.isEmpty();
          }
          return true;
        }
      }
    }
    return false;
  }

  private final class InternalAlertListener implements AdviserListener<AlertEvent> {
    @Override
    public void onEvent(AlertEvent event) {
      eventNotificationService.notifyAlertEventAsync(event);
      // TODO(robertom): Non-repeat event remains in memory, however they should be removed.
    }
  }
}
