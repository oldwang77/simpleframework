package org.simpleframework.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class ClassUtilTest {

    @DisplayName("提取目标类的方法：extractPackageClass")
    @Test
    public void extractPackageClass(){
        Set<Class<?>> classes = ClassUtil.extractPackageClass("com.imooc.entity");
        System.out.println(classes);
        Assertions.assertEquals(4,classes.size());
    }
}
