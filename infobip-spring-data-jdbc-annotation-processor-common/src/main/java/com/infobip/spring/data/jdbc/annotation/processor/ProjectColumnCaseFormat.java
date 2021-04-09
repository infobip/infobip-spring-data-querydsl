package com.infobip.spring.data.jdbc.annotation.processor;

import com.google.common.base.CaseFormat;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
@Documented
public @interface ProjectColumnCaseFormat {

    CaseFormat value();
}
