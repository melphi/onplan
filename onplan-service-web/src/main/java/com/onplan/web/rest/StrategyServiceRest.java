package com.onplan.web.rest;

import com.onplan.adviser.StrategyInfo;
import com.onplan.adviser.TemplateInfo;
import com.onplan.domain.configuration.StrategyConfiguration;
import com.onplan.service.StrategyServiceRemote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.onplan.util.MorePreconditions.checkNotNullOrEmpty;

@RestController
@RequestMapping(value = "/rest/strategies", produces = "application/json")
public class StrategyServiceRest implements StrategyServiceRemote {
  @Autowired
  private StrategyServiceRemote strategyService;

  @Override
  @RequestMapping(value = "/{strategyId}", method = RequestMethod.DELETE)
  public boolean removeStrategy(@PathVariable("strategyId") String strategyId) throws Exception {
    checkNotNullOrEmpty(strategyId);
    return strategyService.removeStrategy(strategyId);
  }

  @Override
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public String addStrategy(@RequestBody StrategyConfiguration strategyConfiguration)
      throws Exception {
    checkNotNull(strategyConfiguration);
    return strategyService.addStrategy(strategyConfiguration);
  }

  @Override
  @RequestMapping(value = "/loadsamplestrategies", method = RequestMethod.GET)
  public long loadSampleStrategies() throws Exception {
    return strategyService.loadSampleStrategies();
  }

  @Override
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public List<StrategyInfo> getStrategiesInfo() {
    return strategyService.getStrategiesInfo();
  }

  @Override
  @RequestMapping(value = "/strategytemplateid", method = RequestMethod.GET)
  public List<String> getStrategyTemplatesIds() {
    return strategyService.getStrategyTemplatesIds();
  }

  // TODO(robertom): Refactor template ids with a proper alphanumeric id.
  // The expression "templateId:.+" is used to avoid the string truncation after the last dot.
  @Override
  @RequestMapping(value = "/strategytemplate/{templateId:.+}", method = RequestMethod.GET)
  public TemplateInfo getStrategyTemplateInfo(@PathVariable("templateId") String templateId) {
    checkNotNullOrEmpty(templateId);
    TemplateInfo templateInfo = strategyService.getStrategyTemplateInfo(templateId);
    return templateInfo;
  }
}
