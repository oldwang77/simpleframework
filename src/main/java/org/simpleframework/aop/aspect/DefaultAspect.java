package org.simpleframework.aop.aspect;

import java.lang.reflect.Method;

/**
 * 定义钩子方法，类似模版方法中的钩子方法，交给用户决定实现方式，即使不实现，也不影响程序的继续运行
 */
public abstract class DefaultAspect {

    /**
     * 事前拦截
     * @param targetClass 被代理的目标类
     * @param method    被代理的目标方法
     * @param args  被代理的目标方法对应的参数列表
     * @throws Throwable
     */
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable{

    }

    /**
     * 事后拦截
     * @param targetClass
     * @param method
     * @param args
     * @param returnValue
     * @return
     * @throws Throwable
     */
    public Object afterReturning(Class<?> targetClass,Method method,Object[] args,Object returnValue) throws Throwable{
        return returnValue;
    }

    /**
     *
     * @param targetClass
     * @param method
     * @param args
     * @param e
     * @throws Throwable
     */
    public void afterThrowing(Class<?> targetClass,Method method,Object[] args,Throwable e) throws Throwable{

    }
}
