package org.pattern.singleton;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

// 通过看反射的源码，可以看出反射在通过newInstance创建对象时，会检查该类是否ENUM修饰，如果是则抛出异常，反射失败。
// 枚举类型不能通过反射创建newInstance()创建对象

// 枚举类实现单例模式是 effective java 作者极力推荐的单例实现模式，因为枚举类型是线程安全的，并且只会装载一次，
// 设计者充分的利用了枚举的这个特性来实现单例模式，枚举的写法非常简单，
// 而且枚举类型是所用单例实现中唯一一种不会被破坏的单例实现模式。

public class EnumStarvingSingleton {
    private EnumStarvingSingleton(){}

    public static EnumStarvingSingleton getInstance(){
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder{
        // 我的理解是枚举类的一个实例
        HOLDER;
        private EnumStarvingSingleton instance;

        //枚举类也可以有构造器，构造器默认都是private修饰，而且只能是private。因为枚举类的实例不能让外界来创建！
        private ContainerHolder(){
            instance = new EnumStarvingSingleton();
        }
    }

    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class clazz = ContainerHolder.class;
        // 报错，无法通过构造函数构造
        /**
         * 枚举的构造函数
         *     protected Enum(String name, int ordinal) {
         *         this.name = name;
         *         this.ordinal = ordinal;
         *     }
         */
        Constructor constructor = clazz.getDeclaredConstructor(String.class,int.class);
        constructor.setAccessible(true);

        System.out.println(EnumStarvingSingleton.getInstance());
        System.out.println(constructor.newInstance());
    }
}
