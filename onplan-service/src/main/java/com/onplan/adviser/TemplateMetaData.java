package com.onplan.adviser;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface TemplateMetaData {
  String displayName();
  String[] availableParameters() default {};
}
