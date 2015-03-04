package com.onplan.startup;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.onplan.adapter.HistoricalPriceService;
import com.onplan.adapter.InstrumentService;
import com.onplan.adapter.PriceService;
import com.onplan.adapter.ServiceConnection;
import com.onplan.adapter.igindex.IgIndexConnection;
import com.onplan.adapter.igindex.IgIndexHistoricalPriceService;
import com.onplan.adapter.igindex.IgIndexInstrumentService;
import com.onplan.adapter.igindex.IgIndexPriceService;
import com.onplan.util.PropertiesUtils;

import javax.inject.Singleton;
import java.util.Properties;

/**
 * Guice module specific for IgIndex services.
 */
// TODO(robertom): Use configuration files for setting up the broker instead of hard coded classes.
public class IgIndexGuiceModule extends AbstractModule {
  private static final String BROKER_PROPERTIES_FILE = "broker.properties";

  private static IgIndexConnection igIndexServiceConnection;

  @Override
  protected void configure() {
    // Intentionally empty.
  }

  private static IgIndexConnection getIgIndexConnectionInstance() throws Exception {
    if (null == igIndexServiceConnection) {
      Properties properties = PropertiesUtils.loadPropertiesFromFile(BROKER_PROPERTIES_FILE);
      String apiKey = properties.getProperty("com.onplan.adapter.igindex.apiKey");
      String username = properties.getProperty("com.onplan.adapter.igindex.username");
      String password = properties.getProperty("com.onplan.adapter.igindex.password");
      String serverUrl = properties.getProperty("com.onplan.adapter.igindex.severUrl");
      igIndexServiceConnection = new IgIndexConnection(apiKey, username, password, serverUrl);
    }
    return igIndexServiceConnection;
  }

  @Provides
  private static ServiceConnection provideServiceConnection() throws Exception {
    return getIgIndexConnectionInstance();
  }

  @Singleton
  @Provides
  private PriceService providePriceService() throws Exception {
    return new IgIndexPriceService(getIgIndexConnectionInstance());
  }

  @Singleton
  @Provides
  private HistoricalPriceService provideHistoricalPriceService() throws Exception {
    return new IgIndexHistoricalPriceService(getIgIndexConnectionInstance());
  }

  @Singleton
  @Provides
  private InstrumentService provideInstrumentService() throws Exception {
    return new IgIndexInstrumentService(getIgIndexConnectionInstance());
  }
}
