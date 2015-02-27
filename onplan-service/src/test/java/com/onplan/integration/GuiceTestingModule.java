package com.onplan.integration;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.onplan.adapter.HistoricalPriceService;
import com.onplan.adapter.InstrumentService;
import com.onplan.adapter.PriceService;
import com.onplan.adapter.ServiceConnection;
import com.onplan.adapter.igindex.IgIndexConnection;
import com.onplan.adapter.igindex.IgIndexHistoricalPriceService;
import com.onplan.adapter.igindex.IgIndexInstrumentService;
import com.onplan.adapter.igindex.IgIndexPriceService;
import com.onplan.persistence.AlertConfigurationDao;
import com.onplan.persistence.StrategyConfigurationDao;
import com.onplan.persistence.mongodb.MongoDbAlertConfigurationDao;
import com.onplan.persistence.mongodb.MongoDbStrategyConfigurationDao;
import com.onplan.service.AlertService;
import com.onplan.service.EventNotificationService;
import com.onplan.service.StrategyService;
import com.onplan.service.impl.AlertServiceImpl;
import com.onplan.service.impl.EventNotificationServiceImpl;
import com.onplan.service.impl.StrategyServiceImpl;
import com.onplan.util.PropertiesUtil;

import javax.inject.Singleton;
import java.util.List;
import java.util.Properties;

import static com.onplan.util.PropertiesUtil.loadPropertiesFromFile;

public class GuiceTestingModule extends AbstractModule {
  private static final String BROKER_PROPERTIES_FILE = "broker-testing.properties";
  private static final List<String> PROPERTIES_FILES = ImmutableList.of(
      "marketinfo-testing.properties",
      "mongodb-testing.properties",
      "notification-testing.properties");

  private static IgIndexConnection igIndexServiceConnection;

  @Override
  protected void configure() {
    try {
      loadPropertyFiles();
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

    bind(StrategyConfigurationDao.class).to(MongoDbStrategyConfigurationDao.class);
    bind(AlertConfigurationDao.class).to(MongoDbAlertConfigurationDao.class);

    bind(AlertService.class).to(AlertServiceImpl.class);
    bind(StrategyService.class).to(StrategyServiceImpl.class);
    bind(EventNotificationService.class).to(EventNotificationServiceImpl.class);
  }

  private static IgIndexConnection getIgIndexConnectionInstance() throws Exception {
    if (null == igIndexServiceConnection) {
      Properties properties = PropertiesUtil.loadPropertiesFromFile(BROKER_PROPERTIES_FILE);
      String apiKey = properties.getProperty("com.onplan.adapter.igindex.apiKey");
      String username = properties.getProperty("com.onplan.adapter.igindex.username");
      String password = properties.getProperty("com.onplan.adapter.igindex.password");
      String serverUrl = properties.getProperty("com.onplan.adapter.igindex.severUrl");
      return new IgIndexConnection(apiKey, username, password, serverUrl);
    }
    return igIndexServiceConnection;
  }

  @Provides
  @Singleton
  private ServiceConnection provideServiceConnection() throws Exception {
    return getIgIndexConnectionInstance();
  }

  @Provides
  @Singleton
  private PriceService providePriceService() throws Exception {
    return new IgIndexPriceService(getIgIndexConnectionInstance());
  }

  @Provides
  @Singleton
  private HistoricalPriceService provideHistoricalPriceService() throws Exception {
    return new IgIndexHistoricalPriceService(getIgIndexConnectionInstance());
  }

  @Provides
  @Singleton
  private InstrumentService provideInstrumentService() throws Exception {
    return new IgIndexInstrumentService(getIgIndexConnectionInstance());
  }

  private void loadPropertyFiles() throws Exception {
    for (String propertyFile : PROPERTIES_FILES) {
      Names.bindProperties(binder(), loadPropertiesFromFile(propertyFile));
    }
  }
}
