package org.pattern.singleton;

/**
 * 饿汉式
 */
public class StarvingSingleon {
    // static确保唯一性
    // private防止外界通过s.starvingSingleton()生成实例
    // final一旦初始化就不能改变
    private static final StarvingSingleon starvingSingleton = new StarvingSingleon();
    private StarvingSingleon(){}
    public static StarvingSingleon getInstance(){
        return starvingSingleton;
    }

    public static void main(String[] args) {
        System.out.println(StarvingSingleon.getInstance());
        System.out.println(StarvingSingleon.getInstance());
    }
}
