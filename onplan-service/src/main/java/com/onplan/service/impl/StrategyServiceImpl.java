package com.onplan.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.onplan.adviser.StrategyInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.adviser.automatedorder.AutomatedOrderEvent;
import com.onplan.adviser.strategy.Strategy;
import com.onplan.adviser.strategy.StrategyListener;
import com.onplan.adviser.strategy.scripting.JavaScriptStrategy;
import com.onplan.adviser.strategy.system.IntegrationTestStrategy;
import com.onplan.connector.HistoricalPriceService;
import com.onplan.connector.InstrumentService;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.domain.transitory.PriceTick;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.service.EventNotificationService;
import com.onplan.service.InstrumentSubscriptionListener;
import com.onplan.service.StrategyService;
import org.apache.log4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.onplan.adviser.strategy.StrategyUtil.*;
import static com.onplan.service.impl.AdviserFactory.createStrategy;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Singleton
public final class StrategyServiceImpl extends AbstractAdviserService implements StrategyService {
  private static final Logger LOGGER = Logger.getLogger(StrategyServiceImpl.class);

  private final StrategyListener strategyListener = new InternalStrategyListener();
  private final StrategyPool strategiesPool = new StrategyPool();
  private final List<Strategy> registeredStrategies = Lists.newArrayList();
  // TODO(robertom): Register available strategies at runtime.
  private final Collection<Class<? extends Strategy>> availableStrategies =
      ImmutableList.of(IntegrationTestStrategy.class, JavaScriptStrategy.class);

  private StrategyConfigurationDao strategyConfigurationDao;
  private InstrumentService instrumentService;
  private HistoricalPriceService historicalPriceService;
  private EventNotificationService eventNotificationService;

  @Inject
  public void setStrategyConfigurationDao(StrategyConfigurationDao strategyConfigurationDao) {
    this.strategyConfigurationDao = strategyConfigurationDao;
  }

  @Inject
  public void setInstrumentService(InstrumentService instrumentService) {
    this.instrumentService = instrumentService;
  }

  @Inject
  public void setHistoricalPriceService(HistoricalPriceService historicalPriceService) {
    this.historicalPriceService = historicalPriceService;
  }

  @Inject
  public void setEventNotificationService(EventNotificationService eventNotificationService){
    this.eventNotificationService = eventNotificationService;
  }

  @Override
  public void onPriceTick(final PriceTick priceTick) {
    strategiesPool.processPriceTick(priceTick);
  }

  @Override
  public List<Strategy> getStrategies() {
    return ImmutableList.copyOf(registeredStrategies);
  }

  @Override
  public List<StrategyInfo> getStrategiesInfo() {
    ImmutableList.Builder<StrategyInfo> result = ImmutableList.builder();
    for (Strategy strategy : registeredStrategies) {
      result.add(getStrategyInfo(strategy));
    }
    return result.build();
  }

  @Override
  public void setInstrumentSubscriptionListener(
      InstrumentSubscriptionListener instrumentSubscriptionListener) {
    super.setInstrumentSubscriptionListener(instrumentSubscriptionListener);
  }

  @Override
  public List<String> getStrategyTemplatesIds() {
    List<String> result = Lists.newArrayListWithCapacity(availableStrategies.size());
    try {
      for (Class clazz : availableStrategies) {
        result.add(getTemplateId(clazz));
      }
    } catch (Exception e) {
      LOGGER.error(
          String.format("Error while loading available strategies [%s].", e.getMessage()));
      throw e;
    }
    return result;
  }

  @Override
  public TemplateInfo getStrategyTemplateInfo(String className) {
    checkNotNullOrEmpty(className);
    Class clazz = null;
    try {
      clazz = getClass().getClassLoader().loadClass(className);
    } catch (ClassNotFoundException e) {
      return null;
    }
    try {
      return getTemplateInfo(clazz);
    } catch (Exception e) {
      LOGGER.warn(String.format(
          "Error while getting the strategy template info for [%s]: [%s]",
          clazz.getName(),
          e.getMessage()));
      return null;
    }
  }

  @Override
  public String addStrategy(StrategyConfiguration strategyConfiguration) throws Exception {
    checkStrategyConfiguration(strategyConfiguration);
    StrategyConfiguration oldStrategyConfiguration = null;
    try {
      if (!isNullOrEmpty(strategyConfiguration.getId())) {
        oldStrategyConfiguration = strategyConfigurationDao.findById(strategyConfiguration.getId());
        unLoadStrategy(strategyConfiguration.getId());
      }
      strategyConfiguration = strategyConfigurationDao.saveAndGet(strategyConfiguration);
      loadStrategy(strategyConfiguration);
    } catch (Exception e) {
      if (null != oldStrategyConfiguration) {
        strategyConfigurationDao.save(oldStrategyConfiguration);
      }
      LOGGER.error(String.format(
          "Error while loading strategy configuration [%s]: [%s].",
          strategyConfiguration,
          e.getMessage()));
      throw e;
    }
    LOGGER.info(String.format("Strategy [%s] saved in database.", strategyConfiguration.getId()));
    return strategyConfiguration.getId();
  }

  @Override
  public boolean removeStrategy(String strategyId) throws Exception {
    checkNotNullOrEmpty(strategyId);
    if (strategyConfigurationDao.removeById(strategyId)) {
      LOGGER.info(String.format("Strategy [%s] removed from database.", strategyId));
    } else {
      LOGGER.warn(String.format("Strategy [%s] not found in database.", strategyId));
    }
    try {
      return unLoadStrategy(strategyId);
    } catch (Exception e) {
      LOGGER.error(
          String.format("Error while removing strategy [%s]: [%s].", strategyId, e.getMessage()));
      throw e;
    }
  }

