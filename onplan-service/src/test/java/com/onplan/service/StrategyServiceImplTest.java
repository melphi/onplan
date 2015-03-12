package com.onplan.service;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.onplan.adviser.strategy.Strategy;
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

  @Ignore
  public void testGetStrategiesInfo() {
    fail("Not yet implemented.");
  }

  @Ignore
  public void testGetStrategiesTemplateInfo() {
    fail("Not yet implemented.");
  }

  @Ignore
  public void testGetStrategyTemplateInfo() {
    fail("Not yet implemented.");
  }

  @Ignore
  public void testOnPriceTick() {
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

  private static void assertMatch(
      StrategyConfiguration strategyConfiguration, Strategy loadedStrategy) {
    assertNotNull(strategyConfiguration);
    assertNotNull(loadedStrategy);
    assertEquals(strategyConfiguration.getId(), loadedStrategy.getId());
    assertEquals(strategyConfiguration.getClassName(), loadedStrategy.getClass().getName());
    assertEquals(
        strategyConfiguration.getExecutionParameters(), loadedStrategy.getExecutionParameters());
    assertEquals(strategyConfiguration.getInstruments(), loadedStrategy.getRegisteredInstruments());
  }
}
