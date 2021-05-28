package org.simpleframework.aop.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)       // 作用在类上
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    // 1.0版本
    // 指明Aspect的作用目标
    // 属性值定义成value
    // 类型就是注解类型的
    // 表明当前被Aspect标注的横切逻辑，会被织入属性值被注解标签标记的类里
    // 比如说标记为@Aspect(value = Controller.class)，表示被织入@controller标签的类里
    // Class<? extends Annotation> value();


    // 2.0版本
    String pointcut();
}
