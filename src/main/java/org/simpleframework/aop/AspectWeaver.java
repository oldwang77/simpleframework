package org.simpleframework.aop;

import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.AspectInfo;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// AOP2.0版本
public class AspectWeaver {
    private BeanContainer beanContainer;

    public AspectWeaver() {
        this.beanContainer = BeanContainer.getInstance();
    }

    public void doAop() {
        // 1 获取所有的切面类
        Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
        if (ValidationUtil.isEmpty(aspectSet)) {
            return;
        }

        // 2 拼装AspectInfoList
        List<AspectInfo> aspectInfoList =  packageAspectInfoList(aspectSet);

        // 3 遍历容器内部的类
        Set<Class<?>> classeSet = beanContainer.getClasses();

        for(Class<?> targetClass:classeSet){
            // 排除Aspect类自身
            if(targetClass.isAnnotationPresent(Aspect.class)){
                continue;
            }

            // 4 初步筛选出符合的Aspect
            List<AspectInfo> roughMatchedAspectList =
                    collectRoughMatchedAspectListForSpecificClass(aspectInfoList,targetClass);

            // 5 尝试进行Aspect织入
            wrapIfNecessary(roughMatchedAspectList,targetClass);
        }
    }

    /**
     * 尝试进行Aspect织入
     * @param roughMatchedAspectList
     * @param targetClass
     */
    private void wrapIfNecessary(List<AspectInfo> roughMatchedAspectList, Class<?> targetClass) {
        if(ValidationUtil.isEmpty(roughMatchedAspectList)) return ;
        // 创建动态代理对象
        AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, roughMatchedAspectList);
        Object proxyBean = ProxyCreator.createProxy(targetClass, aspectListExecutor);
        beanContainer.addBean(targetClass,proxyBean);
    }

    /**
     * 初步筛选出符合的Aspect
     * @param aspectInfoList
     * @param targetClass
     * @return
     */
    private List<AspectInfo> collectRoughMatchedAspectListForSpecificClass(List<AspectInfo> aspectInfoList, Class<?> targetClass) {
        List<AspectInfo> roughMatchedAspectList = new ArrayList<>();
        for(AspectInfo aspectInfo:aspectInfoList){
            // 粗筛
            if(aspectInfo.getPointCutLocator().roughMatch(targetClass)){
                roughMatchedAspectList.add(aspectInfo);
            }
        }
        return roughMatchedAspectList;
    }

    /**
     * 拼装AspectInfoList
     * @param aspectSet
     * @return
     */
    private List<AspectInfo> packageAspectInfoList(Set<Class<?>> aspectSet) {
        List<AspectInfo> aspectInfoList = new ArrayList<>();
        for(Class<?> aspectClass:aspectSet){
            if(verifyAspect(aspectClass)){
                 Order orderTag = aspectClass.getAnnotation(Order.class);
                 Aspect aspectTag = aspectClass.getAnnotation(Aspect.class);
                 // 所有的切面类都必须实现一个接口
                 DefaultAspect defaultAspect = (DefaultAspect) beanContainer.getBean(aspectClass);
                 // 初始化表达式定位器
                PointCutLocator pointCutLocator = new PointCutLocator(aspectTag.pointcut());
                AspectInfo aspectInfo = new AspectInfo(orderTag.value(), defaultAspect, pointCutLocator);
                aspectInfoList.add(aspectInfo);
            }else{
                throw new RuntimeException("Exception!@Aspect或者@Order标签没有使用 或者" +
                        "没有继承自DefaultAspect.class");
            }
        }
        return aspectInfoList;
    }

    //框架中一定要遵守给Aspect类添加@Aspect和@Order标签的规范，同时，必须继承自DefaultAspect.class
    //此外，@Aspect的属性值不能是它本身
    private boolean verifyAspect(Class<?> aspectClass) {
        return aspectClass.isAnnotationPresent(Aspect.class) &&
                aspectClass.isAnnotationPresent(Order.class) &&
                DefaultAspect.class.isAssignableFrom(aspectClass);
    }

}
