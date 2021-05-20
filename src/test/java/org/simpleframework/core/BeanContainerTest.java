package org.simpleframework.core;

import com.imooc.controller.frontend.MainPageController;
import com.imooc.service.solo.HeadLineService;
import com.imooc.service.solo.impl.HeadLineServiceImpl;
import org.junit.jupiter.api.*;
import org.simpleframework.core.annotation.Controller;

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
    @DisplayName("加载目标类及其实例到BeanContainer：loadBeansTest")
    @Test
    @Order(1)
    public void loadBeansTest(){
        Assertions.assertEquals(false,beanContainer.isLoaded());
        beanContainer.loadBeans("com.imooc");
        Assertions.assertEquals(7,beanContainer.size());
        Assertions.assertEquals(true,beanContainer.isLoaded());
    }

    @DisplayName("根绝类型获取实例:getBean")
    @Test
    @Order(2)
    public void getBeanTest(){
        MainPageController controller = (MainPageController)beanContainer.getBean(MainPageController.class);
        Assertions.assertEquals(true,controller instanceof MainPageController);
    }

    @DisplayName("根据注解获取对应的实例：getClassesByAnnotationTest")
    @Test
    @Order(3)
    public void getClassesByAnnotationTest(){
        Assertions.assertEquals(true,beanContainer.isLoaded());
        Assertions.assertEquals(3,beanContainer.getClassesByAnnotation(Controller.class).size());
    }

    @DisplayName("根据接口获取实现类：getClassesBySuperTest")
    @Test
    @Order(4)
    public void getClassesBySuperTest(){
        Assertions.assertEquals(true,beanContainer.isLoaded());
        Assertions.assertEquals(true,beanContainer.getClassBySuper(HeadLineService.class).contains(HeadLineServiceImpl.class));
    }

}
