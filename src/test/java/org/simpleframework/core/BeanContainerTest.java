package org.simpleframework.core;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeanContainerTest {

    private static BeanContainer beanContainer;

    /**
     * 会执行一次初始化
     */
    @BeforeAll
    static void init() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * 对容器的加载
     */
    @Test
    public void loadBeansTest(){
        Assertions.assertEquals(false,beanContainer.isLoaded());
        beanContainer.loadBeans("com.imooc");
        Assertions.assertEquals(7,beanContainer.size());
        Assertions.assertEquals(true,beanContainer.isLoaded());
    }

}
