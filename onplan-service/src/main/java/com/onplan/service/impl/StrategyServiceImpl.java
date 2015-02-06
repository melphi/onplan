package com.onplan.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.onplan.adapter.HistoricalPriceService;
import com.onplan.adapter.InstrumentService;
import com.onplan.adapter.PriceService;
import com.onplan.adviser.StrategyInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.adviser.alert.AlertEvent;
import com.onplan.adviser.automatedorder.AutomatedOrderEvent;
import com.onplan.adviser.strategy.Strategy;
import com.onplan.adviser.strategy.StrategyExecutionContext;
import com.onplan.adviser.strategy.StrategyListener;
import com.onplan.adviser.strategy.StrategyUtil;
import com.onplan.adviser.strategy.system.IntegrationTestStrategy;
import com.onplan.domain.PriceTick;
import com.onplan.domain.configuration.adviser.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.service.EventNotificationService;
import com.onplan.service.StrategyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Service(value = "strategyService")
public final class StrategyServiceImpl implements StrategyService {
  private static final Logger LOGGER = Logger.getLogger(StrategyServiceImpl.class);
  private static final String ALERT_MESSAGE_TITLE = "Strategy alert.";

  private final StrategyListener strategyListener = new InternalStrategyListener();
  private final StrategyPool strategiesPool = new StrategyPool();
  private final List<Strategy> registeredStrategies = Lists.newArrayList();
  private final Set<String> registeredInstruments = Sets.newHashSet();
  // TODO(robertom): Register available strategies at runtime.
  private final Iterable<Class<? extends Strategy>> availableStrategies =
      ImmutableList.of(IntegrationTestStrategy.class);

  @Autowired
  private StrategyConfigurationDao strategyConfigurationDao;

  @Autowired
  private InstrumentService instrumentService;

  // TODO(robertom): Decouple priceService and register instruments via listener.
  private PriceService priceService;

  @Autowired
  private HistoricalPriceService historicalPriceService;

  @Autowired
  private EventNotificationService eventNotificationService;

  @Autowired
  public void setPriceService(PriceService priceService) {
    checkArgument(this.priceService == null, "PriceService is already set.");
    this.priceService = checkNotNull(priceService);
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
  public Set<String> getSubscribedInstruments() {
    return ImmutableSet.copyOf(registeredInstruments);
  }

  @Override
  public List<StrategyInfo> getStrategiesInfo() {
    ImmutableList.Builder<StrategyInfo> result = ImmutableList.builder();
    for (Strategy strategy : registeredStrategies) {
      result.add(StrategyUtil.getStrategyInfo(strategy));
    }
    return result.build();
  }

  @Override
  public List<TemplateInfo> getStrategiesTemplateInfo() {
    ImmutableList.Builder<TemplateInfo> result = ImmutableList.builder();
    try {
      for (Class clazz : availableStrategies) {
        result.add(StrategyUtil.getStrategyTemplateInfo(clazz));
      }
    } catch (Exception e) {
      LOGGER.error(
          String.format("Error while loading available strategies [%s].", e.getMessage()));
      throw e;
    }
    return result.build();
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
      return StrategyUtil.getStrategyTemplateInfo(clazz);
    } catch (Exception e) {
      LOGGER.warn(String.format(
          "Error while getting the strategy template info for [%s]: [%s]",
          clazz.getName(),
          e.getMessage()));
      return null;
    }
  }

  // TODO(robertom): Call the garbage collector, test the performance improvement.
  @Override
  public void addStrategy(StrategyConfiguration strategyConfiguration) throws Exception {
    checkStrategyConfiguration(strategyConfiguration);
    strategyConfiguration = strategyConfigurationDao.save(strategyConfiguration);
    LOGGER.info(String.format("Strategy [%s] saved in database.", strategyConfiguration.getId()));
    try {
      loadStrategy(strategyConfiguration);
    } catch (Exception e) {
      LOGGER.error(String.format(
          "Error while loading strategy configuration [%s]: [%s].",
          strategyConfiguration,
          e.getMessage()));
      strategyConfigurationDao.removeById(strategyConfiguration.getId());
      LOGGER.info(String.format(
          "Strategy [%s] removed from database.", strategyConfiguration.getId()));
      throw e;
    }
  }

  // TODO(robertom): Call the garbage collector, test the performance improvement.
  @Override
  public void removeStrategy(String strategyId) throws Exception {
    checkNotNullOrEmpty(strategyId);
    if (strategyConfigurationDao.removeById(strategyId)) {
      LOGGER.info(String.format("Strategy [%s] removed from database.", strategyId));
    } else {
      LOGGER.error(String.format("Strategy [%s] not found in database.", strategyId));
    }
    try {
      unLoadStrategy(strategyId);
    } catch (Exception e) {
      LOGGER.error(
          String.format("Error while removing strategy [%s]: [%s].", strategyId, e.getMessage()));
      throw e;
    }
  }

