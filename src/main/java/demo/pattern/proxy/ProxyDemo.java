package demo.pattern.proxy;

import demo.pattern.proxy.cglib.AlipayMethodInterceptor;
import demo.pattern.proxy.cglib.CglibUtil;
import demo.pattern.proxy.impl.*;
import demo.pattern.proxy.jdkproxy.AlipayInvocationHandler;
import demo.pattern.proxy.jdkproxy.JdkDynamicProxyUtil;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationHandler;

public class ProxyDemo {
    public static void main(String[] args) {
       // ToCPayment toCProxy = new AlipayToC(new ToCPaymentImpl());
       // toCProxy.pay();
       // ToBPayment toBProxy = new AlipayToB(new ToBPaymentImpl());
       // toBProxy.pay();

//        ToCPayment toCPayment = new ToCPaymentImpl();
//        InvocationHandler handler = new AlipayInvocationHandler(toCPayment);
//        ToCPayment toCProxy = JdkDynamicProxyUtil.newProxyInstance(toCPayment,handler);  // 去执行handler的invocationHandler的Invoke方法
//        toCProxy.pay();
//        ToBPayment toBPayment = new ToBPaymentImpl();
//        InvocationHandler handlerToB = new AlipayInvocationHandler(toBPayment);
//        ToBPayment toBProxy = JdkDynamicProxyUtil.newProxyInstance(toBPayment, handlerToB);
//        toBProxy.pay();

        CommonPayment commonPayment = new CommonPayment();
//        AlipayInvocationHandler invocationHandler = new AlipayInvocationHandler(commonPayment);
//        CommonPayment commonPaymentProxy = JdkDynamicProxyUtil.newProxyInstance(commonPayment, invocationHandler);
        MethodInterceptor methodInterceptor = new AlipayMethodInterceptor();
        // 当调用createProxy=>Enchance.create()方法=>返回动态代理对象=>动态代理对象执行方法
        CommonPayment commonPaymentProxy = CglibUtil.createProxy(commonPayment, methodInterceptor);
        commonPaymentProxy.pay();

        // ToCPayment toCPayment = new ToCPaymentImpl();
        // ToCPayment toCProxy = CglibUtil.createProxy(toCPayment, methodInterceptor);
        // toCProxy.pay();
    }
}
