package org.pattern.singleton;

public class LazyDoubleCheckSingleton {
    // 为什么要加上volatile关键字
    // lazyDoubleCheckSingleton = new LazyDoubleCheckSingleton();这个步骤实际上是分成三个步骤的
    // 但是执行顺序并不是固定的，可能线程1执行了步骤1和3，还没有执行步骤2，即instance还没有被初始化
    // 虽然instance没有初始化，但是已经分配了内存空间
    // 如果此时其他线程2进入第一个if判断，由于分配了空间,不为null，就直接返回结果了
    // volatile修饰的关键字可以禁止指令重排！
    private volatile static LazyDoubleCheckSingleton lazyDoubleCheckSingleton;

    private LazyDoubleCheckSingleton(){};

    private static LazyDoubleCheckSingleton getInstance(){
        // 第一次检测
        if(lazyDoubleCheckSingleton==null){
            // 同步
            synchronized (LazyDoubleCheckSingleton.class){
                if(lazyDoubleCheckSingleton==null){
                    // 这个步骤是分了三个步骤完成的
                    // memory = allocate()  1分配空间
                    // instance(memory)     2初始化对象
                    // instance = memory()  3设置instance指向分配的内存地址，此时instance！=null
                    lazyDoubleCheckSingleton = new LazyDoubleCheckSingleton();
                }
            }
        }
        return lazyDoubleCheckSingleton;
    }

    public static void main(String[] args) {
        System.out.println(LazyDoubleCheckSingleton.getInstance());
        System.out.println(LazyDoubleCheckSingleton.getInstance());
    }
}
