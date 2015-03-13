package com.onplan.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Range;
import com.onplan.adviser.AdviserPredicateInfo;
import com.onplan.adviser.AlertInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.adviser.alert.Alert;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.adviser.predicate.pricevalue.PriceValuePredicate;
import com.onplan.dao.TestingAlertConfigurationDao;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.domain.transitory.PriceTick;
import com.onplan.service.impl.AlertServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static com.google.common.collect.Iterables.size;
import static com.onplan.adviser.AdviserConfigurationFactory.createAlertConfiguration;
import static com.onplan.adviser.alert.TestingAlertConfigurationFactory.createSampleAlertConfigurationWithNullId;
import static com.onplan.adviser.predicate.AdviserPredicateUtil.createAdviserPredicateTemplateInfo;
import static com.onplan.adviser.predicate.TestingAdviserPredicateUtils.checkAdviserPredicate;
import static com.onplan.domain.TestingPriceFactory.createPriceTicks;
import static com.onplan.util.TestingConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AlertServiceImplTest {
  private AlertServiceImpl alertService;
  private InstrumentSubscriptionListener instrumentSubscriptionListener;
  private EventNotificationService eventNotificationService;
  private TestingAlertConfigurationDao alertConfigurationDao;

  @Before
  public void init() throws Exception {
    instrumentSubscriptionListener = mock(InstrumentSubscriptionListener.class);
    eventNotificationService = mock(EventNotificationService.class);
    alertConfigurationDao = new TestingAlertConfigurationDao();
    alertService = new AlertServiceImpl();
    alertService.setAlertConfigurationDao(alertConfigurationDao);
    alertService.setInstrumentSubscriptionListener(instrumentSubscriptionListener);
    alertService.setEventNotificationService(eventNotificationService);
    alertService.setHistoricalPriceService(new TestingHistoricalPriceService());
    alertService.setInstrumentService(new TestingInstrumentService());
  }

  @Test
  public void getAlerts() throws Exception {
    alertService.loadAllAlerts();
    List<Alert> alerts = alertService.getAlerts();
    assertTrue(!alerts.isEmpty());
    for (Alert alert : alerts) {
      assertValidAlert(alert);
    }
    assertEquals(alerts.size(), alertConfigurationDao.findAll().size());
  }

  @Test
  public void testHasAlertsBeforeInitialization() throws Exception {
    assertTrue(!alertService.hasAlerts());
    assertTrue(alertService.getAlerts().isEmpty());
  }

  @Test
  public void testHasAlertsAfterInitialization() throws Exception {
    alertService.loadAllAlerts();
    assertTrue(alertService.hasAlerts());
  }

  @Test
  public void testHasAlertsAfterRemoveAll() throws Exception {
    alertService.loadAllAlerts();
    for (Alert alert : alertService.getAlerts()) {
      alertService.removeAlert(alert.getId());
    }
    assertTrue(!alertService.hasAlerts());
    assertTrue(alertService.getAlerts().isEmpty());
  }

  @Test
  public void testHasAlertsAfterUnloadAll() throws Exception {
    alertService.loadAllAlerts();
    alertService.unLoadAllAlerts();
    assertTrue(!alertService.hasAlerts());
    assertTrue(alertService.getAlerts().isEmpty());
  }

  @Test
  public void testLoadAllAlerts() throws Exception {
    alertService.loadAllAlerts();
    assertTrue(alertService.hasAlerts());
    assertTrue(!alertService.getAlerts().isEmpty());
    for (String instrumentId : INSTRUMENT_IDS) {
      verify(instrumentSubscriptionListener, times(1))
          .onInstrumentSubscriptionRequest(instrumentId);
    }
    for (String instrumentId : INSTRUMENT_IDS) {
      verify(instrumentSubscriptionListener, never())
          .onInstrumentUnSubscriptionRequest(instrumentId);
    }
  }

  @Test
  public void testUnLoadAllAlerts() throws Exception {
    alertService.loadAllAlerts();
    reset(instrumentSubscriptionListener);
    alertService.unLoadAllAlerts();
    assertTrue(!alertService.hasAlerts());
    assertTrue(alertService.getAlerts().isEmpty());
    for (String instrumentId : INSTRUMENT_IDS) {
      verify(instrumentSubscriptionListener, never())
          .onInstrumentSubscriptionRequest(instrumentId);
    }
    for (String instrumentId : INSTRUMENT_IDS) {
      verify(instrumentSubscriptionListener, times(1))
          .onInstrumentUnSubscriptionRequest(instrumentId);
    }
  }

  @Test
  public void testRemoveAlertAfterInitialization() throws Exception {
    alertService.loadAllAlerts();
    Alert alertToRemove = alertService.getAlerts()
        .stream()
        .findFirst()
        .get();
    assertValidAlert(alertToRemove);
    assertTrue(alertService.removeAlert(alertToRemove.getId()));
    assertTrue(alertService.getAlerts()
        .stream()
        .noneMatch(alert -> alertToRemove.getId().equals(alert.getId())));
    assertTrue(alertService.hasAlerts());
  }

  @Test
  public void testRemoveAlertAfterUnloadAll() throws Exception {
    alertService.loadAllAlerts();
    alertService.unLoadAllAlerts();
    assertTrue(!alertService.removeAlert(DEFAULT_ALERT_ID));
    assertTrue(!alertService.hasAlerts());
    assertTrue(alertService.getAlerts().isEmpty());
  }

  @Test
  public void testRemoveAlertWhenEmpty() throws Exception {
    assertTrue(!alertService.removeAlert(DEFAULT_ALERT_ID));
    assertTrue(!alertService.hasAlerts());
    assertTrue(alertService.getAlerts().isEmpty());
  }

  @Test
  public void testRemoveSingleAlert() throws Exception {
    AlertConfiguration alertConfiguration = createSampleAlertConfigurationWithNullId();
    alertService.addAlert(alertConfiguration);
    Alert alert = alertService.getAlerts()
        .stream()
        .findFirst()
        .get();
    assertTrue(alertConfigurationDao.findAll().stream()
        .anyMatch(record -> alert.getId().equals(record.getId())));
    assertTrue(alertService.removeAlert(alert.getId()));
    assertTrue(!alertService.hasAlerts());
    assertTrue(alertService.getAlerts().isEmpty());
    assertTrue(alertConfigurationDao.findAll().stream()
        .noneMatch(record -> alert.getId().equals(record.getId())));
  }

  @Test
  public void testAddAlertAfterInitialization() throws Exception {
    alertService.loadAllAlerts();
    AlertConfiguration alertConfiguration = createSampleAlertConfigurationWithNullId();
    String id = alertService.addAlert(alertConfiguration);
    long counter = alertService.getAlerts()
        .stream()
        .filter(alert -> id.equals(alert.getId()))
        .count();
    assertEquals(1, counter);
    assertEquals(INITIAL_ALERTS_LIST_SIZE + 1, alertService.getAlerts().size());
    assertTrue(alertService.hasAlerts());
    Alert loadedAlert = alertService.getAlerts()
        .stream()
        .filter(alert -> id.equals(alert.getId()))
        .findFirst()
        .get();
    alertConfiguration.setId(id);
    assertMatch(alertConfiguration, loadedAlert);
  }

  @Test
  public void testAddAlertBeforeInitialization() throws Exception {
    AlertConfiguration alertConfiguration = createSampleAlertConfigurationWithNullId();
    String id = alertService.addAlert(alertConfiguration);
    assertTrue(alertService.hasAlerts());
    assertEquals(1, alertService.getAlerts().size());
    Alert loadedAlert = alertService.getAlerts()
        .stream()
        .findFirst()
        .get();
    alertConfiguration.setId(id);
    assertMatch(alertConfiguration, loadedAlert);
  }

  @Test
  public void testAddAlertReplacingExisting() throws Exception {
    alertService.loadAllAlerts();
    AlertConfiguration alertConfiguration = alertConfigurationDao.findAll()
        .stream()
        .findFirst()
        .get();
    alertConfiguration.setRepeat(!alertConfiguration.getRepeat());
    alertConfiguration.setInstrumentId("IX.DAX.DAILY");
    alertConfiguration.setMessage("NewMessage");
    alertService.addAlert(alertConfiguration);
    Alert alert = alertService.getAlerts()
        .stream()
        .filter(a -> alertConfiguration.getId().equals(a.getId()))
        .findFirst()
        .get();
    assertMatch(alertConfiguration, alert);
  }

  @Test
  public void testLoadSampleAlerts() throws Exception {
    alertService.loadSampleAlerts();
    List<Alert> sampleAlerts = alertService.getAlerts();
    assertTrue(alertService.hasAlerts());
    assertEquals(INITIAL_ALERTS_LIST_SIZE, sampleAlerts.size());
    alertService.unLoadAllAlerts();
    alertService.loadAllAlerts();
    List<Alert> savedAlerts = alertService.getAlerts();
    assertEquals(INITIAL_ALERTS_LIST_SIZE, savedAlerts.size());
    for (Alert sampleAlert : sampleAlerts) {
      assertTrue(savedAlerts.contains(sampleAlert));
    }
  }

  @Test
  public void testGetAlertsInfo() throws Exception {
    alertService.loadAllAlerts();
    List<Alert> alerts = alertService.getAlerts();
    List<AlertInfo> alertsInfo = alertService.getAlertsInfo();
    assertEquals(alerts.size(), INITIAL_ALERTS_LIST_SIZE);
    assertEquals(alerts.size(), alertsInfo.size());
    for (AlertInfo alertInfo : alertsInfo) {
      assertMatch(
          alertInfo,
          alerts.stream()
              .filter(record -> alertInfo.getId().equals(record.getId()))
              .findFirst()
              .get());
    }
  }

  @Test
  public void testOnPriceTickForRepeatTrue() throws Exception {
    AlertConfiguration alertConfiguration = createAlertConfiguration(
        PriceValuePredicate.class,
        INSTRUMENT_EURUSD_ID,
        ImmutableMap.of(
            PriceValuePredicate.PARAMETER_COMPARISON_OPERATOR, PriceValuePredicate.OPERATOR_EQUALS,
            PriceValuePredicate.PARAMETER_PRICE_VALUE, String.valueOf(PRICE_VALUE_FROM)),
        DEFAULT_ALERT_MESSAGE,
        true,
        DEFAULT_CREATION_DATE.getMillis());
    alertService.addAlert(alertConfiguration);
    List<PriceTick> priceTicks = createPriceTicks(
        INSTRUMENT_EURUSD_ID,
        Range.closed(DEFAULT_START_DATE.getMillis(), DEFAULT_START_DATE.getMillis() + 4),
        Range.closed(PRICE_VALUE_FROM, PRICE_VALUE_TO),
        0);
    for (PriceTick priceTick : priceTicks) {
      alertService.onPriceTick(priceTick);
    }
    assertAlertEventFired(eventNotificationService, alertConfiguration, 1);
    reset(eventNotificationService);
    for (PriceTick priceTick : priceTicks) {
      alertService.onPriceTick(priceTick);
    }
    assertAlertEventFired(eventNotificationService, alertConfiguration, 1);
  }

  @Test
  public void testOnPriceTickForRepeatFalse() throws Exception {
    AlertConfiguration alertConfiguration = createAlertConfiguration(
        PriceValuePredicate.class,
        INSTRUMENT_EURUSD_ID,
        ImmutableMap.of(
            PriceValuePredicate.PARAMETER_COMPARISON_OPERATOR, PriceValuePredicate.OPERATOR_EQUALS,
            PriceValuePredicate.PARAMETER_PRICE_VALUE, String.valueOf(PRICE_VALUE_FROM)),
        DEFAULT_ALERT_MESSAGE,
        false,
        DEFAULT_CREATION_DATE.getMillis());
    alertService.addAlert(alertConfiguration);
    List<PriceTick> priceTicks = createPriceTicks(
        INSTRUMENT_EURUSD_ID,
        Range.closed(DEFAULT_START_DATE.getMillis(), DEFAULT_END_DATE.getMillis()),
        Range.closed(PRICE_VALUE_FROM, PRICE_VALUE_TO),
        0);
    for (PriceTick priceTick : priceTicks) {
      alertService.onPriceTick(priceTick);
    }
    assertAlertEventFired(eventNotificationService, alertConfiguration, 1);
    reset(eventNotificationService);
    for (PriceTick priceTick : priceTicks) {
      alertService.onPriceTick(priceTick);
    }
    assertAlertEventFired(eventNotificationService, alertConfiguration, 0);
  }

  private static void assertAlertEventFired(EventNotificationService eventNotificationService,
      AlertConfiguration alertConfiguration, int times) {
    ArgumentCaptor<AlertEvent> alertEventCaptor = ArgumentCaptor.forClass(AlertEvent.class);
    if (times == 0) {
      verify(eventNotificationService, never())
          .notifyAlertEventAsync(alertEventCaptor.capture());
    } else {
      verify(eventNotificationService, times(times))
          .notifyAlertEventAsync(alertEventCaptor.capture());
      AlertEvent alertEvent = alertEventCaptor.getValue();
      assertEquals(alertEvent.getMessage(), alertConfiguration.getMessage());
      assertEquals(alertEvent.getSeverityLevel(), alertConfiguration.getSeverityLevel());
      assertEquals(alertEvent.getAdviserId(), alertConfiguration.getId());
    }
  }

  private static void assertValidAlert(Alert alert) {
    assertNotNull(alert);
    assertTrue(!Strings.isNullOrEmpty(alert.getId()));
    assertTrue(!Strings.isNullOrEmpty(alert.getInstrumentId()));
    assertTrue(!Strings.isNullOrEmpty(alert.getMessage()));
    assertNotNull(alert.getSeverityLevel());
    assertNotNull(alert.getPredicatesChain());
    assertTrue(!Iterables.isEmpty(alert.getPredicatesChain()));
    for (AdviserPredicate adviserPredicate : alert.getPredicatesChain()) {
      checkAdviserPredicate(adviserPredicate);
    }
  }

  private static void assertMatch(AlertInfo alertInfo, Alert alert) {
    assertNotNull(alertInfo);
    assertNotNull(alert);
    assertEquals(alertInfo.getId(), alert.getId());
    assertEquals(alertInfo.getInstrumentId(), alert.getInstrumentId());
    assertEquals(alertInfo.getMessage(), alert.getMessage());
    assertEquals(alertInfo.getCreatedOn(), alert.getCreatedOn());
    assertEquals(alertInfo.getLastFiredOn(), alert.getLastFiredOn());
    assertEquals(alertInfo.getRepeat(), alert.getRepeat());
    assertEquals(alertInfo.getSeverityLevel(), alert.getSeverityLevel());
    for (AdviserPredicateInfo adviserPredicateInfo : alertInfo.getPredicatesChainInfo()) {
      for (AdviserPredicate adviserPredicate : alert.getPredicatesChain()) {
        boolean recordFound = false;
        if (adviserPredicateInfo.getClassName().equals(adviserPredicate.getClass().getName()) &&
            adviserPredicateInfo.getExecutionParameters()
                .equals(adviserPredicate.getParametersCopy())) {
          assertTrue("Duplicated record found.", !recordFound);
          recordFound = true;
          TemplateInfo templateInfo =
              createAdviserPredicateTemplateInfo(adviserPredicate.getClass());
          assertEquals(adviserPredicateInfo.getAvailableParameters(),
              templateInfo.getAvailableParameters());
          assertEquals(adviserPredicateInfo.getDisplayName(), templateInfo.getDisplayName());
        }
        assertTrue(recordFound);
      }
    }
  }

  private static void assertMatch(AlertConfiguration alertConfiguration, Alert alert) {
    assertNotNull(alertConfiguration);
    assertNotNull(alert);
    assertEquals(alertConfiguration.getId(), alert.getId());
    assertEquals(alertConfiguration.getMessage(), alert.getMessage());
    assertEquals(alertConfiguration.getInstrumentId(), alert.getInstrumentId());
    assertEquals(alertConfiguration.getCreateOn(), alert.getCreatedOn());
    assertEquals(alertConfiguration.getSeverityLevel(), alert.getSeverityLevel());
    assertEquals(alertConfiguration.getRepeat(), alert.getRepeat());
    assertEquals(size(alertConfiguration.getPredicatesChain()), size(alert.getPredicatesChain()));
    for (AdviserPredicateConfiguration predicateConfiguration
        : alertConfiguration.getPredicatesChain()) {
      boolean recordFound = false;
      for (AdviserPredicate predicate : alert.getPredicatesChain()) {
        if (predicateConfiguration.getClassName().equals(predicate.getClass().getName()) &&
          predicateConfiguration.getParameters().equals(predicate.getParametersCopy())) {
          assertTrue("Duplicated record found.", !recordFound);
          recordFound = true;
        }
      }
      assertTrue(
          String.format("Predicate not found [%s].", predicateConfiguration), recordFound);
    }
  }
}
