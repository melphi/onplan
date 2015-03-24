package com.onplan.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/virtualmachine/*")
public class VirtualMachineController {
  @RequestMapping(value = "show", method = RequestMethod.GET)
  public String list() {
    return "virtualmachine/show";
  }
}
