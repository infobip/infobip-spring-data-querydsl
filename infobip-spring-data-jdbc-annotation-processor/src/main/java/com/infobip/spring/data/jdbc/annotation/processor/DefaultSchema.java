package com.infobip.spring.data.jdbc.annotation.processor;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ ElementType.TYPE })
@Documented
public @interface DefaultSchema {

    String value();
}