  @Override
  public long loadSampleStrategies() throws Exception {
    List<StrategyConfiguration> oldStrategies = strategyConfigurationDao.findAll();
    List<StrategyConfiguration> sampleStrategies =
        strategyConfigurationDao.getSampleStrategiesConfiguration();
    LOGGER.info(String.format(
        "Replacing [%d] old strategies with [%d] sample strategies.",
        oldStrategies.size(),
        sampleStrategies.size()));
    strategyConfigurationDao.removeAll();
    strategyConfigurationDao.insertAll(sampleStrategies);
    try {
      unLoadAllStrategies();
      loadAllStrategies();
      return sampleStrategies.size();
    } catch (Exception e) {
      LOGGER.error(String.format("Error while loading strategies: [%s].", e.getMessage()));
      strategyConfigurationDao.removeAll();
      strategyConfigurationDao.insertAll(oldStrategies);
      throw e;
    }
  }

  @Override
  public void loadAllStrategies() throws Exception {
    LOGGER.info("Loading strategies configuration from database.");
    checkArgument(strategiesPool.poolSize() == 0, "Unload all the registered strategies first.");
    List<StrategyConfiguration> strategyConfigurations = strategyConfigurationDao.findAll();
    LOGGER.info(String.format("[%s] strategies found in database.", strategyConfigurations.size()));
    for (StrategyConfiguration strategyConfiguration : strategyConfigurations) {
      try {
        loadStrategy(strategyConfiguration);
      } catch (Exception e) {
        LOGGER.error(String.format(
            "Error while loading strategy [%s] [%s] with parameters [%s]: [%s].",
            strategyConfiguration.getId(),
            strategyConfiguration.getClassName(),
            strategyConfiguration.getExecutionParameters(),
            e.getMessage()));
        throw e;
      }
    }
    checkArgument(strategyConfigurations.size() == strategiesPool.poolSize(), String.format(
        "Expected [%d] strategies registered but [%d] strategies found in the pool",
        strategyConfigurations.size(),
        strategiesPool.poolSize()));
  }

  @Override
  public void unLoadAllStrategies() throws Exception {
    for (Strategy strategy : ImmutableList.copyOf(registeredStrategies)) {
      unLoadStrategy(strategy.getId());
    }
    checkArgument(registeredStrategies.isEmpty(), String.format(
        "Expected zero registered strategies but [%d] strategies found.",
        registeredStrategies.size()));
    checkArgument(strategiesPool.poolSize() == 0, String.format(
        "Expected zero strategies in pool but [%d] strategies found.",
        strategiesPool.poolSize()));
  }

  private static void checkStrategyConfiguration(StrategyConfiguration strategyConfiguration) {
    checkNotNull(strategyConfiguration);
    checkNotNullOrEmpty(strategyConfiguration.getClassName());
    checkNotNull(strategyConfiguration.getExecutionParameters());
    checkNotNull(strategyConfiguration.getInstruments());
    checkArgument(!strategyConfiguration.getInstruments().isEmpty());
    for (String instrumentId : strategyConfiguration.getInstruments()) {
      checkNotNullOrEmpty(instrumentId, "Strategy configuration contains empty instruments");
    }
  }

  private void loadStrategy(StrategyConfiguration strategyConfiguration) throws Exception {
    checkStrategyConfiguration(strategyConfiguration);
    checkArgument(!strategiesPool.containsStrategy(strategyConfiguration.getId()), String.format(
        "Strategy with id [%s] is already in the pool.", strategyConfiguration.getId()));
    Strategy strategy = createStrategy(
        strategyConfiguration, strategyListener, instrumentService, historicalPriceService);
    strategy.init();
    LOGGER.info(String.format(
        "Strategy [%s] [%s] created and initialized for instruments [%s].",
        strategy.getId(),
        strategy.getClass().getName(),
        Joiner.on(", ").join(strategy.getRegisteredInstruments())));
    strategiesPool.addStrategy(strategy);
    registeredStrategies.add(strategy);
    for (String instrumentId : strategyConfiguration.getInstruments()) {
      dispatchInstrumentSubscriptionRequired(instrumentId);
    }
  }

  private boolean unLoadStrategy(String strategyId) throws Exception{
    checkNotNullOrEmpty(strategyId);
    if (!strategiesPool.removeStrategy(strategyId)) {
      LOGGER.warn(String.format("Strategy [%s] not found in the pool.", strategyId));
    }
    Strategy strategy = registeredStrategies.stream()
        .filter(record -> strategyId.equals(record.getId()))
        .findFirst()
        .orElse(null);
    if (null == strategy) {
      LOGGER.warn(String.format("Strategy [%s] not found in registeredStrategies.", strategyId));
      return false;
    }
    registeredStrategies.remove(strategy);
    LOGGER.info(String.format("Strategy [%s] un-loaded.", strategyId));
    // TODO(robertom): Fix instrument un-subscription logic.
    for (String instrumentId : strategy.getRegisteredInstruments()) {
      dispatchInstrumentUnSubscriptionRequired(instrumentId);
    }
    return true;
  }

  private final class InternalStrategyListener implements StrategyListener {
    @Override
    public void onNewOrder(final AutomatedOrderEvent automatedOrderEvent) {
      throw new IllegalArgumentException("Not yet implemented.");
    }

    @Override
    public void onAlert(final AlertEvent alertEvent) {
      eventNotificationService.notifyAlertEventAsync(alertEvent);
    }
  }
}
