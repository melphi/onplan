package com.onplan.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.onplan.adviser.alert.Alert;
import com.onplan.adviser.predicate.AdviserPredicate;
import com.onplan.dao.TestingAlertConfigurationDao;
import com.onplan.domain.configuration.AdviserPredicateConfiguration;
import com.onplan.domain.configuration.AlertConfiguration;
import com.onplan.service.impl.AlertServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Iterables.size;
import static com.onplan.adviser.predicate.TestingAdviserPredicateUtils.checkAdviserPredicate;
import static com.onplan.factory.TestingAlertConfigurationFactory.createSampleAlertConfigurationWithNullId;
import static com.onplan.util.TestingConstants.DEFAULT_ALERT_ID;
import static com.onplan.util.TestingConstants.INITIAL_ALERTS_LIST_SIZE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AlertServiceImplTest {
  private final List<String> instrumentIds = ImmutableList.of(
      "CS.EURUSD.TODAY",
      "CS.AUDUSD.TODAY",
      "IX.DAX.DAILY",
      "IX.FTSE.DAILY");

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
    for (String instrumentId : instrumentIds) {
      verify(instrumentSubscriptionListener, times(1))
          .onInstrumentSubscriptionRequest(instrumentId);
    }
    for (String instrumentId : instrumentIds) {
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
    for (String instrumentId : instrumentIds) {
      verify(instrumentSubscriptionListener, never())
          .onInstrumentSubscriptionRequest(instrumentId);
    }
    for (String instrumentId : instrumentIds) {
      verify(instrumentSubscriptionListener, times(1))
          .onInstrumentUnSubscriptionRequest(instrumentId);
    }
  }

  @Test
  public void testRemoveAlertAfterInitialization() throws Exception {
    alertService.loadAllAlerts();
    Alert alertToRemove = alertService.getAlerts().stream().findFirst().get();
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
    Alert alert = alertService.getAlerts().stream().findFirst().get();
    assertTrue(alertService.removeAlert(alert.getId()));
    assertTrue(!alertService.hasAlerts());
    assertTrue(alertService.getAlerts().isEmpty());
  }

  @Test
  public void testAddAlertAfterInitialization() throws Exception {
    alertService.loadAllAlerts();
    AlertConfiguration alertConfiguration = createSampleAlertConfigurationWithNullId();
    String id = alertService.addAlert(alertConfiguration);
    long counter =
        alertService.getAlerts().stream().filter(alert -> id.equals(alert.getId())).count();
    assertEquals(1, counter);
    assertEquals(INITIAL_ALERTS_LIST_SIZE + 1, alertService.getAlerts().size());
    assertTrue(alertService.hasAlerts());
    Alert loadedAlert = alertService.getAlerts()
        .stream().filter(alert -> id.equals(alert.getId())).findFirst().get();
    alertConfiguration.setId(id);
    checkMatch(alertConfiguration, loadedAlert);
  }

  @Test
  public void testAddAlertAfterBeforeInitialization() throws Exception {
    AlertConfiguration alertConfiguration = createSampleAlertConfigurationWithNullId();
    String id = alertService.addAlert(alertConfiguration);
    assertTrue(alertService.hasAlerts());
    assertEquals(1, alertService.getAlerts().size());
    Alert loadedAlert = alertService.getAlerts().stream().findFirst().get();
    alertConfiguration.setId(id);
    checkMatch(alertConfiguration, loadedAlert);
  }

  @Ignore
  @Test
  public void testAddAlertReplacingExisting() throws Exception {
    fail("Not yet implemented.");
  }

  @Ignore
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

  @Ignore
  @Test
  public void testGetAlertsInfo() {
    fail("Not yet implemented.");
  }

  @Ignore
  @Test
  public void testOnPriceTickForRepeatTrue() {
    fail("Not yet implemented.");
  }

  @Ignore
  @Test
  public void testOnPriceTickForRepeatFalse() {
    fail("Not yet implemented.");
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

  private static void checkMatch(AlertConfiguration alertConfiguration, Alert alert) {
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
      boolean predicateFound = false;
      for (AdviserPredicate predicate : alert.getPredicatesChain()) {
        if (predicateConfiguration.getClassName().equals(predicate.getClass().getName()) &&
          predicateConfiguration.getParameters().equals(predicate.getExecutionParameters())) {
          predicateFound = true;
          break;
        }
      }
      assertTrue(
          String.format("Predicate not found [%s].", predicateConfiguration), predicateFound);
    }
  }
}
