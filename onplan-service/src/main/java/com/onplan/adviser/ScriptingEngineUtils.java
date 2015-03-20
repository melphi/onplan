package com.onplan.adviser;

import org.apache.log4j.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ScriptingEngineUtils {
  private static final Logger LOGGER = Logger.getLogger(ScriptingEngineUtils.class);

  public static final String BINDING_NAME_CONTEXT = "ctx";
  public static final String FUNCTION_INIT = "init";
  public static final String FUNCTION_APPLY = "apply";

  private static final String JAVASCRIPT_ENGINE_NAME = "nashorn";

  private static ScriptEngineManager scriptEngineManager;

  public static ScriptEngine createJavaScriptEngine() {
    if (null == scriptEngineManager) {
      LOGGER.info(String.format("Loading [%s] script engine.", JAVASCRIPT_ENGINE_NAME));
      scriptEngineManager = new ScriptEngineManager();
    }
    return checkNotNull(
        scriptEngineManager.getEngineByName(JAVASCRIPT_ENGINE_NAME),
        String.format("Scripting engine [%s] not found.", JAVASCRIPT_ENGINE_NAME));
  }
}
