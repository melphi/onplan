package com.onplan.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.onplan.adapter.InstrumentService;
import com.onplan.adapter.PriceListener;
import com.onplan.adapter.PriceService;
import com.onplan.domain.PriceBar;
import com.onplan.domain.PriceTick;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.processor.StrategyEventProcessor;
import com.onplan.service.StrategyService;
import com.onplan.strategy.*;
import com.onplan.strategy.alert.IntegrationTestStrategy;
import com.onplan.strategy.alert.PriceSpikeStrategy;
import com.onplan.strategy.alert.candlestickpattern.CandlestickHammerStrategy;
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

  private final PriceListener priceListener = new InternalPriceListener();
  private final StrategyListener strategyListener = new InternalStrategyListener();
  private final StrategiesPool strategiesPool = new StrategiesPool();
  private final List<Strategy> registeredStrategies = Lists.newArrayList();
  private final Set<String> registeredInstruments = Sets.newHashSet();

  @Autowired
  private StrategyConfigurationDao strategyConfigurationDao;

  @Autowired
  private InstrumentService instrumentService;

  @Autowired
  private StrategyEventProcessor eventProcessor;

  private PriceService priceService;

  @Autowired
  public void setPriceService(PriceService priceService) {
    checkArgument(this.priceService == null, "PriceService is already set.");
    this.priceService = checkNotNull(priceService);
    priceService.setPriceListener(priceListener);
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
      result.add(getStrategyInfo(strategy));
    }
    return result.build();
  }

  @Override
  public List<StrategyTemplateInfo> getStrategiesTemplateInfo() {
    try {
      return ImmutableList.of(
          getStrategyTemplateInfo(CandlestickHammerStrategy.class),
          getStrategyTemplateInfo(PriceSpikeStrategy.class),
          getStrategyTemplateInfo(IntegrationTestStrategy.class));
    } catch (Exception e) {
      LOGGER.error(
          String.format("Error while loading available strategies [%s].", e.getMessage()));
      throw e;
    }
  }

  @Override
  public StrategyTemplateInfo getStrategyTemplateInfo(String className) {
    checkNotNullOrEmpty(className);
    Class clazz = null;
    try {
      clazz = getClass().getClassLoader().loadClass(className);
    } catch (ClassNotFoundException e) {
      return null;
    }
    try {
      return getStrategyTemplateInfo(clazz);
    } catch (Exception e) {
      LOGGER.warn(String.format(
          "Error while getting the strategy template info for [%s]: [%s]",
          clazz.getName(),
          e.getMessage()));
      return null;
    }
  }

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

  private StrategyInfo getStrategyInfo(final Strategy strategy) {
    StrategyTemplateInfo strategyTemplateInfo = getStrategyTemplateInfo(strategy.getClass());
    StrategyInfo strategyInfo = new StrategyInfo();
    strategyInfo.setName(strategyTemplateInfo.getName());
    strategyInfo.setClassName(strategyTemplateInfo.getClassName());
    strategyInfo.setAvailableParameters(strategyTemplateInfo.getAvailableParameters());
    strategyInfo.setId(strategy.getId());
    strategyInfo.setExecutionParameters(strategy.getExecutionParameters());
    strategyInfo.setRegisteredInstruments(strategy.getRegisteredInstruments());
    strategyInfo.setStrategyStatistics(strategy.getStrategyStatistics());
    return strategyInfo;
  }

  private StrategyTemplateInfo getStrategyTemplateInfo(Class<? extends Strategy> clazz) {
    StrategyTemplate strategyMetaData = clazz.getAnnotation(StrategyTemplate.class);
    checkNotNull(strategyMetaData, String.format(
        "Strategy [%s] does not implement the annotation [%s].",
        clazz.getName(),
        StrategyTemplate.class.getName()));
    StrategyTemplateInfo strategyTemplateInfo = new StrategyInfo();
    strategyTemplateInfo.setName(strategyMetaData.name());
    strategyTemplateInfo.setClassName(clazz.getName());
    strategyTemplateInfo.setAvailableParameters(
        ImmutableList.copyOf(strategyMetaData.availableParameters()));
    return strategyTemplateInfo;
  }

  private Strategy createAndInitStrategy(StrategyConfiguration strategyConfiguration,
      StrategyListener strategyListener) throws Exception {
    StrategyExecutionContext strategyExecutionContext = StrategyExecutionContext.newBuilder()
        .setStrategyId(strategyConfiguration.getId())
        .setExecutionParameters(strategyConfiguration.getExecutionParameters())
        .setStrategyListener(checkNotNull(strategyListener, "strategyListener is null."))
        .setInstrumentService(checkNotNull(instrumentService, "instrumentService is null."))
        .setPriceService(checkNotNull(priceService, "priceService is null."))
        .setRegisteredInstruments(strategyConfiguration.getInstruments())
        .build();
    Class clazz = null;
    Strategy strategy = null;
    try {
      clazz = getClass().getClassLoader().loadClass(strategyConfiguration.getClassName());
      strategy = (Strategy) clazz.newInstance();
      strategy.setExecutionContext(strategyExecutionContext);
      strategy.initStrategy();
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

  private class InternalPriceListener implements PriceListener {
    @Override
    public void onPriceTick(PriceTick priceTick) {
      strategiesPool.processPriceTick(priceTick);
    }

    @Override
    public void onPriceBar(PriceBar priceBar) {
      // Intentionally empty.
    }
  }

  private class InternalStrategyListener implements StrategyListener {
    @Override
    public void onEvent(StrategyEvent strategyEvent) {
      try {
        LOGGER.info(String.format("StrategyEvent [%s].", strategyEvent));
        eventProcessor.processStrategyEvent(strategyEvent);
      } catch (Exception e) {
        LOGGER.error(String.format(
            "Error while processor event. Message: [%s] Event: [%s]",
            e.getMessage(),
            strategyEvent));
      }
    }
  }
}
