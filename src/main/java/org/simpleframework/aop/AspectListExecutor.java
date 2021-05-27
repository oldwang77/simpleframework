package org.simpleframework.aop;

import lombok.Getter;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.util.ValidationUtil;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AspectListExecutor implements MethodInterceptor {
    // 被代理的类
    private Class<?> targetClass;

    // AspectList 获取被@Aspect @Order 标注的DefaultAspect列表
    @Getter
    private List<AspectInfo> sortedAspectInfoList;

    public AspectListExecutor(Class<?> targetClass, List<AspectInfo> aspectInfoList) {
        this.targetClass = targetClass;
        this.sortedAspectInfoList = sortAspectInfoList(aspectInfoList);
    }

    /**
     * 按照order的值进行升序排序，确保order较小的aspect先织入
     *
     * @param aspectInfoList
     * @return
     */
    private List<AspectInfo> sortAspectInfoList(List<AspectInfo> aspectInfoList) {
        Collections.sort(aspectInfoList, new Comparator<AspectInfo>() {
            @Override
            public int compare(AspectInfo o1, AspectInfo o2) {
                return o1.getOrderIndex() - o2.getOrderIndex();
            }
        });
        return aspectInfoList;
    }

    // 通过intercept方法定义横切逻辑
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object returnValue = null;
        if (ValidationUtil.isEmpty(sortedAspectInfoList)) {
            return returnValue;
        }
        // 1 按照order顺序升序完成Aspect所有的before方法
        invokeBeforeAdvices(method, args);
        // 2 执行被代理类的方法
        try {
            returnValue = methodProxy.invokeSuper(proxy, args);
            // 3 如果被代理类方法正常返回，则按照order的顺序执行完所有Aspect的afterRuning方法
            invokeAfterReturningAdvices(method, args, returnValue);
        } catch (Exception e) {
            // 4 如果抛出异常，则按照order的顺序降序执行完所有的Aspect的afterThrowing方法
            invokeAfterThrowingAdvices(method, args, e);
        }
        return returnValue;
    }

    // 1 按照order顺序升序完成Aspect所有的before方法
    public void invokeBeforeAdvices(Method method, Object[] args) throws Throwable {
        for (AspectInfo aspectInfo : sortedAspectInfoList) {
            aspectInfo.getAspectObject().before(targetClass, method, args);
        }
    }

    // 3 如果被代理类方法正常返回，则按照order的顺序执行完所有Aspect的afterRuning方法
    public Object invokeAfterReturningAdvices(Method method, Object[] args, Object returnValue) throws Throwable {
        Object result = null;
        for (int i = sortedAspectInfoList.size() - 1; i >= 0; i--) {
            result = sortedAspectInfoList.get(i).getAspectObject().afterReturning(targetClass, method, args, returnValue);
        }
        return result;
    }

    // 4 如果抛出异常，则按照order的顺序降序执行完所有的Aspect的afterThrowing方法
    public void invokeAfterThrowingAdvices(Method method, Object[] args, Exception e) throws Throwable {
        for (int i = sortedAspectInfoList.size() - 1; i >= 0; i--) {
            sortedAspectInfoList.get(i).getAspectObject().afterThrowing(targetClass, method, args, e);
        }
    }
}
