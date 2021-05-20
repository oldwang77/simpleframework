package org.simpleframework.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)    //构造器私有化
public class BeanContainer {

    /**
     * 并发性能较好
     * 存放所有被配置标记的目标对象的Map
     */
    private final Map<Class<?>,Object> beanMap = new ConcurrentHashMap();
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

}
