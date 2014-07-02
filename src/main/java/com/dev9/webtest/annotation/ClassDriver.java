package com.dev9.webtest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ClassDriver {

    /**
     * Whether the WebDriver will be initialized at runtime
     */
    public boolean enabled() default true;
}
