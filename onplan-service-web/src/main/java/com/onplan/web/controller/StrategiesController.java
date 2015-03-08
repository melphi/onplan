package com.onplan.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/strategies")
public class StrategiesController {
  @RequestMapping(value = "/list", method = RequestMethod.GET)
  public String list() {
    return "strategies/list";
  }
}
