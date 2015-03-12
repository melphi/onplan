package com.onplan.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.onplan.adviser.StrategyInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.adviser.TemplateMetaData;
import com.onplan.adviser.strategy.Strategy;
import com.onplan.adviser.strategy.system.IntegrationTestStrategy;
import com.onplan.connector.HistoricalPriceService;
import com.onplan.connector.InstrumentService;
import com.onplan.dao.TestingStrategyConfigurationDao;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.service.impl.StrategyServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.onplan.adviser.alert.TestingStrategyConfigurationFactory.createSampleStrategyConfigurationWithNullId;
import static com.onplan.util.TestingConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StrategyServiceImplTest {
  private StrategyConfigurationDao strategyConfigurationDao;
  private InstrumentService instrumentService;
  private HistoricalPriceService historicalPriceService;
  private EventNotificationService eventNotificationService;
  private StrategyServiceImpl strategyService;
  private InstrumentSubscriptionListener instrumentSubscriptionListener;

  @Before
  public void init() throws Exception {
    strategyConfigurationDao = new TestingStrategyConfigurationDao();
    instrumentService = new TestingInstrumentService();
    historicalPriceService = new TestingHistoricalPriceService();
    eventNotificationService = mock(EventNotificationService.class);
    instrumentSubscriptionListener = mock(InstrumentSubscriptionListener.class);
    strategyService = new StrategyServiceImpl();
    strategyService.setStrategyConfigurationDao(strategyConfigurationDao);
    strategyService.setInstrumentService(instrumentService);
    strategyService.setHistoricalPriceService(historicalPriceService);
    strategyService.setEventNotificationService(eventNotificationService);
    strategyService.setInstrumentSubscriptionListener(instrumentSubscriptionListener);
  }

  @Test
  public void testGetStrategies() throws Exception {
    strategyService.loadAllStrategies();
    List<Strategy> strategies = strategyService.getStrategies();
    assertTrue(!strategies.isEmpty());
    for (Strategy strategy : strategies) {
      assertValidStrategy(strategy);
    }
    assertEquals(strategies.size(), strategyConfigurationDao.findAll().size());
  }

  @Test
  public void testLoadAllStrategies() throws Exception {
    strategyService.loadAllStrategies();
    assertTrue(!strategyService.getStrategies().isEmpty());
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
  public void testUnLoadAllStrategies() throws Exception {
    strategyService.loadAllStrategies();
    reset(instrumentSubscriptionListener);
    strategyService.unLoadAllStrategies();
    assertTrue(strategyService.getStrategies().isEmpty());
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
  public void testRemoveStrategyAfterInitialization() throws Exception {
    strategyService.loadAllStrategies();
    Strategy strategyToRemove = strategyService.getStrategies().stream()
        .findFirst()
        .get();
    assertValidStrategy(strategyToRemove);
    assertTrue(strategyService.removeStrategy(strategyToRemove.getId()));
    assertTrue(strategyService.getStrategies().stream()
        .noneMatch(record -> strategyToRemove.getId().equals(record.getId())));
  }

  @Test
  public void testRemoveStrategyAfterUnloadAll() throws Exception {
    strategyService.loadAllStrategies();
    strategyService.unLoadAllStrategies();
    assertTrue(!strategyService.removeStrategy(DEFAULT_STRATEGY_ID));
    assertTrue(strategyService.getStrategies().isEmpty());
  }

  @Test
  public void testRemoveStrategyWhenEmpty() throws Exception {
    assertTrue(!strategyService.removeStrategy(DEFAULT_STRATEGY_ID));
    assertTrue(strategyService.getStrategies().isEmpty());
  }

  @Test
  public void testRemoveSingleStrategy() throws Exception {
    StrategyConfiguration strategyConfiguration = createSampleStrategyConfigurationWithNullId();
    strategyService.addStrategy(strategyConfiguration);
    Strategy strategy = strategyService.getStrategies().stream()
        .findFirst()
        .get();
    assertTrue(!strategyService.getStrategies().isEmpty());
    assertTrue(strategyConfigurationDao.findAll().stream()
        .anyMatch(record -> strategy.getId().equals(record.getId())));
    strategyService.removeStrategy(strategy.getId());
    assertTrue(strategyService.getStrategies().isEmpty());
    assertTrue(strategyConfigurationDao.findAll().stream()
        .noneMatch(record -> strategy.getId().equals(record.getId())));
  }

  @Test
  public void testAddStrategyAfterInitialization() throws Exception {
    strategyService.loadAllStrategies();
    StrategyConfiguration strategyConfiguration = createSampleStrategyConfigurationWithNullId();
    String id = strategyService.addStrategy(strategyConfiguration);
    long counter = strategyService.getStrategies().stream()
        .filter(record -> id.equals(record.getId()))
        .count();
    assertEquals(1, counter);
    assertEquals(INITIAL_STRATEGIES_LIST_SIZE + 1, strategyService.getStrategies().size());
    Strategy loadedStrategy = strategyService.getStrategies().stream()
        .filter(record -> id.equals(record.getId()))
        .findFirst()
        .get();
    strategyConfiguration.setId(id);
    assertMatch(strategyConfiguration, loadedStrategy);
  }

  @Test
  public void testAddStrategyBeforeInitialization() throws Exception {
    StrategyConfiguration strategyConfiguration = createSampleStrategyConfigurationWithNullId();
    String id = strategyService.addStrategy(strategyConfiguration);
    assertEquals(1, strategyService.getStrategies().size());
    Strategy loadedStrategy = strategyService.getStrategies().stream()
        .filter(record -> id.equals(record.getId()))
        .findFirst()
        .get();
    strategyConfiguration.setId(id);
    assertMatch(strategyConfiguration, loadedStrategy);
  }

  @Test
  public void testAddStrategyReplacingExisting() throws Exception {
    strategyService.loadAllStrategies();
    StrategyConfiguration strategyConfiguration = strategyConfigurationDao.findAll().stream()
        .findFirst()
        .get();
    strategyConfiguration.setInstruments(ImmutableSet.of(INSTRUMENT_AUDUSD_ID));
    strategyConfiguration.setExecutionParameters(DEFAULT_ADVISER_PARAMETERS);
    String id = strategyService.addStrategy(strategyConfiguration);
    Strategy strategy = strategyService.getStrategies().stream()
        .filter(record -> id.equals(record.getId()))
        .findFirst()
        .get();
    assertMatch(strategyConfiguration, strategy);
  }

  @Test
  public void testLoadSampleStrategies() throws Exception {
    strategyService.loadSampleStrategies();
    List<Strategy> sampleStrategies = strategyService.getStrategies();
    assertEquals(INITIAL_STRATEGIES_LIST_SIZE, sampleStrategies.size());
    strategyService.unLoadAllStrategies();
    strategyService.loadAllStrategies();
    List<Strategy> savedStrategies = strategyService.getStrategies();
    assertEquals(INITIAL_STRATEGIES_LIST_SIZE, savedStrategies.size());
    for (Strategy savedStrategy : savedStrategies) {
      assertTrue(sampleStrategies.contains(savedStrategy));
    }
  }

  @Test
  public void testGetStrategiesInfo() throws Exception {
    strategyService.loadAllStrategies();
    List<Strategy> strategies = strategyService.getStrategies();
    List<StrategyInfo> strategiesInfo = strategyService.getStrategiesInfo();
    assertEquals(strategies.size(), INITIAL_STRATEGIES_LIST_SIZE);
    assertEquals(strategies.size(), strategiesInfo.size());
    for (StrategyInfo strategyInfo : strategiesInfo) {
      assertMatch(
          strategyInfo,
          strategies.stream()
              .filter(record -> strategyInfo.getId().equals(record.getId()))
              .findFirst()
              .get());
    }
  }

  @Test
  public void testGetStrategiesTemplateInfo() throws Exception {
    List<TemplateInfo> templatesInfo = strategyService.getStrategiesTemplateInfo();
    TemplateInfo templateInfo = templatesInfo.stream()
        .filter(record -> record.getClassName().equals(IntegrationTestStrategy.class.getName()))
        .findFirst()
        .get();
    assertMatch(templateInfo, IntegrationTestStrategy.class);
  }

  @Test
  public void testGetStrategyTemplateInfo() {
    TemplateInfo templateInfo =
        strategyService.getStrategyTemplateInfo(IntegrationTestStrategy.class.getName());
    assertMatch(templateInfo, IntegrationTestStrategy.class);
  }

  @Ignore
  public void testOnPriceTickWithAlertEvent() {
    fail("Not yet implemented.");
  }

  @Ignore
  public void testOnPriceTickWithoutAlertEvent() {
    fail("Not yet implemented.");
  }

  private static void assertValidStrategy(Strategy strategy) {
    assertNotNull(strategy);
    assertNotNull(strategy.getExecutionParameters());
    assertNotNull(strategy.getStrategyStatistics());
    assertNotNull(strategy.getRegisteredInstruments());
    assertTrue(!Strings.isNullOrEmpty(strategy.getId()));
    assertTrue(!strategy.getRegisteredInstruments().isEmpty());
  }

  private static void assertMatch(StrategyInfo strategyInfo, Strategy strategy) {
    assertNotNull(strategyInfo);
    assertNotNull(strategy);
    assertEquals(strategy.getId(), strategyInfo.getId());
    assertEquals(strategy.getExecutionParameters(), strategyInfo.getExecutionParameters());
    assertEquals(strategy.getRegisteredInstruments(), strategyInfo.getRegisteredInstruments());
    TemplateMetaData templateMetaData = strategy.getClass().getAnnotation(TemplateMetaData.class);
    assertEquals(templateMetaData.displayName(), strategyInfo.getDisplayName());
    assertArrayEquals(templateMetaData.availableParameters(),
        Iterables.toArray(strategyInfo.getAvailableParameters(), String.class));
    assertEquals(strategy.getClass().getName(), strategyInfo.getClassName());
  }

  private static void assertMatch(
      TemplateInfo templateInfo, Class<? extends Strategy> strategyClass) {
    assertNotNull(templateInfo);
    assertNotNull(strategyClass);
    assertEquals(templateInfo.getClassName(), strategyClass.getName());
    TemplateMetaData templateMetaData = strategyClass.getAnnotation(TemplateMetaData.class);
    assertEquals(templateMetaData.displayName(), templateInfo.getDisplayName());
    assertArrayEquals(templateMetaData.availableParameters(),
        Iterables.toArray(templateInfo.getAvailableParameters(), String.class));
  }

  private static void assertMatch(
      StrategyConfiguration strategyConfiguration, Strategy strategy) {
    assertNotNull(strategyConfiguration);
    assertNotNull(strategy);
    assertEquals(strategyConfiguration.getId(), strategy.getId());
    assertEquals(strategyConfiguration.getClassName(), strategy.getClass().getName());
    assertEquals(
        strategyConfiguration.getExecutionParameters(), strategy.getExecutionParameters());
    assertEquals(strategyConfiguration.getInstruments(), strategy.getRegisteredInstruments());
  }
}
