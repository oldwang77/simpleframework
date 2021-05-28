// package org.simpleframework.aop;
//
// import org.simpleframework.aop.annotation.Aspect;
// import org.simpleframework.aop.annotation.Order;
// import org.simpleframework.aop.aspect.AspectInfo;
// import org.simpleframework.aop.aspect.DefaultAspect;
// import org.simpleframework.core.BeanContainer;
// import org.simpleframework.util.ValidationUtil;
//
// import java.lang.annotation.Annotation;
// import java.util.*;
//
// public class AspectWeaver_Version1 {
//     private BeanContainer beanContainer;
//
//     public AspectWeaver_Version1() {
//         this.beanContainer = beanContainer.getInstance();
//     }
//
//     public void doAop() {
//         // 1 获取所有的切面类
//         // 切面类需要添加入bean容器
//         Set<Class<?>> aspectSet = beanContainer.getClassesByAnnotation(Aspect.class);
//
//         // 2 将所有切面类按照不同的织入目标进行切分
//         // 被@Aspect标记的，但是它们的属性值可能是不同的
//         Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap = new HashMap<>();
//
//         if (ValidationUtil.isEmpty(aspectSet)) return;
//
//         // aspectClass: com.imooc.aspect.ServiceTimeCalculatorAspect
//         // 主要目的就是生成aspectInfoList （orderTag,aspect切面的类）
//         for (Class<?> aspectClass : aspectSet) {
//             if (verifyAspect(aspectClass)) {
//                 categorizeAspect(categorizedMap, aspectClass);
//             } else {
//                 throw new RuntimeException("Exception!@Aspect或者@Order标签没有使用 或者" +
//                         "没有继承自DefaultAspect.class");
//             }
//         }
//
//         // 3 按照不同的织入目标分别去按序织入Aspect的逻辑
//         if (!ValidationUtil.isEmpty(categorizedMap)) {
//             for (Class<? extends Annotation> category : categorizedMap.keySet()) {
//                 weaveByCategory(category, categorizedMap.get(category));
//             }
//         }
//     }
//
//
//
//
//
//      // * public class AspectInfo {
//      // *     private int orderIndex;
//      // *     private DefaultAspect aspectObject;
//      // * }
//
//
//     // 按照不同的织入目标分别去按序织入Aspect的逻辑
//     private void weaveByCategory(Class<? extends Annotation> category, List<AspectInfo> aspectInfoList) {
//         // 1 获取被代理类的集合
//         Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(category);
//         if (ValidationUtil.isEmpty(classSet)) return;
//
//         // 2 遍历被代理的类，分别为每个被代理的类生成动态代理实例
//         for (Class<?> targetClass : classSet) {
//             // aspectListExecutor：目的是在创建代理对象的时候去执行intercept逻辑，也就是执行切面类的方法
//             AspectListExecutor aspectListExecutor = new AspectListExecutor(targetClass, aspectInfoList);
//
//             // createProxy => return Enhancer.create(targetClass,methodInterceptor);
//             Object proxyBean = ProxyCreator.createProxy(targetClass, aspectListExecutor);
//
//             // 3 将动态代理对象实例添加到容器中，取代未被代理前的类实例
//             beanContainer.addBean(targetClass, proxyBean);
//         }
//     }
//
//     // 将所有切面类按照不同的织入目标进行切分，目的就是为了生成aspectInfoList
//     // map < 标注@Aspect的切面类的value(value的形式是注解)， 切面类信息AspectInfo(切入order,切面类aspect) > categorizedMap
//     // @Aspect(value= Controller.class)，表示针对所有以@Controller注解的类进行织入，织入的是列表
//     private void categorizeAspect(Map<Class<? extends Annotation>, List<AspectInfo>> categorizedMap, Class<?> aspectClass) {
//         // getAnnotation返回该元素的指定类型的注释，如果是这样的注释，否则返回null。
//         Order orderTag = aspectClass.getAnnotation(Order.class); //orderTag: @org.simpleframework.aop.annotation.Order(value=0)
//         Aspect aspectTag = aspectClass.getAnnotation(Aspect.class); //aspectTag: @org.simpleframework.aop.annotation.Aspect(value=org.simpleframework.core.annotation.Service.class)
//
//         // class都必须继承自DefaultAspect
//         DefaultAspect aspect = (DefaultAspect) beanContainer.getBean(aspectClass); // ServiceTimeCalculatorAspect
//         AspectInfo aspectInfo = new AspectInfo(orderTag.value(), aspect);
//
//         if (!categorizedMap.containsKey(aspectTag.value())) {
//             // 如果织入的jointpoint第一次出现，那么以该jointpoint为key，以新创建的List<Aspect>为value
//             List<AspectInfo> aspectInfoList = new ArrayList<>();
//             aspectInfoList.add(aspectInfo);
//             categorizedMap.put(aspectTag.value(), aspectInfoList);
//         } else {
//             // 如果织入的Aspect不是第一次出现，则往jointpoint对应的value里添加新的Aspect逻辑
//             List<AspectInfo> aspectInfoList = categorizedMap.get(aspectTag.value());
//             aspectInfoList.add(aspectInfo);
//         }
//     }
//
//
//
//
//     // 框架中一定要遵守@Aspect和@Order标签的规范，同时必须继承自DefaultAspect.class
//     // 此外，@Aspect的属性值不能是它本身，因为你不能针对切面执行切面，会陷入死循环中
//     private boolean verifyAspect(Class<?> aspectClass) {
//         // 这个类必须有@Aspect和@Order标签
//         // 必须继承自DefaultAspect.class
//         return aspectClass.isAnnotationPresent(Aspect.class) &&
//                 aspectClass.isAnnotationPresent(Order.class) &&
//                 DefaultAspect.class.isAssignableFrom(aspectClass) &&
//                 aspectClass.getAnnotation(Aspect.class).value() != Aspect.class;
//     }
//
//
//
//
// }
