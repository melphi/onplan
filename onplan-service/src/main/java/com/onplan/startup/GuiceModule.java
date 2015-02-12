package com.onplan.startup;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
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
import org.apache.log4j.Logger;

import java.util.Collection;
import java.util.Properties;

import static com.onplan.util.PropertiesUtil.loadAllPropertiesFromClassPath;

public class GuiceModule extends AbstractModule {
  private static final Logger LOGGER = Logger.getLogger(GuiceModule.class);

  @Override
  protected void configure() {
    try {
      loadPropertyFiles();
    } catch (Exception e) {
      LOGGER.error("Error while loading all the properties file from the class path.", e);
      throw new IllegalArgumentException(e);
    }

    bind(StrategyConfigurationDao.class).to(MongoDbStrategyConfigurationDao.class);
    bind(AlertConfigurationDao.class).to(MongoDbAlertConfigurationDao.class);

    bind(AlertService.class).to(AlertServiceImpl.class);
    bind(StrategyService.class).to(StrategyServiceImpl.class);
    bind(EventNotificationService.class).to(EventNotificationServiceImpl.class);
  }

  private void loadPropertyFiles() throws Exception {
    Collection<Properties> allProperties = loadAllPropertiesFromClassPath();
    LOGGER.info(String.format("Loading [%d] properties files.", allProperties.size()));
    for (Properties properties : allProperties) {
      Names.bindProperties(binder(), properties);
    }
  }
}
