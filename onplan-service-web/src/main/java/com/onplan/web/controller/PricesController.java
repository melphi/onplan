package com.onplan.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/prices/*")
public class PricesController {
  @RequestMapping(value = "list", method = RequestMethod.GET)
  public String list() {
    return "prices/list";
  }
}
