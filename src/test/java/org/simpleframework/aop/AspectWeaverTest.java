package org.simpleframework.aop;

import com.imooc.controller.superadmin.HeadLineOperationController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.DependencyInjector;

public class AspectWeaverTest {

    @DisplayName("织入通用逻辑测试")
    @Test
    public void doAopTest(){
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.imooc");

        // Aop的织入操作是先于依赖注入的
        new AspectWeaver().doAop();
        new DependencyInjector().doIoc();

        HeadLineOperationController headLineOperationController = (HeadLineOperationController)
                beanContainer.getBean(HeadLineOperationController.class);

        headLineOperationController.removeHeadLine(null,null);
    }
}
