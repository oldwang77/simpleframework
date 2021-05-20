package org.pattern.singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class SingletonDemo {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = StarvingSingleon.class;
        Constructor constructor = clazz.getDeclaredConstructor();
        // 由于构造函数是私有的
        constructor.setAccessible(true);
        // starving并不安全
        // org.pattern.singleton.StarvingSingleon@43a25848
        // org.pattern.singleton.StarvingSingleon@3ac3fd8b
        System.out.println(constructor.newInstance());
        System.out.println(StarvingSingleon.getInstance());


        // 我们测试一下使用了枚举之后，反射是否可以解决问题
        Class clazz2 = EnumStarvingSingleton.class;
        Constructor constructor2 = clazz2.getDeclaredConstructor();
        constructor2.setAccessible(true);
        EnumStarvingSingleton enumStarvingSingleton = (EnumStarvingSingleton) constructor2.newInstance();
        // org.pattern.singleton.EnumStarvingSingleton@3b6eb2ec
        // org.pattern.singleton.EnumStarvingSingleton@3b6eb2ec
        System.out.println(EnumStarvingSingleton.getInstance());
        System.out.println(enumStarvingSingleton.getInstance());
    }
}