  @Override
  public void loadSampleStrategies() throws Exception {
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
      loadAllStrategies();
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
    List<StrategyConfiguration> strategyConfigurations = strategyConfigurationDao.findAll();
    LOGGER.info(String.format("%s strategies found in database.", strategyConfigurations.size()));
    for (StrategyConfiguration strategyConfiguration : strategyConfigurations) {
      loadStrategy(strategyConfiguration);
    }
    checkArgument(strategyConfigurations.size() == strategiesPool.poolSize(), String.format(
        "Expected [%d] strategies registered but [%d] strategies found in the pool",
        strategyConfigurations.size(),
        strategiesPool.poolSize()));
    LOGGER.info(String.format(
        "[%d] strategies loaded for instruments [%s].",
        strategyConfigurations.size(),
        Joiner.on(", ").join(registeredInstruments)));
  }

  @Override
  public void unLoadAllStrategies() throws Exception {
    LOGGER.info(String.format(
        "Un-loading all the [%d] registered strategies.", registeredStrategies.size()));
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
    Strategy strategy = createAndInitStrategy(strategyConfiguration, strategyListener);
    strategiesPool.addStrategy(strategy);
    registeredStrategies.add(strategy);
    registeredInstruments.addAll(strategy.getRegisteredInstruments());
    LOGGER.info(String.format("Strategy [%s] [%s] loaded.",
        strategyConfiguration.getId(),
        strategyConfiguration.getClassName()));
    if (priceService.isConnected()) {
      updatePriceServiceSubscribedInstruments();
    }
  }

  private void unLoadStrategy(String strategyId) throws Exception{
    checkNotNullOrEmpty(strategyId);
    strategiesPool.removeStrategy(strategyId);
    Strategy strategy = null;
    for (Strategy item : registeredStrategies) {
      if (strategyId.equals(item.getId())) {
        strategy = item;
        break;
      }
    }
    checkNotNull(strategy, String.format("Strategy [%] not found.", strategyId));
    registeredStrategies.remove(strategy);
    registeredInstruments.clear();
    for (Strategy registeredStrategy : registeredStrategies) {
      registeredInstruments.addAll(registeredStrategy.getRegisteredInstruments());
    }
    LOGGER.info(String.format("Strategy [%s] un-loaded.", strategyId));
    if (priceService.isConnected()) {
      updatePriceServiceSubscribedInstruments();
    }
  }

  private void updatePriceServiceSubscribedInstruments() throws Exception {
    Collection<String> priceServiceInstruments = priceService.getSubscribedInstruments();
    for (String poolInstrumentId : registeredInstruments) {
      if (!priceServiceInstruments.contains(poolInstrumentId)) {
        priceService.subscribeInstrument(poolInstrumentId);
        LOGGER.info(String.format(
            "Instrument [%s] subscribed in price service.", poolInstrumentId));
      }
    }
    for (String priceServiceInstrumentId : priceServiceInstruments) {
      if (!registeredInstruments.contains(priceServiceInstrumentId)) {
        priceService.unsubscribeInstrument(priceServiceInstrumentId);
        LOGGER.info(String.format(
            "Instrument [%s] un-subscribed from price service.", priceServiceInstrumentId));
      }
    }
  }

  private Strategy createAndInitStrategy(StrategyConfiguration strategyConfiguration,
      StrategyListener strategyListener) throws Exception {
    StrategyExecutionContext strategyExecutionContext = StrategyExecutionContext.newBuilder()
        .setStrategyId(strategyConfiguration.getId())
        .setExecutionParameters(strategyConfiguration.getExecutionParameters())
        .setStrategyListener(checkNotNull(strategyListener, "strategyListener is null."))
        .setInstrumentService(checkNotNull(instrumentService, "instrumentService is null."))
        .setHistoricalPriceService(
            checkNotNull(historicalPriceService, "historicalPriceService is null."))
        .setRegisteredInstruments(strategyConfiguration.getInstruments())
        .build();
    Class clazz = null;
    Strategy strategy = null;
    try {
      clazz = getClass().getClassLoader().loadClass(strategyConfiguration.getClassName());
      strategy = (Strategy) clazz.getConstructor(StrategyExecutionContext.class)
          .newInstance(strategyExecutionContext);
      strategy.init();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      LOGGER.error(e);
      throw new Exception(String.format(
          "[%] exception while loading strategy id [%s] for class [%s]: [%s].",
          e,
          strategyConfiguration.getId(),
          strategyConfiguration.getClassName(),
          e.getMessage()));
    }
    LOGGER.info(String.format(
        "Strategy [%s] [%s] created and initialized for instruments [%s].",
        strategy.getId(),
        clazz.getName(),
        Joiner.on(", ").join(strategyConfiguration.getInstruments())));
    return strategy;
  }

  private class InternalStrategyListener implements StrategyListener {
    @Override
    public void onNewOrder(final AutomatedOrderEvent automatedOrderEvent) {
      throw new IllegalArgumentException("Not yet implemented.");
    }

    @Override
    public void onAlert(final AlertEvent alertEvent) {
      eventNotificationService.notifyAlertAsync(alertEvent);
    }
  }
}
