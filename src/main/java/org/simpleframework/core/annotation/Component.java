package org.simpleframework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)               // 表明我们注入的类型是在类上使用的
@Retention(RetentionPolicy.RUNTIME)     // RUNTIME,需要在运行的时候获取
public @interface Component {
}
