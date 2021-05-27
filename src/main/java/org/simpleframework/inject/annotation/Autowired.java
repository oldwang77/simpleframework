package org.simpleframework.inject.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)          //  只让autowired作用在成员变量上
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    String value() default "";
}
