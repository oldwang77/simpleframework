package org.simpleframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Component;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.core.annotation.Repository;
import org.simpleframework.core.annotation.Service;
import org.simpleframework.util.ClassUtil;
import org.simpleframework.util.ValidationUtil;

import java.lang.annotation.Annotation;
import java.util.*;
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
     *
     * @return
     */
    boolean isLoaded() {
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
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap();

    /**
     * 加载bean的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION =
            Arrays.asList(Component.class, Controller.class, Service.class, Repository.class);

    /**
     * 获取bean实例容器
     * 单例+使用了枚举方式防止反射的入侵
     *
     * @return
     */
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;

        private ContainerHolder() {
            instance = new BeanContainer();
        }
    }

    /**
     * 扫描加载所有的bean
     * 为了防止两个线程都加载beans，我们加上Synchronized
     *
     * @param packageName
     */
    public synchronized void loadBeans(String packageName) {
        // 判断beans是否已经被加载过
        if (isLoaded()) {
            log.warn("BeanContainer has been loaded");
            return;
        }
        Set<Class<?>> classSet = ClassUtil.extractPackageClass(packageName);
        if (classSet == null || classSet.isEmpty()) {
            log.warn("extract nothing from packageName" + packageName);
            return;
        }

        for (Class<?> clazz : classSet) {
            for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                // 如果上面这个类被标记了定义的注解
                if (clazz.isAnnotationPresent(annotation)) {
                    // 将类的目标本身作为键，目标类的实例作为值，放入到beanMap中
                    beanMap.put(clazz, ClassUtil.newInstance(clazz, true));
                }
            }
        }
        loaded = true;
    }

    /**
     * 添加一个class对象和它的bean实例
     *
     * @param clazz
     * @param bean
     * @return
     */
    public Object addBean(Class<?> clazz, Object bean) {
        return beanMap.put(clazz, bean);
    }

    /**
     * 移除一个IOC容器管理的对象
     *
     * @param clazz
     * @param bean
     * @return
     */
    public Object removeBean(Class<?> clazz, Object bean) {
        return beanMap.remove(clazz);
    }

    /**
     * 获取class对象的bean实例
     *
     * @param clazz
     * @return
     */
    public Object getBean(Class<?> clazz) {
        return beanMap.get(clazz);
    }

    /**
     * 获取容器内所有Class对象的集合
     *
     * @return
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * 获取所有beans的集合
     *
     * @return
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 根据注解筛选出Bean的class集合
     *
     * @param annotation
     * @return
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        // 1 获取beanMap内所有的class对象
        Set<Class<?>> keySet = getClasses();
        if (ValidationUtil.isEmpty(keySet)) {
            log.warn("nothing in beanMap");
            return null;
        }
        // 2 通过注解筛选被标记的class对象
        Set<Class<?>> classSet = new HashSet<>();
        for (Class clazz : keySet) {
            // 类是否包含相关的标记
            if (clazz.isAnnotationPresent(annotation)) {
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }

    /**
     * 通过接口或者父类获取实现类或者子类的class集合，不包括本身
     *
     * @param interfaceOrClass
     * @return
     */
    public Set<Class<?>> getClassBySuper(Class<?> interfaceOrClass) {
        // 1 获取beanMap的所有class对象
        Set<Class<?>> keySet = getClasses();
        if (ValidationUtil.isEmpty(keySet)) {
            log.warn("nothing in beanMap");
            return null;
        }
        // 判断传入的类或者接口是否是类的子类，如果是，就将它添加到classSet中
        Set<Class<?>> classSet = new HashSet<>();
        for (Class clazz : keySet) {
            // isAssignableFrom是否是父类
            if (interfaceOrClass.isAssignableFrom(clazz) && !clazz.equals(interfaceOrClass)) {
                classSet.add(clazz);
            }
        }
        return classSet.size() > 0 ? classSet : null;
    }

}
