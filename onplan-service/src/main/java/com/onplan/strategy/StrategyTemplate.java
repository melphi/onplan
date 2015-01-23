package com.onplan.strategy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface StrategyTemplate {
  String name();
  String[] availableParameters() default {};
}
