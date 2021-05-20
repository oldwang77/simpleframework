package org.simpleframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Component;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Repository;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)    //构造器私有化
public class BeanContainer {

    /**
     * 容器是否已经被加载过
     */
    private boolean loaded = false;

    /**
     * 是否已经加载过bean
     * @return
     */
    boolean isLoaded(){
        return loaded;
    }

    /**
     * Bean实例数量
     *
     * @return 数量
     */
    public int size() {
        return beanMap.size();
    }

    /**
     * 并发性能较好
     * 存放所有被配置标记的目标对象的Map
     */
    private final Map<Class<?>,Object> beanMap = new ConcurrentHashMap();

    /**
     * 加载bean的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION =
            Arrays.asList(Component.class, Controller.class, Service.class, Repository.class);

    /**
     * 获取bean实例容器
     * 单例+使用了枚举方式防止反射的入侵
     * @return
     */
    public static BeanContainer getInstance(){
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder{
        HOLDER;
        private BeanContainer instance;

        private ContainerHolder(){
            instance = new BeanContainer();
        }
    }

    /**
     * 扫描加载所有的bean
     * 为了防止两个线程都加载beans，我们加上Synchronized
     * @param packageName
     */
    public synchronized void loadBeans(String packageName){
        // 判断beans是否已经被加载过
        if(isLoaded()){
            log.warn("BeanContainer has been loaded");
            return ;
        }
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        if(classSet==null || classSet.isEmpty()){
            log.warn("extract nothing from packageName"+packageName);
            return ;
        }

        for(Class<?> clazz:classSet){
            for(Class<? extends Annotation> annotation:BEAN_ANNOTATION){
                // 如果上面这个类被标记了定义的注解
                if(clazz.isAnnotationPresent(annotation)){
                    // 将类的目标本身作为键，目标类的实例作为值，放入到beanMap中
                    beanMap.put(clazz,ClassUtil.newInstance(clazz,true));
                }
            }
        }
        loaded = true;
    }

}
