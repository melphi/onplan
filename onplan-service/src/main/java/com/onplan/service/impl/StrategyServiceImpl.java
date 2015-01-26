package com.onplan.service.impl;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.onplan.adapter.InstrumentService;
import com.onplan.adapter.PriceListener;
import com.onplan.adapter.PriceService;
import com.onplan.domain.PriceBar;
import com.onplan.domain.PriceTick;
import com.onplan.domain.configuration.EventHandlerConfiguration;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@Service(value = "strategyService")
public class StrategyServiceImpl implements StrategyService {
  private static final Logger LOGGER = Logger.getLogger(StrategyServiceImpl.class);

  private final PriceListener priceListener = new InternalPriceListener();
  private final StrategyListener strategyListener = new InternalStrategyListener();
  private final StrategiesPool strategiesPool = new StrategiesPool();

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
    LOGGER.info("Setting price listener.");
    priceService.setPriceListener(priceListener);
    LOGGER.info("Loading strategies.");
    try {
      loadAllStrategies();
    } catch (Exception e) {
      LOGGER.error(String.format("Error while loading strategies: [%s].", e.getMessage()));
    }
  }

  @Override
  public List<Strategy> getStrategies() {
    return strategiesPool.getStrategiesList();
  }

  @Override
  public Set<String> getSubscribedInstruments() {
    return strategiesPool.getInstruments();
  }

  @Override
  public List<StrategyInfo> getStrategiesInfo() {
    ImmutableList.Builder<StrategyInfo> result = ImmutableList.builder();
    for (Strategy strategy : getStrategies()) {
      result.add(createStrategyInfo(strategy));
    }
    return result.build();
  }

  @Override
  public List<StrategyTemplateInfo> getStrategiesTemplateInfo() {
    try {
      return ImmutableList.of(
          createStrategyTemplateInfo(CandlestickHammerStrategy.class),
          createStrategyTemplateInfo(PriceSpikeStrategy.class),
          createStrategyTemplateInfo(IntegrationTestStrategy.class));
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
      return createStrategyTemplateInfo(clazz);
    } catch (Exception e) {
      LOGGER.warn(String.format(
          "Error while getting the strategy template info for [%s]: [%s]",
          clazz.getName(),
          e.getMessage()));
      return null;
    }
  }

  @Override
  public void removeStrategy(String strategyId) {
    checkNotNullOrEmpty(strategyId);
    LOGGER.info(String.format("Removing strategy [%s].", strategyId));
    strategiesPool.removeStrategy(strategyId);
    boolean result = strategyConfigurationDao.removeById(strategyId);
    if (!result) {
      LOGGER.warn(String.format("Strategy [%s] not found in database.", strategyId));
    }
  }

  /**
   * Loads a collection of sample strategies, replacing the previous ones. In case of errors tires
   * to restore the previous strategies on the database without loading them in memory and throws
   * an exception.
   * @throws Exception Error while replacing the strategies.
   */
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
      strategyConfigurationDao.insertAll(oldStrategies);
      throw e;
    }
  }

  private StrategyTemplateInfo createStrategyTemplateInfo(Class<? extends Strategy> clazz) {
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

  private StrategyInfo createStrategyInfo(Strategy strategy) {
    StrategyTemplateInfo strategyTemplateInfo = createStrategyTemplateInfo(strategy.getClass());
    StrategyInfo strategyInfo = new StrategyInfo();
    strategyInfo.setName(strategyTemplateInfo.getName());
    strategyInfo.setClassName(strategyTemplateInfo.getClassName());
    strategyInfo.setAvailableParameters(strategyTemplateInfo.getAvailableParameters());
    strategyInfo.setId(strategy.getId());
    strategyInfo.setExecutionParameters(strategy.getExecutionParameters());
    strategyInfo.setRegisteredInstruments(strategy.getRegisteredInstruments());
    return strategyInfo;
  }

  private void loadAllStrategies() throws Exception {
    checkNotNull(strategyListener);
    LOGGER.info("Loading strategies configuration from database.");
    List<StrategyConfiguration> strategyConfigurations = strategyConfigurationDao.findAll();
    Map<String, Iterable<Strategy>> strategies = Maps.newHashMap();
    for (StrategyConfiguration strategyConfiguration : strategyConfigurations) {
      for (String instrumentId : strategyConfiguration.getInstruments()) {
        Strategy strategy = createAndInitStrategy(strategyConfiguration, strategyListener);
        if (!strategies.containsKey(instrumentId)) {
          strategies.put(instrumentId, Lists.newArrayList());
        }
        // TODO(robertom): Refactor to avoid unchecked warning.
        ((List) strategies.get(instrumentId)).add(strategy);
      }
    }
    strategiesPool.setStrategies(strategies);
    LOGGER.info(String.format(
        "[%d] strategies loaded for instruments [%s].",
        strategyConfigurations.size(),
        Joiner.on(", ").join(strategiesPool.getInstruments())));
  }

  private Iterable<StrategyEventHandler> createStrategyEventHandlers(
      Iterable<EventHandlerConfiguration> eventHandlersConfiguration) {
    ImmutableList.Builder<StrategyEventHandler> eventHandlers = ImmutableList.builder();
    for (EventHandlerConfiguration eventHandlerConfiguration : eventHandlersConfiguration) {
      eventHandlers.add(new StrategyEventHandler(eventHandlerConfiguration));
    }
    return eventHandlers.build();
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
        "Strategy [%s] created and initialized for instruments [%s].",
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
